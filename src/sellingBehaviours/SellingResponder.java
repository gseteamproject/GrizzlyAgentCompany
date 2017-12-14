package sellingBehaviours;

import basicAgents.Selling;
import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import jade.core.AID;
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
    private MessageObject msgObj;

    public SellingResponder(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        // Selling reacts on SalesMarket's request

        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
        msgObj = new MessageObject(request, orderText);
        // Agent should send agree or refuse
        // TODO: Add refuse answer (some conditions should be added)

        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);
        response.setSender(new AID(("AgentSelling"), AID.ISLOCALNAME));

        // response.setPerformative(ACLMessage.REFUSE);

        System.out.println(msgObj.getReceivedMessage());

        if (request.getConversationId() == "Ask") {

            msgObj = new MessageObject(response, orderText);
            System.out.println(msgObj.getReceivedMessage());
            Communication.server.sendMessageToClient("SellingAgent", "[agree] I will check warehouse for " + orderText);
            myAgent.addBehaviour(new CheckWarehouseBehaviour(myAgent, request));
        } else if (request.getConversationId() == "Take") {




            msgObj = new MessageObject(response, orderText);
            System.out.println(msgObj.getReceivedMessage());
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