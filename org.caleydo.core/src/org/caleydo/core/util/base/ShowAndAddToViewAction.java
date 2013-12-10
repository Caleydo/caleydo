/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.core.util.base;

import java.util.Arrays;
import java.util.UUID;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
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
public class ShowAndAddToViewAction implements Runnable {
	private static final Logger log = Logger.create(ShowAndAddToViewAction.class);

	private final String view;
	private final TablePerspective[] tablePerspective;
	private String secondaryId;

	public static ShowAndAddToViewAction show(String viewType, TablePerspective... tablePerspective) {
		return new ShowAndAddToViewAction(viewType, tablePerspective);
	}

	public static ShowAndAddToViewAction showMultiple(String viewType, TablePerspective... tablePerspective) {
		return new ShowAndAddToViewAction(viewType, Integer.toString(UUID.randomUUID().hashCode()), tablePerspective);
	}

	public static ShowAndAddToViewAction showMultiple(String viewType, String secondaryId,
			TablePerspective... tablePerspective) {
		return new ShowAndAddToViewAction(viewType, secondaryId, tablePerspective);
	}
	/**
	 * @param viewType
	 * @param tablePerspective
	 */
	public ShowAndAddToViewAction(String viewType, TablePerspective... tablePerspective) {
		this(viewType, null, tablePerspective);
	}
	/**
	 * @param viewType
	 * @param object
	 * @param tablePerspective2
	 */
	public ShowAndAddToViewAction(String viewType, String secondardyId, TablePerspective... tablePerspective) {
		this.view = viewType;
		this.tablePerspective = tablePerspective;
		this.secondaryId = secondardyId;
	}

	@Override
	public void run() {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			IViewPart part;
			if (secondaryId != null) { // multipe ones
				part = activePage.showView(view, Integer.toString(UUID.randomUUID().hashCode(), Character.MAX_RADIX),
						IWorkbenchPage.VIEW_ACTIVATE);
			} else { // single one
				part = activePage.showView(view, null, IWorkbenchPage.VIEW_ACTIVATE);
			}
			if (tablePerspective.length > 0 && part instanceof CaleydoRCPViewPart) {
				CaleydoRCPViewPart rcp = (CaleydoRCPViewPart)part;
				IView v = rcp.getView();
				if (v instanceof ISingleTablePerspectiveBasedView) {
					((ISingleTablePerspectiveBasedView) v).setTablePerspective(tablePerspective[0]);
				} else if (v instanceof IMultiTablePerspectiveBasedView)
					EventPublisher.trigger(new AddTablePerspectivesEvent(Arrays.asList(tablePerspective)).to(v));
			}
		} catch (PartInitException e) {
			log.error("can't show " + view, e);
		}
	}

}
