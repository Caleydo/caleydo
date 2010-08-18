package org.caleydo.view.radial;

/**
 * Interface for creating element representations of selected partial discs for
 * drawing connection lines.
 * 
 * @author Christian Partl
 */
public interface IElementRepCreator {

	/**
	 * Creates an element representation with the specified parameters.
	 * 
	 * @param partialDisc
	 *            Partial disc an element representation shall be created for.
	 * @param iViewID
	 *            View ID of the radial hierarchy.
	 */
	public void createElementRep(PartialDisc partialDisc, int iViewIDfloat,
			float fHierarchyCenterX, float fHierarchyCenterY, float fHierarchyCenterZ);
}
