/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapter;
import org.caleydo.view.tourguide.spi.adapter.IViewAdapterFactory;
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

	private EDataDomainQueryMode mode;

	public RcpGLTourGuideView() {
		super(SerializedTourGuideView.class);
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		view = new GLTourGuideView(glCanvas, mode);
		initializeView();
		createPartControlGL();

		EPartService service = (EPartService) getSite().getService(EPartService.class);

		final GLTourGuideView m = getView();
		if (m == null)
			return;
		// find visible StratomeX
		for (MPart part : service.getParts()) {
			if (service.isPartVisible(part)) {
				IViewPart view = getSite().getPage().findView(part.getElementId());
				for (IViewAdapterFactory factory : ViewAdapters.get()) {
					IViewAdapter adapter = factory.createFor(view, mode, m);
					if (adapter != null)
						m.switchTo(adapter);
				}
			}
		}
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		this.mode = EDataDomainQueryMode.valueOfSafe(site.getSecondaryId());
		site.getPage().addPartListener(stratomexListener);
	}


	@Override
	public void dispose() {
		getSite().getPage().removePartListener(stratomexListener);
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
		serializedView.setViewLabel(this.mode.getLabel() + " LineUp");
		determineDataConfiguration(serializedView, false);
	}

	/**
	 * listener that checks which stratomex is open and tell that the tour guide instance
	 */
	private final IPartListener2 stratomexListener = new IPartListener2() {
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
			if (m.getAdapter().isRepresenting(part))
				m.switchTo(null);
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

			if (ignorePartChange(part))
				return;
			if (part instanceof RcpGLTourGuideView && m != null) {
				if (part == RcpGLTourGuideView.this) { // I was activated
					m.attachToView();
				} else { // another tour guide than me was activated
					m.detachFromView();
				}
			} else if (part instanceof IViewPart) {
				for (IViewAdapterFactory factory : ViewAdapters.get()) {
					IViewAdapter adapter = factory.createFor((IViewPart) part, mode, m);
					if (adapter != null)
						m.switchTo(adapter);
				}
			}
		}
	};

	private static boolean ignorePartChange(IWorkbenchPart part) {
		final String canonicalName = part.getClass().getCanonicalName();
		return canonicalName.startsWith("org.caleydo.view.info")
				|| canonicalName.startsWith("org.caleydo.core.gui.toolbar");
	}


}
