package app.dss.cadeiaLN.subsistemaRestaurante;

import java.util.Set;
import java.util.Map;
import java.util.List;
public interface IGestRestaurante {

	/**
	 * 
	 * @param id
	 * @param name
	 * @param location
	 */
	void addRestaurant(String id, String name, String location);

	/**
	 * 
	 * @param id
	 */
	void removeRestaurant(String id);

	Set<String> listRestaurants();

	public String getOrderId(String idRest, String type);
	/**
	 * 
	 * @param idRest
	 */
	List<String> listIngredientOrders(String idRest);

	/**
	 * 
	 * @param idRest
	 * @param orderID
	 */
	Map<String, Integer> getIngredientOrderItem(String idRest, int orderID);

	/**
	 * 
	 * @param idRest
	 */
	Map<String, Integer> getIngredients(String idRest);

	/**
	 * 
	 * @param idRest
	 * @param ingredient
	 * @param quantity
	 */
	void makIngredientOrder(String idRest, String ingredient, int quantity);

	/**
	 * 
	 * @param idRest
	 * @param ingredient
	 * @param quantity
	 */
	void addStock(String idRest, int orderID);

	/**
	 * 
	 * @param idRest
	 * @param ingredient
	 * @param quantity
	 */
	void removeStock(String idRest, String ingredient, int quantity);

	/**
	 * 
	 * @param idRest
	 * @param ingredientName
	 * @param quantity
	 */
	boolean hasStock(String idRest, String ingredientName, int quantity);

}