package cerberus.view.gui.swt.pathway;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.view.gui.IView;

public interface IPathwayGraphView 
extends IView
{
	public void createVertex(PathwayVertex vertex, int iHeight, int iWidth, 
			int iXPosition, int iYPosition, PathwayVertexType vertexType);
	
	public void createEdge(int iVertexId1, int iVertexId2);
	
	public void setPathwayId(int iPathwayId);
	
	public void loadPathwayFromFile(String sFilePath);
	
	public void setNeighbourhoodDistance(int iNeighbourhoodDistance);
	
	public void zoomOrig();
	
	public void zoomIn();
	
	public void zoomOut();
}