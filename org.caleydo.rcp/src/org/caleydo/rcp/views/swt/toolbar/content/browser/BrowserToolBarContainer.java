package org.caleydo.rcp.views.swt.toolbar.content.browser;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.swt.browser.EBrowserQueryType;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.views.swt.toolbar.content.ToolBarContainer;

/**
 * Widget based toolbar container to display browser related toolbar content. 
 * @author Werner Puff
 */
public class BrowserToolBarContainer
	extends ToolBarContainer {

	/** query type of the browser */
	EBrowserQueryType selectedQueryType;

	/** Mediator to handle actions triggered by the contributed elements */ 
	BrowserToolBarMediator browserToolBarMediator;
	
	/**
	 * Creates a set of radio-buttons toolbar contributions to 
	 * switch between different browser types 
	 */
	@Override
	public List<IToolBarItem> getToolBarItems() {

		List<IToolBarItem> elements = new ArrayList<IToolBarItem>();

		elements.add(createQueryTypeButton(EBrowserQueryType.KEGG));
		elements.add(createQueryTypeButton(EBrowserQueryType.BioCarta));
		elements.add(createQueryTypeButton(EBrowserQueryType.EntrezGene));
		elements.add(createQueryTypeButton(EBrowserQueryType.PubMed));
		elements.add(createQueryTypeButton(EBrowserQueryType.GeneCards));
		
		return elements;
	}

	private IToolBarItem createQueryTypeButton(EBrowserQueryType buttonType) {

		QueryTypeRadioButton button = new QueryTypeRadioButton(buttonType.toString());
		if (selectedQueryType == buttonType) {
			button.setSelection(true);
		} else {
			button.setSelection(false);
		}
		button.setText(buttonType.toString());
		button.setBrowserToolBarMediator(browserToolBarMediator);
		
		return button;
	}

	public EBrowserQueryType getSelectedQueryType() {
		return selectedQueryType;
	}

	public void setSelectedQueryType(EBrowserQueryType queryType) {
		this.selectedQueryType = queryType;
	}

	public BrowserToolBarMediator getBrowserToolBarMediator() {
		return browserToolBarMediator;
	}

	public void setBrowserToolBarMediator(BrowserToolBarMediator browserToolBarMediator) {
		this.browserToolBarMediator = browserToolBarMediator;
	}

}
