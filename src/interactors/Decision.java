package interactors;

import jade.lang.acl.ACLMessage;

public class Decision {
    protected OrderDataStore dataStore;

    public Decision(OrderDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public ACLMessage execute(ACLMessage request) {
        return null;
    }
}
