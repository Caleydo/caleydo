package cerberus.data.pathway.element;

public abstract class APathwayElement  {
	
	private int iElementId = 0;

	private String sElementTitle;

	public APathwayElement() {
		
	}
	
	public APathwayElement(int iElementId, String sElementTitle) {
		
		this.iElementId = iElementId;

		this.sElementTitle = sElementTitle;
	}

	public int getElementId() {
		
		return iElementId;
	}
	
	public String getElementTitle() {
		
		return sElementTitle;
	}
	
	public void setElementTitle(String sElementTitle) {
		
		this.sElementTitle = sElementTitle;
	}
	
	/**
	 * Method needed for the JGraph labeling of the vertices.
	 */
	public String toString() {
		
		return sElementTitle;
	}
}
