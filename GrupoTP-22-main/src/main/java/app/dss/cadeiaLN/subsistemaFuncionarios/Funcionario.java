package app.dss.cadeiaLN.subsistemaFuncionarios;

import java.util.Set;
import java.util.ArrayList; 
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.List;

public abstract class Funcionario {
    private String number;
    private String name;
    private String email;
    private String phone_number;
    private String password;

    private ArrayList<Message> sentMessages;
    private ArrayList<Message> receivedMessages;

    public Funcionario(String number, String name, String email, String phone_number) {
        this.number = number; 
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = generateInicialPassword();
        
        this.sentMessages = new ArrayList<>();
        this.receivedMessages = new ArrayList<>();
    }

    public Funcionario(String number, String name, String email, String phone_number, String password, List<Message> sent, List<Message> received) {
        this.number = number;
        this.name = name;
        this.email = email;
        this.phone_number = phone_number;
        this.password = (password != null) ? password : generateInicialPassword();
        
        this.sentMessages = sent != null ? new ArrayList<>(sent) : new ArrayList<>();
        this.receivedMessages = received != null ? new ArrayList<>(received) : new ArrayList<>();
    }

    private String generateInicialPassword() {
        return this.number;
    }

    public String getNumber() { return this.number; }
    public String getName() { return this.name; }
    public String getEmail() { return this.email; }
    public String getPhoneNumber() { return this.phone_number; }
    public String getPassword() { return this.password; }

    public Boolean changePassword(String newPassword) {
        if (this.password.equals(newPassword)) {
            return false;
        }
        this.password = newPassword;
        return true;
    }

    public Boolean verifyPassword(String password) {
        return this.password.equals(password);
    }

    public void addReceivedMessage(String text, String senderId) {
        Message new_message = new Message(senderId, this.number, text, LocalDate.now());
        this.receivedMessages.add(new_message);
    }

    public void addSentMessage(String text, String receiverId) {
        Message new_message = new Message(this.number, receiverId, text, LocalDate.now());
        this.sentMessages.add(new_message);
    }

    public Set<String> getReceivedMessages() {
        return this.receivedMessages.stream().map(Message::toString).collect(Collectors.toSet());
    }

    public List<Message> getReceivedMsgList() {
        return new ArrayList<>(this.receivedMessages);
    }

    public List<Message> getSentMsgList() {
        return new ArrayList<>(this.sentMessages);
    }

    public Set<String> getSentMessages() {
        return this.sentMessages.stream().map(Message::toString).collect(Collectors.toSet());
    }

    @Override
    public abstract String toString();
    public abstract String getRole();
    public abstract String getRestaurantId();
    
    protected String getBaseInfoString() {
        StringBuilder sb = new StringBuilder();
        sb.append("======================================\n");
        sb.append("       FICHA DE FUNCIONÁRIO\n");
        sb.append("======================================\n");
        sb.append(String.format("Nome:           %s\n", this.name));
        sb.append(String.format("Nº Mecan.:      %s\n", this.number));
        return sb.toString();
    }
}