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
package org.caleydo.core.gui.toolbar;

import java.util.ArrayList;

import org.caleydo.core.gui.toolbar.action.ClearSelectionsAction;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
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

		toolBarManager.add(new SaveProjectAction());
		toolBarManager.add(new ImportDataAction());
		toolBarManager.add(new ExportDataAction());
		if (!System.getProperty("os.name").startsWith("Windows"))
			toolBarManager.add(new TakeSnapshotAction());

		// IToolBarItem startClustering = new StartClusteringDialogAction(targetViewID);
		// actionList.add(startClustering);

		// if (DataDomainManager.getInstance().getDataDomain("org.caleydo.datadomain.genetic") != null) {
		// toolBarManager2.add(new OpenSearchViewAction());
		// }

		toolBarManager2.add(new ClearSelectionsAction());
		toolBarManager2.add(new StartClusteringAction());
		// FIXME: removed because we need new concept for restoring data
		// toolBarManager2.add(new RestoreOriginalDataAction());

		// toolBarManager2.add(new SwitchDataRepresentationAction());

		toolBarManager.update(true);

		if (toolBarManager2.isEmpty())
			toolBarManager2.dispose();
		else
			toolBarManager2.update(true);

		Label label = new Label(group, SWT.CENTER);
		label.setText("General");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
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
