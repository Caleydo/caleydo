package org.caleydo.rcp.views.swt.toolbar.content.pathway;

import java.util.Collection;

import org.caleydo.core.data.graph.pathway.core.PathwayGraph;
import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.manager.event.EEventType;
import org.caleydo.core.manager.event.IDListEventContainer;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.genome.pathway.EPathwayDatabaseType;
import org.caleydo.rcp.Application;
import org.caleydo.rcp.perspective.GenomePerspective;
import org.caleydo.rcp.util.search.SearchBox;
import org.caleydo.rcp.views.swt.toolbar.ToolBarView;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
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
 * @author Marc Streit
 */
public class PathwaySearchBox
	extends ControlContribution
	implements IToolBarItem {

	/** mediator to handle actions triggered by the contributed element */ 
	PathwayToolBarMediator pathwayToolBarMediator;
	
	/**
	 * constructor as requested by ControlContribution
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

		if (Application.bLoadPathwayData) {
			pathwaySearchBox.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					if (!Application.bLoadPathwayData) {
						pathwaySearchBox.setEnabled(false);
						return;
					}

					Collection<PathwayGraph> allPathways =
						GeneralManager.get().getPathwayManager().getAllItems();
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

						sArSearchItems[iIndex] = sPathwayTitle + " (" + pathway.getType().toString() + ")";
						iIndex++;
					}

					pathwaySearchBox.setItems(sArSearchItems);
					pathwaySearchBox.removeFocusListener(this);
				}
			});
		}
		else {
			// pathwaySearchLabel.setEnabled(false);
			pathwaySearchBox.setEnabled(false);
		}
		pathwaySearchBox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String sSearchEntity = pathwaySearchBox.getItem(pathwaySearchBox.getSelectionIndex());
				// sSearchEntity = sSearchEntity.substring(0,
				// sSearchEntity.indexOf(" ("));

				determinePathwayID(sSearchEntity);
			}
		});
		
		return pathwaySearchBox;
	}

	@Override
	protected int computeWidth(Control control) {
		if (GenomePerspective.bIsWideScreen)
			return ToolBarView.TOOLBAR_WIDTH - 25;
		
		return control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
	}
	
	public PathwayToolBarMediator getPathwayToolBarMediator() {
		return pathwayToolBarMediator;
	}

	public void setPathwayToolBarMediator(PathwayToolBarMediator pathwayToolBarMediator) {
		this.pathwayToolBarMediator = pathwayToolBarMediator;
	}
	
	/**
	 * Method gets a pathway title and tries to determine the pathway ID.
	 * If this is successfull the load pathway event is triggered.
	 * 
	 * @param sEntity Pathway search title
	 * @return
	 */
	private boolean determinePathwayID(String sEntity) {
		EPathwayDatabaseType ePathwayDatabaseType;

		if (sEntity.contains("KEGG")) {
			ePathwayDatabaseType = EPathwayDatabaseType.KEGG;
		}
		else if (sEntity.contains("BioCarta")) {
			ePathwayDatabaseType = EPathwayDatabaseType.BIOCARTA;
		}
		else
			return false;

		sEntity = sEntity.substring(0, sEntity.indexOf(" ("));

		int iPathwayID =
			GeneralManager.get().getPathwayManager().searchPathwayIdByName(sEntity, ePathwayDatabaseType);

		if (iPathwayID == -1)
			return false;

		IDListEventContainer<Integer> idListEventContainer =
			new IDListEventContainer<Integer>(EEventType.LOAD_PATHWAY_BY_PATHWAY_ID, EIDType.PATHWAY);
		idListEventContainer.addID(iPathwayID);

		pathwayToolBarMediator.loadPathway(iPathwayID);

		return true;
	}
}
