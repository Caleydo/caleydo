/**
 * 
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.RenameDataDomainEvent;

/**
 * Context menu item to rename an {@link IDataDomain} using a dialog.
 * 
 * @author Christian Partl
 * 
 */
public class RenameDataDomainItem extends AContextMenuItem {

	public RenameDataDomainItem(IDataDomain dataDomain) {
		setLabel("Rename Dataset");

		RenameDataDomainEvent event = new RenameDataDomainEvent(dataDomain);
		event.setSender(this);
		registerEvent(event);
	}

}
