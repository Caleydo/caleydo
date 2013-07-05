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
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.gui.util.WithinSWTThread;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.GroupingParseSpecification;
import org.eclipse.swt.widgets.Shell;

/**
 * command for importing a group
 *
 * @author Samuel Gratzl
 *
 */
public class ImportGroupingCommand implements Runnable {
	private final IDCategory idCategory;
	private final ATableBasedDataDomain dataDomain;

	public ImportGroupingCommand(IDCategory idCategory, ATableBasedDataDomain dataDomain) {
		this.idCategory = idCategory;
		this.dataDomain = dataDomain;
	}

	@WithinSWTThread
	@Override
	public void run() {
		ImportGroupingDialog d = new ImportGroupingDialog(new Shell(), idCategory);
		GroupingParseSpecification spec = d.call();
		if (spec == null)
			return;
		DataSetDescription dataSetDescription = dataDomain.getDataSetDescription();
		if (dataDomain.getRecordIDCategory() == idCategory) {
			if (dataDomain.isColumnDimension()) {
				dataSetDescription.addRowGroupingSpecification(spec);
			} else {
				dataSetDescription.addColumnGroupingSpecification(spec);
			}
		} else {
			if (dataDomain.isColumnDimension()) {
				dataSetDescription.addColumnGroupingSpecification(spec);
			} else {
				dataSetDescription.addRowGroupingSpecification(spec);
			}
		}
		DataLoader.loadGrouping(dataDomain, spec);
	}
}
