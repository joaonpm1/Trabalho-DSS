package app.dss.cadeiaLN.subsistemaMenus;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Locale;

public class Drink{
    private String name;
    private float price;
    private int expectedPrep;

    private Map<String,Integer> recipe;

    public Drink(String name, float price, int expectedPrep, Map<String,Integer> recipe){
        this.name = new String(name);
        this.price = price;
        this.expectedPrep = expectedPrep;

        this.recipe = new HashMap<>(recipe);
    }

    public String getName(){
        return new String(this.name);
    }

    public float getPrice(){
        return this.price;
    }

    public int getPreparationMinutes(){
        return this.expectedPrep;
    }

    public Map<String,Integer> getRecipe(){
        return new HashMap<>(this.recipe);
    }


    @Override
    public String toString() {
        String ingredientesStr = "N/A";
        if (this.recipe != null && !this.recipe.isEmpty()) {
            ingredientesStr = this.recipe.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", "));
        }

        return String.format(Locale.US, "[BEBIDA] %s (%.2f€) [%d min] - Ingredientes: %s",
                this.name,
                this.price,
                this.expectedPrep,
                ingredientesStr);
    }
}