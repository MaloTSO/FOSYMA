package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;


import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentEvolutif;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;



/**
 * This behaviour receives nb integer values, sum them and then send the result back to the agent whose name is given in parameters
 * 
 * In order to stay relatively simple for now, this behaviour is not fully generic (it is not declared as being part of a protocol) 
 * and do both the receiving and the sending processes.
 * 
 * @author hc
 *
 */
public class ReceiverPing extends OneShotBehaviour {
	
	private static final long serialVersionUID = 9088209402507795299L;	

	private List<Couple<String,Location>> posAgent;
	

	public ReceiverPing(final AbstractDedaleAgent myagent,List<Couple<String,Location>> posAgent) {
		super(myagent);
		this.posAgent=posAgent;
	}

	public void action(){

		try {
			this.myAgent.doWait(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(this.posAgent==null) {
			this.posAgent=((AgentEvolutif)(this.myAgent)).getPosAgent();
		}

		//1) create the reception template (inform + name of the sender)
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("Position-Share"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		//2) get the message
		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {	
			Location posi= new gsLocation(msg.getContent());
			Couple<String,Location> myCouple=new Couple<>(msg.getSender().getLocalName(), posi);
			((AgentEvolutif)(this.myAgent)).setPosAgent(myCouple);					
			final ACLMessage msgResult = new ACLMessage(ACLMessage.INFORM);
			msgResult.setProtocol("Position-Share");
			msgResult.setSender(this.myAgent.getAID());
			msgResult.addReceiver(new AID(msg.getSender().getLocalName(), AID.ISLOCALNAME));  	
			msgResult.setContent(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString());
			this.myAgent.send(msgResult);
			
		}
	}
}


