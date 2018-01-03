package salesMarketBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.ParallelBehaviour;

public class SalesMarketActivityBehaviour extends ParallelBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -3030187281731033803L;

    public static SalesMarketRequestResult interactor;

    public SalesMarketActivityBehaviour(SalesMarketResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent(), WHEN_ANY);
        interactor = new SalesMarketRequestResult(dataStore);

        addSubBehaviour(new SalesMarketAskBehaviour(interactionBehaviour, interactor, dataStore));
        addSubBehaviour(new SalesMarketDeadlineBehaviour(interactionBehaviour, interactor, dataStore));
    }

}
