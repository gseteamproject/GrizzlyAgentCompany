package procurementBehaviours;

import java.util.List;

import basicAgents.ProcurementMarket;
import basicClasses.Order;
import basicClasses.OrderPart;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;

public class AskForAuction extends OneShotBehaviour {

    private static final long serialVersionUID = -6100676860519799721L;
    private ACLMessage materialToBuy, request;
    private Order order;
    private String orderText;
    public static int partsCount;

    public AskForAuction(Agent a, ACLMessage msg, ACLMessage request) {
        super(a);
        this.materialToBuy = msg;
        this.request = request;
    }

    @Override
    public void action() {
        order = Order.gson.fromJson(materialToBuy.getContent(), Order.class);
        orderText = order.getTextOfOrder();
        partsCount = order.orderList.size();
        System.out.println("ProcurementAgent: Sending an info to ProcurementMarket to buy materials for " + orderText);
      
        for (OrderPart orderPart : order.orderList) {            
            System.out.println("mne nado: " + orderPart.getTextOfOrderPart());
            String requestedAction = "Order";
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setConversationId(requestedAction);
            msg.addReceiver(new AID(("AgentProcurementMarket"), AID.ISLOCALNAME));
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setContent(orderPart.getTextOfOrderPart());

            System.out.println("\nlooking for agents with procurement service = "
                    + orderPart.getPart().getClass().getSimpleName());
            List<AID> agents = ProcurementMarket.findAgents(myAgent, orderPart.getPart().getClass().getSimpleName());
            if (!agents.isEmpty()) {
                System.out.println("agents providing service are found. trying to get infromation...");
                myAgent.addBehaviour(new RequestToBuy(agents, orderPart));
                // TODO: Check if material is really bought
            } else {
                System.out.println("no agents providing service are found");
            }
        }
    }
}