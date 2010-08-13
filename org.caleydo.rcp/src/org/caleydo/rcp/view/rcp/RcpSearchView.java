package org.caleydo.rcp.view.rcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.util.graph.EGraphItemHierarchy;
import org.caleydo.util.graph.EGraphItemProperty;
import org.caleydo.util.graph.IGraphItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
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
import org.eclipse.swt.widgets.MessageBox;
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
public class RcpSearchView
	extends ViewPart {

	public static final String ID = "org.caleydo.rcp.views.swt.SearchView";

	public static boolean bHorizontal = false;

	private Text searchText;

	private Group resultsGroup;

	private Table pathwayTable;
	private Table pathwayContainingGeneTable;
	private Table geneTable;

	private Composite composite;
	private Composite pathwayResultsComposite;

	private Label geneResultsLabel;
	private Label pathwayResultsLabel;
	private Label pathwayContainingGeneLabel;
	private Label horizontalSeparator;

	private IGeneralManager generalManager;

	private Button usePathways;
	private Button useGeneSymbol;
	private Button useGeneName;
	private Button useGeneRefSeqID;
	private Button useGeneEntrezGeneID;
	private Button useGeneDavidID;

	private Button showOnlyGenesContainedInAnyPathway;
	private boolean bShowOnlyGenesContainedInAnyPathway = false;

	private IIDMappingManager idMappingManager;

	private SearchViewMediator searchViewMediator;

	public RcpSearchView() {
		searchViewMediator = new SearchViewMediator();
	}

	@Override
	public void createPartControl(Composite parent) {

		generalManager = GeneralManager.get();

		idMappingManager = generalManager.getIDMappingManager();

		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		Group searchQueryGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		// searchDataKindGroup.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		searchQueryGroup.setLayout(new RowLayout());
		searchQueryGroup.setText("Search query");

		searchText = new Text(searchQueryGroup, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new RowData(550, 20));
		searchText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchText.setText("");
			}
		});

		Group searchDataKindGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		// searchDataKindGroup.setLayoutData(new
		// GridData(GridData.FILL_HORIZONTAL));
		searchDataKindGroup.setLayout(new RowLayout());
		searchDataKindGroup.setText("Search filter");

		Button searchButton = new Button(searchQueryGroup, SWT.PUSH);
		searchButton.setText("Search");

		usePathways = new Button(searchDataKindGroup, SWT.CHECK);
		usePathways.setText("Pathways");
		if (generalManager.getPathwayManager().size() == 0)
			usePathways.setEnabled(false);
		else
			usePathways.setSelection(true);

		useGeneSymbol = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneSymbol.setText("Gene symbol");
		if (generalManager.getIDMappingManager().hasMapping(EIDType.GENE_SYMBOL, EIDType.DAVID))
			useGeneSymbol.setSelection(true);
		else
			useGeneSymbol.setEnabled(false);

		useGeneName = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneName.setText("Gene name (full)");
		if (idMappingManager.hasMapping(EIDType.GENE_NAME, EIDType.DAVID))
			useGeneName.setSelection(true);
		else
			useGeneName.setEnabled(false);

		useGeneRefSeqID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneRefSeqID.setText("RefSeq ID");
		if (generalManager.getIDMappingManager().hasMapping(EIDType.REFSEQ_MRNA, EIDType.DAVID))
			useGeneRefSeqID.setSelection(true);
		else
			useGeneRefSeqID.setEnabled(false);

		useGeneEntrezGeneID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneEntrezGeneID.setText("Entrez Gene ID");
		if (generalManager.getIDMappingManager().hasMapping(EIDType.ENTREZ_GENE_ID, EIDType.DAVID))
			useGeneEntrezGeneID.setSelection(true);
		else
			useGeneEntrezGeneID.setEnabled(false);

		useGeneDavidID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneDavidID.setText("David ID");
		if (generalManager.getIDMappingManager().hasMapping(EIDType.DAVID, EIDType.REFSEQ_MRNA_INT))
			useGeneDavidID.setSelection(true);
		else
			useGeneDavidID.setEnabled(false);

		searchDataKindGroup.pack();

		resultsGroup = new Group(composite, SWT.NULL);
		// resultsGroup.setText("Search results");
		resultsGroup.setLayout(new GridLayout(1, false));
		resultsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		// addGeneContextMenu();
		// addResultsContent(resultsGroup);

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

	private void addResultsContent(Composite composite) {

		if (pathwayTable != null) {
			pathwayTable.dispose();
			pathwayResultsLabel.dispose();
			pathwayResultsComposite.dispose();
			pathwayContainingGeneTable.dispose();
			pathwayContainingGeneLabel.dispose();
		}

		if (geneTable != null) {
			geneTable.dispose();
			geneResultsLabel.dispose();
			showOnlyGenesContainedInAnyPathway.dispose();
		}

		if (horizontalSeparator != null)
			horizontalSeparator.dispose();

		if (useGeneSymbol.getSelection() || useGeneDavidID.getSelection()
			|| useGeneEntrezGeneID.getSelection() || useGeneName.getSelection()
			|| useGeneRefSeqID.getSelection()) {

			geneResultsLabel = new Label(composite, SWT.NULL);
			geneResultsLabel.setText("Gene results:");

			geneTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
			geneTable.setLinesVisible(true);
			geneTable.setHeaderVisible(true);
			geneTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			addGeneContextMenu();

			String[] titles =
				{ "Expression", "RefSeq ID", "David ID", "Entrez Gene ID", "Gene Symbol", "Gene Name" };
			for (int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(geneTable, SWT.NONE);
				column.setText(titles[i]);
			}

			geneTable.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					if (pathwayContainingGeneTable == null || pathwayContainingGeneTable.getItemCount() <= 0)
						return;

					// Flush old pathway results
					for (TableItem item : pathwayContainingGeneTable.getItems())
						item.dispose();

					pathwayContainingGeneLabel.setEnabled(true);
					pathwayContainingGeneTable.setEnabled(true);

					for (PathwayGraph pathway : getPathwaysContainingGene((Integer) e.item.getData())) {
						TableItem item = new TableItem(pathwayContainingGeneTable, SWT.NULL);
						item.setText(0, pathway.getType().getName());
						item.setText(1, pathway.getTitle());
						item.setData(pathway);
					}

					pathwayContainingGeneTable.getColumn(0).pack();
					pathwayContainingGeneTable.getColumn(1).pack();

					pathwayContainingGeneLabel.setText("Pathway containing selected gene: "
						+ geneTable.getSelection()[0].getText(3));
				}
			});

			showOnlyGenesContainedInAnyPathway = new Button(composite, SWT.CHECK);
			showOnlyGenesContainedInAnyPathway.setText("Show only genes contained in any pathway");
			showOnlyGenesContainedInAnyPathway.setSelection(bShowOnlyGenesContainedInAnyPathway);
			showOnlyGenesContainedInAnyPathway.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					bShowOnlyGenesContainedInAnyPathway = showOnlyGenesContainedInAnyPathway.getSelection();
					startSearch();
				}
			});
		}

		if ((useGeneSymbol.getSelection() || useGeneDavidID.getSelection()
			|| useGeneEntrezGeneID.getSelection() || useGeneName.getSelection() || useGeneRefSeqID
			.getSelection())
			&& usePathways.getSelection()) {

			horizontalSeparator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			data.heightHint = 20;
			horizontalSeparator.setLayoutData(data);
		}

		if (usePathways.getSelection()) {

			pathwayResultsComposite = new Composite(composite, SWT.NULL);
			pathwayResultsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
			pathwayResultsComposite.setLayout(new GridLayout(2, true));

			pathwayResultsLabel = new Label(pathwayResultsComposite, SWT.NULL);
			pathwayResultsLabel.setText("Pathway results:");

			pathwayContainingGeneLabel = new Label(pathwayResultsComposite, SWT.NULL);
			pathwayContainingGeneLabel.setText("Pathway containing selected gene:");
			pathwayContainingGeneLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			pathwayTable = new Table(pathwayResultsComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
			pathwayTable.setLinesVisible(true);
			pathwayTable.setHeaderVisible(true);
			pathwayTable.setLayoutData(new GridData(GridData.FILL_BOTH));

			TableColumn pathwayDatabaseColumn = new TableColumn(pathwayTable, SWT.NONE);
			pathwayDatabaseColumn.setText("Database");
			TableColumn pathwayNameColumn = new TableColumn(pathwayTable, SWT.NONE);
			pathwayNameColumn.setText("Pathway Name");

			addPathwayContextMenu(pathwayTable);

			pathwayContainingGeneTable =
				new Table(pathwayResultsComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
			pathwayContainingGeneTable.setLinesVisible(true);
			pathwayContainingGeneTable.setHeaderVisible(true);
			pathwayContainingGeneTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			pathwayContainingGeneTable.setEnabled(false);

			TableColumn pathwayContainingGeneDatabaseColumn =
				new TableColumn(pathwayContainingGeneTable, SWT.NONE);
			pathwayContainingGeneDatabaseColumn.setText("Database");
			TableColumn pathwayContainingGeneNameColumn =
				new TableColumn(pathwayContainingGeneTable, SWT.NONE);
			pathwayContainingGeneNameColumn.setText("Pathway Name");

			addPathwayContextMenu(pathwayContainingGeneTable);

		}

		// composite.pack();
		composite.layout();
	}

	private void startSearch() {

		if (searchText.getText().length() < 3) {

			MessageBox messageBox = new MessageBox(composite.getShell(), SWT.OK);
			messageBox.setText("Invalid search query");
			messageBox.setMessage("Please enter a search query with the minimum of 3 characters.");
			messageBox.open();
			return;
		}

		// Initialize result content
		addResultsContent(resultsGroup);

		if (usePathways.getSelection()) {
			searchForPathway(searchText.getText());
		}

		if (useGeneSymbol.getSelection() || useGeneDavidID.getSelection()
			|| useGeneEntrezGeneID.getSelection() || useGeneName.getSelection()
			|| useGeneRefSeqID.getSelection()) {

			searchForGene(searchText.getText());
		}
	}

	private void searchForPathway(String sSearchQuery) {

		// Flush old pathway results
		for (TableItem item : pathwayTable.getItems())
			item.dispose();

		if (!usePathways.getSelection())
			return;

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
			for (Object sGeneSymbol : idMappingManager.getMap(EMappingType.GENE_SYMBOL_2_DAVID).keySet()) {
				regexMatcher = pattern.matcher((String) sGeneSymbol);
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) idMappingManager.getID(EIDType.GENE_SYMBOL,
						EIDType.DAVID, sGeneSymbol));
			}
		}

		if (useGeneEntrezGeneID.getSelection()) {
			for (Object iEntrezGeneID : idMappingManager.getMap(EMappingType.ENTREZ_GENE_ID_2_DAVID).keySet()) {
				regexMatcher = pattern.matcher(iEntrezGeneID.toString());
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) idMappingManager.getID(EIDType.ENTREZ_GENE_ID,
						EIDType.DAVID, iEntrezGeneID));
			}
		}

		if (useGeneRefSeqID.getSelection()) {
			for (Object sGeneSymbol : idMappingManager.getMap(EMappingType.REFSEQ_MRNA_2_DAVID).keySet()) {
				regexMatcher = pattern.matcher((String) sGeneSymbol);
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) idMappingManager.getID(EIDType.REFSEQ_MRNA,
						EIDType.DAVID, sGeneSymbol));
			}
		}

		if (useGeneName.getSelection()) {
			for (Object sGeneName : idMappingManager.getMap(EMappingType.GENE_NAME_2_DAVID).keySet()) {
				regexMatcher = pattern.matcher((String) sGeneName);
				if (regexMatcher.find())
					iArDavidGeneResults.add((Integer) generalManager.getIDMappingManager().getID(
						EIDType.GENE_NAME, EIDType.DAVID, sGeneName));
			}
		}

		// Fill results in table
		for (Integer iDavidID : iArDavidGeneResults) {

			// When gene filter is on and gene is not contained in any pathway
			// -> ignore
			if (showOnlyGenesContainedInAnyPathway.getSelection()
				&& getPathwaysContainingGene(iDavidID).size() == 0)
				continue;

			String sRefSeqIDs = "";

			try {
				for (Object sRefSeqID : idMappingManager.<Integer, Set<Object>> getID(EIDType.DAVID,
					EIDType.REFSEQ_MRNA, iDavidID)) {
					sRefSeqIDs += sRefSeqID + " ";
				}
			}
			catch (NullPointerException npe) {
				sRefSeqIDs = "<No Mapping>";
			}

			String sEntrezGeneID = "";
			Integer iEntrezGeneID = idMappingManager.getID(EIDType.DAVID, EIDType.ENTREZ_GENE_ID, iDavidID);
			if (iEntrezGeneID == null)
				sEntrezGeneID = "<No Mapping>";
			else
				sEntrezGeneID = iEntrezGeneID.toString();

			String sGeneSymbol = idMappingManager.getID(EIDType.DAVID, EIDType.GENE_SYMBOL, iDavidID);
			if (sGeneSymbol == null)
				sGeneSymbol = "<Unknown>";

			String sGeneName = idMappingManager.getID(EIDType.DAVID, EIDType.GENE_NAME, iDavidID);
			if (sGeneName == null)
				sGeneName = "<Unknown>";

			// Determine whether the gene has a valid expression value in the
			// current data set
			String sExpressionValueInCurrentDataSet = "NOT FOUND";

			Set<Integer> setExpIndex =
				idMappingManager.getIDAsSet(EIDType.DAVID, EIDType.EXPRESSION_INDEX, iDavidID);
			// h.getExpressionIndicesFromDavid(iDavidID);

			if (setExpIndex != null && setExpIndex.size() > 0)
				sExpressionValueInCurrentDataSet = "FOUND";

			TableItem item = new TableItem(geneTable, SWT.NULL);
			item.setText(0, new String(sExpressionValueInCurrentDataSet));
			item.setText(1, sRefSeqIDs);
			item.setText(2, Integer.toString(iDavidID));
			item.setText(3, sEntrezGeneID);
			item.setText(4, sGeneSymbol);
			item.setText(5, sGeneName);

			item.setData(iDavidID);
		}

		// Sort gene table using the info whether expression data is available
		// or not.
		sortTable(geneTable, 0);

		// Highlight content if it matches the search query
		for (int iIndex = 0; iIndex < geneTable.getColumnCount(); iIndex++) {
			for (TableItem item : geneTable.getItems()) {
				regexMatcher = pattern.matcher(item.getText(iIndex));
				if (regexMatcher.find()) {
					item.setBackground(iIndex, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				}
			}
		}

		for (TableColumn column : geneTable.getColumns())
			column.pack();

		geneTable.getColumn(0).setWidth(100);
		geneTable.getColumn(4).setWidth(200);
		geneTable.getColumn(1).setWidth(300);
	}

	@Override
	public void setFocus() {

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

					// Do not create context menu for genes that have to
					// expression value
					if (tableItem.getText(0).equals("FOUND")) {
						MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
						openInBrowserMenuItem.setText("Open in browser");
						openInBrowserMenuItem.setImage(generalManager.getResourceLoader().getImage(
							geneTable.getDisplay(), "resources/icons/view/browser/browser.png"));
						openInBrowserMenuItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {

								searchViewMediator.selectGeneSystemWide((Integer) tableItem.getData());

								// Switch to browser view
								try {
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
										.showView("org.caleydo.view.browser");
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
							@Override
							public void widgetSelected(SelectionEvent e) {

								searchViewMediator.loadPathwayByGene((Integer) tableItem.getData());
							};
						});

						MenuItem loadGeneInHeatMapMenuItem = new MenuItem(menu, SWT.PUSH);
						loadGeneInHeatMapMenuItem.setText("Show gene in heat map");
						loadGeneInHeatMapMenuItem.setImage(generalManager.getResourceLoader().getImage(
							geneTable.getDisplay(), "resources/icons/view/storagebased/heatmap/heatmap.png"));

						loadGeneInHeatMapMenuItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {

								searchViewMediator.selectGeneSystemWide((Integer) tableItem.getData());

								// Switch to browser view
								try {
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
										.showView("org.caleydo.view.heatmap.hierarchical");
								}
								catch (PartInitException e1) {
									e1.printStackTrace();
								}
							};
						});

						MenuItem loadGeneInParallelCoordinatesMenuItem = new MenuItem(menu, SWT.PUSH);
						loadGeneInParallelCoordinatesMenuItem.setText("Show gene in parallel coordinates");
						loadGeneInParallelCoordinatesMenuItem.setImage(generalManager.getResourceLoader()
							.getImage(geneTable.getDisplay(),
								"resources/icons/view/storagebased/parcoords/parcoords.png"));

						loadGeneInParallelCoordinatesMenuItem.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {

								searchViewMediator.selectGeneSystemWide((Integer) tableItem.getData());

								// Switch to browser view
								try {
									PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
										.showView("org.caleydo.view.parcoords");
								}
								catch (PartInitException e1) {
									e1.printStackTrace();
								}
							};
						});
					}
					else {
						MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
						openInBrowserMenuItem
							.setText("Sorry, no mapping was found in your data (see first column).");
					}
				}
			}
		});
	}

	private void addPathwayContextMenu(final Table pathwayTable) {

		final Menu menu = new Menu(pathwayTable.getShell(), SWT.POP_UP);
		pathwayTable.setMenu(menu);

		menu.addListener(SWT.Show, new Listener() {
			public void handleEvent(Event event) {

				MenuItem[] menuItems = menu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}

				final TableItem tableItem = pathwayTable.getSelection()[0];

				MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
				openInBrowserMenuItem.setText("Open in browser");
				openInBrowserMenuItem.setImage(generalManager.getResourceLoader().getImage(
					pathwayTable.getDisplay(), "resources/icons/view/browser/browser.png"));
				openInBrowserMenuItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						// Switch to browser view
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
								"org.caleydo.view.browser");
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
					@Override
					public void widgetSelected(SelectionEvent e) {

						// Switch to bucket view
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
								"org.caleydo.view.bucket");
						}
						catch (PartInitException e1) {
							e1.printStackTrace();
						}

						searchViewMediator.loadPathway(((PathwayGraph) tableItem.getData()).getID());
					};
				});
			}
		});
	}

	private Set<PathwayGraph> getPathwaysContainingGene(int iDavidID) {

		// set to avoid duplicate pathways
		Set<PathwayGraph> pathwaysContainingGene = new HashSet<PathwayGraph>();

		PathwayVertexGraphItem pathwayGraphItem =
			generalManager.getPathwayItemManager().getPathwayVertexGraphItemByDavidId(iDavidID);

		// Only handle David IDs that does exist in any pathway
		if (pathwayGraphItem != null) {

			List<IGraphItem> pathwayItems =
				pathwayGraphItem.getAllItemsByProp(EGraphItemProperty.ALIAS_CHILD);
			for (IGraphItem pathwayItem : pathwayItems) {
				PathwayGraph pathwayGraph =
					(PathwayGraph) pathwayItem.getAllGraphByType(EGraphItemHierarchy.GRAPH_PARENT).get(0);
				pathwaysContainingGene.add(pathwayGraph);
			}
		}

		return pathwaysContainingGene;
	}

	@SuppressWarnings(value = { "unchecked" })
	public static void sortTable(Table table, int iColumnIndex) {
		if (table == null || table.getColumnCount() <= 1)
			return;
		if (iColumnIndex < 0 || iColumnIndex >= table.getColumnCount())
			throw new IllegalArgumentException("The specified column does not exits. ");

		final int colIndex = iColumnIndex;
		Comparator comparator = new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((TableItem) o1).getText(colIndex).compareTo(((TableItem) o2).getText(colIndex));
			}

			@Override
			public boolean equals(Object obj) {
				return false;
			}
		};

		TableItem[] tableItems = table.getItems();
		Arrays.sort(tableItems, comparator);

		for (int i = 0; i < tableItems.length; i++) {
			TableItem item = new TableItem(table, SWT.NULL);
			for (int j = 0; j < table.getColumnCount(); j++) {
				item.setText(j, tableItems[i].getText(j));
				item.setImage(j, tableItems[i].getImage(j));
				item.setData(tableItems[i].getData());
			}
			tableItems[i].dispose();
		}
	}
}
