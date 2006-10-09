package cerberus.data.pathway.element;

public class PathwayElement  {
	
	private int iElementId = 0;

	private String sElementTitle;

	public PathwayElement() {
		
	}
	
	public PathwayElement(int iElementId, String sElementTitle) {
		
		this.iElementId = iElementId;

		this.sElementTitle = sElementTitle;
	}

	public int getIElementId() {
		
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
