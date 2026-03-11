package app.dss.cadeiaLN.subsistemaMenus;

import java.util.List;
import java.util.Locale;
import java.util.ArrayList;

public class Menu {
    private String name;
    private float price;
    private int expectedPreparationTime;

    private List<Drink> drinks;
    private List<Meal> meals;

    public Menu(String name, float price, int expectedPrep, List<Drink> drinks, List<Meal> meals){
        this.name = name; 
        this.price = price;
        this.expectedPreparationTime = expectedPrep;
        
        this.drinks = new ArrayList<>(drinks);
        this.meals = new ArrayList<>(meals);
    }

    public String getName(){
        return this.name;
    }

    public float getPrice(){
        return this.price;
    }

    public List<Drink> getDrinks(){
        return new ArrayList<>(this.drinks);
    }

    public List<Meal> getMeals(){
        return new ArrayList<>(this.meals);
    }
    
    public int getPreparationMinutes(){
        return this.expectedPreparationTime;
    }

    public List<String> getMealNames() { 
        ArrayList<String> mealnames = new ArrayList<>();
        for(Meal m : meals){
            mealnames.add(m.getName());
        }
        return mealnames;
    }

    public List<String> getDrinkNames() { 
        ArrayList<String> drinkNames = new ArrayList<>();
        for(Drink d : drinks){
            drinkNames.add(d.getName());
        }
        return drinkNames;
     }

    @Override
    public String toString() {
        List<String> itemNames = new ArrayList<>();
        
        for (Meal m : this.meals) {
            itemNames.add(m.getName()); 
        }
        for (Drink d : this.drinks) {
            itemNames.add(d.getName());
        }
        
        String itemsString = String.join(", ", itemNames);

        return String.format(Locale.US, "[MENU] %s (%.2f€) - Inclui: %s", 
                             this.name, 
                             this.price, 
                             itemsString);
    }
}