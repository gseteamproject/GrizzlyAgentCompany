package basicClasses;

public class OrderPart {

    public Good good;
    public int amount;

    public OrderPart() {
    }

    public String getTextOfOrderPart() {
        String text = "";
        if(good instanceof Product) {
            good = (Product) good;
            text = amount + " " + ((Product) good).getColor()+ " stone of size " + ((Product) good).getSize() + "; ";
        } else if (good instanceof Paint){
            good = (Paint) good;
            text = amount + " " + ((Paint) good).getColor() + "; ";
        } else if (good instanceof Stone){
            good = (Stone) good;
            text = amount + " " + ((Stone) good).getSize() + "; ";
        }
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OrderPart orderPart = (OrderPart) o;

        if (this.good.equals(orderPart.good) && this.amount == orderPart.amount)
            return true;

        return false;
    }
}
