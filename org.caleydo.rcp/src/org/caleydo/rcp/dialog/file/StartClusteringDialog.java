package org.caleydo.rcp.dialog.file;

import org.caleydo.core.util.clusterer.ClusterState;
import org.caleydo.rcp.action.toolbar.general.StartClusteringAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Monitor;
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

		parentShell.setText("Open project file");
		Monitor primary = parentShell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = parentShell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		parentShell.setLocation(x, y);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Start Clustering");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		startClusteringAction = new StartClusteringAction(parent);
		startClusteringAction.run();
		return parent;
	}

	@Override
	protected void okPressed() {
		startClusteringAction.execute();

		super.okPressed();
	}

	public ClusterState getClusterState() {
		return startClusteringAction.getClusterState();
	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		StartClusteringDialog dialog = new StartClusteringDialog(new Shell());
		dialog.open();
	}
}
