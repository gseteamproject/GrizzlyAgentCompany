package basicAgents;

import java.util.Random;

import communication.Communication;
import communication.MessageObject;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class StoneSeller extends Agent {

    private static final long serialVersionUID = -7418692714860762106L;
    private MessageObject msgObj;

    @Override
    protected void setup() {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName("Stone");
        serviceDescription.setType("procurement-service");
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());
        agentDescription.addServices(serviceDescription);
        try {
            DFService.register(this, agentDescription);
        } catch (FIPAException exception) {
            exception.printStackTrace();
        }

        /* registering Behaviours to react for different types of messages */
        addBehaviour(new HandleAcceptProposal());
        addBehaviour(new HandleCallForProposal());
        /*
         * 
         */
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    class HandleCallForProposal extends CyclicBehaviour {
        private static final long serialVersionUID = 2429876704345890795L;

        @Override
        public void action() {
            /*
             * only messages containing CALL-FOR-PROPOSAL in "performative" slot will be
             * processed
             */
            MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = receive(msgTemplate);
            if (msg != null) {
                /* create reply for incoming message with price at "content" slot */
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.PROPOSE);
                int price = new Random().nextInt(100);
                reply.setContent(String.valueOf(price));

                msgObj = new MessageObject("AgentProcurementMarket", "Stone price is "+ price);
                Communication.server.sendMessageToClient(msgObj);

/*
                System.out.println(String.format("Stone: my price is %d", price));
*/
                /* send reply for incoming message */
                send(reply);
            } else {
                /* wait till there is message matching template in message-queue */
                block();
            }
        }
    }

    class HandleAcceptProposal extends CyclicBehaviour {
        private static final long serialVersionUID = 8759104857697556076L;

        @Override
        public void action() {
            /*
             * only message containg ACCEPT-PROPOSAL in "performative" slot will be
             * processed
             */
            MessageTemplate msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
            ACLMessage msg = receive(msgTemplate);
            if (msg != null) {
                /* create reply for incoming message */
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);

                msgObj = new MessageObject("AgentProcurementMarket" , "Delivering stone(s).");
                Communication.server.sendMessageToClient(msgObj);

/*
                System.out.println("delivering...");
*/
                /* send reply for incoming message */
                send(reply);
            } else {
                /* wait till there is message matching template in message-queue */
                block();
            }

        }
    }
}