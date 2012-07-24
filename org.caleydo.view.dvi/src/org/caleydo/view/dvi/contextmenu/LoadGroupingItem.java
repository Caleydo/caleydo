/**
 * 
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.gui.ImportGroupingDialog;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.LoadGroupingEvent;

/**
 * Item that is can be used via context menu to trigger the
 * {@link ImportGroupingDialog}.
 * 
 * @author Christian Partl
 * 
 */
public class LoadGroupingItem extends AContextMenuItem {

	/**
	 * @param dataDomain
	 *            The datadomain a grouping should be loaded for.
	 * @param isColumnGrouping
	 *            Determines whether the grouping should be loaded for rows or
	 *            columns.
	 */
	public LoadGroupingItem(ATableBasedDataDomain dataDomain, IDCategory idCategory) {
		setLabel("Load Grouping for " + idCategory.getCategoryName());
		LoadGroupingEvent event = new LoadGroupingEvent(dataDomain, idCategory);
		event.setSender(this);
		registerEvent(event);
	}

}
