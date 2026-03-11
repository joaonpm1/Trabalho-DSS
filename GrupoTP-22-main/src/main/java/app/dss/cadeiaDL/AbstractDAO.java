package app.dss.cadeiaDL;

import java.sql.*;
import java.util.*;

public abstract class AbstractDAO<K, V> implements Map<K, V> {

    protected Connection connection;
    protected String tableName;

    public AbstractDAO(String tableName) {
        this.tableName = tableName;
        try {
            this.connection = DriverManager.getConnection(
                DatabaseCredentials.getUrl(),
                DatabaseCredentials.getUsername(),
                DatabaseCredentials.getPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar à BD", e);
        }
    }

    protected abstract String getKeyColumnNames();

    protected abstract void setKeyParameters(PreparedStatement ps, int initialIndex, K key) throws SQLException;

    protected abstract K decodeKey(ResultSet rs) throws SQLException;

    protected abstract V decodeValue(ResultSet rs) throws SQLException;

    protected abstract String getInsertSql();

    protected abstract void setInsertParameters(PreparedStatement ps, V value) throws SQLException;

    protected abstract String getUpdateSql();

    protected abstract void setUpdateParameters(PreparedStatement ps, V value) throws SQLException;

    @Override
    public int size() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        String sql = "SELECT 1 FROM " + tableName + " WHERE " + getKeyColumnNames();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setKeyParameters(ps, 1, (K) key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public V get(Object key) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + getKeyColumnNames();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setKeyParameters(ps, 1, (K) key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return decodeValue(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        try {
            if (containsKey(key)) {
                try (PreparedStatement ps = connection.prepareStatement(getUpdateSql())) {
                    setUpdateParameters(ps, value);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = connection.prepareStatement(getInsertSql())) {
                    setInsertParameters(ps, value);
                    ps.executeUpdate();
                }
            }
            return value;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao guardar dados na tabela " + tableName, e);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public V remove(Object key) {
        V res = get(key);
        String sql = "DELETE FROM " + tableName + " WHERE " + getKeyColumnNames();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            setKeyParameters(ps, 1, (K) key);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    @Override
    public void clear() {
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("DELETE FROM " + tableName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tableName)) {
            while (rs.next()) {
                keys.add(decodeKey(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tableName)) {
            while (rs.next()) {
                values.add(decodeValue(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entries = new HashSet<>();
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM " + tableName)) {
            while (rs.next()) {
                K key = decodeKey(rs);
                V value = decodeValue(rs);
                entries.add(new AbstractMap.SimpleEntry<>(key, value));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entries;
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("containsValue não suportado por performance.");
    }
}