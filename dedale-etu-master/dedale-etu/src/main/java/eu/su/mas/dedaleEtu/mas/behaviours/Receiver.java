package eu.su.mas.dedaleEtu.mas.behaviours;

import java.io.IOException;
import java.util.List;

import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.gs.gsLocation;
import dataStructures.serializableGraph.SerializableSimpleGraph;
import eu.su.mas.dedaleEtu.mas.agents.dummies.AgentFaitTout;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
public class Receiver extends OneShotBehaviour {
	
	private static final long serialVersionUID = 9088209402507795399L;	
	private int exitValue;
	private List<Couple<String,Location>> posAgent;

	public Receiver(final AbstractDedaleAgent myagent,int max,List<Couple<String,Location>> posAgent) {
		super(myagent);
		this.posAgent=posAgent;
		exitValue=max;
	}
    public void action(){
		exitValue=0;
	
		try {
			this.myAgent.doWait(100);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(this.posAgent==null) {
			this.posAgent=((AgentFaitTout)(this.myAgent)).getPosAgent();
		}

		final MessageTemplate msgTemplate = MessageTemplate.and(MessageTemplate.MatchProtocol("Position-Share"),MessageTemplate.MatchPerformative(ACLMessage.INFORM));

		final ACLMessage msg = this.myAgent.receive(msgTemplate);

		if (msg!=null){
			Location posi= new gsLocation(msg.getContent());

            Couple<String,Location> myCouple=new Couple<>(msg.getSender().getLocalName(), posi);

            ((AgentFaitTout)(this.myAgent)).setPosAgent(myCouple);
			exitValue=1;
		}
    }
	public int onEnd() {
		return exitValue;
	}
    
}
