package org.caleydo.view.tourguide.internal.view.ui;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.view.tourguide.internal.event.EditDataDomainFilterEvent;
import org.caleydo.view.tourguide.internal.view.model.CategoricalDataDomainQuery;
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
	protected void createContextMenu(ContextMenuCreator creator) {
		creator.add("Edit Filter", new EditDataDomainFilterEvent().to(this));
		creator.addSeparator();
	}

	@SuppressWarnings("unchecked")
	@ListenTo(sendToMe = true)
	private void onEditDataDomainFilter(final EditDataDomainFilterEvent e) {
		if (e.isStartEditing()) {
			 Display.getDefault().asyncExec(new Runnable() {
				 @Override
				 public void run() {
					new CategoricalFilterDialog(new Shell()).open();
				 }
			 });
		} else {
			this.setSelection((Set<CategoryProperty<?>>) e.getPayload());
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
			EventPublisher.publishEvent(new EditDataDomainFilterEvent(r).to(CategoricalDataDomainElement.this));
			super.okPressed();
		}
	}
}