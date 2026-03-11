package app.dss.cadeiaDL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import app.dss.cadeiaLN.subsistemaRestaurante.Ingredient;
import app.dss.cadeiaLN.subsistemaRestaurante.IngredientOrder;
import app.dss.cadeiaLN.subsistemaRestaurante.Restaurant;

public class RestaurantDAO extends AbstractDAO<String, Restaurant>{
    public RestaurantDAO(){
        super("restaurant");
    }

    @Override
    protected String getKeyColumnNames(){
        return "idRest = ?";
    }

    @Override
    protected void setKeyParameters(PreparedStatement ps, int initialIndex, String key) throws SQLException{
        ps.setString(initialIndex, key);
    }

    @Override 
    protected String decodeKey(ResultSet rs) throws SQLException{
        return new String(rs.getString("idRest"));
    }

    @Override
    protected Restaurant decodeValue(ResultSet rs) throws SQLException {
        String idRest = rs.getString("idRest");
        String name = rs.getString("Nome");
        String loc = rs.getString("Localização");
        int nextOrder = rs.getInt("nextOrderID");
        int nextIngOrder = rs.getInt("nextIngredientOrderID");

        Map<String, Ingredient> stock = new HashMap<>();
        String sqlStock = "SELECT * FROM ingredient_stock WHERE id_rest = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlStock)) {
            ps.setString(1, idRest);
            try (ResultSet rsStock = ps.executeQuery()) {
                while (rsStock.next()) {
                    String ingName = rsStock.getString("name");
                    int qtd = rsStock.getInt("quantity");
                    stock.put(ingName, new Ingredient(ingName, qtd));
                }
            }
        }

        Map<Integer, IngredientOrder> orders = new TreeMap<>();
        String sqlOrders = "SELECT * FROM ingredientOrder WHERE idRest = ?";
        try (PreparedStatement ps = connection.prepareStatement(sqlOrders)) {
            ps.setString(1, idRest);
            try (ResultSet rsOrd = ps.executeQuery()) {
                while (rsOrd.next()) {
                    int oId = rsOrd.getInt("id");
                    String state = rsOrd.getString("state");
                    String prod = rsOrd.getString("product");
                    int qtd = rsOrd.getInt("quantity");
                    
                    java.sql.Timestamp tsOrder = rsOrd.getTimestamp("order_date");
                    java.sql.Timestamp tsArrival = rsOrd.getTimestamp("expected_arrival");
                    
                    LocalDateTime dtOrder = tsOrder != null ? tsOrder.toLocalDateTime() : LocalDateTime.now();
                    LocalDateTime dtArrival = tsArrival != null ? tsArrival.toLocalDateTime() : LocalDateTime.now().plusDays(1);
                    
                    Ingredient item = new Ingredient(prod, qtd);
                    
                    IngredientOrder io = new IngredientOrder(oId, item, dtOrder, dtArrival); 
                    io.setState(state);
                    
                    orders.put(oId, io);
                }
            }
        }

        return new Restaurant(idRest, name, loc, nextOrder, nextIngOrder, stock, orders);
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO restaurant (idRest, Nome, Localização, nextOrderID, nextIngredientOrderID) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Restaurant v) throws SQLException {
        ps.setString(1, v.getID());
        ps.setString(2, v.getName());
        ps.setString(3, v.getLocation());
        ps.setInt(4, v.getNextOrderIDNum());
        ps.setInt(5, v.getNextIngredientOrderId());
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE restaurant SET Nome=?, Localização=?, nextOrderID=?, nextIngredientOrderID=? WHERE idRest=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Restaurant v) throws SQLException {
        ps.setString(1, v.getName());
        ps.setString(2, v.getLocation());
        ps.setInt(3, v.getNextOrderIDNum());
        ps.setInt(4, v.getNextIngredientOrderId());
        ps.setString(5, v.getID());
    }

    @Override
    public Restaurant put(String key, Restaurant value) {
        super.put(key, value); 
        saveDetails(value);
        return value;
    }

    private void saveDetails(Restaurant r) {
        try {
            String delStock = "DELETE FROM ingredient_stock WHERE id_rest=?";
            try (PreparedStatement ps = connection.prepareStatement(delStock)) {
                ps.setString(1, r.getID());
                ps.executeUpdate();
            }
            String insStock = "INSERT INTO ingredient_stock (name, quantity, id_rest) VALUES (?, ?, ?) " +
                              "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";
            try (PreparedStatement ps = connection.prepareStatement(insStock)) {
                for (Ingredient i : r.getStockMap().values()) {
                    ps.setString(1, i.getName());
                    ps.setInt(2, i.getQuantity());
                    ps.setString(3, r.getID());
                    ps.executeUpdate();
                }
            }

            String sqlOrder = "INSERT INTO ingredientOrder (id, state, product, quantity, order_date, expected_arrival, idRest) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                              "ON DUPLICATE KEY UPDATE state=VALUES(state)"; 
            
            try(PreparedStatement ps = connection.prepareStatement(sqlOrder)){
                for(IngredientOrder io : r.getOrdersMap().values()){
                    ps.setInt(1, io.getOrderId());
                    ps.setString(2, io.getState());
                    ps.setString(3, io.getIngredientName());
                    ps.setInt(4, io.getQuantity());
                    
                    // Datas
                    ps.setTimestamp(5, io.getOrderDate() != null ? java.sql.Timestamp.valueOf(io.getOrderDate()) : null);
                    ps.setTimestamp(6, io.getExpectedArrivalDate() != null ? java.sql.Timestamp.valueOf(io.getExpectedArrivalDate()) : null);
                    
                    ps.setString(7, r.getID());
                    ps.executeUpdate();
                }
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao guardar detalhes do restaurante", e);
        }
    }
}