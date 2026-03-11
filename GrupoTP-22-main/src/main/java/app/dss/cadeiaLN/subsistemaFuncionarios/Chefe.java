package app.dss.cadeiaLN.subsistemaFuncionarios;

import java.util.List;

public class Chefe extends Funcionario{
    private String restaurantId;

    public Chefe(String num, String name, String email, String phone_number, String idRest){
        super(num, name, email, phone_number);
        this.restaurantId = new String(idRest);
    }

    public Chefe (String number,String idRest ,String name, String email, String phone_number, String password, List<Message> sent, List<Message> received) {
        super(number, name, email, phone_number, password, sent, received);
        this.restaurantId = new String (idRest);
    }

    @Override
    public String getRestaurantId(){return new String(this.restaurantId);}
    @Override
    public String getRole(){return "Chefe";}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getBaseInfoString());
        sb.append(String.format("Cargo:          %s\n", "CHEFE DE RESTAURANTE"));
        sb.append(String.format("Restaurante ID: %s\n", this.getRestaurantId()));
        sb.append("--------------------------------------\n");
        sb.append(String.format("Email:          %s\n", super.getEmail()));
        sb.append(String.format("Telefone:       %s\n", super.getPhoneNumber()));
        sb.append("======================================\n");
        return sb.toString();
    }
}