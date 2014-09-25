/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;

/**
 * Interface for views that manage multiple table perspectives. </p>
 * <p>
 * Setting data containers can be achieved using the {@link AddTablePerspectivesEvent} and
 * {@link AddTablePerspectivesListener}.
 * </p>
 *
 * @author Alexander Lex
 */
public interface IMultiTablePerspectiveBasedView extends ITablePerspectiveBasedView {

	/** Adds a single data container to the view */
	public void addTablePerspective(TablePerspective newTablePerspective);

	/**
	 * Add a list of tablePerspectives to the view.
	 *
	 * @param newTablePerspectives
	 */
	public void addTablePerspectives(List<TablePerspective> newTablePerspectives);

	/** Returns all {@link TablePerspective}s of this view */
	@Override
	public List<TablePerspective> getTablePerspectives();

	/**
	 * Removes the table perspective from the view
	 *
	 * @param tablePerspective
	 */
	public void removeTablePerspective(TablePerspective tablePerspective);
}
