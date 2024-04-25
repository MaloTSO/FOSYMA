package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentFaitTout;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java_cup.runtime.ComplexSymbolFactory.Location;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


/**
 * The agent periodically share its map.
 * It blindly tries to send all its graph to its friend(s)  	
 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.

 * @author hc
 *
 */
public class ShareMapBehaviour extends OneShotBehaviour{
	
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
	public ShareMapBehaviour(Agent myagent,MapRepresentation mymap, List<String> receivers) {
		super(myagent);
		this.myMap=mymap;
		this.receivers=receivers;	
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -568863390879327961L;

	@Override
	public void action() {
		try {
			this.myAgent.doWait(150);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(this.myMap==null) {
			this.myMap=((AgentFaitTout)(this.myAgent)).getMyMap();
		}
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setProtocol("SHARE-TOPO");
		msg.setSender(this.myAgent.getAID());
		for (String agentName : receivers) {
			msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
		}
		SerializableSimpleGraph<String, MapAttribute> sg=this.myMap.getSerializableGraph();
		
		try {					
			msg.setContentObject(sg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		((AbstractDedaleAgent)this.myAgent).sendMessage(msg);

		try {
			this.myAgent.doWait(250);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
		MessageTemplate msgTemplate=MessageTemplate.and(MessageTemplate.MatchProtocol("SHARE-TOPO"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));
		ACLMessage msgReceived=this.myAgent.receive(msgTemplate);

        if (msgReceived!=null) {
            SerializableSimpleGraph<String, MapAttribute> sgreceived=null;
            try {
                sgreceived = (SerializableSimpleGraph<String, MapAttribute>)msgReceived.getContentObject();
            } catch (UnreadableException e) {
                e.printStackTrace();
            }
			if (sgreceived!=null){
				this.myMap.mergeMap(sgreceived);
			}
        }
	}
}


