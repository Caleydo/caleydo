/**
 * 
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.ShowViewWithoutDataEvent;

/**
 * Context menu item that is used to open and show views that do not display
 * data.
 * 
 * @author Christian Partl
 * 
 */
public class ShowViewWithoutDataItem extends AContextMenuItem {

	/**
	 * @param viewID
	 *            ID of the view to show
	 * @param viewName
	 *            View name that is displayed in the context menu.
	 */
	public ShowViewWithoutDataItem(String viewID, String viewName) {

		setLabel("Show " + viewName);
		ShowViewWithoutDataEvent event = new ShowViewWithoutDataEvent(viewID);
		event.setSender(this);
		registerEvent(event);
	}
}
