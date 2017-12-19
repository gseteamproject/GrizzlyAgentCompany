package interactors;

import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;

public class RequestResult {
    protected DataStore dataStore;

    public RequestResult(DataStore dataStore) {
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