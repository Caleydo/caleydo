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