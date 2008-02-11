package org.geneview.core.data.view.rep.selection;

/**
 * Class that holds information about a 
 * selected element in a specific view.
 * 
 * @author Marc Streit
 *
 */
public class SelectedElementRep {
	
	private int iContainingViewID;
	
	private float fXCoord;
	private float fYCoord;
	
	/**
	 * Constructor.
	 * 
	 * @param iContainingViewID
	 * @param fXCoord
	 * @param fYCoord
	 */
	public SelectedElementRep(final int iContainingViewID, 
			final float fXCoord, 
			final float fYCoord) {
		
		this.iContainingViewID = iContainingViewID;
		this.fXCoord = fXCoord;
		this.fYCoord = fYCoord;
	}
	
	public int getContainingViewID () {
		return iContainingViewID;
	}
	
	public float getXCoord() {
		return fXCoord;
	}
	
	public float getYCoord() {
		return fYCoord;
	}
}
