package app.dss.cadeiaLN.subsistemaReports;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class DailyReport {
    private String idRest;
    private LocalDate day;

    private Sales salesReport;
    private StockReport stockReport;
    private AverageAttendanceTime averageAttendanceTime;

    public DailyReport(String idRestaurant, LocalDate date) {
        this.idRest = idRestaurant;
        this.day = date;

        this.salesReport = new Sales();
        this.stockReport = new StockReport();
        this.averageAttendanceTime = new AverageAttendanceTime();
    }

    public String getRestId() { return this.idRest; }
    public LocalDate getDate() { return this.day; }
    
    public float getProfit() { return this.salesReport.getProfit(); }
    public int getQuantity() { return this.salesReport.getQuantity(); }
    public int getAverageAttendanceTime() { return (int) this.averageAttendanceTime.getTotalTime(); }
    public int getTotalOrders() { return this.averageAttendanceTime.getTotalOrders(); }

    public Map<String,Integer> getItemSales(){
        return this.salesReport.getItemSalesMap();
    }

    public void updateSales(float price, int quantity, Map<String,Integer> items_sales) {
        this.salesReport.incrementValues(price, quantity, items_sales);
    }

    public void setAverageAttendanceTime(int totalSeconds, int totalOrders) {
        this.averageAttendanceTime = new AverageAttendanceTime(); 
        this.averageAttendanceTime.incrementValues((long) totalSeconds, totalOrders);
    }

    public void updateAverageAttendenceTime(long duration) {
        this.averageAttendanceTime.updateTime(duration);
    }

    public void updateStockReport(Map<String, Integer> items) {
        this.stockReport.updateStockReport(items);
    }

    public void setStockReportMap(Map<String, Integer> map) {
        this.stockReport = new StockReport(map);
    }

    public void setSalesReportMap(Map<String, Integer> map){
        this.salesReport.setSalesMap(map);
    }
    
    public void setSalesData(float profit, int quantity) {
        this.salesReport = new Sales();
        this.salesReport.setProfit(profit);
        this.salesReport.setQuantity(quantity);
    }

    public Sales getSalesReport() { return this.salesReport.clone(); }
    public AverageAttendanceTime getAttendanceReport() { return this.averageAttendanceTime.clone(); }
    
    public Map<String, Integer> getStockMap() { 
        return this.stockReport.getStockReport(); 
    }
    
    public StockReport getStockReport() { return this.stockReport.clone(); }

    @Override
    public String toString() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = (this.day != null) ? this.day.format(dateFormatter) : "N/A";

        StringBuilder sb = new StringBuilder();
        sb.append("\n=============================================\n");
        sb.append("         RELATÓRIO DIÁRIO DE ATIVIDADE\n");
        sb.append("=============================================\n");
        sb.append(String.format(" Restaurante: %s\n", this.idRest));
        sb.append(String.format(" Data:          %s\n", formattedDate));
        sb.append("---------------------------------------------\n");
        
        sb.append(this.salesReport.toString()).append("\n");
        sb.append(this.averageAttendanceTime.toString()).append("\n");
        sb.append("---------------------------------------------\n");
        sb.append(this.stockReport.toString()).append("\n");
        
        sb.append("=============================================\n");
        
        return sb.toString();
    }
}