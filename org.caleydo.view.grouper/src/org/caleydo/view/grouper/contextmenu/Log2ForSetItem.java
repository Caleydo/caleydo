package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.view.opengl.util.overlay.contextmenu.AContextMenuItem;

public class Log2ForSetItem extends AContextMenuItem {


	DataTable set;

	public Log2ForSetItem(DataTable set) {
		super();
		this.set = (DataTable)set;
		setText("Run log2 for set");

	}

	public void triggerEvent() {
		DataTableUtils.setExternalDataRepresentation((DataTable) set,
				EExternalDataRepresentation.LOG2, false);

	};
}
