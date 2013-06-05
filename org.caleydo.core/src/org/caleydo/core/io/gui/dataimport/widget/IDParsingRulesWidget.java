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

import java.util.regex.PatternSyntaxException;

import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.ATextParser;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

	/**
	 * Button to specify whether the ids shall be transformed to upper case.
	 */
	protected Button toUpperCaseButton;

	protected Label replacementRegExLabel;
	protected Label replacementStringLabel;
	protected Label substringRegExLabel;
	protected Label substringExplanationLabel;
	protected Label replacementExplanationLabel;
	protected Label previewLabel;
	protected Label originalIDLabel;
	protected Label convertedIDLabel;
	protected Label errorImage;
	protected Label errorLabel;

	protected String idSample;

	protected final ICallback<Boolean> validRegExCallback;

	/**
	 * @param parent
	 * @param templateIdTypeParsingRules
	 *            Template that is used to fill the widgets. May be null.
	 * @param showEnableButton
	 *            Determines, whether a checkbox is displayed that enables/disables all widgets.
	 * @param idSample
	 *            Sample id that shall be used to preview effects of regular expressions.
	 * @param validRegExCallback
	 *            Called when the inserted regular expression is valid or not valid. This can be used to disable buttons
	 *            external to this widget when the inserted regex is not valid.
	 */
	public IDParsingRulesWidget(Composite parent, IDTypeParsingRules templateIdTypeParsingRules,
			boolean showEnableButton, String idSample, ICallback<Boolean> validRegExCallback) {
		this.idSample = idSample;
		this.validRegExCallback = validRegExCallback;

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

		ModifyListener regexModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePreview();

			}
		};

		toUpperCaseButton = new Button(regExGroup, SWT.CHECK);
		toUpperCaseButton.setText("To upper case");
		toUpperCaseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreview();
			}
		});

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
		replacementRegExTextField.addModifyListener(regexModifyListener);

		replacementStringLabel = new Label(regExGroup, SWT.NONE);
		replacementStringLabel.setText("with");

		replacementStringTextField = new Text(regExGroup, SWT.BORDER);
		replacementStringTextField.setLayoutData(replacementTextFieldsGridData);
		replacementStringTextField.addModifyListener(regexModifyListener);

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
		substringRegExTextField.addModifyListener(regexModifyListener);

		if (idSample != null) {
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
			previewLabel = new Label(regExGroup, SWT.NONE);
			previewLabel.setText("Preview: ");
			previewLabel.setLayoutData(gridData);
			FontData fontData = previewLabel.getFont().getFontData()[0];
			Font font = new Font(previewLabel.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(),
					SWT.BOLD));
			previewLabel.setFont(font);

			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
			originalIDLabel = new Label(regExGroup, SWT.NONE);
			originalIDLabel.setText("Original ID: " + idSample);
			originalIDLabel.setLayoutData(gridData);
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
			convertedIDLabel = new Label(regExGroup, SWT.NONE);
			convertedIDLabel.setLayoutData(gridData);

		}

		errorImage = new Label(parent, SWT.NONE);
		errorImage.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR));
		errorImage.setVisible(false);
		errorLabel = new Label(parent, SWT.NONE);
		errorLabel.setText("Invalid regular expression.");
		errorLabel.setVisible(false);

		fillWidgets(templateIdTypeParsingRules);
	}

	/**
	 * Fills all widgets according to the specified {@link IDTypeParsingRules}.
	 *
	 * @param idTypeParsingRules
	 */
	private void fillWidgets(IDTypeParsingRules idTypeParsingRules) {
		if (idTypeParsingRules != null) {

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
		updatePreview();
	}

	private void updatePreview() {
		if (idSample == null)
			return;
		try {
			String idPreview = ATextParser.convertID(idSample, getIDTypeParsingRules());
			convertedIDLabel.setText("Converted ID: " + idPreview);
			if (errorImage.isVisible()) {
				errorImage.setVisible(false);
				errorLabel.setVisible(false);
				validRegExCallback.on(true);
			}
		} catch (PatternSyntaxException e) {
			// it can happen, that a regex is not valid during entering a value...
			if (!errorImage.isVisible()) {
				errorImage.setVisible(true);
				errorLabel.setVisible(true);
				validRegExCallback.on(false);
			}
		}
	}

	/**
	 * Creates parsing rules specified by the gui elements of this widget.
	 */
	public IDTypeParsingRules getIDTypeParsingRules() {
		IDTypeParsingRules idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setToUpperCase(toUpperCaseButton.getSelection());
		idTypeParsingRules.setReplacementExpression(replacementStringTextField.getText(),
				replacementRegExTextField.getText());
		idTypeParsingRules.setSubStringExpression(getSubStringExpression());
		return idTypeParsingRules;
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
		if (convertedIDLabel != null) {
			previewLabel.setEnabled(enabled);
			originalIDLabel.setEnabled(enabled);
			convertedIDLabel.setEnabled(enabled);
		}
		toUpperCaseButton.setEnabled(enabled);
	}

	/**
	 * @return True if all widgets are enabled, false otherwise.
	 */
	public boolean isEnabled() {
		return useRegExButton.getSelection();
	}

	// /**
	// * @return The replacing expression entered by the user. Null will be returned in case of an empty string.
	// */
	// private String getReplacingExpression() {
	// return replacementRegExTextField.getText().equals("") ? null : replacementRegExTextField.getText();
	// }
	//
	// /**
	// * @return The replacement string entered by the user.
	// */
	// private String getReplacementString() {
	// return replacementStringTextField.getText();
	// }

	/**
	 * @return The substring expression entered by te user. Null will be returned in case of an empty string.
	 */
	private String getSubStringExpression() {
		return substringRegExTextField.getText().equals("") ? null : substringRegExTextField.getText();
	}

}
