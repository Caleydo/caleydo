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
package org.caleydo.view.info.selection.external;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Samuel Gratzl
 *
 */
public class ExternalFieldEditor extends FieldEditor {

	private final List<IDCategoryPattern> data = new ArrayList<>();
	private Composite top;
	private TableViewer viewer;

	public ExternalFieldEditor(String name, String labelText, Composite parent) {
		super(name, labelText, parent);
		for (IDCategory cat : IDCategory.getAllRegisteredIDCategories()) {
			if (cat.isInternaltCategory() || cat.getPublicIdTypes().isEmpty())
				continue;
			data.add(new IDCategoryPattern(cat));
		}
		Collections.sort(data);
		if (viewer != null)
			viewer.setInput(data);
	}

	@Override
	protected void adjustForNumColumns(int numColumns) {
		((GridData) top.getLayoutData()).horizontalSpan = numColumns;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Composite tableComposite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = numColumns;
		tableComposite.setLayoutData(gd);
		top = tableComposite;
		TableColumnLayout tableColumnLayout = new TableColumnLayout();
		tableComposite.setLayout(tableColumnLayout);
		this.viewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);

		TableViewerColumn idCategory = createTableColumn(viewer, "ID Category");
		idCategory.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				return row.category.getCategoryName();
			}
		});
		idCategory.getColumn().setToolTipText("Use {0} as placeholder");
		TableViewerColumn idType = createTableColumn(viewer, "Argument ID Type");
		idType.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				return row.idType == null ? "" : row.idType.getTypeName();
			}
		});
		idType.setEditingSupport(new EditingSupport(viewer) {
			@Override
			protected void setValue(Object element, Object value) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				if (((Integer) value).intValue() == -1)
					row.idType = null;
				else
					row.idType = IDType.getIDType(row.publicIDTypes[(Integer) value]);
			}

			@Override
			protected Object getValue(Object element) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				if (row.idType == null)
					return row.publicIDTypes.length > 1 ? -1 : 0;
				for (int i = 0; i < row.publicIDTypes.length; ++i)
					if (row.publicIDTypes[i].equals(row.idType.getTypeName()))
						return i;
				return -1;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				return new ComboBoxCellEditor(viewer.getTable(), row.getPublicIDTypes());
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});
		TableViewerColumn pattern = createTableColumn(viewer, "URL Pattern");
		pattern.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				return row.pattern == null ? "" : row.pattern;
			}
		});
		pattern.setEditingSupport(new EditingSupport(viewer) {
			@Override
			protected void setValue(Object element, Object value) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				row.pattern = value.toString();
			}

			@Override
			protected Object getValue(Object element) {
				IDCategoryPattern row = (IDCategoryPattern) element;
				return row.pattern == null ? "" : row.pattern;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(viewer.getTable());
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		tableColumnLayout.setColumnData(idCategory.getColumn(), new ColumnWeightData(15, 100, true));
		tableColumnLayout.setColumnData(idType.getColumn(), new ColumnWeightData(15, 100, true));
		tableColumnLayout.setColumnData(pattern.getColumn(), new ColumnWeightData(70, 100, true));

		viewer.setInput(data);
	}

	private TableViewerColumn createTableColumn(TableViewer viewer, String name) {
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn coll = col.getColumn();
		coll.setText(name);
		coll.setMoveable(true);
		coll.setResizable(true);
		coll.setWidth(100);
		return col;
	}

	@Override
	protected void doLoad() {
		read(false);
	}

	private void read(boolean defaultValue) {
		IPreferenceStore store = getPreferenceStore();
		for (IDCategoryPattern p : this.data) {
			Pair<String, IDType> pair = MyPreferences.getExternalIDCategory(store, p.category, defaultValue);
			if (pair == null) {
				p.idType = null;
				p.pattern = null;
			} else {
				p.idType = pair.getSecond();
				p.pattern = pair.getFirst();
			}
		}
		if (viewer != null)
			viewer.refresh();
	}

	@Override
	protected void doLoadDefault() {
		read(true);
	}

	@Override
	protected void doStore() {
		IPreferenceStore store = getPreferenceStore();
		for (IDCategoryPattern p : this.data) {
			String prefix = "external.idcategory." + p.category + ".";
			if (p.pattern == null || p.idType == null || p.pattern.isEmpty()) {
				store.setToDefault(prefix + "pattern");
				store.setToDefault(prefix + "idType");
			} else {
				store.setValue(prefix + "pattern", p.pattern);
				store.setValue(prefix + "idType", p.idType.getTypeName());
			}
		}
	}

	@Override
	public int getNumberOfControls() {
		return 1;
	}

	private static class IDCategoryPattern implements Comparable<IDCategoryPattern> {
		private final IDCategory category;
		private String pattern;
		private IDType idType;
		private final String[] publicIDTypes;

		public IDCategoryPattern(IDCategory category) {
			super();
			this.category = category;
			List<IDType> r = category.getPublicIdTypes();
			String[] a = new String[r.size()];
			for (int i = 0; i < a.length; ++i)
				a[i] = r.get(i).getTypeName();
			this.publicIDTypes = a;
		}

		/**
		 * @return
		 */
		public String[] getPublicIDTypes() {
			return publicIDTypes;
		}

		@Override
		public int compareTo(IDCategoryPattern o) {
			return category.getCategoryName().compareTo(o.category.getCategoryName());
		}

	}

}
