/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import java.util.ArrayList;
import java.util.Collection;
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
 * Generator class that writes the loading information of a series of TCGA data sets to an XML file.
 *
 * @author Nils Gehlenborg
 * @author Alexander Lex
 * @author Marc Streit
 */
public class TCGADataSetGenerator extends RecursiveTask<TCGADataSets> {
	private static final long serialVersionUID = 7866075803605970224L;

	private final TCGASettings settings;
	private final FirehoseProvider fileProvider;

	private final TumorType tumorAbbreviation;

	public TCGADataSetGenerator(TumorType tumorAbbreviation, FirehoseProvider fileProvider, TCGASettings settings) {
		this.tumorAbbreviation = tumorAbbreviation;
		this.fileProvider = fileProvider;
		this.settings = settings;
	}

	@Override
	protected TCGADataSets compute() {
		Collection<ForkJoinTask<TCGADataSet>> tasks = new ArrayList<>();

		for (EDataSetType type : EDataSetType.values()) {
			tasks.add(TCGADataSetBuilder.create(type, fileProvider, settings.isSampleGenes(),
					settings));
		}

		invokeAll(tasks); // fork and wait

		TCGADataSets result = new TCGADataSets(tumorAbbreviation.getLabel());
		for (ForkJoinTask<TCGADataSet> task : tasks) {
			try {
				TCGADataSet ds = task.get();
				if (ds == null)
					continue;
				result.add(ds);
			} catch (InterruptedException e) {
				System.err.println(e.getMessage());
			} catch (ExecutionException e) {
				System.err.println(e.getMessage());
			}
		}

		return result;
	}
}
