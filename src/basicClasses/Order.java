package basicClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/* Orders are Product-Orders (Red Small Stones or Big Blue Stones and so on) from Customers which
   are safed in this Class and are used by different Agents
 */

public class Order implements Serializable {
	private static final long serialVersionUID = 8348788729049247785L;
	
	private int orderID;
	private Map<Product, Integer> order = new HashMap<Product, Integer>();

	public Order(int id) {
		this.orderID = id;
	}

	public List<Product> getProducts() {
		List<Product> list = new ArrayList<Product>(order.keySet());
		return list;
	}

	public void addProduct (Product prod, int amount) {
		this.order.put(prod, amount);
	}
	
	public int getID() {
		return orderID;
	}
	
	public int getAmountByProduct (Product prod) {
		return order.get(prod);
	}

	public String getTextOfOrder() {
		String text = "";

		for (Entry<Product, Integer> entry : order.entrySet()) {
			Product key = entry.getKey();
			Integer value = entry.getValue();

			text += value.toString() + " " + key.getColor() + " stone of size " + key.getSize();
		}
		return text;
	}

	public static Order readOrder(String request) {
		String[] params = request.split(" ");
		String color = params[0];
		double size = Double.parseDouble(params[1]);
		int amount = Integer.parseInt(params[2]);

		Product prod = new Product(size, color);
		Order order = new Order(0);
		order.addProduct(prod, amount);

		return order;
	}
	
	public static Order readOrder(int id, String request) {
		String[] params = request.split(" ");
		String color = params[0];
		double size = Double.parseDouble(params[1]);
		int amount = Integer.parseInt(params[2]);

		Product prod = new Product(size, color);
		Order order = new Order(id);
		order.addProduct(prod, amount);

		return order;
	}
}
