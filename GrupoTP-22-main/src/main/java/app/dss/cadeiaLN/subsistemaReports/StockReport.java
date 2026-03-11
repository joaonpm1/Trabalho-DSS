package app.dss.cadeiaLN.subsistemaReports;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class StockReport {
    private Map<String, Integer> soldIngredients;

    public StockReport() {
        this.soldIngredients = new HashMap<>();
    }

    public StockReport(Map<String, Integer> data) {
        this.soldIngredients = (data != null) ? new HashMap<>(data) : new HashMap<>();
    }

    public Map<String, Integer> getStockReport() {
        return new HashMap<>(this.soldIngredients);
    }

    public void updateStockReport(Map<String, Integer> items) {
        if (items == null) {
            return;
        }
        // Merge soma as quantidades novas às existentes
        for (Map.Entry<String, Integer> itemEntry : items.entrySet()) {
            String ingredientName = itemEntry.getKey();
            int quantitySold = itemEntry.getValue();

            this.soldIngredients.merge(ingredientName, quantitySold, 
                                       (oldValue, newValue) -> oldValue + newValue);
        }
    }

    @Override
    public StockReport clone() {
        return new StockReport(this.getStockReport());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  \n-> Relatório de Stock (Items Vendidos):\n");

        if (this.soldIngredients.isEmpty()) {
            sb.append("      (Nenhum ingrediente vendido)\n");
        } else {
            for (Map.Entry<String, Integer> entry : this.soldIngredients.entrySet()) {
                sb.append(String.format(Locale.US, "      - %-25s [Vendido: %3d]\n", 
                                          entry.getKey() + ":", 
                                          entry.getValue()));
            }
        }
        return sb.toString().trim(); 
    }
}