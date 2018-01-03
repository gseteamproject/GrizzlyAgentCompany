package procurementBehaviours;

import interactors.AskBehaviour;
import interactors.OrderDataStore;
import jade.lang.acl.ACLMessage;

public class ProcurementAskBehaviour extends AskBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4443443755165652310L;

    public ProcurementAskBehaviour(ProcurementResponder interactionBehaviour, ProcurementRequestResult interactor,
            OrderDataStore dataStore) {
        super(interactionBehaviour, interactor, dataStore);
    }

    @Override
    public void action() {
        ACLMessage request = interactionBehaviour.getRequest();
        if (request.getConversationId() == "Materials") {
            if (!this.isStarted) {
                this.interactor.isDone = false;
                myAgent.addBehaviour(new CheckMaterialStorage((ProcurementResponder) interactionBehaviour, dataStore));
            }
            this.isStarted = true;
        } else if (request.getConversationId() == "Take") {
            if (this.isStarted) {
                this.interactor.isDone = false;
                myAgent.addBehaviour(
                        new GiveMaterialToProduction((ProcurementResponder) interactionBehaviour, dataStore));
            }
            this.isStarted = false;
        }
    }
}
