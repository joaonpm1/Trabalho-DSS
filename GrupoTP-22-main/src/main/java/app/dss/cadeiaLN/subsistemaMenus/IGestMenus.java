package app.dss.cadeiaLN.subsistemaMenus;


import java.util.Map;
import java.util.Set;



public interface IGestMenus{
    public void importMenu();

    public String getItemType(String itemName);
    
    public Map<String,Integer> getMealReceipe(String mealName);

    public Map<String,Integer> getDrinkReceipe(String drinkName);

    public Map<String,Integer> getMenuReceipe(String menuName);
    
    public float getMenuPrice(String menuName);

    public float getDrinkPrice(String drinkName);

    public float getMealPrice(String mealName);

    public Set<String> listMenus();

    public int getItemExpectedPreperationTime(String itemName);

    public Set<String> listMeals();

    public Set<String> listDrinks();
}