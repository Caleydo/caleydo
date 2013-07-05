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
import org.caleydo.core.io.IDTypeParsingRules;
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
 * Widget that contains all necessary fields for specifying {@link IDTypeParsingRules}.
 *
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
	protected Label substringExplanationLabel;
	protected Label replacementExplanationLabel;

	/**
	 * @param parent
	 * @param templateIdTypeParsingRules
	 *            Template that is used to fill the widgets. May be null.
	 * @param showEnableButton
	 *            Determines, whether a checkbox is displayed that enables/disables all widgets.
	 */
	public IDParsingRulesWidget(Composite parent, IDTypeParsingRules templateIdTypeParsingRules,
			boolean showEnableButton) {

		final Group regExGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		regExGroup.setText("Regular expressions");
		regExGroup.setLayout(new GridLayout(4, false));
		regExGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Composite useRegExComposite = new Composite(regExGroup, SWT.NONE);
		useRegExComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		useRegExComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 4, 1));
		if (showEnableButton) {
			useRegExButton = new Button(useRegExComposite, SWT.CHECK);
			useRegExButton.setText("Use regular expressions to convert IDs");
			useRegExButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setEnabled(useRegExButton.getSelection());
				}
			});
		}

		replacementExplanationLabel = new Label(regExGroup, SWT.WRAP);
		replacementExplanationLabel
				.setText("In order to convert the IDs of a data file into a format that can be mapped by Caleydo, regular expressions "
						+ "can be used. Caleydo supports two different methods for ID conversion and they are applied to the IDs in "
						+ "consecutive order.\nIn the first method, certain expressions within an ID are replaced by a specified "
						+ "string. For example, applying the regular expression '\\.' and the replacement string '-' on the ID "
						+ "'abc.000.yz' results in 'abc-000-yz'.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gridData.widthHint = 500;
		replacementExplanationLabel.setLayoutData(gridData);

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

		substringExplanationLabel = new Label(regExGroup, SWT.WRAP);
		substringExplanationLabel
				.setText("The second method splits an ID into substrings around matches of the specified regular expression. "
						+ "The first non-empty substring of this operation will be the resulting ID. For example, a leading string "
						+ "'abc' can be removed from an ID 'abc-001' using the expression 'abc-'. In this case the split operation "
						+ "results in ['','001']. Using, for example, '\\-' as expression would result in ['abc','001'] and 'abc' "
						+ "would be used as the result.");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gridData.widthHint = 500;
		substringExplanationLabel.setLayoutData(gridData);

		substringRegExLabel = new Label(regExGroup, SWT.NONE);
		substringRegExLabel.setText("Substring specification");

		substringRegExTextField = new Text(regExGroup, SWT.BORDER);
		substringRegExTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

		fillWidgets(templateIdTypeParsingRules);
	}

	/**
	 * Fills all widgets according to the specified {@link IDTypeParsingRules}.
	 *
	 * @param idTypeParsingRules
	 */
	private void fillWidgets(IDTypeParsingRules idTypeParsingRules) {
		if (idTypeParsingRules == null)
			return;

		String[] replacingExpressions = idTypeParsingRules.getReplacingExpressions();
		if (replacingExpressions != null && replacingExpressions.length >= 1 && replacingExpressions[0] != null) {
			replacementRegExTextField.setText(replacingExpressions[0]);
		}
		String replacementString = idTypeParsingRules.getReplacementString();
		if (replacementString != null) {
			replacementStringTextField.setText(replacementString);
		}
		String subStringExpression = idTypeParsingRules.getSubStringExpression();
		if (subStringExpression != null) {
			substringRegExTextField.setText(subStringExpression);
		}
	}

	/**
	 * Enables or disables all widgets.
	 *
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		replacementRegExTextField.setEnabled(enabled);
		replacementStringTextField.setEnabled(enabled);
		substringRegExTextField.setEnabled(enabled);
		replacementRegExLabel.setEnabled(enabled);
		replacementStringLabel.setEnabled(enabled);
		substringRegExLabel.setEnabled(enabled);
		substringExplanationLabel.setEnabled(enabled);
		replacementExplanationLabel.setEnabled(enabled);
	}

	/**
	 * @return True if all widgets are enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return useRegExButton.getSelection();
	}

	/**
	 * @return The replacing expression entered by the user. Null will be returned in case of an empty string.
	 */
	public String getReplacingExpression() {
		return replacementRegExTextField.getText().equals("") ? null : replacementRegExTextField.getText();
	}

	/**
	 * @return The replacement string entered by the user.
	 */
	public String getReplacementString() {
		return replacementStringTextField.getText();
	}

	/**
	 * @return The substring expression entered by te user. Null will be returned in case of an empty string.
	 */
	public String getSubStringExpression() {
		return substringRegExTextField.getText().equals("") ? null : substringRegExTextField.getText();
	}

}
