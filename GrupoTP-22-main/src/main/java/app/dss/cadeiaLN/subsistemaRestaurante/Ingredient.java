package app.dss.cadeiaLN.subsistemaRestaurante;


public class Ingredient{
    private String name;
    private int quantity;

    public Ingredient(String name, int quantity){
        this.name = new String(name);
        this.quantity = quantity;
    }

    public Ingredient(Ingredient p){
        this.name = new String(p.name);
        this.quantity = p.quantity;
    }

    public String getName(){ return new String(this.name); }

    public int getQuantity(){ return this.quantity; }

    public void add(int quantity){ this.quantity += quantity; }

    public void remove(int quantity) {this.quantity -= quantity; }

    @Override
    public String toString(){
        return String.format("-> %-30s [Stock: %4d]", this.name, this.quantity);
    }
}