package org.caleydo.rcp.views.opengl;

import java.util.ArrayList;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.event.view.storagebased.ClearSelectionsEvent;
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
	implements IViewCommandHandler {
	public static final String ID = "org.caleydo.rcp.views.opengl.GLHistogramView";

	private CLabel colorMappingPreviewLabel;
	
	protected RedrawViewListener redrawViewListener = null;
	protected ClearSelectionsListener clearSelectionsListener = null;

	/**
	 * Constructor.
	 */
	public GLHistogramView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite baseComposite = new Composite(parent, SWT.NULL);
		baseComposite.setLayout(new GridLayout(1, false));
		super.createPartControl(baseComposite);
		swtComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite buttonComposite = new Composite(baseComposite, SWT.NULL);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new FillLayout());
		// Button button = new Button(buttonComposite, SWT.PUSH);
		colorMappingPreviewLabel = new CLabel(buttonComposite, SWT.SHADOW_IN);

		updateColorLabel();
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
		// button.setText("true");
		createGLCanvas();
		createGLEventListener(ECommandType.CREATE_GL_HISTOGRAM, glCanvas.getID(), true);

	}

	private void updateColorLabel() {

		PreferenceStore store = GeneralManager.get().getPreferenceStore();
		int iNumberOfMarkerPoints =
			store.getInt(PreferenceConstants.GENE_EXPRESSION_PREFIX + PreferenceConstants.NUMBER_OF_COLOR_MARKER_POINTS);

		Color[] alColor = new Color[iNumberOfMarkerPoints];
		int[] iArColorMarkerPoints = new int[iNumberOfMarkerPoints - 1];
		for (int iCount = 1; iCount <= iNumberOfMarkerPoints; iCount++) {
			int iColorMarkerPoint =
				(int) (100 * store.getFloat(PreferenceConstants.GENE_EXPRESSION_PREFIX + PreferenceConstants.COLOR_MARKER_POINT_VALUE
					+ iCount));

			// Gradient label does not need the 0 point
			if (iColorMarkerPoint != 0) {
				iArColorMarkerPoints[iCount - 2] = iColorMarkerPoint;
			}

			String color =
				store.getString(PreferenceConstants.GENE_EXPRESSION_PREFIX + PreferenceConstants.COLOR_MARKER_POINT_COLOR + iCount);
			
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

}
