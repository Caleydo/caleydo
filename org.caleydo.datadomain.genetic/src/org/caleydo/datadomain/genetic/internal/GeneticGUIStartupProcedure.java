/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.datadomain.genetic.internal;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.caleydo.core.startup.AImportStartupProcedure;
import org.caleydo.datadomain.genetic.Activator;
import org.caleydo.datadomain.genetic.Organism;

/**
 * Startup procedure for project wizard.
 *
 * @author Marc Streit
 */
public class GeneticGUIStartupProcedure extends AImportStartupProcedure {

	private final Organism organism;
	private final boolean loadSampleData;

	// NOTE: change also organism when setting another dataset
	private static String REAL_DATA_SAMPLE_FILE = "data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public GeneticGUIStartupProcedure(Organism organism, boolean loadSampleData) {
		this.organism = organism;
		this.loadSampleData = loadSampleData;
	}

	@Override
	public boolean run() {
		Activator.setOrganism(organism);
		DataDomainManager.get().initalizeDataDomain("org.caleydo.datadomain.genetic");
		return super.run();
	}

	@Override
	protected DataImportWizard createDataImportWizard() {
		if (this.loadSampleData) {
			DataSetDescription dataSetDescription = new DataSetDescription(ECreateDefaultProperties.NUMERICAL);
			dataSetDescription.setDataSourcePath(REAL_DATA_SAMPLE_FILE);

			dataSetDescription.getDataDescription().getNumericalProperties()
					.setDataTransformation(NumericalTable.Transformation.LOG2);
			return new DataImportWizard(dataSetDescription);
		} else {
			return new DataImportWizard();
		}
	}
}
