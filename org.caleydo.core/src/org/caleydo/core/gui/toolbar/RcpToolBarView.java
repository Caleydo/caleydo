/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.gui.toolbar;

import org.caleydo.core.internal.MyPreferences;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Widget;
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
	public static final String ID = "org.caleydo.core.gui.toolbar.RcpToolBarView";

	public static final int TOOLBAR_WIDTH = 213;
	public static final int TOOLBAR_HEIGHT = 110;

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		parentComposite.setLayout(new GridLayout(1, false));

		addGeneralToolBar(parentComposite);
		// addZoomController(parentComposite);
	}

	@Override
	public void setFocus() {

	}

	private void addGeneralToolBar(Composite parent) {

		Group group = new Group(parent, SWT.NULL);
		group.setLayout(new FillLayout(SWT.VERTICAL));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// group.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));

		// Needed to simulate toolbar wrapping which is not implemented for
		// linux
		// See bug: https://bugs.eclipse.org/bugs/show_bug.cgi?id=46025

		final ToolBar toolBar = new ToolBar(group, SWT.WRAP);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);
		final ToolBar toolBarLine2 = new ToolBar(group, SWT.WRAP);
		ToolBarManager toolBarManager2 = new ToolBarManager(toolBarLine2);

		IMenuService menuService = (IMenuService) PlatformUI.getWorkbench().getService(IMenuService.class);
		menuService.populateContributionManager(toolBarManager, "toolbar:org.caleydo.core.gui.toolbar1");
		menuService.populateContributionManager(toolBarManager2, "toolbar:org.caleydo.core.gui.toolbar2");

		toolBarManager.update(true);
		toolBarManager2.update(true);
	}

	/**
	 * @param parentComposite
	 */
	private void addZoomController(Composite parent) {
		Group group = new Group(parent, SWT.NULL);
		group.setText("Zoom");
		group.setLayout(new GridLayout(3, false));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Spinner spinner = new Spinner(group, SWT.NONE);
		spinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));
		spinner.setMaximum(300);
		spinner.setMinimum(10);
		spinner.setIncrement(10);
		spinner.setPageIncrement(20);

		Label l = new Label(group, SWT.NONE);
		l.setText("%");
		spinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true));

		final Scale scale = new Scale(group, SWT.HORIZONTAL);
		scale.setMinimum(10);
		scale.setMaximum(300);
		scale.setIncrement(10);
		scale.setPageIncrement(20);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));

		final IPreferenceStore prefs = MyPreferences.prefs();

		int value = prefs.getInt(MyPreferences.VIEW_ZOOM_FACTOR);
		scale.setSelection(value);
		spinner.setSelection(value);

		prefs.addPropertyChangeListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (!MyPreferences.VIEW_ZOOM_FACTOR.equals(event.getProperty()))
					return;
				if (scale.isDisposed()) {
					prefs.removePropertyChangeListener(this);
					return;
				}
				Integer v = (Integer) event.getNewValue();
				scale.setSelection(v.intValue());
				spinner.setSelection(v.intValue());
			}
		});

		final SelectionAdapter listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Widget widget = e.widget;
				int s;
				if (widget instanceof Scale)
					s = ((Scale) widget).getSelection();
				else if (widget instanceof Spinner)
					s = ((Spinner) widget).getSelection();
				else
					return;
				prefs.setValue(MyPreferences.VIEW_ZOOM_FACTOR, s);
			}
		};
		spinner.addSelectionListener(listener);
		scale.addSelectionListener(listener);
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
