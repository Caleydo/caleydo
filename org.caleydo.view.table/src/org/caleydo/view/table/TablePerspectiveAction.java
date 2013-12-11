/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.table;

import static org.caleydo.core.util.base.Runnables.show;
import static org.caleydo.core.util.base.Runnables.withinSWTThread;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.datadomain.TablePerspectiveActions.ITablePerspectiveFactory;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.gui.command.AOpenViewHandler;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Samuel Gratzl
 *
 */
public class TablePerspectiveAction implements ITablePerspectiveFactory {

	@Override
	public Collection<Pair<String, Runnable>> create(TablePerspective tablePerspective, Object sender) {
		List<Pair<String, Runnable>> r = new ArrayList<>();
		r.add(Pair.make("Show in Table",
				show(TableView.VIEW_TYPE, AOpenViewHandler.createSecondaryID() + "_lazy", tablePerspective)));
		r.add(export("Export Data", tablePerspective, null, null));
		final String recLabel = WordUtils.capitalizeFully(tablePerspective.getDataDomain().getRecordIDCategory()
				.getCategoryName());
		final String dimLabel = WordUtils.capitalizeFully(tablePerspective.getDataDomain().getDimensionIDCategory()
				.getCategoryName());
		if (ExportTablePerspectiveAction.hasGrouping(tablePerspective, EDimension.RECORD))
			r.add(export("Export " + recLabel + " Grouping Data", tablePerspective, null, EDimension.RECORD));
		if (ExportTablePerspectiveAction.hasGrouping(tablePerspective, EDimension.DIMENSION))
			r.add(export("Export " + dimLabel + " Grouping Data", tablePerspective, null, EDimension.DIMENSION));
		r.add(export("Export " + recLabel + " Identifiers", tablePerspective, EDimension.RECORD, null));
		r.add(export("Export " + dimLabel + " Identifiers", tablePerspective, EDimension.DIMENSION, null));
		return r;
	}

	private static Pair<String, Runnable> export(String label, TablePerspective tablePerspective,
			EDimension limitToIdentifiersOf, EDimension exportGroupingsOf) {
		return Pair.make(label, withinSWTThread(new ExportTablePerspectiveAction(tablePerspective,
				limitToIdentifiersOf, exportGroupingsOf)));
	}

}
