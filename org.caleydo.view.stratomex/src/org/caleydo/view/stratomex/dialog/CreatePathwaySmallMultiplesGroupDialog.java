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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.DimensionPerspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.collection.Pair.ComparablePair;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.PathwayDataDomain;
import org.caleydo.datadomain.pathway.data.PathwayTablePerspective;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Dialog where the user can specify the pathway that shall be displayed as a
 * dimension group with small multiples in StratomeX.
 *
 * @author Marc Streit
 *
 */
public class CreatePathwaySmallMultiplesGroupDialog
	extends TitleAreaDialog {

	private TablePerspective tablePerspective;
	private DimensionPerspective dimensionPerspective;

	private PathwayTableSorter pathwayTableSorter = new PathwayTableSorter();

	private CheckboxTableViewer viewer;

	private Table pathwayTable;

	private Composite parent;

	private ArrayList<PathwayTablePerspective> pathwayTablePerspective = new ArrayList<PathwayTablePerspective>();
	private HashMap<PathwayGraph, Integer> pathwayGraphsWithOccurrences;
	private PathwayComparator comparator;

	private class PathwayTableSorter
		implements Listener {

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
						String[] values = { items[i].getText(0), items[i].getText(1),
								items[i].getText(2) };
						PathwayGraph pathway = (PathwayGraph) items[i].getData();
						boolean checked = items[i].getChecked();
						items[i].dispose();
						TableItem item = new TableItem(pathwayTable, SWT.NONE, j);
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
			}
			else {
				pathwayTable.setSortDirection(SWT.DOWN);
			}
		}
	}

	public CreatePathwaySmallMultiplesGroupDialog(Shell parentShell,
			TablePerspective tablePerspective, DimensionPerspective dimensionPerspective) {

		super(parentShell);
		this.dimensionPerspective = dimensionPerspective;
		this.tablePerspective = tablePerspective;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Create Small Pathway Multiples Group");
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
		// if (tablePerspective.getDataDomain().isColumnDimension())
		va = tablePerspective.getDataDomain().getTable().getDefaultDimensionPerspective()
				.getVirtualArray();

		pathwayGraphsWithOccurrences = PathwayManager.get()
				.getPathwayGraphsWithOccurencesByGeneIDs(
						(GeneticDataDomain) tablePerspective.getDataDomain(), va.getIdType(),
						va.getIDs());

		// Create a list that contains pathways sorted by gene occurrences
		ArrayList<ComparablePair<Integer, PathwayGraph>> sortedPathwayList = new ArrayList<>();
		for (PathwayGraph pathway : pathwayGraphsWithOccurrences.keySet()) {
			sortedPathwayList.add(Pair.make(pathwayGraphsWithOccurrences
					.get(pathway), pathway));
		}
		Collections.sort(sortedPathwayList, Collections.reverseOrder());

		// final Combo databaseCombo = new Combo(parent, SWT.DROP_DOWN);
		// List<String> databaseNames = new
		// ArrayList<String>(pathwayMap.keySet());
		// if (!databaseNames.isEmpty()) {
		// Collections.sort(databaseNames);
		// for (String dbName : databaseNames) {
		// databaseCombo.add(dbName);
		// }
		// databaseCombo.select(databaseCombo.getItemCount() - 1);
		// databaseCombo.addSelectionListener(new SelectionListener() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// setTableContent(databaseCombo.getText());
		// }
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e) {
		// widgetSelected(e);
		//
		// }
		// });
		// }

		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.verticalAlignment = GridData.FILL;

		viewer = CheckboxTableViewer.newCheckList(parent, SWT.NONE);
		TableViewerColumn column1 = newColumn(viewer, "Pathway", 0);
		column1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ComparablePair<Integer, PathwayGraph> p = (ComparablePair<Integer, PathwayGraph>) element;
				return p.getSecond().getTitle();
			}
		});

		// Set the sorter for the table
		comparator = new PathwayComparator();
		viewer.setComparator(comparator);

		TableViewerColumn column2 = newColumn(viewer, "Database", 1);
		column2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ComparablePair<Integer, PathwayGraph> p = (ComparablePair<Integer, PathwayGraph>) element;
				return p.getSecond().getType().getName();
			}
		});

		TableViewerColumn column3 = newColumn(viewer, "Gene Occurences", 2);
		column3.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				ComparablePair<Integer, PathwayGraph> p = (ComparablePair<Integer, PathwayGraph>) element;
				return p.getFirst().toString();
			}
		});
		viewer.getTable().setLayoutData(data);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setSortDirection(comparator.getDirection());
		viewer.getTable().setSortColumn(column3.getColumn());
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setInput(sortedPathwayList);
		column1.getColumn().pack();
		column2.getColumn().pack();
		column3.getColumn().pack();

		return parent;
	}

	public class PathwayComparator extends ViewerComparator {
		private int propertyIndex;
		private static final int DESCENDING = 1;
		private int direction = DESCENDING;

		public PathwayComparator() {
			this.propertyIndex = 2;
			direction = DESCENDING;
		}

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int column) {
			if (column == this.propertyIndex) {
				// Same column as last sort; toggle the direction
				direction = 1 - direction;
			} else {
				// New column; do an ascending sort
				this.propertyIndex = column;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			ComparablePair<Integer, PathwayGraph> p1 = (ComparablePair<Integer, PathwayGraph>) e1;
			ComparablePair<Integer, PathwayGraph> p2 = (ComparablePair<Integer, PathwayGraph>) e2;
			int rc = 0;
			switch (propertyIndex) {
			case 0:
				rc = p1.getSecond().getTitle().compareTo(p2.getSecond().getTitle());
				break;
			case 1:
				rc = p1.getSecond().getType().getName().compareTo(p2.getSecond().getType().getName());
				break;
			case 2:
				rc = p1.getFirst().compareTo(p2.getFirst());
				break;
			default:
				rc = 0;
			}
			// If descending order, flip the direction
			if (direction == DESCENDING) {
				rc = -rc;
			}
			return rc;
		}

	}

	private TableViewerColumn newColumn(CheckboxTableViewer viewer, String label, int index) {
		TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
		column.getColumn().setText(label);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
		column.getColumn().addSelectionListener(getSelectionAdapter(column.getColumn(), index));
		return column;
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(index);
				int dir = comparator.getDirection();
				viewer.getTable().setSortDirection(dir);
				viewer.getTable().setSortColumn(column);
				viewer.refresh();
			}
		};
		return selectionAdapter;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void okPressed() {
		Object[] pathways = viewer.getCheckedElements();

		if (pathways.length > 0) {
			PathwayDataDomain pathwayDataDomain = (PathwayDataDomain) DataDomainManager.get().getDataDomainByType(
					PathwayDataDomain.DATA_DOMAIN_TYPE);

			for (Object obj : pathways) {
				ComparablePair<Integer, PathwayGraph> pathway = (ComparablePair<Integer, PathwayGraph>) obj;

				RecordPerspective oldRecordPerspective = tablePerspective.getRecordPerspective();
				RecordPerspective newRecordPerspective = new RecordPerspective(tablePerspective.getDataDomain());

				PerspectiveInitializationData data = new PerspectiveInitializationData();
				data.setData(oldRecordPerspective.getVirtualArray());

				newRecordPerspective.init(data);

				PathwayTablePerspective pathwayDimensionGroup = new PathwayTablePerspective(
						tablePerspective.getDataDomain(), pathwayDataDomain, newRecordPerspective,
						dimensionPerspective, pathway.getSecond());

				pathwayDataDomain.addTablePerspective(pathwayDimensionGroup);

				pathwayTablePerspective.add(pathwayDimensionGroup);
			}

			super.okPressed();
		}

	}

	/**
	 * @return the pathwayTablePerspective, see {@link #pathwayTablePerspective}
	 */
	public ArrayList<PathwayTablePerspective> getPathwayTablePerspective() {
		return pathwayTablePerspective;
	}
}
