package org.caleydo.core.startup;

import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.LoadDataParameters;
import org.caleydo.core.data.collection.table.DataTableUtils;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.serialize.DataInitializationData;
import org.caleydo.core.serialize.ProjectLoader;
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
		DataInitializationData data;
		Logger.log(new Status(IStatus.INFO, this.toString(), "Load sample project"));

		ProjectLoader loader = new ProjectLoader();

		if (loadSampleProject) {
			data = loader.load(SAMPLE_PROJECT_LOCATION);

			// Application.initializedStartViews = Application.initData.getViewIDs();
			// Application.ProjectMode = ProjectMode.SAMPLE_PROJECT;
			Application.bDeleteRestoredWorkbenchState = true;
		}
		else {
			if (loadRecentProject) {
				data = loader.loadRecent();
			}
			else if (projectLocation != null || projectLocation.isEmpty()) {
				data = loader.load(projectLocation);
			}
			else {
				throw new IllegalArgumentException("encoutnered unknown project-load-type");
			}
			// dataDomain = Application.initData.getDataDomain();
			// Application.startViewWithDataDomain.clear();
			// Application.initializedStartViews = Application.initData.getViewIDs();
			// Application.ProjectMode = ProjectMode.LOAD_PROJECT;
			// SerializationManager.
			// Application.bDeleteRestoredWorkbenchState = true;
		}

		// CODE FROM APPLICATION.JAVA

		ADataDomain dataDomain = data.getDataDomain();

		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain setBasedDataDomain = (ATableBasedDataDomain) dataDomain;

			LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
			DataTableUtils.createStorages(loadDataParameters);
			DataTable set = DataTableUtils.createData(setBasedDataDomain);

			HashMap<String, ContentVirtualArray> contentVAMap = data.getContentVAMap();
			for (Entry<String, ContentVirtualArray> entry : contentVAMap.entrySet()) {
				setBasedDataDomain.setContentVirtualArray(entry.getKey(), entry.getValue());
			}

			HashMap<String, StorageVirtualArray> storageVAMap = data.getStorageVAMap();
			for (Entry<String, StorageVirtualArray> entry : storageVAMap.entrySet()) {
				setBasedDataDomain.setStorageVirtualArray(entry.getKey(), entry.getValue());
			}
			// we need the VAs to be available before the tree is initialized
			DataTableUtils.loadTrees(loadDataParameters, set);
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
