/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.histogram;

import java.util.ArrayList;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.format.Formatter;
import org.caleydo.core.util.mapping.color.ChooseColorMappingDialog;
import org.caleydo.core.util.mapping.color.ColorMarkerPoint;
import org.caleydo.core.util.mapping.color.UpdateColorMappingListener;
import org.caleydo.core.view.opengl.canvas.listener.IViewCommandHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class RcpGLColorMapperHistogramView extends RcpGLHistogramView implements IViewCommandHandler {

	private CLabel colorMappingPreview;

	private ArrayList<CLabel> labels;
	protected UpdateColorMappingListener updateViewListener;

	/**
	 *
	 */
	public RcpGLColorMapperHistogramView() {
	}

	@Override
	public void redrawView() {

		/** The color scale below the histogram */
		colorMappingPreview = new CLabel(histoComposite, SWT.SHADOW_IN);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 10;
		gridData.grabExcessHorizontalSpace = true;
		colorMappingPreview.setLayoutData(gridData);
		colorMappingPreview.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				openColorMapDialog();

			}
		});

		FillLayout fillLayout = new FillLayout();
		Composite labelComposite = new Composite(histoComposite, SWT.NULL);
		labelComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		labelComposite.setLayout(fillLayout);

		labels = new ArrayList<CLabel>(3);

		int numberOfMarkerPoints = dataDomain.getColorMapper().getMarkerPoints().size();

		for (int count = 0; count < numberOfMarkerPoints; count++) {
			CLabel label = new CLabel(labelComposite, SWT.NONE);
			labels.add(label);
			if (count == numberOfMarkerPoints - 1) {
				label.setAlignment(SWT.RIGHT);
			} else if (count > 0) {
				label.setAlignment(SWT.CENTER);
			}
		}

		updateColorMappingPreview();

		Button changeColorButton = new Button(histoComposite, SWT.PUSH);
		changeColorButton.setText("Colormap");
		changeColorButton.setToolTipText("Choose a colormap for this dataset");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		// gridData.heightHint = 20;
		gridData.grabExcessHorizontalSpace = true;
		changeColorButton.setLayoutData(gridData);
		changeColorButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(MouseEvent e) {
				openColorMapDialog();
			}
		});
	}

	@Override
	public void handleRedrawView() {
		colorMappingPreview.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateColorMappingPreview();
			}
		});
	}

	/**
	 * Opens the {@link ChooseColorMappingDialog}.
	 */
	private void openColorMapDialog() {
		ChooseColorMappingDialog dialog = new ChooseColorMappingDialog(new Shell(SWT.APPLICATION_MODAL), dataDomain);
		// dialog.setPossibleDataDomains(availableDomains);
		dialog.setBlockOnOpen(true);
		dialog.open();

		updateColorMappingPreview();
	}

	private void updateColorMappingPreview() {

		if(colorMappingPreview == null)
			return;
		if (!dataDomain.getTable().isDataHomogeneous())
			return;
		ArrayList<ColorMarkerPoint> markerPoints = dataDomain.getColorMapper().getMarkerPoints();

		Color[] alColor = new Color[markerPoints.size()];
		int[] colorMarkerPoints = new int[markerPoints.size() - 1];
		for (int iCount = 1; iCount <= markerPoints.size(); iCount++) {

			float normalizedValue = markerPoints.get(iCount - 1).getMappingValue();

			double correspondingValue = (dataDomain).getTable().getRawForNormalized(normalizedValue);

			if (labels != null)
				labels.get(iCount - 1).setText(Formatter.formatNumber(correspondingValue));

			int colorMarkerPoint = (int) (100 * normalizedValue);

			// Gradient label does not need the 0 point
			if (colorMarkerPoint != 0) {
				colorMarkerPoints[iCount - 2] = colorMarkerPoint;
			}

			int[] color = markerPoints.get(iCount - 1).getIntColor();

			alColor[iCount - 1] = new Color(PlatformUI.getWorkbench().getDisplay(), color[0], color[1], color[2]);
		}

		colorMappingPreview.setBackground(alColor, colorMarkerPoints);
		colorMappingPreview.update();
	}

	@Override
	public void handleClearSelections() {
		// TODO Auto-generated method stub

	}

	@Override
	public void registerEventListeners() {

		super.registerEventListeners();

		// clearSelectionsListener = new SelectionCommandListener();
		// clearSelectionsListener.setHandler(this);
		// eventPublisher.addListener(SelectionCommandEvent.class, clearSelectionsListener);
	}

	@Override
	public void unregisterEventListeners() {

		super.unregisterEventListeners();

		if (updateViewListener != null) {
			eventPublisher.removeListener(updateViewListener);
			updateViewListener = null;
		}
		// if (clearSelectionsListener != null) {
		// eventPublisher.removeListener(clearSelectionsListener);
		// clearSelectionsListener = null;
		// }
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		super.setDataDomain(dataDomain);
		updateColorMappingPreview();
	}

}
