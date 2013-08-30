/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.filter;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.MetaFilter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.filter.event.RemoveFilterEvent;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.gui.toolbar.action.UseRandomSamplingAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.view.filter.listener.FilterUpdateListener;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * Filter view showing a pipeline of filters for a data table.
 *
 * @author Marc Streit
 */
public class RcpFilterView extends CaleydoRCPViewPart implements IListenerOwner {

	public static String VIEW_TYPE = "org.caleydo.view.filter";

	private ATableBasedDataDomain dataDomain;

	private Tree tree;

	private Menu contextMenu;

	private EventPublisher eventPublisher;

	private FilterUpdateListener filterUpdateListener;

	private String recordPerspectiveID;

	private String dimensionPerspectiveID;

	/**
	 * Constructor.
	 */
	public RcpFilterView() {
		super(SerializedFilterView.class);
		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public void createPartControl(Composite parent) {

		ASerializedSingleTablePerspectiveBasedView serializedSDView = ((ASerializedSingleTablePerspectiveBasedView) serializedView);
		dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(
				serializedSDView.getDataDomainID());
		// FIXME - that is probably null
		TablePerspective tablePerspective = dataDomain.getTablePerspective(serializedSDView
				.getTablePerspectiveKey());
		recordPerspectiveID = tablePerspective.getRecordPerspective().getPerspectiveID();
		dimensionPerspectiveID = tablePerspective.getDimensionPerspective()
				.getPerspectiveID();
		parentComposite = parent;

		updateTree();
	}

	private void updateTree() {

		tree = new Tree(parentComposite, SWT.SINGLE | SWT.BORDER);
		TreeItem dimensionFilterTreeItem = new TreeItem(tree, SWT.NONE, 0);
		dimensionFilterTreeItem.setText("Experiment Filter");

		// Create the pop-up menu
		contextMenu = new Menu(parentComposite);
		contextMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {

				MenuItem[] menuItems = contextMenu.getItems();
				for (int menuIndex = contextMenu.getItemCount() - 1; menuIndex >= 0; menuIndex--) {
					contextMenu.getItem(menuIndex).dispose();
				}

				if (tree.getSelection()[0] != null
						&& tree.getSelection()[0].getData() instanceof Filter) {
					addShowDetailsContextMenuItem();
					addRemoveContextMenuItem();
				}
			}
		});

		tree.setMenu(contextMenu);
		tree.addListener(SWT.MouseDoubleClick, new Listener() {
			@Override
			public void handleEvent(Event e) {

				Object filter = tree.getSelection()[0].getData();
				if (!(filter instanceof Filter))
					return;

				((Filter) filter).openRepresentation();
			}
		});

		TreeItem child;

		for (Filter filter : dataDomain.getTable()
				.getDimensionPerspective(dimensionPerspectiveID).getFilterManager()
				.getFilterPipe()) {
			if (filter instanceof MetaFilter) {

				TreeItem metaDimensionFilterTreeItem = new TreeItem(
						dimensionFilterTreeItem, SWT.NONE, 0);
				metaDimensionFilterTreeItem.setText(filter.getLabel());
				metaDimensionFilterTreeItem.setData(filter);

				for (Filter subFilter : ((MetaFilter) filter)
						.getFilterList()) {
					child = new TreeItem(metaDimensionFilterTreeItem, SWT.NONE, 0);
					child.setText(subFilter.getLabel());
					child.setData(subFilter);
				}
			} else {
				child = new TreeItem(dimensionFilterTreeItem, SWT.NONE, 0);
				child.setText(filter.getLabel());
				child.setData(filter);
			}
		}

		TreeItem contentFilterTreeItem = new TreeItem(tree, SWT.NONE, 0);
		contentFilterTreeItem.setText("Gene Filter");

		for (Filter filter : dataDomain.getTable()
				.getRecordPerspective(recordPerspectiveID).getFilterManager()
				.getFilterPipe()) {

			if (filter instanceof MetaFilter) {

				TreeItem metaContentFilterTreeItem = new TreeItem(contentFilterTreeItem,
						SWT.NONE, 0);
				metaContentFilterTreeItem.setText(filter.getLabel());
				metaContentFilterTreeItem.setData(filter);

				for (Filter subFilter : ((MetaFilter) filter).getFilterList()) {
					child = new TreeItem(metaContentFilterTreeItem, SWT.NONE, 0);
					child.setText(subFilter.getLabel());
					child.setData(subFilter);
				}
			} else {
				child = new TreeItem(contentFilterTreeItem, SWT.NONE, 0);
				child.setText(filter.getLabel());
				child.setData(filter);
			}
		}

		contentFilterTreeItem.setExpanded(true);
		dimensionFilterTreeItem.setExpanded(true);
		for (int itemIndex = 0; itemIndex < contentFilterTreeItem.getItems().length; itemIndex++) {
			contentFilterTreeItem.getItem(itemIndex).setExpanded(true);
		}
		for (int itemIndex = 0; itemIndex < dimensionFilterTreeItem.getItems().length; itemIndex++) {
			dimensionFilterTreeItem.getItem(itemIndex).setExpanded(true);
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedFilterView();
		determineDataConfiguration(serializedView);
	}

	@Override
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
			final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	private void addRemoveContextMenuItem() {
		MenuItem removeItem = new MenuItem(contextMenu, SWT.NONE);
		removeItem.setText("Remove");
		removeItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem selectedTreeItem = tree.getSelection()[0];

				if (selectedTreeItem.getData() instanceof Filter) {
					RemoveFilterEvent filterEvent = new RemoveFilterEvent();
					filterEvent.setEventSpace(dataDomain.getDataDomainID());
					filterEvent.setFilter((Filter) selectedTreeItem.getData());
					selectedTreeItem.dispose();
					eventPublisher.triggerEvent(filterEvent);
				}
			}
		});

	}

	private void addShowDetailsContextMenuItem() {
		MenuItem detailsItem = new MenuItem(contextMenu, SWT.NONE);
		detailsItem.setText("Show details");
		detailsItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Object filter = tree.getSelection()[0].getData();
				if (!(filter instanceof Filter))
					return;

				((Filter) filter).openRepresentation();
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		unregisterEventListeners();
	}

	@Override
	public void registerEventListeners() {
		filterUpdateListener = new FilterUpdateListener();
		filterUpdateListener.setHandler(this);
		eventPublisher.addListener(FilterUpdatedEvent.class, filterUpdateListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (filterUpdateListener != null) {
			eventPublisher.removeListener(filterUpdateListener);
			filterUpdateListener = null;
		}
	}

	public void handleFilterUpdatedEvent() {
		tree.dispose();
		updateTree();
		parentComposite.layout();
	}

	@Override
	public void addToolBarContent(IToolBarManager toolBarManager) {
		toolBarManager.add(new UseRandomSamplingAction());
	}
}
