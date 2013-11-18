/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.caleydo.view.tourguide.spi.adapter.ITourGuideAdapter;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;


public class RcpGLTourGuideView extends ARcpGLViewPart {

	private ITourGuideAdapter adapter;

	public RcpGLTourGuideView() {
		super(SerializedTourGuideView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		if (this.adapter == null)
			return; // nothing to create -> error
		super.createPartControl(parent);
		view = new GLTourGuideView(glCanvas, adapter);
		initializeView();
		createPartControlGL();

		EPartService service = (EPartService) getSite().getService(EPartService.class);

		final GLTourGuideView m = getView();
		if (m == null)
			return;
		// find visible StratomeX
		outer: for (MPart part : service.getParts()) {
			if (service.isPartVisible(part)) {
				IViewPart view = getSite().getPage().findView(part.getElementId());
				if (adapter.bindTo(view)) {
					break outer;
				}
			}
		}
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		this.adapter = TourGuideAdapters.createFrom(site.getSecondaryId());
		// assume this.adapter != null;
		if (this.adapter != null)
			site.getPage().addPartListener(partListener);
	}


	@Override
	public void dispose() {
		if (this.adapter != null)
			getSite().getPage().removePartListener(partListener);
		super.dispose();
	}

	@Override
	public GLTourGuideView getView() {
		return (GLTourGuideView) super.getView();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedTourGuideView();
		serializedView.setLabelDefault(false);
		serializedView.setViewLabel((adapter == null ? "???" : adapter.getPartName()) + " - LineUp");
		determineDataConfiguration(serializedView, false);
	}

	/**
	 * listener that checks which stratomex is open and tell that the tour guide instance
	 */
	private final IPartListener2 partListener = new IPartListener2() {
		@Override
		public void partVisible(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partOpened(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partInputChanged(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partHidden(IWorkbenchPartReference partRef) {

		}

		@Override
		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			if (partRef == null)
				return;
			IWorkbenchPart part = partRef.getPart(false);

			GLTourGuideView m = getView();
			if (m == null)
				return;
			if (m.getAdapter() != null && m.getAdapter().isRepresenting(part))
				m.getAdapter().bindTo(null);
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			if (partRef == null)
				return;
			IWorkbenchPart part = partRef.getPart(false);

			GLTourGuideView m = getView();

			if (ignorePartChange(part) || m == null)
				return;
			if (part instanceof IViewPart) {
				if (m.getAdapter() != null && m.getAdapter().isRepresenting(part))
					return;

				if (adapter.ignoreActive((IViewPart) part))
					return;
				adapter.bindTo((IViewPart) part);
			}
		}
	};

	private static boolean ignorePartChange(IWorkbenchPart part) {
		if (part instanceof RcpGLTourGuideView)
			return true;
		final String canonicalName = part.getClass().getCanonicalName();
		return canonicalName.startsWith("org.caleydo.view.info")
				|| canonicalName.startsWith("org.caleydo.core.gui.toolbar");
	}


}
