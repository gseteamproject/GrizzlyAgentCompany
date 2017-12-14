package procurementBehaviours;

import basicAgents.Procurement;
import basicClasses.Order;
import basicClasses.OrderPart;
import basicClasses.Product;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class CheckMaterialStorage extends OneShotBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -4869963544017982955L;
    private String requestedMaterial;

    private ACLMessage request;

    public CheckMaterialStorage(Agent a, ACLMessage msg) {
        super(a);
        requestedMaterial = msg.getContent();
        request = msg;
    }

    @Override
    public void action() {
        Order order = Order.gson.fromJson(requestedMaterial, Order.class);

        Procurement.isInMaterialStorage = true;
        boolean isInQueue = false;

        // check if this order is not in queue yet
        isInQueue = Procurement.procurementQueue.contains(order);

        // part of order, that needs to be produced
        Order orderToBuy = new Order();
        orderToBuy.id = order.id;

        for (OrderPart orderPart : order.orderList) {
            Product productToCheck = orderPart.getProduct();

            String color = productToCheck.getColor();
            Double size = productToCheck.getSize();

            int amount = orderPart.getAmount();

            System.out.println("ProcurementAgent: Asking materialStorage about " + orderPart.getTextOfOrderPart());

            int paintAmountInMS = Procurement.materialStorage.getAmountOfPaint(color);
            int stoneAmountInMS = Procurement.materialStorage.getAmountOfStones(size);

            if (paintAmountInMS >= amount && stoneAmountInMS >= amount) {
                if (Procurement.isInMaterialStorage) {
                    Procurement.isInMaterialStorage = true;
                }
                System.out.println("ProcurementAgent: I say that materials for " + orderPart.getTextOfOrderPart()
                        + " are in materialStorage");
            } else {
                // need to describe multiple statements to check every material
                Procurement.isInMaterialStorage = false;

                // creating new instance of OrderPart to change its amount
                OrderPart paintOrderPart = new OrderPart(orderPart.getProduct().getPaint());
                OrderPart stoneOrderPart = new OrderPart(orderPart.getProduct().getStone());

                paintOrderPart.setAmount(amount - paintAmountInMS);
                stoneOrderPart.setAmount(amount - stoneAmountInMS);

                System.out.println("paintOrderPart.getAmount() " + paintOrderPart.getAmount());

                if (paintOrderPart.getAmount() > 0) {
                    orderToBuy.orderList.add(paintOrderPart);
                }
                if (stoneOrderPart.getAmount() > 0) {
                    orderToBuy.orderList.add(stoneOrderPart);
                }
            }
        }

        if (!isInQueue && orderToBuy.orderList.size() > 0) {
            String testGson = Order.gson.toJson(orderToBuy);
            ACLMessage agree = (ACLMessage) request.clone();
            agree.setContent(testGson);;

            // add order to queue
            Procurement.procurementQueue.add(order);

            System.out.println("ProcurementAgent: send info to ProcurementMarket to buy materials for "
                    + orderToBuy.getTextOfOrder());
            myAgent.addBehaviour(new AskForAuction(myAgent, agree, request));
        }
    }
}