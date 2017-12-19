package productionBehaviours;

import interactors.DecisionBehaviour;
import jade.core.behaviours.DataStore;

public class ProductionDecisionBehaviour extends DecisionBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -5138036682748995317L;

    public ProductionDecisionBehaviour(ProductionResponder interactionBehaviour, DataStore dataStore) {
        super(interactionBehaviour);
        this.interactor = new ProductionDecision(dataStore);
    }
}
