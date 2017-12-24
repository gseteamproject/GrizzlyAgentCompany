package interactors;

import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;

public class OrderDataStore extends DataStore {

    /**
     * 
     */
    private static final long serialVersionUID = 2340744686374901306L;

    public void setRequestMessage(ACLMessage msg) {
        put("request-message", msg);
    }

    public ACLMessage getRequestMessage() {
        return (ACLMessage) get("request-message");
    }
}
