package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class Log2ForSetItem extends AContextMenuItem {


	Set set;

	public Log2ForSetItem(ISet set) {
		super();
		this.set = (Set)set;
		setText("Run log2 for set");

	}

	public void triggerEvent() {
		SetUtils.setExternalDataRepresentation((Set) set,
				EExternalDataRepresentation.LOG2, false);

	};
}
