package app.dss.cadeiaDL;

import app.dss.cadeiaLN.subsistemaReports.DailyReport;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DailyReportDAO extends AbstractDAO<DailyReportDAO.DailyReportKey, DailyReport> {

    public DailyReportDAO() {
        super("dailyReport");
    }

    public static class DailyReportKey {
        private String idRest;
        private LocalDate date;

        public DailyReportKey(String idRest, LocalDate date) {
            this.idRest = idRest;
            this.date = date;
        }
        public String getIdRest() { return idRest; }
        public LocalDate getDate() { return date; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DailyReportKey that = (DailyReportKey) o;
            return Objects.equals(idRest, that.idRest) && Objects.equals(date, that.date);
        }
        @Override
        public int hashCode() { return Objects.hash(idRest, date); }
    }

    
    @Override protected String getKeyColumnNames() { return "id_Rest = ? AND date = ?"; }

    @Override protected void setKeyParameters(PreparedStatement ps, int idx, DailyReportKey key) throws SQLException {
        ps.setString(idx, key.getIdRest());
        ps.setDate(idx + 1, java.sql.Date.valueOf(key.getDate()));
    }

    @Override protected DailyReportKey decodeKey(ResultSet rs) throws SQLException {
        return new DailyReportKey(rs.getString("id_Rest"), rs.getDate("date").toLocalDate());
    }

    @Override
    protected DailyReport decodeValue(ResultSet rs) throws SQLException {
        String idRest = rs.getString("id_Rest");
        LocalDate date = rs.getDate("date").toLocalDate();
        float profit = rs.getFloat("profit");
        int quantity = rs.getInt("quantity");
        int avgTime = rs.getInt("avg_attendance_time");
        int totalOrders = rs.getInt("totalOrders");

        DailyReport r = new DailyReport(idRest, date);
        r.setSalesData(profit, quantity);
        r.setAverageAttendanceTime(avgTime, totalOrders);

        Map<String, Integer> stocks = new HashMap<>();
        String sqlStock = "SELECT ingredient_name, quantity FROM stock_report WHERE report_id_rest = ? AND report_date = ?";
        
        try (PreparedStatement ps = connection.prepareStatement(sqlStock)) {
            ps.setString(1, idRest);
            ps.setDate(2, java.sql.Date.valueOf(date));
            
            try (ResultSet rsStock = ps.executeQuery()) {
                while (rsStock.next()) {
                    stocks.put(
                        rsStock.getString("ingredient_name"), 
                        rsStock.getInt("quantity")
                    );
                }
            }
        }
        r.setStockReportMap(stocks);

        Map<String,Integer> item_sales = new HashMap<>();
        String sqlSales = "SELECT item_name, quant_sold FROM top_sales WHERE dailyReport_id_Rest = ? AND dailyReport_date = ?";
        try(PreparedStatement ps = connection.prepareStatement(sqlSales)){
            ps.setString(1,idRest);
            ps.setDate(2, java.sql.Date.valueOf(date));

            try(ResultSet rsSales = ps.executeQuery()){
                while(rsSales.next()){
                    item_sales.put(
                        rsSales.getString("item_name"),
                        rsSales.getInt("quant_sold")
                    );
                }
            }
        }
        r.setSalesReportMap(item_sales);

        return r;
    }


    @Override protected String getInsertSql() {
        return "INSERT INTO dailyReport (id_Rest, date, profit, quantity, avg_attendance_time, totalOrders) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override protected void setInsertParameters(PreparedStatement ps, DailyReport v) throws SQLException {
        ps.setString(1, v.getRestId());
        ps.setDate(2, java.sql.Date.valueOf(v.getDate()));
        ps.setFloat(3, v.getProfit());
        ps.setInt(4, v.getQuantity());
        ps.setInt(5, v.getAverageAttendanceTime());
        ps.setInt(6, v.getTotalOrders());
    }

    @Override protected String getUpdateSql() {
        return "UPDATE dailyReport SET profit=?, quantity=?, avg_attendance_time=?, totalOrders=? WHERE id_Rest=? AND date=?";
    }

    @Override protected void setUpdateParameters(PreparedStatement ps, DailyReport v) throws SQLException {
        ps.setFloat(1, v.getProfit());
        ps.setInt(2, v.getQuantity());
        ps.setInt(3, v.getAverageAttendanceTime());
        ps.setInt(4, v.getTotalOrders());
        ps.setString(5, v.getRestId());
        ps.setDate(6, java.sql.Date.valueOf(v.getDate()));
    }


    @Override
    public DailyReport put(DailyReportKey key, DailyReport value) {
        super.put(key, value); 
        saveStocks(value);
        saveTopSales(value);    
        return value;
    }

    private void saveStocks(DailyReport report) {
        try {
            String delSql = "DELETE FROM stock_report WHERE report_id_rest = ? AND report_date = ?";
            try (PreparedStatement ps = connection.prepareStatement(delSql)) {
                ps.setString(1, report.getRestId());
                ps.setDate(2, java.sql.Date.valueOf(report.getDate()));
                ps.executeUpdate();
            }

            Map<String, Integer> stocks = report.getStockMap(); 
            
            if (stocks != null && !stocks.isEmpty()) {
                String insSql = "INSERT INTO stock_report (report_id_rest, report_date, ingredient_name, quantity) VALUES (?, ?, ?, ?)";
                try (PreparedStatement ps = connection.prepareStatement(insSql)) {
                    for (Map.Entry<String, Integer> entry : stocks.entrySet()) {
                        ps.setString(1, report.getRestId());
                        ps.setDate(2, java.sql.Date.valueOf(report.getDate()));
                        ps.setString(3, entry.getKey());
                        ps.setInt(4, entry.getValue());
                        ps.addBatch();
                    }
                    ps.executeBatch(); 
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gravar stock report", e);
        }
    }

    private void saveTopSales(DailyReport report) {
        try {
            String delStr = "DELETE FROM top_sales WHERE dailyReport_id_Rest = ? AND dailyReport_date = ?";

            try (PreparedStatement ps = connection.prepareStatement(delStr)) {
                ps.setString(1, report.getRestId());
                ps.setDate(2, java.sql.Date.valueOf(report.getDate()));
                ps.executeUpdate();
            }

            Map<String, Integer> item_sales = report.getItemSales();

            if (item_sales != null && !item_sales.isEmpty()) {
                String ins = "INSERT INTO top_sales (dailyReport_id_Rest, dailyReport_date, item_name, quant_sold) VALUES (?, ?, ?, ?)";

                try (PreparedStatement ps = connection.prepareStatement(ins)) {
                    for (Map.Entry<String, Integer> entry : item_sales.entrySet()) {
                        ps.setString(1, report.getRestId());
                        ps.setDate(2, java.sql.Date.valueOf(report.getDate()));
                        ps.setString(3, entry.getKey());
                        ps.setInt(4, entry.getValue());

                        ps.addBatch(); 
                    }
                    ps.executeBatch(); 
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gravar top sales", e);
        }
    }

    public List<DailyReport> getReportsBetween(String idRest, LocalDate start, LocalDate end) {
        List<DailyReport> lista = new ArrayList<>();
        String sql = "SELECT * FROM dailyReport WHERE id_Rest = ? AND date BETWEEN ? AND ?";
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            ps.setString(1, idRest);
            ps.setDate(2, java.sql.Date.valueOf(start));
            ps.setDate(3, java.sql.Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(decodeValue(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }
    
    public List<DailyReport> getAllReportsBetween(LocalDate start, LocalDate end) {
        List<DailyReport> lista = new ArrayList<>();
        String sql = "SELECT * FROM dailyReport WHERE date BETWEEN ? AND ?";
        try (PreparedStatement ps = this.connection.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(start));
            ps.setDate(2, java.sql.Date.valueOf(end));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(decodeValue(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return lista;
    }
}