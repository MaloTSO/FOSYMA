package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentFaitTout;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class PingSomeone extends OneShotBehaviour {
	private static final long serialVersionUID = 9088209402507795288L;	
	private List<String> receivers;
	private List<Couple<String,Location>> posAgent;
	private MapRepresentation myMap;
	private int exitValue;


	public PingSomeone(final AbstractDedaleAgent myagent,List<String> receivers,int max,List<Couple<String,Location>> posAgent,MapRepresentation myMap) {
		super(myagent);
		this.receivers=receivers;
		this.posAgent=posAgent;
		this.myMap=myMap;
		exitValue=max;

	}
	
	public void action(){

		exitValue=0;
		if(this.myMap==null) {
			exitValue=1;
		}
		
		this.myMap=((AgentFaitTout)(this.myAgent)).getMyMap();

		if(this.posAgent==null) {
			this.posAgent=((AgentFaitTout)(this.myAgent)).getPosAgent();
		}
		if (exitValue==0){
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

	public int onEnd() {
		return exitValue;
	}
}
