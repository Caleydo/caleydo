/**
 * 
 */
package org.caleydo.view.dvi.contextmenu;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.gui.dataimport.ImportGroupingDialog;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.dvi.event.CreateClusteringEvent;

/**
 * Item that is can be used via context menu to trigger the
 * {@link ImportGroupingDialog}.
 * 
 * @author Christian Partl
 * 
 */
public class CreateClusteringItem extends AContextMenuItem {


	public CreateClusteringItem(ATableBasedDataDomain dataDomain,
			boolean isDimensionClustering) {

		IDCategory idCategory = isDimensionClustering ? dataDomain
				.getDimensionIDCategory() : dataDomain.getRecordIDCategory();

		setLabel("Create grouping for " + idCategory.getCategoryName()
				+ " using clustering");

		CreateClusteringEvent event = new CreateClusteringEvent(dataDomain,
				isDimensionClustering);
		event.setSender(this);
		registerEvent(event);
	}
}
