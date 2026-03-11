package app.dss.cadeiaLN.subsistemaMenus;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import app.dss.cadeiaDL.MenuDAO;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GestMenusFacade implements IGestMenus {
    private Map<String, Drink> drinks;
    private Map<String, Meal> meals;
    private Map<String, Menu> menus;
    
    private MenuDAO menuDAO;

    public GestMenusFacade() {
        this.drinks = new HashMap<>();
        this.meals = new HashMap<>();
        this.menus = new HashMap<>();
        this.menuDAO = new MenuDAO();

        importMenu();
    }

    @Override
    public void importMenu() {
        JsonElement jsonElement = null;
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("menus.json");
            
            if (inputStream == null) {
                System.err.println("ERRO CRÍTICO: Ficheiro 'menus.json' não encontrado em 'src/main/resources'!");
                return;
            }

            Reader reader = new InputStreamReader(inputStream);
            jsonElement = JsonParser.parseReader(reader);

        } catch (Exception e) {
            System.err.println("Erro ao ler o ficheiro JSON: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (jsonElement == null || !jsonElement.isJsonObject()) {
            return;
        }

        JsonObject root = jsonElement.getAsJsonObject();

        Map<String, Meal> keyToMeal = new HashMap<>();
        Map<String, Drink> keyToDrink = new HashMap<>();

        if (root.has("Meals")) {
            JsonObject mealsObj = root.getAsJsonObject("Meals");
            for (Map.Entry<String, JsonElement> entry : mealsObj.entrySet()) {
                String jsonKey = entry.getKey();
                try {
                    Meal meal = parseMeal(entry.getValue().getAsJsonObject());
                    this.meals.put(meal.getName(), meal);
                    keyToMeal.put(jsonKey, meal); 
                } catch (Exception e) {
                    System.err.println("Erro ao importar refeição (" + jsonKey + "): " + e.getMessage());
                }
            }
        }

        if (root.has("Drinks")) {
            JsonObject drinksObj = root.getAsJsonObject("Drinks");
            for (Map.Entry<String, JsonElement> entry : drinksObj.entrySet()) {
                String jsonKey = entry.getKey(); 
                try {
                    Drink drink = parseDrink(entry.getValue().getAsJsonObject());
                    this.drinks.put(drink.getName(), drink);
                    keyToDrink.put(jsonKey, drink); 
                } catch (Exception e) {
                    System.err.println("Erro ao importar bebida (" + jsonKey + "): " + e.getMessage());
                }
            }
        }

        if (root.has("Menus")) {
            JsonObject menusObj = root.getAsJsonObject("Menus");
            for (Map.Entry<String, JsonElement> entry : menusObj.entrySet()) {
                String jsonKey = entry.getKey();
                try {
                    Menu menu = parseMenu(entry.getValue().getAsJsonObject(), keyToMeal, keyToDrink);
                    this.menus.put(menu.getName(), menu);
                } catch (Exception e) {
                    System.err.println("Erro ao importar menu (" + jsonKey + "): " + e.getMessage());
                }
            }
        }

        System.out.println("Subsistema Menus: Dados carregados com sucesso do JSON.");

        System.out.println("A sincronizar catálogo com a Base de Dados...");

        for (Meal m : this.meals.values()) {
            menuDAO.upsertProduct(
                m.getName(), m.getPrice(), m.getPreparationMinutes(), "Meal", m.getRecipe()
            );
        }

        for (Drink d : this.drinks.values()) {
            menuDAO.upsertProduct(
                d.getName(), d.getPrice(), d.getPreparationMinutes(), "Drink", d.getRecipe()
            );
        }

        for (Menu m : this.menus.values()) {
            menuDAO.upsertMenu(
                m.getName(), m.getPrice(), m.getPreparationMinutes(), m
            );
        }

        System.out.println("Sincronização com BD concluída.");
    }

    private Meal parseMeal(JsonObject json) {
        String name = json.get("name").getAsString();
        float price = json.get("price").getAsFloat();
        int time = json.has("expectedPrep") ? json.get("expectedPrep").getAsInt() : 0;
        
        Map<String, Integer> ingredients = parseIngredients(json.getAsJsonObject("recipe"));
        return new Meal(name, price, time, ingredients);
    }

    private Drink parseDrink(JsonObject json) {
        String name = json.get("name").getAsString();
        float price = json.get("price").getAsFloat();
        int time = json.has("expectedPrep") ? json.get("expectedPrep").getAsInt() : 0;
        
        Map<String, Integer> ingredients = parseIngredients(json.getAsJsonObject("recipe"));
        return new Drink(name, price, time, ingredients);
    }

    private Menu parseMenu(JsonObject json, Map<String, Meal> keyToMeal, Map<String, Drink> keyToDrink) {
        String name = json.get("name").getAsString();
        float price = json.get("price").getAsFloat();
        int time = json.has("expectedPrep") ? json.get("expectedPrep").getAsInt() : 0;
        
        List<Meal> listMeals = new ArrayList<>();
        List<Drink> listDrinks = new ArrayList<>();

        if (json.has("recipe")) {
            JsonObject recipe = json.getAsJsonObject("recipe");
            
            for (String itemKey : recipe.keySet()) {
                int quantity = recipe.get(itemKey).getAsInt();
                
                if (keyToMeal.containsKey(itemKey)) {
                    Meal m = keyToMeal.get(itemKey);
                    for (int i = 0; i < quantity; i++) listMeals.add(m);
                } 
                else if (keyToDrink.containsKey(itemKey)) {
                    Drink d = keyToDrink.get(itemKey);
                    for (int i = 0; i < quantity; i++) listDrinks.add(d);
                } else {
                    System.err.println("Aviso: Item '" + itemKey + "' no menu '" + name + "' não encontrado.");
                }
            }
        }

        return new Menu(name, price, time, listDrinks, listMeals);
    }

    private Map<String, Integer> parseIngredients(JsonObject jsonRecipe) {
        Map<String, Integer> map = new HashMap<>();
        if (jsonRecipe != null && !jsonRecipe.isJsonNull()) {
            for (String key : jsonRecipe.keySet()) {
                map.put(key, jsonRecipe.get(key).getAsInt());
            }
        }
        return map;
    }

    @Override
    public String getItemType(String itemName){
        if(this.menus.containsKey(itemName)) return "Menu";
        if(this.drinks.containsKey(itemName)) return "Drink";
        if(this.meals.containsKey(itemName)) return "Meal";
        return null;
    }

    @Override
    public Map<String, Integer> getMealReceipe(String mealName) {
        if (meals.containsKey(mealName)) {
            return meals.get(mealName).getRecipe();
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Integer> getDrinkReceipe(String drinkName) {
        if (drinks.containsKey(drinkName)) {
            return drinks.get(drinkName).getRecipe();
        }
        return new HashMap<>();
    }

    @Override
    public Map<String, Integer> getMenuReceipe(String menuName) {
        Map<String, Integer> aggregatedRecipe = new HashMap<>();
        
        if (!menus.containsKey(menuName)) {
            return aggregatedRecipe;
        }

        Menu menu = menus.get(menuName);

        for (Meal meal : menu.getMeals()) {
            mergeRecipes(aggregatedRecipe, meal.getRecipe());
        }

        for (Drink drink : menu.getDrinks()) {
            mergeRecipes(aggregatedRecipe, drink.getRecipe());
        }

        return aggregatedRecipe;
    }

    @Override
    public float getMenuPrice(String menuName) {
        return menus.containsKey(menuName) ? menus.get(menuName).getPrice() : 0.0f;
    }

    @Override
    public float getDrinkPrice(String drinkName) {
        return drinks.containsKey(drinkName) ? drinks.get(drinkName).getPrice() : 0.0f;
    }

    @Override
    public float getMealPrice(String mealName) {
        return meals.containsKey(mealName) ? meals.get(mealName).getPrice() : 0.0f;
    }

    @Override
    public Set<String> listMenus() {
        return new HashSet<>(menus.keySet());
    }
    
    @Override
    public Set<String> listMeals() {
        return new HashSet<>(this.meals.keySet());
    }
    
    @Override
    public Set<String> listDrinks() {
        return new HashSet<>(this.drinks.keySet());
    }

    @Override
    public int getItemExpectedPreperationTime(String itemName) {
        if (meals.containsKey(itemName)) return meals.get(itemName).getPreparationMinutes();
        if (drinks.containsKey(itemName)) return drinks.get(itemName).getPreparationMinutes();
        if (menus.containsKey(itemName)) return menus.get(itemName).getPreparationMinutes();
        return 0;
    }

    private void mergeRecipes(Map<String, Integer> total, Map<String, Integer> partial) {
        for (Map.Entry<String, Integer> entry : partial.entrySet()) {
            total.put(entry.getKey(), total.getOrDefault(entry.getKey(), 0) + entry.getValue());
        }
    }
}