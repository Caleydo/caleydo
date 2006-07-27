package cerberus.data.pathway.element;

import java.util.Vector;
import cerberus.data.view.rep.pathway.VertexRepInter;


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
	
	private VertexType vertexType;
	
	private Vector<VertexRepInter> vertexReps;
	
	public Vertex(int iVertexID, String sName, String sType)
	{	
		super(iVertexID, sName);
		vertexReps = new Vector<VertexRepInter>();
		
		if (sType.equals("ortholog"))
			vertexType = VertexType.ortholog;
		else if (sType.equals("enzyme"))
			vertexType = VertexType.enzyme;
		else if (sType.equals("gene"))
			vertexType = VertexType.gene;
		else if (sType.equals("group"))
			vertexType = VertexType.group;
		else if (sType.equals("compound"))
			vertexType = VertexType.compound;
		else if (sType.equals("map"))
			vertexType = VertexType.map;
	}
	
	public void addVertexRep(VertexRepInter vertexRep)
	{
		vertexReps.add(vertexRep);
	}

	public Vector<VertexRepInter> getVertexReps()
	{
		return vertexReps;
	}

	public VertexType getVertexType() 
	{
		return vertexType;
	}
}
