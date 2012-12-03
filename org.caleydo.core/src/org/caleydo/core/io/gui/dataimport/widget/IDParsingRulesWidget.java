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
package org.caleydo.core.io.gui.dataimport.widget;

import org.caleydo.core.id.IDType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian Partl
 *
 */
public class IDParsingRulesWidget {

	/**
	 * Text field where the user can specify a string that shall be replaced using regular expressions. This regular
	 * expression is applied when parsing ids of the {@link IDType} created using this dialog.
	 */
	protected Text replacementRegExTextField;

	/**
	 * Text field where the user can specify a string that shall replace the string specified by
	 * {@link #replacementRegExTextField}. This regular expression is applied when parsing ids of the {@link IDType}
	 * created using this dialog.
	 */
	protected Text replacementStringTextField;

	/**
	 * Text field where the user can specify a regular expression to define a substring. This regular expression is
	 * applied when parsing ids of the {@link IDType} created using this dialog.
	 */
	protected Text substringRegExTextField;

	/**
	 * Button to specify whether to use regular expressions shall be used to parse ids for the {@link IDType} created in
	 * this dialog.
	 */
	protected Button useRegExButton;

	protected Label replacementRegExLabel;
	protected Label replacementStringLabel;
	protected Label substringRegExLabel;

	public IDParsingRulesWidget(Composite parent) {

		final Group regExGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		regExGroup.setText("Regular expressions");
		regExGroup.setLayout(new GridLayout(4, false));
		regExGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Composite useRegExComposite = new Composite(regExGroup, SWT.NONE);
		useRegExComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		useRegExComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 4, 1));
		useRegExButton = new Button(useRegExComposite, SWT.CHECK);
		useRegExButton.setText("Use regular expressions to convert IDs");

		replacementRegExLabel = new Label(regExGroup, SWT.NONE);
		replacementRegExLabel.setText("Replace");
		replacementRegExLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

		replacementRegExTextField = new Text(regExGroup, SWT.BORDER);

		GridData replacementTextFieldsGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		replacementTextFieldsGridData.widthHint = 150;
		replacementRegExTextField.setLayoutData(replacementTextFieldsGridData);

		replacementStringLabel = new Label(regExGroup, SWT.NONE);
		replacementStringLabel.setText("with");

		replacementStringTextField = new Text(regExGroup, SWT.BORDER);
		replacementStringTextField.setLayoutData(replacementTextFieldsGridData);

		substringRegExLabel = new Label(regExGroup, SWT.NONE);
		substringRegExLabel.setText("Substring specification");

		substringRegExTextField = new Text(regExGroup, SWT.BORDER);
		substringRegExTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		useRegExButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				setEnabled(useRegExButton.getSelection());
			}
		});
	}

	public void setEnabled(boolean enabled) {
		replacementRegExTextField.setEnabled(enabled);
		replacementStringTextField.setEnabled(enabled);
		substringRegExTextField.setEnabled(enabled);
		replacementRegExLabel.setEnabled(enabled);
		replacementStringLabel.setEnabled(enabled);
		substringRegExLabel.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return useRegExButton.isEnabled();
	}

	public String getReplacingExpression() {
		return replacementRegExTextField.getText();
	}

	public String getReplacementString() {
		return replacementStringTextField.getText();
	}

	public String getSubStringExpression() {
		return substringRegExTextField.getText();
	}

}
