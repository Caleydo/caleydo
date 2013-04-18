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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;

/**
 * Search view for any id in the system
 *
 * @author Marc Streit and Samuel Gratzl
 */
public final class RcpSearchView extends CaleydoRCPViewPart {
	public static final String VIEW_TYPE = "org.caleydo.view.search";

	private Composite root;

	/**
	 * text select for the query
	 */
	private Text searchText;
	/**
	 * set of checkbox buttons for different id typess
	 */
	private final List<Button> searchWithinIDType = new ArrayList<>();
	/**
	 * container for all the results
	 */
	private Composite results;

	/**
	 * {@link #results} {@link ScrolledComposite}
	 */
	private ScrolledComposite resultsScrolled;

	/**
	 * marker decoration for nothing found
	 */
	private ControlDecoration nothingFound;

	private static final Comparator<ILabelHolder> byLabel = new Comparator<ILabelHolder>() {
		@Override
		public int compare(ILabelHolder o1, ILabelHolder o2) {
			return String.CASE_INSENSITIVE_ORDER.compare(o1.getLabel(), o2.getLabel());
		}
	};

	@Override
	public void createPartControl(Composite parent) {
		this.root = new Composite(parent, SWT.NONE);
		root.setLayout(new GridLayout(1, false));

		createSearchGroup(root);
		createFilterGroup(root);

		this.resultsScrolled = new ScrolledComposite(root, SWT.H_SCROLL | SWT.V_SCROLL);
		resultsScrolled.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		resultsScrolled.setExpandVertical(true);
		resultsScrolled.setExpandHorizontal(true);

		results = new Composite(resultsScrolled, SWT.NONE);
		results.setLayout(new GridLayout(1, false));

		resultsScrolled.setContent(results);

		root.layout();
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
				e.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						System.out.println("gained");
						searchText.selectAll();
					}
				});
			}
		});

		final Button searchButton = new Button(group, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.setEnabled(false);

		final ControlDecoration dec = new ControlDecoration(searchText, SWT.TOP | SWT.LEFT);
		dec.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_WARNING));
		dec.setShowOnlyOnFocus(true);
		dec.hide();

		this.nothingFound = new ControlDecoration(searchText, SWT.TOP | SWT.LEFT);
		nothingFound.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
		nothingFound.setShowOnlyOnFocus(false);
		nothingFound.setDescriptionText("No Entries were found matching your query");
		nothingFound.hide();

		searchText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (searchText.getText().length() >= 3) {
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
		group.setLayout(new RowLayout(SWT.VERTICAL));
		group.setText("Search filter");

		// split in multiple idtypes and single ones
		List<Collection<IDType>> multiple = new ArrayList<>();
		List<IDType> single = new ArrayList<>();

		for (IDCategory cat : IDCategory.getAllRegisteredIDCategories()) {
			List<IDType> publics = cat.getPublicIdTypes();
			if (publics.isEmpty()) continue;
			if (publics.size() == 1)
				single.add(publics.get(0));
			else
				multiple.add(publics);
		}
		// the more the better for multiple
		Collections.sort(multiple, new Comparator<Collection<IDType>>() {
			@Override
			public int compare(Collection<IDType> o1, Collection<IDType> o2) {
				int r = o2.size() - o1.size();
				if (r == 0) {
					// same size use the name
					return String.CASE_INSENSITIVE_ORDER.compare(
							o1.iterator().next().getIDCategory().getCategoryName(), o2.iterator().next()
									.getIDCategory().getCategoryName());
				}
				return r;
			}
		});
		for (Collection<IDType> types : multiple) {
			Composite row = new Composite(group, SWT.NONE);
			row.setLayout(new RowLayout());

			// create a select all/none category button
			final IDCategory cat = types.iterator().next().getIDCategory();
			final Button b = new Button(row, SWT.CHECK);
			b.setText(cat.getCategoryName());
			b.setSelection(true); // by default search all
			//a button for selecting all/none of a category
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					boolean selected = b.getSelection();
					for(Button btype : searchWithinIDType) {
						if (((IDType)btype.getData()).getIDCategory() == cat)
							btype.setSelection(selected);
					}
					super.widgetSelected(e);
				}
			});
			Label l = new Label(row, SWT.NONE);
			l.setText(": ");

			for (IDType type : types) {
				this.searchWithinIDType.add(createCheckIDType(row, type));
			}
		}

		Collections.sort(single, new Comparator<IDType>() {
			@Override
			public int compare(IDType o1, IDType o2) {
				return String.CASE_INSENSITIVE_ORDER.compare(
							o1.getTypeName(),o2.getTypeName());
			}
		});
		{
			Composite row = new Composite(group, SWT.NONE);
			row.setLayout(new RowLayout());
			for (IDType type : single) {
				this.searchWithinIDType.add(createCheckIDType(row, type));
			}
		}
		group.pack();
	}

	private Button createCheckIDType(Composite group, IDType type) {
		Button b = new Button(group, SWT.CHECK);
		b.setText(type.getTypeName());
		b.setData(type);
		b.setSelection(true); // by default search all
		return b;
	}

	private void search(String query) {
		// delete old results
		for (Control c : results.getChildren())
			c.dispose();

		com.google.common.collect.Table<IDCategory, IDType, Set<?>> result = searchImpl(query);

		if (result.isEmpty()) {
			nothingFound.show();
			nothingFound.showHoverText("No Entries were found matching your query");
			return;
		} else {
			nothingFound.hide();
			// order by number of hits
			List<Map.Entry<IDCategory, Map<IDType, Set<?>>>> entries = new ArrayList<>(result.rowMap().entrySet());
			Collections.sort(entries, new Comparator<Map.Entry<IDCategory, Map<IDType, Set<?>>>>() {
				@Override
				public int compare(Entry<IDCategory, Map<IDType, Set<?>>> o1, Entry<IDCategory, Map<IDType, Set<?>>> o2) {
					return o1.getValue().size() - o2.getValue().size();
				}
			});

			// create table per category
			for (Map.Entry<IDCategory, Map<IDType, Set<?>>> entry : entries) {
				createResultTable(results, entry.getKey(), entry.getValue());
			}
		}

		// update layouts
		results.layout();
		resultsScrolled.setMinSize(results.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * implements the search logic
	 *
	 * @param query
	 * @return a table containing all matching id types and their matching ids
	 */
	private com.google.common.collect.Table<IDCategory, IDType, Set<?>> searchImpl(String query) {
		// compile to regex and create a predicate out of it
		final Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
		Predicate<Object> searchQuery = new Predicate<Object>() {
			@Override
			public boolean apply(Object in) {
				return in != null && pattern.matcher(in.toString()).matches();
			}
		};

		com.google.common.collect.Table<IDCategory, IDType, Set<?>> result = HashBasedTable.create();

		for (Button b : searchWithinIDType) {
			if (!b.getSelection()) // not selected skip
				continue;

			IDType idType = (IDType) b.getData();

			// find all ids and check the predicate
			IDMappingManager mappingManager = IDMappingManagerRegistry.get()
					.getIDMappingManager(idType.getIDCategory());

			Set<?> ids = mappingManager.getAllMappedIDs(idType);
			ids = new HashSet<>(Sets.filter(ids, searchQuery));
			if (ids.isEmpty())
				continue;
			result.put(idType.getIDCategory(), idType, ids);
		}
		return result;
	}

	/**
	 * creates out of search result a swt table
	 *
	 * @param composite
	 *            parent
	 * @param category
	 *            the theme of the table
	 * @param foundIdTypes
	 *            all idtypes and ids of this category
	 * @return the root element created
	 */
	private Group createResultTable(Composite composite, IDCategory category, final Map<IDType, Set<?>> foundIdTypes) {
		Group group = new Group(results, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		group.setLayout(new GridLayout(1, true));
		group.setText(category.getCategoryName());

		TableViewer viewer = new TableViewer(group, SWT.BORDER | SWT.FULL_SELECTION | SWT.VIRTUAL);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		// get a list of relevant datadomains i.e. perspectives
		List<Perspective> perspectives = findRelevantPerspectives(category);

		// convert the abstract data to result rows
		List<ResultRow> rows = createResultRows(category, foundIdTypes, perspectives);

		viewer.setInput(rows);

		// add columns for every perspective
		final Color ddColor = Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_BACKGROUND);
		for (final Perspective perspective : perspectives) {
			TableViewerColumn col = createTableColumn(viewer, perspective.getDataDomain().getLabel());
			col.getColumn().setAlignment(SWT.CENTER);
			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					ResultRow row = (ResultRow) element;
					Object id = row.get(perspective.getIdType());
					return id == null ? "Not Found" : "Found";
				}

				@Override
				public Color getBackground(Object element) {
					return ddColor;
				}
			});
		}

		// lets order the IDTypes according to their name
		List<IDType> types = new ArrayList<>(category.getPublicIdTypes());
		Collections.sort(types, new Comparator<IDType>() { // by name
			@Override
			public int compare(IDType o1, IDType o2) {
				return String.CASE_INSENSITIVE_ORDER.compare(o1.getTypeName(), o2.getTypeName());
			}
		});

		// add columns for every public type
		for (final IDType type : types) {
			TableViewerColumn col = createTableColumn(viewer, type.getTypeName());

			col.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					ResultRow row = (ResultRow) element;
					Object id = row.get(type);
					return id == null ? "<Not Mapped>" : id.toString();
				}

				@Override
				public Color getBackground(Object element) {
					ResultRow row = (ResultRow) element;
					boolean found = row.wasFound(type);
					return found ? Display.getCurrent().getSystemColor(SWT.COLOR_GRAY) : null;
				}
			});
		}

		return group;
	}


	/**
	 * converts the abstract search results into a list of {@link ResultRow}s
	 *
	 * @param category
	 * @param foundIdTypes
	 *            search hits
	 * @param perspectives
	 *            the relevant perspectives for preparing its data
	 * @return
	 */
	private List<ResultRow> createResultRows(IDCategory category, Map<IDType, Set<?>> foundIdTypes,
			List<Perspective> perspectives) {

		Map<Object, ResultRow> result = new TreeMap<>();

		final IDType primary = category.getPrimaryMappingType();
		final IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(category);

		// first convert every found Id type to its primary value
		for (Map.Entry<IDType, Set<?>> entry : foundIdTypes.entrySet()) {
			IDType idType = entry.getKey();
			IIDTypeMapper<Object, Object> mapper = idMappingManager.getIDTypeMapper(idType, primary);

			for (Object id : entry.getValue()) {
				Set<Object> pids = mapper.apply(id);
				for (Object pid : pids) {
					if (!result.containsKey(pid))
						result.put(pid, new ResultRow(primary, pid));
					result.get(pid).set(idType, id, true);
				}
			}
		}

		List<ResultRow> data = new ArrayList<>(result.values());

		//fill out all missing values
		for(IDType idType : category.getPublicIdTypes()) {
			addMissing(primary, idMappingManager, data, idType);
		}
		for (Perspective per : perspectives) {
			addMissing(primary, idMappingManager, data, per.getIdType());
		}
		return data;
	}

	private void addMissing(final IDType primary, final IDMappingManager idMappingManager, List<ResultRow> data,
			IDType idType) {
		IIDTypeMapper<Object, Object> mapper = null;
		for (ResultRow row : data) {
			if (row.has(idType)) // already there
				continue;
			if (mapper == null) {// lazy for better performance
				mapper = idMappingManager.getIDTypeMapper(primary, idType);
				if (mapper == null) // nothing to map
					return;
			}
			Set<Object> ids = mapper.apply(row.getPrimaryId());
			row.set(idType, ids);
		}
	}

	private TableViewerColumn createTableColumn(TableViewer viewer, String name) {
		TableViewerColumn col = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn coll = col.getColumn();
		coll.setText(name);
		coll.setMoveable(true);
		coll.setResizable(true);
		coll.setWidth(100);
		return col;
	}

	/**
	 * find the relevant perspectives that have an {@link IDType} of the given {@link IDCategory}
	 *
	 * @param category
	 * @return
	 */
	private List<Perspective> findRelevantPerspectives(IDCategory category) {
		List<ATableBasedDataDomain> dataDomains = new ArrayList<>(DataDomainManager.get().getDataDomainsByType(
				ATableBasedDataDomain.class));

		List<Perspective> dataDomainPerspectives = new ArrayList<>(dataDomains.size());
		for (ATableBasedDataDomain dd : dataDomains) {
			if (dd.getRecordIDCategory() == category)
				dataDomainPerspectives.add(dd.getTable().getDefaultRecordPerspective());

			if (dd.getDimensionIDCategory() == category)
				dataDomainPerspectives.add(dd.getTable().getDefaultDimensionPerspective());
		}
		Collections.sort(dataDomainPerspectives, byLabel);
		return dataDomainPerspectives;
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

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSearchView();
		determineDataConfiguration(serializedView);
	}
}
