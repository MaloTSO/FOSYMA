package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils.Null;

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
	
	private static final long serialVersionUID = 9088209402507795399L;	
	private int exitValue;

	public Receiver(final AbstractDedaleAgent myagent,int max) {
		super(myagent);
		exitValue=max;
	}
    public void action(){
		exitValue=0;
	
		try {
			this.myAgent.doWait(100);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("Communication"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);

		if (msg!=null){
			
			exitValue=1;
		}
    }
	public int onEnd() {
		return exitValue;
	}
    
}
