package org.caleydo.view.visbricks.dialog;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayDimensionGroupData;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class CreatePathwayComparisonGroupDialog extends TitleAreaDialog {

	private IDataDomain sourceDataDomain;
	private IDataDomain pathwayDataDomain;
	private RecordVirtualArray sourceVA;

	private Map<String, List<PathwayGraph>> pathwayMap;

	private Table pathwayTable;

	private PathwayTableSorter pathwayTableSorter;
	private Composite parent;

	private PathwayDimensionGroupData pathwayDimensionGroupData;

	private class PathwayTableSorter implements Listener {

		@Override
		public void handleEvent(Event e) {
			sort(0, !(pathwayTable.getSortDirection() == SWT.UP));
		}

		public void sort(int columnIndex, boolean sortAscending) {
			TableItem[] items = pathwayTable.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());

			for (int i = 1; i < items.length; i++) {
				String value1 = items[i].getText(columnIndex);
				for (int j = 0; j < i; j++) {
					String value2 = items[j].getText(columnIndex);
					if ((collator.compare(value1, value2) < 0 && sortAscending)
							|| (collator.compare(value1, value2) > 0 && !sortAscending)) {
						String[] values = { items[i].getText(0),
								items[i].getText(1) };
						PathwayGraph pathway = (PathwayGraph) items[i]
								.getData();
						boolean checked = items[i].getChecked();
						items[i].dispose();
						TableItem item = new TableItem(pathwayTable, SWT.NONE,
								j);
						item.setText(values);
						item.setData(pathway);
						item.setChecked(checked);
						items = pathwayTable.getItems();
						break;
					}
				}
			}

			if (sortAscending) {
				pathwayTable.setSortDirection(SWT.UP);
			} else {
				pathwayTable.setSortDirection(SWT.DOWN);
			}
		}
	}

	public CreatePathwayComparisonGroupDialog(Shell parentShell) {
		super(parentShell);
		pathwayMap = new HashMap<String, List<PathwayGraph>>();
		pathwayTableSorter = new PathwayTableSorter();
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create Pathway Group");
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

		Collection<PathwayGraph> pathways = PathwayManager.get().getAllItems();

		for (PathwayGraph pathway : pathways) {

			List<PathwayGraph> dbPathways = pathwayMap.get(pathway.getType()
					.getName());
			if (dbPathways == null) {
				dbPathways = new ArrayList<PathwayGraph>();
			}
			dbPathways.add(pathway);
			pathwayMap.put(pathway.getType().getName(), dbPathways);
		}

		final Combo databaseCombo = new Combo(parent, SWT.DROP_DOWN);
		List<String> databaseNames = new ArrayList<String>(pathwayMap.keySet());
		if (!databaseNames.isEmpty()) {
			Collections.sort(databaseNames);
			for (String dbName : databaseNames) {
				databaseCombo.add(dbName);
			}
			databaseCombo.select(databaseCombo.getItemCount() - 1);
			databaseCombo.addSelectionListener(new SelectionListener() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					setTableContent(databaseCombo.getText());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);

				}
			});
		}

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;
		pathwayTable = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);

		pathwayTable.setHeaderVisible(true);
		TableColumn column1 = new TableColumn(pathwayTable, SWT.CHECK);
		column1.setText("Pathway");
		column1.addListener(SWT.Selection, pathwayTableSorter);
		TableColumn column2 = new TableColumn(pathwayTable, SWT.NONE);
		column2.setText("Database");
		pathwayTable.setLayoutData(data);
		pathwayTable.setSortColumn(column1);
		pathwayTable.setSortDirection(SWT.UP);
		pathwayTable.setEnabled(true);

		setTableContent(databaseCombo.getText());

		return parent;
	}

	private void setTableContent(String pathwayDatabase) {
		List<PathwayGraph> pathways = pathwayMap.get(pathwayDatabase);

		if (pathways == null)
			return;

		pathwayTable.removeAll();

		for (PathwayGraph pathway : pathways) {
			TableItem item = new TableItem(pathwayTable, SWT.NONE);
			item.setText(0, pathway.getTitle());
			item.setText(1, pathway.getType().getName());
			item.setData(pathway);
		}

		pathwayTableSorter.sort(0, true);

		for (TableColumn column : pathwayTable.getColumns()) {
			column.pack();
		}

		pathwayTable.pack();
		parent.layout();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		ArrayList<PathwayGraph> pathways = new ArrayList<PathwayGraph>();

		for (TableItem item : pathwayTable.getItems()) {
			if (item.getChecked()) {
				pathways.add((PathwayGraph) item.getData());
			}
		}

		// FIXME: DataDomainByType is not appropriate
		pathwayDataDomain = DataDomainManager.get().getDataDomainByType(
				"org.caleydo.datadomain.pathway");
		if (!pathways.isEmpty()) {
			pathwayDimensionGroupData = new PathwayDimensionGroupData(
					pathwayDataDomain,
					(ATableBasedDataDomain) sourceDataDomain, pathways,
					"PathwayGroup");
			super.okPressed();
		}
	}

	public PathwayDimensionGroupData getPathwayDimensionGroupData() {
		return pathwayDimensionGroupData;
	}

	public IDataDomain getSourceDataDomain() {
		return sourceDataDomain;
	}

	public void setSourceDataDomain(IDataDomain sourceDataDomain) {
		this.sourceDataDomain = sourceDataDomain;
	}

	public void setPathwayDataDomain(IDataDomain pathwayDataDomain) {
		this.pathwayDataDomain = pathwayDataDomain;
	}

	public IDataDomain getPathwayDataDomain() {
		return pathwayDataDomain;
	}

	public void setSourceVA(RecordVirtualArray sourceVA) {
		this.sourceVA = sourceVA;
	}

	public RecordVirtualArray getSourceVA() {
		return sourceVA;
	}

}
