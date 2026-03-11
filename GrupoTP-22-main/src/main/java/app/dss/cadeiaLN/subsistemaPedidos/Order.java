package app.dss.cadeiaLN.subsistemaPedidos;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class Order {
    private String orderId;
    private String restId;
    private float price;
    private String state;
    private Map<String, Integer> items;
    private String notes;
    private String type;

    private LocalDateTime expectedTime;
    private LocalDateTime orderTime;
    private LocalDateTime finishTime;

    private Map<String, Task> tasks;

    public Order(String orderID, String idRest, String type, float totalPrice, Map<String, Integer> items, String notes, LocalDateTime orderTime, LocalDateTime expectedTime){
        this.orderId = orderID; 
        this.restId = idRest;
        this.type = type;
        this.price = totalPrice;
        this.state = "EM PREPARAÇÃO";
        this.items = new HashMap<>(items);
        this.notes = notes;

        this.orderTime = orderTime;
        this.expectedTime = expectedTime;
        this.finishTime = null;
        this.tasks = new HashMap<>();

        int taskCounter = 1;


        for (Map.Entry<String, Integer> item : items.entrySet()) {
            // Exemplo: "ORD-50-T-1", "ORD-50-T-2"
            String taskId = this.orderId + "-T-" + taskCounter++; 

            String description = "Preparar: " + item.getKey() + " (Qtd: " + item.getValue() + ")";
            String role = "Confeção"; 

            Task t = new Task(taskId, description, role, this.expectedTime);
            this.tasks.put(taskId, t);
        }

        String deliveryTaskId = this.orderId + "-T-" + taskCounter; 
        String deliveryDescription = "Entrega do Pedido";
        String deliveryRole = "Entrega";

        Task deliveryTask = new Task(deliveryTaskId, deliveryDescription, deliveryRole, this.expectedTime);
        this.tasks.put(deliveryTaskId, deliveryTask);

    }

    public Order(String orderId, String restId, float price, String state, 
                 Map<String, Integer> items, String notes, String type, 
                 LocalDateTime orderTime, LocalDateTime expectedTime, LocalDateTime finishTime, 
                 Map<String, Task> tasks) {
        this.orderId = orderId;
        this.restId = restId;
        this.price = price;
        this.state = state;
        this.items = items != null ? new HashMap<>(items) : new HashMap<>();
        this.notes = notes;
        this.type = type;
        this.orderTime = orderTime;
        this.expectedTime = expectedTime;
        this.finishTime = finishTime;
        this.tasks = tasks != null ? new HashMap<>(tasks) : new HashMap<>();
    }


    public String getOrderId(){ return this.orderId; }
    public String getRestId() { return this.restId; } 
    public String getType() { return this.type; }    
    public String getNotes() { return this.notes; }   
    public float getPrice() { return this.price; }
    public String getState(){ return this.state; }
    public LocalDateTime getExpectedTime(){ return this.expectedTime; }
    public LocalDateTime getOrderTime() { return this.orderTime; } 
    public LocalDateTime getFinishTime() { return this.finishTime; } 

    public Map<String,Integer> getItems(){
        return new HashMap<>(this.items);
    }

    public Map<String,String> getTasks(){
        Map<String,String> orderTasks = new HashMap<>();
        for(Task t: this.tasks.values()){
            orderTasks.put(t.getTaskId(),t.toString());
        }
        return orderTasks;
    }

    public Map<String, Task> getRawTasksMap() {
        return this.tasks;
    }

    public List<String> getTasksString(){
        List<String> tasks = new ArrayList<>();

        if(this.tasks == null) return tasks;
        for(Task t: this.tasks.values()){
            tasks.add(t.toString());
        }
        
        return tasks;
    }

    
    public long getTotalTime() {
        if (this.orderTime == null) {
            return 0L;
        }
        LocalDateTime endTime = (this.finishTime != null) ? this.finishTime : LocalDateTime.now();
        return ChronoUnit.SECONDS.between(this.orderTime, endTime);
    }

    public void completeOrder() {
        this.state = "CONCLUIDA";
        this.finishTime = LocalDateTime.now();
    }

    public void delayOrder(LocalDateTime time){
        this.expectedTime = time;
    }

    public void changeOrderState(String newState){
        this.state = newState;
    }

    public boolean finishTask(String taskId){
        if(!this.tasks.containsKey(taskId)){
            System.err.println("Task id " + taskId + " inexistente neste pedido.");
            return false;
        }
        
        this.tasks.get(taskId).completeTask();

        boolean allTasksDone = this.tasks.values().stream()
                                    .allMatch(Task::isCompleted);
        if (allTasksDone) {
            this.completeOrder();
        }
        return allTasksDone;
    }

    @Override
    public String toString() {
        long totalSeconds = this.getTotalTime();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        String formattedDuration = String.format("%dm %02ds", minutes, seconds);

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedOrderTime = (this.orderTime != null) ? this.orderTime.format(timeFormatter) : "N/A";
        String formattedExpectedTime = (this.expectedTime != null) ? this.expectedTime.format(timeFormatter) : "N/A";

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append(String.format("  PEDIDO: %s \n", this.orderId));
        sb.append("==========================================\n");
        sb.append(String.format("  Estado:    %s\n", this.state));
        sb.append(String.format("  Preço:     %.2f€\n", this.price));
        sb.append(String.format("  Tipo:      %s\n", this.type));
        sb.append(String.format("  Feito às:  %s\n", formattedOrderTime));
        sb.append(String.format("  Esperado:  %s\n", formattedExpectedTime));
        sb.append(String.format("  Tempo em espera:   %s\n", formattedDuration));
        sb.append("------------------------------------------\n");

        sb.append("  Itens do Pedido:\n");
        if (this.items == null || this.items.isEmpty()) {
            sb.append("    (Nenhum item listado)\n");
        } else {
            for (Map.Entry<String, Integer> item : this.items.entrySet()) {
                sb.append(String.format("    - %s (Qtd: %d)\n", item.getKey(), item.getValue()));
            }
        }
        sb.append("------------------------------------------\n");
        
        if (this.notes != null && !this.notes.isEmpty()) {
             sb.append("------------------------------------------\n");
             sb.append("  Notas: " + this.notes + "\n");
            sb.append("------------------------------------------\n");
        }
        sb.append(String.format("  Restaurante:    %s\n", this.restId));
        sb.append("==========================================\n");
        
        return sb.toString();
    }
}