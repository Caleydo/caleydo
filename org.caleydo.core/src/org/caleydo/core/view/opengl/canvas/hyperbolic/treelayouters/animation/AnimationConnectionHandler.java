package org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.animation;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.IDrawAbleNode;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.DrawAbleHyperbolicLayoutConnector;
import org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines.IDrawAbleConnection;
import org.caleydo.core.view.opengl.canvas.hyperbolic.treelayouters.projections.ITreeProjection;

public class AnimationConnectionHandler {
	
	List<IDrawAbleNode[]> nodeMapping = null;
	
	public AnimationConnectionHandler(){
		this.nodeMapping = new ArrayList<IDrawAbleNode[]>();
	}
	
	public void addConnectionInformation(IDrawAbleConnection conn){
		if(!nodeMapping.isEmpty())
			if(checkForAccidense(conn))
				return;
		nodeMapping.add(conn.getConnectedNodes());
	}

	private boolean checkForAccidense(IDrawAbleConnection conn) {
		IDrawAbleNode[] newMapping = conn.getConnectedNodes();
		for(IDrawAbleNode[] mapping : nodeMapping)
			if(mapping[0].compareTo(newMapping[0]) == 0 && mapping[1].compareTo(newMapping[1]) == 0)
				return true;
		return false;
	}
	
	public void clearAllOccurencesOfNode(IDrawAbleNode node){
		List<IDrawAbleNode[]> toDelete = new ArrayList<IDrawAbleNode[]>();
		for(IDrawAbleNode[] mapping : nodeMapping)
			if(mapping[0].compareTo(node) == 0 || mapping[1].compareTo(node) == 0)
				toDelete.add(mapping);
		for(IDrawAbleNode[] mapping : toDelete)
			nodeMapping.remove(mapping);			
	}
	
	public List<IDrawAbleConnection> getAllConnections(ITreeProjection treeProjection){
		List<IDrawAbleConnection> connList = new ArrayList<IDrawAbleConnection>();
		for(IDrawAbleNode[] mapping : nodeMapping)
			connList.add(new DrawAbleHyperbolicLayoutConnector(mapping[0], mapping[1], treeProjection));
		return connList;
	}
}
