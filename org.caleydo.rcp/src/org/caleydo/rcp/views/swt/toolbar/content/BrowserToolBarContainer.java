package org.caleydo.rcp.views.swt.toolbar.content;

import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.swt.browser.EBrowserQueryType;
import org.caleydo.core.view.swt.browser.GenomeHTMLBrowserViewRep;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Group;

/**
 * Widget based toolbar container to display browser related toolbar content. 
 * @author Werner Puff
 */
public class BrowserToolBarContainer
	extends WidgetToolBarContainer {

	private int targetViewID;
	
	@Override
	public void render(Group group) {

		IViewManager viewManager = GeneralManager.get().getViewGLCanvasManager();
		final GenomeHTMLBrowserViewRep view = (GenomeHTMLBrowserViewRep) viewManager.getItem(targetViewID);

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

		switch (view.getCurrentBrowserQueryType()) {
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
				System.out.println("widget selected, missing implementation");
				view.changeQueryType(EBrowserQueryType.valueOf(((Button) e.widget).getText()));
			}
		};

		buttonBioCarta.addSelectionListener(queryTypeListener);
		buttonKEGG.addSelectionListener(queryTypeListener);
		buttonPubMed.addSelectionListener(queryTypeListener);
		buttonEntrez.addSelectionListener(queryTypeListener);
		buttonGeneCards.addSelectionListener(queryTypeListener);
	}

	public int getTargetViewID() {
		return targetViewID;
	}

	public void setTargetViewID(int targetViewID) {
		this.targetViewID = targetViewID;
	}
}
