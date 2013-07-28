/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.pathway;

import java.util.Set;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.gui.toolbar.action.OpenOnlineHelpAction;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.datadomain.pathway.IPathwayHandler;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.listener.AddPathwayListener;
import org.caleydo.datadomain.pathway.listener.LoadPathwayEvent;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.datadomain.pathway.toolbar.ClearPathAction;
import org.caleydo.datadomain.pathway.toolbar.SelectPathAction;
import org.caleydo.view.pathway.toolbar.DatasetSelectionBox;
import org.caleydo.view.pathway.toolbar.PathwaySearchBox;
import org.caleydo.view.pathway.toolbar.SampleSelectionMode;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class RcpGLPathwayView extends ARcpGLViewPart implements IPathwayHandler {

	private AddPathwayListener addPathwayListener;

	private final EventListenerManager listeners = EventListenerManagers.createSWTDirect();

	/**
	 * Constructor.
	 */
	public RcpGLPathwayView() {
		super(SerializedPathwayView.class);
		listeners.register(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new GLPathway(glCanvas, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedPathwayView();
		// determineDataConfiguration(serializedView);
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
		addPathwayListener = new AddPathwayListener();
		addPathwayListener.setHandler(this);
		EventPublisher.INSTANCE.addListener(LoadPathwayEvent.class, addPathwayListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (addPathwayListener != null) {
			EventPublisher.INSTANCE.removeListener(addPathwayListener);
			addPathwayListener = null;
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		unregisterEventListeners();
	}

	@Override
	public void addPathwayView(int pathwayID, String dataDomainID) {
		if (view == null)
			return;
		((GLPathway) view).setPathway(pathwayID);

		final PathwayGraph pathway = PathwayManager.get().getItem(pathwayID);
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				int w = glCanvas.toRawPixel(pathway.getWidth());
				int h = glCanvas.toRawPixel(pathway.getHeight());
				minSizeComposite.setMinSize(w, h);
			}
		});

	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {

		SelectPathAction selectPathAction = new SelectPathAction(false, GLPathway.DEFAULT_PATHWAY_PATH_EVENT_SPACE);

		if (view instanceof GLPathway)
			((GLPathway) view).setSelectPathAction(selectPathAction);
		toolBarManager.add(selectPathAction);
		toolBarManager.add(new ClearPathAction(GLPathway.DEFAULT_PATHWAY_PATH_EVENT_SPACE));

		SampleSelectionMode sampleSelectionMode = new SampleSelectionMode(
				((SerializedPathwayView) serializedView).getMappingMode());
		toolBarManager.add(sampleSelectionMode);

		DatasetSelectionBox dataSelectionBox = new DatasetSelectionBox(DataDomainManager.get().getDataDomainByID(
				((SerializedPathwayView) serializedView).getDataDomainID()), (GLPathway) view);
		toolBarManager.add(dataSelectionBox);

		PathwaySearchBox pathwaySearchBox = new PathwaySearchBox((GLPathway) view);
		toolBarManager.add(pathwaySearchBox);

		toolBarManager.add(new OpenOnlineHelpAction(
				"http://www.icg.tugraz.at/project/caleydo/help/caleydo-2.0/pathways", false));

	}

	@Override
	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPathwayTexturesEnabled(boolean pathwayTexturesEnabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public void loadDependentPathways(Set<PathwayGraph> pathwayGraphs) {
		// TODO Auto-generated method stub

	}
}
