package org.caleydo.core.view.swt.pathway;

import java.awt.Dimension;

import org.caleydo.core.data.graph.core.PathwayGraph;
import org.caleydo.core.data.graph.item.vertex.PathwayVertexGraphItemRep;
import org.caleydo.core.view.IView;

public interface IPathwayGraphView 
extends IView {
	
	public void createVertex(PathwayVertexGraphItemRep vertex, 
			PathwayGraph containingPathway);
	
//	public void createEdge(int iVertexId1, 
//			int iVertexId2, 
//			boolean bDrawArrow,
//			APathwayEdge pathwayEdge);
		
	public void loadPathwayFromFile(int iNewPathwayId);
	
	public void loadImageMapFromFile(String sImagePath); 
	
	public void setNeighbourhoodDistance(int iNeighbourhoodDistance);
	
	public void zoomOrig();
	
	public void zoomIn();
	
	public void zoomOut();
	
	public void showOverviewMapInNewWindow(Dimension dim);
	
//	public void showHideEdgesByType(boolean bShowEdges,
//			EdgeType edgeType);
//	
//	public boolean getEdgeVisibilityStateByType(EdgeType edgeType);
	
	public void showBackgroundOverlay(boolean bTurnOn);
	
	public void resetPathway();
}