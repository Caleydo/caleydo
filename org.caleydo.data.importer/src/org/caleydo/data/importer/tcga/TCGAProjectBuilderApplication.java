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

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.importer.XMLToProjectBuilder;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class handles the whole workflow of creating a Caleydo project from TCGA
 * data.
 * 
 * @author Marc Streit
 * 
 */
public class TCGAProjectBuilderApplication
	implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {

		String tumorAbbreviation = "OV";
		String runIdentifier = "2012_05_25";

		String destinationPath = GeneralManager.CALEYDO_HOME_PATH + "/TCGA/"
				+ runIdentifier.replace("_", "") + "/" + tumorAbbreviation + "/";

		String xmlFilePath = destinationPath + tumorAbbreviation + "_" + runIdentifier
				+ ".xml";

		String projectOutputPath = destinationPath + tumorAbbreviation + "_" + runIdentifier
				+ ".cal";

		TCGADataXMLGenerator generator = new TCGADataXMLGenerator(tumorAbbreviation,
				runIdentifier, xmlFilePath);
		generator.run(generator.getOutputFilePath());

		XMLToProjectBuilder xmlToProjectBuilder = new XMLToProjectBuilder();
		xmlToProjectBuilder.buildProject(xmlFilePath, projectOutputPath);

		return context;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
}
