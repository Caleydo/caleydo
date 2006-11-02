package cerberus.data.view.rep.pathway;

import cerberus.data.pathway.element.PathwayVertex;

public interface IPathwayVertexRep {
	
	public PathwayVertex getVertex();
	
	public int getXPosition();

	public int getYPosition();

	public String getName();
	
	public String getShapeType();
	
	/**
	 * Method needed for the JGraph labeling of the vertices.
	 */
	public String toString();
	
	public int getHeight();
	
	public int getWidth();
}