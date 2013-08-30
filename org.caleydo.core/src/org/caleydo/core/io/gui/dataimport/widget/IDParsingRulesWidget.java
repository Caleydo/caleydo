/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport.widget;

import java.util.regex.PatternSyntaxException;

import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.parser.ascii.ATextParser;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.system.BrowserUtils;
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
import org.eclipse.swt.widgets.Link;
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

	protected final Group caseGroup;

	protected final Group replaceGroup;

	protected final Group substringGroup;

	/** Button to specify whether the ids shall be transformed to upper case. */
	protected Button toUpperCaseButton;
	/** Button to specify whether the ids shall be transformed to lower case. */
	protected Button toLowerCaseButton;
	/** Button for keeping case */
	protected Button keepCaseButton;

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
	protected Label caseExplanationLabel;
	protected Link regexLink;

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

		if (showEnableButton) {
			useRegExButton = new Button(parent, SWT.CHECK);
			useRegExButton.setText("Convert IDs");
			useRegExButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setEnabled(useRegExButton.getSelection());
				}
			});
		}

		Label conversionLabel = new Label(parent, SWT.WRAP);
		conversionLabel
				.setText("You can convert IDs using the follwing methods. They are applied in the order shown here.");
		conversionLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		caseGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		caseGroup.setText("Change Case");
		caseGroup.setLayout(new GridLayout(1, false));
		caseGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		caseExplanationLabel = new Label(caseGroup, SWT.WRAP);
		caseExplanationLabel
				.setText("Choose whether to keep the case of the IDs, convert all to lower, or convert all to upper case.");
		caseExplanationLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		keepCaseButton = new Button(caseGroup, SWT.RADIO);
		keepCaseButton.setText("Keep case unchanged");
		keepCaseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		keepCaseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreview();
			}
		});

		toUpperCaseButton = new Button(caseGroup, SWT.RADIO);
		toUpperCaseButton.setText("Make upper case");
		toUpperCaseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toUpperCaseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreview();
			}
		});

		toLowerCaseButton = new Button(caseGroup, SWT.RADIO);
		toLowerCaseButton.setText("Make lower case");
		toLowerCaseButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		toUpperCaseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePreview();
			}
		});

		replaceGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		replaceGroup.setText("Replace parts of IDs");
		replaceGroup.setLayout(new GridLayout(4, false));
		replaceGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Composite useRegExComposite = new Composite(replaceGroup, SWT.NONE);
		useRegExComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
		useRegExComposite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, true, false, 4, 1));

		ModifyListener regexModifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updatePreview();

			}
		};

		replacementExplanationLabel = new Label(replaceGroup, SWT.WRAP);
		replacementExplanationLabel
				.setText("Replace certain expressions within an ID by a specified "
						+ "string. For example, applying the regular expression '\\.' and the replacement string '-' on the ID "
						+ "'abc.000.yz' results in 'abc-000-yz'.");
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gridData.widthHint = 500;
		replacementExplanationLabel.setLayoutData(gridData);

		replacementRegExLabel = new Label(replaceGroup, SWT.NONE);
		replacementRegExLabel.setText("Replace");
		replacementRegExLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, false, true));

		replacementRegExTextField = new Text(replaceGroup, SWT.BORDER);

		GridData replacementTextFieldsGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		replacementTextFieldsGridData.widthHint = 150;
		replacementRegExTextField.setLayoutData(replacementTextFieldsGridData);
		replacementRegExTextField.addModifyListener(regexModifyListener);

		replacementStringLabel = new Label(replaceGroup, SWT.NONE);
		replacementStringLabel.setText("with");

		replacementStringTextField = new Text(replaceGroup, SWT.BORDER);
		replacementStringTextField.setLayoutData(replacementTextFieldsGridData);
		replacementStringTextField.addModifyListener(regexModifyListener);

		substringGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
		substringGroup.setText("Split IDs");
		substringGroup.setLayout(new GridLayout(4, false));
		substringGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		substringExplanationLabel = new Label(substringGroup, SWT.WRAP);
		substringExplanationLabel
				.setText("Splits an ID into substrings around matches of the specified regular expression. "
						+ "The first non-empty substring of this operation will be the resulting ID. For example, a leading string "
						+ "'abc' can be removed from an ID 'abc-001' using the expression 'abc-'. In this case the split operation "
						+ "results in ['','001']. Using, for example, '\\-' as expression would result in ['abc','001'] and 'abc' "
						+ "would be used as the result.");
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gridData.widthHint = 500;
		substringExplanationLabel.setLayoutData(gridData);

		substringRegExLabel = new Label(substringGroup, SWT.NONE);
		substringRegExLabel.setText("Substring specification");

		substringRegExTextField = new Text(substringGroup, SWT.BORDER);
		substringRegExTextField.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		substringRegExTextField.addModifyListener(regexModifyListener);

		if (idSample != null) {
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
			previewLabel = new Label(substringGroup, SWT.NONE);
			previewLabel.setText("Preview: ");
			previewLabel.setLayoutData(gridData);
			FontData fontData = previewLabel.getFont().getFontData()[0];
			Font font = new Font(previewLabel.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(),
					SWT.BOLD));
			previewLabel.setFont(font);

			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
			originalIDLabel = new Label(substringGroup, SWT.NONE);
			originalIDLabel.setText("Original ID: " + idSample);
			originalIDLabel.setLayoutData(gridData);
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
			convertedIDLabel = new Label(substringGroup, SWT.NONE);
			convertedIDLabel.setLayoutData(gridData);

		}

		errorImage = new Label(parent, SWT.NONE);
		errorImage.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR));
		errorImage.setVisible(false);
		errorLabel = new Label(parent, SWT.NONE);
		errorLabel.setText("Invalid regular expression.");
		errorLabel.setVisible(false);

		regexLink = new Link(parent, SWT.WRAP);
		regexLink
				.setText(" <a href=\"http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html\">Regular Expression Reference</a>");
		regexLink.addSelectionListener(BrowserUtils.LINK_LISTENER);


		fillWidgets(templateIdTypeParsingRules);
	}

	/**
	 * Fills all widgets according to the specified {@link IDTypeParsingRules}.
	 *
	 * @param idTypeParsingRules
	 */
	private void fillWidgets(IDTypeParsingRules idTypeParsingRules) {
		if (idTypeParsingRules != null) {

			if (idTypeParsingRules.isToLowerCase()) {
				toLowerCaseButton.setSelection(true);
			} else if (idTypeParsingRules.isToUpperCase()) {
				toUpperCaseButton.setSelection(true);
			} else {
				keepCaseButton.setSelection(true);
			}
			// else {
			// keepCaseButton.setSelection(true);
			// }

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
		} else {
			keepCaseButton.setSelection(true);
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
		idTypeParsingRules.setToLowerCase(toLowerCaseButton.getSelection());
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
		caseGroup.setEnabled(enabled);
		replaceGroup.setEnabled(enabled);
		substringGroup.setEnabled(enabled);
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
		toLowerCaseButton.setEnabled(enabled);
		keepCaseButton.setEnabled(enabled);
		caseExplanationLabel.setEnabled(enabled);
		regexLink.setEnabled(enabled);

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
