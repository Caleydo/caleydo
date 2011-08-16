package org.caleydo.view.pathway.toolbar;

import java.util.Collection;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.util.SearchBox;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Drop down style toolbar-contribution to select pathway.
 * 
 * @author Marc Streit
 */
public class PathwaySearchBox extends ControlContribution implements IToolBarItem {

	public static final int TOOLBAR_WIDTH = 700;

	/** mediator to handle actions triggered by the contributed element */
	PathwayToolBarMediator pathwayToolBarMediator;

	/**
	 * constructor as requested by ControlContribution
	 * 
	 * @param str
	 */
	public PathwaySearchBox(String str) {
		super(str);
	}

	@Override
	protected Control createControl(Composite parent) {

		final SearchBox pathwaySearchBox = new SearchBox(parent, SWT.BORDER);

		String items[] = { "No pathways available!" };
		pathwaySearchBox.setItems(items);
		pathwaySearchBox.setTextLimit(90);

		if (!GeneralManager.get().getPreferenceStore()
				.getString(PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES)
				.isEmpty()) {
			pathwaySearchBox.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					if (GeneralManager
							.get()
							.getPreferenceStore()
							.getString(
									PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES)
							.isEmpty()) {
						pathwaySearchBox.setEnabled(false);
						return;
					}

					Collection<PathwayGraph> allPathways = PathwayManager.get()
							.getAllItems();
					String[] searchItems = new String[allPathways.size()];
					int iIndex = 0;
					String sPathwayTitle = "";
					for (PathwayGraph pathway : allPathways) {
						sPathwayTitle = pathway.getTitle();

						// if (sPathwayTitle.length() >
						// MAX_PATHWAY_TITLE_LENGTH)
						// sPathwayTitle = sPathwayTitle.substring(0,
						// MAX_PATHWAY_TITLE_LENGTH) + "... ";

						// sArSearchItems[iIndex] = pathway.getType().toString()
						// + " - " + sPathwayTitle;

						searchItems[iIndex] = sPathwayTitle + " ("
								+ pathway.getType().getName() + ")";
						iIndex++;
					}

					pathwaySearchBox.setItems(searchItems);
					pathwaySearchBox.removeFocusListener(this);
				}
			});
		} else {
			// pathwaySearchLabel.setEnabled(false);
			pathwaySearchBox.setEnabled(false);
		}

		pathwaySearchBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sSearchEntity = pathwaySearchBox.getItem(pathwaySearchBox
						.getSelectionIndex());
				// sSearchEntity = sSearchEntity.substring(0,
				// sSearchEntity.indexOf(" ("));

				loadPathway(sSearchEntity);
			}
		});

		// pathwaySearchBox.addKeyListener(new KeyAdapter() {
		// @Override
		// public void keyReleased(KeyEvent e) {
		//
		// if (e.character != SWT.CR)
		// return;
		//
		// String sSearchEntity =
		// pathwaySearchBox.getItem(pathwaySearchBox.getSelectionIndex());
		// loadPathway(sSearchEntity);
		// }
		// });

		return pathwaySearchBox;
	}

	@Override
	protected int computeWidth(Control control) {

			return TOOLBAR_WIDTH;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}

	/**
	 * Method gets a pathway title and tries to determine the pathway ID. If
	 * this is successful the load pathway event is triggered.
	 * 
	 * @param entity
	 *            Pathway search title
	 * @return
	 */
	private boolean loadPathway(String entity) {
		EPathwayDatabaseType ePathwayDatabaseType;

		if (entity.contains(EPathwayDatabaseType.KEGG.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.KEGG;
		} else if (entity.contains(EPathwayDatabaseType.BIOCARTA.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.BIOCARTA;
		} else
			return false;

		entity = entity.substring(0, entity.indexOf(" ("));

		PathwayGraph pathway = PathwayManager.get().searchPathwayByName(entity,
				ePathwayDatabaseType);

		if (pathway == null)
			return false;

		pathwayToolBarMediator.loadPathway(pathway);

		ChangeURLEvent event = new ChangeURLEvent();
		event.setSender(this);
		event.setUrl(pathway.getExternalLink());
		GeneralManager.get().getEventPublisher().triggerEvent(event);

		return true;
	}
}
