package org.caleydo.view.pathway.toolbar;

import java.util.Collection;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.browser.ChangeURLEvent;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.rcp.util.search.SearchBox;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * Drop down style toolbar-contribution to select pathway.
 * 
 * @author Marc Streit
 */
public class PathwaySearchBox extends ControlContribution implements
		IToolBarItem {

	public static final int TOOLBAR_WIDTH = 173;

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
		pathwaySearchBox.setTextLimit(21);

		if (!GeneralManager.get().getPreferenceStore().getString(
				PreferenceConstants.LAST_CHOSEN_PATHWAY_DATA_SOURCES).isEmpty()) {
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

					Collection<PathwayGraph> allPathways = PathwayManager.get().getAllItems();
					String[] sArSearchItems = new String[allPathways.size()];
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

						sArSearchItems[iIndex] = sPathwayTitle + " ("
								+ pathway.getType().getName() + ")";
						iIndex++;
					}

					pathwaySearchBox.setItems(sArSearchItems);
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
				String sSearchEntity = pathwaySearchBox
						.getItem(pathwaySearchBox.getSelectionIndex());
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

		Rectangle rectDisplay = Display.getCurrent().getClientArea();
		float fRatio = (float) rectDisplay.width / rectDisplay.height;

		// is widescreen setup
		if (fRatio > 1.35) {
			return TOOLBAR_WIDTH - 25;
		}

		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}

	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(
			PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}

	/**
	 * Method gets a pathway title and tries to determine the pathway ID. If
	 * this is successful the load pathway event is triggered.
	 * 
	 * @param sEntity
	 *            Pathway search title
	 * @return
	 */
	private boolean loadPathway(String sEntity) {
		EPathwayDatabaseType ePathwayDatabaseType;

		if (sEntity.contains(EPathwayDatabaseType.KEGG.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.KEGG;
		} else if (sEntity.contains(EPathwayDatabaseType.BIOCARTA.getName())) {
			ePathwayDatabaseType = EPathwayDatabaseType.BIOCARTA;
		} else
			return false;

		sEntity = sEntity.substring(0, sEntity.indexOf(" ("));

		PathwayGraph pathway = PathwayManager.get()
				.searchPathwayByName(sEntity, ePathwayDatabaseType);

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
