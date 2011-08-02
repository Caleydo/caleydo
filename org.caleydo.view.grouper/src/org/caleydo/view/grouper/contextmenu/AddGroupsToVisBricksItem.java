package org.caleydo.view.grouper.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.data.virtualarray.TableBasedDimensionGroupData;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.ContextMenuItem;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class AddGroupsToVisBricksItem extends ContextMenuItem {

	public AddGroupsToVisBricksItem(ArrayList<DataTable> setsToShow) {

		setLabel("Show Groups In VisBricks");

		ArrayList<ADimensionGroupData> dimensionGroupData = new ArrayList<ADimensionGroupData>(
				setsToShow.size());

		for (DataTable table : setsToShow) {
			TableBasedDimensionGroupData data = new TableBasedDimensionGroupData(
					table.getDataDomain(), table);
			dimensionGroupData.add(data);
		}

		AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
		event.setDimensionGroupData(dimensionGroupData);
		event.setSender(this);

		registerEvent(event);
	}
}
