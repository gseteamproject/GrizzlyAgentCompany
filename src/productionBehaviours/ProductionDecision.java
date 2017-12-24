package productionBehaviours;

import basicClasses.Order;
import interactors.Decision;
import interactors.OrderDataStore;
import jade.lang.acl.ACLMessage;

public class ProductionDecision extends Decision {

    public ProductionDecision(OrderDataStore dataStore) {
        super(dataStore);
    }

    @Override
    public ACLMessage execute(ACLMessage request) {
        String orderText = Order.gson.fromJson(request.getContent(), Order.class).getTextOfOrder();

        System.out.println("ProductionAgent: [request] SellingAgent asks to produce " + orderText);
        // Agent should send agree or refuse
        // TODO: Add refuse answer (some conditions should be added)

        ACLMessage response = request.createReply();
        response.setContent(request.getContent());
        response.setPerformative(ACLMessage.AGREE);
        
        System.out.println("ProductionAgent: [agree] I will produce " + orderText);

        return response;
    }

}
