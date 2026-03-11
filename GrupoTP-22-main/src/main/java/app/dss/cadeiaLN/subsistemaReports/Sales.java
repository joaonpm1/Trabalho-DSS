package app.dss.cadeiaLN.subsistemaReports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class Sales{
    private float profit;
    private int quantity;
    private Map<String,Integer> top_item_sales;


    public Sales(){
        this.profit = 0;
        this.quantity = 0;
        this.top_item_sales = new HashMap<>();
    }

    public float getProfit(){
        return this.profit;
    }

    public int getQuantity(){
        return this.quantity;
    }

    public void setProfit(float profit){
        this.profit = profit;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public void setSalesMap(Map<String, Integer> map){
        this.top_item_sales = new HashMap<>(map);
    }

    public Map<String,Integer> getItemSalesMap(){
        return new HashMap<>(this.top_item_sales);
    }


    public List<Entry<String,Integer>> getTopNSales(int N){
        List<Entry<String, Integer>> sortedList = new ArrayList<>(this.top_item_sales.entrySet());

        sortedList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        if (N > sortedList.size()) {
            return sortedList;
        }

        return sortedList.subList(0, N);
    }

    public void incrementValues(float profit, int quantity, Map<String, Integer> items_sales){
        this.profit += profit;
        this.quantity += quantity;

        for(Entry<String,Integer> item_sale: items_sales.entrySet()){
            if(!this.top_item_sales.containsKey(item_sale.getKey())){
                this.top_item_sales.put(item_sale.getKey(), item_sale.getValue());
            } else{
                int q = this.top_item_sales.get(item_sale.getKey());
                q += item_sale.getValue();
                this.top_item_sales.put(item_sale.getKey(),q);
            }
        }
    }

    public void updateProfit(float proft){
        this.profit += proft;
        this.quantity++;
    }

    @Override
    public Sales clone(){
        Sales s = new Sales();
        s.profit = this.profit;
        s.quantity = this.quantity;
        s.top_item_sales = this.getItemSalesMap();
        return s;
    }

    @Override
    public String toString(){
        return String.format(Locale.US, "  \n-> %-10s %8.2f€ (Total de Items Vendidos: %3d)", 
                                 "Vendas:", 
                                 this.profit, 
                                 this.quantity);
    }
}