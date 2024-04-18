package eu.su.mas.dedaleEtu.mas.agents.dummies;

import java.util.ArrayList;
import java.util.List;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedale.mas.agent.behaviours.platformManagment.*;
import jade.core.behaviours.FSMBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.Chasseur.Balade;
import eu.su.mas.dedaleEtu.mas.behaviours.Chasseur.ChasseurSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.Chasseur.Suiveur;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ExploCoopBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ExploSoloBehaviour;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ReceiveMap;
import eu.su.mas.dedaleEtu.mas.behaviours.exploreur.ReceiverPing;
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


public class ChasseurAgent extends AbstractDedaleAgent {

	private static final long serialVersionUID = -7969469610241668140L;
	private MapRepresentation myMap;
	
	
	private static final String A="explo";
	private static final String B="balade";
	private static final String C="suiveur";
	private static final String D="receive";
	private static final String E="receivemap";

	protected void setup(){

		super.setup();
		
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
		fsm.registerFirstState(new ChasseurSoloBehaviour(this,0,this.myMap),A);
		fsm.registerState(new Balade(this,this.myMap), B);
		fsm.registerState(new Suiveur(this,this.myMap), C);
		fsm.registerState(new ReceiverPing(0), D);
		fsm.registerState(new ReceiveMap(this,this.myMap), E);
		
		fsm.registerTransition(A,B,0);
		fsm.registerTransition(A,C,1);
		fsm.registerDefaultTransition(B,A);
		fsm.registerDefaultTransition(C,C);

		fsm.registerTransition(D,A,0);
		fsm.registerTransition(D,E,1);
		fsm.registerDefaultTransition(E,A);


		List<Behaviour> lb=new ArrayList<Behaviour>();
		
		lb.add(fsm);

		addBehaviour(new startMyBehaviours(this,lb));

		System.out.println("the  agent "+this.getLocalName()+ " is started");
	}
}

