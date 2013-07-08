/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
/**
 *
 */
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;

/**
 * <p>
 * Base interface for views that use a single or multiple
 * {@link TablePerspective} s.
 * </p>
 * <p>
 * Generally it's preferred to use {@link ISingleTablePerspectiveBasedView} or
 * {@link IMultiTablePerspectiveBasedView} instead of this interface.
 * </p>
 *
 * @author Alexander Lex
 */
public interface ITablePerspectiveBasedView extends IView {

	/**
	 * Returns all {@link TablePerspective}s that this view and all of its
	 * possible remote views contain.
	 *
	 * @return
	 */
	public List<TablePerspective> getTablePerspectives();

	/**
	 *
	 * @return The {@link IDataSupportDefinition} that specifies which
	 *         {@link IDataDomain}s are supported by the view.
	 */
	public IDataSupportDefinition getDataSupportDefinition();
}
