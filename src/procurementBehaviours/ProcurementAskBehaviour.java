package procurementBehaviours;

import jade.core.behaviours.DataStore;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;

public class AskBehaviour extends SimpleBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4443443755165652310L;
    private DataStore dataStore;
    private ProcurementResponder interactionBehaviour;
    ProcurementRequestResult interactor;

    public AskBehaviour(ProcurementResponder interactionBehaviour, DataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        this.interactor = new ProcurementRequestResult(dataStore);
    }

    @Override
    public void action() {
        ACLMessage request = interactionBehaviour.getRequest();
        if (request.getConversationId() == "Materials") {
            myAgent.addBehaviour(new CheckMaterialStorage(myAgent, request));
        } else if (request.getConversationId() == "Take") {
            myAgent.addBehaviour(new GiveMaterialToProduction(myAgent, request));
        }

        // interactionBehaviour.setResult(interactor.execute(interactionBehaviour.getRequest()));

    }

    @Override
    public boolean done() {
        return true;
    }

}
