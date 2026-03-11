package app.dss.cadeiaLN;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.dss.cadeiaLN.subsistemaFuncionarios.GestFuncionariosFacade;
import app.dss.cadeiaLN.subsistemaFuncionarios.IGestFuncionarios;
import app.dss.cadeiaLN.subsistemaMenus.GestMenusFacade;
import app.dss.cadeiaLN.subsistemaMenus.IGestMenus;
import app.dss.cadeiaLN.subsistemaPedidos.GestOrdersFacade;
import app.dss.cadeiaLN.subsistemaPedidos.IGestOrders;
import app.dss.cadeiaLN.subsistemaPedidos.Order;
import app.dss.cadeiaLN.subsistemaReports.GestReportsFacade;
import app.dss.cadeiaLN.subsistemaReports.IGestReports;
import app.dss.cadeiaLN.subsistemaRestaurante.GestRestauranteFacade;
import app.dss.cadeiaLN.subsistemaRestaurante.IGestRestaurante;

public class cadeiaLNFacade implements ICadeiaLN {
    private IGestFuncionarios gestorFunc;
    private IGestMenus gestMenu;
    private IGestOrders gestOrders;
    private IGestReports gestReports;
    private IGestRestaurante gestRest;

    public cadeiaLNFacade() {
        this.gestMenu = new GestMenusFacade();
        this.gestOrders = new GestOrdersFacade();
        this.gestorFunc = new GestFuncionariosFacade();
        this.gestReports = new GestReportsFacade();
        this.gestRest = new GestRestauranteFacade();
    }

    @Override
    public Set<String> getReceivedMessages(String numFunc) {
        return this.gestorFunc.getReceivedMessages(numFunc);
    }

    @Override
    public Set<String> getSentMessages(String numFunc) {
        return this.gestorFunc.getSentMessages(numFunc);
    }

    @Override
    public void sendMessage(String numFunc, String numDest, String text) {
        this.gestorFunc.sendMessage(numFunc, numDest, text);
    }

    @Override
    public Boolean changePassword(String numFunc, String newPassword) {
        return this.gestorFunc.changePassword(numFunc, newPassword);
    }

    @Override 
    public List<Map.Entry<String,Integer>> getTopNSales(String idRest, LocalDate inicio, LocalDate fim, int n){
        return this.gestReports.getTopNItemSales(idRest, inicio, fim, n);
    }

    // --- Funcionários ---
    @Override public String getRole(String idFunc) { return this.gestorFunc.getRole(idFunc); }
    @Override public String getRestID(String idFunc) { return this.gestorFunc.getRestId(idFunc); }
    @Override public String consulFuncionario(String num) { return this.gestorFunc.consultFuncionario(num); }
    @Override public void relocateFun(String num, String newRole) { this.gestorFunc.relocateFuncionario(num, newRole); }
    @Override public void addFunc(String idRest, String name, String email, String phone, String role) { this.gestorFunc.addFuncionario(name, email, phone, role, idRest); }
    @Override public void removeFunc(String num) { this.gestorFunc.removeFuncionario(num); }
    @Override public Boolean login(String num, String password) { return this.gestorFunc.login(num, password); }
    
    @Override public void contactRest(String idFunc, String idRest, String msg) { this.gestorFunc.contactRestaurant(idFunc, idRest, msg); }
    
    @Override
    public String consultMessages(String num) {
        Set<String> rcv = this.gestorFunc.getReceivedMessages(num);
        Set<String> snt = this.gestorFunc.getSentMessages(num);
        return "Recebidas:\n" + (rcv != null ? String.join("\n", rcv) : "") + 
               "\nEnviadas:\n" + (snt != null ? String.join("\n", snt) : "");
    }

