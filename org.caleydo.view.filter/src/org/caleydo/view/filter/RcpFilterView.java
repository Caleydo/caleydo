package org.caleydo.view.filter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.StorageFilter;
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
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.caleydo.view.filter.listener.FilterUpdateListener;
import org.eclipse.swt.SWT;
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
		Menu menu = new Menu(parentComposite);
		tree.setMenu(menu);
		tree.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event e) {

				Object filter = tree.getSelection()[0].getData();
				if (!(filter instanceof Filter<?, ?>))
					return;

				((Filter<?, ?>) filter).createRepresentation();
			}
		});

		TreeItem child;

		for (StorageFilter filter : dataDomain.getStorageFilterManager().getFilterPipe()) {
			child = new TreeItem(storageFilterTreeItem, SWT.NONE, 0);
			child.setText(filter.getLabel());
			child.setData(filter);
		}

		TreeItem contentFilterTreeItem = new TreeItem(tree, SWT.NONE, 0);
		contentFilterTreeItem.setText("Gene Filter");
		contentFilterTreeItem.setExpanded(true);

		for (ContentFilter filter : dataDomain.getContentFilterManager().getFilterPipe()) {
			child = new TreeItem(contentFilterTreeItem, SWT.NONE, 0);
			child.setText(filter.getLabel());
			child.setData(filter);
		}

		MenuItem removeItem = new MenuItem(menu, SWT.NONE);
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
		
		MenuItem detailsItem = new MenuItem(menu, SWT.NONE);
		detailsItem.setText("Show details");
		detailsItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				Object filter = tree.getSelection()[0].getData();
				if (!(filter instanceof Filter<?, ?>))
					return;

				((Filter<?, ?>) filter).createRepresentation();
			}
		});
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