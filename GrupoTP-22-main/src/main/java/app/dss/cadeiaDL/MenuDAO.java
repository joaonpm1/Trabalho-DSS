package app.dss.cadeiaDL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import app.dss.cadeiaLN.subsistemaMenus.Drink;
import app.dss.cadeiaLN.subsistemaMenus.Meal;
import app.dss.cadeiaLN.subsistemaMenus.Menu;


public class MenuDAO extends AbstractDAO<String, Object> {

    public MenuDAO() {
        super("catalog_product");
    }

    @Override protected String getKeyColumnNames() { return "name = ?"; }
    @Override protected void setKeyParameters(PreparedStatement ps, int idx, String key) {}
    @Override protected String decodeKey(ResultSet rs) { return null; }
    @Override protected Object decodeValue(ResultSet rs) { return null; }
    @Override protected String getInsertSql() { return ""; }
    @Override protected void setInsertParameters(PreparedStatement ps, Object value) {}
    @Override protected String getUpdateSql() { return ""; }
    @Override protected void setUpdateParameters(PreparedStatement ps, Object value) {}


    public void upsertProduct(String name, float price, int prepTime, String type, Map<String, Integer> recipe) {
        String sqlProduct = "INSERT INTO catalog_product (name, price, exp_prep_minutes, type) VALUES (?, ?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE price = VALUES(price), exp_prep_minutes = VALUES(exp_prep_minutes)";

        try {
            try (PreparedStatement ps = this.connection.prepareStatement(sqlProduct)) {
                ps.setString(1, name);
                ps.setFloat(2, price);
                ps.setInt(3, prepTime);
                ps.setString(4, type);
                ps.executeUpdate();
            }

            updateRecipe(name, recipe);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void upsertMenu(String name, float price, int prepTime, Menu menuObj) {
        String sqlProduct = "INSERT INTO catalog_product (name, price, exp_prep_minutes, type) VALUES (?, ?, ?, 'Menu') " +
                            "ON DUPLICATE KEY UPDATE price = VALUES(price), exp_prep_minutes = VALUES(exp_prep_minutes)";

        try {
            try (PreparedStatement ps = this.connection.prepareStatement(sqlProduct)) {
                ps.setString(1, name);
                ps.setFloat(2, price);
                ps.setInt(3, prepTime);
                ps.executeUpdate();
            }

            updateMenuComposition(name, menuObj);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateRecipe(String productName, Map<String, Integer> recipe) throws SQLException {
        String delSql = "DELETE FROM recipe WHERE catalog_product_name = ?";
        try (PreparedStatement ps = this.connection.prepareStatement(delSql)) {
            ps.setString(1, productName);
            ps.executeUpdate();
        }

        if (recipe != null && !recipe.isEmpty()) {
            String insSql = "INSERT INTO recipe (catalog_product_name, ing_name, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement ps = this.connection.prepareStatement(insSql)) {
                for (Map.Entry<String, Integer> entry : recipe.entrySet()) {
                    ps.setString(1, productName);
                    ps.setString(2, entry.getKey());
                    ps.setInt(3, entry.getValue()); 
                    ps.executeUpdate();
                }
            }
        }
    }

    private void updateMenuComposition(String menuName, Menu menuObj) throws SQLException {
        // Apagar composição antiga
        String delSql = "DELETE FROM menu_composition WHERE menu_name = ?";
        try (PreparedStatement ps = this.connection.prepareStatement(delSql)) {
            ps.setString(1, menuName);
            ps.executeUpdate();
        }

        String insSql = "INSERT INTO menu_composition (menu_name, item_name, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = this.connection.prepareStatement(insSql)) {
            
            for (Meal m : menuObj.getMeals()) {
                ps.setString(1, menuName);
                ps.setString(2, m.getName());
                ps.setInt(3, 1);
                ps.executeUpdate();
            }
            
            for (Drink d : menuObj.getDrinks()) {
                ps.setString(1, menuName);
                ps.setString(2, d.getName());
                ps.setInt(3, 1);
                ps.executeUpdate();
            }
        }
    }
}