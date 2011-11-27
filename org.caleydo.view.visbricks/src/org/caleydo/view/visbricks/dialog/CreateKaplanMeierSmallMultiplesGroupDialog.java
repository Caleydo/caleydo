package org.caleydo.view.visbricks.dialog;

import java.util.ArrayList;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.DimensionPerspective;
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
public class CreateKaplanMeierSmallMultiplesGroupDialog extends TitleAreaDialog {

	private DataContainer dataContainer;
	private DimensionPerspective dimensionPerspective;

	private Composite parent;

	private ArrayList<DataContainer> kaplanMeierDimensionGroupDataList = new ArrayList<DataContainer>();

	private Table possibleKaplanMeierDataTable;

	public CreateKaplanMeierSmallMultiplesGroupDialog(Shell parentShell,
			DataContainer dataContainer, DimensionPerspective dimensionPerspective) {

		super(parentShell);
		this.dimensionPerspective = dimensionPerspective;
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
			if (!(tableBasedDataDomain.getRecordIDCategory() == dataContainer
					.getDataDomain().getRecordIDType().getIDCategory()))
				continue;

			TableItem item = new TableItem(possibleKaplanMeierDataTable, SWT.NONE);
			item.setText(0, tableBasedDataDomain.getLabel());
			item.setData(tableBasedDataDomain);
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
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) item.getData();

				RecordPerspective recordPerspective = dataContainer
						.getRecordPerspective();

				DataContainer kaplanMeierDimensionGroup = new DataContainer(
						dataDomain, recordPerspective, dataDomain.getTable().getDefaultDimensionPerspective());

				kaplanMeierDimensionGroupDataList.add(kaplanMeierDimensionGroup);
			}
		}

		super.okPressed();

	}

	public ArrayList<DataContainer> getKaplanMeierDimensionGroupDataList() {
		return kaplanMeierDimensionGroupDataList;
	}
}
