/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.util.clusterer.gui;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.io.gui.IDataOKListener;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.initialization.ClusterConfiguration;
import org.caleydo.core.util.link.LinkHandler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * File dialog for starting clustering
 * 
 * @author Bernhard Schlegl
 */
public class StartClusteringDialog extends AHelpButtonDialog implements IDataOKListener {

	private StartClusteringDialogAction startClusteringAction;

	private ATableBasedDataDomain dataDomain;
	private ClusterConfiguration clusterConfiguration;

	public StartClusteringDialog(Shell parentShell) {
		super(parentShell);

	}

	public StartClusteringDialog(Shell parentShell, ATableBasedDataDomain dataDomain) {
		super(parentShell);
		this.dataDomain = dataDomain;
		clusterConfiguration = new ClusterConfiguration();
	}

	/**
	 * Constructor with {@link ClusterConfiguration} for pre-setting values
	 */
	public StartClusteringDialog(Shell parentShell, ATableBasedDataDomain dataDomain,
			ClusterConfiguration clusterConfiguration) {
		super(parentShell);
		this.dataDomain = dataDomain;
		this.clusterConfiguration = clusterConfiguration;
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

		startClusteringAction = new StartClusteringDialogAction(this, parent, dataDomain,
				clusterConfiguration);
		startClusteringAction.run();

		return parent;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		if (!startClusteringAction.isDataOK())
			getButton(IDialogConstants.OK_ID).setEnabled(false);
		return control;

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
	 * Returns the ClusterState as determined by the Cluster Dialog, or null if
	 * the dialog was canceled.
	 * 
	 * @return
	 */
	public ClusterConfiguration getClusterConfiguration() {
		return startClusteringAction.getClusterState();

	}

	@Override
	public void dataOK() {
		getButton(OK).setEnabled(true);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.caleydo.core.gui.util.AHelpButtonDialog#helpPressed()
	 */
	@Override
	protected void helpPressed() {
		LinkHandler
				.openLink("http://www.icg.tugraz.at/project/caleydo/help/manipulating-data#clustering");
	}

}
