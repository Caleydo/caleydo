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
import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
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
 * multiples in Visbricks.
 * 
 * @author Marc Streit
 * 
 */
public class CreateKaplanMeierSmallMultiplesGroupDialog
	extends TitleAreaDialog {

	private DataContainer dataContainer;

	private Composite parent;

	private ArrayList<DataContainer> kaplanMeierDimensionGroupDataList = new ArrayList<DataContainer>();

	/**
	 * Hash between the converted record perspective to the original one from
	 * the dimension group on which the kaplan meier plot creation was
	 * triggered. This information is needed for being able to use the same
	 * sorting strategy.
	 */
	private HashMap<RecordPerspective, RecordPerspective> hashConvertedRecordPerspectiveToOrginalRecordPerspective = new HashMap<RecordPerspective, RecordPerspective>();

	private Table possibleKaplanMeierDataTable;

	public CreateKaplanMeierSmallMultiplesGroupDialog(Shell parentShell,
			DataContainer dataContainer) {
		super(parentShell);
		this.dataContainer = dataContainer;
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
		descriptionLabel.setText("Select the pathways for the group.");
		descriptionLabel.setLayoutData(data);

		VirtualArray<?, ?, ?> va = null;
		// if (dataContainer.getDataDomain().isColumnDimension())
		va = dataContainer.getDataDomain().getTable().getDefaultDimensionPerspective()
				.getVirtualArray();

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		possibleKaplanMeierDataTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);

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
			if (!(tableBasedDataDomain.getRecordIDCategory() == dataContainer.getDataDomain()
					.getRecordIDType().getIDCategory()))
				continue;

			if (tableBasedDataDomain.getTable().getDefaultDimensionPerspective()
					.getVirtualArray().size() > 10)
				continue;

			for (Integer dimID : tableBasedDataDomain.getTable()
					.getDefaultDimensionPerspective().getVirtualArray()) {
				String dimLabel = tableBasedDataDomain.getDimensionIDMappingManager().getID(
						tableBasedDataDomain.getTable().getDefaultDimensionPerspective()
								.getIdType(),
						tableBasedDataDomain.getHumanReadableDimensionIDType(), dimID);

				TableItem item = new TableItem(possibleKaplanMeierDataTable, SWT.NONE);
				item.setText(0, dimLabel);

				DimensionPerspective singleDimensionPerspective = new DimensionPerspective(
						tableBasedDataDomain);
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

				RecordPerspective foreignRecordPerspective = dataContainer
						.getRecordPerspective();

				RecordPerspective convertedRecordPerspective = dataDomain
						.convertForeignRecordPerspective(foreignRecordPerspective);

				DimensionPerspective singleDimensionPerspective = (DimensionPerspective) item
						.getData();

				dataDomain.getTable().registerRecordPerspective(convertedRecordPerspective);

				DataContainer kaplanMeierDimensionGroup = dataDomain
						.getDataContainer(convertedRecordPerspective.getID(),
								singleDimensionPerspective.getID());

				kaplanMeierDimensionGroupDataList.add(kaplanMeierDimensionGroup);
				hashConvertedRecordPerspectiveToOrginalRecordPerspective.put(
						convertedRecordPerspective, foreignRecordPerspective);
			}
		}

		super.okPressed();

	}

	public ArrayList<DataContainer> getKaplanMeierDimensionGroupDataList() {
		return kaplanMeierDimensionGroupDataList;
	}

	/**
	 * @return the hashConvertedRecordPerspectiveToOrginalRecordPerspective, see
	 *         {@link #hashConvertedRecordPerspectiveToOrginalRecordPerspective}
	 */
	public HashMap<RecordPerspective, RecordPerspective> getHashConvertedRecordPerspectiveToOrginalRecordPerspective() {
		return hashConvertedRecordPerspectiveToOrginalRecordPerspective;
	}
}
