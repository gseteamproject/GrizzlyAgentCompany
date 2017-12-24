package salesMarketBehaviours;

import interactors.OrderDataStore;
import jade.core.behaviours.SequentialBehaviour;

public class SalesMarketActivityBehaviour extends SequentialBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -3030187281731033803L;

    public SalesMarketActivityBehaviour(SalesMarketResponder interactionBehaviour, OrderDataStore dataStore) {
        super(interactionBehaviour.getAgent());
        // super(interactionBehaviour.getAgent(), WHEN_ANY);

        // TODO: Remove Ask
        addSubBehaviour(new SalesMarketAskBehaviour(interactionBehaviour, dataStore));
        // addSubBehaviour(new AskForOrderBehaviour(interactionBehaviour, dataStore));
        addSubBehaviour(new SalesMarketDeadlineBehaviour(interactionBehaviour, dataStore));
    }

}
