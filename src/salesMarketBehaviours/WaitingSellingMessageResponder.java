package salesMarketBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import communication.MessageObject;
import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class WaitingSellingMessageResponder extends AchieveREResponder {

    /**
     * 
     */
    private static final long serialVersionUID = 7386418031416044376L;
    private String orderText;
    private DataStore dataStore;
    private SalesMarketResponder interactionBehaviour;
    private MessageObject msgObj;

    public WaitingSellingMessageResponder(SalesMarketResponder interactionBehaviour, MessageTemplate mt,
            DataStore dataStore) {
        super(interactionBehaviour.getAgent(), mt);
        this.interactionBehaviour = interactionBehaviour;
        this.dataStore = dataStore;
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        // Sales Market reacts on Selling' information
        ACLMessage response;
        Order order = Order.gson.fromJson(request.getContent(), Order.class);
        orderText = order.getTextOfOrder();

        msgObj = new MessageObject(request, orderText);
        System.out.println(msgObj.getReceivedMessage());

   /*     System.out.println(
                "SalesMarketAgent: received [inform] order " + orderText + " is ready for collection from warehouse");
*/
        response = request.createReply();
        response.setContent(request.getContent());
        response.setSender(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
        if (SalesMarket.orderQueue.contains(order)) {
            response.setPerformative(ACLMessage.AGREE);
            msgObj = new MessageObject(response,orderText);
            System.out.println(msgObj.getReceivedMessage());
           /* System.out.println("SalesMarketAgent: [agree] I will take " + orderText);
*/
            myAgent.addBehaviour(new TakeFromWarehouseBehaviour(interactionBehaviour, request, dataStore));

        } else {
            response.setPerformative(ACLMessage.REFUSE);
            msgObj = new MessageObject(response,orderText);
            System.out.println(msgObj.getReceivedMessage());
          //  System.out.println("SalesMarketAgent: [refuse] I will not take " + orderText);
        }
        return response;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {

        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
        msgObj = new MessageObject(request,orderText);

        ACLMessage inform = request.createReply();
        inform.setContent(request.getContent());
        inform.setPerformative(ACLMessage.INFORM);
        inform.setSender(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
        /*System.out.println("SalesMarketAgent: [inform] I asked to take " + orderText);*/

        return inform;
    }
}