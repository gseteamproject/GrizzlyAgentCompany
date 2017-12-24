package productionBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;

public class ProductionActivityBehaviour extends SequentialBehaviour {
    // public class ProductionActivityBehaviour extends ParallelBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -6703040253614653144L;

    public ProductionActivityBehaviour(ProductionResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        // super(interactionBehaviour.getAgent(), WHEN_ANY);

        // TODO: Remove Ask
        // addSubBehaviour(new ProductionAskBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new AskForMaterialsBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new ProductionDeadlineBehaviour(interactionBehaviour, dataStore));
    }

}
