package procurementMarketBehaviours;

import interactors.AskBehaviour;
import interactors.OrderDataStore;

public class ProcurementMarketAskBehaviour extends AskBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -121063921816472827L;

    public ProcurementMarketAskBehaviour(ProcurementMarketResponder interactionBehaviour,
            ProcurementMarketRequestResult interactor, OrderDataStore dataStore) {
        super(interactionBehaviour, interactor, dataStore);
    }

    @Override
    public void action() {
        if (!this.isStarted) {
            // TODO: pass DataStore?
            myAgent.addBehaviour(new AuctionInitiator((ProcurementMarketResponder) interactionBehaviour));
        }
        this.isStarted = true;
    }
}
