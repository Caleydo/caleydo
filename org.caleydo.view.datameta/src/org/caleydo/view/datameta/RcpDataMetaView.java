package org.caleydo.view.datameta;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.Set;
import org.caleydo.core.data.selection.ContentSelectionManager;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.view.histogram.RcpGLColorMapperHistogramView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Data meta view showing details about a data set.
 * 
 * @author Marc Streit
 */
public class RcpDataMetaView extends CaleydoRCPViewPart implements
		IDataDomainBasedView<ASetBasedDataDomain> {

	public final static String VIEW_ID = "org.caleydo.view.datameta";

	private ASetBasedDataDomain dataDomain;

	private Set set;

	private GeneralManager generalManager = null;
	// private EventPublisher eventPublisher = null;

	private ContentSelectionManager contentSelectionManager;

	// private StorageSelectionManager storageSelectionManager;

	/**
	 * Constructor.
	 */
	public RcpDataMetaView() {
		super();

		// try {
		// viewContext = JAXBContext.newInstance(SerializedDataMetaView.class);
		// } catch (JAXBException ex) {
		// throw new RuntimeException("Could not create JAXBContext", ex);
		// }

		generalManager = GeneralManager.get();
		eventPublisher = generalManager.getEventPublisher();
	}

	@Override
	public void createPartControl(Composite parent) {

		dataDomain = (ASetBasedDataDomain) DataDomainManager.get().getDataDomain(
				serializedView.getDataDomainType());
		set = dataDomain.getSet();

		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;

		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayoutData(gridData);
		infoComposite.setLayout(new FillLayout(SWT.VERTICAL));

		Label label = new Label(infoComposite, SWT.NONE);
		label.setText("Number of genes: " + set.depth());

		label = new Label(infoComposite, SWT.NONE);
		label.setText("Number of experiments: " + set.size());

		label = new Label(infoComposite, SWT.NONE);
		label.setText("Loaded from file: " + dataDomain.getFileName());

		label = new Label(infoComposite, SWT.NONE);
		label.setText("Human readable ID type: "
				+ dataDomain.getHumanReadableContentIDType().getTypeName());

		// Tree<ClusterNode> storageTree =
		// dataDomain.getSet().getStorageData(StorageVAType.STORAGE).getStorageTree();

		// label = new Label(parent, SWT.NONE);
		// label.setText("Experiments clustered: "+storageTree == null ? "false"
		// : "true");
		//
		// if
		// (dataDomain.getSet().getStorageData(StorageVAType.STORAGE).getStorageClusterSizes()
		// != null) {
		// label = new Label(parent, SWT.NONE);
		// label.setText("Number of clusters: "+dataDomain.getSet().getStorageData(StorageVAType.STORAGE).getStorageClusterSizes().size());
		// }

		// label = new Label(parent, SWT.NONE);
		// label.setText(": "+dataDomain);

		Composite histoComposite = new Composite(parentComposite, SWT.NULL);
		histoComposite.setLayoutData(gridData);
		histoComposite.setLayout(new FillLayout(SWT.VERTICAL));

		RcpGLColorMapperHistogramView histogramView = new RcpGLColorMapperHistogramView();
		histogramView.setDataDomain(dataDomain);
		histogramView.createDefaultSerializedView();
		histogramView.createPartControl(histoComposite);
		// Usually the canvas is registered to the GL2 animator in the
		// PartListener.
		// Because the GL2 histogram is no usual RCP view we have to do it on our
		// own
		GeneralManager.get().getViewGLCanvasManager()
				.registerGLCanvasToAnimator(histogramView.getGLCanvas());
	}

	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void setDataDomain(ASetBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		this.set = dataDomain.getSet();

		String contentVAType = ISet.CONTENT;
		contentSelectionManager = dataDomain.getContentSelectionManager();

		ContentVirtualArray contentVA = dataDomain.getContentVA(contentVAType);
		contentSelectionManager.setVA(contentVA);
	}

	@Override
	public ASetBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDataMetaView();
		determineDataDomain(serializedView);
	}
}