package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.animation;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.IDrawAbleConnection;

public class AnimationConnectionHandler {
	
	List<IDrawAbleConnection> nodeMapping = null;
	
	public AnimationConnectionHandler(){
		this.nodeMapping = new ArrayList<IDrawAbleConnection>();
	}
	
	public void addConnectionInformation(IDrawAbleConnection conn){
		if(!nodeMapping.isEmpty())
			if(checkForAccidense(conn))
				return;
		nodeMapping.add(conn);
	}

	private boolean checkForAccidense(IDrawAbleConnection conn) {
		for(IDrawAbleConnection mapping : nodeMapping)
			if(conn.compareTo(mapping)== 0)
				return true;
		return false;
	}
	
	public void clearAllOccurencesOfNode(IDrawAbleNode node){
		List<IDrawAbleConnection> toDelete = new ArrayList<IDrawAbleConnection>();
		for(IDrawAbleConnection mapping : nodeMapping)
			if(mapping.getConnectedNodes()[0].compareTo(node) == 0 || mapping.getConnectedNodes()[1].compareTo(node) == 0)
				toDelete.add(mapping);
		for(IDrawAbleConnection mapping : toDelete)
			nodeMapping.remove(mapping);			
	}
	
	public List<IDrawAbleConnection> getAllConnections(){
		return nodeMapping;
	}
}
