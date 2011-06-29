package org.caleydo.view.grouper.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.virtualarray.ADimensionGroupData;
import org.caleydo.core.data.virtualarray.SetBasedDimensionGroupData;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;
import org.caleydo.view.visbricks.event.AddGroupsToVisBricksEvent;

public class AddGroupsToVisBricksItem extends AContextMenuItem {

	public AddGroupsToVisBricksItem(ArrayList<ISet> setsToShow) {
		super();

		setText("Show Groups In VisBricks");

		ArrayList<ADimensionGroupData> dimensionGroupData = new ArrayList<ADimensionGroupData>(
				setsToShow.size());

		for (ISet set : setsToShow) {
			SetBasedDimensionGroupData data = new SetBasedDimensionGroupData(set.getDataDomain(), set);
			dimensionGroupData.add(data);
		}
		
		AddGroupsToVisBricksEvent event = new AddGroupsToVisBricksEvent();
		event.setDimensionGroupData(dimensionGroupData);
		event.setSender(this);
		
		registerEvent(event);
	}
}
