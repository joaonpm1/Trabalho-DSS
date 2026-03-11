package app.dss.cadeiaLN.subsistemaPedidos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    private String idTask;
    private String description;
    private boolean isCompleted;
    private String role;
    private LocalDateTime expcTime; 

    public Task(String id, String description, String role, LocalDateTime expcTime) {
        this.idTask = id;
        this.description = description;
        this.role = role;
        this.expcTime = expcTime;
        this.isCompleted = false;
    }
    
    public Task(String id, String description, boolean isCompleted, String role, LocalDateTime expcTime) {
        this.idTask = id;
        this.description = description;
        this.isCompleted = isCompleted;
        this.role = role;
        this.expcTime = expcTime;
    }

    
    public String getTaskId() { return idTask; }
    
    public String getId() { return idTask; } 

    public boolean isCompleted() { return isCompleted; }
    
    public String getDescription() { return description; }
    
    public String getRole() { return role; }
    
    public LocalDateTime getExpcTime() { return expcTime; }

    public LocalDateTime getDeadline() { 
        return this.expcTime; 
    }


    public void completeTask() {
        this.isCompleted = true;
    }

    @Override
    public String toString() {
        String status = isCompleted ? "[X]" : "[ ]";
        String timeStr = (expcTime != null) ? expcTime.format(DateTimeFormatter.ofPattern("HH:mm")) : "N/A";
        return String.format("%s %s - %s (%s) até %s", status, idTask, description, role, timeStr);
    }
}