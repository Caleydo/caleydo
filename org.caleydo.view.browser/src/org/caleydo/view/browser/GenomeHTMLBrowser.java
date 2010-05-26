package org.caleydo.view.browser;

import java.util.ArrayList;
import java.util.Set;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.event.view.browser.EBrowserQueryType;
import org.caleydo.core.manager.event.view.storagebased.SelectionUpdateEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.manager.specialized.genetic.EOrganism;
import org.caleydo.core.manager.usecase.EDataDomain;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.listener.ISelectionUpdateHandler;
import org.caleydo.core.view.opengl.canvas.listener.SelectionUpdateListener;
import org.caleydo.datadomain.genetic.GeneticUseCase;
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
public class GenomeHTMLBrowser extends HTMLBrowser implements
		ISelectionUpdateHandler {

	private URLGenerator urlGenerator;

	private ArrayList<Integer> iAlDavidID;

	private EBrowserQueryType eBrowserQueryType = EBrowserQueryType.GeneCards;

	protected ChangeQueryTypeListener changeQueryTypeListener;
	protected SelectionUpdateListener selectionUpdateListener;

	/**
	 * Constructor.
	 */
	public GenomeHTMLBrowser(int iParentContainerID, String sLabel) {

		super(iParentContainerID, sLabel, GeneralManager.get().getIDManager()
				.createID(EManagedObjectType.VIEW_SWT_BROWSER_GENOME));

		urlGenerator = new URLGenerator();
		iAlDavidID = new ArrayList<Integer>();
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		super.initViewSWTComposite(parentComposite);

		useCase = generalManager.getUseCase(EDataDomain.GENETIC_DATA);

		final Combo queryTypeCombo = new Combo(subContributionComposite,
				SWT.READ_ONLY);

		if (useCase instanceof GeneticUseCase) {

			String storedDatabase = generalManager.getPreferenceStore()
					.getString(PreferenceConstants.BROWSER_QUERY_DATABASE);
			// if (storedDatabase == "")
			// storedDatabase = "GeneCards";
			eBrowserQueryType = EBrowserQueryType.valueOf(storedDatabase);

			queryTypeCombo.add(EBrowserQueryType.EntrezGene.getTitle());
			if (eBrowserQueryType == EBrowserQueryType.EntrezGene)
				queryTypeCombo.select(0);

			queryTypeCombo.add(EBrowserQueryType.PubMed.getTitle());
			if (eBrowserQueryType == EBrowserQueryType.PubMed)
				queryTypeCombo.select(1);

			queryTypeCombo.add(EBrowserQueryType.GeneCards.getTitle());
			if (eBrowserQueryType == EBrowserQueryType.GeneCards)
				queryTypeCombo.select(2);

			EOrganism organism = ((GeneticUseCase) useCase).getOrganism();
			if (organism == EOrganism.HOMO_SAPIENS) {
				queryTypeCombo.add(EBrowserQueryType.Ensembl_HomoSapiens
						.getTitle());
				if (eBrowserQueryType == EBrowserQueryType.Ensembl_HomoSapiens)
					queryTypeCombo.select(3);

				queryTypeCombo.add(EBrowserQueryType.KEGG_HomoSapiens
						.getTitle());
				if (eBrowserQueryType == EBrowserQueryType.KEGG_HomoSapiens)
					queryTypeCombo.select(4);

				queryTypeCombo.add(EBrowserQueryType.BioCarta_HomoSapiens
						.getTitle());
				if (eBrowserQueryType == EBrowserQueryType.BioCarta_HomoSapiens)
					queryTypeCombo.select(5);

			} else if (organism == EOrganism.MUS_MUSCULUS) {
				queryTypeCombo.add(EBrowserQueryType.Ensembl_MusMusculus
						.getTitle());
				if (eBrowserQueryType == EBrowserQueryType.Ensembl_MusMusculus)
					queryTypeCombo.select(3);

				queryTypeCombo.add(EBrowserQueryType.KEGG_MusMusculus
						.getTitle());
				if (eBrowserQueryType == EBrowserQueryType.KEGG_MusMusculus)
					queryTypeCombo.select(4);

				queryTypeCombo.add(EBrowserQueryType.BioCarta_MusMusculus
						.getTitle());
				if (eBrowserQueryType == EBrowserQueryType.BioCarta_MusMusculus)
					queryTypeCombo.select(5);
			}
		}

		queryTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String sQueryTypeTitle = queryTypeCombo.getItem(queryTypeCombo
						.getSelectionIndex());

				for (EBrowserQueryType eBrowserQueryType : EBrowserQueryType
						.values()) {
					if (eBrowserQueryType.getTitle().equals(sQueryTypeTitle)) {
						changeQueryType(eBrowserQueryType);
						break;
					}
				}
			}
		});

		subContributionComposite.layout();
		subContributionComposite.getParent().layout();
	}

	public void handleSelectionUpdate(final ISelectionDelta selectionDelta,
			boolean scrollToSelection, String info) {
		if (selectionDelta.getIDType() != EIDType.EXPRESSION_INDEX)
			return;

		// Prevent handling of non genetic entities
		if (useCase == null
				|| useCase.getDataDomain() != EDataDomain.GENETIC_DATA)
			return;

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!checkInternetConnection())
					return;

				int iItemsToLoad = 0;
				// SelectionDeltaItem selectionItem;

				for (SelectionDeltaItem selectionDeltaItem : selectionDelta) {
					if (selectionDeltaItem.getSelectionType() == SelectionType.MOUSE_OVER
							|| selectionDeltaItem.getSelectionType() == SelectionType.SELECTION) {

						// Integer iRefSeqID =
						// generalManager.getIDMappingManager().getID(EIDType.EXPRESSION_INDEX,
						// EIDType.REFSEQ_MRNA_INT,
						// selectionDeltaItem.getPrimaryID());

						int expressionIndex = selectionDeltaItem.getPrimaryID();

						// FIXME: Due to new mapping system, a mapping involving
						// expression index can return a
						// Set of
						// values, depending on the IDType that has been
						// specified when loading expression
						// data.
						// Possibly a different handling of the Set is required.
						Set<String> setRefSeqIDs = generalManager
								.getIDMappingManager().getIDAsSet(
										EIDType.EXPRESSION_INDEX,
										EIDType.REFSEQ_MRNA, expressionIndex);

						String sRefSeqID = null;
						if ((setRefSeqIDs != null && !setRefSeqIDs.isEmpty())) {
							sRefSeqID = (String) setRefSeqIDs.toArray()[0];
						}

						// FIXME: Due to new mapping system, a mapping involving
						// expression index can return a
						// Set of
						// values, depending on the IDType that has been
						// specified when loading expression
						// data.
						// Possibly a different handling of the Set is required.
						Set<Integer> setDavidIDs = generalManager
								.getIDMappingManager().getIDAsSet(
										EIDType.EXPRESSION_INDEX,
										EIDType.DAVID, expressionIndex);

						Integer iDavidID = null;
						if ((setDavidIDs != null && !setDavidIDs.isEmpty())) {
							iDavidID = (Integer) setDavidIDs.toArray()[0];
						}

						if (iDavidID == null)
							continue;

						if (iItemsToLoad == 0) {
							String sURL = urlGenerator.createURL(
									eBrowserQueryType, iDavidID);

							browser.setUrl(sURL);
							browser.update();
							textURL.setText(sURL);

							iAlDavidID.clear();
							// list.removeAll();
						}

						String sOutput = "";
						sOutput = sOutput
								+ generalManager.getIDMappingManager().getID(
										EIDType.DAVID, EIDType.GENE_SYMBOL,
										iDavidID);

						sOutput = sOutput + "\n";
						sOutput = sOutput + sRefSeqID;

						if (iAlDavidID.contains(selectionDeltaItem
								.getPrimaryID())) {
							continue;
						}

						// list.add(sOutput);
						iAlDavidID.add(iDavidID);

						iItemsToLoad++;
					}
				}

				// list.redraw();
				// list.setSelection(0);
			}
		});
	}

	public void changeQueryType(EBrowserQueryType eBrowserQueryType) {
		this.eBrowserQueryType = eBrowserQueryType;
		GeneralManager.get().getPreferenceStore().setValue(
				PreferenceConstants.BROWSER_QUERY_DATABASE,
				eBrowserQueryType.name());
		if (!iAlDavidID.isEmpty()) {
			String sURL = urlGenerator.createURL(eBrowserQueryType, iAlDavidID
					.get(0));

			browser.setUrl(sURL);
			browser.update();
			textURL.setText(sURL);
		}
	}

	public EBrowserQueryType getCurrentBrowserQueryType() {
		return eBrowserQueryType;
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();

		selectionUpdateListener = new SelectionUpdateListener();
		selectionUpdateListener.setHandler(this);
		eventPublisher.addListener(SelectionUpdateEvent.class,
				selectionUpdateListener);

		// changeQueryTypeListener = new ChangeQueryTypeListener();
		// changeQueryTypeListener.setBrowserView(this);
		// eventPublisher.addListener(ChangeQueryTypeEvent.class,
		// changeQueryTypeListener);

	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();

		if (selectionUpdateListener != null) {
			eventPublisher.removeListener(selectionUpdateListener);
			selectionUpdateListener = null;
		}
		// if (changeQueryTypeListener != null) {
		// eventPublisher.removeListener(ChangeQueryTypeEvent.class,
		// changeQueryTypeListener);
		// changeQueryTypeListener = null;
		// }
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHTMLBrowserView serializedForm = new SerializedHTMLBrowserView(
				dataDomain);
		serializedForm.setViewID(getID());
		serializedForm.setQueryType(getCurrentBrowserQueryType());
		serializedForm.setUrl(getUrl());
		return serializedForm;
	}
}
