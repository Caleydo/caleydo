/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filterpipeline;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.filterpipeline.toolbar.SelectFilterTypeWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

/**
 * TODO: DOCUMENT ME!
 *
 * @author <INSERT_YOUR_NAME>
 */
public class RcpGLFilterPipelineView extends ARcpGLViewPart implements IListenerOwner {
	public static String VIEW_TYPE = "org.caleydo.view.filterpipeline";

	/**
	 * Constructor.
	 */
	public RcpGLFilterPipelineView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedFilterPipelineView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}

		eventPublisher = GeneralManager.get().getEventPublisher();
		isSupportView = true;
		registerEventListeners();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLFilterPipeline(glCanvas, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedFilterPipelineView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public String getViewGUIID() {
		return GLFilterPipeline.VIEW_TYPE;
	}

	@Override
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void registerEventListeners() {

	}

	@Override
	public void unregisterEventListeners() {

	}

	@Override
	public void addToolBarContent() {
		toolBarManager.add(new SelectFilterTypeWidget((GLFilterPipeline) view));
	}
}
