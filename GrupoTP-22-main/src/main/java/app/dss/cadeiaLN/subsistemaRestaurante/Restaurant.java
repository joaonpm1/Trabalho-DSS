package app.dss.cadeiaLN.subsistemaRestaurante;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import java.util.List;
public class Restaurant{
    private int nextIngredientOrderId = 1;
    private int nextOrderIDNum = 1;

    private String id;
    private String name;
    private String location;

    private Map<Integer, IngredientOrder> orders;
    private Map<String, Ingredient> ingredientStock;


    public Restaurant(String id,String name, String location){
        this.id = new String(id);
        this.name = new String(name);
        this.location = new String(location);

        this.orders = new TreeMap<>();
        this.ingredientStock = new HashMap<>();
    }

    public Restaurant(String id, String name, String location, int nextOrderID, int nextIngOrderID, 
                      Map<String, Ingredient> stock, Map<Integer, IngredientOrder> orders) {
        this.id = id;
        this.name = name;
        this.location = location;
        
        this.nextOrderIDNum = nextOrderID;
        this.nextIngredientOrderId = nextIngOrderID;

        this.ingredientStock = (stock != null) ? stock : new HashMap<>();
        this.orders = (orders != null) ? orders : new TreeMap<>();
    }

    public int getNextOrderIDNum() { return nextOrderIDNum; }
    public int getNextIngredientOrderId() { return nextIngredientOrderId; }
    
    public Map<String, Ingredient> getStockMap() { return this.ingredientStock; }
    public Map<Integer, IngredientOrder> getOrdersMap() { return this.orders; }


    public String getID(){ return new String(this.id); }
    public String getName(){ return new String(this.name); }

    public String getLocation(){ return new String(this.location); }

    public String getNextOrderID(String orderType){
        nextOrderIDNum ++;
        return orderType.concat(String.valueOf(nextOrderIDNum));
    }

    public List<String> listIngredientOrders(){
        List<String> resultList = new ArrayList<>();
    
        for (IngredientOrder order : this.orders.values()) {
            
            if (order.getState().equals("EM TRANSITO")) {
                String ordStr = order.toString();
                resultList.add(ordStr);
            }
        }
    
        return resultList;
    }

   public Map<String,Integer> getIngredientOrderItem(int orderID){
        IngredientOrder i = this.orders.get(orderID);
        if(i == null) return null;
        HashMap<String,Integer> result = new HashMap<>();

        result.put(i.getIngredientName(),i.getQuantity());
        return result;
   }    

    public void makeIngredientOrder(String name, int quantity){
        Ingredient i = new Ingredient(name, quantity);
        LocalDateTime ordDate = LocalDateTime.now();

        double horasNecessarias = ((double) quantity / 100.0) * 24.0;
        long horasParaAdicionar = (long) Math.ceil(horasNecessarias);

        LocalDateTime expectedArr = ordDate.plusDays(horasParaAdicionar);

        IngredientOrder order = new IngredientOrder(nextIngredientOrderId, i, ordDate, expectedArr);
        nextIngredientOrderId++;

        this.orders.put(order.getOrderId(), order);
    }

    public int getIngredientQuantity(String name){
         return this.ingredientStock.containsKey(name)? this.ingredientStock.get(name).getQuantity() : 0;
    }

    public Map<String, Integer> getIngredients() {
        
        if (this.ingredientStock == null) {
            return new HashMap<>(); 
        }
    
        return this.ingredientStock.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,                    
                entry -> entry.getValue().getQuantity() 
            ));
    }

    public void addStock(int orderID){
        IngredientOrder order = this.orders.get(orderID);

        if(order == null){
            System.err.println("Order id nao existe");
            return;
        }

        String ingName = order.getIngredientName();

        if(this.ingredientStock.containsKey(ingName)){
            this.ingredientStock.get(ingName).add(order.getQuantity());
        } else {
            Ingredient i = new Ingredient(ingName, order.getQuantity());
            this.ingredientStock.put(i.getName(), i);
        }
        order.finishOrder();
    }

    public void removeStock(String ingName, int quantity){
        if(!this.ingredientStock.containsKey(ingName)){
            System.err.println("Ingrediente nao existe");
            return;
        }
        this.ingredientStock.get(ingName).remove(quantity);
    }


}