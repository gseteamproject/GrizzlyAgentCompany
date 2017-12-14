package procurementBehaviours;

import basicAgents.Procurement;
import basicClasses.Order;
import basicClasses.Paint;
import basicClasses.Product;
import basicClasses.Stone;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class GiveMaterialToProduction extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -1386982676634257780L;
    private String materialsToGive;
    private String orderText;
    public static boolean isGiven = false;

    public GiveMaterialToProduction(Agent a, ACLMessage msg) {
        super(a);
        materialsToGive = msg.getContent();
    }

    @Override
    public void action() {
        Order order = Order.gson.fromJson(materialsToGive, Order.class);
        orderText = order.getTextOfOrder();
        System.out.println("ProcurementAgent: Taking " + orderText + " from materialStorage");

        for (Product product : order.getProducts()) {
            Procurement.materialStorage.remove(new Paint(product.getColor()));
            Procurement.materialStorage.remove(new Stone(product.getSize()));
        }
    }
}