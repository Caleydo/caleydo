package org.caleydo.rcp.startup;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.serialize.ProjectLoader;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.wizard.project.ChooseProjectTypePage;
import org.caleydo.rcp.wizard.project.ProjectMode;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

public class SerializationStartupProcedure
	extends AStartupProcedure {

	public static final String SAMPLE_PROJECT_LOCATION = "data/sample_project/sample_project.cal";

	private boolean loadSampleProject = false;

	@Override
	public void init() {
		GeneralManager.get().getLogger()
			.log(new Status(IStatus.INFO, Activator.PLUGIN_ID, "Load sample project"));

		ProjectLoader loader = new ProjectLoader();

		if (loadSampleProject) {
			Application.initData = loader.load(SAMPLE_PROJECT_LOCATION);

			// Application.initializedStartViews = Application.initData.getViewIDs();
			// Application.ProjectMode = ProjectMode.SAMPLE_PROJECT;
			Application.bDeleteRestoredWorkbenchState = true;
		}
		else {
			//
			// ProjectLoader loader = new ProjectLoader();
			// if (page.getProjectLoadType() == ChooseProjectTypePage.EProjectLoadType.RECENT) {
			// Application.initData = loader.loadRecent();
			// }
			// else if (page.getProjectLoadType() == ChooseProjectTypePage.EProjectLoadType.SPECIFIED) {
			// Application.initData = loader.load(page.getProjectFileName());
			// }
			// else {
			// throw new IllegalArgumentException("encoutnered unknown project-load-type");
			// }
			// //dataDomain = Application.initData.getDataDomain();
			// // Application.startViewWithDataDomain.clear();
			// Application.initializedStartViews = Application.initData.getViewIDs();
			// Application.ProjectMode = ProjectMode.LOAD_PROJECT;
			// Application.bDeleteRestoredWorkbenchState = true;
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
}
