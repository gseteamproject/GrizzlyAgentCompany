package productionBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.SimpleBehaviour;

public class ProductionAskBehaviour extends SimpleBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4443443755165652310L;
    private OrderDataStore dataStore;
    private ProductionResponder interactionBehaviour;

    public ProductionAskBehaviour(ProductionResponder interactionBehaviour, OrderDataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    public void action() {
        myAgent.addBehaviour(new AskForMaterialsBehaviour(interactionBehaviour, dataStore));
    }

    @Override
    public boolean done() {
        return true;
    }

}
