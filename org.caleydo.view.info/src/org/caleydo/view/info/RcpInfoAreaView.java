package org.caleydo.view.info;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Search view contains gene and pathway search.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class RcpInfoAreaView extends CaleydoRCPViewPart implements
		IDataDomainBasedView<ATableBasedDataDomain> {

	public static final String VIEW_TYPE = "org.caleydo.view.info";

	private ATableBasedDataDomain dataDomain;

	private Composite parent;

	/**
	 * Constructor.
	 */
	public RcpInfoAreaView() {
		super();

		isSupportView = true;

		try {
			viewContext = JAXBContext.newInstance(SerializedInfoAreaView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));

		if (dataDomain == null) {
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.getDataDomainByID(
							((ASerializedTopLevelDataView) serializedView)
									.getDataDomainID());
			if (dataDomain == null)
				return;
		}

		InfoArea infoArea = new InfoArea();
		infoArea.setDataDomain((ATableBasedDataDomain) dataDomain);
		infoArea.registerEventListeners();
		infoArea.createControl(parentComposite);

		parent.layout();
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedInfoAreaView();
		determineDataConfiguration(serializedView, false);
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {

		// Do nothing if new data domain is the same as the current one
		if (dataDomain == this.dataDomain)
			return;

		this.dataDomain = dataDomain;

		((ASerializedTopLevelDataView) serializedView).setDataDomainID(dataDomain
				.getDataDomainID());
		((ASerializedTopLevelDataView) serializedView).setRecordPerspectiveID(dataDomain
				.getTable().getDefaultRecordPerspective().getID());
		((ASerializedTopLevelDataView) serializedView)
				.setDimensionPerspectiveID(dataDomain.getTable()
						.getDefaultDimensionPerspective().getID());

		parentComposite.dispose();
		createPartControl(parent);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}
}
