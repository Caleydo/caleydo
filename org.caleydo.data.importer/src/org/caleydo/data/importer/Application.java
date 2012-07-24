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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.VAUtils;
import org.caleydo.core.data.virtualarray.delta.DimensionVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.io.DataLoader;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.DataSetDescriptionCollection;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ProjectSaver;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

/**
 * This class controls all aspects of the application's execution
 * 
 * @author Alexander Lex
 * @author Marc Streit
 * @author Nils Gehlenborg
 */
public class Application implements IApplication {

	private boolean useQuickClustering = true;

	/** {link JAXBContext} for DataTypeSet (de-)serialization */
	private JAXBContext context;

	private String dataSetDescriptionFilePath = "";

	@Override
	public Object start(IApplicationContext context) throws Exception {

		String[] runConfigParameters = (String[]) context.getArguments().get(
				"application.args");
		String outputCaleydoProjectFilePath = "";

		if (runConfigParameters == null || runConfigParameters.length != 2) {

			dataSetDescriptionFilePath = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "caleydo_data.xml";

			outputCaleydoProjectFilePath = System.getProperty("user.home")
					+ System.getProperty("file.separator") + "export_"
					+ (new SimpleDateFormat("yyyy.MM.dd_HH.mm").format(new Date()))
					+ ".cal";
		} else {
			outputCaleydoProjectFilePath = runConfigParameters[0];
			dataSetDescriptionFilePath = runConfigParameters[1];
		}

		GeneralManager.get().init();
		// FIXME: temp hack
		// GeneralManager.get().getBasicInfo().setOrganism(Organism.MUS_MUSCULUS);

		createJAXBContext();
		DataSetDescriptionCollection dataSetMetInfoCollection = deserialzeDataSetMetaInfo();

		// Iterate over data type sets and trigger processing
		for (DataSetDescription dataTypeSet : dataSetMetInfoCollection
				.getDataSetDescriptionCollection()) {
			loadSources(dataTypeSet);
		}
		// calculateVAIntersections();

		new ProjectSaver().save(outputCaleydoProjectFilePath, true);

		return IApplication.EXIT_OK;
	}

	private void calculateVAIntersections() {
		ArrayList<RecordVirtualArray> vasToIntersect = new ArrayList<RecordVirtualArray>(
				5);
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
			dataDomains.get(i).getTable()
					.registerRecordPerspective(intersectedPerspective);
		}
	}

	@Override
	public void stop() {
	}

	private void loadSources(DataSetDescription dataSetDescription)
			throws FileNotFoundException, IOException {

		DataLoader.loadData(dataSetDescription);

	}

	private void createSampleOfGenes(ATableBasedDataDomain dataDomain,
			PerspectiveInitializationData clusterResult) {
		if (clusterResult.getIndices().size() < 50)
			return;
		DimensionPerspective sampledDimensionPerspective = new DimensionPerspective(
				dataDomain);

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

	private void createJAXBContext() {
		try {
			Class<?>[] serializableClasses = new Class<?>[2];
			serializableClasses[0] = DataSetDescription.class;
			serializableClasses[1] = DataSetDescriptionCollection.class;
			context = JAXBContext.newInstance(serializableClasses);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}
	}

	private DataSetDescriptionCollection deserialzeDataSetMetaInfo() {

		DataSetDescriptionCollection dataTypeSetCollection = null;
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();

			dataTypeSetCollection = (DataSetDescriptionCollection) unmarshaller
					.unmarshal(new File(dataSetDescriptionFilePath));
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContexts", ex);
		}

		return dataTypeSetCollection;
	}

}