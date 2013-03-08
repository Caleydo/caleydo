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
package org.caleydo.view.stratomex.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
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
 * Dialog where the user can specify the data vectors to be visualized as a
 * Kaplan Meier plot. Each data vector will be a dimension group with small
 * multiples in StratomeX.
 *
 * @author Marc Streit
 *
 */
public class CreateKaplanMeierSmallMultiplesGroupDialog extends TitleAreaDialog {

	private TablePerspective tablePerspective;

	private Composite parent;

	private ArrayList<TablePerspective> kaplanMeierDimensionGroupDataList = new ArrayList<TablePerspective>();

	/**
	 * Hash between the converted record perspective to the original one from
	 * the dimension group on which the kaplan meier plot creation was
	 * triggered. This information is needed for being able to use the same
	 * sorting strategy.
	 */
	private HashMap<Perspective, Perspective> hashConvertedRecordPerspectiveToOrginalRecordPerspective = new HashMap<Perspective, Perspective>();

	private Table possibleKaplanMeierDataTable;

	public CreateKaplanMeierSmallMultiplesGroupDialog(Shell parentShell,
			TablePerspective tablePerspective) {
		super(parentShell);
		this.tablePerspective = tablePerspective;
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
		descriptionLabel
				.setText("Select the clinical variables you want to see in the Kaplan-Meier plots.");
		descriptionLabel.setLayoutData(data);

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		possibleKaplanMeierDataTable = new Table(parent, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);

		possibleKaplanMeierDataTable.setHeaderVisible(true);
		TableColumn column1 = new TableColumn(possibleKaplanMeierDataTable, SWT.CHECK);
		column1.setText("Data vector");

		possibleKaplanMeierDataTable.setLayoutData(data);
		possibleKaplanMeierDataTable.setSortColumn(column1);
		possibleKaplanMeierDataTable.setSortDirection(SWT.UP);
		possibleKaplanMeierDataTable.setEnabled(true);

		setTableContent();

		return parent;
	}

	private void setTableContent() {

		for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {

			if (!(dataDomain instanceof ATableBasedDataDomain))
				continue;

			ATableBasedDataDomain tableBasedDataDomain = (ATableBasedDataDomain) dataDomain;
			if (!(tableBasedDataDomain.getRecordIDCategory() == tablePerspective
					.getDataDomain().getRecordIDType().getIDCategory()))
				continue;

			// FIXME: This assumption is a very bad hack. We need a better way to find out if the DD is the clinical one
			if (tableBasedDataDomain.getTable().getDefaultDimensionPerspective()
					.getVirtualArray().size() > 10)
				continue;

			for (Integer dimID : tableBasedDataDomain.getTable()
					.getDefaultDimensionPerspective().getVirtualArray()) {
				String dimLabel = tableBasedDataDomain.getDimensionIDMappingManager()
						.getID(tableBasedDataDomain.getTable()
								.getDefaultDimensionPerspective().getIdType(),
								tableBasedDataDomain.getDimensionIDCategory().getHumanReadableIDType(),
								dimID);

				TableItem item = new TableItem(possibleKaplanMeierDataTable, SWT.NONE);
				item.setText(0, dimLabel);

				Perspective singleDimensionPerspective = new Perspective(
tableBasedDataDomain,
						tableBasedDataDomain.getDimensionIDType());
				singleDimensionPerspective.setDefault(false);
				PerspectiveInitializationData data = new PerspectiveInitializationData();
				ArrayList<Integer> dimIDList = new ArrayList<Integer>();
				dimIDList.add(dimID);
				data.setData(dimIDList);
				singleDimensionPerspective.init(data);
				singleDimensionPerspective.setLabel(dimLabel, false);

				tableBasedDataDomain.getTable().registerDimensionPerspective(
						singleDimensionPerspective);

				item.setData(singleDimensionPerspective);
				item.setData("dataDomain", tableBasedDataDomain);
			}
		}

		for (TableColumn column : possibleKaplanMeierDataTable.getColumns()) {
			column.pack();
		}

		possibleKaplanMeierDataTable.pack();
		parent.layout();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {

		for (TableItem item : possibleKaplanMeierDataTable.getItems()) {
			if (item.getChecked()) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) item
						.getData("dataDomain");

				Perspective foreignRecordPerspective = tablePerspective
						.getRecordPerspective();

				Perspective convertedRecordPerspective = dataDomain
						.convertForeignPerspective(foreignRecordPerspective);

				Perspective singleDimensionPerspective = (Perspective) item
						.getData();

				dataDomain.getTable().registerRecordPerspective(
						convertedRecordPerspective);

				TablePerspective kaplanMeierDimensionGroup = dataDomain.getTablePerspective(
						convertedRecordPerspective.getPerspectiveID(),
						singleDimensionPerspective.getPerspectiveID());

				kaplanMeierDimensionGroupDataList.add(kaplanMeierDimensionGroup);
				hashConvertedRecordPerspectiveToOrginalRecordPerspective.put(
						convertedRecordPerspective, foreignRecordPerspective);
			}
		}

		super.okPressed();

	}

	public ArrayList<TablePerspective> getKaplanMeierDimensionGroupDataList() {
		return kaplanMeierDimensionGroupDataList;
	}

	/**
	 * @return the hashConvertedRecordPerspectiveToOrginalRecordPerspective, see
	 *         {@link #hashConvertedRecordPerspectiveToOrginalRecordPerspective}
	 */
	public HashMap<Perspective, Perspective> getHashConvertedRecordPerspectiveToOrginalRecordPerspective() {
		return hashConvertedRecordPerspectiveToOrginalRecordPerspective;
	}
}
