/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.search.internal;

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
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.SelectionUpdateEvent;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.view.search.api.ISearchResultActionFactory;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
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
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.swt.widgets.TableColumn;
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
	private static final String EXTENSION_POINT = "org.caleydo.view.search.SearchResultActionFactory";

	public static final String VIEW_TYPE = "org.caleydo.view.search";

	private final static Logger log = Logger.create(RcpSearchView.class);

	private Composite root;

	/**
	 * text select for the query
	 */
	private Text searchText;

	private Button regexSearch;

	private Button caseSensitive;
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

	private final Collection<ISearchResultActionFactory> actionFactories = ExtensionUtils.findImplementation(
			EXTENSION_POINT, "class", ISearchResultActionFactory.class);

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
		resultsScrolled.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_BLUE));

		results = new Composite(resultsScrolled, SWT.NONE);
		results.setLayout(new GridLayout(1, false));

		resultsScrolled.setContent(results);

		root.layout();
	}

	private void createSearchGroup(Composite composite) {
		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new RowLayout(SWT.VERTICAL));
		group.setText("Search query");

		Composite row = new Composite(group, SWT.NONE);
		row.setLayout(new RowLayout());

		searchText = new Text(row, SWT.BORDER | SWT.SINGLE);
		searchText.setLayoutData(new RowData(550, 20));
		searchText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				e.display.asyncExec(new Runnable() {
					@Override
					public void run() {
						searchText.selectAll();
					}
				});
			}
		});

		final Button searchButton = new Button(row, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.setEnabled(false);

		final ControlDecoration dec = new ControlDecoration(searchText, SWT.TOP | SWT.LEFT);
		dec.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_WARNING));
		dec.setShowOnlyOnFocus(true);
		dec.setDescriptionText("You have to enter at least 3 characters");
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
					dec.showHoverText("You have to enter at least 3 characters");
					searchButton.setEnabled(false);
				}
			}
		});

		searchButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				search(searchText.getText(), caseSensitive.getSelection(), regexSearch.getSelection());
			}
		});
		searchText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR: {
					if (searchButton.isEnabled())
						search(searchText.getText(), caseSensitive.getSelection(), regexSearch.getSelection());
					break;
				}
				}
			}
		});


		row = new Composite(group, SWT.NONE);
		row.setLayout(new RowLayout());

		caseSensitive = new Button(row, SWT.CHECK);
		caseSensitive.setText("Case sensitive");

		regexSearch = new Button(row, SWT.CHECK);
		regexSearch.setText("Regular Expression");

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

	/**
	 * search implementation of the given query
	 *
	 * @param query
	 * @param caseSensitive
	 * @param regexSearch
	 */
	private void search(String query, boolean caseSensitive, boolean regexSearch) {
		// delete old results
		deleteOldSearchResult(true);

		com.google.common.collect.Table<IDCategory, IDType, Set<?>> result = searchImpl(toPattern(query, caseSensitive,
				regexSearch));

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

	protected void deleteOldSearchResult(boolean dispose) {
		for (Control c : results.getChildren()) {
			SelectionTriggerListener lisener = (SelectionTriggerListener) c.getData();
			if (lisener != null)
				lisener.cleanUp();
			if (dispose)
				c.dispose();
		}
	}

	/**
	 * converts the query with the given flags into a pattern
	 *
	 * @return
	 */
	private static Pattern toPattern(String query, boolean caseSensitive, boolean regexSearch) {
		if (!regexSearch)
			query = starToRegex(query);
		return Pattern.compile(query, caseSensitive ? 0 : Pattern.CASE_INSENSITIVE);
	}

	/**
	 * replaces the * wildcard notation to a regex
	 *
	 * @param query
	 * @return
	 */
	private static String starToRegex(String query) {
		return "\\Q" + query.replace("*", "\\E.*\\Q") + "\\E";
	}

	/**
	 * implements the search logic
	 *
	 * @param query
	 * @param regexSearch
	 * @param caseSensitive
	 * @return a table containing all matching id types and their matching ids
	 */
	private com.google.common.collect.Table<IDCategory, IDType, Set<?>> searchImpl(final Pattern pattern) {
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
		final GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
		group.setLayoutData(gd);
		group.setLayout(new FillLayout());
		group.setText(category.getCategoryName());

		final TableViewer viewer = new TableViewer(group, SWT.FULL_SELECTION | SWT.BORDER | SWT.VIRTUAL | SWT.NO_SCROLL);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		SelectionTriggerListener listener = new SelectionTriggerListener(category);
		group.setData(listener);
		viewer.addSelectionChangedListener(listener);

		// get a list of relevant datadomains i.e. perspectives
		List<Perspective> perspectives = findRelevantPerspectives(category);

		// convert the abstract data to result rows
		List<ResultRow> rows = createResultRows(category, foundIdTypes, perspectives);

		viewer.setInput(rows);

		// add columns for every perspective
		final Color ddColor = new Color(Display.getCurrent(), 240, 240, 240);

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

		// TableColumnLayout layout = new TableColumnLayout();
		// viewer.getTable().setLayout(layout);

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
			// col.getColumn().pack();
			// layout.setColumnData(col.getColumn(), new ColumnWeightData(col.getColumn().getWidth()));
		}

		createContextMenu(viewer, perspectives);

		// // Pack the columns
		// for (int i = 0, n = viewer.getTable().getColumnCount(); i < n; i++) {
		// viewer.getTable().getColumn(i).pack();
		// }
		return group;
	}

	private void createContextMenu(final TableViewer viewer, final List<Perspective> perspectives) {
		final MenuManager mgr = new MenuManager();
		mgr.setRemoveAllWhenShown(true);
		mgr.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
				if (!selection.isEmpty()) {
					ResultRow row = (ResultRow) selection.getFirstElement();
					// create a custom context menu for the row
					createContextMenuActions(mgr, row, perspectives);
				}
			}
		});

		viewer.getTable().setMenu(mgr.createContextMenu(viewer.getTable()));
	}


	protected void createContextMenuActions(MenuManager mgr, ResultRow row, List<Perspective> perspectives) {
		boolean any = false;
		for (ISearchResultActionFactory factory : actionFactories) {
			any = factory.createPerspectiveActions(mgr, row, perspectives) || any;
		}
		if (any) // add a separator if we added any
			mgr.add(new Separator());

		for (ISearchResultActionFactory factory : actionFactories) {
			factory.createIDTypeActions(mgr, row);
		}
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
				if (pids == null) {
					log.warn("can't map " + id + " of " + idType + " to its primary it type: " + primary);
					continue;
				}
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

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedSearchView();
		determineDataConfiguration(serializedView);
	}

	private static class SelectionTriggerListener implements ISelectionChangedListener {
		private final SelectionManager selectionManager;

		public SelectionTriggerListener(IDCategory category) {
			this.selectionManager = new SelectionManager(category.getPrimaryMappingType());
		}

		public void cleanUp() {
			selectionManager.clearSelection(SelectionType.SELECTION);
			trigger(selectionManager.getDelta());
			selectionManager.unregisterEventListeners();
		}

		@Override
		protected void finalize() throws Throwable {
			selectionManager.unregisterEventListeners();
			super.finalize();
		}

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			selectionManager.clearSelection(SelectionType.SELECTION);
			if (!selection.isEmpty()) {
				for (Object obj : selection.toList()) {
					ResultRow r = (ResultRow) obj;
					selectionManager.addToType(SelectionType.SELECTION, (Integer) r.getPrimaryId());
				}
			}
			trigger(selectionManager.getDelta());
		}

		/**
		 * @param delta
		 */
		private void trigger(SelectionDelta delta) {
			SelectionUpdateEvent event = new SelectionUpdateEvent();
			event.setSender(this);
			event.setSelectionDelta(delta);
			EventPublisher.trigger(event);
		}

	}
}
