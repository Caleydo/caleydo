package org.caleydo.view.histogram;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.container.DataContainer;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.selection.events.ClearSelectionsListener;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.view.ClearSelectionsEvent;
import org.caleydo.core.serialize.ASerializedTopLevelDataView;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.mapping.color.UpdateColorMappingListener;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.ITableBasedDataDomainView;
import org.caleydo.core.view.MinimumSizeComposite;
import org.caleydo.core.view.opengl.canvas.ATableBasedView;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class RcpGLHistogramView extends ARcpGLViewPart implements IViewCommandHandler,
		IListenerOwner, ITableBasedDataDomainView {

	protected UpdateColorMappingListener updateViewListener;
	protected ClearSelectionsListener clearSelectionsListener;

	protected Composite histoComposite;

	protected ATableBasedDataDomain dataDomain;
	protected DataContainer dataContainer;

	/**
	 * Constructor.
	 */
	public RcpGLHistogramView() {
		super();

		try {
			viewContext = JAXBContext.newInstance(SerializedHistogramView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		minSizeComposite = new MinimumSizeComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		histoComposite = new Composite(minSizeComposite, SWT.NULL);
		minSizeComposite.setContent(histoComposite);
		minSizeComposite.setMinSize(160, 80);
		minSizeComposite.setExpandHorizontal(true);
		minSizeComposite.setExpandVertical(true);

		GridLayout baseLayout = new GridLayout(1, false);
		baseLayout.verticalSpacing = 2;
		histoComposite.setLayout(baseLayout);

		parentComposite = new Composite(histoComposite, SWT.EMBEDDED);
		parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createGLCanvas();

		view = new GLHistogram(glCanvas, parentComposite, serializedView.getViewFrustum());
		// ((GLHistogram) view).setRenderColorBars(false);
		initializeViewWithData();
		initialize();

		createPartControlGL();
		redrawView();
	}

	public void redrawView() {

	}

	public static void createToolBarItems(int viewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public void registerEventListeners() {

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {

		if (updateViewListener != null) {
			eventPublisher.removeListener(updateViewListener);
			updateViewListener = null;
		}
		if (clearSelectionsListener != null) {
			eventPublisher.removeListener(clearSelectionsListener);
			clearSelectionsListener = null;
		}
	}

	@Override
	public synchronized void queueEvent(
			final AEventListener<? extends IListenerOwner> listener, final AEvent event) {

		parentComposite.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void createDefaultSerializedView() {

		serializedView = new SerializedHistogramView();

		if (dataDomain == null)
			determineDataConfiguration(serializedView);
		else
			((ASerializedTopLevelDataView) serializedView).setDataDomainID(dataDomain
					.getDataDomainID());
	}

	@Override
	public String getViewGUIID() {
		return GLHistogram.VIEW_TYPE;
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	@Override
	public void setDataContainer(DataContainer dataContainer) {
		this.dataContainer = dataContainer;
	}

	@Override
	public void initialize() {
		ATableBasedView glHistogram = (ATableBasedView) view;
		setDataDomain(glHistogram.getDataDomain());
		setDataContainer(glHistogram.getDataContainer());
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView serializedView) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getViewType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void handleRedrawView() {

	}

	@Override
	public List<DataContainer> getDataContainers() {
		return ((ITableBasedDataDomainView) view).getDataContainers();
	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}
}
