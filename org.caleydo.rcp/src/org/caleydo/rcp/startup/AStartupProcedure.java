package org.caleydo.rcp.startup;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateDataDomain;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
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

//			// Force plugins of start views to load
//			try {
//				String viewType = viewWithDataDomain.getFirst();
//
//				if (!viewType.contains("unspecified")) {
//					if (viewType.contains("hierarchical"))
//						viewType = viewType.replace(".hierarchical", "");
//
//					Platform.getBundle(viewType).start();
//				}
//			}
//			catch (NullPointerException ex) {
//				System.out.println("Cannot load view plugin " + viewWithDataDomain.getFirst());
//				ex.printStackTrace();
//			}
//			catch (BundleException e) {
//				System.out.println("Cannot load view plugin " + viewWithDataDomain.getFirst());
//				e.printStackTrace();
//			}

			ASerializedView view =
				GeneralManager.get().getViewGLCanvasManager().getViewCreator(viewWithDataDomain.getFirst())
					.createSerializedView();

			view.setDataDomainType(viewWithDataDomain.getSecond());
			appInitData.getInitializedStartViews().add(viewWithDataDomain.getFirst());
		}
	}

	public void openRCPViews(IFolderLayout layout) {

		// Create RCP view manager
		RCPViewManager.get();

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
