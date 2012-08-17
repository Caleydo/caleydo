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
package org.caleydo.view.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertex;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.datadomain.pathway.manager.PathwayItemManager;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
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

/**
 * Search view contains gene and pathway search.
 * 
 * @author Marc Streit
 */
public class RcpSearchView extends CaleydoRCPViewPart {

	// private GeneticDataDomain dataDomain;
	private ArrayList<GeneticDataDomain> geneticDataDomains;

	public static String VIEW_TYPE = "org.caleydo.view.search";

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

	private GeneralManager generalManager;

	private Button usePathways;
	private Button useGeneSymbol;
	private Button useGeneName;
	private Button useGeneRefSeqID;
	private Button useGeneEntrezGeneID;
	private Button useGeneDavidID;

	private Button showOnlyGenesContainedInAnyPathway;
	private boolean bShowOnlyGenesContainedInAnyPathway = false;

	private IDMappingManager geneIDMappingManager;

	private SearchViewMediator searchViewMediator;

	private IDCategory geneIDCategory = IDCategory.getIDCategory("GENE");

	private IDType geneSymbolIDType = IDType.getIDType("GENE_SYMBOL");
	private IDType davidIDType = IDType.getIDType("DAVID");
	private IDType geneNameIDType = IDType.getIDType("GENE_NAME");
	private IDType refseqMrnaIDTYpe = IDType.getIDType("REFSEQ_MRNA");
	private IDType entrez = IDType.getIDType("ENTREZ_GENE_ID");

	public RcpSearchView() {

		searchViewMediator = new SearchViewMediator();
	}

	@Override
	public void createPartControl(Composite parent) {

		geneticDataDomains = DataDomainManager.get().getDataDomainsByType(
				GeneticDataDomain.class);

		generalManager = GeneralManager.get();

		if (geneticDataDomains.size() == 0)
			throw new IllegalStateException("No genetic data domains found");

		geneIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				geneIDCategory);

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
		if (PathwayManager.get().size() == 0)
			usePathways.setEnabled(false);
		else
			usePathways.setSelection(true);

		useGeneSymbol = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneSymbol.setText("Gene symbol");
		if (geneIDMappingManager.hasMapping(geneSymbolIDType, davidIDType))
			useGeneSymbol.setSelection(true);
		else
			useGeneSymbol.setEnabled(false);

		useGeneName = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneName.setText("Gene name (full)");
		if (geneIDMappingManager.hasMapping(geneNameIDType, davidIDType))
			useGeneName.setSelection(true);
		else
			useGeneName.setEnabled(false);

		useGeneRefSeqID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneRefSeqID.setText("RefSeq ID");
		if (geneIDMappingManager.hasMapping(refseqMrnaIDTYpe, davidIDType))
			useGeneRefSeqID.setSelection(true);
		else
			useGeneRefSeqID.setEnabled(false);

		useGeneEntrezGeneID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneEntrezGeneID.setText("Entrez Gene ID");
		if (geneIDMappingManager.hasMapping(entrez, davidIDType))
			useGeneEntrezGeneID.setSelection(true);
		else
			useGeneEntrezGeneID.setEnabled(false);

		useGeneDavidID = new Button(searchDataKindGroup, SWT.CHECK);
		useGeneDavidID.setText("David ID");
		if (geneIDMappingManager.hasMapping(davidIDType, refseqMrnaIDTYpe))
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

			geneTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION
					| SWT.VIRTUAL);
			geneTable.setLinesVisible(true);
			geneTable.setHeaderVisible(true);
			geneTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			addGeneContextMenu();

			String[] titles = new String[5 + geneticDataDomains.size()];
			int count = 0;
			for (GeneticDataDomain geneticDataDomain : geneticDataDomains) {
				titles[count] = geneticDataDomain.getLabel();
				count++;
			}
			titles[count++] = "RefSeq ID";
			titles[count++] = "David ID";
			titles[count++] = "Entrez Gene ID";
			titles[count++] = "Gene Symbol";
			titles[count++] = "Gene Name";

			for (int i = 0; i < titles.length; i++) {
				TableColumn column = new TableColumn(geneTable, SWT.NONE);
				column.setText(titles[i]);
			}

			geneTable.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {

					// if (pathwayContainingGeneTable == null
					// || pathwayContainingGeneTable.getItemCount() <= 0)
					// return;

					// Flush old pathway results
					for (TableItem item : pathwayContainingGeneTable.getItems())
						item.dispose();

					pathwayContainingGeneLabel.setEnabled(true);
					pathwayContainingGeneTable.setEnabled(true);

					for (PathwayGraph pathway : getPathwaysContainingGene((Integer) e.item
							.getData())) {
						TableItem item = new TableItem(pathwayContainingGeneTable,
								SWT.NULL);
						item.setText(0, pathway.getType().getName());
						item.setText(1, pathway.getTitle());
						item.setData(pathway);
					}

					pathwayContainingGeneTable.getColumn(0).pack();
					pathwayContainingGeneTable.getColumn(1).pack();

					pathwayContainingGeneLabel
							.setText("Pathway containing selected gene: "
									+ geneTable.getSelection()[0].getText(3));
				}
			});

			showOnlyGenesContainedInAnyPathway = new Button(composite, SWT.CHECK);
			showOnlyGenesContainedInAnyPathway
					.setText("Show only genes contained in any pathway");
			showOnlyGenesContainedInAnyPathway
					.setSelection(bShowOnlyGenesContainedInAnyPathway);
			showOnlyGenesContainedInAnyPathway
					.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							bShowOnlyGenesContainedInAnyPathway = showOnlyGenesContainedInAnyPathway
									.getSelection();
							startSearch();
						}
					});
		}

		if ((useGeneSymbol.getSelection() || useGeneDavidID.getSelection()
				|| useGeneEntrezGeneID.getSelection() || useGeneName.getSelection() || useGeneRefSeqID
					.getSelection()) && usePathways.getSelection()) {

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
			pathwayContainingGeneLabel.setLayoutData(new GridData(
					GridData.FILL_HORIZONTAL));

			pathwayTable = new Table(pathwayResultsComposite, SWT.BORDER
					| SWT.FULL_SELECTION | SWT.VIRTUAL);
			pathwayTable.setLinesVisible(true);
			pathwayTable.setHeaderVisible(true);
			pathwayTable.setLayoutData(new GridData(GridData.FILL_BOTH));

			TableColumn pathwayDatabaseColumn = new TableColumn(pathwayTable, SWT.NONE);
			pathwayDatabaseColumn.setText("Database");
			TableColumn pathwayNameColumn = new TableColumn(pathwayTable, SWT.NONE);
			pathwayNameColumn.setText("Pathway Name");

			addPathwayContextMenu(pathwayTable);

			pathwayContainingGeneTable = new Table(pathwayResultsComposite, SWT.BORDER
					| SWT.FULL_SELECTION | SWT.VIRTUAL);
			pathwayContainingGeneTable.setLinesVisible(true);
			pathwayContainingGeneTable.setHeaderVisible(true);
			pathwayContainingGeneTable.setLayoutData(new GridData(GridData.FILL_BOTH));
			pathwayContainingGeneTable.setEnabled(false);

			TableColumn pathwayContainingGeneDatabaseColumn = new TableColumn(
					pathwayContainingGeneTable, SWT.NONE);
			pathwayContainingGeneDatabaseColumn.setText("Database");
			TableColumn pathwayContainingGeneNameColumn = new TableColumn(
					pathwayContainingGeneTable, SWT.NONE);
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
			messageBox
					.setMessage("Please enter a search query with the minimum of 3 characters.");
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
		for (PathwayGraph pathway : PathwayManager.get().getAllItems()) {
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

	private void searchForGene(final String searchQuery) {

		// Flush old pathway results
		for (TableItem item : geneTable.getItems())
			item.dispose();

		Pattern pattern = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE);
		Matcher regexMatcher;
		ArrayList<Integer> davidGeneResults = new ArrayList<Integer>();

		if (useGeneSymbol.getSelection()) {
			for (Object sGeneSymbol : geneIDMappingManager.getMap(
					MappingType.getType(geneSymbolIDType, davidIDType)).keySet()) {
				regexMatcher = pattern.matcher((String) sGeneSymbol);
				if (regexMatcher.find())
					davidGeneResults.add((Integer) geneIDMappingManager.getID(
							geneSymbolIDType, davidIDType, sGeneSymbol));
			}
		}

		if (useGeneEntrezGeneID.getSelection()) {
			for (Object entrezGeneID : geneIDMappingManager.getMap(
					MappingType.getType(entrez, davidIDType)).keySet()) {
				regexMatcher = pattern.matcher(entrezGeneID.toString());
				if (regexMatcher.find())
					davidGeneResults.add((Integer) geneIDMappingManager.getID(entrez,
							davidIDType, entrezGeneID));
			}
		}

		if (useGeneRefSeqID.getSelection()) {
			for (Object refSeqMrna : geneIDMappingManager.getMap(
					MappingType.getType(refseqMrnaIDTYpe, davidIDType)).keySet()) {
				regexMatcher = pattern.matcher((String) refSeqMrna);
				if (regexMatcher.find())
					davidGeneResults
							.addAll((Collection<? extends Integer>) geneIDMappingManager
									.getID(refseqMrnaIDTYpe, davidIDType, refSeqMrna));
			}
		}

		if (useGeneName.getSelection()) {
			for (Object geneName : geneIDMappingManager.getMap(
					MappingType.getType(geneNameIDType, davidIDType)).keySet()) {
				regexMatcher = pattern.matcher((String) geneName);
				if (regexMatcher.find())
					davidGeneResults.add((Integer) geneIDMappingManager.getID(
							geneNameIDType, davidIDType, geneName));
			}
		}

		// Fill results in table
		for (Integer davidID : davidGeneResults) {

			// When gene filter is on and gene is not contained in any pathway
			// -> ignore
			if (showOnlyGenesContainedInAnyPathway.getSelection()
					&& getPathwaysContainingGene(davidID).size() == 0)
				continue;

			String sRefSeqIDs = "";

			try {
				for (Object refSeqID : geneIDMappingManager.<Integer, Set<Object>> getID(
						davidIDType, refseqMrnaIDTYpe, davidID)) {
					sRefSeqIDs += refSeqID + " ";
				}
			} catch (NullPointerException npe) {
				sRefSeqIDs = "<No Mapping>";
			}

			String entrezGeneID = "";
			Integer iEntrezGeneID = geneIDMappingManager.getID(davidIDType, entrez,
					davidID);
			if (iEntrezGeneID == null)
				entrezGeneID = "<No Mapping>";
			else
				entrezGeneID = iEntrezGeneID.toString();

			String geneSymbol = geneIDMappingManager.getID(davidIDType, geneSymbolIDType,
					davidID);
			if (geneSymbol == null)
				geneSymbol = "<Unknown>";

			String geneName = geneIDMappingManager.getID(davidIDType, geneNameIDType,
					davidID);
			if (geneName == null)
				geneName = "<Unknown>";

			ArrayList<String> foundInDataSet = new ArrayList<String>(
					geneticDataDomains.size());

			for (GeneticDataDomain geneticDataDomain : geneticDataDomains) {
				Set<Integer> expressionIndices = geneIDMappingManager.getIDAsSet(
						davidIDType, geneticDataDomain.getGeneIDType(), davidID);
				if (expressionIndices != null && expressionIndices.size() > 0) {
					foundInDataSet.add("Found");
				} else
					foundInDataSet.add("Not found");
			}

			// Determine whether the gene has a valid expression value in the
			// current data set

			// h.getExpressionIndicesFromDavid(iDavidID);

			TableItem item = new TableItem(geneTable, SWT.NULL);
			for (int dataDomainCount = 0; dataDomainCount < geneticDataDomains.size(); dataDomainCount++) {
				item.setText(dataDomainCount, foundInDataSet.get(dataDomainCount));
			}
			int nrDataDomains = geneticDataDomains.size();
			item.setText(nrDataDomains, sRefSeqIDs);
			item.setText(nrDataDomains + 1, Integer.toString(davidID));
			item.setText(nrDataDomains + 2, entrezGeneID);
			item.setText(nrDataDomains + 3, geneSymbol);
			item.setText(nrDataDomains + 4, geneName);

			item.setData(davidID);
		}

		// Sort gene table using the info whether expression data is available
		// or not.
		sortTable(geneTable, 0);

		// Highlight content if it matches the search query
		for (int iIndex = 0; iIndex < geneTable.getColumnCount(); iIndex++) {
			for (TableItem item : geneTable.getItems()) {
				regexMatcher = pattern.matcher(item.getText(iIndex));
				if (regexMatcher.find()) {
					item.setBackground(iIndex,
							Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
				}
			}
		}

		for (TableColumn column : geneTable.getColumns())
			column.pack();

		geneTable.getColumn(2).setWidth(100);
		// geneTable.getColumn(4).setWidth(200);
		// geneTable.getColumn(1).setWidth(300);
	}

	@Override
	public void setFocus() {

	}

	private void addGeneContextMenu() {

		final Menu menu = new Menu(geneTable.getShell(), SWT.POP_UP);
		geneTable.setMenu(menu);

		menu.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MenuItem[] menuItems = menu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}

				for (final TableItem tableItem : geneTable.getSelection()) {

					for (int dataDomainCount = 0; dataDomainCount < geneticDataDomains
							.size(); dataDomainCount++) {
						// Do not create context menu for genes that have to
						// expression value

						if (tableItem.getText(dataDomainCount).equalsIgnoreCase("FOUND")) {
							createContextMenuItemsForDataDomain(menu, tableItem,
									geneticDataDomains.get(dataDomainCount));
						} else {
							MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
							openInBrowserMenuItem.setText("Not loaded");
						}
						if (dataDomainCount + 1 < geneticDataDomains.size())
							new MenuItem(menu, SWT.SEPARATOR);

					}
				}
			}

		});
	}

	private void createContextMenuItemsForDataDomain(Menu menu,
			final TableItem tableItem, final GeneticDataDomain dataDomain) {

		//
		// MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
		// openInBrowserMenuItem.setText("Open in browser");
		// openInBrowserMenuItem.setImage(generalManager.getResourceLoader().getImage(
		// geneTable.getDisplay(), "resources/icons/view/browser/browser.png"));
		// openInBrowserMenuItem.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// searchViewMediator.selectGeneSystemWide((Integer)
		// tableItem.getData());
		//
		// // Switch to browser view
		// try {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		// .showView("org.caleydo.view.browser");
		// } catch (PartInitException e1) {
		// e1.printStackTrace();
		// }
		// };
		// });
		//
		// MenuItem loadPathwayInBucketMenuItem = new MenuItem(menu, SWT.PUSH);
		// loadPathwayInBucketMenuItem.setText("Load containing pathways in Bucket");
		// loadPathwayInBucketMenuItem.setImage(generalManager.getResourceLoader().getImage(
		// geneTable.getDisplay(), "resources/icons/view/remote/remote.png"));
		//
		// loadPathwayInBucketMenuItem.addSelectionListener(new
		// SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// searchViewMediator.loadPathwayByGene((Integer) tableItem.getData());
		// };
		// });
		//
		// MenuItem loadGeneInHeatMapMenuItem = new MenuItem(menu, SWT.PUSH);
		// loadGeneInHeatMapMenuItem.setText("Show gene in heat map");
		// loadGeneInHeatMapMenuItem.setImage(generalManager.getResourceLoader().getImage(
		// geneTable.getDisplay(),
		// "resources/icons/view/tablebased/heatmap/heatmap.png"));
		//
		// loadGeneInHeatMapMenuItem.addSelectionListener(new SelectionAdapter()
		// {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// searchViewMediator.selectGeneSystemWide((Integer)
		// tableItem.getData());
		//
		// // Switch to browser view
		// try {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		// .showView("org.caleydo.view.heatmap.hierarchical");
		// } catch (PartInitException e1) {
		// e1.printStackTrace();
		// }
		// };
		// });
		//
		// MenuItem loadGeneInParallelCoordinatesMenuItem = new MenuItem(menu,
		// SWT.PUSH);
		// loadGeneInParallelCoordinatesMenuItem
		// .setText("Show gene in parallel coordinates");
		// loadGeneInParallelCoordinatesMenuItem.setImage(generalManager.getResourceLoader()
		// .getImage(geneTable.getDisplay(),
		// "resources/icons/view/tablebased/parcoords/parcoords.png"));
		//
		// loadGeneInParallelCoordinatesMenuItem
		// .addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// searchViewMediator.selectGeneSystemWide((Integer) tableItem
		// .getData());
		//
		// // Switch to browser view
		// try {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage()
		// .showView("org.caleydo.view.parcoords");
		// } catch (PartInitException e1) {
		// e1.printStackTrace();
		// }
		// };
		// });
		//
		MenuItem makeCategoryOfGene = new MenuItem(menu, SWT.PUSH);
		makeCategoryOfGene.setText("Create categorization of " + dataDomain.getLabel());
		// loadGeneInHeatMapMenuItem.setImage(generalManager
		// .getResourceLoader()
		// .getImage(geneTable.getDisplay(),dat
		// "resources/icons/view/tablebased/heatmap/heatmap.png"));

		makeCategoryOfGene.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				searchViewMediator.createPerspecive(dataDomain,
						(Integer) tableItem.getData());

				// Switch to browser view
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView("org.caleydo.view.dvi");
				} catch (PartInitException e1) {
					e1.printStackTrace();
				}
			};
		});

	}

	private void addPathwayContextMenu(final Table pathwayTable) {

		final Menu menu = new Menu(pathwayTable.getShell(), SWT.POP_UP);
		pathwayTable.setMenu(menu);

		menu.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {

				MenuItem[] menuItems = menu.getItems();
				for (int i = 0; i < menuItems.length; i++) {
					menuItems[i].dispose();
				}

				final TableItem tableItem = pathwayTable.getSelection()[0];

				MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
				openInBrowserMenuItem.setText("Open in browser");
				openInBrowserMenuItem.setImage(generalManager.getResourceLoader()
						.getImage(pathwayTable.getDisplay(),
								"resources/icons/view/browser/browser.png"));
				openInBrowserMenuItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						// Switch to browser view
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage().showView("org.caleydo.view.browser");
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}

						searchViewMediator.loadURLInBrowser(((PathwayGraph) tableItem
								.getData()).getExternalLink());
					};
				});

				MenuItem loadPathwayInBucketMenuItem = new MenuItem(menu, SWT.PUSH);
				loadPathwayInBucketMenuItem.setText("Load pathway in Bucket");
				loadPathwayInBucketMenuItem.setImage(generalManager.getResourceLoader()
						.getImage(pathwayTable.getDisplay(),
								"resources/icons/view/remote/remote.png"));

				loadPathwayInBucketMenuItem.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						// Switch to bucket view
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow()
									.getActivePage().showView("org.caleydo.view.bucket");
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}

						searchViewMediator.loadPathway(((PathwayGraph) tableItem
								.getData()).getID());
					};
				});
			}
		});
	}

	private Set<PathwayGraph> getPathwaysContainingGene(int iDavidID) {

		// set to avoid duplicate pathwaysserializedView
		Set<PathwayGraph> pathwaysContainingGene = new HashSet<PathwayGraph>();

		PathwayVertex vertex = PathwayItemManager.get()
				.getPathwayVertexByDavidId(iDavidID);

		// Only handle David IDs that does exist in any pathway
		if (vertex != null) {

			for (PathwayVertexRep vertexRep : vertex.getPathwayVertexReps()) {
				pathwaysContainingGene.add(vertexRep.getPathway());
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
			@Override
			public int compare(Object o1, Object o2) {
				return ((TableItem) o1).getText(colIndex).compareTo(
						((TableItem) o2).getText(colIndex));
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

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSearchView();
		determineDataConfiguration(serializedView);
	}
}
