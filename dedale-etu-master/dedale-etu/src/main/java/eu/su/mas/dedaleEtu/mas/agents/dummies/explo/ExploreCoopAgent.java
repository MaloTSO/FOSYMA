package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;
import jade.core.behaviours.FSMBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ExploCoopBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.PingSomeone;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ReceiveMap;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ReceiverPing;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.Receiver;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ShareMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.finish;
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


public class ExploreCoopAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap=null;




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
	private static final String D="share";
	private static final String E="finish";
	private static final String F="receivemap";
	private static final String G="receive";


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

		fsm.registerFirstState(new ExploCoopBehaviour(this,1,this.myMap,list_agentNames),A);
		fsm.registerLastState(new finish(this), E);
		fsm.registerState(new PingSomeone(this,list_agentNames,0), B);
		fsm.registerState(new ReceiverPing(0), C);
		fsm.registerState(new ShareMapBehaviour(this,this.myMap,list_agentNames), D);
		fsm.registerState(new ReceiveMap(this ,this.myMap), F);
		fsm.registerState(new Receiver(this,0), G);


		fsm.registerTransition(A,D,1);


		fsm.registerTransition(A,E,0);
		fsm.registerDefaultTransition(D,A);






		/*
		fsm.registerTransition(A,B,1);
		fsm.registerTransition(A,E,0);
		fsm.registerTransition(B,D,1);
		fsm.registerTransition(B,G,0);
		fsm.registerTransition(G,D,1);
		fsm.registerTransition(G,F,0);
		fsm.registerDefaultTransition(D,F);
		fsm.registerDefaultTransition(F,A);

		 */








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

}
