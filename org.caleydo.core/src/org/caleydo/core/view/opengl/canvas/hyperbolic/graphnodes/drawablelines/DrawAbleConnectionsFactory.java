package org.caleydo.core.view.opengl.canvas.hyperbolic.graphnodes.drawablelines;

/**
 * Factory to auto-generate draw able connections.
 * 
 * @author Helmut Pichlhoefer
 * @author Georg Neubauer
 */

public final class DrawAbleConnectionsFactory {
	public static IDrawAbleConnection getDrawAbleConnection(String str, int iIDRoot, int iIDChild) {
		if (str == "Line")
			return new DrawAbleLinearConnection(generateID(iIDRoot, iIDChild));
		else if (str == "Spline")
			return new DrawAbleSplineConnection(generateID(iIDRoot, iIDChild));
		return null;
	}

	private static int generateID(int iID1, int iID2) {
		int left = (iID1 >= iID2 ? iID1 : iID2);
		int right = (iID1 < iID2 ? iID1 : iID2);
		return ((left << 12) | (left >> (32 - 12))) ^ right;
	}
}
