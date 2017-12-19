package interactors;

import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;

public class Decision {
    protected DataStore dataStore;
    
    public Decision(DataStore dataStore) {
        this.dataStore = dataStore;
    }
    
    public ACLMessage execute(ACLMessage request) {
        // TODO Auto-generated method stub
        return null;
    }
}
