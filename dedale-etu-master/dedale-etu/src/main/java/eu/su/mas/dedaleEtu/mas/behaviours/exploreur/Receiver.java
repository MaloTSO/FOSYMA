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

public class Receiver extends OneShotBehaviour {
	
	private static final long serialVersionUID = 9088209402507795299L;	
	private int exitValue;

	public Receiver(int max) {
		super();
		exitValue=max;
	}
    public void action(){

		try {
			this.myAgent.doWait(500);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		//1) create the reception template (inform + name of the sender)
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("Communication"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		//2) get the message
		final ACLMessage msg = this.myAgent.receive(msgTemplate);
		
    }
    
}
