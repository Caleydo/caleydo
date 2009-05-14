package org.caleydo.rcp.view.opengl;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.view.ClearSelectionsEvent;
import org.caleydo.core.manager.event.view.storagebased.RedrawViewEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.conversion.ConversionTools;
import org.caleydo.core.util.preferences.PreferenceConstants;
import org.caleydo.core.view.opengl.canvas.listener.ClearSelectionsListener;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.caleydo.core.view.opengl.canvas.listener.RedrawViewListener;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class GLHistogramView
	extends AGLViewPart
	implements IViewCommandHandler, IListenerOwner {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLHistogramView";

	private CLabel colorMappingPreviewLabel;

	private ArrayList<CLabel> labels;

	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;

	PreferenceStore store = GeneralManager.get().getPreferenceStore();

	/**
	 * Constructor.
	 */
	public GLHistogramView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite baseComposite = new Composite(parent, SWT.NULL);
		GridLayout baseLayout = new GridLayout(1, false);
		baseLayout.verticalSpacing = 2;
		baseComposite.setLayout(baseLayout);

		super.createPartControl(baseComposite);
		parentComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Composite colorMappingComposite = new Composite(baseComposite, SWT.NULL);
		// colorMappingComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// GridLayout layout = new GridLayout(1, false);
		// layout.marginWidth = 0;
		// layout.marginHeight = 0;
		// layout.verticalSpacing = 0;
		// colorMappingComposite.setLayout(layout);
		// Button button = new Button(buttonComposite, SWT.PUSH);
		colorMappingPreviewLabel = new CLabel(baseComposite, SWT.SHADOW_IN);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 10;
		gridData.grabExcessHorizontalSpace = true;
		// colorData.
		colorMappingPreviewLabel.setLayoutData(gridData);
		// colorMappingPreviewLabel.setBounds(0, 0, buttonComposite.getSize().x, 3);//setSize(, 3);

		colorMappingPreviewLabel.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub
				PreferenceDialog pref =
					PreferencesUtil.createPreferenceDialogOn(new Shell(),
						"org.caleydo.rcp.preferences.ColorMappingPreferencePage", null, null);

				if (pref != null) {
					pref.open();
				}
			}

			@Override
			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

		});

		FillLayout fillLayout = new FillLayout();
		Composite labelComposite = new Composite(baseComposite, SWT.NULL);
		labelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		labelComposite.setLayout(fillLayout);

		labels = new ArrayList<CLabel>(3);

		int iNumberOfMarkerPoints =
			store.getInt(PreferenceConstants.GENE_EXPRESSION_PREFIX
				+ PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);

		for (int count = 0; count < iNumberOfMarkerPoints; count++) {
			CLabel label = new CLabel(labelComposite, SWT.NONE);
			labels.add(label);
			if (count == iNumberOfMarkerPoints - 1) {
				label.setAlignment(SWT.RIGHT);
			}
			else if (count > 0) {
				label.setAlignment(SWT.CENTER);
			}
		}

		updateColorLabel();

		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_HISTOGRAM, glCanvas.getID(), true);

	}

	private void updateColorLabel() {

		DecimalFormat decimalFormat;

		int iNumberOfMarkerPoints =
			store.getInt(PreferenceConstants.GENE_EXPRESSION_PREFIX
				+ PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);

		Color[] alColor = new Color[iNumberOfMarkerPoints];
		int[] iArColorMarkerPoints = new int[iNumberOfMarkerPoints - 1];
		for (int iCount = 1; iCount <= iNumberOfMarkerPoints; iCount++) {

			float normalizedValue =
				store.getFloat(PreferenceConstants.GENE_EXPRESSION_PREFIX
					+ PreferenceConstants.COLOR_MARKER_POINT_VALUE + iCount);

			double correspondingValue =
				GeneralManager.get().getUseCase().getSet().getRawForNormalized(normalizedValue);

			if (Math.abs(correspondingValue) > 10000)
				decimalFormat = new DecimalFormat("0.#E0");
			else if (Math.abs(correspondingValue) > 100)
				decimalFormat = new DecimalFormat("#####");
			else if (Math.abs(correspondingValue) > 10)
				decimalFormat = new DecimalFormat("#####.#");
			else
				decimalFormat = new DecimalFormat("#####.##");

			labels.get(iCount - 1).setText(decimalFormat.format(correspondingValue));
			int iColorMarkerPoint = (int) (100 * normalizedValue);

			// Gradient label does not need the 0 point
			if (iColorMarkerPoint != 0) {
				iArColorMarkerPoints[iCount - 2] = iColorMarkerPoint;
			}

			String color =
				store.getString(PreferenceConstants.GENE_EXPRESSION_PREFIX
					+ PreferenceConstants.COLOR_MARKER_POINT_COLOR + iCount);

			int[] iArColor = ConversionTools.getIntColorFromString(color);

			alColor[iCount - 1] =
				new Color(PlatformUI.getWorkbench().getDisplay(), iArColor[0], iArColor[1], iArColor[2]);
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
	super.registerEventListeners();
	
		redrawViewListener = new RedrawViewListener();
		redrawViewListener.setHandler(this);
		eventPublisher.addListener(RedrawViewEvent.class, redrawViewListener);

		clearSelectionsListener = new ClearSelectionsListener();
		clearSelectionsListener.setHandler(this);
		eventPublisher.addListener(ClearSelectionsEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
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
	public synchronized void queueEvent(final AEventListener<? extends IListenerOwner> listener,
		final AEvent event) {
		
		parentComposite.getDisplay().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
			}
		});
	}
}
