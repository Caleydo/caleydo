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
package org.caleydo.core.startup;

import java.util.Map;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.serialize.DataDomainSerializationData;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SerializationStartupProcedure extends AStartupProcedure {

	public static final String SAMPLE_PROJECT_LOCATION = "data/sample_project/sample_project.cal";

	private String projectLocation = null;

	private boolean loadSampleProject = false;
	private boolean loadRecentProject = false;

	private SerializationData serializationDataList;

	private ProjectLoader loader = new ProjectLoader();

	@Override
	public void initPreWorkbenchOpen() {
		super.initPreWorkbenchOpen();

		if (loadSampleProject) {
			loader.loadProjectFromZIP(SAMPLE_PROJECT_LOCATION);
			loader.loadWorkbenchData(ProjectLoader.TEMP_PROJECT_ZIP_FOLDER);
		} else {
			if (loadRecentProject) {
				loader.loadWorkbenchData(ProjectSaver.RECENT_PROJECT_FOLDER);
			} else if (projectLocation != null || projectLocation.isEmpty()) {
				loader.loadProjectFromZIP(projectLocation);
				loader.loadWorkbenchData(ProjectLoader.TEMP_PROJECT_ZIP_FOLDER);
			} else {
				throw new IllegalArgumentException(
						"encountered unknown project-load-type");
			}
		}
	}

	@Override
	public void init(ApplicationInitData appInitData) {

		// not calling super.init() on purpose

		this.appInitData = appInitData;
		Logger.log(new Status(IStatus.INFO, this.toString(), "Load serialized project"));

		// FIXME: remove when TCGA data sets can store the pathway data domain
		appInitData.setLoadPathways(true);

		if (loadSampleProject) {
			serializationDataList = loader
					.loadProjectData(ProjectLoader.TEMP_PROJECT_ZIP_FOLDER);
		} else {
			if (loadRecentProject) {
				serializationDataList = loader
						.loadProjectData(ProjectSaver.RECENT_PROJECT_FOLDER);
			} else if (projectLocation != null || projectLocation.isEmpty()) {
				serializationDataList = loader
						.loadProjectData(ProjectLoader.TEMP_PROJECT_ZIP_FOLDER);
			} else {
				throw new IllegalArgumentException(
						"encoutnered unknown project-load-type");
			}
		}

		deserializeData(serializationDataList);
		generateSampleIntIDs();
	}

	private void deserializeData(SerializationData serializationDataList) {

		for (DataDomainSerializationData dataSerializationData : serializationDataList
				.getDataDomainSerializationDataList()) {
			ADataDomain dataDomain = dataSerializationData.getDataDomain();

			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain tDataDomain = (ATableBasedDataDomain) dataDomain;

				DataSetDescription dataSetDescription = dataDomain
						.getDataSetDescription();

				DataTableUtils.loadData(tDataDomain, dataSetDescription, false, false);
				DataTable table = tDataDomain.getTable();
				for (RecordPerspective perspective : dataSerializationData
						.getRecordPerspectiveMap().values()) {
					table.registerRecordPerspective(perspective);
				}
				for (DimensionPerspective perspective : dataSerializationData
						.getDimensionPerspectiveMap().values()) {
					table.registerDimensionPerspective(perspective);
				}
			}
		}
	}

	// FIXME this is probably the worst place ever for this and it's not general
	// at all
	private void generateSampleIntIDs() {
		IDCategory sampleIDCategory = IDCategory.getIDCategory("SAMPLE");
		IDType sampleIDType = IDType.getIDType("SAMPLE");
		IDType sampleIntIDType = IDType.getIDType("SAMPLE_INT");

		IDMappingManager idMappingManager = IDMappingManagerRegistry.get()
				.getIDMappingManager(sampleIDCategory);
		MappingType sampleMappingType = idMappingManager.createMap(sampleIDType,
				sampleIntIDType, false);
		Map<String, Integer> sampleIDMap = idMappingManager.getMap(sampleMappingType);

		// Merge SAMPLE maps from each data set to one
		int generatedSampleID = 0;
		for (DataDomainSerializationData dataSerializationData : serializationDataList
				.getDataDomainSerializationDataList()) {
			ADataDomain dataDomain = dataSerializationData.getDataDomain();

			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain tableDataDomain = (ATableBasedDataDomain) dataDomain;

				IDType sourceIDType;

				if (tableDataDomain.getRecordIDCategory() == sampleIDCategory)
					sourceIDType = tableDataDomain.getRecordIDType();
				else if (tableDataDomain.getDimensionIDCategory() == sampleIDCategory)
					sourceIDType = tableDataDomain.getDimensionIDType();
				else {
					Logger.log(new Status(Status.ERROR, this.toString(),
							"No sample ID Category specified"));
					continue;
				}

				MappingType mappingType = null;

				// FIXME marc look into this please!
				mappingType = idMappingManager.getMappingType(sampleIDType + "_2_"
						+ sourceIDType);

				if (mappingType == null) {
					Logger.log(new Status(Status.ERROR, this.toString(),
							"Could not create mappingType for: " + sampleIDType + " - "
									+ tableDataDomain.getRecordIDType()));
					continue;
				}
				for (Object sampleID : idMappingManager.getMap(mappingType).keySet()) {
					if (sampleIDMap.containsKey(sampleID))
						continue;

					sampleIDMap.put((String) sampleID, generatedSampleID++);
				}
			}
		}

		idMappingManager.createReverseMap(sampleMappingType);
	}

	public void loadSampleProject(boolean loadSampleProject) {
		this.loadSampleProject = loadSampleProject;
	}

	@Override
	public void addDefaultStartViews() {

		// no default views exist for serialization
	}

	public void setProjectLocation(String projectLocation) {
		this.projectLocation = projectLocation;
	}

	public void setLoadRecentProject(boolean loadRecentProject) {
		this.loadRecentProject = loadRecentProject;
	}

}
