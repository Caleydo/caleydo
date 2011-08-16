package org.caleydo.view.info;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Search view contains gene and pathway search.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class RcpInfoAreaView extends CaleydoRCPViewPart {

	public static final String VIEW_TYPE = "org.caleydo.view.info";

	public static boolean bHorizontal = false;

	private Composite parentComposite;

	private InfoArea infoArea;

	/**
	 * Constructor.
	 */
	public RcpInfoAreaView() {
		super();
		
		try {
			viewContext = JAXBContext
					.newInstance(SerializedInfoAreaView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}
	
	@Override
	public void createPartControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NULL);

		// FIXME: when view plugin reorganizatin is done
		// if (!GenomePerspective.bIsWideScreen) {
		// bHorizontal = true;
		// }

		if (bHorizontal) {
			parentComposite.setLayout(new GridLayout(10, false));
		} else {
			parentComposite.setLayout(new GridLayout(1, false));
		}

		this.parentComposite = parentComposite;

		addInfoBar();

	}

	@Override
	public void setFocus() {

	}

	private void addInfoBar() {

		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout layout;
		if (bHorizontal) {
			layout = new GridLayout(2, false);
		} else {
			layout = new GridLayout(1, false);
		}

		layout.marginBottom = layout.marginTop = layout.marginLeft = layout.marginRight = layout.horizontalSpacing = layout.verticalSpacing = 0;
		layout.marginHeight = layout.marginWidth = 0;

		infoComposite.setLayout(layout);
		infoArea = new InfoArea();

		IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
				serializedView.getDataDomainID());
		infoArea.setDataDomain((ATableBasedDataDomain) dataDomain);

		infoArea.registerEventListeners();
		infoArea.createControl(infoComposite);
	}

	@Override
	public void dispose() {
		super.dispose();

		infoArea.dispose();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedInfoAreaView();
		determineDataDomain(serializedView);
	}
}
