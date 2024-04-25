package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;
import jade.core.behaviours.FSMBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.Balade;
import eu.su.mas.dedaleEtu.mas.behaviours.ChasseurSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploCoopBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.PingSomeone;
//import eu.su.mas.dedaleEtu.mas.behaviours.ReceiveMap;
import eu.su.mas.dedaleEtu.mas.behaviours.Receiver;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiverPing;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceivePosition;
import eu.su.mas.dedaleEtu.mas.behaviours.SendPosition;
import eu.su.mas.dedaleEtu.mas.behaviours.ShareMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.Share;
import eu.su.mas.dedaleEtu.mas.behaviours.BloqueGolem;
import eu.su.mas.dedaleEtu.mas.behaviours.Suiveur;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;

import jade.core.behaviours.Behaviour;


/**
 * <pre>
 * ExploreCoop agent. 
 * Basic example of how to "collaboratively" explore the map
 *  - It explore the map using a DFS algorithm and blindly tries to share the topology with the agents within reach.
 *  - The shortestPath computation is not optimized
 *  - Agents do not coordinate themselves on the node(s) to visit, thus progressively creating a single file. It's bad.
 *  - The agent sends all its map, periodically, forever. Its bad x3.
 *  - You should give him the list of agents'name to send its map to in parameter when creating the agent.
 *   Object [] entityParameters={"Name1","Name2};
 *   ag=createNewDedaleAgent(c, agentName, ExploreCoopAgent.class.getName(), entityParameters);
 *  
 * It stops when all nodes have been visited.
 * 
 * 
 *  </pre>
 *  
 * @author hc
 *
 */


public class AgentFaitTout extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap=null;
    private List<Couple<String,Location>> posAgent = new ArrayList<>();

	private int counter=0;




	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */

	private static final String A="explo";
	private static final String B="ping";
	private static final String C="receivePing";
	private static final String D="shareMap";
	private static final String E="receive";
	private static final String F="Share";
	//private static final String F="receivemap";
	private static final String G="chasseur";
    //private static final String H="balade";
    //private static final String I="suiveur";
	private static final String H="SendPos";
    private static final String I="receivePos";
	private static final String J="Bloque";




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

		fsm.registerFirstState(new ExploCoopBehaviour(this,1,this.myMap,list_agentNames,this.posAgent),A);
		fsm.registerState(new PingSomeone(this,list_agentNames,0,this.posAgent), B);
		fsm.registerState(new ReceiverPing(this,this.posAgent), C);
		fsm.registerState(new ShareMapBehaviour(this,this.myMap,list_agentNames), D);
		//fsm.registerState(new ReceiveMap(this ,this.myMap), F);
		fsm.registerState(new Share(this,this.myMap,list_agentNames), F);
		fsm.registerState(new Receiver(this,0,this.posAgent), E);
        fsm.registerState(new ChasseurSoloBehaviour(this,this.myMap,this.posAgent,0,counter),G);
		//fsm.registerState(new Balade(this,this.myMap), H);
		//fsm.registerState(new Suiveur(this,this.myMap,0), I);
		fsm.registerState(new SendPosition(this,list_agentNames,this.posAgent), H);
		fsm.registerState(new ReceivePosition(this,this.posAgent), I);
		fsm.registerState(new BloqueGolem(this,list_agentNames), J);




		fsm.registerTransition(A,B,1);
		fsm.registerTransition(A,F,0);
		fsm.registerTransition(B,D,1);
		fsm.registerTransition(B,C,0);
		fsm.registerTransition(E,D,1);
		fsm.registerTransition(E,A,0);
		fsm.registerDefaultTransition(C,E);
		fsm.registerDefaultTransition(D,A);
		fsm.registerDefaultTransition(F,H);
        fsm.registerDefaultTransition(H,I);
		fsm.registerDefaultTransition(I,G);
		fsm.registerTransition(G,F,0);
		fsm.registerTransition(G,J,1);
		fsm.registerDefaultTransition(J,J);





		






		List<Behaviour> lb=new ArrayList<Behaviour>();

		/************************************************
		 * 
		 * ADD the behaviours of the Dummy Moving Agent
		 * 
		 ************************************************/

		lb.add(fsm);



		/***
		 * MANDATORY TO ALLOW YOUR AGENT TO BE DEPLOYED CORRECTLY
		 */


		addBehaviour(new startMyBehaviours(this,lb));


		System.out.println("the  agent "+this.getLocalName()+ " is started");

	}
	public void setMyMap(MapRepresentation m) {
		this.myMap=m;
	}
	public MapRepresentation getMyMap() {
		return this.myMap;
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

	public int getCount(){
		return counter;
	}

	public void reset(){
		counter=0;
	}




}