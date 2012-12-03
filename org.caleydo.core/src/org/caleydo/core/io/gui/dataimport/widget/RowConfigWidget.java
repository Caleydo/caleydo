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

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.io.IDTypeParsingRules;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
	/**
	 * The maximum number of ids that are tested in order to determine the {@link IDType}.
	 */
	private static final int MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION = 10;

	private final Group group;
	private final Spinner columnOfRowIDSpinner;
	private final Spinner numHeaderRowsSpinner;
	private final Combo rowIDCombo;
	private final Label categoryIDLabel;
	private final Button defineParsingButton;

	public RowConfigWidget(Composite parent, final IntegerCallback onNumHeaderRowsChanged,
			final IntegerCallback onColumnOfRowIDChanged) {
		this.group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		group.setText("Row Configuration");
		group.setLayout(new GridLayout(1, false));

		Composite leftConfigGroupPart = new Composite(group, SWT.NONE);
		leftConfigGroupPart.setLayout(new GridLayout(3, false));
		leftConfigGroupPart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label idCategoryLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idCategoryLabel.setText("Row ID Class");
		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT));
		this.categoryIDLabel = new Label(leftConfigGroupPart, SWT.NONE);
		categoryIDLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		Label idTypeLabel = new Label(leftConfigGroupPart, SWT.SHADOW_ETCHED_IN);
		idTypeLabel.setText("Row ID Type");
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT));
		this.rowIDCombo = new Combo(leftConfigGroupPart, SWT.DROP_DOWN | SWT.READ_ONLY);
		rowIDCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		this.defineParsingButton = new Button(leftConfigGroupPart, SWT.PUSH);
		defineParsingButton.setText("Define Parsing");
		defineParsingButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				onDefineParsing();
			}
		});

		Label startParseAtLineLabel = new Label(leftConfigGroupPart, SWT.NONE);
		startParseAtLineLabel.setText("Number of Header Rows");

		this.numHeaderRowsSpinner = new Spinner(leftConfigGroupPart, SWT.BORDER);
		numHeaderRowsSpinner.setMinimum(0);
		numHeaderRowsSpinner.setMaximum(Integer.MAX_VALUE);
		numHeaderRowsSpinner.setIncrement(1);
		numHeaderRowsSpinner.setSelection(1);
		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1);
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
		gridData = new GridData(SWT.LEFT, SWT.FILL, false, true, 2, 1);
		gridData.widthHint = 70;
		columnOfRowIDSpinner.setLayoutData(gridData);
		columnOfRowIDSpinner.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				onColumnOfRowIDChanged.on(columnOfRowIDSpinner.getSelection());
			}
		});
	}

	protected void onDefineParsing() {
		IDType idType = getIDType();
		idType.getIdTypeParsingRules();
	}

	public void setLayoutData(Object layoutData) {
		this.group.setLayoutData(layoutData);
	}

	public void setEnabled(boolean enabled) {
		this.group.setEnabled(enabled);
		this.columnOfRowIDSpinner.setEnabled(enabled);
		this.numHeaderRowsSpinner.setEnabled(enabled);
		this.rowIDCombo.setEnabled(enabled);
		this.defineParsingButton.setEnabled(enabled);
	}

	public void setCategoryID(IDCategory rowIDCategory) {
		this.categoryIDLabel.setText(rowIDCategory.getCategoryName());
		rowIDCombo.removeAll();
		for (IDType type : rowIDCategory.getPublicIdTypes())
			rowIDCombo.add(type.getTypeName());
		if (rowIDCombo.getItemCount() == 1) {
			rowIDCombo.select(0);
			rowIDCombo.setEnabled(false);
		}
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

	public void setIDType(IDType selection) {
		if (selection == null)
			return;
		rowIDCombo.setText(selection.getTypeName());
	}

	public IDType getIDType() {
		int i = rowIDCombo.getSelectionIndex();
		if (i < 0)
			return null;
		String type = rowIDCombo.getItem(i);
		return IDType.getIDType(type);
	}

	public void determineConfigFromPreview(List<? extends List<String>> dataMatrix, IDCategory rowIDCategory) {
		guessNumberOfHeaderRows(dataMatrix);
		determineRowIDType(dataMatrix, rowIDCategory);
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

	private void determineRowIDType(List<? extends List<String>> dataMatrix, IDCategory rowIDCategory) {
		List<String> idList = new ArrayList<String>();
		for (int i = 0; i < dataMatrix.size() && i < MAX_CONSIDERED_IDS_FOR_ID_TYPE_DETERMINATION; i++) {
			List<String> row = dataMatrix.get(i);
			idList.add(row.get(this.columnOfRowIDSpinner.getSelection()));
		}

		float maxProbability = 0;
		IDType mostProbableIDType = null;
		List<Pair<Float, IDType>> probabilityList = rowIDCategory
				.getListOfIDTypeAffiliationProbabilities(idList, false);
		if (probabilityList.size() > 0) {
			Pair<Float, IDType> pair = probabilityList.get(0);
			if (pair.getFirst() > maxProbability) {
				maxProbability = pair.getFirst();
				mostProbableIDType = pair.getSecond();
			}
		}

		if (maxProbability < 0.0001f)
			mostProbableIDType = null;

		if (mostProbableIDType != null)
			this.setIDType(mostProbableIDType);
	}

	/**
	 * creates a new {@link IDSpecification} based on the selected {@link IDType}
	 *
	 * @return
	 */
	public IDSpecification getIDSpecification() {
		IDSpecification rowIDSpecification = new IDSpecification();
		IDType rowIDType = this.getIDType();

		rowIDSpecification.setIdType(rowIDType.toString());
		if (rowIDType.getIDCategory().getCategoryName().equals("GENE"))
			rowIDSpecification.setIDTypeGene(true);
		rowIDSpecification.setIdCategory(rowIDType.getIDCategory().toString());
		if (rowIDType.getTypeName().equalsIgnoreCase("REFSEQ_MRNA")) {
			// for REFSEQ_MRNA we ignore the .1, etc.
			IDTypeParsingRules parsingRules = new IDTypeParsingRules();
			parsingRules.setSubStringExpression("\\.");
			parsingRules.setDefault(true);
			rowIDSpecification.setIdTypeParsingRules(parsingRules);
		}
		return rowIDSpecification;
	}

}
