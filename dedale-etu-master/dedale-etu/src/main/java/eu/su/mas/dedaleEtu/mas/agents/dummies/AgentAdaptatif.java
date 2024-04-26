package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;
import jade.core.behaviours.FSMBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap.Communication;
import eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap.CommunicationMap;

import eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap.Spawn;
import eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap.ExploreNode;
import eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap.ModeChasseur;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.Behaviour;

public class AgentAdaptatif extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap=null;
    private List<Couple<String,Location>> posAgent = new ArrayList<>();
	private String mode;


	private static final String A="spawn";
	private static final String C="Communication";
	private static final String D="CommunicationMap";
	private static final String E="ExploreMap";
	private static final String F="ModeChasse";

	protected void setup(){

		super.setup();

		//get the parameters added to the agent at creation (if any)
		final Object[] args = getArguments();

		List<String> list_agentNames=new ArrayList<String>();

		if(args.length==0){
			System.err.println("Error while creating the agent, names of agent to contact expected");
			System.exit(-1);
		}else{
			int i=2;// WARNING YOU SHOULD ALWAYS START AT 2. This will be corrected in the next release.
			while (i<args.length) {
				list_agentNames.add((String)args[i]);
				i++;
			}
		}

		FSMBehaviour fsm =new FSMBehaviour(this);

		fsm.registerFirstState(new Spawn(this,this.myMap,this.posAgent),A);
		fsm.registerState(new Communication(this,list_agentNames),C);
		fsm.registerState(new CommunicationMap(this,this.myMap,this.posAgent,this.mode,0),D);
		fsm.registerState(new ExploreNode(this,this.myMap,list_agentNames,this.posAgent,this.mode),E);
		fsm.registerState(new ModeChasseur(this,this.myMap,this.posAgent,this.mode),F);

		fsm.registerDefaultTransition(A,C);
		fsm.registerDefaultTransition(C,D);
		fsm.registerTransition(D,E,0);
		fsm.registerTransition(D,F,1);

		fsm.registerDefaultTransition(E,C);
		fsm.registerDefaultTransition(F,C);

		List<Behaviour> lb=new ArrayList<Behaviour>();

		lb.add(fsm);
		addBehaviour(new startMyBehaviours(this,lb));

		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	public void setMyMap(MapRepresentation m) {
		this.myMap=m;
	}
	public MapRepresentation getMyMap() {
		return this.myMap;
	}

	public String getMode(){
		return this.mode;
	}

	public void setMode(String m){
		this.mode=m;
	}
    public void setPosAgent(Couple<String,Location> m) {

		
		for (int i = 0; i<this.posAgent.size(); i++)  
		{
			if(this.posAgent.get(i).getLeft().equals(m.getLeft()))
			{
				this.posAgent.remove(this.posAgent.get(i));
			}
		}
		this.posAgent.add(m);
	}
	public List<Couple<String,Location>> getPosAgent() {
		return this.posAgent;
	}

}