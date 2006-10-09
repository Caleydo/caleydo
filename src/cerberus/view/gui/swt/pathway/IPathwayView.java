package cerberus.view.gui.swt.pathway;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.view.gui.IView;

public interface IPathwayView extends IView
{
	public void createVertex(PathwayVertex vertex, int iHeight, int iWidth, 
			int iXPosition, int iYPosition, PathwayVertexType vertexType);
	
	public void createEdge(int iVertexId1, int iVertexId2);
}