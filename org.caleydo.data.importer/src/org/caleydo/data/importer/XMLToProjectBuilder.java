/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
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
			ATableBasedDataDomain dataDomain = DataLoader.loadData(dataSetDescription, new NullProgressMonitor());
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
