/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.data.importer.tcga.regular;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.manager.GeneralManager;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * helper main just for downloading without a real rcp application and a different build path
 * 
 * @author Samuel Gratzl
 *
 */
public class Main {
	public static void main(String[] args) {
		TCGASettings settings = parseArgs(args);

		GeneralManager.get().setDryMode(true);

		generateTCGAProjectFiles(settings);

		// FileOperations.deleteDirectory(tmpDataOutputPath);

	}

	private static TCGASettings parseArgs(String[] args) {
		TCGASettings settings = new TCGASettings();

		CmdLineParser parser = new CmdLineParser(settings);
		try {
			parser.parseArgument(args);
			if (!settings.validate()) {
				parser.printUsage(System.err);
				System.exit(1);
			}
		} catch (CmdLineException e) {
			// log.log(Level.SEVERE, "parsing argument error: " + e.getMessage(), e);
			parser.printUsage(System.err);
			System.exit(1);
		}

		return settings;
	}


	protected static List<ForkJoinTask<Void>> createTasks(TCGASettings settings) {
		List<ForkJoinTask<Void>> tasks = new ArrayList<>();
		List<Date> analysisRuns = settings.getAnalysisRuns();
		List<Date> dataRuns = settings.getDataRuns();
		for (int i = 0; i < analysisRuns.size(); i++) {
			Date analysisRun = analysisRuns.get(i);
			Date dataRun = dataRuns.get(i);

			tasks.add(new TCGARunTask(analysisRun, dataRun, settings));
		}
		return tasks;
	}

	private static void generateTCGAProjectFiles(final TCGASettings settings) {
		ForkJoinPool pool = new ForkJoinPool(settings.getNumThreads());

		RecursiveAction action = new RecursiveAction() {
			private static final long serialVersionUID = -8919058430905145146L;

			@Override
			protected void compute() {
				List<? extends ForkJoinTask<Void>> tasks = createTasks(settings);
				for (int i = 0; i < tasks.size(); i += settings.getBatchSize()) {
					int to = Math.min(i + settings.getBatchSize(), tasks.size());
					invokeAll(tasks.subList(i, to));
					cleanupBatch();
				}
			}
		};
		pool.invoke(action);
		pool.shutdown();
	}

	protected static void cleanupBatch() {
		for (IDMappingManager idMappingManager : IDMappingManagerRegistry.get().getAllIDMappingManager()) {
			idMappingManager.clearInternalMappingsAndIDTypes();
		}
	}
}
