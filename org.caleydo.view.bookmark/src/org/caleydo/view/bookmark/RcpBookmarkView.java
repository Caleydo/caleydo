/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.bookmark;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.bookmark.toolbar.ExportDataAction;
import org.eclipse.swt.widgets.Composite;

public class RcpBookmarkView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpBookmarkView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedBookmarkView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLBookmarkView(glCanvas, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedBookmarkView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLBookmarkView.VIEW_TYPE;
	}

	@Override
	public void addToolBarContent() {
		toolBarManager.add(new ExportDataAction());
	}

}
