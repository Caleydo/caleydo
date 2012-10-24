/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
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