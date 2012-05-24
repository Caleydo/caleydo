/**
 * 
 */
package org.caleydo.view.dvi.event;

import org.caleydo.core.event.AEvent;

/**
 * Event to show a view that has no associated data.
 * 
 * @author Christian Partl
 * 
 */
public class ShowViewWithoutDataEvent extends AEvent {

	/**
	 * ID of the view to show.
	 */
	private String viewID;
	
	public ShowViewWithoutDataEvent(String viewID) {
		this.viewID = viewID;
	}

	@Override
	public boolean checkIntegrity() {
		return viewID != null;
	}
	
	/**
	 * @param viewID setter, see {@link #viewID}
	 */
	public void setViewID(String viewID) {
		this.viewID = viewID;
	}
	
	/**
	 * @return the viewID, see {@link #viewID}
	 */
	public String getViewID() {
		return viewID;
	}

}
