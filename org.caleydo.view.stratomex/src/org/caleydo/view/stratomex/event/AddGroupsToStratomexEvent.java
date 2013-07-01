/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex.event;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.configurer.IBrickConfigurer;
import org.caleydo.view.stratomex.brick.configurer.NumericalDataConfigurer;
import org.caleydo.view.stratomex.column.BrickColumn;

/**
 * <p>
 * The {@link AddGroupsToStratomexEvent} is an event that signals to add one or several {@link TablePerspective}s as
 * DimensionGroups to {@link GLStratomex}.
 * </p>
 * <p>
 * There are two ways to specify a group to be added:
 * <ol>
 * <li>by adding a list of pre-existing {@link TablePerspective}s</li>
 * <li>by specifying exactly one {@link TablePerspective}</li>
 * </ol>
 * </p>
 *
 * @author Alexander Lex
 *
 */
public class AddGroupsToStratomexEvent extends AddTablePerspectivesEvent {

	/**
	 * Optional member for determining a specialized data configurer that will be used in stratomex. If not specified,
	 * stratomex will use the {@link NumericalDataConfigurer}.
	 */
	private IBrickConfigurer dataConfigurer;
	/**
	 * The column that was used to create the table perspective for the new column to be added.
	 */
	private BrickColumn sourceColumn;

	public AddGroupsToStratomexEvent() {
	}

	/**
	 * Initialize event with a single data container
	 */
	public AddGroupsToStratomexEvent(TablePerspective tablePerspective) {
		super(tablePerspective);
	}

	/**
	 * Add a list of data containers, creating multiple dimension groups at the same time
	 *
	 * @param tablePerspectives
	 */
	public AddGroupsToStratomexEvent(List<TablePerspective> tablePerspectives) {
		super(tablePerspectives);
	}

	/**
	 * @param dataConfigurer
	 *            setter, see {@link #dataConfigurer}
	 */
	public void setDataConfigurer(IBrickConfigurer dataConfigurer) {
		this.dataConfigurer = dataConfigurer;
	}

	/**
	 * @return the dataConfigurer, see {@link #dataConfigurer}
	 */
	public IBrickConfigurer getDataConfigurer() {
		return dataConfigurer;
	}

	/**
	 * @param sourceColumn
	 *            setter, see {@link sourceColumn}
	 */
	public void setSourceColumn(BrickColumn sourceColumn) {
		this.sourceColumn = sourceColumn;
	}

	/**
	 * @return the sourceColumn, see {@link #sourceColumn}
	 */
	public BrickColumn getSourceColumn() {
		return sourceColumn;
	}
}
