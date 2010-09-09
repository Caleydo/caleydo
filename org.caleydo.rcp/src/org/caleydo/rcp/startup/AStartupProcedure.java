package org.caleydo.rcp.startup;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.rcp.view.RCPViewManager;
import org.eclipse.ui.IFolderLayout;

/**
 * Abstract startup procedure. Handling of view initialization and application init data.
 * 
 * @author Marc Streit
 */
public abstract class AStartupProcedure {

	protected ApplicationInitData appInitData;

	public void init(ApplicationInitData appInitData) {
		this.appInitData = appInitData;
		initializeStartViews();
	}

	public void execute() {
		loadPathways();
		
		// Create RCP view manager
		RCPViewManager.get();
	}

	public abstract void addDefaultStartViews();

	/**
	 * Parses through the list of start-views to initialize them by creating default serialized
	 * representations of them.
	 */
	public void initializeStartViews() {
		// Create view list dynamically when not specified via the command line

		addDefaultStartViews();

		for (Pair<String, String> viewWithDataDomain : appInitData.getAppArgumentStartViewWithDataDomain()) {

//			ASerializedView view =
//				GeneralManager.get().getViewGLCanvasManager().getViewCreator(viewWithDataDomain.getFirst())
//					.createSerializedView();
//
//			view.setDataDomainType(viewWithDataDomain.getSecond());
			appInitData.getInitializedStartViews().add(viewWithDataDomain.getFirst());
		}
	}

	public void openRCPViews(IFolderLayout layout) {

		for (String startViewID : appInitData.getInitializedStartViews()) {
			layout.addView(startViewID);
		}
	}

	private void loadPathways() {
		if (!appInitData.isLoadPathways())
			return;

		CmdDataCreateDataDomain cmd = new CmdDataCreateDataDomain(ECommandType.CREATE_DATA_DOMAIN);
		cmd.setAttributes("org.caleydo.datadomain.pathway");
		cmd.doCommand();
	}
}
