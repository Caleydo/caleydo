package org.caleydo.view.selectionbrowser;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.rcp.view.rcp.CaleydoRCPViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * View showing selections.
 * 
 * @author Marc Streit
 */
public class RcpSelectionBrowserView extends CaleydoRCPViewPart {

	public static final String VIEW_ID = "org.caleydo.view.selectionbrowser";
	private Composite parentComposite;

	private SelectionBrowserView selectionBrowser;

	public RcpSelectionBrowserView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedSelectionBrowserView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		parentComposite.setLayout(new GridLayout(1, false));

		this.parentComposite = parentComposite;

		Composite infoComposite = new Composite(this.parentComposite, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout;
		layout = new GridLayout(1, false);

		layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;

		infoComposite.setLayout(layout);

		SerializedSelectionBrowserView serializedView = new SerializedSelectionBrowserView();

		selectionBrowser = new SelectionBrowserView();
		selectionBrowser.setDataDomain((ASetBasedDataDomain) DataDomainManager
				.getInstance().getDataDomain(determineDataDomain(serializedView)));
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
