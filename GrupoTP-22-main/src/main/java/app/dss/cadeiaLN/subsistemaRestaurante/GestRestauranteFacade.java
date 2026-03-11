package app.dss.cadeiaLN.subsistemaRestaurante;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import app.dss.cadeiaDL.RestaurantDAO;

public class GestRestauranteFacade implements IGestRestaurante {
    private RestaurantDAO restaurants;

    public GestRestauranteFacade() {
        this.restaurants = new RestaurantDAO();
    }

    public void addRestaurant(String id, String name, String location) {
        Restaurant r = new Restaurant(id, name, location);
        this.restaurants.put(id, r); 
    }

    public void removeRestaurant(String id) {
        if (!this.restaurants.containsKey(id)) {
            System.err.println("Id de restaurante não existe");
            return;
        }
        this.restaurants.remove(id); 
    }

    public String getOrderId(String idRest, String type) {
        Restaurant r = this.restaurants.get(idRest);
        if (r != null) {
            String newId = r.getNextOrderID(type);
            this.restaurants.put(idRest, r); 
            return newId;
        }
        return null;
    }

    public void makIngredientOrder(String idRest, String ingredient, int quantity) {
        Restaurant r = this.restaurants.get(idRest);
        if (r == null) {
            System.err.println("Id de restaurante nao existe");
            return;
        }

        r.makeIngredientOrder(ingredient, quantity);
        
        this.restaurants.put(idRest, r);
    }

    public void addStock(String idRest, int orderID) {
        Restaurant r = this.restaurants.get(idRest);
        if (r == null) {
            System.err.println("Id de restaurante nao existe");
            return;
        }
        
        r.addStock(orderID);
        
        this.restaurants.put(idRest, r);
    }

    public void removeStock(String idRest, String ingredient, int quantity) {
        Restaurant r = this.restaurants.get(idRest);
        if (r == null) {
            System.err.println("Id de restaurante nao existe");
            return;
        }
        
        r.removeStock(ingredient, quantity);
        
        this.restaurants.put(idRest, r);
    }

    public Set<String> listRestaurants() {
        return new HashSet<>(this.restaurants.keySet());
    }

    public List<String> listIngredientOrders(String idRest) {
        Restaurant r = this.restaurants.get(idRest);
        if (r == null) {
            System.err.println("Id de restaurante nao existe");
            return new ArrayList<>();
        }
        return r.listIngredientOrders();
    }

    public Map<String, Integer> getIngredientOrderItem(String idRest, int orderID) {
        Restaurant r = this.restaurants.get(idRest);
        if (r == null) {
            System.err.println("Id de restaurante nao existe");
            return new HashMap<>();
        }
        return r.getIngredientOrderItem(orderID);
    }

    public Map<String, Integer> getIngredients(String idRest) {
        Restaurant r = this.restaurants.get(idRest);
        if (r == null) {
            System.err.println("Id de restaurante nao existe");
            return new HashMap<>();
        }
        return r.getIngredients();
    }

    public boolean hasStock(String idRest, String ingredientName, int quantity) {
        Restaurant r = this.restaurants.get(idRest);
        if (r == null) {
            System.err.println("Id de restaurante nao existe");
            return false;
        }
        return r.getIngredientQuantity(ingredientName) >= quantity;
    }
}