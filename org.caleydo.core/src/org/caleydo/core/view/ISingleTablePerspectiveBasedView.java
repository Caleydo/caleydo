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
public interface ISingleTablePerspectiveBasedView extends ITablePerspectiveBasedView,  IDataDomainBasedView<ATableBasedDataDomain> {

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
