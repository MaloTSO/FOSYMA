package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentFaitTout;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
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

	private MapRepresentation myMap;
	private List<Couple<String,Location>> posAgent;
/**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public ChasseurSoloBehaviour(final AbstractDedaleAgent myagent, MapRepresentation myMap, List<Couple<String,Location>> posAgent) {
		super(myagent);
		this.myMap=myMap;
		this.posAgent=posAgent;
	}

	@Override
	public void action() {

		if(this.myMap == null) {
			this.myMap=((AgentFaitTout)(this.myAgent)).getMyMap();
		}

		
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		Couple<String,Location> myCouple=new Couple<>(this.myAgent.getLocalName(), myPosition);

		if (this.posAgent==null){
			((AgentFaitTout)(this.myAgent)).setPosAgent(myCouple);
			
		}

		this.posAgent=((AgentFaitTout)(this.myAgent)).getPosAgent();
		if (myPosition!=null){
			
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();


			this.posAgent=((AgentFaitTout)(this.myAgent)).getPosAgent();
			

			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();

			while (iter.hasNext()){
				Location node=iter.next().getLeft();
				for (int j=0; j<this.posAgent.size();j++){
					if (node.equals(this.posAgent.get(j).getRight())){
						iter.remove();
						break;
					}
				}
			}


			((AgentFaitTout)(this.myAgent)).setPosAgent(myCouple);

			try {
				this.myAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

			String nextNodeId=null;
			boolean stenchdetected = false;
			int i=0;
			int j=0;
			while (i<lobs.size() && stenchdetected== false){

				if (lobs.get(i).getRight().size()!=0 && lobs.get(i).getRight().get(j).getLeft().equals(Observation.STENCH)){
					stenchdetected=true;
				}
				else{
					i++;
				}

			}
			if (stenchdetected==false){
				int n = (int)(Math.random()*(lobs.size()));
				if(n==0 && lobs.size()>1){
					n=1;
				}
				Location balade=lobs.get(n).getLeft();
				nextNodeId=balade.getLocationId();
			}
			else{	
				Location accessibleNode=lobs.get(i).getLeft();
				nextNodeId=accessibleNode.getLocationId();
			}
			((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
		}

	}
}
