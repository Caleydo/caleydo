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
import org.caleydo.view.filterpipeline.RcpGLFilterPipelineView;
import org.caleydo.view.grouper.RcpGLGrouperView;
import org.caleydo.view.histogram.RcpGLColorMapperHistogramView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
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

	private ContentSelectionManager contentSelectionManager;

	/**
	 * Constructor.
	 */
	public RcpDataMetaView() {
		super();

		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	@Override
	public void createPartControl(Composite parent) {

		dataDomain = (ASetBasedDataDomain) DataDomainManager.get().getDataDomain(
				serializedView.getDataDomainType());
		set = dataDomain.getSet();

		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));
//		parentComposite.setBackground(new Color(parentComposite.getDisplay() ,127,178,127));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL); 
		
		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayout(new GridLayout(1, false));
		infoComposite.setLayoutData(gridData);

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
		
		
		ExpandBar bar = new ExpandBar (parentComposite, SWT.V_SCROLL);
		gridData = new GridData(GridData.FILL_BOTH);
		bar.setLayoutData(gridData);
//		Display display = parentComposite.getDisplay();
//		Image image = display.getSystemImage(SWT.ICON_QUESTION);
		
		// Third item
		Composite composite = new Composite (bar, SWT.NONE);
		composite.setLayout(new FillLayout());
//		layout = new GridLayout (1, true);
//		layout.marginLeft = layout.marginTop = layout.marginRight = layout.marginBottom = 10;
//		layout.verticalSpacing = 10;
//		composite.setLayout(layout);

		RcpGLColorMapperHistogramView histogramView = new RcpGLColorMapperHistogramView();
		histogramView.setDataDomain(dataDomain);
		histogramView.createDefaultSerializedView();
		histogramView.createPartControl(composite);
		// Usually the canvas is registered to the GL2 animator in the
		// PartListener.
		// Because the GL2 histogram is no usual RCP view we have to do it on our
		// own
		GeneralManager.get().getViewGLCanvasManager()
				.registerGLCanvasToAnimator(histogramView.getGLCanvas());
		ExpandItem item2 = new ExpandItem (bar, SWT.NONE, 0);
		item2.setText("Histogram");
		item2.setHeight(200);//composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item2.setControl(composite);
//		item2.setImage(image);
		
		composite = new Composite (bar, SWT.NONE);
		composite.setLayout(new FillLayout());
		RcpGLFilterPipelineView filterPipelineView = new RcpGLFilterPipelineView();
//		filterPipelineView.setDataDomain(dataDomain);
		filterPipelineView.createDefaultSerializedView();
		filterPipelineView.createPartControl(composite);
		GeneralManager.get().getViewGLCanvasManager()
				.registerGLCanvasToAnimator(filterPipelineView.getGLCanvas());
		ExpandItem item3 = new ExpandItem (bar, SWT.NONE, 1);
		item3.setText("Filter Pipeline");
		item3.setHeight(200);//composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item3.setControl(composite);
//		item2.setImage(image);
		
		composite = new Composite (bar, SWT.NONE);
		composite.setLayout(new FillLayout());
		RcpGLGrouperView grouperView = new RcpGLGrouperView();
//		grouperView.setDataDomain(dataDomain);
		grouperView.createDefaultSerializedView();
		grouperView.createPartControl(composite);
		GeneralManager.get().getViewGLCanvasManager()
				.registerGLCanvasToAnimator(grouperView.getGLCanvas());
		ExpandItem item4 = new ExpandItem (bar, SWT.NONE, 2);
		item4.setText("Grouper");
		item4.setHeight(800);//composite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		item4.setControl(composite);
//		item2.setImage(image);
		
		item2.setExpanded(true);
		item3.setExpanded(true);
		item4.setExpanded(true);
		
		bar.setSpacing(2);
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