    @Override
    public boolean hasOrderStock(String idRest, Map<String, Integer> mealItems) {
        Map<String, Integer> totalIngredientsNeeded = new HashMap<>();

        for (Map.Entry<String, Integer> entry : mealItems.entrySet()) {
            String prodName = entry.getKey();
            int quantity = entry.getValue();

            Map<String, Integer> recipe = null;
            String type = this.gestMenu.getItemType(prodName);
            
            if ("Menu".equals(type)) recipe = this.gestMenu.getMenuReceipe(prodName);
            else if ("Meal".equals(type)) recipe = this.gestMenu.getMealReceipe(prodName);
            else if ("Drink".equals(type)) recipe = this.gestMenu.getDrinkReceipe(prodName);

            if (recipe != null) {
                recipe.forEach((ingName, ingQtd) -> 
                    totalIngredientsNeeded.merge(ingName, ingQtd * quantity, Integer::sum)
                );
            }
        }

        for (Map.Entry<String, Integer> needed : totalIngredientsNeeded.entrySet()) {
            if (!this.gestRest.hasStock(idRest, needed.getKey(), needed.getValue())) {
                return false; 
            }
        }
        return true;
    }

    @Override
    public void createOrder(String idRest, String orderType, List<String> itemsList, String notes, LocalDateTime orderTime) {
        float totalPrice = 0;
        int totalPrepTime = 0;
        Map<String, Integer> itemsMap = new HashMap<>(); 
        Map<String, Integer> consumedIngredients = new HashMap<>();

        for (String itemName : itemsList) {
            itemsMap.merge(itemName, 1, Integer::sum);

            String type = this.gestMenu.getItemType(itemName);
            if (type != null) {
                totalPrepTime += this.gestMenu.getItemExpectedPreperationTime(itemName);
                
                Map<String, Integer> recipe = null;
                switch (type){
                    case "Menu":
                        totalPrice += this.gestMenu.getMenuPrice(itemName);
                        recipe = this.gestMenu.getMenuReceipe(itemName);
                        break;
                    case "Meal":
                        totalPrice += this.gestMenu.getMealPrice(itemName);
                        recipe = this.gestMenu.getMealReceipe(itemName);
                        break;
                    case "Drink":
                        totalPrice += this.gestMenu.getDrinkPrice(itemName);
                        recipe = this.gestMenu.getDrinkReceipe(itemName);
                        break;
                }

                if (recipe != null) {
                    recipe.forEach((ing, qtd) -> {
                        consumedIngredients.merge(ing, qtd, Integer::sum);
                        this.gestRest.removeStock(idRest, ing, qtd);
                    });
                }
            }
        }

        String orderID = this.gestRest.getOrderId(idRest, orderType);
        
        LocalDateTime expectedPrepTime = orderTime.plusMinutes(totalPrepTime);

        if (orderID != null) {
            Order o = new Order(orderID, idRest, orderType, totalPrice, itemsMap, notes, orderTime, expectedPrepTime);
            this.gestOrders.addOrder(o, idRest);
            System.out.println("Order criada com sucesso: " + orderID);
        } else {
            System.err.println("Erro ao gerar ID para o restaurante: " + idRest);
            return;
        }

        this.gestReports.updateSalesReport(idRest, orderTime.toLocalDate(), itemsList.size(), totalPrice, itemsMap);
        this.gestReports.updateStockReport(idRest, orderTime.toLocalDate(), consumedIngredients);
    }

    @Override
    public String getItemDetails(String name) {
        String type = this.gestMenu.getItemType(name);
        if (type == null) return null;

        float price = 0;
        int time = this.gestMenu.getItemExpectedPreperationTime(name);
        Map<String, Integer> recipeMap = null;

        switch (type) {
            case "Menu": 
                price = this.gestMenu.getMenuPrice(name); 
                recipeMap = this.gestMenu.getMenuReceipe(name);
                break;
            case "Meal": 
                price = this.gestMenu.getMealPrice(name); 
                recipeMap = this.gestMenu.getMealReceipe(name);
                break;
            case "Drink": 
                price = this.gestMenu.getDrinkPrice(name); 
                recipeMap = this.gestMenu.getDrinkReceipe(name);
                break;
        }

        StringBuilder recipeStr = new StringBuilder();
        if (recipeMap != null && !recipeMap.isEmpty()) {
            int i = 0;
            for (Map.Entry<String, Integer> entry : recipeMap.entrySet()) {
                recipeStr.append(entry.getKey()).append(": ").append(entry.getValue());
                if (i < recipeMap.size() - 1) recipeStr.append(", ");
                i++;
            }
        } else {
            recipeStr.append("N/A");
        }

        return String.format("%.2f;%d;%s", price, time, recipeStr.toString());
    }

