/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.tourguide.internal;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.RcpGLStratomexView;
import org.caleydo.view.tourguide.internal.view.VendingMachine;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;


public class RcpGLTourGuideView extends ARcpGLViewPart {

	/**
	 * Constructor.
	 */
	public RcpGLTourGuideView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedTourGuideView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		site.getPage().addPartListener(stratomexListener);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.caleydo.core.view.ARcpGLViewPart#dispose()
	 */
	@Override
	public void dispose() {
		getSite().getPage().removePartListener(stratomexListener);
		super.dispose();
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		view = new VendingMachine(glCanvas, parentComposite, serializedView.getViewFrustum());
		initializeView();
		createPartControlGL();
		stratomexListener.partActivated(getSite().getPage().getActivePartReference());
	}

	/* (non-Javadoc)
	 * @see org.caleydo.core.view.CaleydoRCPViewPart#getView()
	 */
	@Override
	public VendingMachine getView() {
		return (VendingMachine) super.getView();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedTourGuideView();
		determineDataConfiguration(serializedView, false);
	}

	@Override
	public String getViewGUIID() {
		return VendingMachine.VIEW_TYPE;
	}

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
			if (ignorePartChange(part))
				return;
			if (part instanceof RcpGLStratomexView) {
				RcpGLStratomexView strat = (RcpGLStratomexView) part;
				stratomex = strat.getView();
			}
			VendingMachine m = getView();
			if (m != null)
				m.switchToStratomex(stratomex);
		}
	};

	private static boolean ignorePartChange(IWorkbenchPart part) {
		final String canonicalName = part.getClass().getCanonicalName();
		return part instanceof RcpGLTourGuideView || canonicalName.startsWith("org.caleydo.view.info")
				|| canonicalName.startsWith("org.caleydo.core.gui.toolbar");
	}

}