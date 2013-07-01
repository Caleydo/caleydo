/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.stratomex;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.stratomex.toolbar.ConnectionsModeGUI;
import org.eclipse.swt.widgets.Composite;

/**
 * RCP view container for {@link GLStratomex}
 *
 * @author <Alexander Lex
 */
public class RcpGLStratomexView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLStratomexView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedStratomexView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		GLStratomex stratomex = new GLStratomex(glCanvas, parentComposite, serializedView.getViewFrustum());
		view = stratomex;
		initializeView();

		createPartControlGL();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.CaleydoRCPViewPart#getView()
	 */
	@Override
	public GLStratomex getView() {
		return (GLStratomex) super.getView();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedStratomexView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLStratomex.VIEW_TYPE;
	}

	@Override
	public void addToolBarContent() {

		toolBarManager.add(new ConnectionsModeGUI());
		toolBarManager.add(new OpenOnlineHelpAction(
				"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/stratomex", true));

		toolBarManager.update(true);
	}
}
