package basicClasses;

public class OrderPart {

    public Product product;
    public int amount;

    public OrderPart() {
    }

    public String getTextOfOrderPart() {
        String text = amount + " " + product.getColor() + " stone of size " + product.getSize() + "; ";
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OrderPart orderPart = (OrderPart) o;

        if (this.product.equals(orderPart.product) && this.amount == orderPart.amount)
            return true;

        return false;
    }
}
