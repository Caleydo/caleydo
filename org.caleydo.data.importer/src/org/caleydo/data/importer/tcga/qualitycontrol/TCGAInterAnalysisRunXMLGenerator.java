/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.qualitycontrol;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.caleydo.data.importer.tcga.EDataSetType;
import org.caleydo.data.importer.tcga.FirehoseProvider;
import org.caleydo.data.importer.tcga.TCGADataSetBuilder;
import org.caleydo.data.importer.tcga.model.TCGADataSet;
import org.caleydo.data.importer.tcga.model.TCGADataSets;
import org.caleydo.data.importer.tcga.model.TumorType;

/**
 * Generator class that writes the loading information of a series of TCGA data
 * sets to an XML file.
 *
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGAInterAnalysisRunXMLGenerator extends RecursiveTask<TCGADataSets> {
	private static final long serialVersionUID = -7056378841113169134L;

	private final EDataSetType dataSetType;
	private final TCGAQCSettings settings;
	private final boolean loadSampledGenes = true;

	private final TumorType tumor;

	public TCGAInterAnalysisRunXMLGenerator(TumorType tumor, EDataSetType dataSetType, TCGAQCSettings settings) {
		this.settings = settings;
		this.tumor = tumor;
		this.dataSetType = dataSetType;
	}

	@Override
	public TCGADataSets compute() {
		Collection<ForkJoinTask<TCGADataSet>> tasks = new ArrayList<>();

		List<Date> analysisRuns = settings.getAnalysisRuns();
		List<Date> dataRuns = settings.getDataRuns();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		for (int i = 0; i < analysisRuns.size(); i++) {
			Date analysisRun = analysisRuns.get(i);
			Date dataRun = dataRuns.get(i);
			FirehoseProvider fileProvider = settings.createFirehoseProvider(tumor, analysisRun, dataRun);

			tasks.add(TCGADataSetBuilder.create(this.dataSetType, df.format(analysisRun), fileProvider,
					loadSampledGenes,
					settings));
		}

		TCGADataSets projectDescription = new TCGADataSets(dataSetType.getName());

		for (ForkJoinTask<TCGADataSet> task : invokeAll(tasks)) {
			try {
				TCGADataSet ds = task.get();
				if (ds == null)
					continue;
				projectDescription.add(ds);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			} catch (ExecutionException e) {
				System.err.println(e.getMessage());
			}
		}
		return projectDescription;
	}
}
