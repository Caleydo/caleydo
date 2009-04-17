package org.caleydo.rcp.views.swt;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.event.view.bucket.LoadPathwaysByGeneEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.views.opengl.GLHeatMapView;
import org.caleydo.rcp.views.opengl.GLRemoteRenderingView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * Search view contains gene and pathway search.
 * 
 * @author Marc Streit
 */
public class SearchView
	extends ViewPart
	implements IMediatorReceiver {
	public static final String ID = "org.caleydo.rcp.views.swt.SearchView";

	public static boolean bHorizontal = false;

	private Text searchText;

	private Table pathwayTable;
	private Table geneTable;

	private IGeneralManager generalManager;

	private Button usePathways;
	private Button useGeneSymbol;
	private Button useGeneName;
	private Button useGeneRefSeqID;
	private Button useGeneEntrezGeneID;
	private Button useGeneDavidID;

	private IIDMappingManager idMappingManager;

	private SearchViewMediator searchViewMediator;

	public SearchView() {
		searchViewMediator = new SearchViewMediator();
	}

	@Override
	public void createPartControl(Composite parent) {

		generalManager = GeneralManager.get();
		generalManager.getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR, this);

		idMappingManager = generalManager.getIDMappingManager();

		final Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		Label entitySearchLabel = new Label(composite, SWT.NULL);
		entitySearchLabel.setText("Search query:");

		searchText = new Text(composite, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchText.setText("");
			}
		});

		Group searchDataKindGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		// searchDataKindGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		searchDataKindGroup.setLayout(new RowLayout());

		Button searchButton = new Button(composite, SWT.PUSH);
		searchButton.setText("Search");

		usePathways = new Button(searchDataKindGroup, SWT.CHECK);
		usePathways.setText("Pathways");
		if (generalManager.getPathwayManager().size() == 0)
			usePathways.setEnabled(false);
		else
			usePathways.setSelection(true);

		useGeneSymbol = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneSymbol.setText("Gene symbol");
		if (generalManager.getIDMappingManager().hasMapping(EMappingType.GENE_SYMBOL_2_DAVID))
			useGeneSymbol.setSelection(true);
		else
			useGeneSymbol.setEnabled(false);

		useGeneName = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneName.setText("Gene name (full)");
		if (idMappingManager.hasMapping(EMappingType.GENE_NAME_2_DAVID))
			useGeneName.setSelection(true);
		else
			useGeneName.setEnabled(false);

		useGeneRefSeqID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneRefSeqID.setText("RefSeq ID");
		if (generalManager.getIDMappingManager().hasMapping(EMappingType.REFSEQ_MRNA_2_DAVID))
			useGeneRefSeqID.setSelection(true);
		else
			useGeneRefSeqID.setEnabled(false);

		useGeneEntrezGeneID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneEntrezGeneID.setText("Entrez Gene ID");
		if (generalManager.getIDMappingManager().hasMapping(EMappingType.ENTREZ_GENE_ID_2_DAVID))
			useGeneEntrezGeneID.setSelection(true);
		else
			useGeneEntrezGeneID.setEnabled(false);

		useGeneDavidID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneDavidID.setText("David ID");
		if (generalManager.getIDMappingManager().hasMapping(EMappingType.DAVID_2_REFSEQ_MRNA_INT))
			useGeneDavidID.setSelection(true);
		else
			useGeneDavidID.setEnabled(false);

		searchDataKindGroup.pack();

		Label resultLabel = new Label(composite, SWT.NULL);
		resultLabel.setText("Results:");

		geneTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		geneTable.setLinesVisible(true);
		geneTable.setHeaderVisible(true);
		geneTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		addGeneContextMenu();
		
		String[] titles = { "RefSeq ID", "David ID", "Entrez Gene ID", "Gene Symbol", "Gene Name" };
		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(geneTable, SWT.NONE);
			column.setText(titles[i]);
		}

		pathwayTable = new Table(composite, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		pathwayTable.setLinesVisible(true);
		pathwayTable.setHeaderVisible(true);
		pathwayTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		addPathwayContextMenu();

		TableColumn pathwayDatabaseColumn = new TableColumn(pathwayTable, SWT.NONE);
		pathwayDatabaseColumn.setText("Database");
		TableColumn pathwayNameColumn = new TableColumn(pathwayTable, SWT.NONE);
		pathwayNameColumn.setText("Pathway Name");

		// TreeItem experimentTree = new TreeItem(selectionTree, SWT.NONE);
		// experimentTree.setText("Experiments");
		// experimentTree.setExpanded(true);
		// experimentTree.setData(-1);

		searchButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				startSearch();
			}
		});

		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
					case SWT.CR: {
						startSearch();
					}
				}
			}
		});

		composite.layout();
	}

	private void startSearch() {

		searchForPathway(searchText.getText());
		searchForGene(searchText.getText());
	}

	private void searchForPathway(String sSearchQuery) {

		if (!usePathways.getSelection())
			return;

		// Flush old pathway results
		for (TableItem item : pathwayTable.getItems())
			item.dispose();

		Pattern pattern = Pattern.compile(sSearchQuery, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;

		// Search for pathways
		for (PathwayGraph pathway : generalManager.getPathwayManager().getAllItems()) {
			regexMatcher = pattern.matcher(pathway.getTitle());
			if (regexMatcher.find()) {
				TableItem item = new TableItem(pathwayTable, SWT.NULL);
				item.setText(0, pathway.getType().getName());
				item.setText(1, pathway.getTitle());
				item.setData(pathway);
			}
		}

		pathwayTable.getColumn(0).pack();
		pathwayTable.getColumn(1).pack();
	}

	private void searchForGene(final String sSearchQuery) {

		// Flush old pathway results
		for (TableItem item : geneTable.getItems())
			item.dispose();

		Pattern pattern = Pattern.compile(sSearchQuery, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		ArrayList<Integer> iArDavidGeneResults = new ArrayList<Integer>();

		if (useGeneSymbol.getSelection()) {
			for (Object sGeneSymbol : idMappingManager.getMapping(EMappingType.GENE_SYMBOL_2_DAVID).keySet()) {
				regexMatcher = pattern.matcher((String) sGeneSymbol);
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) idMappingManager.getID(
						EMappingType.GENE_SYMBOL_2_DAVID, sGeneSymbol));
			}
		}

		if (useGeneEntrezGeneID.getSelection()) {
			for (Object iEntrezGeneID : idMappingManager.getMapping(EMappingType.ENTREZ_GENE_ID_2_DAVID)
				.keySet()) {
				regexMatcher = pattern.matcher(iEntrezGeneID.toString());
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) idMappingManager.getID(
						EMappingType.ENTREZ_GENE_ID_2_DAVID, iEntrezGeneID));
			}
		}

		if (useGeneRefSeqID.getSelection()) {
			for (Object sGeneSymbol : idMappingManager.getMapping(EMappingType.REFSEQ_MRNA_2_DAVID).keySet()) {
				regexMatcher = pattern.matcher((String) sGeneSymbol);
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) idMappingManager.getID(
						EMappingType.REFSEQ_MRNA_2_DAVID, sGeneSymbol));
			}
		}

		if (useGeneName.getSelection()) {
			for (Object sGeneName : idMappingManager.getMapping(EMappingType.GENE_NAME_2_DAVID).keySet()) {
				regexMatcher = pattern.matcher((String) sGeneName);
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) generalManager.getIDMappingManager().getID(
						EMappingType.GENE_NAME_2_DAVID, sGeneName));
			}
		}

		// Fill results in table
		for (Integer iDavidID : iArDavidGeneResults) {
			String sRefSeqIDs = "";

			try {
				for (Object iRefSeqID : idMappingManager.getMultiID(EMappingType.DAVID_2_REFSEQ_MRNA_INT,
					iDavidID)) {
					sRefSeqIDs +=
						idMappingManager.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA, iRefSeqID) + " ";
				}
			}
			catch (NullPointerException npe) {
				sRefSeqIDs = "<No Mapping>";
			}

			String sEntrezGeneID = "";
			Integer iEntrezGeneID = idMappingManager.getID(EMappingType.DAVID_2_ENTREZ_GENE_ID, iDavidID);
			if (iEntrezGeneID == null)
				sEntrezGeneID = "<No Mapping>";
			else
				sEntrezGeneID = iEntrezGeneID.toString();

			String sGeneSymbol = idMappingManager.getID(EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);
			if (sGeneSymbol == null)
				sGeneSymbol = "<Unknown>";

			String sGeneName = idMappingManager.getID(EMappingType.DAVID_2_GENE_NAME, iDavidID);
			if (sGeneName == null)
				sGeneName = "<Unknown>";

			TableItem item = new TableItem(geneTable, SWT.NULL);
			item.setText(0, sRefSeqIDs);
			item.setText(1, Integer.toString(iDavidID));
			item.setText(2, sEntrezGeneID);
			item.setText(3, sGeneSymbol);
			item.setText(4, sGeneName);

			item.setData(iDavidID);
			
			// Highlight content if it matches the search query
			for (int iIndex = 0; iIndex < geneTable.getColumnCount(); iIndex++) {
				regexMatcher = pattern.matcher(item.getText(iIndex));
				if (regexMatcher.find()) {
					item.setBackground(iIndex, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				}
			}
		}

		for (TableColumn column : geneTable.getColumns())
			column.pack();

		geneTable.getColumn(0).setWidth(300);
		geneTable.getColumn(4).setWidth(200);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR, this);
	}

	// TODO: remove after commit of Werner
	public void updateSearchBar(boolean bIsVisible) {
		// pathwaySearchBox.setVisible(bIsVisible);
		// pathwaySearchLabel.setVisible(bIsVisible);
		// parentComposite.layout();
	}

	@Override
	public void handleExternalEvent(final IMediatorSender eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {

	}

	private void addGeneContextMenu() {

		final Menu menu = new Menu(geneTable.getShell(), SWT.POP_UP);
		geneTable.setMenu(menu);

		menu.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event) {
				MenuItem[] menuItems = menu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}

				for (final TableItem tableItem : geneTable.getSelection()) {

					MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
					openInBrowserMenuItem.setText("Open in browser");
					openInBrowserMenuItem.setImage(generalManager.getResourceLoader().getImage(
						geneTable.getDisplay(), "resources/icons/view/browser/browser.png"));
					openInBrowserMenuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
						
							// Switch to browser view
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(HTMLBrowserView.ID);
							}
							catch (PartInitException e1) {
								e1.printStackTrace();
							}
						};
					});

					MenuItem loadPathwayInBucketMenuItem = new MenuItem(menu, SWT.PUSH);
					loadPathwayInBucketMenuItem.setText("Load containing pathways in Bucket");
					loadPathwayInBucketMenuItem.setImage(generalManager.getResourceLoader().getImage(
						geneTable.getDisplay(), "resources/icons/view/remote/remote.png"));

					loadPathwayInBucketMenuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {

							// Switch to bucket view
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(GLRemoteRenderingView.ID);
							}
							catch (PartInitException e1) {
								e1.printStackTrace();
							}
							
							searchViewMediator.loadPathwayByGene((Integer)tableItem.getData());
						};
					});

					MenuItem loadGeneInHeatMap = new MenuItem(menu, SWT.PUSH);
					loadGeneInHeatMap.setText("Show gene in heat map");
					loadGeneInHeatMap.setImage(generalManager.getResourceLoader().getImage(
						geneTable.getDisplay(), "resources/icons/view/storagebased/heatmap/heatmap.png"));

					loadGeneInHeatMap.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {

							// Switch to heat map view
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(GLHeatMapView.ID);
							}
							catch (PartInitException e1) {
								e1.printStackTrace();
							}
						};
					});
				}
			}
		});
	}

	private void addPathwayContextMenu() {

		final Menu menu = new Menu(pathwayTable.getShell(), SWT.POP_UP);
		pathwayTable.setMenu(menu);

		menu.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event) {

				MenuItem[] menuItems = menu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}

				for (final TableItem tableItem : pathwayTable.getSelection()) {

					MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
					openInBrowserMenuItem.setText("Open in browser");
					openInBrowserMenuItem.setImage(generalManager.getResourceLoader().getImage(
						pathwayTable.getDisplay(), "resources/icons/view/browser/browser.png"));
					openInBrowserMenuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {

							// Switch to browser view
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(HTMLBrowserView.ID);
							}
							catch (PartInitException e1) {
								e1.printStackTrace();
							}

							searchViewMediator.loadURLInBrowser(((PathwayGraph) tableItem.getData())
								.getExternalLink());
						};
					});

					MenuItem loadPathwayInBucketMenuItem = new MenuItem(menu, SWT.PUSH);
					loadPathwayInBucketMenuItem.setText("Load pathway in Bucket");
					loadPathwayInBucketMenuItem.setImage(generalManager.getResourceLoader().getImage(
						pathwayTable.getDisplay(), "resources/icons/view/remote/remote.png"));

					loadPathwayInBucketMenuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {

							// Switch to bucket view
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(GLRemoteRenderingView.ID);
							}
							catch (PartInitException e1) {
								e1.printStackTrace();
							}

							searchViewMediator.loadPathway(((PathwayGraph) tableItem.getData()).getID());
						};
					});
				}
			}
		});
	}
}
