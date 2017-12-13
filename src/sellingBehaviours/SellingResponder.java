package sellingBehaviours;

import basicAgents.Selling;
import basicClasses.Order;
import communication.Communication;
import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class SellingResponder extends AchieveREResponder {

    /**
     * 
     */
    private static final long serialVersionUID = 4671831774439180119L;
    private String orderText;

    public SellingResponder(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        // Selling reacts on SalesMarket's request

        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

        // Agent should send agree or refuse
        // TODO: Add refuse answer (some conditions should be added)

        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);

        // response.setPerformative(ACLMessage.REFUSE);

        if (request.getConversationId() == "Ask") {
            System.out.println("SellingAgent: [request] SalesMarket orders a " + orderText);
            Communication.server.sendMessageToClient("SellingAgent", "[request] SalesMarket orders a " + orderText);

            System.out.println("SellingAgent: [agree] I will check warehouse for " + orderText);
            Communication.server.sendMessageToClient("SellingAgent", "[agree] I will check warehouse for " + orderText);
            myAgent.addBehaviour(new CheckWarehouseBehaviour(myAgent, request));
        } else if (request.getConversationId() == "Take") {
            System.out.println("SellingAgent: [request] SalesMarket wants to take " + orderText + " from warehouse");
            Communication.server.sendMessageToClient("SellingAgent",
                    "[request] SalesMarket wants to take " + orderText + " from warehouse");
            System.out.println("SellingAgent: [agree] I will give you " + orderText + " from warehouse");
            Communication.server.sendMessageToClient("SellingAgent",
                    "[agree] I will give you " + orderText + " from warehouse");

            myAgent.addBehaviour(new GiveProductToMarketBehaviour(myAgent, request));
        }

        return response;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {

        // if agent agrees to request
        // after executing, it should send failure of inform

        // in case of inform product will be taken from warehouse
        // in case of failure product will be produced
        ACLMessage reply = request.createReply();
        reply.setContent(request.getContent());

        if (request.getConversationId() == "Ask") {
            if (Selling.isInWarehouse) {
                reply.setPerformative(ACLMessage.INFORM);
            } else {
                reply.setPerformative(ACLMessage.FAILURE);
            }
        } else if (request.getConversationId() == "Take") {
            if (Selling.isTaken) {
                reply.setPerformative(ACLMessage.INFORM);
            } else {
                reply.setPerformative(ACLMessage.FAILURE);
            }
        }

        return reply;
    }
}