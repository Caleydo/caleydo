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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import com.martiansoftware.jsap.JSAP;

/**
 * This abstract class coordinates the whole workflow of creating a Caleydo
 * projects including the intermediate XML file generation and cleanup.
 * 
 * @author Marc Streit
 * 
 */
public abstract class AProjectBuilderApplication
	implements IApplication {

	protected String defaultTCGAServerURL;
	protected String caleydoWebstartURL;
	protected String defaultOutputFolderPath;
	protected String[] tumorTypes = null;
	protected String[] analysisRuns = null;
	protected String[] dataRuns = null;
	protected String outputPath = "";
	protected String tcgaServerURL = "";
	protected boolean sampleGenes = true;

	protected String reportJSONGenomicData = "";

	@Override
	public Object start(IApplicationContext context) throws Exception {

		handleProgramArguments(context);

		generateTCGAProjectFiles();

		// FileOperations.deleteDirectory(tmpDataOutputPath);

		return context;
	}

	protected abstract void handleProgramArguments(IApplicationContext context);

	protected abstract void generateTCGAProjectFiles();

	protected void handleJSAPError(JSAP jsap) {
		System.err.println("Error during parsing of program arguments. Closing program.");
		System.err.println("Usage: Caleydo");
		System.err.println(jsap.getUsage());
		System.err.println();
		System.exit(1);
	}

	private void replaceStringInFile(String oldstring, String newstring, File in, File out)
			throws IOException {

		BufferedReader reader = new BufferedReader(new FileReader(in));
		PrintWriter writer = new PrintWriter(new FileWriter(out));
		String line = null;
		while ((line = reader.readLine()) != null)
			writer.println(line.replaceAll(oldstring, newstring));
		reader.close();
		writer.close();
	}

	protected void cleanUp(String xmlFilePath, String jnlpOutputPath, String jnlpFileName,
			String jnlpRemoteOutputURL) {

		DataDomainManager.get().unregisterAllDataDomains();

		// Clean up
		new File(xmlFilePath).delete();

		try {
			// Generate jnlp file from jnlp template
			replaceStringInFile("CALEYDO_PROJECT_URL", jnlpRemoteOutputURL, new File(
					"resources/caleydo.jnlp"), new File(jnlpOutputPath + "_"));

			replaceStringInFile("JNLP_NAME", jnlpFileName, new File(jnlpOutputPath + "_"),
					new File(jnlpOutputPath));

			new File(jnlpOutputPath + "_").delete();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		for (IDMappingManager idMappingManager : IDMappingManagerRegistry.get()
				.getAllIDMappingManager()) {
			idMappingManager.clearInternalMappingsAndIDTypes();
		}
	}

	protected void writeJSONReport(String reportJSONOutputPath) {

		StringBuilder htmlBuilder = new StringBuilder();
		htmlBuilder.append(reportJSONGenomicData);

		FileWriter writer;
		try {
			writer = new FileWriter(reportJSONOutputPath);
			writer.write(htmlBuilder.toString());
			writer.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
