package procurementMarketBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.ParallelBehaviour;

public class ProcurementMarketActivityBehaviour extends ParallelBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -8533259660402146147L;

    public static ProcurementMarketRequestResult interactor;
    
    public ProcurementMarketActivityBehaviour(ProcurementMarketResponder interactionBehaviour,
            OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent(), WHEN_ANY);
        interactor = new ProcurementMarketRequestResult(dataStore);

        addSubBehaviour(new ProcurementMarketAskBehaviour(interactionBehaviour, interactor, dataStore));
        addSubBehaviour(new ProcurementMarketDeadlineBehaviour(interactionBehaviour, interactor, dataStore));
    }
}
