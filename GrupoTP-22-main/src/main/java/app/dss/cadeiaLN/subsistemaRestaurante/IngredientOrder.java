package app.dss.cadeiaLN.subsistemaRestaurante;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IngredientOrder{
    private int orderId;
    private Ingredient product;
    private String state;
    private LocalDateTime orderDate;
    private LocalDateTime expectedArrival;

    public IngredientOrder(int id, Ingredient p, LocalDateTime ordDate, LocalDateTime expDate){
        this.orderId = id;
        this.product = new Ingredient(p);
        this.state = "EM TRANSITO";
        this.orderDate = ordDate;
        this.expectedArrival = expDate; 
    }

    public int getOrderId() { return this.orderId; }

    public int getQuantity() {return this.product.getQuantity(); }

    public LocalDateTime getOrderDate() { return this.orderDate; }

    public String getState() {return new String(this.state); }

    public void finishOrder(){ this.state = "RECECIONADA";}

    public String getIngredientName() {return this.product.getName(); }

    public LocalDateTime getExpectedArrivalDate() {return this.expectedArrival; }

    public void setState(String s) { this.state = new String(s); }

    @Override
    public String toString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        String dataPedido = (this.orderDate != null) ? this.orderDate.format(formatter) : "N/A";
        String dataPrevista = (this.expectedArrival != null) ? this.expectedArrival.format(formatter) : "N/A";

        return String.format("[ID: %d (%s)] (Pedido: %s, Prev: %s) - %dx %s",
                this.orderId,
                this.state,
                dataPedido,
                dataPrevista,
                this.product.getQuantity(),
                this.product.getName());
    }
}