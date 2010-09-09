package org.caleydo.rcp.startup;

import java.util.HashMap;
import java.util.Map.Entry;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.LoadDataParameters;
import org.caleydo.core.data.collection.set.SetUtils;
import org.caleydo.core.data.virtualarray.ContentVAType;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.StorageVAType;
import org.caleydo.core.data.virtualarray.StorageVirtualArray;
import org.caleydo.core.manager.datadomain.ADataDomain;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.serialize.DataInitializationData;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.rcp.Application;
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

		if (dataDomain instanceof ASetBasedDataDomain) {
			ASetBasedDataDomain setBasedDataDomain = (ASetBasedDataDomain) dataDomain;

			LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
			SetUtils.createStorages(loadDataParameters);
			ISet set = SetUtils.createData(setBasedDataDomain);

			HashMap<ContentVAType, ContentVirtualArray> contentVAMap = data.getContentVAMap();
			for (Entry<ContentVAType, ContentVirtualArray> entry : contentVAMap.entrySet()) {
				setBasedDataDomain.setContentVirtualArray(entry.getKey(), entry.getValue());
			}

			HashMap<StorageVAType, StorageVirtualArray> storageVAMap = data.getStorageVAMap();
			for (Entry<StorageVAType, StorageVirtualArray> entry : storageVAMap.entrySet()) {
				setBasedDataDomain.setStorageVirtualArray(entry.getKey(), entry.getValue());
			}
			// we need the VAs to be available before the tree is initialized
			SetUtils.loadTrees(loadDataParameters, set);
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
