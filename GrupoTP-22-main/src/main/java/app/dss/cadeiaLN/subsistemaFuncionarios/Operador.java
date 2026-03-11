package app.dss.cadeiaLN.subsistemaFuncionarios;

import java.util.List;

public class Operador extends Funcionario {
    private String restaurantId;
    private String role; 
    public Operador(String number, String name, String email, String phone_number, String restaurantId, String role) {
        super(number, name, email, phone_number);
        this.restaurantId = restaurantId;
        this.role = role;
    }

    public Operador (String number,String idRest, String role ,String name, String email, String phone_number, String password, List<Message> sent, List<Message> received) {
        super(number, name, email, phone_number, password, sent, received);
        this.restaurantId = new String(idRest);
        this.role = new String(role);
    }
    
    @Override
    public String getRestaurantId() { return this.restaurantId; }
    @Override
    public String getRole() { return this.role; } 

    public void changeRole(String newRole) {this.role = newRole;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getBaseInfoString());
        sb.append(String.format("Cargo:          %s\n", "OPERADOR DE RESTAURANTE")); 
        sb.append(String.format("Função:         %s\n", this.getRole())); 
        sb.append(String.format("Restaurante ID: %s\n", this.getRestaurantId()));
        sb.append("--------------------------------------\n");
        sb.append(String.format("Email:          %s\n", this.getEmail()));
        sb.append(String.format("Telefone:       %s\n", this.getPhoneNumber()));
        sb.append("======================================\n");
        return sb.toString();
    }
}