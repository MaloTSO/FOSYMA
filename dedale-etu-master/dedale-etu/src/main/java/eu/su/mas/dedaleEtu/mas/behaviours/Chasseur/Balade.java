package eu.su.mas.dedaleEtu.mas.behaviours.Chasseur;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ShareMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.OneShotBehaviour; 
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage; 
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.ArrayList;
import javafx.beans.Observable;


/**
 * <pre>
 * This behaviour allows an agent to explore the environment and learn the associated topological map.
 * The algorithm is a pseudo - DFS computationally consuming because its not optimised at all.
 * 
 * When all the nodes around him are visited, the agent randomly select an open node and go there to restart its dfs. 
 * This (non optimal) behaviour is done until all nodes are explored. 
 * 
 * Warning, this behaviour does not save the content of visited nodes, only the topology.
 * Warning, the sub-behaviour ShareMap periodically share the whole map
 * </pre>
 * @author hc
 *
 */
public class Balade extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;



	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;
	
    


/**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public Balade(final AbstractDedaleAgent myagent,MapRepresentation myMap) {
		super(myagent);
		this.myMap=myMap;
	}

	@Override
	public void action() {
		


		if(this.myMap==null) {
			this.myMap= new MapRepresentation();
		}

		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=null){
			
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

			try {
				this.myAgent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);
			String nextNodeId=null;
			
			int n = (int)(Math.random()*(lobs.size()));
			if(n==0){
				n=1;
			}
			Location balade=lobs.get(n).getLeft();
            nextNodeId=balade.getLocationId();
			if (myPosition.getLocationId()!=lobs.get(n).getLeft().getLocationId()) {
				this.myMap.addEdge(myPosition.getLocationId(), lobs.get(n).getLeft().getLocationId());	
			}
            ((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
		}
		
				
				

	}
}
