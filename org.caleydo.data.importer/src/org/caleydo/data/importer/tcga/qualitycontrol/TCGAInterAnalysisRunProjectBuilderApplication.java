/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.qualitycontrol;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinTask;

import org.caleydo.data.importer.tcga.AProjectBuilderApplication;
import org.caleydo.data.importer.tcga.EDataSetType;
import org.eclipse.equinox.app.IApplication;

public class TCGAInterAnalysisRunProjectBuilderApplication extends AProjectBuilderApplication<TCGAQCSettings> implements
		IApplication {

	@Override
	protected TCGAQCSettings createSettings() {
		return new TCGAQCSettings();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.caleydo.data.importer.tcga.AProjectBuilderApplication#createTasks(org.caleydo.data.importer.tcga.Settings)
	 */
	@Override
	protected List<ForkJoinTask<Void>> createTasks(TCGAQCSettings settings) {
		List<ForkJoinTask<Void>> tasks = new ArrayList<>();
		for (EDataSetType dataSetType : EDataSetType.values()) {
			tasks.add(new TCGAQCDataSetTypeTask(dataSetType, settings));
		}
		return tasks;
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
