package cerberus.data.pathway.element;

import cerberus.data.view.rep.pathway.IPathwayVertexRep;

public class PathwayVertex 
extends APathwayElement {
	
	protected PathwayVertexType vertexType;
	
	/*
	 * Array can hold up to 3 Vertex Representations.
	 * The array is of fixed size to get maximum performance.
	 * Therefore each view has to hold their own vertexRep index.
	 */
	protected IPathwayVertexRep[] refVertexRepArray;
	
	protected String sVertexLink = ""; 
	
	/*
	 * The enzyme's reaction name
	 */
	protected String sReactionName;
	
	/*
	 * Constructor.
	 */
	public PathwayVertex(
			int iVertexID, 
			String sName, 
			String sType, 
			String sLink,
			String sReactionName) {
		
		super(iVertexID, sName);
		
		refVertexRepArray = new IPathwayVertexRep[3];
		
		vertexType = PathwayVertexType.valueOf( sType );
		
		sVertexLink = sLink;
		
		this.sReactionName = sReactionName;
	}
	
	public void addVertexRep(IPathwayVertexRep vertexRep, int iVertexRepIndex) {
		
		refVertexRepArray[iVertexRepIndex] = vertexRep;
	}

	public IPathwayVertexRep[] getVertexReps() {
		
		return refVertexRepArray;
	}

	public PathwayVertexType getVertexType() {
		
		return vertexType;
	}
	
	public String getVertexLink() {
		
		return sVertexLink;
	}
	
	public String getVertexReactionName() {
		
		return sReactionName;
	}
	
	/*
	 * Returns a Vertex Representation by a given index.
	 */
	public IPathwayVertexRep getVertexRepByIndex(int iVertexRepIndex) {
		
		return refVertexRepArray[iVertexRepIndex];
	}
}
