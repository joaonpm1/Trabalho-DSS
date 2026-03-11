package app.dss.cadeiaDL;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import app.dss.cadeiaLN.subsistemaPedidos.Order;
import app.dss.cadeiaLN.subsistemaPedidos.Task;

public class OrderDAO extends AbstractDAO<OrderDAO.OrderKey, Order> {

    public OrderDAO() {
        super("`order`");
    }


    public static class OrderKey {
        private String orderId;
        private String restId; 

        public OrderKey(String oID, String rID) {
            this.orderId = oID;
            this.restId = rID;
        }
        public String getOrderId() { return orderId; }
        public String getRestId() { return restId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OrderKey orderKey = (OrderKey) o;
            return Objects.equals(orderId, orderKey.orderId) &&
                   Objects.equals(restId, orderKey.restId);
        }
        
        @Override
        public int hashCode() { 
            return Objects.hash(orderId, restId); 
        }
    }


    @Override
    protected String getKeyColumnNames() {
        return "order_id = ? AND restaurant_idRest = ?";
    }

    @Override
    protected void setKeyParameters(PreparedStatement ps, int idx, OrderKey key) throws SQLException {
        ps.setString(idx, key.getOrderId());
        ps.setString(idx + 1, key.getRestId());
    }

    @Override
    protected OrderKey decodeKey(ResultSet rs) throws SQLException {
        return new OrderKey(rs.getString("order_id"), rs.getString("restaurant_idRest"));
    }

    @Override
    protected Order decodeValue(ResultSet rs) throws SQLException {

        String orderId = rs.getString("order_id");
        String restId = rs.getString("restaurant_idRest");
        float price = rs.getFloat("price");
        String state = rs.getString("state");
        String notes = rs.getString("notes");
        String type = rs.getString("type");


        LocalDateTime expcTime = toLDT(rs.getTimestamp("expc_prep_time"));
        LocalDateTime orderTime = toLDT(rs.getTimestamp("order_time"));
        LocalDateTime finishTime = toLDT(rs.getTimestamp("finish_time"));


        Map<String, Integer> items = new HashMap<>();
        String sqlItems = "SELECT name, quantity FROM items WHERE order_order_id = ? AND order_restaurant_idRest = ?";
        try (PreparedStatement ps = this.connection.prepareStatement(sqlItems)) {
            ps.setString(1, orderId);
            ps.setString(2, restId);
            try (ResultSet rsItems = ps.executeQuery()) {
                while (rsItems.next()) {
                    items.put(rsItems.getString("name"), rsItems.getInt("quantity"));
                }
            }
        }


        Map<String, Task> tasks = new HashMap<>();
        String sqlTasks = "SELECT * FROM task WHERE order_order_id = ? AND order_restaurant_idRest = ?";
        try (PreparedStatement ps = this.connection.prepareStatement(sqlTasks)) {
            ps.setString(1, orderId);
            ps.setString(2, restId);
            try (ResultSet rsTasks = ps.executeQuery()) {
                while (rsTasks.next()) {
                    String tId = rsTasks.getString("idtask");
                    String desc = rsTasks.getString("description");
                    boolean completed = rsTasks.getBoolean("is_completed");
                    String role = rsTasks.getString("role");
                    LocalDateTime tTime = toLDT(rsTasks.getTimestamp("expc_time"));


                    Task t = new Task(tId, desc, role, tTime);
                    if (completed) t.completeTask(); 
                    
                    tasks.put(tId, t);
                }
            }
        }

        return new Order(orderId, restId, price, state, items, notes, type, orderTime, expcTime, finishTime, tasks);
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO `order` (order_id, restaurant_idRest, price, state, notes, expc_prep_time, order_time, finish_time, type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Order v) throws SQLException {
        // Aqui usamos os GETTERS que adicionei à classe Order
        ps.setString(1, v.getOrderId());
        ps.setString(2, v.getRestId());
        ps.setFloat(3, v.getPrice());
        ps.setString(4, v.getState());
        ps.setString(5, v.getNotes());
        ps.setTimestamp(6, toSQL(v.getExpectedTime()));
        ps.setTimestamp(7, toSQL(v.getOrderTime()));
        ps.setTimestamp(8, toSQL(v.getFinishTime()));
        ps.setString(9, v.getType());
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE `order` SET price=?, state=?, notes=?, expc_prep_time=?, order_time=?, finish_time=?, type=? WHERE order_id=? AND restaurant_idRest=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Order v) throws SQLException {
        ps.setFloat(1, v.getPrice());
        ps.setString(2, v.getState());
        ps.setString(3, v.getNotes());
        ps.setTimestamp(4, toSQL(v.getExpectedTime()));
        ps.setTimestamp(5, toSQL(v.getOrderTime()));
        ps.setTimestamp(6, toSQL(v.getFinishTime()));
        ps.setString(7, v.getType());
        // WHERE
        ps.setString(8, v.getOrderId());
        ps.setString(9, v.getRestId());
    }

    @Override
    public Order put(OrderKey key, Order value) {
        super.put(key, value); 
        saveChildren(value);
        return value;
    }

    private void saveChildren(Order order) {
        String oId = order.getOrderId();
        String rId = order.getRestId();

        try {
            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM items WHERE order_order_id=? AND order_restaurant_idRest=?")) {
                ps.setString(1, oId); ps.setString(2, rId);
                ps.executeUpdate();
            }
            String sqlItem = "INSERT INTO items (order_order_id, order_restaurant_idRest, name, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sqlItem)) {
                for (Map.Entry<String, Integer> entry : order.getItems().entrySet()) {
                    ps.setString(1, oId);
                    ps.setString(2, rId);
                    ps.setString(3, entry.getKey());
                    ps.setInt(4, entry.getValue());
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement ps = connection.prepareStatement("DELETE FROM task WHERE order_order_id=? AND order_restaurant_idRest=?")) {
                ps.setString(1, oId); ps.setString(2, rId);
                ps.executeUpdate();
            }
            String sqlTask = "INSERT INTO task (idtask, is_completed, description, order_order_id, order_restaurant_idRest, role, expc_time) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sqlTask)) {
                // Aqui usamos o novo método getRawTasksMap()
                for (Task t : order.getRawTasksMap().values()) {
                    ps.setString(1, t.getTaskId()); 
                    ps.setBoolean(2, t.isCompleted());
                    ps.setString(3, t.getDescription());
                    ps.setString(4, oId);
                    ps.setString(5, rId);
                    ps.setString(6, t.getRole());
                    ps.setTimestamp(7, toSQL(t.getDeadline())); 
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao guardar detalhes da Order", e);
        }
    }

    private LocalDateTime toLDT(Timestamp ts) { return ts == null ? null : ts.toLocalDateTime(); }
    private Timestamp toSQL(LocalDateTime ldt) { return ldt == null ? null : Timestamp.valueOf(ldt); }
}