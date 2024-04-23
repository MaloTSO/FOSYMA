package eu.su.mas.dedaleEtu.mas.behaviours;
 
import java.io.IOException;
import java.util.List;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SendPosition extends OneShotBehaviour {
	private static final long serialVersionUID = 9088209402507795288L;	
	private List<String> receivers;
    private List<Couple<String,Location>> posAgent;


	public SendPosition(final AbstractDedaleAgent myagent,List<String> receivers,List<Couple<String,Location>> posAgent) {
		super(myagent);
		this.receivers=receivers;
        this.posAgent=posAgent;

	}
	
	public void action(){

		try {
			this.myAgent.doWait(200);
		} catch (Exception e) {
			e.printStackTrace();
		}

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("Position-Share");
        msg.setSender(this.myAgent.getAID());

		
        msg.setContent(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString());
        for (String agentName : receivers) {
            msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
        }
        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
	}
}


