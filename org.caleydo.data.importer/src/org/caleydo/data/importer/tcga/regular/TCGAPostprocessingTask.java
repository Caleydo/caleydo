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
package org.caleydo.data.importer.tcga.regular;

import java.util.concurrent.RecursiveAction;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.data.importer.tcga.model.ClinicalMapping;
import org.caleydo.data.importer.tcga.model.TCGADataSet;

/**
 * utility task to create the initial table perspectives for the clinical data domain
 * 
 */
public class TCGAPostprocessingTask extends RecursiveAction {
	// private static final Logger log = Logger.getLogger(TCGAPostprocessingTask.class.getSimpleName());
	private static final long serialVersionUID = 7378867458430247164L;

	private final TCGADataSet dataSet;
	private TCGASettings settings;

	public TCGAPostprocessingTask(TCGADataSet dataSet, TCGASettings settings) {
		this.dataSet = dataSet;
		this.settings = settings;
	}

	@Override
	protected void compute() {
		if (dataSet.getDataDomain() == null)
			return;

		switch (dataSet.getType()) {
		case clinical:
			updateClinicalNames(dataSet.getDataDomain());
			break;
		default:
			// nothing to do up to now
		}
	}

	/**
	 * create initial table perspectives per column
	 *
	 * @param dataDomain
	 */
	private void updateClinicalNames(ATableBasedDataDomain dataDomain) {
		for (TablePerspective p : dataDomain.getAllTablePerspectives()) {
			if (p.getDimensionPerspective().isDefault())
				continue;
			String name = p.getLabel();
			ClinicalMapping mapping = ClinicalMapping.byName(name);
			if (mapping != null) {
				p.setLabel(mapping.getLabel());
				p.getDimensionPerspective().setLabel(mapping.getLabel());
				if (!p.getRecordPerspective().isDefault())
					p.getRecordPerspective().setLabel(mapping.getLabel());
			}
		}

	}


}
