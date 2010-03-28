package org.caleydo.rcp.view.rcp;

import org.caleydo.rcp.util.selections.SelectionBrowser;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
	private Composite parentComposite;

	private SelectionBrowser selectionBrowser;

	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);
		
		parentComposite.setLayout(new GridLayout(1, false));
		
		this.parentComposite = parentComposite;

		Composite infoComposite = new Composite(this.parentComposite, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		GridLayout layout;
		layout = new GridLayout(1, false);

		layout.marginBottom =
			layout.marginTop =
				layout.marginLeft =
					layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;

		infoComposite.setLayout(layout);
		
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
