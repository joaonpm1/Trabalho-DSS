package app.dss.cadeiaLN.subsistemaFuncionarios;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Message {
    private String numSender;
    private String numReceiver; 
    private String text;
    private LocalDate date;

    public Message(String sender, String receiver, String text, LocalDate date) {
        this.numSender = sender;
        this.numReceiver = receiver;
        this.text = text;
        this.date = date;
    }

    public LocalDate getDate() {
        return this.date;
    }
    
    public String getNumSender() {
        return this.numSender;
    }

    // NOVO GETTER
    public String getNumReceiver() {
        return this.numReceiver;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String dataFormatada = this.date.format(formatter);

        return String.format("[%s] De: %s Para: %s - %s", 
                                 dataFormatada, 
                                 this.numSender, 
                                 this.numReceiver,
                                 this.text);
    }
}