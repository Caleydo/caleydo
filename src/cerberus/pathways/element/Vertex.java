package cerberus.pathways.element;

import java.util.Map;


public class Vertex extends Element 
{
	enum VertexType
	{
		ortholog,	// the node is a KO (ortholog group)
		enzyme,		// the node is an enzyme
		gene,		// the node is a gene product (mostly a protein)
		group		// the node is a chemical compound (including a glycan)
	}
	
	protected VertexType vertexType;
	
	//protected Map<Integer, VertexRepresentation> mapVertexRepresentation;
}
