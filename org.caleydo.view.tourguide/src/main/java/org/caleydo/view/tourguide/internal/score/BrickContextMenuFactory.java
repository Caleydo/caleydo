/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.score;

import java.util.Collections;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.gui.util.DisplayUtils;
import org.caleydo.core.util.execution.SafeCallable;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.view.stratomex.brick.IContextMenuBrickFactory;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.tourguide.internal.RcpGLTourGuideView;
import org.caleydo.view.tourguide.internal.view.GLTourGuideView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Samuel Gratzl
 *
 */
public class BrickContextMenuFactory implements IContextMenuBrickFactory {

	@Override
	public Iterable<AContextMenuItem> createGroupEntries(BrickColumn brick, TablePerspective group) {
		GLTourGuideView firstVisible = getVisibleTourGuide();
		if (firstVisible == null) // show context menu only if the tour guide view is visible
			return Collections.emptyList();
		return ScoreFactories.createGroupEntries(brick.getTablePerspective(), group.getRecordGroup(), firstVisible);
	}


	@Override
	public Iterable<AContextMenuItem> createStratification(BrickColumn brick) {
		GLTourGuideView firstVisible = getVisibleTourGuide();
		if (firstVisible == null) // show context menu only if the tour guide view is visible
			return Collections.emptyList();
		return ScoreFactories.createStratEntries(brick.getTablePerspective(), firstVisible);
	}

	private static GLTourGuideView getVisibleTourGuide() {
		final IWorkbench wb = PlatformUI.getWorkbench();
		return DisplayUtils.syncExec(wb.getDisplay(), new SafeCallable<GLTourGuideView>() {
			@Override
			public GLTourGuideView call() {
				IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
				IWorkbenchPage page = win.getActivePage();
				for (IViewReference r : page.getViewReferences()) {
					if (!r.getId().startsWith(GLTourGuideView.VIEW_TYPE))
						continue;
					IWorkbenchPart p = r.getPart(false);
					if (p == null || !page.isPartVisible(p))
						continue;
					IViewPart v = r.getView(false);
					if (!(v instanceof RcpGLTourGuideView))
						continue;
					RcpGLTourGuideView rv = (RcpGLTourGuideView)v;
					return rv.getView();
				}
				return null;
			}
		});
	}

}
