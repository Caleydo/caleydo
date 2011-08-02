package org.caleydo.view.grouper.contextmenu;

import org.caleydo.core.data.collection.ExternalDataRepresentation;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.view.contextmenu.ContextMenuItem;

public class Log2ForSetItem extends ContextMenuItem {

	private DataTable table;

	public Log2ForSetItem(DataTable table) {

		this.table = (DataTable)table;
		setLabel("Run log2 for set");
	}

	public void triggerEvent() {
		DataTableUtils.setExternalDataRepresentation((DataTable) table,
				ExternalDataRepresentation.LOG2, false);
	};
}
