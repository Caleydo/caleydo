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
package org.caleydo.view.tourguide.internal.external.ui;

import java.util.List;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.io.gui.dataimport.widget.IntegerCallback;
import org.caleydo.core.io.gui.dataimport.widget.PreviewTableWidget;
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
public class RowStratificationConfigWidget {
	private final Group group;
	private final Spinner columnIDSpinner;
	private final Spinner numHeaderRowsSpinner;
	private final Combo stratifiationCombo;
	private final Label categoryLabel;

	private final ATableBasedDataDomain dataDomain;
	private final boolean inDimensionDirection;

	public RowStratificationConfigWidget(Composite parent, ATableBasedDataDomain dataDomain,
			boolean inDimensionDirection, final IntegerCallback onNumHeaderRowsChanged,
			final IntegerCallback onColumnOfRowIDChanged) {
		this.dataDomain = dataDomain;
		this.inDimensionDirection = inDimensionDirection;
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Row Configuration");
		group.setLayout(new GridLayout(1, false));

		Composite leftConfigGroupPart = new Composite(group, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(2, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label idCategoryLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idCategoryLabel.setText("Data Domain");
		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT));
		this.categoryLabel = new Label(leftConfigGroupPart, SWT.NONE);

		Label idTypeLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText("Stratification");
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		this.stratifiationCombo = new Combo(leftConfigGroupPart, SWT.DROP_DOWN | SWT.READ_ONLY);
		stratifiationCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Table table = dataDomain.getTable();
		if (inDimensionDirection) {
			this.categoryLabel.setText(dataDomain.getDimensionIDCategory().getCategoryName());
			for (String key : dataDomain.getDimensionPerspectiveIDs()) {
				stratifiationCombo.add(table.getDimensionPerspective(key).getLabel());
			}
		} else {
			this.categoryLabel.setText(dataDomain.getRecordIDCategory().getCategoryName());
			for (String key : dataDomain.getRecordPerspectiveIDs()) {
				stratifiationCombo.add(table.getRecordPerspective(key).getLabel());
			}
		}

		if (stratifiationCombo.getItemCount() == 1) {
			stratifiationCombo.select(0);
			stratifiationCombo.setEnabled(false);
		}

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
		columnOfRowIDlabel.setText("Column with Group Labels");
		// columnOfRowIDGroup.setLayout(new GridLayout(1, false));

		this.columnIDSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		columnIDSpinner.setMinimum(1);
		columnIDSpinner.setMaximum(Integer.MAX_VALUE);
		columnIDSpinner.setIncrement(1);
		columnIDSpinner.setSelection(1);
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.widthHint = 70;
		columnIDSpinner.setLayoutData(gridData);
		columnIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				onColumnOfRowIDChanged.on(columnIDSpinner.getSelection());
			}
		});
	}

	public void setLayoutData(Object layoutData) {
		this.group.setLayoutData(layoutData);
	}

	public void setEnabled(boolean enabled) {
		this.group.setEnabled(enabled);
		this.columnIDSpinner.setEnabled(enabled);
		this.numHeaderRowsSpinner.setEnabled(enabled);
		this.stratifiationCombo.setEnabled(enabled);
	}

	public String getPerspectiveKey() {
		String selection = stratifiationCombo.getText();
		if (selection == null || selection.trim().isEmpty())
			return null;

		Table table = dataDomain.getTable();
		if (inDimensionDirection) {
			for (String key : dataDomain.getDimensionPerspectiveIDs()) {
				if (selection.equals(table.getDimensionPerspective(key).getLabel()))
					return key;
			}
		} else {
			for (String key : dataDomain.getRecordPerspectiveIDs()) {
				if (selection.equals(table.getRecordPerspective(key).getLabel()))
					return key;
			}
		}
		return null;
	}

	/**
	 * @param perspectiveKey
	 */
	public void setPerspectiveKey(String perspectiveKey) {
		if (inDimensionDirection) {
			int i = 0;
			for (String key : dataDomain.getDimensionPerspectiveIDs()) {
				if (perspectiveKey.equals(key))
					stratifiationCombo.select(i);
				else
					i++;
			}
		} else {
			int i = 0;
			for (String key : dataDomain.getRecordPerspectiveIDs()) {
				if (perspectiveKey.equals(key))
					stratifiationCombo.select(i);
				else
					i++;
			}
		}
	}

	public void setNumHeaderRows(int numberOfHeaderLines) {
		this.numHeaderRowsSpinner.setSelection(numberOfHeaderLines);
	}

	public void setColumnOfRowIds(int i) {
		this.columnIDSpinner.setSelection(i);
	}

	public void setMaxDimension(int totalNumberOfColumns, int totalNumberOfRows) {
		columnIDSpinner.setMaximum(totalNumberOfColumns);
		numHeaderRowsSpinner.setMaximum(totalNumberOfRows);
	}

	public void determineConfigFromPreview(List<? extends List<String>> dataMatrix) {
		guessNumberOfHeaderRows(dataMatrix);
	}

	private void guessNumberOfHeaderRows(List<? extends List<String>> dataMatrix) {
		// In grouping case we can have 0 header rows as there does not have to
		// be an id row
		int numHeaderRows = 0;
		for (int i = 0; i < dataMatrix.size(); i++) {
			List<String> row = dataMatrix.get(i);
			int numFloatsFound = 0;
			for (int j = 0; j < row.size() && j < PreviewTableWidget.MAX_PREVIEW_TABLE_COLUMNS; j++) {
				String text = row.get(j);
				try {
					// This currently only works for numerical values
					Float.parseFloat(text);
					numFloatsFound++;
					if (numFloatsFound >= 3) {
						this.setNumHeaderRows(numHeaderRows);
						return;
					}
				} catch (Exception e) {

				}
			}
			numHeaderRows++;
		}
	}
}
