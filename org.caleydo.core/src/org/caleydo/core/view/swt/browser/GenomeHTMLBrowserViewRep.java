package org.caleydo.core.view.swt.browser;

import java.util.ArrayList;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.delta.DeltaEventContainer;
import org.caleydo.core.data.selection.delta.ISelectionDelta;
import org.caleydo.core.data.selection.delta.SelectionDeltaItem;
import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IEventContainer;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.event.IMediatorSender;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.serialize.ASerializedView;
import org.caleydo.core.view.serialize.SerializedHTMLBrowserView;
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
public class GenomeHTMLBrowserViewRep
	extends HTMLBrowserViewRep
	implements IMediatorReceiver {
	private URLGenerator urlGenerator;

	private ArrayList<Integer> iAlDavidID;

	private EBrowserQueryType eBrowserQueryType = EBrowserQueryType.EntrezGene;

	ChangeQueryTypeListener changeQueryTypeListener;
	
	/**
	 * Constructor.
	 */
	public GenomeHTMLBrowserViewRep(int iParentContainerID, String sLabel) {

		super(iParentContainerID, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_BROWSER_GENOME));

		urlGenerator = new URLGenerator();
		iAlDavidID = new ArrayList<Integer>();
		
//		registerEventListeners();
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		super.initViewSWTComposite(parentComposite);

		final Combo queryTypeCombo = new Combo(subContributionComposite, SWT.READ_ONLY);
		queryTypeCombo.add(EBrowserQueryType.EntrezGene.toString());
		queryTypeCombo.add(EBrowserQueryType.KEGG.toString());
		queryTypeCombo.add(EBrowserQueryType.BioCarta.toString());
		queryTypeCombo.add(EBrowserQueryType.GeneCards.toString());
		queryTypeCombo.add(EBrowserQueryType.PubMed.toString());
		
		queryTypeCombo.select(0);
		
		queryTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				changeQueryType(EBrowserQueryType.valueOf(queryTypeCombo.getItem(queryTypeCombo.getSelectionIndex())));
			}
		});
		
		subContributionComposite.layout();
		subContributionComposite.getParent().layout();
	}
	
	private void handleSelectionUpdate(IMediatorSender eventTrigger, final ISelectionDelta selectionDelta) {
		if (selectionDelta.getIDType() != EIDType.REFSEQ_MRNA_INT)
			return;

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!checkInternetConnection())
					return;

				int iItemsToLoad = 0;
				// SelectionDeltaItem selectionItem;

				for (SelectionDeltaItem selectionDeltaItem : selectionDelta) {
					if (selectionDeltaItem.getSelectionType() == ESelectionType.MOUSE_OVER
						|| selectionDeltaItem.getSelectionType() == ESelectionType.SELECTION) {
						String sRefSeqID =
							GeneralManager.get().getIDMappingManager()
								.getID(EMappingType.REFSEQ_MRNA_INT_2_REFSEQ_MRNA,
									selectionDeltaItem.getPrimaryID());

						Integer iDavidID =
							GeneralManager.get().getIDMappingManager().getID(
								EMappingType.REFSEQ_MRNA_INT_2_DAVID, selectionDeltaItem.getPrimaryID());

						if (iItemsToLoad == 0) {
							String sURL = urlGenerator.createURL(eBrowserQueryType, iDavidID);

							browser.setUrl(sURL);
							browser.update();
							textURL.setText(sURL);

							iAlDavidID.clear();
							// list.removeAll();
						}

						String sOutput = "";
						sOutput =
							sOutput
								+ GeneralManager.get().getIDMappingManager().getID(
									EMappingType.DAVID_2_GENE_SYMBOL, iDavidID);

						sOutput = sOutput + "\n";
						sOutput = sOutput + sRefSeqID;

						if (iAlDavidID.contains(selectionDeltaItem.getPrimaryID())) {
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

	@SuppressWarnings("unchecked")
	@Override
	public void handleExternalEvent(IMediatorSender eventTrigger, IEventContainer eventContainer,
		EMediatorType eMediatorType) {
		switch (eventContainer.getEventType()) {
			case SELECTION_UPDATE:
				DeltaEventContainer<ISelectionDelta> selectionDeltaEventContainer =
					(DeltaEventContainer<ISelectionDelta>) eventContainer;
				handleSelectionUpdate(eventTrigger, selectionDeltaEventContainer.getSelectionDelta());
				break;
		}
	}

	public void changeQueryType(EBrowserQueryType eBrowserQueryType) {
		this.eBrowserQueryType = eBrowserQueryType;
		if (!iAlDavidID.isEmpty()) {
			String sURL = urlGenerator.createURL(eBrowserQueryType, iAlDavidID.get(0));

			browser.setUrl(sURL);
			browser.update();
			textURL.setText(sURL);
		}
	}
	
	public EBrowserQueryType getCurrentBrowserQueryType() {
		return eBrowserQueryType;
	}

//	public void registerEventListeners() {
//		
//		super.registerEventListeners();
//		
//		IEventPublisher eventPublisher = generalManager.getEventPublisher();
//		
//		changeQueryTypeListener = new ChangeQueryTypeListener();
//		changeQueryTypeListener.setBrowserView(this);
//		eventPublisher.addListener(ChangeQueryTypeEvent.class, changeQueryTypeListener);
//
//	}
//	
//	public void unregisterEventListeners() {
//		IEventPublisher eventPublisher = generalManager.getEventPublisher();
//
//		super.registerEventListeners();
//		
//		if (changeQueryTypeListener != null) {
//			eventPublisher.removeListener(ChangeQueryTypeEvent.class, changeQueryTypeListener);
//			changeQueryTypeListener = null;
//		}
//	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedHTMLBrowserView serializedForm = new SerializedHTMLBrowserView();
		serializedForm.setViewID(getID());
		serializedForm.setQueryType(getCurrentBrowserQueryType());
		serializedForm.setUrl(getUrl());
		return serializedForm;
	}
}
