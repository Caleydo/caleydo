/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.dvi.toolbar.ToolBarWidgets;
import org.eclipse.swt.widgets.Composite;

public class RcpGLDVIView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLDVIView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedDVIView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLDataViewIntegrator(glCanvas, parentComposite, serializedView.getViewFrustum());
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
	public String getViewGUIID() {
		return GLDataViewIntegrator.VIEW_TYPE;
	}

	@Override
	public void addToolBarContent() {

		toolBarManager.add(new ToolBarWidgets("Graph Layout"));
		toolBarManager
				.add(new OpenOnlineHelpAction(
						"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/setting-up-visualizations-data-view-integrator",
						true));
	}
}
