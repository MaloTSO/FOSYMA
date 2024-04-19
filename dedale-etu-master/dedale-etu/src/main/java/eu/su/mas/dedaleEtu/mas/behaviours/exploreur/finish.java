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

public class finish extends OneShotBehaviour{
	
	private static final long serialVersionUID = 8567689731496787771L;
	
	public finish(final AbstractDedaleAgent a) {
		super(a);
	}
	
	public void action() {
		System.out.println(this.myAgent.getLocalName()+" a terminé");
	}



}
