package org.caleydo.view.histogram;

import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.gui.preferences.PreferenceConstants;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.manager.datadomain.IDataDomainBasedView;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.util.conversion.ConversionTools;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.view.ARcpGLViewPart;
import org.caleydo.core.view.MinimumSizeComposite;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;

public class RcpBasicGLHistogramView extends ARcpGLViewPart implements
		IViewCommandHandler, IListenerOwner, IDataDomainBasedView<IDataDomain> {

	private CLabel colorMappingPreviewLabel;

	private ArrayList<CLabel> labels;

	protected RedrawViewListener redrawViewListener;
	protected ClearSelectionsListener clearSelectionsListener;

	protected Composite histoComposite;

	protected IDataDomain dataDomain;

	PreferenceStore store = GeneralManager.get().getPreferenceStore();

	/**
	 * Constructor.
	 */
	public RcpBasicGLHistogramView() {
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
		// fillToolBar();
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

		view = new GLHistogram(glCanvas, serializedView.getViewFrustum());
		view.initFromSerializableRepresentation(serializedView);

		if (view instanceof IDataDomainBasedView<?>) {
			IDataDomain dataDomain = DataDomainManager.get().getDataDomainByID(
					serializedView.getDataDomainID());
			@SuppressWarnings("unchecked")
			IDataDomainBasedView<IDataDomain> dataDomainBasedView = (IDataDomainBasedView<IDataDomain>) view;
			dataDomainBasedView.setDataDomain(dataDomain);
			this.dataDomain = dataDomain;
		}
		((GLHistogram) view).setRenderColorBars(false);
		view.initialize();

		createPartControlGL();

		redrawView();
	}

	public void redrawView() {

		// colorMappingPreviewLabel = new CLabel(histoComposite, SWT.SHADOW_IN);
		// GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		// gridData.heightHint = 10;
		// gridData.grabExcessHorizontalSpace = true;
		// // colorData.
		// colorMappingPreviewLabel.setLayoutData(gridData);
		// // colorMappingPreviewLabel.setBounds(0, 0,
		// buttonComposite.getSize().x,
		// // 3);//setSize(, 3);
		//
		// colorMappingPreviewLabel.addMouseListener(new MouseListener() {
		// @Override
		// public void mouseDown(MouseEvent e) {
		//
		// }
		//
		// @Override
		// public void mouseDoubleClick(MouseEvent e) {
		// // TODO Auto-generated method stub
		// PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(
		// new Shell(),
		// "org.caleydo.rcp.preferences.ColorMappingPreferencePage", null,
		// null);
		//
		// if (pref != null) {
		// pref.open();
		// }
		// }
		//
		// @Override
		// public void mouseUp(MouseEvent e) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// });
		//
		// FillLayout fillLayout = new FillLayout();
		// Composite labelComposite = new Composite(histoComposite, SWT.NULL);
		// labelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// labelComposite.setLayout(fillLayout);
		//
		// labels = new ArrayList<CLabel>(3);
		//
		// int iNumberOfMarkerPoints = store
		// .getInt(PreferenceConstants.GENE_EXPRESSION_PREFIX
		// + PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);
		//
		// for (int count = 0; count < iNumberOfMarkerPoints; count++) {
		// CLabel label = new CLabel(labelComposite, SWT.NONE);
		// labels.add(label);
		// if (count == iNumberOfMarkerPoints - 1) {
		// label.setAlignment(SWT.RIGHT);
		// } else if (count > 0) {
		// label.setAlignment(SWT.CENTER);
		// }
		// }
		//
		// updateColorLabel();

	}

	private void updateColorLabel() {

		int iNumberOfMarkerPoints = store
				.getInt(PreferenceConstants.GENE_EXPRESSION_PREFIX
						+ PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);

		Color[] alColor = new Color[iNumberOfMarkerPoints];
		int[] iArColorMarkerPoints = new int[iNumberOfMarkerPoints - 1];
		for (int iCount = 1; iCount <= iNumberOfMarkerPoints; iCount++) {

			float normalizedValue = store
					.getFloat(PreferenceConstants.GENE_EXPRESSION_PREFIX
							+ PreferenceConstants.COLOR_MARKER_POINT_VALUE + iCount);

			double correspondingValue = ((ATableBasedDataDomain) dataDomain).getDataTable()
					.getRawForNormalized(normalizedValue);

			labels.get(iCount - 1).setText(Formatter.formatNumber(correspondingValue));
			int iColorMarkerPoint = (int) (100 * normalizedValue);

			// Gradient label does not need the 0 point
			if (iColorMarkerPoint != 0) {
				iArColorMarkerPoints[iCount - 2] = iColorMarkerPoint;
			}

			String color = store.getString(PreferenceConstants.GENE_EXPRESSION_PREFIX
					+ PreferenceConstants.COLOR_MARKER_POINT_COLOR + iCount);

			int[] iArColor = ConversionTools.getIntColorFromString(color);

			alColor[iCount - 1] = new Color(PlatformUI.getWorkbench().getDisplay(),
					iArColor[0], iArColor[1], iArColor[2]);
		}

		colorMappingPreviewLabel.setBackground(alColor, iArColorMarkerPoints);
		colorMappingPreviewLabel.update();
	}

	public static void createToolBarItems(int iViewID) {
		alToolbar = new ArrayList<IAction>();
	}

	@Override
	public void handleRedrawView() {
		colorMappingPreviewLabel.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateColorLabel();
			}
		});
	}

	@Override
	public void handleUpdateView() {
		handleRedrawView();
	}

	@Override
	public void handleClearSelections() {
		// nothing to do here
	}

	@Override
	public void registerEventListeners() {

		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {

		if (redrawViewListener != null) {
			eventPublisher.removeListener(redrawViewListener);
			redrawViewListener = null;
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
			determineDataDomain(serializedView);
		else
			serializedView.setDataDomainID(dataDomain.getDataDomainID());
	}

	@Override
	public String getViewGUIID() {
		return GLHistogram.VIEW_TYPE;
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
		// updateColorLabel();
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}
}
