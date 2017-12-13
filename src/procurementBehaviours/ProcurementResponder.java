package procurementBehaviours;

import basicAgents.Procurement;
import basicClasses.Order;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

public class ProcurementResponder extends AchieveREResponder {

    /**
     * 
     */
    private static final long serialVersionUID = -5804509731381843266L;
    private String orderText;

    public ProcurementResponder(Agent a, MessageTemplate mt, DataStore dataStore) {
        super(a, mt, dataStore);

        registerHandleRequest(new DecisionBehaviour(this, dataStore));
        registerPrepareResultNotification(new ActivityBehaviour(this, dataStore));
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

//    @Override
//    protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
//        // Selling reacts on SalesMarket's request
//
//        orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();
//
//        // Agent should send agree or refuse
//        // TODO: Add refuse answer (some conditions should be added)
//        ACLMessage agree = request.createReply();
//        agree.setContent(request.getContent());
//        agree.setPerformative(ACLMessage.AGREE);
//
//        if (request.getConversationId() == "Materials") {
//            System.out.println("ProcurementAgent: [request] ProductionAgent asks for materials for " + orderText);
//            System.out.println("ProcurementAgent: [agree] I will check materialStorage for materials for " + orderText);
//            myAgent.addBehaviour(new CheckMaterialStorage(myAgent, agree));
//        } else if (request.getConversationId() == "Take") {
//            System.out.println("ProcurementAgent: [request] ProductionAgent wants to get materials for " + orderText
//                    + " from materialStorage");
//            System.out.println(
//                    "ProcurementAgent: [agree] I will give you materials for " + orderText + " from materialStorage");
//            myAgent.addBehaviour(new GiveMaterialToProduction(myAgent, agree));
//        }
//
//        return agree;
//    }
//
//    @Override
//    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
//        // if agent agrees to request
//        // after executing, it should send failure of inform
//
//        // in case of inform product will be taken from warehouse
//        // in case of failure product will be produced
//        ACLMessage reply = request.createReply();
//        reply.setContent(request.getContent());
//
//        if (Procurement.isInMaterialStorage) {
//            reply.setPerformative(ACLMessage.INFORM);
//        } else {
//            reply.setPerformative(ACLMessage.FAILURE);
//        }
//        return reply;
//    }
}