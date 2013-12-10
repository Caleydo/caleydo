/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.table;

import java.util.Collection;
import java.util.Collections;

import org.caleydo.core.data.datadomain.TablePerspectiveActions.ITablePerspectiveFactory;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.util.base.ShowAndAddToViewAction;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Samuel Gratzl
 *
 */
public class TablePerspectiveAction implements ITablePerspectiveFactory {

	@Override
	public Collection<Pair<String, Runnable>> create(TablePerspective tablePerspective, Object sender) {
		final Pair<String, Runnable> p = Pair.make("Show As Table",
				Runnables.withinSWTThread(new ShowAndAddToViewAction(TableView.VIEW_TYPE, tablePerspective)));
		return Collections.singleton(p);
	}

}
