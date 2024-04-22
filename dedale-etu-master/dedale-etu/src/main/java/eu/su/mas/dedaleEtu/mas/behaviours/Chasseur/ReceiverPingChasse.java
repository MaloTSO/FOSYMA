package eu.su.mas.dedaleEtu.mas.behaviours.Chasseur;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/**
 * This behaviour receives nb integer values, sum them and then send the result back to the agent whose name is given in parameters
 * 
 * In order to stay relatively simple for now, this behaviour is not fully generic (it is not declared as being part of a protocol) 
 * and do both the receiving and the sending processes.
 * 
 * @author hc
 *
 */
public class ReceiverPingChasse extends OneShotBehaviour {
	
	private static final long serialVersionUID = 9088209402507795299L;	
	

	public ReceiverPingChasse(final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	public void action(){

		try {
			this.myAgent.doWait(500);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		//1) create the reception template (inform + name of the sender)
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("UselessProtocol"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		//2) get the message
		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
		if (msg != null) {						
				final ACLMessage msgResult = new ACLMessage(ACLMessage.INFORM);
				msgResult.setProtocol("Communication");
				msgResult.setSender(this.myAgent.getAID());
				msgResult.addReceiver(new AID(msg.getSender().getLocalName(), AID.ISLOCALNAME));  	
				msgResult.setContent("Yes i'm "+this.myAgent.getAID());
				this.myAgent.send(msgResult);
				
		}
	}
}
