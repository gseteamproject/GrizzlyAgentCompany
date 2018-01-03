package productionBehaviours;

import interactors.AskBehaviour;
import interactors.OrderDataStore;

public class ProductionAskBehaviour extends AskBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4443443755165652310L;

    public ProductionAskBehaviour(ProductionResponder interactionBehaviour, ProductionRequestResult interactor,
            OrderDataStore dataStore) {
        super(interactionBehaviour, interactor, dataStore);
    }

    @Override
    public void action() {
        if (!this.isStarted) {
            myAgent.addBehaviour(new AskForMaterialsBehaviour((ProductionResponder) interactionBehaviour, dataStore));
        }
        this.isStarted = true;
    }
}
