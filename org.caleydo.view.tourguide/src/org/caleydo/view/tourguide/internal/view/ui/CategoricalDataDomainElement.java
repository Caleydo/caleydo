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
package org.caleydo.view.tourguide.internal.view.ui;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.model.CategoricalDataDomainQuery;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class CategoricalDataDomainElement extends ADataDomainElement {
	public CategoricalDataDomainElement(CategoricalDataDomainQuery dataDomain) {
		super(dataDomain);
	}

	@Override
	public CategoricalDataDomainQuery getModel() {
		return (CategoricalDataDomainQuery) super.getModel();
	}

	@Override
	protected void onFilterEdit(boolean isStartEditing, Object payload) {
		if (isStartEditing) {
			 Display.getDefault().asyncExec(new Runnable() {
				 @Override
				 public void run() {
					new CategoricalFilterDialog(new Shell()).open();
				 }
			 });
		} else {
			this.setSelection((Set<CategoryProperty<?>>) payload);
		}
	}

	private void setSelection(Set<CategoryProperty<?>> selected) {
		getModel().setSelection(selected);
		setHasFilter(model.hasFilter());
	}

	private class CategoricalFilterDialog extends Dialog {
		// the visual selection widget group
		private CheckboxTableViewer categoriesUI;

		public CategoricalFilterDialog(Shell shell) {
			super(shell);
		}

		@Override
		public void create() {
			super.create();
			getShell().setText("Edit filter of " + model.getDataDomain().getLabel());
			this.setBlockOnOpen(false);
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			parent = (Composite) super.createDialogArea(parent);

			this.categoriesUI = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION);
			categoriesUI.getTable().setLayoutData(
					new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			Table table = categoriesUI.getTable();
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			TableViewerColumn tableColumn = new TableViewerColumn(categoriesUI, SWT.LEAD);
			tableColumn.getColumn().setText("Category");
			tableColumn.getColumn().setWidth(200);
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					CategoryProperty<?> p = (CategoryProperty<?>) element;
					return p.getCategoryName();
				}
			});
			categoriesUI.setContentProvider(ArrayContentProvider.getInstance());
			categoriesUI.setInput(getModel().getCategories());
			for (Object s : getModel().getSelected()) {
				categoriesUI.setChecked(s, true);
			}
			applyDialogFont(parent);
			return parent;
		}

		@Override
		protected void okPressed() {
			Set<Object> r = new HashSet<>();
			for (Object score : categoriesUI.getCheckedElements()) {
				r.add(score);
			}
			EventPublisher.trigger(new EditDataDomainFilterEvent(r).to(CategoricalDataDomainElement.this));
			super.okPressed();
		}
	}
}