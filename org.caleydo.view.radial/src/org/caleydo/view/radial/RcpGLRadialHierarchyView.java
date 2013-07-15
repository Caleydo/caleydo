/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.radial;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.radial.actions.ChangeColorModeAction;
import org.caleydo.view.radial.actions.GoBackInHistoryAction;
import org.caleydo.view.radial.actions.GoForthInHistoryAction;
import org.caleydo.view.radial.toolbar.DepthSlider;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;

public class RcpGLRadialHierarchyView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLRadialHierarchyView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedRadialHierarchyView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// minSizeComposite.setView(view);

		view = new GLRadialHierarchy(glCanvas, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	public static void createToolBarItems(int viewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedRadialHierarchyView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLRadialHierarchy.VIEW_TYPE;
	}

	@Override
	public void addToolBarContent() {

		toolBarManager.add(new GoBackInHistoryAction());
		toolBarManager.add(new GoForthInHistoryAction());
		toolBarManager.add(new ChangeColorModeAction());
		toolBarManager.add(new DepthSlider(((SerializedRadialHierarchyView) serializedView)
				.getMaxDisplayedHierarchyDepth()));
	}
}
