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
package org.caleydo.core.gui.perspective;

import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.gui.toolbar.AToolBarContent;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.ToolBarContainer;
import org.caleydo.core.gui.toolbar.ToolBarContentFactory;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.IView;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

/**
 * Listener for events that are related to view changes (detach, visible, hide,
 * activate, etc.)
 * 
 * @author Marc Streit
 */
public class PartListener implements IPartListener2 {

	boolean redoActivation = true;

	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
		// IWorkbenchPart activePart = partRef.getPart(false);
		//
		// if (!(activePart instanceof ARcpGLViewPart))
		// return;
		//
		// ARcpGLViewPart glView = (ARcpGLViewPart) activePart;
		//
		// GeneralManager.get().getViewManager().registerGLCanvasToAnimator(glView.getGLCanvas());
	}

	@Override
	public void partClosed(IWorkbenchPartReference partRef) {

	}

	@Override
	public void partVisible(IWorkbenchPartReference partRef) {

		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof CaleydoRCPViewPart)) {
			return;
		}
		CaleydoRCPViewPart viewPart = (CaleydoRCPViewPart) activePart;

		if (viewPart instanceof ARcpGLViewPart) {
			ARcpGLViewPart glViewPart = (ARcpGLViewPart) activePart;

			glViewPart.getGLView().setVisible(true);
		}

		drawInlineToolBar(viewPart);
	}

	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partHidden(IWorkbenchPartReference partRef) {

		IWorkbenchPart activePart = partRef.getPart(false);

		if (!(activePart instanceof CaleydoRCPViewPart)) {
			return;
		}

		if (!(activePart instanceof ARcpGLViewPart)) {
			return;
		}

		ARcpGLViewPart glViewPart = (ARcpGLViewPart) activePart;

		// GeneralManager.get().getViewGLCanvasManager()
		// .unregisterGLCanvasFromAnimator(glViewPart.getGLCanvas());

		glViewPart.getGLView().setVisible(false);
	}

	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
	}

	@Override
	public void partActivated(IWorkbenchPartReference partRef) {

		IWorkbenchPart activePart = partRef.getPart(false);
		if (!(activePart instanceof CaleydoRCPViewPart)) {
			return;
		}

		CaleydoRCPViewPart viewPart = (CaleydoRCPViewPart) activePart;

		drawInlineToolBar(viewPart);
		updateSupportViews(viewPart);

		// Make sure that keyboard listener gets the events
		if (viewPart.getSWTComposite() != null) {
			viewPart.getSWTComposite().forceFocus();
		}

	}

	private void redoActivation(IWorkbenchPartReference partRef) {
		partDeactivated(partRef);
		redoActivation = false;
		partActivated(partRef);
		partActivated(partRef);
		partActivated(partRef);
		partActivated(partRef);
		partActivated(partRef);
		redoActivation = true;
	}

	@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
	private void updateSupportViews(CaleydoRCPViewPart viewPart) {
		if (viewPart.getView() instanceof IDataDomainBasedView) {
			IDataDomain dataDomain = ((IDataDomainBasedView<?>) viewPart
					.getView()).getDataDomain();

			for (IViewPart rcpViewPart : PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage().getViews()) {

				if (!(rcpViewPart instanceof CaleydoRCPViewPart))
					continue;

				CaleydoRCPViewPart caleydoRCPViewPart = (CaleydoRCPViewPart) rcpViewPart;

				if (caleydoRCPViewPart.isSupportView()) {
					if (caleydoRCPViewPart instanceof IDataDomainBasedView) {
						((IDataDomainBasedView) caleydoRCPViewPart)
								.setDataDomain(dataDomain);

					} else if (caleydoRCPViewPart.getView() instanceof IDataDomainBasedView) {
						((IDataDomainBasedView) (caleydoRCPViewPart.getView()))
								.setDataDomain(dataDomain);
					}
				}
			}
		}
	}

	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	/**
	 * Draws the toolbar items within the views default toolbar (inline)
	 * 
	 * @param viewPartcreatePartControl
	 *            view to add the toolbar items
	 */
	private void drawInlineToolBar(CaleydoRCPViewPart viewPart) {
		List<IView> views = getAllViews(viewPart);

		ToolBarContentFactory contentFactory = ToolBarContentFactory.get();
		List<AToolBarContent> toolBarContents = contentFactory
				.getToolBarContent(views);

		final IToolBarManager toolBarManager = viewPart.getViewSite()
				.getActionBars().getToolBarManager();

		toolBarManager.removeAll();
		for (AToolBarContent toolBarContent : toolBarContents) {
			for (ToolBarContainer container : toolBarContent.getInlineToolBar()) {
				for (IToolBarItem item : container.getToolBarItems()) {
					if (item instanceof IAction) {
						toolBarManager.add((IAction) item);
					} else if (item instanceof ControlContribution) {
						toolBarManager.add((ControlContribution) item);
					}
				}
				toolBarManager.add(new Separator());
			}
		}
		toolBarManager.update(true);
	}

	/**
	 * Gets the views and all sub-views (if there are any)
	 * 
	 * @param viewPart
	 * @return
	 */
	private List<IView> getAllViews(CaleydoRCPViewPart viewPart) {
		return viewPart.getAllViews();
	}
}
