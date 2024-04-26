package eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentEvo;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentEvolutif;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.OneShotBehaviour; 
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage; 
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.ArrayList;
import javafx.beans.Observable;
import weka.core.pmml.jaxbbindings.False;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


public class BloqueGolem extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;



	/**
	 * Current knowledge of the agent regarding the environment
	 */
	
    private List<String> receivers;
    


/**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public BloqueGolem(final AbstractDedaleAgent myagent,List<String> receivers) {
		super(myagent);
        this.receivers=receivers;
	}

	@Override
	public void action() {
        try {
            this.myAgent.doWait(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("BloqueGolem");
        msg.setSender(this.myAgent.getAID());

        msg.setContent(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString());
        for (String agentName : receivers) {
            msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
        }
        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
    }

}