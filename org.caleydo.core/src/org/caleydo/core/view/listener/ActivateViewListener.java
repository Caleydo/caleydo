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
package org.caleydo.core.view.listener;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.BookmarkEvent;
import org.caleydo.core.event.view.OpenViewEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class ActivateViewListener
	extends AEventListener<IListenerOwner> {

	@Override
	public void handleEvent(final AEvent event) {

		try {
			if (event instanceof BookmarkEvent<?>) {

				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

				String viewType = "org.caleydo.view.bookmark";
				boolean viewExists = false;
				for (IViewPart viewPart : page.getViews()) {
					if (!(viewPart instanceof CaleydoRCPViewPart))
						continue;
					ASerializedView serView = ((CaleydoRCPViewPart) viewPart).getSerializedView();

					if (event.getEventSpace().equals(
						((ASerializedSingleTablePerspectiveBasedView) serView).getDataDomainID())
						&& serView.getViewType().equals(viewType)) {
						viewExists = true;
						break;
					}
				}

				if (!viewExists) {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView("org.caleydo.view.bookmark");
				}

				// TODO only re-trigger event if view is initially opened
				event.setSender(handler);

				// Re-trigger event so that the opened view receives it
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}
			else if (event instanceof OpenViewEvent) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(((OpenViewEvent) event).getViewType());
			}
		}
		catch (PartInitException e) {
			e.printStackTrace();
			Logger.log(new Status(IStatus.INFO, this.toString(), "Unable to open bucket view.", e));
		}
	}
}
