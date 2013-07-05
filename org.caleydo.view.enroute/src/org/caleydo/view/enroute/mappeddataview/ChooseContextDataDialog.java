/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Dialog where the user can specify the ids out of a perspective
 *
 * @author Alexander Lex
 * @author Marc Streit
 *
 */
public class ChooseContextDataDialog extends TitleAreaDialog {

	private Perspective perspective;
	private IDType contextIDType;
	private List<Integer> selectedItems = new ArrayList<>();

	private Composite parent;

	private Table candidateCompoundsTable;

	public ChooseContextDataDialog(Shell parentShell, Perspective perspective) {
		super(parentShell);
		this.perspective = perspective;
		this.contextIDType = perspective.getIdType();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create Kaplan Meier Small Multiples Group");
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		this.parent = parent;

		parent.setLayout(new GridLayout());

		GridData data = new GridData();
		GridLayout layout = new GridLayout(1, true);

		parent.setLayout(layout);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		Label descriptionLabel = new Label(parent, SWT.NONE);
		descriptionLabel.setText("Select compound to show.");
		descriptionLabel.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		candidateCompoundsTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		candidateCompoundsTable.setHeaderVisible(true);
		TableColumn column1 = new TableColumn(candidateCompoundsTable, SWT.CHECK);
		column1.setText("Data vector");

		candidateCompoundsTable.setLayoutData(data);
		candidateCompoundsTable.setSortColumn(column1);
		candidateCompoundsTable.setSortDirection(SWT.UP);
		candidateCompoundsTable.setEnabled(true);

		setTableContent();

		return parent;
	}

	private void setTableContent() {

		for (Integer id : perspective.getVirtualArray()) {
			String label = IDMappingManagerRegistry.get().getIDMappingManager(contextIDType)
					.getID(contextIDType, contextIDType.getIDCategory().getHumanReadableIDType(), id);

			TableItem item = new TableItem(candidateCompoundsTable, SWT.NONE);
			item.setText(0, label);

			item.setData(id);

		}

		for (TableColumn column : candidateCompoundsTable.getColumns()) {
			column.pack();
		}

		candidateCompoundsTable.pack();
		parent.layout();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {

		for (TableItem item : candidateCompoundsTable.getItems()) {
			if (item.getChecked()) {
				Integer id = (Integer) item.getData();
				if (id != null)
					selectedItems.add(id);

			}
		}

		super.okPressed();

	}

	/**
	 * @return the selectedItems, see {@link #selectedItems}
	 */
	public List<Integer> getSelectedItems() {
		return selectedItems;
	}
}
