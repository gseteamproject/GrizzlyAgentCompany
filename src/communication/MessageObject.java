package communication;

public class MessageObject {
    private String agent;
    private String message;

    public MessageObject(String agent, String message) {
        super();

        setAgent(agent);
        setMessage(message);
    }

    public String getAgent() {
        return agent;
    }
    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
