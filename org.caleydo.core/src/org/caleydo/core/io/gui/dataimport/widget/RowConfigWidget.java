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

import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Samuel Gratzl
 *
 */
public class RowConfigWidget {
	private final Group group;
	private final Spinner columnOfRowIDSpinner;
	private final Spinner numHeaderRowsSpinner;
	private final Combo rowIDCombo;
	private final Label categoryIDLabel;

	public RowConfigWidget(Composite parent, final IntegerCallback onNumHeaderRowsChanged,
			final IntegerCallback onColumnOfRowIDChanged) {
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Row Configuration");
		group.setLayout(new GridLayout(1, false));

		Composite leftConfigGroupPart = new Composite(group, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label idCategoryLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idCategoryLabel.setText("Row ID Class");
		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT));
		this.categoryIDLabel = new Label(leftConfigGroupPart, SWT.NONE);

		Label idTypeLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText("Row ID Type");
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		this.rowIDCombo = new Combo(leftConfigGroupPart, SWT.DROP_DOWN | SWT.READ_ONLY);
		rowIDCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label startParseAtLineLabel = new Label(leftConfigGroupPart, SWT.NONE);
		startParseAtLineLabel.setText("Number of Header Rows");

		this.numHeaderRowsSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(0);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
		numHeaderRowsSpinner.setSelection(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		numHeaderRowsSpinner.setLayoutData(gridData);
		numHeaderRowsSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				onNumHeaderRowsChanged.on(numHeaderRowsSpinner.getSelection());
			}
		});

		Label columnOfRowIDlabel = new Label(leftConfigGroupPart, SWT.NONE);
		columnOfRowIDlabel.setText("Column with Row IDs");
		// columnOfRowIDGroup.setLayout(new GridLayout(1, false));

		this.columnOfRowIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		columnOfRowIDSpinner.setMinimum(1);
		columnOfRowIDSpinner.setMaximum(Integer.MAX_VALUE);
		columnOfRowIDSpinner.setIncrement(1);
		columnOfRowIDSpinner.setSelection(1);
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		columnOfRowIDSpinner.setLayoutData(gridData);
		columnOfRowIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				onColumnOfRowIDChanged.on(columnOfRowIDSpinner.getSelection());
			}
		});
	}

	public void setLayoutData(Object layoutData) {
		this.group.setLayoutData(layoutData);
	}

	public void setEnabled(boolean enabled) {
		this.group.setEnabled(enabled);
		this.columnOfRowIDSpinner.setEnabled(enabled);
		this.numHeaderRowsSpinner.setEnabled(enabled);
		this.rowIDCombo.setEnabled(enabled);
	}


	public void setCategoryID(IDCategory rowIDCategory) {
		this.categoryIDLabel.setText(rowIDCategory.getCategoryName());
	}

	public void setNumHeaderRows(int numberOfHeaderLines) {
		this.numHeaderRowsSpinner.setSelection(numberOfHeaderLines);
	}

	public void setColumnOfRowIds(int i) {
		this.columnOfRowIDSpinner.setSelection(i);
	}

	public void setMaxDimension(int totalNumberOfColumns, int totalNumberOfRows) {
		columnOfRowIDSpinner.setMaximum(totalNumberOfColumns);
		numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
	}

	public void setIDTypes(List<IDType> rowIDTypes, IDType selection) {
		rowIDCombo.removeAll();
		for (IDType type : rowIDTypes)
			rowIDCombo.add(type.getTypeName());
		if (selection != null)
			rowIDCombo.select(rowIDTypes.indexOf(selection));
		else if (rowIDTypes.size() == 1) {
			rowIDCombo.select(0);
			rowIDCombo.setEnabled(false);
		}
	}

	public IDType getIDType() {
		int i = rowIDCombo.getSelectionIndex();
		if (i < 0)
			return null;
		String type = rowIDCombo.getItem(i);
		return IDType.getIDType(type);
	}

}
