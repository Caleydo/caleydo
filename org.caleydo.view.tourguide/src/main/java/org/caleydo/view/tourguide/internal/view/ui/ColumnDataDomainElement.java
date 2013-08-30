/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.tourguide.internal.view.ui;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.model.InhomogenousDataDomainQuery;
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

public class ColumnDataDomainElement extends ADataDomainElement {

	public ColumnDataDomainElement(InhomogenousDataDomainQuery model) {
		super(model);
	}

	@Override
	public InhomogenousDataDomainQuery getModel() {
		return (InhomogenousDataDomainQuery) super.getModel();
	}

	@Override
	protected void onFilterEdit(boolean isStartEditing, Object payload) {
		if (isStartEditing) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					new ColumnDataFilterDialog(new Shell()).open();
				}
			});
		} else {
			@SuppressWarnings("unchecked")
			Set<EDataType> p = (Set<EDataType>) payload;
			this.setSelection(p);
		}
	}

	private void setSelection(Set<EDataType> selected) {
		getModel().setMatches(selected);
		setHasFilter(model.hasFilter());
	}

	private class ColumnDataFilterDialog extends Dialog {
		// the visual selection widget group
		private CheckboxTableViewer categoriesUI;

		public ColumnDataFilterDialog(Shell shell) {
			super(shell);
		}

		@Override
		public void create() {
			super.create();
			getShell().setText("Edit Filter of " + model.getDataDomain().getLabel());
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
			tableColumn.getColumn().setText("Data Type");
			tableColumn.getColumn().setWidth(200);
			tableColumn.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					EDataType p = (EDataType) element;
					return p.name();
				}
			});
			categoriesUI.setContentProvider(ArrayContentProvider.getInstance());
			categoriesUI.setInput(getModel().getDataClass().getSupportedDataTypes());
			for (Object s : getModel().getSelectedDataTypes()) {
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
			EventPublisher.trigger(new EditDataDomainFilterEvent(r).to(ColumnDataDomainElement.this));
			super.okPressed();
		}
	}
}
