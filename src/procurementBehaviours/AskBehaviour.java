package procurementBehaviours;

import basicClasses.Order;
import communication.MessageObject;
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
    Work interactor;
    private MessageObject msgObj;

    public AskBehaviour(ProcurementResponder interactionBehaviour, DataStore dataStore) {
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
        this.interactor = new Work(dataStore);
    }

    @Override
    public void action() {
        ACLMessage request = interactionBehaviour.getRequest();
        String orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
        if (request.getConversationId() == "Materials") {
            
            msgObj = new MessageObject(request, orderText);
            System.out.println(msgObj.getReceivedMessage());
            
//            System.out.println("ProcurementAgent: [request] ProductionAgent asks for materials for " + orderText);
            System.out.println("ProcurementAgent: [agree] I will check materialStorage for materials for " + orderText);
            myAgent.addBehaviour(new CheckMaterialStorage(myAgent, request));
        } else if (request.getConversationId() == "Take") {
            
            msgObj = new MessageObject(request, orderText);
            System.out.println(msgObj.getReceivedMessage());
            
//            System.out.println("ProcurementAgent: [request] ProductionAgent wants to get materials for " + orderText
//                    + " from materialStorage");
            System.out.println(
                    "ProcurementAgent: [agree] I will give you materials for " + orderText + " from materialStorage");
            myAgent.addBehaviour(new GiveMaterialToProduction(myAgent, request));
        }
        
        interactionBehaviour.setResult(interactor.execute(interactionBehaviour.getRequest()));

    }

    @Override
    public boolean done() {
        return true;
    }

}
