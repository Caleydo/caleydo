package cerberus.data.pathway.element;

public class PathwayRelationEdge 
extends PathwayEdge {

	protected int iElementId1 = 0;

	protected int iElementId2 = 0;

	protected String sRelationType = "";

	protected int iCompoundId = 0;
	
	public PathwayRelationEdge(
			int iElementId1, 
			int iElementId2, 
			String sRelationType) {
		
		edgeType = PathwayEdgeType.RELATION;
		
		this.iElementId1 = iElementId1;
		this.iElementId2 = iElementId2;
		this.sRelationType = sRelationType;
	}

	public int getElementId1() {
		
		return iElementId1;
	}

	public int getElementId2() {
		
		return iElementId2;
	}

	public String getRelationType() {
		
		return sRelationType;
	}
	
	public void setICompoundId(int iCompoundId) {
		
		this.iCompoundId = iCompoundId;
	}

	public int getICompoundId() {
		
		return iCompoundId;
	}
}
