package productionBehaviours;

import basicAgents.SalesMarket;
import basicClasses.Order;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class WaitingProcurementMessageResponder extends AchieveREResponder {

    /**
     * 
     */
    private static final long serialVersionUID = 7386418031416044376L;
    private String orderText;
    private DataStore dataStore;
    private ProductionResponder interactionBehaviour;

    public WaitingProcurementMessageResponder(ProductionResponder interactionBehaviour, MessageTemplate mt,
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

        System.out.println(
                "ProductionAgent: received [inform] order " + orderText + " is ready for collection from procurementMarket");

        response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);
        System.out.println("ProductionAgent: [agree] I will take " + orderText);

        myAgent.addBehaviour(new GetFromStorageBehaviour(interactionBehaviour, 2000, dataStore));

        return response;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {

        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

        ACLMessage inform = request.createReply();
        inform.setContent(request.getContent());
        inform.setPerformative(ACLMessage.INFORM);
        System.out.println("SalesMarketAgent: [inform] I asked to take " + orderText);

        return inform;
    }
}