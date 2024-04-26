package eu.su.mas.dedaleEtu.mas.behaviours.BehaviourAgentAdap;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentAdaptatif;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Communication extends OneShotBehaviour {
	private static final long serialVersionUID = 9088209402507795288L;	
	private List<String> receivers;


	public Communication(final AbstractDedaleAgent myagent,List<String> receivers) {
		super(myagent);
		this.receivers=receivers;

	}
	
	public void action(){

        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.setProtocol("Position-Share");
        msg.setSender(this.myAgent.getAID());
        msg.setContent(((AbstractDedaleAgent)this.myAgent).getCurrentPosition().toString());
        for (String agentName : receivers){
            msg.addReceiver(new AID(agentName,AID.ISLOCALNAME));
        }
        ((AbstractDedaleAgent)this.myAgent).sendMessage(msg);
		}
	}



