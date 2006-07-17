package cerberus.pathways.element;

import java.util.Vector;

public class Vertex extends Element 
{
	public enum VertexType
	{
		ortholog,	// the node is a KO (ortholog group)
		enzyme,		// the node is an enzyme
		gene,		// the node is a gene product (mostly a protein)
		group,		// the node is a complex of gene products (mostly a protein complex)
		compound,	// the node is a chemical compound (including a glycan)
		map
	}
	
	protected VertexType vertexType;
	
	protected Vector<VertexRepresentation> vertexRepresentations;
	
	public Vertex(int iVertexID, String sName, String sType)
	{	
		super(iVertexID, sName);
		vertexRepresentations = new Vector<VertexRepresentation>();
	}
	
	public void addVertexRepresentation(VertexRepresentation vertexRep)
	{
		vertexRepresentations.add(vertexRep);
	}

	public Vector<VertexRepresentation> getVertexRepresentations()
	{
		return vertexRepresentations;
	}
}
