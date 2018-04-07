package salesMarketBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.ParallelBehaviour;

public class SalesMarketActivityBehaviour extends ParallelBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -3030187281731033803L;


    public SalesMarketActivityBehaviour(SalesMarketResponder interactionBehaviour, SalesMarketRequestResult interactor, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent(), WHEN_ANY);

        addSubBehaviour(new SalesMarketDeadlineBehaviour(interactionBehaviour, interactor, dataStore));
        addSubBehaviour(new AskForOrderBehaviour((SalesMarketResponder) interactionBehaviour, dataStore));
//        addSubBehaviour(new SalesMarketAskBehaviour(interactionBehaviour, interactor, dataStore));
    }

}
