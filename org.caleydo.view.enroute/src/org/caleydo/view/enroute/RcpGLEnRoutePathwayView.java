/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute;

import org.caleydo.core.gui.OpenOnlineHelpAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.toolbar.actions.FitToViewWidthAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO: DOCUMENT ME!
 *
 * @author Christian
 */
public class RcpGLEnRoutePathwayView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLEnRoutePathwayView() {
		super(SerializedEnRoutePathwayView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLEnRoutePathway(glCanvas, serializedView.getViewFrustum());
		initializeView();
		minSizeComposite.setView((AGLView) view);
		createPartControlGL();
	}

	@Override
	public GLEnRoutePathway getView() {
		return (GLEnRoutePathway) super.getView();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedEnRoutePathwayView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {

		toolBarManager
				.add(new FitToViewWidthAction(((SerializedEnRoutePathwayView) serializedView).isFitToViewWidth()));
		toolBarManager.add(new OpenOnlineHelpAction(GeneralManager.HELP_URL
				+ "views/pathway/pathway.md#enRoute_-_Experimental_Data_Analysis", false));
	}
}
