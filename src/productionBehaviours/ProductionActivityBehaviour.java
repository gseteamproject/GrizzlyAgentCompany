package productionBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.ParallelBehaviour;

public class ProductionActivityBehaviour extends ParallelBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6703040253614653144L;

    // TODO: put this into DataStore
    public static ProductionRequestResult interactor;

    public ProductionActivityBehaviour(ProductionResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent(), WHEN_ANY);
        interactor = new ProductionRequestResult(dataStore);

        // TODO: Should I remove Ask????
        addSubBehaviour(new ProductionAskBehaviour(interactionBehaviour, interactor, dataStore));
        // addSubBehaviour(new AskForMaterialsBehaviour(interactionBehaviour,
        // dataStore));
        addSubBehaviour(new ProductionDeadlineBehaviour(interactionBehaviour, interactor, dataStore));
    }
}
