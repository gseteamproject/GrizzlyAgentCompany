package basicClasses;

public class OrderPart {

    private Product product;
    private Paint paint;
    private Stone stone;
    private int amount;

    public OrderPart(Product prod) {
        this.product = prod;
    }

    public OrderPart(Paint paint) {
        this.paint = paint;
    }

    public OrderPart(Stone stone) {
        this.stone = stone;
    }

    public String getTextOfOrderPart() {
        String text = "error";

        if (this.product != null) {

            text = this.amount + " " + this.product.getColor() + " stone of size " + this.product.getSize() + "; ";
        }
        if (this.paint != null) {

            text = this.amount + " portion(s) of  " + this.paint.getColor() + " color; ";
        }
        if (this.stone != null) {

            text = this.amount + " stone(s) of size " + this.stone.getSize() + "; ";
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

        if (this.product != null && this.product.equals(orderPart.product) && this.amount == orderPart.amount)
            return true;

        // TODO: equals for stones and colors

        return false;
    }

    public String getPartClassName() {
        if (this.product != null) {
            return "product";
        }
        if (this.paint != null) {
            return "paint";
        }
        if (this.stone != null) {
            return "stone";
        }
        return "";
    }

    // Getters and Setters for product, paint and stone

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Stone getStone() {
        return stone;
    }

    public void setStone(Stone stone) {
        this.stone = stone;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (amount > 0) {

            this.amount = amount;
        } else {

            this.amount = 0;
        }
    }
}
