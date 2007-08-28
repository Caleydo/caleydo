package cerberus.view.gui.swt.pathway;

import java.awt.Dimension;

import cerberus.data.graph.core.PathwayGraph;
import cerberus.data.graph.item.vertex.PathwayVertexGraphItemRep;
import cerberus.view.gui.IView;

public interface IPathwayGraphView 
extends IView {
	
	public void createVertex(PathwayVertexGraphItemRep vertex, 
			PathwayGraph refContainingPathway);
	
//	public void createEdge(int iVertexId1, 
//			int iVertexId2, 
//			boolean bDrawArrow,
//			APathwayEdge refPathwayEdge);
		
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