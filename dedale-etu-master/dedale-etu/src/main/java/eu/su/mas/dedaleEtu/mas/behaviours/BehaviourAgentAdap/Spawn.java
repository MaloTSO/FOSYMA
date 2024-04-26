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



public class Spawn extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private MapRepresentation myMap;
	private List<Couple<String,Location>> posAgent;


    /**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public Spawn(final AbstractDedaleAgent myagent,MapRepresentation mymap,List<Couple<String,Location>> posAgent) {
		super(myagent);
		this.myMap=mymap;
		this.posAgent=posAgent;
	}

	@Override
	public void action() {

        ((AgentAdaptatif)(this.myAgent)).setMyMap(new MapRepresentation());
        this.myMap=((AgentAdaptatif)(this.myAgent)).getMyMap(); 

		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		Couple<String,Location> myCouple=new Couple<>(this.myAgent.getLocalName(), myPosition);

		this.posAgent=((AgentAdaptatif)(this.myAgent)).getPosAgent();
		((AgentAdaptatif)(this.myAgent)).setPosAgent(myCouple);

		if (myPosition!=null){
			try {
				this.myAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.myMap.addNewNode(myPosition.getLocationId());
		}
    }
}