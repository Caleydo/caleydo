package org.caleydo.rcp.dialog;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.rcp.action.toolbar.general.StartClusteringAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * File dialog for starting clustering
 * 
 * @author Bernhard Schlegl
 */
public class StartClusteringDialog
	extends Dialog {

	private StartClusteringAction startClusteringAction;

	/**
	 * Constructor.
	 */
	public StartClusteringDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(StartClusteringAction.TEXT);
		newShell.setImage(GeneralManager.get().getResourceLoader().getImage(newShell.getDisplay(),
			StartClusteringAction.ICON));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		startClusteringAction = new StartClusteringAction(parent);
		startClusteringAction.run();
		return parent;
	}

	@Override
	protected void okPressed() {
		startClusteringAction.execute(false);

		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		startClusteringAction.execute(true);

		super.cancelPressed();
	}

	public ClusterState getClusterState() {
		return startClusteringAction.getClusterState();
	}
}
