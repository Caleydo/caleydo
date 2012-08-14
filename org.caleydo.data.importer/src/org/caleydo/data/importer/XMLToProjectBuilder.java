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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VAUtils;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.ProjectDescription;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectSaver;

/**
 * This class handles the creation of Caleydo project files. The class takes an
 * XML file as input.
 * 
 * @author Alexander Lex
 * @author Marc Streit
 * @author Nils Gehlenborg
 */
public class XMLToProjectBuilder {

	/** {link JAXBContext} for DataTypeSet (de-)serialization */
	private JAXBContext context;

	private String dataSetDescriptionFilePath = "";

	public void buildProject(String xmlInputPath, String projectFileOutputPath) {

		dataSetDescriptionFilePath = xmlInputPath;

		GeneralManager.get().setDryMode(true);

		createJAXBContext();
		ProjectDescription dataSetMetInfoCollection = deserialzeDataSetMetaInfo();

		try {
			// Iterate over data type sets and trigger processing
			for (DataSetDescription dataSetDescription : dataSetMetInfoCollection
					.getDataSetDescriptionCollection()) {

				DataLoader.loadData(dataSetDescription);
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		new ProjectSaver().save(projectFileOutputPath, true);
	}

	private void createJAXBContext() {
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataSetDescription.class;
			serializableClasses[1] = ProjectDescription.class;
			context = JAXBContext.newInstance(serializableClasses);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private ProjectDescription deserialzeDataSetMetaInfo() {

		ProjectDescription dataTypeSetCollection = null;
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();

			dataTypeSetCollection = (ProjectDescription) unmarshaller.unmarshal(new File(
					dataSetDescriptionFilePath));
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}

		return dataTypeSetCollection;
	}

	private void calculateVAIntersections() {
		ArrayList<RecordVirtualArray> vasToIntersect = new ArrayList<RecordVirtualArray>(5);
		// int loopCount = 0;
		ArrayList<ATableBasedDataDomain> dataDomains = DataDomainManager.get()
				.getDataDomainsByType(ATableBasedDataDomain.class);
		for (ATableBasedDataDomain dataDomain : dataDomains) {
			vasToIntersect.add(dataDomain.getTable().getDefaultRecordPerspective()
					.getVirtualArray());
		}
		List<RecordVirtualArray> intersectedVAs = VAUtils
				.createIntersectingVAs(vasToIntersect);

		for (int i = 0; i < dataDomains.size(); i++) {
			PerspectiveInitializationData data = new PerspectiveInitializationData();
			data.setData(intersectedVAs.get(i));
			RecordPerspective intersectedPerspective = new RecordPerspective(
					dataDomains.get(i));
			intersectedPerspective.setLabel("Intersected 4 Clusters", false);
			intersectedPerspective.setIDType(intersectedVAs.get(i).getIdType());
			intersectedPerspective.init(data);
			dataDomains.get(i).getTable().registerRecordPerspective(intersectedPerspective);
		}
	}

	private void createSampleOfGenes(ATableBasedDataDomain dataDomain,
			PerspectiveInitializationData clusterResult) {
		if (clusterResult.getIndices().size() < 50)
			return;
		DimensionPerspective sampledDimensionPerspective = new DimensionPerspective(dataDomain);

		sampledDimensionPerspective.init(clusterResult);

		DimensionVADelta delta = new DimensionVADelta();
		DimensionVirtualArray va = sampledDimensionPerspective.getVirtualArray();
		int moduloFactor = va.size() / 50;
		for (int vaIndex = 0; vaIndex < va.size(); vaIndex++) {
			if (vaIndex % moduloFactor != 0)
				delta.add(VADeltaItem.removeElement(va.get(vaIndex)));
		}

		sampledDimensionPerspective.setVADelta(delta);

		sampledDimensionPerspective.setLabel("Clustered, sampled genes, size: "
				+ sampledDimensionPerspective.getVirtualArray().size(), false);
		dataDomain.getTable().registerDimensionPerspective(sampledDimensionPerspective);
	}
}
