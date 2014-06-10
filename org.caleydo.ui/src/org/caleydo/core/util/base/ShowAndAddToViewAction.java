/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.base;

import java.util.Arrays;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.core.view.ISingleTablePerspectiveBasedView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.listener.AddTablePerspectivesEvent;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Samuel Gratzl
 *
 */
class ShowAndAddToViewAction implements Runnable {
	private static final Logger log = Logger.create(ShowAndAddToViewAction.class);

	private final String view;
	private final TablePerspective[] tablePerspective;
	private final ASerializedView init;
	private String secondaryId;

	public ShowAndAddToViewAction(String viewType, String secondaryId, TablePerspective... tablePerspective) {
		this.view = viewType;
		this.tablePerspective = tablePerspective;
		this.secondaryId = secondaryId;
		this.init = null;
	}

	public ShowAndAddToViewAction(String viewType, String secondaryId, ASerializedView init) {
		this.view = viewType;
		this.tablePerspective = null;
		this.secondaryId = secondaryId;
		this.init = null;
	}

	@Override
	public void run() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			int action = init != null ? IWorkbenchPage.VIEW_CREATE : IWorkbenchPage.VIEW_ACTIVATE;

			IViewPart part = activePage.showView(view, secondaryId, action);

			if (tablePerspective.length > 0 && part instanceof CaleydoRCPViewPart) {
				CaleydoRCPViewPart rcp = (CaleydoRCPViewPart)part;
				IView v = rcp.getView();
				if (v instanceof ISingleTablePerspectiveBasedView) {
					((ISingleTablePerspectiveBasedView) v).setTablePerspective(tablePerspective[0]);
				} else if (v instanceof IMultiTablePerspectiveBasedView)
					EventPublisher.trigger(new AddTablePerspectivesEvent(Arrays.asList(tablePerspective)).to(v));
			} else if (init != null && part instanceof CaleydoRCPViewPart) {
				// init before create
				((CaleydoRCPViewPart) part).setExternalSerializedView(init);
				activePage.showView(view, part.getViewSite().getSecondaryId(), IWorkbenchPage.VIEW_ACTIVATE);
			}
		} catch (PartInitException e) {
			log.error("can't show " + view, e);
		}
	}

}
