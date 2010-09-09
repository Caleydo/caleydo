package org.caleydo.view.filter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.StorageFilter;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Filter view showing a pipeline of filters for a data set.
 * 
 * @author Marc Streit
 */
public class RcpFilterView extends CaleydoRCPViewPart {

	public static final String VIEW_ID = "org.caleydo.view.filter";

	private ASetBasedDataDomain dataDomain;

	private Tree tree;

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

		TreeItem child;
		for (StorageFilter filter : dataDomain.getStorageFilterManager().getFilterPipe()) {
			child = new TreeItem(storageFilterTreeItem, SWT.NONE, 0);
			child.setText(filter.toString());
		}

		TreeItem contentFilterTreeItem  = new TreeItem(tree, SWT.NONE, 0);
		contentFilterTreeItem.setText("Gene Filter");

		for (ContentFilter filter : dataDomain.getContentFilterManager().getFilterPipe()) {
			child = new TreeItem(contentFilterTreeItem, SWT.NONE, 0);
			child.setText(filter.toString());
		}
		
	    // Create the pop-up menu
	    Menu menu = new Menu(parentComposite);
	    tree.setMenu(menu);

	    MenuItem removeItem = new MenuItem(menu, SWT.NONE);
	    removeItem.setText("Remove");
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
}