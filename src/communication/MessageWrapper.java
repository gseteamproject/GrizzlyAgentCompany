package communication;

public class MessageWrapper {
    public String performative;
    public String message;
    public String color1;
    public String color2;

    public MessageWrapper(MessageObject obj) {
        this.message = obj.getReceivedMessage();
        this.color1 = obj.getColorForPerformative();
        this.color2 = obj.getColorForAgent();
        this.performative = obj.getPerformative();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getColor1() {
        return color1;
    }

    public void setColor1(String color1) {
        this.color1 = color1;
    }

    public String getColor2() {
        return color2;
    }

    public void setColor2(String color2) {
        this.color2 = color2;
    }

    public String getPerformative() {
        return performative;
    }

    public void setPerformative(String performative) {
        this.performative = performative;
    }
}
