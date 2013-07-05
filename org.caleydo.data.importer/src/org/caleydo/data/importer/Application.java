/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.data.importer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 * 
 * @author Alexander Lex
 * @author Marc Streit
 * @author Nils Gehlenborg
 */
public class Application
	implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {

		XMLToProjectBuilder projectBuilder = new XMLToProjectBuilder();

		String[] programArguments = (String[]) context.getArguments().get("application.args");

		String projectFileOutputPath = "";
		String xmlInputPath = "";
		String reportFileOutputPath = "";

		if (programArguments == null || programArguments.length != 3) {

			xmlInputPath = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "caleydo_data.xml";

			projectFileOutputPath = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "export_"
					+ (new SimpleDateFormat("yyyy.MM.dd_HH.mm").format(new Date())) + ".cal";

			reportFileOutputPath = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "index.html";
		}
		else {
			xmlInputPath = programArguments[0];
			projectFileOutputPath = programArguments[1];
			reportFileOutputPath = programArguments[2];
		}

		projectBuilder.buildProject(xmlInputPath, projectFileOutputPath);

		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
	}
}
