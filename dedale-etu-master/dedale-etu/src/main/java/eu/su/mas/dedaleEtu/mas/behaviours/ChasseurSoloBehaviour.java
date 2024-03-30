package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.behaviours.ShareMapBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.OneShotBehaviour; 
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage; 
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
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
public class ChasseurSoloBehaviour extends TickerBehaviour {

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
	public ChasseurSoloBehaviour(final AbstractDedaleAgent myagent,MapRepresentation myMap,long period) {
		super(myagent,period);
		this.myMap=myMap;

	}

	@Override
	protected void onTick() {

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
			
			int i=0;
		
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			while(iter.hasNext()){
				Couple<Location, List<Couple<Observation, Integer>>> coupleSuiv=iter.next();
				if(coupleSuiv.getRight().get(i).getLeft().equals(Observation.STENCH)){
					Location accessibleNode=coupleSuiv.getLeft();
					boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
					if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
						this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
						if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
					}
				}
			}
			if (nextNodeId==null){
				int n = (int)(Math.random()*(lobs.size()+1));
				Location balade=lobs.get(n).getLeft();
				boolean isNewNode=this.myMap.addNewNode(balade.getLocationId());
				if (myPosition.getLocationId()!=balade.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), balade.getLocationId());
					if (nextNodeId==null && isNewNode) nextNodeId=balade.getLocationId();
				}
			}

		}

	}
}
