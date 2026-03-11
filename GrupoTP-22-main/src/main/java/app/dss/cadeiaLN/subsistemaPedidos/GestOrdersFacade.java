package app.dss.cadeiaLN.subsistemaPedidos;

import app.dss.cadeiaDL.OrderDAO;
import app.dss.cadeiaDL.OrderDAO.OrderKey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
public class GestOrdersFacade implements IGestOrders {
    
    private OrderDAO orderDAO;

    public GestOrdersFacade() {
        this.orderDAO = new OrderDAO();
    }


    @Override
    public long getOrderFinishTime(String idRest, String orderID) {
        OrderKey key = new OrderKey(orderID, idRest);
        Order o = this.orderDAO.get(key);

        if (o == null) return 0;

        return o.getTotalTime();
    }

    @Override
    public List<String> getOrderTasks(String idRest, String orderID){
        List<String> tasks = new ArrayList<>();
        
        OrderKey key = new OrderKey(orderID, idRest);
        Order o = this.orderDAO.get(key);


        if (o != null) {
            tasks = o.getTasksString();
        }

        return tasks;
    }

    @Override 
    public List<String> getPendingOrders(String restId) {    
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : this.orderDAO.values()) {
            if (order.getRestId().equals(restId) && order.getState().equals("EM PREPARAÇÃO")) {
                filteredOrders.add(order);
            }
        }


        filteredOrders.sort(Comparator.comparing(Order::getExpectedTime));


        List<String> result = new ArrayList<>();
        for (Order order : filteredOrders) {
            result.add("[Order #" + order.getOrderId() + "] : " + order.getExpectedTime().toString());
        }

        return result;
    }

    @Override
    public String getOrderResume(String orderID, String idRest) {
        OrderKey key = new OrderKey(orderID, idRest);
        Order o = this.orderDAO.get(key);

        if (o == null) {
            System.err.println("Pedido " + orderID + " não encontrado no restaurante " + idRest);
            return null;
        }

        return o.toString();
    }



    @Override
    public void addOrder(Order o, String idRest) {
        OrderKey key = new OrderKey(o.getOrderId(), idRest);
        
        this.orderDAO.put(key, o);

        System.out.println("Pedido " + o.getOrderId() + " guardado na BD do restaurante " + idRest);
    }

    @Override
    public void delayOrder(String orderID, String idRest, LocalDateTime delay) {
        OrderKey key = new OrderKey(orderID, idRest);
        Order o = this.orderDAO.get(key);

        if (o == null) {
            System.err.println("Erro: Pedido não encontrado para atrasar.");
            return;
        }

        o.delayOrder(delay);
        

        this.orderDAO.put(key, o);
        System.out.println("Atraso registado no pedido " + orderID);
    }

    @Override
    public void changeOrderState(String orderID, String idRest, String newState) {
        OrderKey key = new OrderKey(orderID, idRest);
        Order o = this.orderDAO.get(key);
        
        if (o == null) {
            System.err.println("Erro: Pedido não encontrado para mudar estado.");
            return;
        }

        o.changeOrderState(newState);
        
        this.orderDAO.put(key, o);
        System.out.println("Estado do pedido " + orderID + " alterado para: " + newState);
    }

    @Override
    public boolean changeTaskState(String orderID, String idRest, String taskID) {
        OrderKey key = new OrderKey(orderID, idRest);
        Order o = this.orderDAO.get(key);
        
        if (o == null) {
            System.err.println("Erro: Pedido não encontrado para completar tarefa.");
            return false;
        }
        

        boolean allDone = o.finishTask(taskID);
        

        this.orderDAO.put(key, o);
        
        return allDone;
    }   
}