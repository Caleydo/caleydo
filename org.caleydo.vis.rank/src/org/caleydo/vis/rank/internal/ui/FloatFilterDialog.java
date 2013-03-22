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


import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.vis.rank.internal.event.FilterEvent;
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
public class FloatFilterDialog extends Dialog {
	private final Object receiver;

	private Button filterNotMappedUI;
	private Button filterMissingUI;

	private final String title;

	private final boolean filterMissing;
	private final boolean filterNotMapped;

	public FloatFilterDialog(Shell parentShell, String title, Object receiver, boolean filterNotMapped,
			boolean filterMissing) {
		super(parentShell);
		this.title = title;
		this.receiver = receiver;
		this.filterMissing = filterMissing;
		this.filterNotMapped = filterNotMapped;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Filter Settings for " + title);
	}

	@Override
	protected Composite createDialogArea(Composite parent) {
		Composite res = (Composite) super.createDialogArea(parent);
		// Composite p = new Composite(res, SWT.NONE)
		filterNotMappedUI = new Button(res, SWT.CHECK);
		filterNotMappedUI.setText("Filter Not Mapped Entries?");
		filterNotMappedUI.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		filterNotMappedUI.setSelection(filterNotMapped);

		filterMissingUI = new Button(res, SWT.CHECK);
		filterMissingUI.setText("Filter Missing Value Entries?");
		filterMissingUI.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		filterMissingUI.setSelection(filterMissing);

		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventPublisher.publishEvent(new FilterEvent(Pair.make(filterNotMappedUI.getSelection(),
						filterMissingUI.getSelection())).to(receiver));
			}
		};
		filterNotMappedUI.addSelectionListener(adapter);
		filterMissingUI.addSelectionListener(adapter);
		return res;
	}
}

