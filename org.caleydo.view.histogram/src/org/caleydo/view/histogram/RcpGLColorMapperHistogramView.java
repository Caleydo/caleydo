/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.histogram;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.color.mapping.ChooseColorMappingDialog;
import org.caleydo.core.util.color.mapping.ColorMarkerPoint;
import org.caleydo.core.util.format.Formatter;
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

public class RcpGLColorMapperHistogramView extends RcpGLHistogramView {

	private CLabel colorMappingPreview;

	private ArrayList<CLabel> labels;

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

		int numberOfMarkerPoints = dataDomain.getTable().getColorMapper().getMarkerPoints().size();

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

		if (colorMappingPreview == null)
			return;
		if (!dataDomain.getTable().isDataHomogeneous())
			return;
		List<ColorMarkerPoint> markerPoints = dataDomain.getTable().getColorMapper().getMarkerPoints();

		Color[] alColor = new Color[markerPoints.size()];
		int[] colorMarkerPoints = new int[markerPoints.size() - 1];
		for (int count = 1; count <= markerPoints.size(); count++) {

			float normalizedValue = markerPoints.get(count - 1).getMappingValue();

			if (dataDomain.getTable() instanceof NumericalTable) {
				double correspondingValue = ((NumericalTable) dataDomain.getTable()).getRawForNormalized(dataDomain
						.getTable().getDefaultDataTransformation(), normalizedValue);

				if (labels != null)
					labels.get(count - 1).setText(Formatter.formatNumber(correspondingValue));
			}
			int colorMarkerPoint = (int) (100 * normalizedValue);

			// Gradient label does not need the 0 point
			if (colorMarkerPoint != 0) {
				colorMarkerPoints[count - 2] = colorMarkerPoint;
			}

			int[] color = markerPoints.get(count - 1).getColor().getIntRGBA();

			alColor[count - 1] = new Color(PlatformUI.getWorkbench().getDisplay(), color[0], color[1], color[2]);
		}

		colorMappingPreview.setBackground(alColor, colorMarkerPoints);
		colorMappingPreview.update();
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {
		super.setDataDomain(dataDomain);
		updateColorMappingPreview();
	}

}
