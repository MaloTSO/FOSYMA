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
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.OneShotBehaviour; 
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage; 
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.leap.ArrayList;
import javafx.beans.Observable;
import weka.core.pmml.jaxbbindings.False;

public class Suiveur extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;



	/**
	 * Current knowledge of the agent regarding the environment
	 */
	private MapRepresentation myMap;
	private int exitValue;
    


/**
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public Suiveur(final AbstractDedaleAgent myagent,MapRepresentation myMap,int max) {
		super(myagent);
		this.myMap=myMap;
		exitValue=max;
	}

	@Override
	public void action() {
		exitValue=0;
		if(this.myMap==null) {
			this.myMap=((AgentFaitTout)(this.myAgent)).getMyMap();
		}

		try {
			this.myAgent.doWait(100);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();

		if (myPosition!=null){
			
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();

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
				exitValue=1;
			}

			else{	
				Location accessibleNode=lobs.get(i).getLeft();
				if( this.myMap.getCloseNodes().contains(accessibleNode.getLocationId())){
					nextNodeId=accessibleNode.getLocationId();
				}
				else{
					boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
					if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
						this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
						if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
					}
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


