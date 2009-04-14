package org.caleydo.rcp.views.swt;

import org.caleydo.core.manager.event.EMediatorType;
import org.caleydo.core.manager.event.IMediatorReceiver;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.swt.browser.EBrowserQueryType;
import org.caleydo.core.view.swt.browser.GenomeHTMLBrowserViewRep;
import org.caleydo.core.view.swt.browser.HTMLBrowserViewRep;
import org.caleydo.rcp.views.CaleydoViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class HTMLBrowserView
	extends CaleydoViewPart {
	public static final String ID = "org.caleydo.rcp.views.swt.HTMLBrowserView";

	private HTMLBrowserViewRep browserView;

	@Override
	public void createPartControl(Composite parent) {
		browserView =
			(HTMLBrowserViewRep) GeneralManager.get().getViewGLCanvasManager().createView(
				EManagedObjectType.VIEW_SWT_BROWSER_GENOME, -1, "Browser");

		browserView.initViewRCP(parent);
		browserView.drawView();
		iViewID = browserView.getID();

		GeneralManager.get().getEventPublisher().addReceiver(EMediatorType.SELECTION_MEDIATOR,
			(IMediatorReceiver) browserView);

		GeneralManager.get().getViewGLCanvasManager().registerItem(browserView);
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR,
			(IMediatorReceiver) browserView);

		GeneralManager.get().getEventPublisher().removeReceiver(EMediatorType.SELECTION_MEDIATOR,
			(IMediatorReceiver) browserView);

		GeneralManager.get().getViewGLCanvasManager().unregisterItem(browserView.getID());
	}

	public HTMLBrowserViewRep getHTMLBrowserViewRep() {
		return browserView;
	}

	public void createToolBarItems2(int iViewID, Composite group) {
		final Button buttonKEGG = new Button(group, SWT.RADIO);
		buttonKEGG.setText(EBrowserQueryType.KEGG.toString());
		final Button buttonBioCarta = new Button(group, SWT.RADIO);
		buttonBioCarta.setText(EBrowserQueryType.BioCarta.toString());
		final Button buttonEntrez = new Button(group, SWT.RADIO);
		buttonEntrez.setText(EBrowserQueryType.EntrezGene.toString());
		buttonEntrez.setSelection(true);
		final Button buttonPubMed = new Button(group, SWT.RADIO);
		buttonPubMed.setText(EBrowserQueryType.PubMed.toString());
		final Button buttonGeneCards = new Button(group, SWT.RADIO);
		buttonGeneCards.setText(EBrowserQueryType.GeneCards.toString());

		switch (((GenomeHTMLBrowserViewRep) browserView).getCurrentBrowserQueryType()) {
			case KEGG:
				buttonKEGG.setSelection(true);
				buttonBioCarta.setSelection(false);
				buttonEntrez.setSelection(false);
				buttonPubMed.setSelection(false);
				buttonGeneCards.setSelection(false);
				break;
			case BioCarta:
				buttonKEGG.setSelection(false);
				buttonBioCarta.setSelection(true);
				buttonEntrez.setSelection(false);
				buttonPubMed.setSelection(false);
				buttonGeneCards.setSelection(false);
				break;
			case EntrezGene:
				buttonKEGG.setSelection(false);
				buttonBioCarta.setSelection(false);
				buttonEntrez.setSelection(true);
				buttonPubMed.setSelection(false);
				buttonGeneCards.setSelection(false);
				break;
			case PubMed:
				buttonKEGG.setSelection(false);
				buttonBioCarta.setSelection(false);
				buttonEntrez.setSelection(false);
				buttonPubMed.setSelection(true);
				buttonGeneCards.setSelection(false);
				break;
			case GeneCards:
				buttonKEGG.setSelection(false);
				buttonBioCarta.setSelection(false);
				buttonEntrez.setSelection(false);
				buttonPubMed.setSelection(false);
				buttonGeneCards.setSelection(true);
				break;
			default:
				break;
		}

		SelectionListener queryTypeListener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				((GenomeHTMLBrowserViewRep) browserView).changeQueryType(EBrowserQueryType
					.valueOf(((Button) e.widget).getText()));

			}
		};

		buttonBioCarta.addSelectionListener(queryTypeListener);
		buttonKEGG.addSelectionListener(queryTypeListener);
		buttonPubMed.addSelectionListener(queryTypeListener);
		buttonEntrez.addSelectionListener(queryTypeListener);
		buttonGeneCards.addSelectionListener(queryTypeListener);
	}
}
