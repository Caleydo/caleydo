package org.caleydo.rcp.views.swt.toolbar.content.browser;

import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Radio-button style toolbar-contribution to switch browser query type.
 * @author Werner Puff
 */
public class QueryTypeRadioButton
	extends ControlContribution
	implements IToolBarItem {

	/** mediator to handle actions triggered by the contributed element */ 
	BrowserToolBarMediator browserToolBarMediator;

	/** 
	 * label for the radio button as computed by EBrowserQueryType-value toString(), 
	 * the radio buttons are identified by their label
	 */
	private String text;
	
	/**
	 * specifies if the button should be selected at creation time (=true)
	 */
	private boolean selection;
	
	/**
	 * constructor as requested by ControlContribution
	 * @param str
	 */
	public QueryTypeRadioButton(String str) {
		super(str);
	}
	
	@Override
	protected Control createControl(Composite parent) {

		Button button = new Button(parent, SWT.RADIO);
		button.setSelection(selection);
		button.setText(text);

		QueryTypeSelectionListener queryTypeSelectionListener = new QueryTypeSelectionListener(browserToolBarMediator); 
		button.addSelectionListener(queryTypeSelectionListener);

		browserToolBarMediator.addQueryTypeButton(button);
		
		return button;
	}

	public BrowserToolBarMediator getBrowserToolBarMediator() {
		return browserToolBarMediator;
	}

	public void setBrowserToolBarMediator(BrowserToolBarMediator browserToolBarMediator) {
		this.browserToolBarMediator = browserToolBarMediator;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isSelection() {
		return selection;
	}

	public void setSelection(boolean selection) {
		this.selection = selection;
	}

}
