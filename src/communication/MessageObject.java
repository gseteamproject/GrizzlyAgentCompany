package communication;

import jade.lang.acl.ACLMessage;

public class MessageObject {


    private ACLMessage aclmsg;
    private String sender;
    private String receiver;
    private String orderText;
    private String performative;
    private String message;


    public MessageObject (ACLMessage acl, String orderText){
        this.aclmsg = acl;
        this.orderText = orderText;
        this.setPerformative();
        this.setSender();
        this.setReceiver();

    }
    public MessageObject (String manualSender, String manualMessage){
        this.message = manualSender + manualMessage;
    }

    public ACLMessage getAclmsg() {
        return aclmsg;
    }

    public void setAclmsg(ACLMessage aclmsg) {
        this.aclmsg = aclmsg;
    }

    public String getSender() {
        return sender;
    }

    public void setSender() {
        this.sender = this.aclmsg.getSender().toString().split("name ")[1].split("@")[0];
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver() {
        this.receiver = this.aclmsg.getAllReceiver().next().toString().split("name ")[1].split("@")[0];
    }

    public String getOrderText() {
        return orderText;
    }

    public void setOrderText(String message) {
        this.orderText = message;
    }

    public String getPerformative() {
        return performative;
    }

    public void setPerformative() {

        switch (this.aclmsg.getPerformative()) {
            case 0: this.performative = "ACCEPT_PROPOSAL";
                break;
            case 1: this.performative = "AGREE";
                break;
            case 2: this.performative = "CANCEL";
                break;
            case 6: this.performative = "FAILURE";
                break;
            case 7: this.performative = "INFORM";
                break;
            case 14: this.performative = "REFUSE";
                break;
            case 17: this.performative = "REQUEST";
                break;
            default: this.performative = "UNKNOWN";
                break;
        }

    }
    public String getMessage (){
        return this.message;
    }
    public String receivedMessage (){
        String msg;
        msg = this.receiver + " received a Message of Type [" + this.performative + "] from " + this.sender;
        return msg;

    }
    //TODO
    public String actionMessage(){
        String msg;
        msg = this.receiver + ": ";
        switch (this.performative){
            case "AGREE": msg+= " SOME MORE INFORMATION THAT SOME AGENT AGREES WITH SOMETHING";
                break;
            default: break;
        }

        return msg;
    }




}