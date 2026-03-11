package app.dss.cadeiaLN.subsistemaFuncionarios;

import java.util.List;
import java.util.Set;

public interface IGestFuncionarios {

	/**
	 * 
	 * @param number
	 */
	String consultFuncionario(String number);

	/**
	 * 
	 * @param name
	 * @param email
	 * @param phone_number
	 */
	void addFuncionario(String name, String email, String phone_number, String role, String restaurantId);

	/**
	 * 
	 * @param number
	 */
	void removeFuncionario(String number);

	/**
	 * 
	 * @param number
	 * @param role
	 */
	void relocateFuncionario(String number, String role);

	public List<String> listEmployees(String idRestFilter);
	/**
	 * 
	 * @param idFunc
	 * @param idRest
	 * @param text
	 */
	void contactRestaurant(String idFunc, String idRest, String text);

	/**
	 * 
	 * @param numFunc
	 * @param numDest
	 * @param text
	 */
	void sendMessage(String numFunc, String numDest, String text);

	/**
	 * 
	 * @param numFunc
	 */
	Set<String> getSentMessages(String numFunc);

	/**
	 * 
	 * @param numFunc
	 */
	Set<String> getReceivedMessages(String numFunc);

	/**
	 * 
	 * @param numFunc
	 * @param password
	 */
	Boolean login(String numFunc, String password);

	String getRole(String idFunc);

	String getRestId(String idFunc);
	/**
	 * 
	 * @param numFunc
	 * @param newPassword
	 */
	Boolean changePassword(String numFunc, String newPassword);

}