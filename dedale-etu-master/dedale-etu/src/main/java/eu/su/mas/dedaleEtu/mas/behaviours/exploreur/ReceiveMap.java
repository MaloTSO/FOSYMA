package eu.su.mas.dedaleEtu.mas.behaviours.exploreur;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;

import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;


/**
 * This behaviour receives nb integer values, sum them and then send the result back to the agent whose name is given in parameters
 * 
 * In order to stay relatively simple for now, this behaviour is not fully generic (it is not declared as being part of a protocol) 
 * and do both the receiving and the sending processes.
 * 
 * @author hc
 *
 */
public class ReceiveMap extends OneShotBehaviour {
	
	private static final long serialVersionUID = 9088209402507795299L;	
    
	private MapRepresentation myMap;
	

	public ReceiveMap(final AbstractDedaleAgent myagent,MapRepresentation myMap) {
		super(myagent);
        this.myMap=myMap;
		
	}

	public void action(){

        try {
			this.myAgent.doWait(500);
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