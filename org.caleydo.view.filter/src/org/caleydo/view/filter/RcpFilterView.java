package org.caleydo.view.filter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.ContentMetaFilter;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.StorageFilter;
import org.caleydo.core.data.filter.StorageMetaFilter;
import org.caleydo.core.data.filter.event.FilterUpdatedEvent;
import org.caleydo.core.data.filter.event.RemoveContentFilterEvent;
import org.caleydo.core.data.filter.event.RemoveStorageFilterEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.event.IListenerOwner;
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
 * Filter view showing a pipeline of filters for a data set.
 * 
 * @author Marc Streit
 */
public class RcpFilterView extends CaleydoRCPViewPart implements IListenerOwner {

	public static final String VIEW_ID = "org.caleydo.view.filter";

	private ASetBasedDataDomain dataDomain;

	private Tree tree;

	private Menu contextMenu;

	private EventPublisher eventPublisher;

	private FilterUpdateListener filterUpdateListener;

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

		dataDomain = (ASetBasedDataDomain) DataDomainManager.get().getDataDomain(
				serializedView.getDataDomainType());

		parentComposite = parent;

		updateTree();
	}

	private void updateTree() {

		tree = new Tree(parentComposite, SWT.SINGLE | SWT.BORDER);
		TreeItem storageFilterTreeItem = new TreeItem(tree, SWT.NONE, 0);
		storageFilterTreeItem.setText("Experiment Filter");

		// Create the pop-up menu
		contextMenu = new Menu(parentComposite);
		contextMenu.addMenuListener(new MenuAdapter() {
			@Override
			public void menuShown(MenuEvent e) {
	
				MenuItem[] menuItems = contextMenu.getItems();
				for (int menuIndex = contextMenu.getItemCount()-1; menuIndex >= 0 ; menuIndex--) {
					contextMenu.getItem(menuIndex).dispose();
				}
				
				if (tree.getSelection()[0] != null && tree.getSelection()[0].getData() instanceof Filter) {
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

		for (StorageFilter filter : dataDomain.getStorageFilterManager().getFilterPipe()) {
			if (filter instanceof StorageMetaFilter) {

				TreeItem metaStorageFilterTreeItem = new TreeItem(storageFilterTreeItem,
						SWT.NONE, 0);
				metaStorageFilterTreeItem.setText(filter.getLabel());
				metaStorageFilterTreeItem.setData(filter);

				for (StorageFilter subFilter : ((StorageMetaFilter) filter)
						.getFilterList()) {
					child = new TreeItem(metaStorageFilterTreeItem, SWT.NONE, 0);
					child.setText(subFilter.getLabel());
					child.setData(subFilter);
				}
			} else {
				child = new TreeItem(storageFilterTreeItem, SWT.NONE, 0);
				child.setText(filter.getLabel());
				child.setData(filter);
			}
		}

		TreeItem contentFilterTreeItem = new TreeItem(tree, SWT.NONE, 0);
		contentFilterTreeItem.setText("Gene Filter");

		for (ContentFilter filter : dataDomain.getContentFilterManager().getFilterPipe()) {

			if (filter instanceof ContentMetaFilter) {

				TreeItem metaContentFilterTreeItem = new TreeItem(contentFilterTreeItem,
						SWT.NONE, 0);
				metaContentFilterTreeItem.setText(filter.getLabel());
				metaContentFilterTreeItem.setData(filter);

				for (ContentFilter subFilter : ((ContentMetaFilter) filter)
						.getFilterList()) {
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
		storageFilterTreeItem.setExpanded(true);
		for (int itemIndex = 0; itemIndex < contentFilterTreeItem.getItems().length; itemIndex++) {
			contentFilterTreeItem.getItem(itemIndex).setExpanded(true);
		}
		for (int itemIndex = 0; itemIndex < storageFilterTreeItem.getItems().length; itemIndex++) {
			storageFilterTreeItem.getItem(itemIndex).setExpanded(true);
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedFilterView();
		determineDataDomain(serializedView);
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

				if (selectedTreeItem.getData() instanceof StorageFilter) {
					RemoveStorageFilterEvent filterEvent = new RemoveStorageFilterEvent();
					filterEvent.setDataDomainType(dataDomain.getDataDomainType());
					filterEvent.setFilter((StorageFilter) selectedTreeItem.getData());
					selectedTreeItem.dispose();
					eventPublisher.triggerEvent(filterEvent);
				} else if (selectedTreeItem.getData() instanceof ContentFilter) {
					RemoveContentFilterEvent filterEvent = new RemoveContentFilterEvent();
					filterEvent.setDataDomainType(dataDomain.getDataDomainType());
					filterEvent.setFilter((ContentFilter) selectedTreeItem.getData());
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
}