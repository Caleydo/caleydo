package org.caleydo.core.startup;

import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.serialize.DataDomainSerializationData;
import org.caleydo.core.serialize.ProjectLoader;
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

	@Override
	public void init(ApplicationInitData appInitData) {

		// super.init(appInitData);

		this.appInitData = appInitData;
		SerializationData serializationDataList;
		Logger.log(new Status(IStatus.INFO, this.toString(), "Load sample project"));

		ProjectLoader loader = new ProjectLoader();

		if (loadSampleProject) {
			serializationDataList = loader.load(SAMPLE_PROJECT_LOCATION);
			Application.bDeleteRestoredWorkbenchState = true;
		}
		else {
			if (loadRecentProject) {
				serializationDataList = loader.loadRecent();
			}
			else if (projectLocation != null || projectLocation.isEmpty()) {
				serializationDataList = loader.load(projectLocation);
			}
			else {
				throw new IllegalArgumentException("encoutnered unknown project-load-type");
			}
		}

		for (DataDomainSerializationData dataSerializationData : serializationDataList
			.getDataSerializationDataList()) {
			ADataDomain dataDomain = dataSerializationData.getDataDomain();

			// Register data domain by hand because it restored from the serialization and not created via the
			// DataDomainManager
			DataDomainManager.get().register(dataDomain);

			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain setBasedDataDomain = (ATableBasedDataDomain) dataDomain;

				LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
				DataTableUtils.createDimensions(loadDataParameters);
				
				DataTable dataTable = DataTableUtils.createData(setBasedDataDomain);

				HashMap<String, ContentVirtualArray> contentVAMap = dataSerializationData.getContentVAMap();
				for (Entry<String, ContentVirtualArray> entry : contentVAMap.entrySet()) {
					setBasedDataDomain.setContentVirtualArray(entry.getKey(), entry.getValue());
				}

				HashMap<String, DimensionVirtualArray> dimensionVAMap = dataSerializationData.getDimensionVAMap();
				for (Entry<String, DimensionVirtualArray> entry : dimensionVAMap.entrySet()) {
					setBasedDataDomain.setDimensionVirtualArray(entry.getKey(), entry.getValue());
				}
				// we need the VAs to be available before the tree is initialized
				DataTableUtils.loadTrees(loadDataParameters, dataTable);
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
