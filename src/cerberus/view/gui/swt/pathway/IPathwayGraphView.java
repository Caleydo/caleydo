package cerberus.view.gui.swt.pathway;

import java.awt.Dimension;

import cerberus.data.pathway.element.APathwayEdge;
import cerberus.data.pathway.element.APathwayEdge.EdgeType;
import cerberus.data.view.rep.pathway.jgraph.PathwayVertexRep;
import cerberus.view.gui.IView;

public interface IPathwayGraphView 
extends IView
{
	public void createVertex(PathwayVertexRep vertex);
	
	public void createEdge(int iVertexId1, 
			int iVertexId2, 
			boolean bDrawArrow,
			APathwayEdge refPathwayEdge);
	
	public void setPathwayId(int iPathwayId);
	
	public void loadPathwayFromFile(String sFilePath);
	
	public void loadImageMapFromFile(String sImagePath); 
	
	public void setNeighbourhoodDistance(int iNeighbourhoodDistance);
	
	public void zoomOrig();
	
	public void zoomIn();
	
	public void zoomOut();
	
	public void showOverviewMapInNewWindow(Dimension dim);
	
	public void showHideEdgesByType(boolean bShowEdges,
			EdgeType edgeType);
	
	public void showBackgroundOverlay(boolean bTurnOn);
	
	public void finishGraphBuilding();
	
	public void loadBackgroundOverlayImage(String sPathwayImageFilePath);
	
	public void resetPathway();
}