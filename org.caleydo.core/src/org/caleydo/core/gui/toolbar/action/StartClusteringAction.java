/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar.action;

import org.caleydo.core.gui.SimpleAction;
import org.caleydo.core.util.clusterer.gui.ClusterDialog;
import org.eclipse.swt.widgets.Shell;

public class StartClusteringAction extends SimpleAction {

	public static final String LABEL = "Cluster Data";
	public static final String ICON = "resources/icons/view/tablebased/clustering.png";

	/**
	 * Constructor.
	 */
	public StartClusteringAction() {
		super(LABEL, ICON);
	}

	@Override
	public void run() {
		super.run();

		ClusterDialog dialog = new ClusterDialog(new Shell());

		dialog.open();
	}
}
