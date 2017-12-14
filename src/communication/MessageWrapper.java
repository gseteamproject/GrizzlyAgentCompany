package communication;

public class MessageWrapper {
    public String message;

    public MessageWrapper(MessageObject obj) {
        message = obj.getReceivedMessage();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
