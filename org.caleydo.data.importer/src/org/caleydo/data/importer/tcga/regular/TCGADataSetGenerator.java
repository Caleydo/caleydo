/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final Logger log = Logger.getLogger(TCGADataSetGenerator.class.getName());

	private final TCGASettings settings;
	private final FirehoseProvider fileProvider;

	private final TumorType tumorAbbreviation;
	private final String id;

	public TCGADataSetGenerator(TumorType tumorAbbreviation, FirehoseProvider fileProvider, TCGASettings settings) {
		this.tumorAbbreviation = tumorAbbreviation;
		this.fileProvider = fileProvider;
		this.settings = settings;
		this.id = String.format("%s@%s", tumorAbbreviation, fileProvider);
	}

	@Override
	protected TCGADataSets compute() {
		log.info(id + " start for data types: " + EDataSetType.values());
		Collection<ForkJoinTask<TCGADataSet>> tasks = new ArrayList<>();
		List<ForkJoinTask<? extends Object>> t = new ArrayList<>();

		for (EDataSetType type : EDataSetType.values()) {
			final ForkJoinTask<TCGADataSet> ti = TCGADataSetBuilder.create(type, fileProvider,
					settings.isSampleGenes(), settings);
			tasks.add(ti);
			t.add(ti);
		}

		// log.fine(id+" start mutsig task");
		//MutSigTask mutSigTask = new MutSigTask(fileProvider);
		//t.add(mutSigTask);

		invokeAll(t); // fork and wait

		TCGADataSets result = new TCGADataSets(tumorAbbreviation.getLabel());
		/*
		try {
			result.setMutsigParser(mutSigTask.get());
		} catch (InterruptedException e) {
			System.err.println(e.getMessage());
		} catch (ExecutionException e) {
			System.err.println(e.getMessage());
		}
		*/
		int i = -1;
		for (ForkJoinTask<TCGADataSet> task : tasks) {
			i++;
			try {
				TCGADataSet ds = task.get();
				if (ds == null) {
					log.warning(id + " " + EDataSetType.values()[i] + " delivered no result, skipping");
					continue;
				}
				result.add(ds);
			} catch (InterruptedException | ExecutionException e) {
				log.log(Level.SEVERE, id + " " + EDataSetType.values()[i] + " execution error: " + e, e);
			}
		}
		log.info(id + " " + result.size() + " datasets computed");
		return result;
	}
}
