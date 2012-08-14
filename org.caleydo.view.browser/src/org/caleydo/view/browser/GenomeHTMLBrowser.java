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
package org.caleydo.view.browser;

import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.SelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.data.selection.events.ISelectionUpdateHandler;
import org.caleydo.core.data.selection.events.SelectionUpdateListener;
import org.caleydo.core.event.view.tablebased.SelectionUpdateEvent;
import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.object.ManagedObjectType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.specialized.Organism;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Special HTML browser for genomics use case.
 * 
 * @author Marc Streit
 */
public class GenomeHTMLBrowser extends HTMLBrowser implements ISelectionUpdateHandler {

	// private GeneticDataDomain dataDomain;

	private BrowserQueryType browserQueryType = BrowserQueryType.KEGG_HomoSapiens;

	private SelectionUpdateListener selectionUpdateListener;

	private Combo comboQueryIDType;

	private IDType sourceIDType;
	private Integer sourceID;
	private IDCategory geneIDCategory;

	/**
	 * Constructor.
	 */
	public GenomeHTMLBrowser(Composite parentComposite) {

		super(GeneralManager.get().getIDCreator()
				.createID(ManagedObjectType.VIEW_SWT_BROWSER_GENOME), parentComposite);

		geneIDCategory = IDCategory.getIDCategory(EGeneIDTypes.GENE.name());
		registerEventListeners();

	}

	@Override
	public void draw() {

		super.draw();

		final Combo comboQueryDatabaseType = new Combo(subContributionComposite,
				SWT.READ_ONLY);
		comboQueryIDType = new Combo(subContributionComposite, SWT.READ_ONLY);

		String storedDatabase = generalManager.getPreferenceStore().getString(
				PreferenceConstants.BROWSER_QUERY_DATABASE);

		browserQueryType = BrowserQueryType.valueOf(storedDatabase);

		comboQueryDatabaseType.add(BrowserQueryType.EntrezGene.getTitle());
		if (browserQueryType == BrowserQueryType.EntrezGene)
			comboQueryDatabaseType.select(0);

		comboQueryDatabaseType.add(BrowserQueryType.PubMed.getTitle());
		if (browserQueryType == BrowserQueryType.PubMed)
			comboQueryDatabaseType.select(1);

		comboQueryDatabaseType.add(BrowserQueryType.GeneCards.getTitle());
		if (browserQueryType == BrowserQueryType.GeneCards)
			comboQueryDatabaseType.select(2);

		Organism organism = generalManager.getBasicInfo().getOrganism();
		if (organism == Organism.HOMO_SAPIENS) {
			// comboQueryDatabaseType.add(BrowserQueryType.Ensembl_HomoSapiens.getTitle());
			// if (browserQueryType == BrowserQueryType.Ensembl_HomoSapiens)
			// comboQueryDatabaseType.select(3);

			comboQueryDatabaseType.add(BrowserQueryType.KEGG_HomoSapiens.getTitle());
			if (browserQueryType == BrowserQueryType.KEGG_HomoSapiens)
				comboQueryDatabaseType.select(4);

			comboQueryDatabaseType.add(BrowserQueryType.BioCarta_HomoSapiens.getTitle());
			if (browserQueryType == BrowserQueryType.BioCarta_HomoSapiens)
				comboQueryDatabaseType.select(5);

		} else if (organism == Organism.MUS_MUSCULUS) {
			// comboQueryDatabaseType.add(BrowserQueryType.Ensembl_MusMusculus.getTitle());
			// if (browserQueryType == BrowserQueryType.Ensembl_MusMusculus)
			// comboQueryDatabaseType.select(3);

			comboQueryDatabaseType.add(BrowserQueryType.KEGG_MusMusculus.getTitle());
			if (browserQueryType == BrowserQueryType.KEGG_MusMusculus)
				comboQueryDatabaseType.select(4);

			comboQueryDatabaseType.add(BrowserQueryType.BioCarta_MusMusculus.getTitle());
			if (browserQueryType == BrowserQueryType.BioCarta_MusMusculus)
				comboQueryDatabaseType.select(5);
		}

		comboQueryDatabaseType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String sQueryTypeTitle = comboQueryDatabaseType
						.getItem(comboQueryDatabaseType.getSelectionIndex());

				for (BrowserQueryType eBrowserQueryType : BrowserQueryType.values()) {
					if (eBrowserQueryType.getTitle().equals(sQueryTypeTitle)) {
						changeQueryType(eBrowserQueryType);
						break;
					}
				}
			}
		});

		for (String idType : browserQueryType.getQueryIDTypes()) {
			comboQueryIDType.add(idType);
			comboQueryIDType.select(0);
		}

		subContributionComposite.layout();
		subContributionComposite.getParent().layout();
	}

	@Override
	public void handleSelectionUpdate(final SelectionDelta selectionDelta) {

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!checkInternetConnection())
					return;

				for (SelectionDeltaItem selectionDeltaItem : selectionDelta) {
					if (selectionDeltaItem.getSelectionType() == SelectionType.SELECTION
							&& !selectionDeltaItem.isRemove()) {

						IDType targetIDType = IDType.getIDType(comboQueryIDType
								.getItem(comboQueryIDType.getSelectionIndex()));

						sourceIDType = selectionDelta.getIDType();
						sourceID = selectionDeltaItem.getID();

						updateURL(targetIDType);
					}
				}

				// list.redraw();
				// list.setSelection(0);
			}
		});
	}

	public void changeQueryType(BrowserQueryType eBrowserQueryType) {
		this.browserQueryType = eBrowserQueryType;
		GeneralManager
				.get()
				.getPreferenceStore()
				.setValue(PreferenceConstants.BROWSER_QUERY_DATABASE,
						eBrowserQueryType.name());

		comboQueryIDType.removeAll();
		for (String idType : browserQueryType.getQueryIDTypes()) {
			comboQueryIDType.add(idType);
			comboQueryIDType.select(0);
		}

		updateURL(IDType.getIDType(comboQueryIDType.getItem(0)));
	}

	private void updateURL(IDType targetIDType) {
		if (!sourceIDType.getIDCategory().equals(geneIDCategory))
			return;

		Set<Object> queryIDs = IDMappingManagerRegistry.get()
				.getIDMappingManager(targetIDType)
				.getIDAsSet(sourceIDType, targetIDType, sourceID);

		String sURL = "";

		if (queryIDs == null || queryIDs.size() == 0) {
			sURL = "Sorry, cannot resolve ID for selection!";
			browser.setUrl("about:blank");
		} else {
			// FIXME: only first found ID is taken - multi mappings are ignored!
			// How should we handle this?
			sURL = browserQueryType.getBrowserQueryStringPrefix()
					+ queryIDs.toArray()[0].toString();
			browser.setUrl(sURL);
		}

		textURL.setText(sURL);
		browser.update();
	}

	public BrowserQueryType getBrowserQueryType() {
		return browserQueryType;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class, selectionUpdateListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHTMLBrowserView serializedForm = new SerializedHTMLBrowserView();
		serializedForm.setViewID(getID());
		serializedForm.setQueryType(getBrowserQueryType());
		serializedForm.setUrl(getUrl());
		return serializedForm;
	}

	// @Override
	// public void setDataDomain(GeneticDataDomain dataDomain) {
	// this.dataDomain = dataDomain;
	//
	// registerEventListeners();
	// }
	//
	// @Override
	// public GeneticDataDomain getDataDomain() {
	// return dataDomain;
	// }
}
