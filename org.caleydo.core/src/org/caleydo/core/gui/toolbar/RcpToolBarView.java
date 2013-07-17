/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar;

import java.util.ArrayList;

import org.caleydo.core.gui.toolbar.action.ClearSelectionsAction;
import org.caleydo.core.gui.toolbar.action.OpenProjectAction;
import org.caleydo.core.gui.toolbar.action.SaveProjectAction;
import org.caleydo.core.gui.toolbar.action.StartClusteringAction;
import org.caleydo.core.gui.toolbar.action.TakeSnapshotAction;
import org.caleydo.core.io.gui.ExportDataAction;
import org.caleydo.core.io.gui.ImportDataAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.ISizeProvider;
import org.eclipse.ui.part.ViewPart;

/**
 * Toolbar view containing all toolbars contributed dynamically by views. This view is implemented as IMediatorReceiver
 * because it highlights the active view toolbar when an event is coming in.
 *
 * @author Marc Streit
 */
public class RcpToolBarView extends ViewPart implements ISizeProvider {
	public static final String ID = "org.caleydo.core.gui.toolbar.RcpToolBarView";

	public static final int TOOLBAR_WIDTH = 213;
	public static final int TOOLBAR_HEIGHT = 110;

	private Composite parentComposite;

	private ArrayList<Group> viewSpecificGroups;

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		parentComposite.setLayout(new GridLayout(1, false));
		this.parentComposite = parentComposite;

		viewSpecificGroups = new ArrayList<Group>();

		addGeneralToolBar();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	private void addGeneralToolBar() {

		Group group = new Group(parentComposite, SWT.NULL);
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// group.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));

		// Needed to simulate toolbar wrapping which is not implemented for
		// linux
		// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025
		ArrayList<ToolBar> alToolBar = new ArrayList<ToolBar>();
		ArrayList<IToolBarManager> alToolBarManager = new ArrayList<IToolBarManager>();

		final ToolBar toolBar = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		alToolBar.add(toolBar);
		alToolBarManager.add(toolBarManager);

		final ToolBar toolBar2 = new ToolBar(group, SWT.WRAP | SWT.FLAT);
		ToolBarManager toolBarManager2 = new ToolBarManager(toolBar2);
		alToolBar.add(toolBar2);
		alToolBarManager.add(toolBarManager2);

		toolBarManager.add(new OpenProjectAction());
		toolBarManager.add(new ImportDataAction());
		toolBarManager.add(new ExportDataAction());

		if (!System.getProperty("os.name").startsWith("Windows"))
			toolBarManager.add(new TakeSnapshotAction());

		toolBarManager2.add(new SaveProjectAction());
		toolBarManager2.add(new ClearSelectionsAction());
		toolBarManager2.add(new StartClusteringAction());

		toolBarManager.update(true);

		if (toolBarManager2.isEmpty())
			toolBarManager2.dispose();
		else
			toolBarManager2.update(true);
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

	public Composite getParentComposite() {
		return parentComposite;
	}

	public void setParentComposite(Composite parentComposite) {
		this.parentComposite = parentComposite;
	}

	public ArrayList<Group> getViewSpecificGroups() {
		return viewSpecificGroups;
	}

	public void setViewSpecificGroups(ArrayList<Group> viewSpecificGroups) {
		this.viewSpecificGroups = viewSpecificGroups;
	}

}
