package eu.su.mas.dedaleEtu.mas.behaviours.exploreur;

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

public class PingSomeone extends OneShotBehaviour {
	private static final long serialVersionUID = 9088209402507795288L;	
	private List<String> receivers;
	private int exitValue;


	public PingSomeone(final AbstractDedaleAgent myagent,List<String> receivers,int max) {
		super(myagent);
		this.receivers=receivers;
		exitValue=max;

	}
	
	public void action(){

		exitValue=0;
		try {
			this.myAgent.doWait(100);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("UselessProtocol"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		final ACLMessage msgR = this.myAgent.receive(msgTemplate);
		
		if (msgR != null) {		
				final ACLMessage msgResult = new ACLMessage(ACLMessage.INFORM);
				msgResult.setProtocol("Communication");
				msgResult.setSender(this.myAgent.getAID());
				msgResult.addReceiver(new AID(msgR.getSender().getLocalName(), AID.ISLOCALNAME));  	
				msgResult.setContent("Yes i'm "+this.myAgent.getLocalName());
				this.myAgent.send(msgResult);
				exitValue=1;
		}

		else{

			
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setProtocol("UselessProtocol");
			msg.setSender(this.myAgent.getAID());
			msg.setContent("Hello, there is someone");
			for (String agentName : receivers) {
				msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
			}
					
			((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		}
	}

	public int onEnd() {
		return exitValue;
	}
}
