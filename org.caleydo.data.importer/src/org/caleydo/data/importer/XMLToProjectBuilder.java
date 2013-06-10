/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.data.importer;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectManager;
import org.caleydo.core.serialize.ProjectMetaData;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * This class handles the creation of Caleydo project files. The class takes an XML file as input.
 *
 * @author Alexander Lex
 * @author Marc Streit
 * @author Nils Gehlenborg
 */
public class XMLToProjectBuilder {
	public Collection<ATableBasedDataDomain> buildProject(ProjectDescription projectDescription,
			String projectFileOutputPath, ProjectMetaData metaData) {
		GeneralManager.get().setDryMode(true);

		Collection<ATableBasedDataDomain> dataDomains = new ArrayList<>();

		// Iterate over data type sets and trigger processing
		for (DataSetDescription dataSetDescription : projectDescription.getDataSetDescriptionCollection()) {
			ATableBasedDataDomain dataDomain = DataLoader.loadData(dataSetDescription, null);
			if (dataDomain != null)
				dataDomains.add(dataDomain);
			else
				throw new RuntimeException("Failed to load datadomain " + dataSetDescription);
		}

		try {
			ProjectManager.save(projectFileOutputPath, true, dataDomains, metaData).run(new NullProgressMonitor());
		} catch (InvocationTargetException | InterruptedException e) {
			throw new RuntimeException("Can't save project", e);
		}

		return dataDomains;
	}

	public void buildProject(String xmlInputPath, String projectFileOutputPath) {
		buildProject(deserialzeDataSetMetaInfo(xmlInputPath), projectFileOutputPath, ProjectMetaData.createDefault());
	}

	private static JAXBContext createJAXBContext() {
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataSetDescription.class;
			serializableClasses[1] = ProjectDescription.class;
			return JAXBContext.newInstance(serializableClasses);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private ProjectDescription deserialzeDataSetMetaInfo(String file) {
		JAXBContext context = createJAXBContext();

		ProjectDescription dataTypeSetCollection = null;
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();

			dataTypeSetCollection = (ProjectDescription) unmarshaller.unmarshal(new File(file));
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}

		return dataTypeSetCollection;
	}
}
