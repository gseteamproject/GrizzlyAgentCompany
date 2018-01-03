package procurementMarketBehaviours;

import java.util.ArrayList;
import java.util.List;

import basicClasses.Order;
import basicClasses.OrderPart;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class AuctionInitiator extends OneShotBehaviour {

    private static final long serialVersionUID = -6100676860519799721L;
    private ACLMessage materialToBuy;
    private Order order;
    private String orderText;
    private ProcurementMarketResponder interactionBehaviour;
    public static int partsCount;

    public AuctionInitiator(ProcurementMarketResponder interactionBehaviour) {
        super(interactionBehaviour.getAgent());
        this.interactionBehaviour = interactionBehaviour;
        this.materialToBuy = interactionBehaviour.getRequest();
    }

    public static List<AID> findAgents(Agent a, String serviceName) {
        /* prepare service-search template */
        ServiceDescription requiredService = new ServiceDescription();
        requiredService.setName(serviceName);
        /*
         * prepare agent-search template. agent-search template can have several
         * service-search templates
         */
        DFAgentDescription agentDescriptionTemplate = new DFAgentDescription();
        agentDescriptionTemplate.addServices(requiredService);

        List<AID> foundAgents = new ArrayList<AID>();
        try {
            /* perform request to DF-Agent */
            DFAgentDescription[] agentDescriptions = DFService.search(a, agentDescriptionTemplate);
            for (DFAgentDescription agentDescription : agentDescriptions) {
                /* store all found agents in an array for further processing */
                foundAgents.add(agentDescription.getName());
            }
        } catch (FIPAException exception) {
            exception.printStackTrace();
        }

        return foundAgents;
    }

    @Override
    public void action() {
        order = Order.gson.fromJson(materialToBuy.getContent(), Order.class);
        orderText = order.getTextOfOrder();
        partsCount = order.orderList.size();
        System.out.println("ProcurementAgent: Sending an info to ProcurementMarket to buy materials for " + orderText);

        for (OrderPart orderPart : order.orderList) {
            // System.out.println("mne nado: " + orderPart.getTextOfOrderPart());
            String requestedAction = "Order";
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setConversationId(requestedAction);
            msg.addReceiver(new AID(("AgentProcurementMarket"), AID.ISLOCALNAME));
            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
            msg.setContent(orderPart.getTextOfOrderPart());

            System.out.println("\nlooking for agents with procurement service = "
                    + orderPart.getPart().getClass().getSimpleName());
            List<AID> agents = findAgents(myAgent, orderPart.getPart().getClass().getSimpleName());
            if (!agents.isEmpty()) {
                System.out.println("agents providing service are found. trying to get infromation...");
                myAgent.addBehaviour(new RequestToBuy(agents, interactionBehaviour, orderPart));
                // TODO: Check if material is really bought
            } else {
                System.out.println("no agents providing service are found");
            }
        }
    }
}