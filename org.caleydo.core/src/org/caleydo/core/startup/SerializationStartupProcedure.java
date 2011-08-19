package org.caleydo.core.startup;

import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.datadomain.ADataDomain;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.serialize.DataDomainSerializationData;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.core.serialize.ProjectSaver;
import org.caleydo.core.serialize.SerializationData;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SerializationStartupProcedure
	extends AStartupProcedure {

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
		}
		else {
			if (loadRecentProject) {
				loader.loadWorkbenchData(ProjectSaver.RECENT_PROJECT_FOLDER);
			}
			else if (projectLocation != null || projectLocation.isEmpty()) {
				loader.loadProjectFromZIP(projectLocation);
				loader.loadWorkbenchData(ProjectLoader.TEMP_PROJECT_ZIP_FOLDER);
			}
			else {
				throw new IllegalArgumentException("encountered unknown project-load-type");
			}
		}
	}
	
	@Override
	public void init(ApplicationInitData appInitData) {

		// not calling super.init() on purpose
		
		this.appInitData = appInitData;
		Logger.log(new Status(IStatus.INFO, this.toString(), "Load serialized project"));

		if (loadSampleProject) {
			serializationDataList = loader.loadProjectData(ProjectLoader.TEMP_PROJECT_ZIP_FOLDER);
		}
		else {
			if (loadRecentProject) {
				serializationDataList = loader.loadProjectData(ProjectSaver.RECENT_PROJECT_FOLDER);
			}
			else if (projectLocation != null || projectLocation.isEmpty()) {
				serializationDataList = loader.loadProjectData(ProjectLoader.TEMP_PROJECT_ZIP_FOLDER);
			}
			else {
				throw new IllegalArgumentException("encoutnered unknown project-load-type");
			}
		}
		
		deserializeData(serializationDataList);
	}


	private void deserializeData(SerializationData serializationDataList) {
		
		for (DataDomainSerializationData dataSerializationData : serializationDataList
			.getDataDomainSerializationDataList()) {
			ADataDomain dataDomain = dataSerializationData.getDataDomain();

			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain setBasedDataDomain = (ATableBasedDataDomain) dataDomain;

				LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
				loadDataParameters.setDataDomain(setBasedDataDomain);
				DataTableUtils.createDimensions(loadDataParameters);

				DataTable table = DataTableUtils.createData(setBasedDataDomain);

				HashMap<String, RecordVirtualArray> recordVAMap = dataSerializationData.getRecordDataMap();
				for (Entry<String, RecordVirtualArray> entry : recordVAMap.entrySet()) {
					setBasedDataDomain.setRecordVirtualArray(entry.getKey(), entry.getValue());
				}

				HashMap<String, DimensionVirtualArray> dimensionVAMap =
					dataSerializationData.getDimensionVAMap();
				for (Entry<String, DimensionVirtualArray> entry : dimensionVAMap.entrySet()) {
					setBasedDataDomain.setDimensionVirtualArray(entry.getKey(), entry.getValue());
				}

				// we need the VAs to be available before the tree is initialized
				DataTableUtils.loadTrees(loadDataParameters, table);
			}
		}
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
