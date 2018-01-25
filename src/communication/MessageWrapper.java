package communication;

public class MessageWrapper {
    public String performative;
    public String message;
    public String color1;
    public String color2;

    public MessageWrapper(MessageObject msgObj) {

        if (msgObj.getActingAgent()==null) {
            this.message = msgObj.getReceivedMessage();
            this.color1 = msgObj.getColorForPerformative();
            this.color2 = msgObj.getColorForAgent();
            this.performative = msgObj.getPerformative();
        } else{
            this.message = msgObj.getActionMessage();
            this.color2 = msgObj.getColorForAction();
        }
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
