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

import org.caleydo.view.tourguide.data.filter.CompareScoreFilter;
import org.caleydo.view.tourguide.data.filter.ECompareOperator;
import org.caleydo.view.tourguide.util.EnumUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Samuel Gratzl
 *
 */
public class ScoreFilterWidget {
	private final CompareScoreFilter filter;
	private final Combo operatorUI;
	private final Spinner referenceUI;
	private final boolean isInt;

	/**
	 * @param filter
	 */
	public ScoreFilterWidget(Composite parent, CompareScoreFilter filter) {
		this.filter = filter;
		Label l = new Label(parent, SWT.NONE);
		l.setText(filter.getReference().getLabel());
		GridData d = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		d.widthHint = 200;
		l.setLayoutData(d);
		this.operatorUI = new Combo(parent, SWT.READ_ONLY);
		this.operatorUI.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		this.operatorUI.setItems(EnumUtils.getLabels(ECompareOperator.class));
		this.operatorUI.setText(filter.getOp().getLabel());

		this.isInt = filter.getReference().getScoreType().isRank();
		this.referenceUI = new Spinner(parent, SWT.BORDER);
		this.referenceUI.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		this.referenceUI.setMinimum(Integer.MIN_VALUE);
		this.referenceUI.setMaximum(Integer.MAX_VALUE);
		if (this.isInt) {
			// int
			this.referenceUI.setSelection((int) filter.getAgainst());
		} else {
			// float
			this.referenceUI.setSelection((int) (filter.getAgainst() * 1000));
			this.referenceUI.setDigits(3);
			this.referenceUI.setIncrement(100);
			this.referenceUI.setSelection(500);
		}
	}

	public CompareScoreFilter save() {
		if (this.isInt)
			this.filter.setAgainst(referenceUI.getSelection());
		else {
			this.filter.setAgainst(referenceUI.getSelection() / 1000.f);
		}
		this.filter.setOp(ECompareOperator.values()[operatorUI.getSelectionIndex()]);
		return this.filter;
	}

}
