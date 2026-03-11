package app.dss.cadeiaLN.subsistemaPedidos;

import java.util.List;

import java.time.LocalDateTime;
public interface IGestOrders {

	List<String> getPendingOrders(String idRest);

	/**
	 * 
	 * @param orderID
	 * @param delay
	 */
	void delayOrder(String orderID, String idRest,LocalDateTime delay);

	/**
	 * 
	 * @param idRest
	 * @param items
	 */
	public void addOrder(Order o, String idRest);

	/**
	 * 
	 * @param orderID
	 * @param newState
	 */
	void changeOrderState(String orderID,String idRest, String newState);
	
	public long getOrderFinishTime(String idRest, String orderID);
	/**
	 * 
	 * @param orderID
	 */
	public String getOrderResume(String orderID, String idRest);

	/**
	 * 
	 * @param orderID
	 * @param taskID
	 * @param newState
	 */
	boolean changeTaskState(String orderID,String idRest ,String taskID);


	public List<String> getOrderTasks(String idRest, String orderID);

}