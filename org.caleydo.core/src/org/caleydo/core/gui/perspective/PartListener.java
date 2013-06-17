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

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.opengl.canvas.IGLView;
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

		IView view = viewPart.getView();
		if (view instanceof IGLView) {
			((IGLView) view).setVisible(true);
		}
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

		IView view = glViewPart.getView();
		if (view instanceof IGLView) {
			((IGLView) view).setVisible(false);
		}
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
		updateSupportViews(viewPart);

		// Make sure that keyboard listener gets the events
		if (viewPart.getSWTComposite() != null) {
			viewPart.getSWTComposite().forceFocus();
		}

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
}
// =======
// /*******************************************************************************
// * Caleydo - visualization for molecular biology - http://caleydo.org
// *
// * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
// * Lex, Christian Partl, Johannes Kepler University Linz </p>
// *
// * This program is free software: you can redistribute it and/or modify it under
// * the terms of the GNU General Public License as published by the Free Software
// * Foundation, either version 3 of the License, or (at your option) any later
// * version.
// *
// * This program is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
// * details.
// *
// * You should have received a copy of the GNU General Public License along with
// * this program. If not, see <http://www.gnu.org/licenses/>
// *******************************************************************************/
// package org.caleydo.core.gui.perspective;
//
// import org.caleydo.core.data.datadomain.IDataDomain;
// import org.caleydo.core.view.ARcpGLViewPart;
// import org.caleydo.core.view.CaleydoRCPViewPart;
// import org.caleydo.core.view.IDataDomainBasedView;
// import org.caleydo.core.view.IView;
// import org.caleydo.core.view.opengl.canvas.IGLView;
// import org.eclipse.ui.IPartListener2;
// import org.eclipse.ui.IViewPart;
// import org.eclipse.ui.IWorkbenchPart;
// import org.eclipse.ui.IWorkbenchPartReference;
// import org.eclipse.ui.PlatformUI;
//
// /**
// * Listener for events that are related to view changes (detach, visible, hide,
// * activate, etc.)
// *
// * @author Marc Streit
// */
// public class PartListener implements IPartListener2 {
// @Override
// public void partOpened(IWorkbenchPartReference partRef) {
// // IWorkbenchPart activePart = partRef.getPart(false);
// //
// // if (!(activePart instanceof ARcpGLViewPart))
// // return;
// //
// // ARcpGLViewPart glView = (ARcpGLViewPart) activePart;
// //
// // GeneralManager.get().getViewManager().registerGLCanvasToAnimator(glView.getGLCanvas());
// }
//
// @Override
// public void partClosed(IWorkbenchPartReference partRef) {
//
// }
//
// @Override
// public void partVisible(IWorkbenchPartReference partRef) {
//
// IWorkbenchPart activePart = partRef.getPart(false);
//
// if (!(activePart instanceof CaleydoRCPViewPart)) {
// return;
// }
// CaleydoRCPViewPart viewPart = (CaleydoRCPViewPart) activePart;
//
// IView view = viewPart.getView();
// if (view instanceof IGLView) {
// ((IGLView) view).setVisible(true);
// }
// }
//
// @Override
// public void partDeactivated(IWorkbenchPartReference partRef) {
// }
//
// @Override
// public void partHidden(IWorkbenchPartReference partRef) {
//
// IWorkbenchPart activePart = partRef.getPart(false);
//
// if (!(activePart instanceof CaleydoRCPViewPart)) {
// return;
// }
//
// if (!(activePart instanceof ARcpGLViewPart)) {
// return;
// }
//
// ARcpGLViewPart glViewPart = (ARcpGLViewPart) activePart;
//
// // GeneralManager.get().getViewGLCanvasManager()
// // .unregisterGLCanvasFromAnimator(glViewPart.getGLCanvas());
//
// IView view = glViewPart.getView();
// if (view instanceof IGLView) {
// ((IGLView) view).setVisible(false);
// }
// }
//
// @Override
// public void partInputChanged(IWorkbenchPartReference partRef) {
// }
//
// @Override
// public void partActivated(IWorkbenchPartReference partRef) {
//
// IWorkbenchPart activePart = partRef.getPart(false);
// if (!(activePart instanceof CaleydoRCPViewPart)) {
// return;
// }
//
// CaleydoRCPViewPart viewPart = (CaleydoRCPViewPart) activePart;
// updateSupportViews(viewPart);
//
// // Make sure that keyboard listener gets the events
// if (viewPart.getSWTComposite() != null) {
// viewPart.getSWTComposite().forceFocus();
// }
//
// }
//
// @SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
// private void updateSupportViews(CaleydoRCPViewPart viewPart) {
// if (viewPart.getView() instanceof IDataDomainBasedView) {
// IDataDomain dataDomain = ((IDataDomainBasedView<?>) viewPart
// .getView()).getDataDomain();
//
// for (IViewPart rcpViewPart : PlatformUI.getWorkbench()
// .getActiveWorkbenchWindow().getActivePage().getViews()) {
//
// if (!(rcpViewPart instanceof CaleydoRCPViewPart))
// continue;
//
// CaleydoRCPViewPart caleydoRCPViewPart = (CaleydoRCPViewPart) rcpViewPart;
//
// if (caleydoRCPViewPart.isSupportView()) {
// if (caleydoRCPViewPart instanceof IDataDomainBasedView) {
// ((IDataDomainBasedView) caleydoRCPViewPart)
// .setDataDomain(dataDomain);
//
// } else if (caleydoRCPViewPart.getView() instanceof IDataDomainBasedView) {
// ((IDataDomainBasedView) (caleydoRCPViewPart.getView()))
// .setDataDomain(dataDomain);
// }
// }
// }
// }
// }
//
// @Override
// public void partBroughtToTop(IWorkbenchPartReference partRef) {
// }
// }
// >>>>>>> 67c21486e36d765ef68f2a00a48d7af00038b002
