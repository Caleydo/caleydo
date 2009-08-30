package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;



/**
 * Factory to auto-generate draw able connections.
 * 
 * @author Helmut Pichlhoefer
 * @author Georg Neubauer
 */

public final class DrawAbleConnectionsFactory {
	private static int iID = 0;
	public static IDrawAbleConnection getDrawAbleConnection(String str){
		if(str == "Line")
			return new DrawAbleLinearConnection(iID++);
//		else if (str == "Spline")
//			return 0;
		return null;
			
	}

}
