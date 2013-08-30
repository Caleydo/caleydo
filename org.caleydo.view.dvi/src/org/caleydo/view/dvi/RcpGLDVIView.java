/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.dvi.toolbar.ToolBarWidgets;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDVIView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDVIView() {
		super(SerializedDVIView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLDataViewIntegrator(glCanvas, serializedView.getViewFrustum());
		initializeView();
		minSizeComposite.setView((AGLView) view);
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDVIView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {

		toolBarManager.add(new ToolBarWidgets("Graph Layout"));
		toolBarManager
				.add(new OpenOnlineHelpAction(
						"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/setting-up-visualizations-data-view-integrator",
						true));
	}
}
