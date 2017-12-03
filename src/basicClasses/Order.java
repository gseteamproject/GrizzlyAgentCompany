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
    public List<OrderPart> productOrderList = new ArrayList<OrderPart>();
    public List<OrderPart> materialOrderList = new ArrayList<OrderPart>();

    public Order() {
    }

    public List<Product> getProducts() {
        List<Product> list = new ArrayList<Product>();
        for (OrderPart part : productOrderList) {
            if(part.good instanceof Product)
            list.add((Product)part.good);
        }
        return list;
    }

    public void addProduct(Product product, int amount) {
        OrderPart part = new OrderPart();
            part.good = (Product) part.good;
            part.good = product;

        part.amount = amount;
        boolean count = false;
        for (OrderPart partInList : productOrderList) {
            if (partInList.good.equals(product)) {
                partInList.amount += amount;
                count = true;
            }
        }
        if (!count) {
            productOrderList.add(part);
        }
    }

    public boolean addGood (Good good, int amount) {
        OrderPart part = new OrderPart();

        if(part.good instanceof Paint && good instanceof Paint){
            part.good = (Paint) part.good;
            part.good = (Paint) good;
        } else if (part.good instanceof Stone && good instanceof Stone){
            part.good = (Stone) part.good;
            part.good = (Stone) good;
        } else  if(part.good instanceof Product && good instanceof Product) {
            addProduct((Product)part.good, amount);
            return true;
        }

        part.amount = amount;
        boolean count = false;
        for (OrderPart partInList : materialOrderList) {
            if (partInList.good.equals(good)) {
                partInList.amount += amount;
                count = true;
            }
        }
        if (!count) {
            materialOrderList.add(part);
            return true;
        }
        return false;
    }

    public int getID() {
        return id;
    }

    public int getAmountByProduct(Product product) {
        int amount = 0;
        for (OrderPart part : productOrderList) {
            if (part.good.equals(product)) {
                amount = part.amount;
            }
        }
        return amount;
    }

    public String getTextOfProductOrder() {
        String text = "";
        Product product;
        Paint paint;
        Stone stone;
        Integer value;
        for (OrderPart part : productOrderList) {
            value = part.amount;
            product = (Product)part.good;
            text += value.toString() + " " + product.getColor() + " stone of size " + product.getSize() + "; ";
        }
        return text;
    }
    public String getTextOfMaterialOrder() {
        String text = "";
        Paint paint;
        Stone stone;
        Integer value;
        for (OrderPart part : materialOrderList) {
            value = part.amount;
            if (part.good instanceof Paint){
                paint = (Paint) part.good;
                text+= value.toString()  + " amounts of " + paint.getColor() + " Paint; ";
            } else if (part.good instanceof Stone){
                stone = (Stone) part.good;
                text += value.toString() + " amounts of stones with size of " + stone.getSize() + "; ";

            }
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
        boolean productListisEqual=false;

        if (this.id == order.id && order.productOrderList.size() == this.productOrderList.size() && order.materialOrderList.size() == this.materialOrderList.size()) {
            isEqual = 0;
            for (OrderPart orderPart : order.productOrderList) {
                for (OrderPart thisOrderPart : this.productOrderList) {
                    if (orderPart.equals(thisOrderPart)) {
                        isEqual += 1;
                    }
                }
            }
            if (isEqual == order.productOrderList.size() && isEqual != 0) {
                productListisEqual = true;
            }
            isEqual = 0;
            if(!productListisEqual)
                return false;
            for (OrderPart orderPart : order.materialOrderList) {
                for (OrderPart thisOrderPart : this.materialOrderList) {
                    if (orderPart.equals(thisOrderPart)) {
                        isEqual += 1;
                    }
                }
            }
            if (isEqual == order.materialOrderList.size() && isEqual != 0) {
                return true;
            }

        }
        return false;
    }

}