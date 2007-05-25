package cerberus.data.pathway.element;

//import cerberus.data.pathway.element.APathwayEdge.EdgeType;

public class PathwayRelationEdge 
extends APathwayEdge {

	public enum EdgeRelationType {
		ECrel,
		PPrel,
		GErel,
		PCrel,
		maplink
	};
	
	protected int iElementId1 = 0;

	protected int iElementId2 = 0;

	protected int iCompoundId = 0;
	
	protected EdgeRelationType edgeRelationType;
	
	public PathwayRelationEdge(
			int iElementId1, 
			int iElementId2, 
			String sRelationType) {

		edgeType = EdgeType.RELATION;
		
		this.iElementId1 = iElementId1;
		this.iElementId2 = iElementId2;
		
		if (sRelationType.equals("ECrel"))
			edgeRelationType = EdgeRelationType.ECrel;
		else if (sRelationType.equals("maplink"))
			edgeRelationType = EdgeRelationType.maplink;
	}

	public int getElementId1() {
		
		return iElementId1;
	}

	public int getElementId2() {
		
		return iElementId2;
	}
	
	public void setCompoundId(int iCompoundId) {
		
		this.iCompoundId = iCompoundId;
	}

	public int getCompoundId() {
		
		return iCompoundId;
	}
	
	public EdgeRelationType getEdgeRelationType() {
		
		return edgeRelationType;
	}
}
