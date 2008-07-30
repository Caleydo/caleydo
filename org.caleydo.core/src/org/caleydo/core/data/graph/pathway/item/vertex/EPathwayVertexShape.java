package org.caleydo.core.data.graph.pathway.item.vertex;

public enum EPathwayVertexShape
{
	// KEGG types

	// the shape is a rectangle, which is used in KEGG to represent a
	// gene product and its complex (including an ortholog group).
	rectangle,
	// the shape is a circle, which is in KEGG used to specify any
	// other molecule such as a chemical compound and a glycan.
	circle,
	// the shape is a round rectangle, which is used in KEGG to represent
	// a linked pathway.
	roundrectangle,

	// BIOCARTA types
	rect, poly,
}
