package app.dss.cadeiaLN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ICadeiaLN{

   // Gest Reportsi
    public String consultChainReport(LocalDate start, LocalDate end, List<String> stats); // seq - done | checked - reported
    public String consultRestaurantReport(String idRest, LocalDate start, LocalDate end, List<String> stats); // seq - done | checked - reported
    public List<Map.Entry<String,Integer>> getTopNSales(String idRest, LocalDate inicio, LocalDate fim, int n); // seq - done | Checked - reported

     //Gest Restaurante
    public String listRestIngredients(String idRest);
    public void receiveStockOrder(String idRest, String orderID); // seq - done | checked - reported
    public void createIngredientOrder(String idRest, String ingredient, int quantity); // seq - done | checked - reported
    public void deleteIngredientStock(String idRest, String ingredient, int quantity); // seq - done | checked - reported
    public String listPendingIngredientOrders(String idRest); //seq - done | checked - reported
    public Set<String> listRestaurants();
    public void addRestaurant(String id, String name, String location);
    public List<String> listEmployees(String idRestFilter);

    // Gest Funcionário
    public String consulFuncionario(String num); // seq - done | checked - reported
    public void relocateFun(String num, String newRole); // seq - done | Checked - reported
    public void addFunc(String idRest, String name, String email, String phone_number, String role); //seq - done | Checked - reported
    public void removeFunc(String num); //seq - done | checked - reported
    public void contactRest(String idRest, String idFunc, String msg); //seq - done | Checked - reported
    public Boolean login(String num, String password); //seq - done | checked - reported
    public String consultMessages(String num); //Seq - done | Checked - reported
    public void sendMessage(String idFunc, String idDest, String msg); //seq - done |  Checked - reported
    public String getRole(String idFunc);
    public String getRestID(String idFunc);
    public Set<String> getReceivedMessages(String numFunc);
    public Set<String> getSentMessages(String numFunc);
    public Boolean changePassword(String numFunc, String newPassword); //seq - done | Checked - reported

    //GestOrders
    public void changeTaskState(String idRest, String orderID, String taskID); //seq - done | Checked - reported
    public String getNextOrders(String idRest); //seq - done | Checked - reported
    public String getOrderResume(String idRest, String orderId);
    public void delayOrder(String idRest, String orderId, LocalDateTime delay); //seq - done | Checked - reported
    public boolean hasOrderStock(String idRest, Map<String,Integer> mealItems);
    public void createOrder(String idRest, String orderType, List<String> items, String notes, LocalDateTime orderTime); //Seq - done |Checked - reported
    public List<String> getOrderTasks(String idRest, String orderID); //seq - done | Checked - reported

    //Subsistema Menus
    public String getItemDetails(String name);
    public Set<String> getMealNames();
    public Set<String> getDrinkNames();
    public Set<String> getMenuNames();

}