package app.dss.cadeiaLN.subsistemaReports;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface IGestReports {

    String getDailySalesReport(String idRest, LocalDate date);
   
    String getDailyStockReport(String idRest, LocalDate date);

    String getDailyAttendenceReport(String idRest, LocalDate date);

    String getSalesReport(String idRest, LocalDate start, LocalDate end);

    String getStockReport(String idRest, LocalDate start, LocalDate end);

    String getAttendenceReport(String idRest, LocalDate start, LocalDate end);

    String getChainSalesReport(LocalDate start, LocalDate end);

    String getChainStockReport(LocalDate start, LocalDate end);

    String getChainAttendenceReport(LocalDate start, LocalDate end);

    public void updateSalesReport(String idRest, LocalDate date, int quantity, float profit, Map<String,Integer> sales);

    void updateStockReport(String idRest, LocalDate date, Map<String, Integer> items);

    void updateAttendenceReport(String idRest, LocalDate date, long time);

    public List<Map.Entry<String, Integer>> getTopNItemSales(String idRest, LocalDate inicio, LocalDate fim, int n);

}