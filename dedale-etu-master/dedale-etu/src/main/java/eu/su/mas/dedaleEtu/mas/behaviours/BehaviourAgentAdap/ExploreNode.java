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
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentEvolutif;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;



public class ExploreNode extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private int exitValue;

	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;

	private List<String> list_agentNames;
	private List<Couple<String,Location>> posAgent;
    private String mode;

/**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public ExploreNode(final AbstractDedaleAgent myagent,MapRepresentation mymap,List<String> agentNames,List<Couple<String,Location>> posAgent,String mode) {
		super(myagent);
		this.myMap=mymap;
		this.list_agentNames=agentNames;
		this.posAgent=posAgent;
        this.mode=mode;
	}

	@Override
	public void action() {
        if (this.mode==null){
			((AgentAdaptatif)(this.myAgent)).setMode("Explo");
		}
        this.mode=((AgentAdaptatif)(this.myAgent)).getMode();

        if(this.myMap==null) {
			this.myMap=((AgentAdaptatif)(this.myAgent)).getMyMap();
		}
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		Couple<String,Location> myCouple=new Couple<>(this.myAgent.getLocalName(), myPosition);

		this.posAgent=((AgentAdaptatif)(this.myAgent)).getPosAgent();
		((AgentAdaptatif)(this.myAgent)).setPosAgent(myCouple);
		

		if (myPosition!=null){
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition

			try {
				this.myAgent.doWait(100);
			} catch (Exception e) {
				e.printStackTrace();
			}

			this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);

            String nextNodeId=null;
            int tmp=-1;
            Boolean stenchdetected=false;
			for (int i=0;i<lobs.size();i++){
				Location accessibleNode=lobs.get(i).getLeft();
				this.myMap.addNewNode(accessibleNode.getLocationId());

				if (myPosition.getLocationId()!=accessibleNode.getLocationId() && !this.myMap.getCloseNodes().contains(accessibleNode.getLocationId())) {
					this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
                }
                if (lobs.get(i).getRight().size()>0 && lobs.get(i).getRight().get(0).getLeft().equals(Observation.STENCH) && !stenchdetected){
                    tmp=i;
                    stenchdetected=true;
                }
			}
            if (!stenchdetected && !this.myMap.hasOpenNode()){
                int n = (int)(Math.random()*(lobs.size()));
				Location balade=lobs.get(n).getLeft();
				nextNodeId=balade.getLocationId();
                

            }
            if (!stenchdetected && this.myMap.hasOpenNode()){
                if (nextNodeId==null){
                    nextNodeId=this.myMap.getShortestPathToClosestOpenNode(myPosition.getLocationId()).get(0);
                }
            }

            if(stenchdetected){
                
                ((AgentAdaptatif)(this.myAgent)).setMode("Chasse");
                
                nextNodeId=lobs.get(tmp).getLeft().getLocationId();
            }
			((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
			
    
	    }
    }

}
