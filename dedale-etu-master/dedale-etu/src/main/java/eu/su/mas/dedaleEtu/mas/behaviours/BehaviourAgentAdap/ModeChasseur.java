package eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentAdaptatif;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentEvolutif;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


public class ModeChasseur extends OneShotBehaviour {

	private static final long serialVersionUID = 8567689731496787661L;

	private MapRepresentation myMap;
	private List<Couple<String,Location>> posAgent;
	private int counter;
    private String mode;
	
/**
 * 
 * 
 * @param myagent reference to the agent we are adding this behaviour to
 * @param myMap known map of the world the agent is living in
 * @param agentNames name of the agents to share the map with
 */
	public ModeChasseur(final AbstractDedaleAgent myagent, MapRepresentation myMap, List<Couple<String,Location>> posAgent,String mode) {
		super(myagent);
		this.myMap=myMap;
		this.counter=0;
		this.posAgent=posAgent;
        this.mode=mode;
	}

	@Override
	public void action() {

		if (this.mode==null){
			((AgentAdaptatif)(this.myAgent)).setMode("Chasse");
		}	
		this.mode=((AgentAdaptatif)(this.myAgent)).getMode();

		if(this.myMap == null) {
			this.myMap=((AgentAdaptatif)(this.myAgent)).getMyMap();
		}
		


		
		Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();
		Couple<String,Location> myCouple=new Couple<>(this.myAgent.getLocalName(), myPosition);

		this.posAgent=((AgentAdaptatif)(this.myAgent)).getPosAgent();

		
		if (myPosition!=null){

			
			for (int i=0; i< this.posAgent.size();i++){
				if(this.posAgent!=null && this.posAgent.get(i).getLeft().equals(this.myAgent.getLocalName()) && !myPosition.equals(this.posAgent.get(i).getRight())){
					this.counter=0;
				}
			}
			List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();
			for (int i=0;i<this.posAgent.size();i++){
				if (myPosition.equals(this.posAgent.get(i).getRight()) && this.posAgent.get(i).getLeft().equals("Golem")){
					this.posAgent.remove(this.posAgent.get(i));
				}
			}


			int cpt=0;
			for (int i=0;i<lobs.size();i++){
				if (lobs.get(i).getRight().size()!=0 && lobs.get(i).getRight().get(0).getLeft().equals(Observation.STENCH)){
					cpt++;
				}
			}
		
			Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();

			while (iter.hasNext()){
				Location node=iter.next().getLeft();
				for (int j=0; j<this.posAgent.size();j++){
					if (!(this.posAgent.get(j).getLeft().equals("Golem")) && node.equals(this.posAgent.get(j).getRight()) && (cpt>2 || cpt==0)){
						iter.remove();
						break;
					}
				}
			}

			((AgentAdaptatif)(this.myAgent)).setPosAgent(myCouple);

			try {
				this.myAgent.doWait(10);
			} catch (Exception e) {
				e.printStackTrace();
			}

			String nextNodeId=null;
			if (lobs.size()==0){
				nextNodeId=myPosition.getLocationId();
			}

			else{

				if (lobs.size()>1 && lobs.get(0).getLeft().equals(myPosition) && (cpt>1 || cpt==0)){
					lobs.remove(0);
				}
				int tmp=-1;
				int emp=-1;
				for (int j=0;j<this.posAgent.size();j++){
					if(this.posAgent.get(j).getLeft().equals("Golem")){
						emp=j;
					}
				}

				for (int i=0; i<lobs.size();i++){
					if (lobs.get(i).getRight().size()!=0 && lobs.get(i).getRight().get(0).getLeft().equals(Observation.STENCH)){
						if (emp!=-1 && lobs.get(i).getLeft().equals(this.posAgent.get(emp).getRight())){
							tmp=i;
						}
						if (tmp==-1){
							tmp=i;
						}

					}
				}

				if (tmp==-1){
					int n = (int)(Math.random()*(lobs.size()));
					Location balade=lobs.get(n).getLeft();
					nextNodeId=balade.getLocationId();
				}
				else{	
					Location accessibleNode=lobs.get(tmp).getLeft();
					nextNodeId=accessibleNode.getLocationId();
				}

			}
			if (!((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId))){
				Location posGolem=new gsLocation(nextNodeId);
				Couple<String,Location> coupleGolem=new Couple<>("Golem", posGolem);
				((AgentAdaptatif)(this.myAgent)).setPosAgent(coupleGolem);
				this.posAgent=((AgentAdaptatif)(this.myAgent)).getPosAgent();

				
				for (int i=0; i<this.posAgent.size();i++){
					
					if (this.posAgent.get(i).getLeft().equals(coupleGolem.getLeft()) && this.posAgent.get(i).getRight().equals(posGolem)){
						this.counter++;
					}
				}

				
			}

			((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
			

		}

	}
}
	

