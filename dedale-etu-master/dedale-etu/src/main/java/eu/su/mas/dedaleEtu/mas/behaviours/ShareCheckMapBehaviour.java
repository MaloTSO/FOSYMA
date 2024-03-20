
package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * The agent periodically share its map.
 * It blindly tries to send all its graph to its friend(s)  	
 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.

 * @author hc
 *
 */
public class ShareCheckMapBehaviour extends TickerBehaviour{
	
	private MapRepresentation myMap;
	private List<String> receivers;

	/**
	 * The agent periodically share its map.
	 * It blindly tries to send all its graph to its friend(s)  	
	 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.

	 * @param a the agent
	 * @param period the periodicity of the behaviour (in ms)
	 * @param mymap (the map to share)
	 * @param receivers the list of agents to send the map to
	 */
	public ShareCheckMapBehaviour(Agent a, long period,MapRepresentation mymap, List<String> receivers) {
		super(a, period);
		this.myMap=mymap;
		this.receivers=receivers;	
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -568863390879327971L;
	private String senderName="";


	@Override
	protected void onTick() {
		//4) At each time step, the agent blindly send all its graph to its surrounding to illustrate how to share its knowledge (the topology currently) with the the others agents. 	
		// If it was written properly, this sharing action should be in a dedicated behaviour set, the receivers be automatically computed, and only a subgraph would be shared.
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-TOPO");
		msg.setSender(this.myAgent.getAID());
		msg.setContent("Hello there is someone");
		for (String agentName : receivers) {
			msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
		}
			
		
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		
		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
				MessageTemplate.MatchSender(new AID(this.senderName, AID.ISLOCALNAME)));
		final ACLMessage msgRep = this.myAgent.receive(msgTemplate);
		if (msgRep != null) {	
			final ACLMessage msgMap = new ACLMessage(ACLMessage.INFORM);
			msgMap.setSender(this.myAgent.getAID());
			msgMap.addReceiver(new AID(this.senderName, AID.ISLOCALNAME));  
			SerializableSimpleGraph<String, MapAttribute> sg=this.myMap.getSerializableGraph();
			try {					
				msgRep.setContentObject(sg);
			} catch (IOException e) {
				e.printStackTrace();
			}
			((AbstractDedaleAgent)this.myAgent).sendMessage(msgMap);

			
		}

	}

}

