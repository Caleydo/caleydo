package org.caleydo.view.datameta;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.view.histogram.RcpGLColorMapperHistogramView;
import org.caleydo.view.histogram.SerializedHistogramView;
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

	private Composite parent;

	/**
	 * Constructor.
	 */
	public RcpDataMetaView() {
		super();

		eventPublisher = GeneralManager.get().getEventPublisher();
		isSupportView = true;

		try {
			viewContext = JAXBContext.newInstance(SerializedDataMetaView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		if (dataDomain == null) {
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get()
					.getDataDomainByID(
							((ASerializedTopLevelDataView) serializedView)
									.getDataDomainID());
		}

		this.parent = parent;
		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));
		// parentComposite.setBackground(new Color(parentComposite.getDisplay()
		// ,127,178,127));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayout(new GridLayout(1, false));
		infoComposite.setLayoutData(gridData);
		
		if (dataDomain == null) {
			Label label = new Label(infoComposite, SWT.NONE);
			label.setText("No data set active");
		}
		else{
			Label label = new Label(infoComposite, SWT.NONE);
			label.setText("Name: "
					+ dataDomain.getLabel());
			
			label = new Label(infoComposite, SWT.NONE);
			label.setText(dataDomain.getRecordDenomination(true, true) + ": "
					+ dataDomain.getTable().getMetaData().depth());

			label = new Label(infoComposite, SWT.NONE);
			label.setText(dataDomain.getDimensionDenomination(true, true) + ": "
					+ dataDomain.getTable().getMetaData().size());

			label = new Label(infoComposite, SWT.NONE);
			label.setText("Source: " + dataDomain.getLoadDataParameters().getFileName());

			// Tree<ClusterNode> dimensionTree =
			// dataDomain.getTable().getDimensionData(DimensionVAType.STORAGE).getDimensionTree();

			// label = new Label(parent, SWT.NONE);
			// label.setText("Experiments clustered: "+dimensionTree == null ?
			// "false"
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

			ExpandBar bar = new ExpandBar(parentComposite, SWT.V_SCROLL);
			gridData = new GridData(GridData.FILL_BOTH);
			bar.setLayoutData(gridData);
			// Display display = parentComposite.getDisplay();
			// Image image = display.getSystemImage(SWT.ICON_QUESTION);

			// Third item
			Composite composite = new Composite(bar, SWT.NONE);
			composite.setLayout(new FillLayout());
			// layout = new GridLayout (1, true);
			// layout.marginLeft = layout.marginTop = layout.marginRight =
			// layout.marginBottom = 10;
			// layout.verticalSpacing = 10;
			// composite.setLayout(layout);

			RcpGLColorMapperHistogramView histogramView = new RcpGLColorMapperHistogramView();
			histogramView.setDataDomain(dataDomain);
			SerializedHistogramView serializedHistogramView = new SerializedHistogramView(
					dataDomain.getDataDomainID());
			serializedHistogramView
					.setDimensionPerspectiveID(((ASerializedTopLevelDataView) serializedView)
							.getDimensionPerspectiveID());
			serializedHistogramView
					.setRecordPerspectiveID(((ASerializedTopLevelDataView) serializedView)
							.getRecordPerspectiveID());

			histogramView.setExternalSerializedView(serializedHistogramView);
			histogramView.createPartControl(composite);
			// Usually the canvas is registered to the GL2 animator in the
			// PartListener.
			// Because the GL2 histogram is no usual RCP view we have to do it on
			// our own
			GeneralManager.get().getViewManager()
					.registerGLCanvasToAnimator(histogramView.getGLCanvas());
			ExpandItem item2 = new ExpandItem(bar, SWT.NONE, 0);
			item2.setText("Histogram");
			item2.setHeight(200);
			item2.setControl(composite);

			item2.setExpanded(true);

			bar.setSpacing(2);
		}

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
	public void setDataDomain(ATableBasedDataDomain dataDomain) {

		// Do nothing if new datadomain is the same as the current one
		if (dataDomain == this.dataDomain)
			return;

		this.dataDomain = dataDomain;
		
		((ASerializedTopLevelDataView)serializedView).setDataDomainID(dataDomain.getDataDomainID());
		((ASerializedTopLevelDataView)serializedView).setRecordPerspectiveID(dataDomain.getTable().getDefaultRecordPerspective().getID());
		((ASerializedTopLevelDataView)serializedView).setDimensionPerspectiveID(dataDomain.getTable().getDefaultDimensionPerspective().getID());
		
		parentComposite.dispose();
		createPartControl(parent);
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDataMetaView();
		determineDataConfiguration(serializedView, false);
	}
}