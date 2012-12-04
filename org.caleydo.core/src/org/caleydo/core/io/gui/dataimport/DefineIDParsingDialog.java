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
package org.caleydo.core.io.gui.dataimport;

import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.io.gui.dataimport.widget.IDParsingRulesWidget;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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
	 */
	public DefineIDParsingDialog(Shell parentShell, IDTypeParsingRules templateIdTypeParsingRules) {
		super(parentShell);
		this.templateIdTypeParsingRules = templateIdTypeParsingRules;
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

		idParsingRulesWidget = new IDParsingRulesWidget(parentComposite, templateIdTypeParsingRules, false);

		return parent;
	}

	@Override
	protected void okPressed() {

		if (idParsingRulesWidget.getReplacingExpression() == null
				&& idParsingRulesWidget.getSubStringExpression() == null) {
			MessageDialog.openError(new Shell(), "Incomplete Parsing Definition",
					"At least one expression (replacing or substring) must be specified.");
			return;
		}

		idTypeParsingRules = new IDTypeParsingRules();
		idTypeParsingRules.setReplacementExpression(idParsingRulesWidget.getReplacementString(),
				idParsingRulesWidget.getReplacingExpression());
		idTypeParsingRules.setSubStringExpression(idParsingRulesWidget.getSubStringExpression());
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
