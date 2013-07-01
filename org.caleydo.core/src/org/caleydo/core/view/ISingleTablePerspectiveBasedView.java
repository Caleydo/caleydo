/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;

/**
 * Interface for views that use a single {@link TablePerspective}
 * 
 * @author Alexander Lex
 * 
 */
public interface ISingleTablePerspectiveBasedView extends ITablePerspectiveBasedView,
		IDataDomainBasedView<ATableBasedDataDomain> {

	/**
	 * Set the data container for this view
	 */
	public void setTablePerspective(TablePerspective tablePerspective);

	/** Returns the data container of this view */
	public TablePerspective getTablePerspective();

	/** Returns the data container of this view as the only element in a list. */
	@Override
	public List<TablePerspective> getTablePerspectives();
}
