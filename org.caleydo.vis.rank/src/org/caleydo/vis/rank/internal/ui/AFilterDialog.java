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
package org.caleydo.vis.rank.internal.ui;


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
	protected final boolean hasSnapshots;
	private final Point loc;

	private Button filterGloballyUI;
	private FontMetrics fontMetrics;

	public AFilterDialog(Shell parentShell, String title, Object receiver, boolean filterGlobally,
			boolean hasSnapshots, Point loc) {
		super(parentShell);
		this.title = title;
		this.receiver = receiver;
		this.filterGlobally = filterGlobally;
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

	protected final void createApplyGlobally(Composite composite) {
		if (hasSnapshots) {
			filterGloballyUI = new Button(composite, SWT.CHECK);
			filterGloballyUI.setText("Apply filter to all snapshots?");
			filterGloballyUI.setLayoutData(twoColumns(new GridData(SWT.LEFT, SWT.CENTER, true, false)));
			filterGloballyUI.setSelection(filterGlobally);
			SelectionAdapter adapter = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					triggerEvent(false);
				}
			};
			filterGloballyUI.addSelectionListener(adapter);
		}
	}

	/**
	 * @param b
	 */
	protected final void addOKButton(Composite composite, boolean spanOverTwoColumns) {
		Button b = new Button(composite, SWT.PUSH);
		b.setText(IDialogConstants.OK_LABEL);
		GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gd.horizontalSpan = spanOverTwoColumns ? 2 : 1;
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

	protected abstract void createSpecificFilterUI(Composite composite);

	protected abstract void triggerEvent(boolean cancel);
}

