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
import org.caleydo.core.gui.command.AOpenViewHandler;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.util.collection.Pair;

/**
 * @author Samuel Gratzl
 *
 */
public class TablePerspectiveAction implements ITablePerspectiveFactory {

	@Override
	public Collection<Pair<String, Runnable>> create(TablePerspective tablePerspective, Object sender) {
		final Pair<String, Runnable> p = Pair.make("Show in Table",
				Runnables.show(TableView.VIEW_TYPE, AOpenViewHandler.createSecondaryID() + "_lazy", tablePerspective));
		return Collections.singleton(p);
	}

}
