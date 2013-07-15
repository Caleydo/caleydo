/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.parcoords;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.parcoords.toolbar.AngularBrushingAction;
import org.caleydo.view.parcoords.toolbar.ResetAxisSpacingAction;
import org.caleydo.view.parcoords.toolbar.SaveSelectionsAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLParCoordsView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLParCoordsView() {
		super();

		try {
			viewContext = JAXBContext
					.newInstance(SerializedParallelCoordinatesView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		view = new GLParallelCoordinates(glCanvas, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedParallelCoordinatesView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLParallelCoordinates.VIEW_TYPE;
	}

	@Override
	public void addToolBarContent() {

		toolBarManager.add(new AngularBrushingAction());
		toolBarManager.add(new ResetAxisSpacingAction());
		toolBarManager.add(new SaveSelectionsAction());

	}
}
