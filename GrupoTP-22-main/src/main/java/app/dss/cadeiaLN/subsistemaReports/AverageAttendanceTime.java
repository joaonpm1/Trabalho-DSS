package app.dss.cadeiaLN.subsistemaReports;


import java.util.Locale;

public class AverageAttendanceTime {
    
    private long totalSecondsSum; 
    private int totalOrders;      


    public AverageAttendanceTime() {
        this.totalSecondsSum = 0L; 
        this.totalOrders = 0;
    }

    public void incrementValues(long duration, int orders){
        this.totalSecondsSum += duration;
        this.totalOrders += orders;
    }

    public void updateTime(long orderDurationInSeconds) {
        this.totalSecondsSum += orderDurationInSeconds;
        this.totalOrders++;
    }


    public long getTotalTime(){
        return this.totalSecondsSum;
    }

    public int getTotalOrders(){
        return this.totalOrders;
    }

    @Override
    public AverageAttendanceTime clone(){
        AverageAttendanceTime a = new AverageAttendanceTime();
        a.totalOrders = this.totalOrders;
        a.totalSecondsSum = this.totalSecondsSum;
        return a;
    }


    @Override
    public String toString() {
        if (this.totalOrders == 0) {
            return "  \n-> Tempo Médio: N/A (0 Pedidos)";
        }

        // 1. Calcular a média em segundos
        long avgSeconds = this.totalSecondsSum / this.totalOrders;

        // 2. Converter segundos para "Xm Ys" (ex: 155s -> 2m 35s)
        long minutes = avgSeconds / 60;
        long seconds = avgSeconds % 60;
        String formattedAvg = String.format("%dm %02ds", minutes, seconds);

        // 3. Formatar a string final (alinhada com a classe Sales)
        return String.format(Locale.US, "  -> %-10s %-8s (Total de Pedidos: %3d)",
                                 "Tempo Médio:",
                                 formattedAvg,
                                 this.totalOrders);
    }
}