/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.datadomain.genetic.internal;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescription.ECreateDefaultProperties;
import org.caleydo.core.io.gui.dataimport.wizard.DataImportWizard;
import org.caleydo.core.startup.ImportStartupProcedure;
import org.caleydo.datadomain.genetic.Activator;
import org.caleydo.datadomain.genetic.Organism;

/**
 * Startup procedure for project wizard.
 *
 * @author Marc Streit
 */
public class GeneticGUIStartupProcedure extends ImportStartupProcedure {

	private final Organism organism;
	private final boolean loadSampleData;

	// NOTE: change also organism when setting another dataset
	private static String REAL_DATA_SAMPLE_FILE = "data/genome/microarray/sample/HCC_sample_dataset_4630_24_cluster.csv";

	public GeneticGUIStartupProcedure(Organism organism, boolean loadSampleData) {
		this.organism = organism;
		this.loadSampleData = loadSampleData;
	}

	@Override
	public void run() {
		Activator.setOrganism(organism);
		DataDomainManager.get().initalizeDataDomain("org.caleydo.datadomain.genetic");
		super.run();
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
