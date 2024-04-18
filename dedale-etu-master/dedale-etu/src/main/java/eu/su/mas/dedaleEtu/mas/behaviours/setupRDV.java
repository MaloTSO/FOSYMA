package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.Iterator;
import java.util.List;

import dataStructures.serializableGraph.SerializableSimpleGraph;
import dataStructures.tuple.Couple;
import eu.su.mas.dedale.env.Location;
import eu.su.mas.dedale.env.Observation;
import eu.su.mas.dedale.env.gs.gsLocation;
import eu.su.mas.dedale.mas.AbstractDedaleAgent;

import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation.MapAttribute;
import eu.su.mas.dedaleEtu.mas.knowledge.MapRepresentation;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


/**
 * The agent periodically share its map.
 * It blindly tries to send all its graph to its friend(s)  	
 * If it was written properly, this sharing action would NOT be in a ticker behaviour and only a subgraph would be shared.

 * @author hc
 *
 */
public class setupRDV extends OneShotBehaviour{

    private int k;
    private MapRepresentation myMap;

    public setupRDV(final AbstractDedaleAgent myagent,MapRepresentation myMap,int k){
        super(myagent);
        this.k=k;
        this.myMap=myMap;
    }
    private static final long serialVersionUID = -568863390879327961L;

	@Override
    public void action(){

        int i=0;
        Location myPosition=((AbstractDedaleAgent)this.myAgent).getCurrentPosition();



        while (i<k){

            if (myPosition!=null){
                //List of observable from the agent's current position
                List<Couple<Location,List<Couple<Observation,Integer>>>> lobs=((AbstractDedaleAgent)this.myAgent).observe();//myPosition
    
                /**
                 * Just added here to let you see what the agent is doing, otherwise he will be too quick
                 */
                try {
                    this.myAgent.doWait(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
    
                //1) remove the current node from openlist and add it to closedNodes.
                this.myMap.addNode(myPosition.getLocationId(), MapAttribute.closed);
    
                //2) get the surrounding nodes and, if not in closedNodes, add them to open nodes.
                String nextNodeId=null;
                Iterator<Couple<Location, List<Couple<Observation, Integer>>>> iter=lobs.iterator();
                while(iter.hasNext()){
                    Location accessibleNode=iter.next().getLeft();
                    boolean isNewNode=this.myMap.addNewNode(accessibleNode.getLocationId());
                    //the node may exist, but not necessarily the edge
                    if (myPosition.getLocationId()!=accessibleNode.getLocationId()) {
                        this.myMap.addEdge(myPosition.getLocationId(), accessibleNode.getLocationId());
                        if (nextNodeId==null && isNewNode) nextNodeId=accessibleNode.getLocationId();
                    }
                }
                i++;
                ((AbstractDedaleAgent)this.myAgent).moveTo(new gsLocation(nextNodeId));
            }
        }
    }
}

    