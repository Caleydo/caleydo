/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.io.MetaDataElement;
import org.caleydo.core.io.MetaDataElement.AttributeType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.view.info.dataset.spi.IDataDomainDataSetItem;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.TreeItem;

/**
 * meta data about the data domain, e.g. how it was generated
 * 
 * @author Samuel Gratzl
 * 
 */
public class MetaDataItem implements IDataDomainDataSetItem {
	private ExpandItem metaDataItem;
	private TreeViewer metaDataTree;

	@Override
	public ExpandItem create(ExpandBar expandBar) {
		this.metaDataItem = new ExpandItem(expandBar, SWT.WRAP);
		metaDataItem.setText("Meta Data");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(1, false));

		metaDataTree = new TreeViewer(c, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		metaDataTree.setContentProvider(new MetaDataContentProvider());
		metaDataTree.setLabelProvider(new MetaDataLabelProvider());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 150;
		metaDataTree.getTree().setLayoutData(gridData);
		ColumnViewerToolTipSupport.enableFor(metaDataTree, ToolTip.NO_RECREATE);
		metaDataTree.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				TreeItem item = (TreeItem) e.item;
				Object element = item.getData();
				if (!(element instanceof MetaDataElement)) {
					@SuppressWarnings("unchecked")
					Entry<String, Pair<String, AttributeType>> attribute = (Entry<String, Pair<String, AttributeType>>) element;
					if (attribute.getValue().getSecond() == AttributeType.URL) {
						BrowserUtils.openURL(attribute.getValue().getFirst());
					}
				}
			}
		});
		// metaDataInfo = new StyledText(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		// metaDataInfo.setBackgroundMode(SWT.INHERIT_FORCE);
		// metaDataInfo.setText("No meta data");
		// GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		// gd.heightHint = 160;
		// metaDataInfo.setLayoutData(gd);
		// metaDataInfo.setEditable(false);
		// metaDataInfo.setWordWrap(true);

		// transformationLabel.set
		// transformationLabel.();

		metaDataItem.setControl(c);
		metaDataItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);

		return metaDataItem;
	}

	@Override
	public void update(IDataDomain dataDomain) {
		MetaDataElement metaData = dataDomain.getDataSetDescription().getMetaData();
		if (metaData != null) {
			// String text = new PlainTextFormatter().format(metaData);
			metaDataTree.setContentProvider(new MetaDataContentProvider(metaData));
			metaDataTree.setInput(metaData);
			// metaDataInfo.setText(text);
		}
	}

	private class MetaDataContentProvider implements ITreeContentProvider {

		private MetaDataElement root;

		/**
		 *
		 */
		public MetaDataContentProvider() {
			// TODO Auto-generated constructor stub
		}

		public MetaDataContentProvider(MetaDataElement root) {
			this.root = root;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof MetaDataElement) {
				return getChildren((MetaDataElement) inputElement);
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof MetaDataElement) {
				return getChildren((MetaDataElement) parentElement);
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getParent(Object element) {
			if (element instanceof MetaDataElement) {
				if (element == root)
					return null;
				return getParent((MetaDataElement) element, root);
			} else {

				return getParent((Entry<String, Pair<String, AttributeType>>) element, root);
			}
		}

		private MetaDataElement getParent(MetaDataElement element, MetaDataElement parent) {
			List<MetaDataElement> elements = parent.getElements();
			if (elements != null && !elements.isEmpty()) {
				if (elements.contains(element))
					return parent;
				for (MetaDataElement child : elements) {
					MetaDataElement p = getParent(element, child);
					if (p != null)
						return p;
				}
			}
			return null;
		}

		private MetaDataElement getParent(Entry<String, Pair<String, AttributeType>> attribute, MetaDataElement parent) {
			Map<String, Pair<String, AttributeType>> attributes = parent.getAttributes();
			if (attributes != null && !attributes.isEmpty()) {
				if (attributes.entrySet().contains(attribute))
					return parent;
				List<MetaDataElement> elements = parent.getElements();
				if (elements != null && !elements.isEmpty()) {
					for (MetaDataElement child : elements) {
						MetaDataElement p = getParent(attribute, child);
						if (p != null)
							return p;
					}
				}
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof MetaDataElement) {
				MetaDataElement e = (MetaDataElement) element;
				Map<String, Pair<String, AttributeType>> attributes = e.getAttributes();
				if (attributes != null && !attributes.isEmpty()) {
					return true;
				}
				List<MetaDataElement> elements = e.getElements();
				if (elements != null && !elements.isEmpty()) {
					return true;
				}
			}
			return false;
		}

		private Object[] getChildren(MetaDataElement element) {
			Map<String, Pair<String, AttributeType>> attributes = element.getAttributes();
			List<Object> children = new ArrayList<>();
			if (attributes != null && !attributes.isEmpty()) {
				children.addAll(attributes.entrySet());
			}
			List<MetaDataElement> elements = element.getElements();
			if (elements != null && !elements.isEmpty()) {
				children.addAll(elements);
			}
			return children.toArray();
		}

		/**
		 * @param root
		 *            setter, see {@link root}
		 */
		public void setRoot(MetaDataElement root) {
			this.root = root;
		}

	}

	private class MetaDataLabelProvider extends StyledCellLabelProvider {

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();

			if (element instanceof MetaDataElement) {
				MetaDataElement e = (MetaDataElement) element;
				String name = e.getName();
				if (name != null)
					text.append(name);
			} else {
				@SuppressWarnings("unchecked")
				Entry<String, Pair<String, AttributeType>> attribute = (Entry<String, Pair<String, AttributeType>>) element;
				if (attribute.getValue().getSecond() == AttributeType.URL) {
					text.append(attribute.getKey() + ": ");
					String url = attribute.getValue().getFirst();
					if (url.length() > 17) {
						int index = url.lastIndexOf("/") + 1;
						url = url.substring(index, url.length());
					}
					text.append(url, StyledString.COUNTER_STYLER);
				} else {
					text.append(attribute.getKey() + ": " + attribute.getValue().getFirst());
				}
			}
			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());
			super.update(cell);
		}

		@Override
		public String getToolTipText(Object element) {
			if (!(element instanceof MetaDataElement)) {
				@SuppressWarnings("unchecked")
				Entry<String, Pair<String, AttributeType>> attribute = (Entry<String, Pair<String, AttributeType>>) element;
				if (attribute.getValue().getSecond() == AttributeType.URL) {
					return attribute.getValue().getFirst();
				}
			}
			return super.getToolTipText(element);
		}

	}
}
