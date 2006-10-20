package cerberus.view.gui.swt.pathway;

import java.awt.Dimension;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.view.gui.IView;

public interface IPathwayGraphView 
extends IView
{
	public void createVertex(
			PathwayVertex vertex,
			String sTitle,
			int iHeight, 
			int iWidth, 
			int iXPosition, 
			int iYPosition, 
			String sShapeType);
	
	public void createEdge(int iVertexId1, int iVertexId2, boolean bDrawArrow);
	
	public void setPathwayId(int iPathwayId);
	
	public void loadPathwayFromFile(String sFilePath);
	
	public void setNeighbourhoodDistance(int iNeighbourhoodDistance);
	
	public void zoomOrig();
	
	public void zoomIn();
	
	public void zoomOut();
	
	public void showOverviewMapInNewWindow(Dimension dim);
}