package app.dss.cadeiaLN.subsistemaFuncionarios;

import app.dss.cadeiaDL.FuncionarioDAO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GestFuncionariosFacade implements IGestFuncionarios {
    private static int nextFuncNumber = 1;
    private FuncionarioDAO funcionarios;

    public GestFuncionariosFacade() {
        this.funcionarios = new FuncionarioDAO();
        nextFuncNumber = this.funcionarios.size() + 1;
    }

    @Override
    public String consultFuncionario(String number) {
        Funcionario f = this.funcionarios.get(number);
        return (f != null) ? f.toString() : "Funcionário não existe";
    }

    @Override
    public String getRole(String idFunc) {
        Funcionario f = this.funcionarios.get(idFunc);
        return (f == null) ? null : f.getRole();
    }

    @Override
    public String getRestId(String idFunc) {
        Funcionario f = this.funcionarios.get(idFunc);
        return (f == null) ? null : f.getRestaurantId();
    }

    @Override
    public List<String> listEmployees(String idRestFilter) {
        List<String> result = new ArrayList<>();
        
        for (Funcionario f : this.funcionarios.values()) {

            if (idRestFilter == null || 
               (f.getRestaurantId() != null && f.getRestaurantId().equals(idRestFilter))) {

                String linha = String.format("[%s] %s : %s", 
                    f.getNumber(), 
                    f.getName(), 
                    (f.getRestaurantId() != null ? f.getRestaurantId() : "N/A"));

                result.add(linha);
            }
        }
        return result;
    }

    @Override
    public void addFuncionario(String name, String email, String phone_number, String role, String restaurantId) {
        
        String newId = String.valueOf(nextFuncNumber);
        nextFuncNumber++;

        Funcionario newFunc;

        switch (role.toLowerCase()) {
            case "coo":
                String cooID = "M";
                newId = cooID.concat(newId);
                newFunc = new Coo(newId, name, email, phone_number);
                break;
            
            case "chefe":
                String bossID = "C";
                newId = bossID.concat(newId);
                newFunc = new Chefe(newId, name, email, phone_number, restaurantId);
                break;

            default:
                String operatorID = "O";
                newId = operatorID.concat(newId);
                newFunc = new Operador(newId, name, email, phone_number, restaurantId, role);
                break;
        }

        this.funcionarios.put(newId, newFunc);
        System.out.println("Funcionário adicionado e persistido: \n" + newFunc.toString());
    }

    @Override
    public void removeFuncionario(String number) {
        if (this.funcionarios.containsKey(number)) {
            this.funcionarios.remove(number);
            System.out.println("Funcionário " + number + " removido da Base de Dados.");
        } else {
            System.err.println("Número mecanográfico " + number + " não existe");
        }
    }

    @Override
    public void relocateFuncionario(String number, String newRole) {
        Funcionario f = this.funcionarios.get(number);
        
        if (f != null) {
            if (f instanceof Operador && !(newRole.equalsIgnoreCase("coo") || newRole.equalsIgnoreCase("chefe"))) {
                ((Operador) f).changeRole(newRole);
                
                this.funcionarios.put(number, f);
                System.out.println("Posto do Operador " + number + " atualizado para " + newRole);
            } 
            else {
                String oldName = f.getName();
                String oldEmail = f.getEmail();
                String oldPhone = f.getPhoneNumber();
                String idRest = f.getRestaurantId();

                this.funcionarios.remove(number);
                
                addFuncionario(oldName, oldEmail, oldPhone, newRole, idRest);
                System.out.println("Função do Funcionário " + number + " alterada (com novo ID) para " + newRole);
            }
        } else {
            System.err.println("Número mecanográfico " + number + " não existe");
        }
    }

    @Override
    public void contactRestaurant(String idFunc, String idRest, String text) {
        Funcionario sender = this.funcionarios.get(idFunc);

        if (sender == null) {
            System.err.println("Número mecanográfico do sender" + idFunc + " não existe");
            return;
        }

        for (Funcionario receiver : this.funcionarios.values()) {

            if (receiver.getRestaurantId() != null 
                && receiver.getRestaurantId().equals(idRest) 
                && !receiver.getNumber().equals(idFunc)) {
                
                Message msg = new Message(sender.getNumber(), receiver.getNumber(), text, LocalDate.now());
                
                this.funcionarios.sendMessage(msg);
                
                System.out.println("Mensagem enviada para: " + receiver.getNumber());
            }
        }
        System.out.println("Envio de mensagem em massa registado por: " + sender.getNumber());
    }

    @Override
    public void sendMessage(String numFunc, String numDest, String text) {
        if (!this.funcionarios.containsKey(numFunc)) {
            System.err.println("Remetente " + numFunc + " não existe");
            return;
        }
        if (!this.funcionarios.containsKey(numDest)) {
            System.err.println("Destinatário " + numDest + " não existe");
            return;
        }

        Message msg = new Message(numFunc, numDest, text, LocalDate.now());
        
        this.funcionarios.sendMessage(msg);

        System.out.println("Mensagem persistida: De " + numFunc + " para " + numDest);
    }

    @Override
    public Set<String> getSentMessages(String numFunc) {
        Funcionario f = this.funcionarios.get(numFunc);
        if (f != null) {
            return f.getSentMessages();
        } else {
            System.err.println("Número mecanográfico " + numFunc + " não existe");
            return null;
        }
    }

    @Override
    public Set<String> getReceivedMessages(String numFunc) {
        Funcionario f = this.funcionarios.get(numFunc);
        if (f != null) {
            return f.getReceivedMessages();
        } else {
            System.err.println("Número mecanográfico " + numFunc + " não existe");
            return null;
        }
    }

    @Override
    public Boolean login(String numFunc, String password) {
        Funcionario f = this.funcionarios.get(numFunc);
        return (f != null) ? f.verifyPassword(password) : false;
    }

    @Override 
    public Boolean changePassword(String numFunc, String newPassword) {
        Funcionario f = this.funcionarios.get(numFunc);
        
        if (f != null) {
            boolean mudou = f.changePassword(newPassword);
            if (mudou) {
                this.funcionarios.put(numFunc, f);
            }
            return mudou;
        }
        return false;
    }
}