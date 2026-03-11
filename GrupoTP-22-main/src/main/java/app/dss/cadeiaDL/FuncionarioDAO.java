package app.dss.cadeiaDL;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import app.dss.cadeiaLN.subsistemaFuncionarios.Chefe;
import app.dss.cadeiaLN.subsistemaFuncionarios.Coo;
import app.dss.cadeiaLN.subsistemaFuncionarios.Operador;
import app.dss.cadeiaLN.subsistemaFuncionarios.Funcionario;

import app.dss.cadeiaLN.subsistemaFuncionarios.Message;

public class FuncionarioDAO extends AbstractDAO<String, Funcionario>{

    public FuncionarioDAO(){
        super("funcionario");
    }

    @Override
    protected String getKeyColumnNames(){
        return "number = ?";
    }

    @Override
    protected void setKeyParameters(PreparedStatement ps, int idx, String key) throws SQLException{
        ps.setString(idx, key);
    }

    @Override 
    protected  String decodeKey(ResultSet rs) throws SQLException{
        return new String(rs.getString("number"));
    }

@Override
    protected Funcionario decodeValue(ResultSet rs) throws SQLException {
        String number = rs.getString("number");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone_number");
        String password = rs.getString("password");
        String role = rs.getString("role");
        String idRest = rs.getString("restaurant_idRest");
        
        List<Message> sentMessages = new ArrayList<>();
        List<Message> receivedMessages = new ArrayList<>();

 
        String sndSqlQuery = "SELECT * FROM message WHERE num_sender = ?";
        
        try (PreparedStatement sndPs = this.connection.prepareStatement(sndSqlQuery)) {
            sndPs.setString(1, number);
            try (ResultSet sndRs = sndPs.executeQuery()) {
                while (sndRs.next()) {
                    // Agora lemos ambos da BD para garantir consistência
                    String sender = sndRs.getString("num_sender"); 
                    String receiver = sndRs.getString("num_receiver"); 
                    String text = sndRs.getString("text");
                    LocalDate date = sndRs.getDate("date").toLocalDate();
                    
                    sentMessages.add(new Message(sender, receiver, text, date));
                }
            }
        }

        String recvSqlQuery = "SELECT * FROM message WHERE num_receiver = ?";
        try (PreparedStatement rcvPs = this.connection.prepareStatement(recvSqlQuery)) {
            rcvPs.setString(1, number);
            try (ResultSet rcvRs = rcvPs.executeQuery()) {
                while (rcvRs.next()) {
                    String sender = rcvRs.getString("num_sender");
                    String receiver = rcvRs.getString("num_receiver");
                    String text = rcvRs.getString("text");
                    LocalDate date = rcvRs.getDate("date").toLocalDate();
                    
                    receivedMessages.add(new Message(sender, receiver, text, date));
                }
            }
        }

        Funcionario f = null;
        switch (role) {
            case "COO":
                f = new Coo(number, name, email, phone, password, sentMessages, receivedMessages); 
                break; 

            case "Chefe":
                f = new Chefe(number, idRest, name, email, number, password, sentMessages, receivedMessages);
                break;
            
            default:
                f = new Operador(number, idRest, role, name, email, number, password, sentMessages, receivedMessages);
                break;
        }
        
        return f;
    }

    @Override
    protected String getInsertSql() {
        return "INSERT INTO funcionario (number, name, email, phone_number, password, role, restaurant_idRest) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Funcionario value) throws SQLException {
        ps.setString(1, value.getNumber());
        ps.setString(2, value.getName());
        ps.setString(3, value.getEmail());
        ps.setString(4, value.getPhoneNumber());
        ps.setString(5, value.getPassword());
        ps.setString(6, value.getRole());
        
        String restId = value.getRestaurantId(); 
        ps.setString(7, restId != null ? restId : "SEDE");
    }

    @Override
    protected String getUpdateSql() {
        return "UPDATE funcionario SET name=?, email=?, phone_number=?, password=?, role=?, restaurant_idRest=? WHERE number=?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Funcionario value) throws SQLException {
        ps.setString(1, value.getName());
        ps.setString(2, value.getEmail());
        ps.setString(3, value.getPhoneNumber());
        ps.setString(4, value.getPassword());
        ps.setString(5, value.getRole());
        
        String restId = value.getRestaurantId();
        ps.setString(6, restId);

        ps.setString(7, value.getNumber());
    }

    public void sendMessage(Message m) {
        String sql = "INSERT INTO message (text, date, num_sender, num_receiver) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            ps.setString(1, m.getText());
            ps.setDate(2, java.sql.Date.valueOf(m.getDate()));
            ps.setString(3, m.getNumSender()); 
            ps.setString(4, m.getNumReceiver()); 
            
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao enviar mensagem", e);
        }
    }

}