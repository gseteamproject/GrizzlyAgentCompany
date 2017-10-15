package basicClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Order implements Serializable {
	private static final long serialVersionUID = 8348788729049247785L;

	private Map<Material, Integer> order = new HashMap<Material, Integer>();;

	public Order(Material mat, int amount) {
		this.order.put(mat, amount);
	}

	public List<Material> getMaterials() {
		List<Material> list = new ArrayList<Material>(order.keySet());
		return list;
	}

	public int getAmountByMaterial(Material mat) {
		return order.get(mat);
	}

	public String getTextOfOrder() {
		String text = "";

		for (Entry<Material, Integer> entry : order.entrySet()) {
			Material key = entry.getKey();
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

		Material mat = new Material(color, size);
		Order order = new Order(mat, amount);

		return order;
	}
}
