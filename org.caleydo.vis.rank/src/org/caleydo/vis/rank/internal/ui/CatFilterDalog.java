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
package org.caleydo.vis.rank.internal.ui;


import java.awt.Color;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.internal.WorkbenchMessages;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public class CatFilterDalog<CATEGORY_TYPE> extends Dialog {
	private final Object receiver;

	private final String title;
	private final Map<CATEGORY_TYPE, ?> metaData;
	private final Set<CATEGORY_TYPE> selection;
	private final boolean filterGlobally;

	private Button filterGloballyUI;

	private CheckboxTreeViewer fViewer;


	public CatFilterDalog(Shell parentShell, String title, Object receiver,
 Map<CATEGORY_TYPE, ?> metaData,
			Set<CATEGORY_TYPE> selection,
			boolean filterGlobally) {
		super(parentShell);
		this.title = title;
		this.receiver = receiver;
		this.filterGlobally = filterGlobally;
		this.metaData = metaData;
		this.selection = new LinkedHashSet<>(selection);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText("Filter column: " + title);
	}

	@Override
	protected Composite createDialogArea(Composite parent) {
		// create composite
		Composite composite = (Composite) super.createDialogArea(parent);
		// create message
		Label label = new Label(composite, SWT.WRAP);
		label.setText("Selection Items to include:");
		GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		CheckboxTreeViewer treeViewer = createTreeViewer(composite);
		treeViewer.setCheckedElements(selection.toArray());
		Control buttonComposite = createSelectionButtons(composite);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(60);
		data.heightHint = convertHeightInCharsToPixels(18);
		Tree treeWidget = treeViewer.getTree();
		treeWidget.setLayoutData(data);
		treeWidget.setFont(parent.getFont());

		filterGloballyUI = new Button(composite, SWT.CHECK);
		filterGloballyUI.setText("Apply Filter Globally?");
		filterGloballyUI.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		filterGloballyUI.setSelection(filterGlobally);
		SelectionAdapter adapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerEvent();
			}
		};
		filterGloballyUI.addSelectionListener(adapter);

		applyDialogFont(composite);
		return composite;
	}

	protected void triggerEvent() {
		Object[] result = fViewer.getCheckedElements();
		Set<Object> r = new HashSet<>();
		for (int i = 0; i < result.length; i++) {
			r.add(result[i]);
		}
		EventPublisher.trigger(new FilterEvent(r, filterGloballyUI.getSelection()).to(receiver));
	}

	/**
	 * Creates the tree viewer.
	 *
	 * @param parent
	 *            the parent composite
	 * @return the tree viewer
	 */
	protected CheckboxTreeViewer createTreeViewer(final Composite parent) {
		fViewer = new CheckboxTreeViewer(parent, SWT.BORDER);
		fViewer.setContentProvider(new ArrayTreeContentProvider());
		org.eclipse.jface.viewers.ILabelProvider label = new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				@SuppressWarnings("unchecked")
				CATEGORY_TYPE k = (CATEGORY_TYPE) element;
				Object r = metaData.get(k);
				return Objects.toString(r, "");
			}

			@Override
			public org.eclipse.swt.graphics.Color getBackground(Object element) {
				@SuppressWarnings("unchecked")
				CATEGORY_TYPE k = (CATEGORY_TYPE) element;
				Object r = metaData.get(k);
				if (r instanceof CategoryInfo) {
					return toSWT(((CategoryInfo) r).getColor());
				}
				return null;
			}

			protected org.eclipse.swt.graphics.Color toSWT(Color color) {
				return new org.eclipse.swt.graphics.Color(parent.getDisplay(), color.getRed(), color.getGreen(),
						color.getBlue());
			}
		};
		fViewer.setLabelProvider(label);
		fViewer.setComparator(new ViewerComparator());
		fViewer.setInput(metaData.keySet());
		return fViewer;
	}

	/**
	 * Adds the selection and deselection buttons to the dialog.
	 *
	 * @param composite
	 *            the parent composite
	 * @return Composite the composite the buttons were created in.
	 */
	protected Composite createSelectionButtons(Composite composite) {
		Composite buttonComposite = new Composite(composite, SWT.RIGHT);
		GridLayout layout = new GridLayout();
		layout.numColumns = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		buttonComposite.setLayout(layout);
		buttonComposite.setFont(composite.getFont());
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		buttonComposite.setLayoutData(data);
		Button selectButton = createButton(buttonComposite, IDialogConstants.SELECT_ALL_ID,
				WorkbenchMessages.CheckedTreeSelectionDialog_select_all, false);
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Object[] viewerElements = ArrayContentProvider.getInstance().getElements(metaData.keySet());
				fViewer.setCheckedElements(viewerElements);
			}
		};
		selectButton.addSelectionListener(listener);
		Button deselectButton = createButton(buttonComposite, IDialogConstants.DESELECT_ALL_ID,
				WorkbenchMessages.CheckedTreeSelectionDialog_deselect_all, false);
		listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fViewer.setCheckedElements(new Object[0]);
			}
		};
		deselectButton.addSelectionListener(listener);
		return buttonComposite;
	}

	@Override
	protected void okPressed() {
		// real values
		triggerEvent();
		super.okPressed();
	}

	@Override
	protected void cancelPressed() {
		// original values
		EventPublisher.trigger(new FilterEvent(selection, filterGlobally).to(receiver));
		super.cancelPressed();
	}

	static class ArrayTreeContentProvider extends ArrayContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}

	}
}

