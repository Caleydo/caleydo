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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
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
public class StringFilterDialog extends AFilterDialog {
	private final String filter;

	private Text text;

	private String hint;


	public StringFilterDialog(Shell parentShell, String title, String hint, Object receiver, String filter,
			boolean filterGlobally, boolean hasSnapshots) {
		super(parentShell, title, receiver, filterGlobally, hasSnapshots);
		this.hint = hint;
		this.filter = filter;
	}

	@Override
	protected void createSpecificFilterUI(Composite composite) {
		// create message
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Edit the filter: " + hint);
		label.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		label.setFont(composite.getFont());

		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		text.setText(filter == null ? "" : filter);
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String newText = text.getText();
				if (newText.length() >= 2)
					EventPublisher.trigger(new FilterEvent(newText, filterGloballyUI.getSelection()).to(receiver));
				if (newText.isEmpty())
					EventPublisher.trigger(new FilterEvent(null, filterGloballyUI.getSelection()).to(receiver));
			}
		});
		text.selectAll();
		text.setFocus();
	}

	@Override
	protected void triggerEvent() {
		EventPublisher.trigger(new FilterEvent(text.getText(), filterGloballyUI.getSelection()).to(receiver));
	}

	@Override
	protected void okPressed() {
		// real values
		EventPublisher.trigger(new FilterEvent(text.getText(), filterGloballyUI.getSelection()).to(receiver));
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		// original values
		EventPublisher.trigger(new FilterEvent(filter, filterGlobally).to(receiver));
		super.cancelPressed();
	}
}

