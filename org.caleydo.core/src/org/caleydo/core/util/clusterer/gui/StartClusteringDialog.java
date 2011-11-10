package org.caleydo.core.util.clusterer.gui;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * File dialog for starting clustering
 * 
 * @author Bernhard Schlegl
 */
public class StartClusteringDialog
	extends TrayDialog {

	private StartClusteringDialogAction startClusteringAction;

	private ATableBasedDataDomain dataDomain;

	private RecordPerspective recordPerspective;
	private DimensionPerspective dimensionPerspective;

	/**
	 * Constructor.
	 */
	public StartClusteringDialog(Shell parentShell, ATableBasedDataDomain dataDomain) {
		super(parentShell);
		this.dataDomain = dataDomain;
	}

	/**
	 * @param recordPerspective
	 *            setter, see {@link #recordPerspective}
	 */
	public void setRecordPerspective(RecordPerspective recordPerspective) {
		this.recordPerspective = recordPerspective;

	}

	/**
	 * @param dimensionPerspective
	 *            setter, see {@link #dimensionPerspective}
	 */
	public void setDimensionPerspective(DimensionPerspective dimensionPerspective) {
		this.dimensionPerspective = dimensionPerspective;

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		newShell.setText(StartClusteringDialogAction.TEXT);
		newShell.setImage(GeneralManager.get().getResourceLoader()
			.getImage(newShell.getDisplay(), StartClusteringDialogAction.ICON));

		TrayDialog trayDialog = (TrayDialog) newShell.getData();
		trayDialog.setHelpAvailable(true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		startClusteringAction = new StartClusteringDialogAction(parent, dataDomain);
		startClusteringAction.setDimensionPerspective(dimensionPerspective);
		startClusteringAction.setRecordPerspective(recordPerspective);
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

	/**
	 * Returns the ClusterState as determined by the Cluster Dialog, or null if the dialog was canceled.
	 * 
	 * @return
	 */
	public ClusterConfiguration getClusterState() {
		return startClusteringAction.getClusterState();
	}

}
