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


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.vis.rank.internal.event.FilterEvent;
import org.caleydo.vis.rank.model.CategoricalRankRankColumnModel.CategoryInfo;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

/**
 *
 *
 * @author Samuel Gratzl
 *
 */
public class CatFilterDalog<CATEGORY_TYPE> extends AFilterDialog {
	private final Map<CATEGORY_TYPE, ?> metaData;
	private final Set<CATEGORY_TYPE> selection;

	private CheckboxTreeViewer fViewer;

	public CatFilterDalog(Shell parentShell, String title, Object receiver, Map<CATEGORY_TYPE, ?> metaData,
			Set<CATEGORY_TYPE> selection, boolean filterGlobally, boolean hasSnapshots, Point loc) {
		super(parentShell, "Filter " + title, receiver, filterGlobally, hasSnapshots, loc);
		this.metaData = metaData;
		this.selection = new LinkedHashSet<>(selection);
	}

	@Override
	protected void createSpecificFilterUI(Composite composite) {
		// create message
		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false);
		data.horizontalSpan = 2;
		buttonComposite.setLayoutData(data);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		buttonComposite.setLayout(layout);
		{
			Label label = new Label(buttonComposite, SWT.WRAP);
			label.setText("Selection items to include:");
			data = new GridData(SWT.LEFT, SWT.CENTER, true, false);
			data.horizontalSpan = 1;
			label.setLayoutData(data);
			label.setFont(composite.getFont());

			Button selectButton = new Button(buttonComposite, SWT.PUSH);
			selectButton.setText("Select &all");
			SelectionListener listener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Object[] viewerElements = ArrayContentProvider.getInstance().getElements(metaData.keySet());
					fViewer.setCheckedElements(viewerElements);
				}
			};
			selectButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
			selectButton.addSelectionListener(listener);
			Button deselectButton = new Button(buttonComposite, SWT.PUSH);
			deselectButton.setText("Select &aone");
			listener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					fViewer.setCheckedElements(new Object[0]);
				}
			};
			deselectButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
			deselectButton.addSelectionListener(listener);
		}
		buttonComposite.pack();

		CheckboxTreeViewer treeViewer = createTreeViewer(composite);
		treeViewer.setCheckedElements(selection.toArray());
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = getCharWith(composite, 60);
		data.heightHint = getCharHeight(composite) * 10;
		data.horizontalSpan = 2;
		Tree treeWidget = treeViewer.getTree();
		treeWidget.setLayoutData(data);
		treeWidget.setFont(composite.getFont());

		createApplyGlobally(composite);
		addOKButton(composite, true);
	}

	@Override
	protected void triggerEvent(boolean cancel) {
		if (cancel) {
			EventPublisher.trigger(new FilterEvent(selection, filterGlobally).to(receiver));
			return;
		}
		Object[] result = fViewer.getCheckedElements();
		Set<Object> r = new HashSet<>();
		for (int i = 0; i < result.length; i++) {
			r.add(result[i]);
		}
		EventPublisher.trigger(new FilterEvent(r, isFilterGlobally()).to(receiver));
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
					return ((CategoryInfo) r).getColor().getSWTColor(parent.getDisplay());
				}
				return null;
			}

			// protected org.eclipse.swt.graphics.Color toSWT(Color color) {
			// return new org.eclipse.swt.graphics.Color(parent.getDisplay(), color.getRed(), color.getGreen(),
			// color.getBlue());
			// }
		};
		fViewer.setLabelProvider(label);
		fViewer.setComparator(new ViewerComparator());
		fViewer.setInput(metaData.keySet());
		return fViewer;
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

