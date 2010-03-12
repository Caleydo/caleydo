package org.caleydo.rcp.view.rcp;

import org.caleydo.rcp.util.selections.SelectionBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * View showing selections.
 * 
 * @author Marc Streit
 */
public class RcpSelectionBrowserView
	extends ViewPart {

	public static final String ID = "org.caleydo.rcp.views.swt.SelectionBrowserView";

	private SelectionBrowser selectionBrowser;

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectionBrowser = new SelectionBrowser();
		selectionBrowser.registerEventListeners();
		selectionBrowser.createControl(infoComposite);
	}

	@Override
	public void setFocus() {

	}


	@Override
	public void dispose() {
		super.dispose();

		selectionBrowser.dispose();
	}
}
