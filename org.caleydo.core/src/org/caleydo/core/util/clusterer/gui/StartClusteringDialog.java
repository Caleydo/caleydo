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
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.io.gui.IDataOKListener;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.clusterer.initialization.AClusterConfiguration;
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
public class StartClusteringDialog extends TrayDialog implements IDataOKListener{

	private StartClusteringDialogAction startClusteringAction;

	private ATableBasedDataDomain dataDomain;

	private RecordPerspective recordPerspective;
	private DimensionPerspective dimensionPerspective;

	public StartClusteringDialog(Shell parentShell) {
		super(parentShell);
	
	}

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
		
		startClusteringAction = new StartClusteringDialogAction(this, parent, dataDomain,
				dimensionPerspective, recordPerspective);
		startClusteringAction.run();
		
		return parent;
	}
	
	@Override
	protected Control createContents(Composite parent) {		
		Control control = super.createContents(parent);
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
	public AClusterConfiguration getClusterState() {
		return startClusteringAction.getClusterState();
		
	}

	@Override
	public void dataOK() {
		getButton(OK).setEnabled(true);
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}

}
