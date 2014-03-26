/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer.tcga;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * This abstract class coordinates the whole workflow of creating a Caleydo
 * projects including the intermediate XML file generation and cleanup.
 *
 * @author Marc Streit
 *
 */
public abstract class AProjectBuilderApplication<S extends Settings>
	implements IApplication {

	private static final Logger log;

	static {
		System.setProperty("java.util.logging.config.file", "logging.properties");
		log = Logger.getLogger(AProjectBuilderApplication.class.getName());
	}

	@Override
	public final Object start(IApplicationContext context) throws Exception {
		log.info("start");
		S settings = parseArgs(context);
		log.info("settings:\n" + settings);

		GeneralManager.get().setDryMode(true);

		generateTCGAProjectFiles(settings);

		// FileOperations.deleteDirectory(tmpDataOutputPath);

		return context;
	}

	private S parseArgs(IApplicationContext context) {
		String[] args = (String[]) context.getArguments().get("application.args");
		S settings = createSettings();

		CmdLineParser parser = new CmdLineParser(settings);
		try {
			parser.parseArgument(args);
			if (!settings.validate()) {
				parser.printUsage(System.err);
				System.exit(1);
			}
		} catch (CmdLineException e) {
			log.log(Level.SEVERE, "parsing argument error: " + e.getMessage(), e);
			parser.printUsage(System.err);
			System.exit(1);
		}

		return settings;
	}

	protected abstract S createSettings();

	protected abstract List<? extends ForkJoinTask<Void>> createTasks(final S settings);

	private void generateTCGAProjectFiles(final S settings) {
		ForkJoinPool pool = new ForkJoinPool(settings.getNumThreads());

		RecursiveAction action = new RecursiveAction() {
			private static final long serialVersionUID = -8919058430905145146L;

			@Override
			protected void compute() {
				List<? extends ForkJoinTask<Void>> tasks = createTasks(settings);
				for(int i = 0; i < tasks.size(); i+=settings.getBatchSize()) {
					int to = Math.min(i + settings.getBatchSize(), tasks.size());
					invokeAll(tasks.subList(i, to));
					cleanupBatch();
				}
			}
		};
		pool.invoke(action);
		pool.shutdown();
	}

	protected void cleanupBatch() {
		for (IDMappingManager idMappingManager : IDMappingManagerRegistry.get().getAllIDMappingManager()) {
			idMappingManager.clearInternalMappingsAndIDTypes();
		}
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
