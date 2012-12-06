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
package org.caleydo.view.tourguide.vendingmachine.ui;

import org.caleydo.view.tourguide.data.filter.CompareDomainFilter;
import org.caleydo.view.tourguide.data.filter.EStringCompareOperator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Samuel Gratzl
 *
 */
public class DataDomainFilterWidget {
	private final CompareDomainFilter filter;
	private final Combo operatorUI;
	private final Text operandUI;

	/**
	 * @param filter
	 */
	public DataDomainFilterWidget(Composite parent, CompareDomainFilter filter) {
		this.filter = filter;
		Label l = new Label(parent, SWT.NONE);
		if (filter.isAgainstStratification())
			l.setText("Stratification X");
		else
			l.setText("Group X");
		GridData d = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		l.setLayoutData(d);
		this.operatorUI = new Combo(parent, SWT.READ_ONLY);
		this.operatorUI.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		this.operatorUI.setItems(EStringCompareOperator.getLabels());
		this.operatorUI.setText(filter.getOp().getLabel());

		this.operandUI = new Text(parent, SWT.BORDER);
		this.operandUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.operandUI.setText(filter.getOperand());
	}

	public CompareDomainFilter save() {
		this.filter.setOperand(operandUI.getText());
		this.filter.setOp(EStringCompareOperator.values()[operatorUI.getSelectionIndex()]);
		return this.filter;
	}

}
