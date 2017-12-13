package productionBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.SimpleBehaviour;

public class AskBehaviour extends SimpleBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4443443755165652310L;
    private DataStore dataStore;
    private ProductionResponder interactionBehaviour;

    public AskBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    public void action() {
        myAgent.addBehaviour(new AskForMaterialBehaviour(interactionBehaviour, 2000, dataStore));

    }

    @Override
    public boolean done() {
        return true;
    }

}
