package sellingBehaviours;

import java.util.Vector;

import basicAgents.SalesMarket;
import basicAgents.Selling;
import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import interactors.AchieveREInitiatorInteractor;
import interactors.OrderDataStore;
import interactors.RequestInteractor;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class AskToProduceInitiator extends RequestInteractor implements AchieveREInitiatorInteractor {

    private SellingResponder interactionBehaviour;
    private String orderText;
    public MessageObject msgObj;

    public AskToProduceInitiator(SellingResponder interactionBehaviour, OrderDataStore dataStore) {
        super(dataStore);
        this.interactionBehaviour = interactionBehaviour;
    }

    @Override
    public Vector<ACLMessage> prepareRequests(ACLMessage request) {
        request = new ACLMessage(ACLMessage.REQUEST);

        System.out.println("5" + interactionBehaviour.getRequest());
        String requestedAction = "Produce";
        request.setConversationId(requestedAction);
        request.addReceiver(new AID(("AgentProduction"), AID.ISLOCALNAME));
        request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
        request.setContent(dataStore.getRequestMessage().getContent());

        Vector<ACLMessage> l = new Vector<ACLMessage>(1);
        l.addElement(request);
        return l;
    }

    @Override
    public void handleAgree(ACLMessage agree) {
        // TODO Auto-generated method stub
        orderText = Order.gson.fromJson(agree.getContent(), Order.class).getTextOfOrder();
        System.out.println("SellingAgent: received [agree] Producing of " + orderText + " is initiated");
        Communication.server.sendMessageToClient("SellingAgent",
                "received [agree] Producing of " + orderText + " is initiated");
    }

    @Override
    public void handleRefuse(ACLMessage refuse) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handleInform(ACLMessage inform) {
        // TODO Auto-generated method stub

        System.out.println("6" + interactionBehaviour.getRequest());
        Order order = Order.gson.fromJson(inform.getContent(), Order.class);
        orderText = order.getTextOfOrder();
        System.out.println("SellingAgent: received [inform] " + orderText + " is delivered to warehouse");
        Communication.server.sendMessageToClient("SellingAgent",
                "received [inform] " + orderText + " is delivered to warehouse");
        Selling.isInWarehouse = true;
        for (Order orderInQueue : SalesMarket.orderQueue) {
            if (orderInQueue.id == order.id) {
                order = orderInQueue;
            }
        }
    }

    @Override
    public void handleFailure(ACLMessage failure) {
        // TODO Auto-generated method stub
        orderText = Order.gson.fromJson(failure.getContent(), Order.class).getTextOfOrder();
        System.out.println("SellingAgent: received [failure] is not produced");
        Communication.server.sendMessageToClient("SellingAgent", "received [failure] is not produced");
    }

    @Override
    public int next() {
        // TODO Auto-generated method stub
        return 0;
    }

}
