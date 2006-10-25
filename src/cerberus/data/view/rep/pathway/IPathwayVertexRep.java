package cerberus.data.view.rep.pathway;

import cerberus.data.pathway.element.PathwayVertex;

public interface IPathwayVertexRep
{
	public abstract PathwayVertex getVertex();
	
	public abstract int getXPosition();

	public abstract int getYPosition();

	public abstract String getName();
	
	public abstract String getShapeType();

}