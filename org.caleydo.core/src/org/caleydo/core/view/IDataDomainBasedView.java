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

import org.caleydo.core.data.datadomain.IDataDomain;

/**
 * This is obsolete for {@link ITablePerspectiveBasedView}s. We need to think
 * what we want to do with the others.
 * 
 * @author Alexander Lex
 * 
 * @param <DataDomainType>
 */
@Deprecated
public interface IDataDomainBasedView<DataDomainType extends IDataDomain> {

	/**
	 * Set the data domain which determines the behavior of the view. Attention:
	 * The data domain need not be changed at runtime.
	 * 
	 * @param dataDomain
	 */
	public void setDataDomain(DataDomainType dataDomain);

	/**
	 * Get the data domain the view is operating on
	 * 
	 * @return
	 */
	public DataDomainType getDataDomain();

}
