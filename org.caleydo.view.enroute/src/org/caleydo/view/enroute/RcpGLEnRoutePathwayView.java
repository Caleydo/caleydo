/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.toolbar.actions.FitToViewWidthAction;
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
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedEnRoutePathwayView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLEnRoutePathway(glCanvas, parentComposite, serializedView.getViewFrustum());
		initializeView();
		minSizeComposite.setView((AGLView) view);
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedEnRoutePathwayView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLEnRoutePathway.VIEW_TYPE;
	}

	@Override
	public void addToolBarContent() {

		toolBarManager
				.add(new FitToViewWidthAction(((SerializedEnRoutePathwayView) serializedView).isFitToViewWidth()));
		toolBarManager.add(new OpenOnlineHelpAction(
				"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/enroute", false));
	}
}
