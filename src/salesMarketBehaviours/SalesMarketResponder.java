package salesMarketBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
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




        System.out.println("SalesMarketAgent: [request] Customer orders a " + orderText);


        // Agent should send agree or refuse
        ACLMessage response;
        response = request.createReply();
        response.setContent(request.getContent());
        if (!SalesMarket.orderQueue.contains(order)) {
            SalesMarket.orderQueue.add(order);
            response.setPerformative(ACLMessage.AGREE);
            System.out.println("SalesMarketAgent: [agree] I will make an order of " + orderText);

            // if agent agrees it starts executing request
            myAgent.addBehaviour(new AskForOrderBehaviour(this, dataStore));
        } else {
            response.setPerformative(ACLMessage.REFUSE);
            System.out.println("SalesMarketAgent: [refuse] Following order is already in queue: " + orderText);
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
        System.out.println("SalesMarketAgent: [inform] I ordered a " + orderText);


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