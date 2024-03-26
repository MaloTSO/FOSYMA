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

	private int exitValue;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;

	private List<String> list_agentNames;

/**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public ChasseurSoloBehaviour(final AbstractDedaleAgent myagent,long period,MapRepresentation myMap,List<String> agentNames) {
		super(myagent,period);
		this.myMap=myMap;
		this.list_agentNames=agentNames;
		
	}

	@Override
	public void onTick() {

		if(this.myMap==null) {
			this.myMap= new MapRepresentation();
		}

		//0) Retrieve the current position
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			/**
	         * Just added here to let you see what the agent is doing, otherwise he will be too quick
			 */
			try {
				this.myAgent.doWait(500);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//1) remove the current node from openlist and add it to closedNodes.
			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

			String nextNodeId=null;
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
			Couple<Location, List<Couple<Observation, Integer>>> coupleSuiv = iter.next();
			while(iter.hasNext()){
                if (coupleSuiv.getRight().get(0).getLeft().equals(Observation.STENCH)){

                    Location strenLocation=coupleSuiv.getLeft();
					boolean isNewNode=this.myMap.addNewNode(strenLocation.getLocationId());
					if (myPosition.getLocationId()!=strenLocation.getLocationId()) {
						this.myMap.addEdge(myPosition.getLocationId(), strenLocation.getLocationId());
					if (nextNodeId==null && isNewNode) nextNodeId=strenLocation.getLocationId();
					}

				}
			
			
				else{
					Location accessibleNode=coupleSuiv.getLeft();
					boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
					if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
						this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
						if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
					}
				}
			
			}

			//3) while openNodes is not empty, continues.
			if (!this.myMap.hasOpenNode()){
				//Explo finished
				
				System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done, behaviour removed.");
			}
		
			else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNodeId==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNodeId=this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId()).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
				}
				
				
				

				((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
			}

		}
	}

	@Override
	public int onEnd() {
		return exitValue;
	}

}
