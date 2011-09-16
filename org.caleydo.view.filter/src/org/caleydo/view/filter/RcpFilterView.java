package org.caleydo.view.filter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.filter.DimensionFilter;
import org.caleydo.core.data.filter.DimensionMetaFilter;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.RecordFilter;
import org.caleydo.core.data.filter.RecordMetaFilter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.filter.event.RemoveDimensionFilterEvent;
import org.caleydo.core.data.filter.event.RemoveRecordFilterEvent;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.view.filter.listener.FilterUpdateListener;
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

	public static final String VIEW_TYPE = "org.caleydo.view.filter";

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
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedFilterView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}

		eventPublisher = GeneralManager.get().getEventPublisher();
		registerEventListeners();
	}

	@Override
	public void createPartControl(Composite parent) {

		dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(
				serializedView.getDataDomainID());
		// FIXME - that is probably null
		recordPerspectiveID = serializedView.getRecordPerspectiveID();
		dimensionPerspectiveID = serializedView.getDimensionPerspectiveID();

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
			public void handleEvent(Event e) {

				Object filter = tree.getSelection()[0].getData();
				if (!(filter instanceof Filter<?>))
					return;

				((Filter<?>) filter).openRepresentation();
			}
		});

		TreeItem child;

		for (DimensionFilter filter : dataDomain.getTable()
				.getDimensionPerspective(dimensionPerspectiveID).getFilterManager()
				.getFilterPipe()) {
			if (filter instanceof DimensionMetaFilter) {

				TreeItem metaDimensionFilterTreeItem = new TreeItem(
						dimensionFilterTreeItem, SWT.NONE, 0);
				metaDimensionFilterTreeItem.setText(filter.getLabel());
				metaDimensionFilterTreeItem.setData(filter);

				for (DimensionFilter subFilter : ((DimensionMetaFilter) filter)
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

		for (RecordFilter filter : dataDomain.getTable()
				.getRecordPerspective(recordPerspectiveID).getFilterManager()
				.getFilterPipe()) {

			if (filter instanceof RecordMetaFilter) {

				TreeItem metaContentFilterTreeItem = new TreeItem(contentFilterTreeItem,
						SWT.NONE, 0);
				metaContentFilterTreeItem.setText(filter.getLabel());
				metaContentFilterTreeItem.setData(filter);

				for (RecordFilter subFilter : ((RecordMetaFilter) filter).getFilterList()) {
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
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener,
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

				if (selectedTreeItem.getData() instanceof DimensionFilter) {
					RemoveDimensionFilterEvent filterEvent = new RemoveDimensionFilterEvent();
					filterEvent.setDataDomainID(dataDomain.getDataDomainID());
					filterEvent.setFilter((DimensionFilter) selectedTreeItem.getData());
					selectedTreeItem.dispose();
					eventPublisher.triggerEvent(filterEvent);
				} else if (selectedTreeItem.getData() instanceof RecordFilter) {
					RemoveRecordFilterEvent filterEvent = new RemoveRecordFilterEvent();
					filterEvent.setDataDomainID(dataDomain.getDataDomainID());
					filterEvent.setFilter((RecordFilter) selectedTreeItem.getData());
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
				if (!(filter instanceof Filter<?>))
					return;

				((Filter<?>) filter).openRepresentation();
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

	// @Override
	// public void setDataDomain(ATableBasedDataDomain dataDomain) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public ATableBasedDataDomain getDataDomain() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void initialize() {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public ASerializedView getSerializableRepresentation() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public void initFromSerializableRepresentation(ASerializedView
	// serializedView) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public String getViewType() {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int getID() {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public void setRecordPerspectiveID(String recordPerspectiveID) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void setDimensionPerspectiveID(String dimensionPerspectiveID) {
	// // TODO Auto-generated method stub
	//
	// }
}