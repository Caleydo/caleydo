/**
 * 
 */
package org.caleydo.core.data.selection;

import java.util.ArrayList;

import org.caleydo.core.view.AView;

/**
 * Special ISet for selection. 
 * Used to exchange selection data between ViewRep's.
 * Interface provides writing and reading access
 * to the selection data.
 * 
 * @see AView.view.gui.AViewRep
 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public interface ISelection
{

	public void setSelectionIdArray(ArrayList<Integer> iAlSelectionId);
	
	public void setGroupArray(ArrayList<Integer> iAlSelectionGroup);

	public void setOptionalDataArray(ArrayList<Integer> iAlSelectionOptionalData);

	public void setAllSelectionDataArrays(ArrayList<Integer> iAlSelectionId, 
			ArrayList<Integer> iAlSelectionGroup, 
			ArrayList<Integer> iAlSelectionOptionalData);
	
	public ArrayList<Integer> getSelectionIdArray();
	
	public ArrayList<Integer> getGroupArray();
	
	public ArrayList<Integer> getOptionalDataArray();
}
