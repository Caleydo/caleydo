package org.caleydo.rcp.startup;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.DataInitializationData;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.core.serialize.SerializationManager;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.wizard.project.ChooseProjectTypePage;
import org.caleydo.rcp.wizard.project.ProjectMode;
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
		GeneralManager.get().getLogger()
			.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "Load sample project"));

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
//			Application.initializedStartViews = Application.initData.getViewIDs();
//			Application.ProjectMode = ProjectMode.LOAD_PROJECT;
//			SerializationManager.
//			Application.bDeleteRestoredWorkbenchState = true;
		}

		// CODE FROM APPLICATION.JAVA

		// IDataDomain dataDomain = initData.getDataDomain();
		// DataDomainManager.getInstance().register(dataDomain);
		//
		// if (!(dataDomain instanceof ISetBasedDataDomain))
		// throw new IllegalStateException(
		// "loading data is not supported for non-set-based data domains. Implement it!");
		//
		// ISetBasedDataDomain setBasedDataDomain = (ISetBasedDataDomain) dataDomain;
		//
		// LoadDataParameters loadDataParameters = dataDomain.getLoadDataParameters();
		// SetUtils.createStorages(loadDataParameters);
		// ISet set = SetUtils.createData(setBasedDataDomain);
		//
		// HashMap<ContentVAType, ContentVirtualArray> contentVAMap = initData.getContentVAMap();
		// for (Entry<ContentVAType, ContentVirtualArray> entry : contentVAMap.entrySet()) {
		// setBasedDataDomain.setContentVirtualArray(entry.getKey(), entry.getValue());
		// }
		//
		// HashMap<StorageVAType, StorageVirtualArray> storageVAMap = initData.getStorageVAMap();
		// for (Entry<StorageVAType, StorageVirtualArray> entry : storageVAMap.entrySet()) {
		// setBasedDataDomain.setStorageVirtualArray(entry.getKey(), entry.getValue());
		// }
		// // we need the VAs to be available before the tree is initialized
		// SetUtils.loadTrees(loadDataParameters, set);
		//
		// Application.initData = null;
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
