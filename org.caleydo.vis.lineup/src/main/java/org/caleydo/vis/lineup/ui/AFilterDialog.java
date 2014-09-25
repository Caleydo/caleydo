/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.vis.lineup.ui;


import org.caleydo.vis.lineup.model.mixin.IFilterColumnMixin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AFilterDialog extends Window {
	protected final Object receiver;

	protected final String title;
	protected final boolean filterGlobally;
	protected final boolean filterRankIndependent;
	protected final boolean hasSnapshots;
	private final Point loc;

	private Button filterGloballyUI;
	private Button filterRankIndependentUI;
	private FontMetrics fontMetrics;

	public AFilterDialog(Shell parentShell, String title, Object receiver, IFilterColumnMixin model,
			boolean hasSnapshots, Point loc) {
		super(parentShell);
		this.title = title;
		this.receiver = receiver;
		this.filterGlobally = model != null ? model.isGlobalFilter() : false;
		this.filterRankIndependent = model != null ? model.isRankIndependentFilter() : false;
		this.hasSnapshots = hasSnapshots;
		this.loc = loc;
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		Point computeSize = getShell().getChildren()[0].computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return new Point(loc.x, loc.y - computeSize.y);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(title);
	}

	protected int getCharWith(Composite parent, int chars) {
		if (fontMetrics == null) {
			GC gc = new GC(parent);
			fontMetrics = gc.getFontMetrics();
			gc.dispose();
		}
		return fontMetrics.getAverageCharWidth() * chars;
	}

	protected int getCharHeight(Composite parent) {
		if (fontMetrics == null) {
			GC gc = new GC(parent);
			fontMetrics = gc.getFontMetrics();
			gc.dispose();
		}
		return fontMetrics.getHeight();
	}

	/**
	 * @param gridData
	 * @return
	 */
	protected static GridData twoColumns(GridData gridData) {
		gridData.horizontalSpan = 2;
		return gridData;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		// create message
		createSpecificFilterUI(composite);

		composite.pack();
		return composite;
	}


	/**
	 * @param composite
	 */
	protected final void addButtonAndOption(Composite composite) {
		initFilterRank(composite);
		addOKButton(composite, hasSnapshots);
		if (hasSnapshots)
			initFilterGlobally(composite);
	}

	private Button initFilterRank(Composite composite) {
		filterRankIndependentUI = new Button(composite, SWT.CHECK);
		filterRankIndependentUI.setText("Should the filter influence the ranking?");
		filterRankIndependentUI.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		filterRankIndependentUI.setSelection(!filterRankIndependent);
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerEvent(false);
			}
		};
		filterRankIndependentUI.addSelectionListener(adapter);
		return filterRankIndependentUI;
	}

	private Button initFilterGlobally(Composite composite) {
		filterGloballyUI = new Button(composite, SWT.CHECK);
		filterGloballyUI.setText("Apply filter to all snapshots?");
		filterGloballyUI.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		filterGloballyUI.setSelection(filterGlobally);
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerEvent(false);
			}
		};
		filterGloballyUI.addSelectionListener(adapter);
		return filterGloballyUI;
	}

	/**
	 * @param b
	 */
	private final void addOKButton(Composite composite, boolean spanOverTwoRows) {
		Button b = new Button(composite, SWT.PUSH);
		b.setText(IDialogConstants.OK_LABEL);
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd.verticalSpan = spanOverTwoRows ? 2 : 1;
		b.setLayoutData(gd);

		b.getShell().setDefaultButton(b);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerEvent(false);
				setReturnCode(OK);
				close();
			}
		});
	}

	@Override
	protected void handleShellCloseEvent() {
		triggerEvent(true);
		super.handleShellCloseEvent();
	}

	/**
	 * @return
	 */
	protected final boolean isFilterGlobally() {
		return filterGloballyUI == null ? filterGlobally : filterGloballyUI.getSelection();
	}

	protected final boolean isFilterRankIndependent() {
		return filterRankIndependentUI == null ? filterRankIndependent : !filterRankIndependentUI.getSelection();
	}

	protected abstract void createSpecificFilterUI(Composite composite);

	protected abstract void triggerEvent(boolean cancel);
}

