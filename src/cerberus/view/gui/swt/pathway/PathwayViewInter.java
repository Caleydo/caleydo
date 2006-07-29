package cerberus.view.gui.swt.pathway;

import cerberus.data.pathway.element.PathwayVertex;
import cerberus.data.pathway.element.PathwayVertexType;
import cerberus.view.gui.ViewInter;

public interface PathwayViewInter extends ViewInter
{
	public void createVertex(PathwayVertex vertex, String sTitle, int iHeight, int iWidth, 
			int iXPosition, int iYPosition, PathwayVertexType vertexType);
	
	public void createEdge(int iVertexId1, int iVertexId2);
}