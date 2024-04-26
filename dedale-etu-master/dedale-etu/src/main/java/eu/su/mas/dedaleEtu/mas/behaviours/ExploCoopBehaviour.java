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
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentFaitTout;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



public class ExploCoopBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private int exitValue;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;

	private List<String> list_agentNames;
	private List<Couple<String,Location>> posAgent;
	private int cptBloque;
	private String nextNode;

/**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public ExploCoopBehaviour(final AbstractDedaleAgent myagent, int max,MapRepresentation mymap,List<String> agentNames,List<Couple<String,Location>> posAgent) {
		super(myagent);
		this.myMap=mymap;
		this.list_agentNames=agentNames;
		this.posAgent=posAgent;
		exitValue=max;
		cptBloque=0;
	}

	@Override
	public void action() {
		if(this.myMap==null) {
			((AgentFaitTout)(this.myAgent)).setMyMap(new MapRepresentation());
			this.myMap=((AgentFaitTout)(this.myAgent)).getMyMap();
		}

		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		Couple<String,Location> myCouple=new Couple<>(this.myAgent.getLocalName(), myPosition);

		this.posAgent=((AgentFaitTout)(this.myAgent)).getPosAgent();
		((AgentFaitTout)(this.myAgent)).setPosAgent(myCouple);
		

		if (myPosition!=null){
			//List of observable from the agent's current position
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			try {
				this.myAgent.doWait(200);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();

			// while (iter.hasNext()){
			// 	Location node=iter.next().getLeft();
			// 	for (int j=0; j<this.posAgent.size();j++){
			// 		if (node.equals(this.posAgent.get(j).getRight())){
			// 			iter.remove();
			// 			break;
			// 		}
			// 	}
			// }

			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

			//2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
			String nextNodeId=null;
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> itera=lobs.iterator();
			while(itera.hasNext()){
				Location accessibleNode=itera.next().getLeft();
				boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
				//the node may exist, but not necessarily the edge
				if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
					if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
				}
			}
			//3) while openNodes is not empty, continues.
			if (!this.myMap.hasOpenNode()){
				//Explo finished
				exitValue=0;
				System.out.println(this.myAgent.getLocalName()+" - Exploration successufully done, behaviour removed.");
			}else{
				//4) select next move.
				//4.1 If there exist one open node directly reachable, go for it,
				//	 otherwise choose one from the openNode list, compute the shortestPath and go for it
				if (nextNodeId==null){
					//no directly accessible openNode
					//chose one, compute the path and take the first step.
					nextNodeId=this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId()).get(0);//getShortestPath(myPosition,this.openNodes.get(0)).get(0);
					//System.out.println(this.myAgent.getLocalName()+"-- list= "+this.myMap.getOpenNodes()+"| nextNode: "+nextNode);
				}
				System.out.println(myAgent.getLocalName()+ " " + myPosition.getLocationId());
				System.out.println(myAgent.getLocalName()+ " " + this.nextNode);
				if(this.nextNode != null && myPosition.getLocationId()==this.nextNode)
				{
					cptBloque++;
					System.out.println(myAgent.getLocalName()+ " " + cptBloque);
				}

				this.nextNode = nextNodeId;

				

				((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
			}
		}
	}

	@Override
	public int onEnd() {
		return exitValue;
	}

}
