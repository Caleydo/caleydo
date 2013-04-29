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


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public abstract class AFilterDalog extends Dialog {
	protected final Object receiver;

	protected final String title;
	protected final boolean filterGlobally;
	protected final boolean hasSnapshots;

	protected Button filterGloballyUI;

	public AFilterDalog(Shell parentShell, String title, Object receiver, boolean filterGlobally, boolean hasSnapshots) {
		super(parentShell);
		this.title = title;
		this.receiver = receiver;
		this.filterGlobally = filterGlobally;
		this.hasSnapshots = hasSnapshots;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Filter column: " + title);
	}

	@Override
	protected final Composite createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// create message
		createSpecificFilterUI(composite);

		filterGloballyUI = new Button(composite, SWT.CHECK);
		filterGloballyUI.setText("Apply filter to all snapshots?");
		filterGloballyUI.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		filterGloballyUI.setSelection(filterGlobally);
		filterGloballyUI.setVisible(hasSnapshots);
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerEvent();
			}
		};
		filterGloballyUI.addSelectionListener(adapter);

		applyDialogFont(composite);
		return composite;
	}

	protected abstract void createSpecificFilterUI(Composite composite);

	protected abstract void triggerEvent();

}

