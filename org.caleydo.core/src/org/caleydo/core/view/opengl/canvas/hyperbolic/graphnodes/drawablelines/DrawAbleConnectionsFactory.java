package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;



/**
 * Factory to auto-generate draw able connections.
 * 
 * @author Helmut Pichlhoefer
 */

public final class DrawAbleConnectionsFactory {
	public static IDrawAbleConnection getDrawAbleConnection(String str){
		if(str == "Line")
			return new DrawAbleLinearConnection();
//		else if (str == "Spline")
//			return 0;
		return null;
			
	}

}
