package eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentAdaptatif;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentFaitTout;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class finish extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;


	public finish(final AbstractDedaleAgent myagent) {
		super(myagent);
	}

	@Override
	public void action() {
        System.out.println(this.myAgent.getLocalName()+" a fini");

    }
}
