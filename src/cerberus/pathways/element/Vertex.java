package cerberus.pathways.element;

import java.util.Vector;

public class Vertex extends Element 
{
	public enum VertexType
	{
		ortholog,	// the node is a KO (ortholog group)
		enzyme,		// the node is an enzyme
		gene,		// the node is a gene product (mostly a protein)
		group		// the node is a chemical compound (including a glycan)
	}
	
	protected VertexType vertexType;
	
	protected Vector vertexRepresentation;
	
	public Vertex(String sName, String sType)
	{
		
	}
}
