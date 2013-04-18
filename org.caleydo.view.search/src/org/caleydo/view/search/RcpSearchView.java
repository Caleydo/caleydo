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
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;

/**
 * Search view contains gene and pathway search.
 *
 * @author Marc Streit and Samuel Gratzl
 */
public class RcpSearchView extends CaleydoRCPViewPart {
	public static final String VIEW_TYPE = "org.caleydo.view.search";

	private Composite composite;

	private Text searchText;
	private final List<Button> searchWithinIDType = new ArrayList<>();

	private Group resultsGroup;

	private TableViewer resultTable;


	@Override
	public void createPartControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		createSearchGroup(composite);
		createFilterGroup(composite);

		resultsGroup = new Group(composite, SWT.NULL);
		// resultsGroup.setText("Search results");
		resultsGroup.setLayout(new GridLayout(1, false));
		resultsGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		// addGeneContextMenu();
		// addResultsContent(resultsGroup);

		composite.layout();
	}

	private void createSearchGroup(Composite composite) {
		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new RowLayout());
		group.setText("Search query");

		searchText = new Text(group, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new RowData(550, 20));
		searchText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				searchText.selectAll();
			}
		});

		final Button searchButton = new Button(group, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.setEnabled(false);

		final ControlDecoration dec = new ControlDecoration(searchText, SWT.TOP | SWT.LEFT);
		dec.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_WARNING));
		dec.setShowOnlyOnFocus(true);
		dec.hide();
		searchText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (searchText.getText().length() > 3) {
					dec.hide();
					searchButton.setEnabled(true);
				} else {
					dec.show();
					searchButton.setEnabled(false);
				}
			}
		});

		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search(searchText.getText());
			}
		});
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR: {
					if (searchButton.isEnabled())
						search(searchText.getText());
					break;
				}
				}
			}
		});
	}

	private void createFilterGroup(Composite composite) {
		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new RowLayout());
		group.setText("Search filter");

		Collection<IDCategory> categories = IDCategory.getAllRegisteredIDCategories();
		for (IDCategory cat : categories) {
			for (IDType type : cat.getIdTypes()) {
				if (type.isInternalType())
					continue;
				Button b = new Button(group, SWT.CHECK);
				b.setText(type.getTypeName());
				b.setData(type);
				b.setSelection(true); // by default search all
				this.searchWithinIDType.add(b);
			}
		}
		group.pack();
	}

	private void search(String query) {
		final Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
		Predicate<Object> searchQuery = new Predicate<Object>() {
			@Override
			public boolean apply(Object in) {
				return in != null && pattern.matcher(in.toString()).matches();
			}
		};
		com.google.common.collect.Table<IDCategory, IDType, Set<?>> result = HashBasedTable.create();
		for (Button b : searchWithinIDType) {
			if (!b.getSelection())
				continue;
			IDType idType = (IDType) b.getData();

			IDMappingManager mappingManager = IDMappingManagerRegistry.get()
					.getIDMappingManager(idType.getIDCategory());

			Set<?> ids = mappingManager.getAllMappedIDs(idType);
			ids = new HashSet<>(Sets.filter(ids, searchQuery));
			if (ids.isEmpty())
				continue;
			result.put(idType.getIDCategory(), idType, ids);
		}

		System.out.println(result);
	}

	private void addResultsContent(Composite composite) {
		// if (true) {
		//
		// geneResultsLabel = new Label(composite, SWT.NULL);
		// geneResultsLabel.setText("Gene results:");
		//
		// resultTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		// resultTable.setLinesVisible(true);
		// resultTable.setHeaderVisible(true);
		// resultTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		// addGeneContextMenu();
		//
		// String[] titles = new String[5 + geneticDataDomains.size()];
		// int count = 0;
		// for (GeneticDataDomain geneticDataDomain : geneticDataDomains) {
		// titles[count] = geneticDataDomain.getLabel();
		// count++;
		// }
		// titles[count++] = "RefSeq ID";
		// titles[count++] = "David ID";
		// titles[count++] = "Entrez Gene ID";
		// titles[count++] = "Gene Symbol";
		// titles[count++] = "Gene Name";
		//
		// for (int i = 0; i < titles.length; i++) {
		// TableColumn column = new TableColumn(resultTable, SWT.NONE);
		// column.setText(titles[i]);
		// }
		// }
		// if ((useGeneSymbol.getSelection() || useGeneDavidID.getSelection() || useGeneEntrezGeneID.getSelection()
		// || useGeneName.getSelection() || useGeneRefSeqID.getSelection())) {
		//
		// horizontalSeparator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		// GridData data = new GridData(GridData.FILL_HORIZONTAL);
		// data.heightHint = 20;
		// horizontalSeparator.setLayoutData(data);
		// }

		// composite.pack();
		composite.layout();
	}

	private void searchForGene(final String searchQuery) {

		// Flush old pathway results
		// for (TableItem item : resultTable.getItems())
		// item.dispose();
		//
		// Pattern pattern = Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE);
		// Matcher regexMatcher;
		// ArrayList<Integer> davidGeneResults = new ArrayList<Integer>();
		//
		// if (useGeneSymbol.getSelection()) {
		// for (Object sGeneSymbol : geneIDMappingManager.getMap(MappingType.getType(geneSymbolIDType, davidIDType))
		// .keySet()) {
		// regexMatcher = pattern.matcher((String) sGeneSymbol);
		// if (regexMatcher.find())
		// davidGeneResults.add((Integer) geneIDMappingManager.getID(geneSymbolIDType, davidIDType,
		// sGeneSymbol));
		// }
		// }
		//
		// if (useGeneEntrezGeneID.getSelection()) {
		// for (Object entrezGeneID : geneIDMappingManager.getMap(MappingType.getType(entrez, davidIDType)).keySet()) {
		// regexMatcher = pattern.matcher(entrezGeneID.toString());
		// if (regexMatcher.find())
		// davidGeneResults.add((Integer) geneIDMappingManager.getID(entrez, davidIDType, entrezGeneID));
		// }
		// }
		//
		// if (useGeneRefSeqID.getSelection()) {
		// for (Object refSeqMrna : geneIDMappingManager.getMap(MappingType.getType(refseqMrnaIDTYpe, davidIDType))
		// .keySet()) {
		// regexMatcher = pattern.matcher((String) refSeqMrna);
		// if (regexMatcher.find())
		// davidGeneResults.addAll((Collection<? extends Integer>) geneIDMappingManager.getID(
		// refseqMrnaIDTYpe, davidIDType, refSeqMrna));
		// }
		// }
		//
		// if (useGeneName.getSelection()) {
		// for (Object geneName : geneIDMappingManager.getMap(MappingType.getType(geneNameIDType, davidIDType))
		// .keySet()) {
		// regexMatcher = pattern.matcher((String) geneName);
		// if (regexMatcher.find())
		// davidGeneResults.add((Integer) geneIDMappingManager.getID(geneNameIDType, davidIDType, geneName));
		// }
		// }
		//
		// // Fill results in table
		// for (Integer davidID : davidGeneResults) {
		// String sRefSeqIDs = "";
		//
		// try {
		// for (Object refSeqID : geneIDMappingManager.<Integer, Set<Object>> getID(davidIDType, refseqMrnaIDTYpe,
		// davidID)) {
		// sRefSeqIDs += refSeqID + " ";
		// }
		// } catch (NullPointerException npe) {
		// sRefSeqIDs = "<No Mapping>";
		// }
		//
		// String entrezGeneID = "";
		// Integer iEntrezGeneID = geneIDMappingManager.getID(davidIDType, entrez, davidID);
		// if (iEntrezGeneID == null)
		// entrezGeneID = "<No Mapping>";
		// else
		// entrezGeneID = iEntrezGeneID.toString();
		//
		// String geneSymbol = geneIDMappingManager.getID(davidIDType, geneSymbolIDType, davidID);
		// if (geneSymbol == null)
		// geneSymbol = "<Unknown>";
		//
		// String geneName = geneIDMappingManager.getID(davidIDType, geneNameIDType, davidID);
		// if (geneName == null)
		// geneName = "<Unknown>";
		//
		// ArrayList<String> foundInDataSet = new ArrayList<String>(geneticDataDomains.size());
		//
		// for (GeneticDataDomain geneticDataDomain : geneticDataDomains) {
		// Set<Integer> expressionIndices = geneIDMappingManager.getIDAsSet(davidIDType,
		// geneticDataDomain.getGeneIDType(), davidID);
		// if (expressionIndices != null && expressionIndices.size() > 0) {
		// foundInDataSet.add("Found");
		// } else
		// foundInDataSet.add("Not found");
		// }
		//
		// // Determine whether the gene has a valid expression value in the
		// // current data set
		//
		// // h.getExpressionIndicesFromDavid(iDavidID);
		//
		// TableItem item = new TableItem(resultTable, SWT.NULL);
		// for (int dataDomainCount = 0; dataDomainCount < geneticDataDomains.size(); dataDomainCount++) {
		// item.setText(dataDomainCount, foundInDataSet.get(dataDomainCount));
		// }
		// int nrDataDomains = geneticDataDomains.size();
		// item.setText(nrDataDomains, sRefSeqIDs);
		// item.setText(nrDataDomains + 1, Integer.toString(davidID));
		// item.setText(nrDataDomains + 2, entrezGeneID);
		// item.setText(nrDataDomains + 3, geneSymbol);
		// item.setText(nrDataDomains + 4, geneName);
		//
		// item.setData(davidID);
		// }

		// Sort gene table using the info whether expression data is available
		// or not.
		// sortTable(resultTable, 0);
		//
		// // Highlight content if it matches the search query
		// for (int iIndex = 0; iIndex < resultTable.getColumnCount(); iIndex++) {
		// for (TableItem item : resultTable.getItems()) {
		// regexMatcher = pattern.matcher(item.getText(iIndex));
		// if (regexMatcher.find()) {
		// item.setBackground(iIndex, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		// }
		// }
		// }
		//
		// for (TableColumn column : resultTable.getColumns())
		// column.pack();
		//
		// resultTable.getColumn(2).setWidth(100);
		// geneTable.getColumn(4).setWidth(200);
		// geneTable.getColumn(1).setWidth(300);
	}

	private void addGeneContextMenu() {

		// final Menu menu = new Menu(resultTable.getShell(), SWT.POP_UP);
		// resultTable.setMenu(menu);
		//
		// menu.addListener(SWT.Show, new Listener() {
		// @Override
		// public void handleEvent(Event event) {
		// MenuItem[] menuItems = menu.getItems();
		// for (int i = 0; i < menuItems.length; i++) {
		// menuItems[i].dispose();
		// }
		//
		// for (final TableItem tableItem : resultTable.getSelection()) {
		//
		// // for (int dataDomainCount = 0; dataDomainCount < geneticDataDomains.size(); dataDomainCount++) {
		// // // Do not create context menu for genes that have to
		// // // expression value
		// //
		// // if (tableItem.getText(dataDomainCount).equalsIgnoreCase("FOUND")) {
		// // createContextMenuItemsForDataDomain(menu, tableItem,
		// // geneticDataDomains.get(dataDomainCount));
		// // } else {
		// // MenuItem openInBrowserMenuItem = new MenuItem(menu, SWT.PUSH);
		// // openInBrowserMenuItem.setText("Not loaded");
		// // }
		// // if (dataDomainCount + 1 < geneticDataDomains.size())
		// // new MenuItem(menu, SWT.SEPARATOR);
		// //
		// // }
		// }
		// }
		//
		// });
	}

	private void createContextMenuItemsForDataDomain(Menu menu, final TableItem tableItem,
			final GeneticDataDomain dataDomain) {

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

		// makeCategoryOfGene.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// new CategoricalTablePerspectiveCreator().createTablePerspeciveByRowID(dataDomain,
		// (Integer) tableItem.getData(), davidIDType, false);
		//
		// DataDomainUpdateEvent event = new DataDomainUpdateEvent(dataDomain);
		// event.setSender(this);
		// GeneralManager.get().getEventPublisher().triggerEvent(event);
		//
		// // Switch to DVI view
		// try {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
		// .showView("org.caleydo.view.dvi");
		// } catch (PartInitException e1) {
		// e1.printStackTrace();
		// }
		// }
		// });

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

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSearchView();
		determineDataConfiguration(serializedView);
	}
}
