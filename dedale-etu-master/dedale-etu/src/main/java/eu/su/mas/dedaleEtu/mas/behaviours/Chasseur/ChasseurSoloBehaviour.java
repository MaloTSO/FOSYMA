package eu.su.mas.dedaleEtu.mas.behaviours.Chasseur;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.dummies.ChasseurAgent;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ShareMapBehaviour;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


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
public class ChasseurSoloBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private int exitValue;

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
	public ChasseurSoloBehaviour(final AbstractDedaleAgent myagent, int max,MapRepresentation myMap) {
		super(myagent);
		this.myMap=myMap;
		exitValue=max;
	}

	@Override
	public void action() {
		
		if(this.myMap == null) {
			((ChasseurAgent)(this.myAgent)).setMyMap(new MapRepresentation());
			this.myMap=((ChasseurAgent)(this.myAgent)).getMyMap();
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
			int j=0;
			int i=0;
			int tmp=-1;

			
			

			while (i<lobs.size()){
				
				Location accessibleNode=lobs.get(i).getLeft();
				this.myMap.addNewNode(accessibleNode.getLocationId());

				if(lobs.get(i).getRight().size()!=0 && lobs.get(i).getRight().get(j).getLeft().equals(Observation.STENCH)){
					exitValue=1;
					tmp=i;
					
				}
				

				i++;


			}
			if(tmp!=-1){

				this.myMap.addNewNode(lobs.get(tmp).getLeft().getLocationId());
				if (myPosition.getLocationId()!=lobs.get(tmp).getLeft().getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), lobs.get(tmp).getLeft().getLocationId());
				
				}
			
			}

		}
	}
	@Override
	public int onEnd() {
		return exitValue;
	}

}
