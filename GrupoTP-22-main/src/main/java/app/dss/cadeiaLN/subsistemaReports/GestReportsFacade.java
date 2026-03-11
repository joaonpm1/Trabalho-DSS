package app.dss.cadeiaLN.subsistemaReports;

import app.dss.cadeiaDL.DailyReportDAO;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GestReportsFacade implements IGestReports {
    private DailyReportDAO reportDAO;

    public GestReportsFacade() {
        this.reportDAO = new DailyReportDAO();
    }

    @Override
    public String getDailySalesReport(String idRest, LocalDate date) {
        DailyReportDAO.DailyReportKey key = new DailyReportDAO.DailyReportKey(idRest, date);
        
        DailyReport r = this.reportDAO.get(key);

        if (r == null) {
            System.err.println("Não existem dados para o restaurante " + idRest + " na data " + date);
            return null;
        }

        return r.getSalesReport().toString();
    }

    @Override
    public String getDailyStockReport(String idRest, LocalDate date) {
        DailyReportDAO.DailyReportKey key = new DailyReportDAO.DailyReportKey(idRest, date);
        DailyReport r = this.reportDAO.get(key);

        if (r == null) {
            System.err.println("Não existem dados para o restaurante " + idRest + " na data " + date);
            return null;
        }

        return r.getStockReport().toString();
    }

    @Override
    public String getDailyAttendenceReport(String idRest, LocalDate date) {
        DailyReportDAO.DailyReportKey key = new DailyReportDAO.DailyReportKey(idRest, date);
        DailyReport r = this.reportDAO.get(key);

        if (r == null) {
            System.err.println("Não existem dados para o restaurante " + idRest + " na data " + date);
            return null;
        }

        return r.getAttendanceReport().toString();
    }
    
    @Override
    public String getSalesReport(String idRest, LocalDate start, LocalDate end) {
        List<DailyReport> reports = this.reportDAO.getReportsBetween(idRest, start, end);
        
        if (reports.isEmpty()) {
            return new Sales().toString(); 
        }
        
        Sales result = new Sales();
        for (DailyReport r : reports) {
            Sales s = r.getSalesReport();
            result.incrementValues(s.getProfit(), s.getQuantity(), s.getItemSalesMap());
        }

        return result.toString();
    }

    @Override
    public String getStockReport(String idRest, LocalDate start, LocalDate end) {
        List<DailyReport> reports = this.reportDAO.getReportsBetween(idRest, start, end);
        
        StockReport result = new StockReport();
        for (DailyReport r : reports) {
            StockReport s = r.getStockReport();
            result.updateStockReport(s.getStockReport());
        }

        return result.toString();
    }

    @Override
    public String getAttendenceReport(String idRest, LocalDate start, LocalDate end) {
        List<DailyReport> reports = this.reportDAO.getReportsBetween(idRest, start, end);
        
        AverageAttendanceTime result = new AverageAttendanceTime();
        for (DailyReport r : reports) {
            AverageAttendanceTime a = r.getAttendanceReport();
            result.incrementValues(a.getTotalTime(), a.getTotalOrders());
        }

        return result.toString();
    }

    @Override
    public String getChainSalesReport(LocalDate start, LocalDate end) {
        List<DailyReport> reports = this.reportDAO.getAllReportsBetween(start, end);

        Sales result = new Sales();
        for (DailyReport r : reports) {
            Sales s = r.getSalesReport();
            result.incrementValues(s.getProfit(), s.getQuantity(), s.getItemSalesMap());
        }
        return result.toString();
    }

    @Override
    public String getChainStockReport(LocalDate start, LocalDate end) {
        List<DailyReport> reports = this.reportDAO.getAllReportsBetween(start, end);

        StockReport result = new StockReport();
        for (DailyReport r : reports) {
            StockReport s = r.getStockReport();
            result.updateStockReport(s.getStockReport());
        }
        return result.toString();
    }

    @Override
    public String getChainAttendenceReport(LocalDate start, LocalDate end) {
        List<DailyReport> reports = this.reportDAO.getAllReportsBetween(start, end);

        AverageAttendanceTime result = new AverageAttendanceTime();
        for (DailyReport r : reports) {
            AverageAttendanceTime a = r.getAttendanceReport();
            result.incrementValues(a.getTotalTime(), a.getTotalOrders());
        }
        return result.toString();
    }

    private DailyReport getOrCreate(String idRest, LocalDate date) {
        DailyReportDAO.DailyReportKey key = new DailyReportDAO.DailyReportKey(idRest, date);
        DailyReport r = this.reportDAO.get(key);
        
        if (r == null) {
            r = new DailyReport(idRest, date);
        }
        return r;
    }

    @Override
    public List<Map.Entry<String, Integer>> getTopNItemSales(String idRest, LocalDate inicio, LocalDate fim, int n) {
        List<DailyReport> reports;

        if (idRest == null) {
            reports = this.reportDAO.getAllReportsBetween(inicio, fim);
        } else {
            reports = this.reportDAO.getReportsBetween(idRest, inicio, fim);
        }

        Sales totalSales = new Sales();

        for (DailyReport r : reports) {
            Sales s = r.getSalesReport();
            
            totalSales.incrementValues(s.getProfit(), s.getQuantity(), s.getItemSalesMap());
        }

        return totalSales.getTopNSales(n);
    }

    @Override
    public void updateSalesReport(String idRest, LocalDate date, int quantity, float profit, Map<String,Integer> sales) {
        DailyReport r = getOrCreate(idRest, date);
        
        r.updateSales(profit, quantity, sales);
        
        DailyReportDAO.DailyReportKey key = new DailyReportDAO.DailyReportKey(idRest, date);
        this.reportDAO.put(key, r);
    }

    @Override
    public void updateStockReport(String idRest, LocalDate date, Map<String, Integer> items) {
        DailyReport r = getOrCreate(idRest, date);
        
        r.updateStockReport(items);
        
        DailyReportDAO.DailyReportKey key = new DailyReportDAO.DailyReportKey(idRest, date);
        this.reportDAO.put(key, r);
    }

    @Override
    public void updateAttendenceReport(String idRest, LocalDate date, long time) {
        DailyReport r = getOrCreate(idRest, date);
        
        r.updateAverageAttendenceTime(time);
        
        DailyReportDAO.DailyReportKey key = new DailyReportDAO.DailyReportKey(idRest, date);
        this.reportDAO.put(key, r);
    }
}