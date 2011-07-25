package org.caleydo.view.datameta;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.selection.RecordSelectionManager;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
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
 * Data meta view showing details about a data table.
 * 
 * @author Marc Streit
 */
public class RcpDataMetaView extends CaleydoRCPViewPart implements
		IDataDomainBasedView<ATableBasedDataDomain> {

	public final static String VIEW_TYPE = "org.caleydo.view.datameta";

	private ATableBasedDataDomain dataDomain;

	private DataTable table;

	private RecordSelectionManager contentSelectionManager;

	/**
	 * Constructor.
	 */
	public RcpDataMetaView() {
		super();

		eventPublisher = GeneralManager.get().getEventPublisher();
	}

	@Override
	public void createPartControl(Composite parent) {

		dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(
				serializedView.getDataDomainID());
		table = dataDomain.getTable();

		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));
//		parentComposite.setBackground(new Color(parentComposite.getDisplay() ,127,178,127));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL); 
		
		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayout(new GridLayout(1, false));
		infoComposite.setLayoutData(gridData);

		Label label = new Label(infoComposite, SWT.NONE);
		label.setText("Number of genes: " + table.getMetaData().depth());

		label = new Label(infoComposite, SWT.NONE);
		label.setText("Number of experiments: " + table.getMetaData().size());

		label = new Label(infoComposite, SWT.NONE);
		label.setText("Loaded from file: " + dataDomain.getFileName());

		label = new Label(infoComposite, SWT.NONE);
		label.setText("Human readable ID type: "
				+ dataDomain.getHumanReadableRecordIDType().getTypeName());

		// Tree<ClusterNode> dimensionTree =
		// dataDomain.getTable().getDimensionData(DimensionVAType.STORAGE).getDimensionTree();

		// label = new Label(parent, SWT.NONE);
		// label.setText("Experiments clustered: "+dimensionTree == null ? "false"
		// : "true");
		//
		// if
		// (dataDomain.getTable().getDimensionData(DimensionVAType.STORAGE).getDimensionClusterSizes()
		// != null) {
		// label = new Label(parent, SWT.NONE);
		// label.setText("Number of clusters: "+dataDomain.getTable().getDimensionData(DimensionVAType.STORAGE).getDimensionClusterSizes().size());
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
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		this.table = dataDomain.getTable();

		String recordVAType = DataTable.RECORD;
		contentSelectionManager = dataDomain.getRecordSelectionManager();

		RecordVirtualArray recordVA = dataDomain.getRecordVA(recordVAType);
		contentSelectionManager.setVA(recordVA);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDataMetaView();
		determineDataDomain(serializedView);
	}
}