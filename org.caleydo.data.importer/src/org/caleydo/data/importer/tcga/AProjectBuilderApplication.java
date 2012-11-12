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
package org.caleydo.data.importer.tcga;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
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

	@Override
	public final Object start(IApplicationContext context) throws Exception {
		S settings = parseArgs(context);

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
			System.err.println("Error parsing arguments: " + e.getMessage());
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