    @Override public Set<String> getMealNames() { return this.gestMenu.listMeals(); }
    @Override public Set<String> getDrinkNames() { return this.gestMenu.listDrinks(); }
    @Override public Set<String> getMenuNames() { return this.gestMenu.listMenus(); }

    @Override
    public void changeTaskState(String idRest, String orderID, String taskID) {
        boolean lastTask = this.gestOrders.changeTaskState(orderID, idRest, taskID);
        if (lastTask) {
            long totalSeconds = this.gestOrders.getOrderFinishTime(idRest, orderID);
            this.gestReports.updateAttendenceReport(idRest, LocalDate.now(), totalSeconds);
        }
    }

    @Override 
    public List<String> getOrderTasks(String idRest, String orderID){
        return this.gestOrders.getOrderTasks(idRest, orderID);
    }

    // --- Relatórios e Stock ---
    @Override public String consultChainReport(LocalDate start, LocalDate end, List<String> stats) {
        StringBuilder result = new StringBuilder();
        result.append("=== Relatório da Cadeia ===\n");
        for (String stat : stats) {
            result.append(">> ").append(stat).append(":\n");
            switch (stat.toLowerCase()) {
                case "vendas": case "sales": result.append(this.gestReports.getChainSalesReport(start, end)); break;
                case "stock": case "stocks": result.append(this.gestReports.getChainStockReport(start, end)); break;
                case "tempos": case "attendance": result.append(this.gestReports.getChainAttendenceReport(start, end)); break;
            }
            result.append("\n");
        }
        return result.toString();
    }

    @Override public String consultRestaurantReport(String idRest, LocalDate start, LocalDate end, List<String> stats) {
        StringBuilder result = new StringBuilder();
        result.append("=== Relatório ").append(idRest).append(" ===\n");
        for (String stat : stats) {
            result.append(">> ").append(stat).append(":\n");
            switch (stat.toLowerCase()) {
                case "vendas": case "sales": result.append(this.gestReports.getSalesReport(idRest, start, end)); break;
                case "stock": case "stocks": result.append(this.gestReports.getStockReport(idRest, start, end)); break;
                case "tempos": case "attendance": result.append(this.gestReports.getAttendenceReport(idRest, start, end)); break;
            }
            result.append("\n");
        }
        return result.toString();
    }

    @Override public String listRestIngredients(String idRest) {
        Map<String, Integer> ings = this.gestRest.getIngredients(idRest);
        if (ings == null || ings.isEmpty()) return "Stock vazio.";
        StringBuilder sb = new StringBuilder();
        ings.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
        return sb.toString();
    }
    @Override public void receiveStockOrder(String idRest, String orderID) { 
        try { this.gestRest.addStock(idRest, Integer.parseInt(orderID)); } 
        catch (NumberFormatException e) { System.err.println("ID inválido: " + orderID); }
    }
    @Override public void createIngredientOrder(String idRest, String ing, int qtd) { this.gestRest.makIngredientOrder(idRest, ing, qtd); }
    @Override public void deleteIngredientStock(String idRest, String ing, int qtd) { this.gestRest.removeStock(idRest, ing, qtd); }
    @Override public String listPendingIngredientOrders(String idRest) { 
        List<String> l = this.gestRest.listIngredientOrders(idRest);
        return (l == null || l.isEmpty()) ? "Sem encomendas pendentes." : String.join("\n", l);
    }
    @Override public String getNextOrders(String idRest) { 
        List<String> l = this.gestOrders.getPendingOrders(idRest);
        return (l == null || l.isEmpty()) ? "Sem pedidos pendentes." : String.join("\n", l);
    }
    @Override public String getOrderResume(String idRest, String oId) { return this.gestOrders.getOrderResume(oId, idRest); }
    @Override public void delayOrder(String idRest, String oId, LocalDateTime delay) { this.gestOrders.delayOrder(oId, idRest, delay); }


    @Override
    public Set<String> listRestaurants() {
        return this.gestRest.listRestaurants();
    }

    @Override
    public void addRestaurant(String id, String name, String location) {
        this.gestRest.addRestaurant(id, name, location);
    }

    @Override
    public List<String> listEmployees(String idRestFilter) {
        return this.gestorFunc.listEmployees(idRestFilter);
    }
}