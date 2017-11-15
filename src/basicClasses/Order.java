package basicClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/* Orders are Product-Orders (Red Small Stones or Big Blue Stones and so on) from Customers which
   are safed in this Class and are used by different Agents
 */

public class Order implements Serializable {
	private static final long serialVersionUID = 8348788729049247785L;

	public static GsonBuilder builder = new GsonBuilder();
	public static Gson gson = builder.create();

	public int id;
	public List<OrderPart> orderList = new ArrayList<OrderPart>();

	public Order() {
	}

	public List<Product> getProducts() {
		List<Product> list = new ArrayList<Product>();
		for (OrderPart part : orderList) {
			list.add(part.product);
		}
		return list;
	}

	public void addProduct(Product product, int amount) {
		OrderPart part = new OrderPart();
		part.product = product;
		part.amount = amount;
		orderList.add(part);
	}

	public int getID() {
		return id;
	}

	public int getAmountByProduct(Product product) {
		int amount = 0;
		for (OrderPart part : orderList) {
			if (part.product == product) {
				amount = part.amount;
			}
		}
		return amount;
	}

	public String getTextOfOrder() {
		String text = "";
		for (OrderPart part : orderList) {
			Product key = part.product;
			Integer value = part.amount;

			text += value.toString() + " " + key.getColor() + " stone of size " + key.getSize();
		}
		return text;
	}
}