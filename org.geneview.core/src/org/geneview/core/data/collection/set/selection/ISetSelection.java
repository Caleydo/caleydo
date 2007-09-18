/**
 * 
 */
package org.geneview.core.data.collection.set.selection;

import org.geneview.core.data.collection.ISet;

/**
 * Special ISet for selection. 
 * Used to exchange selection data between ViewRep's.
 * Interface provides writing and reading access
 * to the selection data.
 * 
 * @see cerverus.view.gui.AViewRep
 * @see org.geneview.core.manager.event.mediator.IMediatorReceiver
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface ISetSelection 
extends ISet {

	public void setSelectionIdArray(int[] iArSelectionId);
	
	public void setGroupArray(int[] iArSelectionGroup);

	public void setOptionalDataArray(int[] iArSelectionOptionalData);

	public void setAllSelectionDataArrays(int[] iArSelectionId, 
			int[] iArSelectionGroup, 
			int[] iArSelectionOptionalData);
	
	public int[] getSelectionIdArray();
	
	public int[] getGroupArray();
	
	public int[] getOptionalDataArray();
}
