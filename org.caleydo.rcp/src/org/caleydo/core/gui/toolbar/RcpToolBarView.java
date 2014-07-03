/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.IMenuService;
import org.eclipse.ui.part.ViewPart;

/**
 * Toolbar view containing all toolbars contributed dynamically by views. This view is implemented as IMediatorReceiver
 * because it highlights the active view toolbar when an event is coming in.
 *
 * @author Marc Streit
 */
public class RcpToolBarView extends ViewPart implements ISizeProvider {
	private static final int MAX_ITEMS_PER_LINE = 5;

	public static final String ID = "org.caleydo.core.gui.toolbar.RcpToolBarView";

	public static final int TOOLBAR_WIDTH = 213;
	public static final int TOOLBAR_HEIGHT = 110;

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		parentComposite.setLayout(new GridLayout(1, false));

		addGeneralToolBar(parentComposite);
	}

	@Override
	public void setFocus() {

	}

	private void addGeneralToolBar(Composite parent) {

		Group group = new Group(parent, SWT.NULL);
		group.setLayout(new FillLayout(SWT.VERTICAL));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// group.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));


		ToolBarManager toolBarManager = new ToolBarManager(SWT.WRAP);
		IMenuService menuService = (IMenuService) PlatformUI.getWorkbench().getService(IMenuService.class);
		menuService.populateContributionManager(toolBarManager, "toolbar:org.caleydo.core.gui.toolbar");

		if (toolBarManager.getSize() > MAX_ITEMS_PER_LINE) { // SystemUtils.IS_OS_LINUX &&
			// Needed to simulate toolbar wrapping which is not implemented for
			// linux See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
			final IContributionItem[] items = toolBarManager.getItems();
			for (int i = 0; i < items.length;) {
				ToolBarManager m = new ToolBarManager();
				for (; i < items.length && m.getSize() < MAX_ITEMS_PER_LINE; ++i)
					if (items[i].isVisible())
						m.add(items[i]);
				m.createControl(group);
			}
		} else {
			toolBarManager.createControl(group);
		}
	}



	@Override
	public int computePreferredSize(boolean width, int availableParallel, int availablePerpendicular,
			int preferredResult) {
		// Set minimum size of the view
		if (width == true)
			return RcpToolBarView.TOOLBAR_WIDTH;

		return RcpToolBarView.TOOLBAR_HEIGHT;
	}

	@Override
	public int getSizeFlags(boolean width) {
		return SWT.MIN;
	}
}
