package salesMarketBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import communication.Communication;
import communication.MessageObject;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

import java.util.Iterator;
import java.util.prefs.Preferences;

public class SalesMarketResponder extends AchieveREResponder {

    /**
     * 
     */
    private static final long serialVersionUID = 7386418031416044376L;
    private String orderText;
    protected DataStore dataStore;
    private MessageObject msgObj;

    public SalesMarketResponder(Agent a, MessageTemplate mt, DataStore dataStore) {
        super(a, mt, dataStore);

        // registerHandleRequest(new DecisionBehaviour(this, dataStore));
        // registerPrepareResultNotification(new ActivityBehaviour(this, dataStore));
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        // Sales Market reacts on customer's request
        System.out.println("request " + request.getContent());
        Order order = Order.gson.fromJson(request.getContent(), Order.class);
        orderText = order.getTextOfOrder();

        msgObj = new MessageObject(request, orderText);
        Communication.server.sendMessageToClient(msgObj);
        System.out.println(msgObj.getReceivedMessage());

        // Agent should send agree or refuse
        ACLMessage response;
        response = request.createReply();
        response.setContent(request.getContent());
        response.setSender(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));

        if (!SalesMarket.orderQueue.contains(order)) {
            SalesMarket.orderQueue.add(order);
            response.setPerformative(ACLMessage.AGREE);
            msgObj = new MessageObject(response, orderText);
            System.out.println(msgObj.getReceivedMessage());

            // if agent agrees it starts executing request
            myAgent.addBehaviour(new AskForOrderBehaviour(this, dataStore));
        } else {
            response.setPerformative(ACLMessage.REFUSE);
            msgObj = new MessageObject(response, orderText);
            System.out.println(msgObj.getReceivedMessage());

        }
        return response;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

        // result of request to sales market
        // if agent agrees to request
        // after executing, it should send failure of inform
        ACLMessage inform = request.createReply();
        inform.setContent(request.getContent());
        inform.setPerformative(ACLMessage.INFORM);
        inform.setSender(new AID(("AgentSalesMarket"), AID.ISLOCALNAME));
        msgObj = new MessageObject(inform, orderText);
        System.out.println(msgObj.getReceivedMessage());


        return inform;
    }

    public ACLMessage getRequest() {
        return (ACLMessage) getDataStore().get(REQUEST_KEY);
    }

    public void setResponse(ACLMessage response) {
        getDataStore().put(RESPONSE_KEY, response);
    }

    public void setResult(ACLMessage result) {
        getDataStore().put(RESULT_NOTIFICATION_KEY, result);
    }
}