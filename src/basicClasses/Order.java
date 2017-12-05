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
        if (amount > 0) {
            OrderPart part = new OrderPart();
            part.product = product;
            part.amount = amount;
            boolean count = false;
            for (OrderPart partInList : orderList) {
                if (partInList.product.equals(product)) {
                    partInList.amount += amount;
                    count = true;
                }
            }
            if (!count) {
                orderList.add(part);
            }
        }
    }

    public int getID() {
        return id;
    }

    public int getAmountByProduct(Product product) {
        int amount = 0;
        for (OrderPart part : orderList) {
            if (part.product.equals(product)) {
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

            text += value.toString() + " " + key.getColor() + " stone of size " + key.getSize() + "; ";
        }
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Order order = (Order) o;
        int isEqual;

        if (this.id == order.id && order.orderList.size() == this.orderList.size()) {
            isEqual = 0;
            for (OrderPart orderPart : order.orderList) {
                for (OrderPart thisOrderPart : this.orderList) {
                    if (orderPart.equals(thisOrderPart)) {
                        isEqual += 1;
                    }
                }
            }
            if (isEqual == order.orderList.size() && isEqual != 0) {
                return true;
            }
        }
        return false;
    }

}