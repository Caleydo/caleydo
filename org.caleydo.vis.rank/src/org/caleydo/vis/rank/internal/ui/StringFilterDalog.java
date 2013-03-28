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
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public class StringFilterDalog extends Dialog {
	private final Object receiver;

	private final String title;
	private final String filter;
	private final boolean filterGlobally;

	private Text text;
	private Button filterGloballyUI;

	private String hint;


	public StringFilterDalog(Shell parentShell, String title, String hint, Object receiver, String filter,
			boolean filterGlobally) {
		super(parentShell);
		this.title = title;
		this.hint = hint;
		this.receiver = receiver;
		this.filter = filter;
		this.filterGlobally = filterGlobally;
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Filter column: " + title);
	}

	@Override
	protected Composite createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// create message
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Edit the filter: " + hint);
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		text.setText(filter == null ? "" : filter);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String newText = text.getText();
				if (newText.length() >= 2)
					EventPublisher.publishEvent(new FilterEvent(newText, filterGloballyUI.getSelection()).to(receiver));
				if (newText.isEmpty())
					EventPublisher.publishEvent(new FilterEvent(null, filterGloballyUI.getSelection()).to(receiver));
			}
		});
		text.selectAll();
		text.setFocus();

		filterGloballyUI = new Button(composite, SWT.CHECK);
		filterGloballyUI.setText("Apply Filter Globally?");
		filterGloballyUI.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		filterGloballyUI.setSelection(filterGlobally);
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EventPublisher.publishEvent(new FilterEvent(text.getText(), filterGloballyUI.getSelection())
						.to(receiver));
			}
		};
		filterGloballyUI.addSelectionListener(adapter);

		applyDialogFont(composite);
		return composite;
	}

	@Override
	protected void okPressed() {
		// real values
		EventPublisher.publishEvent(new FilterEvent(text.getText(), filterGloballyUI.getSelection()).to(receiver));
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		// original values
		EventPublisher.publishEvent(new FilterEvent(filter, filterGlobally).to(receiver));
		super.cancelPressed();
	}
}

