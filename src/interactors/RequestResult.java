package interactors;

import jade.lang.acl.ACLMessage;

public class RequestResult {
    protected OrderDataStore dataStore;

    public RequestResult(OrderDataStore dataStore) {
        super();
        this.dataStore = dataStore;
    }

    public boolean done() {
        return true;
    }

    public ACLMessage execute(ACLMessage request) {
        // TODO Auto-generated method stub
        return null;
    }
}