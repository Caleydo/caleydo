package org.geneview.core.data.graph.item.vertex;


public enum EPathwayVertexShape
{
	// the shape is a rectangle, which is used to represent a 
	// gene product and its complex (including an ortholog group).
	rectangle,
	// the shape is a circle, which is used to specify any 
	// other molecule such as a chemical compound and a glycan.
	circle, 	 
	// the shape is a round rectangle, which is used to represent 
	// a linked pathway.
	roundrectangle 	
}
