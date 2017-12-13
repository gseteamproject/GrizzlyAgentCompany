package procurementBehaviours;

import basicClasses.Order;
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

    public AskBehaviour(ProcurementResponder interactionBehaviour, DataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    public void action() {
        ACLMessage request = interactionBehaviour.getRequest();
        String orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
        if (request.getConversationId() == "Materials") {
            System.out.println("ProcurementAgent: [request] ProductionAgent asks for materials for " + orderText);
            System.out.println("ProcurementAgent: [agree] I will check materialStorage for materials for " + orderText);
            myAgent.addBehaviour(new CheckMaterialStorage(myAgent, request));
        } else if (request.getConversationId() == "Take") {
            System.out.println("ProcurementAgent: [request] ProductionAgent wants to get materials for " + orderText
                    + " from materialStorage");
            System.out.println(
                    "ProcurementAgent: [agree] I will give you materials for " + orderText + " from materialStorage");
            myAgent.addBehaviour(new GiveMaterialToProduction(myAgent, request));
        }
        myAgent.addBehaviour(new AskForAuction(interactionBehaviour.getAgent(), interactionBehaviour.getRequest()));

    }

    @Override
    public boolean done() {
        return true;
    }

}
