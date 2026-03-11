package app.dss.cadeiaLN.subsistemaFuncionarios;

import java.util.List;

public class Coo extends Funcionario {
    public Coo(String number, String name, String email, String phone_number) {
        super(number, name, email, phone_number);
    }

    public Coo(String number, String name, String email, String phone_number, String password, List<Message> sent, List<Message> received){
        super(number, name, email, phone_number, password, sent, received);
    }
    
    @Override
    public String getRestaurantId() { return "SEDE"; } 
    @Override
    public String getRole() { return "COO"; } 
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getBaseInfoString()); 
        sb.append(String.format("Cargo:          %s\n", this.getRole()));
        sb.append(String.format("Restaurante ID: %s\n", "SEDE"));
        sb.append("--------------------------------------\n");
        sb.append(String.format("Email:          %s\n", this.getEmail()));
        sb.append(String.format("Telefone:       %s\n", this.getPhoneNumber()));
        sb.append("======================================\n");
        return sb.toString();
    }
}