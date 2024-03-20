package eu.su.mas.dedaleEtu.mas.agents.dummies.explo;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;
import jade.core.behaviours.FSMBehaviour;

import eu.su.mas.dedaleEtu.mas.behaviours.ExploCoopBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.ShareMapBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.finish;
import eu.su.mas.dedaleEtu.mas.behaviours.ReceiverPing;
import eu.su.mas.dedaleEtu.mas.behaviours.PingSomeone;
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
	private MapRepresentation myMap;
	
	
	

	/**
	 * This method is automatically called when "agent".start() is executed.
	 * Consider that Agent is launched for the first time. 
	 * 			1) set the agent attributes 
	 *	 		2) add the behaviours
	 *          
	 */
	
	private static final String A="explo";
	private static final String B="ping";
	private static final String C="receive";
	private static final String D="share";
	private static final String E="finish";


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
		fsm.registerState(new PingSomeone(list_agentNames), B);
		fsm.registerState(new ReceiverPing(0), C);
		fsm.registerState(new ShareMapBehaviour(this ,this.myMap,list_agentNames), D);
		
		
		
		fsm.registerTransition(A,B,1);
		fsm.registerDefaultTransition(B,C);	
		fsm.registerTransition(C,D,1);
		fsm.registerDefaultTransition(D,A);	
		fsm.registerTransition(C,A,0);
		fsm.registerTransition(A,E,0);



		
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
	
	
	
}
