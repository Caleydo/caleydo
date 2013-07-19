/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.RcpGLStratomexView;
import org.caleydo.view.tourguide.api.query.EDataDomainQueryMode;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
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

		// find visible StratomeX
		for (MPart part : service.getParts()) {
			if (service.isPartVisible(part)) {
				GLTourGuideView m = getView();
				GLStratomex stratomex = null;
				IViewPart view = getSite().getPage().findView(part.getElementId());
				if (view instanceof RcpGLStratomexView) {
					RcpGLStratomexView strat = (RcpGLStratomexView) view;
					stratomex = strat.getView();
					if (m != null && stratomex != null)
						m.switchToStratomex(stratomex);
				}
			}
		}
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		this.mode = EDataDomainQueryMode.valueOfSafe(site.getSecondaryId());
		this.setPartName(this.mode.getLabel() + " LineUp");
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
		determineDataConfiguration(serializedView, false);
	}

	@Override
	public String getViewGUIID() {
		return GLTourGuideView.VIEW_TYPE;
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

			if (part instanceof RcpGLStratomexView && m != null) {
				m.switchToStratomex(null);
			}
		}

		@Override
		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		@Override
		public void partActivated(IWorkbenchPartReference partRef) {
			GLStratomex stratomex = null;
			if (partRef == null)
				return;
			IWorkbenchPart part = partRef.getPart(false);

			GLTourGuideView m = getView();

			if (ignorePartChange(part))
				return;
			if (part instanceof RcpGLTourGuideView && m != null) {
				if (part == RcpGLTourGuideView.this) { // I was activated
					m.attachToStratomex();
				} else { // another tour guide than me was activated
					m.detachFromStratomex();
				}
			} else {
				if (part instanceof RcpGLStratomexView) {
					RcpGLStratomexView strat = (RcpGLStratomexView) part;
					stratomex = strat.getView();
				}
				if (m != null)
					m.switchToStratomex(stratomex);
			}
		}
	};

	private static boolean ignorePartChange(IWorkbenchPart part) {
		final String canonicalName = part.getClass().getCanonicalName();
		return canonicalName.startsWith("org.caleydo.view.info")
				|| canonicalName.startsWith("org.caleydo.core.gui.toolbar");
	}


}
