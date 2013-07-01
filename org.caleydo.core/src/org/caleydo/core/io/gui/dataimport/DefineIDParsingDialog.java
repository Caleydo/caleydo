/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.gui.dataimport.widget.IDParsingRulesWidget;
import org.caleydo.core.util.base.ICallback;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog for specifying the parsing of IDTypes using regular expressions.
 *
 * @author Christian Partl
 *
 */
public class DefineIDParsingDialog extends Dialog {

	/**
	 * Widget containing all fields for specifying regular expressions for id parsing.
	 */
	private IDParsingRulesWidget idParsingRulesWidget;

	/**
	 * The parsing rules defined within this dialog. They are created as non-default.
	 */
	private IDTypeParsingRules idTypeParsingRules;

	/**
	 * Parsing rules that are used as template to fill the widgets.
	 */
	private IDTypeParsingRules templateIdTypeParsingRules;

	/**
	 * ID that is used as sample to preview effects of regular expressions.
	 */
	private String idSample;

	/**
	 * @param parentShell
	 */
	public DefineIDParsingDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @param parentShell
	 * @param templateIdTypeParsingRules
	 *            Parsing rules that are used as template to fill the widgets. This object will not be modified within
	 *            this dialog and differs from the object obtained from {@link #getIdTypeParsingRules()}.
	 * @param idSample
	 *            ID that is used as sample to preview effects of regular expressions.
	 */
	public DefineIDParsingDialog(Shell parentShell, IDTypeParsingRules templateIdTypeParsingRules, String idSample) {
		super(parentShell);
		this.templateIdTypeParsingRules = templateIdTypeParsingRules;
		this.idSample = idSample;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Define ID Parsing");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout(2, false));

		idParsingRulesWidget = new IDParsingRulesWidget(parentComposite, templateIdTypeParsingRules, false, idSample,
				new ICallback<Boolean>() {

					@Override
					public void on(Boolean data) {
						if (!data) {
							DefineIDParsingDialog.this.getButton(OK).setEnabled(false);
						} else {
							DefineIDParsingDialog.this.getButton(OK).setEnabled(true);
						}
					}
				});

		return parent;
	}

	@Override
	protected void okPressed() {

		// if (idParsingRulesWidget.getReplacingExpression() == null
		// && idParsingRulesWidget.getSubStringExpression() == null) {
		// MessageDialog.openError(new Shell(), "Incomplete Parsing Definition",
		// "At least one expression (replacing or substring) must be specified.");
		// return;
		// }

		idTypeParsingRules = idParsingRulesWidget.getIDTypeParsingRules();
		// idTypeParsingRules.setReplacementExpression(idParsingRulesWidget.getReplacementString(),
		// idParsingRulesWidget.getReplacingExpression());
		// idTypeParsingRules.setSubStringExpression(idParsingRulesWidget.getSubStringExpression());
		idTypeParsingRules.setDefault(false);

		super.okPressed();
	}

	/**
	 * @return If ok has been pressed, the {@link #idTypeParsingRules}, null otherwise.
	 */
	public IDTypeParsingRules getIdTypeParsingRules() {
		return idTypeParsingRules;
	}

}
