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
package org.caleydo.core.data.datadomain;

import org.caleydo.core.data.perspective.table.CategoricalTablePerspectiveCreator;

/**
 * helper class for encapsulating magic meta data information about a data domain
 *
 * @author Samuel Gratzl
 *
 */
public final class DataDomainOracle {
	private final static CategoricalTablePerspectiveCreator perspectiveCreator = new CategoricalTablePerspectiveCreator();

	public static boolean isCategoricalDataDomain(IDataDomain dataDomain) {
		if (dataDomain == null)
			return false;
		String label = dataDomain.getLabel();
		if (dataDomain.getLabel() == null)
			return false;
		return label.toLowerCase().contains("mutation") || label.toLowerCase().contains("copy");
	}

	public synchronized static void initDataDomain(ATableBasedDataDomain dataDomain) {
		if (isCategoricalDataDomain(dataDomain))
			perspectiveCreator.createAllTablePerspectives(dataDomain);
	}
}
