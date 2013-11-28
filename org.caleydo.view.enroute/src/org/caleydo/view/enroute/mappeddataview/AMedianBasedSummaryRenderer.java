/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.text.DecimalFormat;

import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.util.base.ILabelProvider;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.view.enroute.EPickingType;

/**
 * @author Christian
 *
 */
public abstract class AMedianBasedSummaryRenderer extends ADataRenderer {

	protected float normalizedIQRMin = 0;
	protected float normalizedIQRMax = 0;
	protected float rawIQRMin = 0;
	protected float rawIQRMax = 0;
	protected AdvancedDoubleStatistics rawStats;
	protected AdvancedDoubleStatistics normalizedStats;

	/**
	 * @param contentRenderer
	 */
	public AMedianBasedSummaryRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
		if (contentRenderer.resolvedRowID == null)
			return;

		VirtualArray va = contentRenderer.columnPerspective.getVirtualArray();

		normalizedIQRMin = Float.MAX_VALUE;
		normalizedIQRMax = Float.MIN_VALUE;
		double[] rawValues = new double[va.size()];
		double[] normalizedValues = new double[va.size()];
		for (int i = 0; i < va.size(); i++) {
			Integer id = va.get(i);
			normalizedValues[i] = contentRenderer.dataDomain.getNormalizedValue(contentRenderer.resolvedRowIDType,
					contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, id);
			Object rawValue = contentRenderer.dataDomain.getRaw(contentRenderer.resolvedRowIDType,
					contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, id);
			if (rawValue instanceof Integer) {
				rawValues[i] = ((Integer) rawValue).doubleValue();
			} else if (rawValue instanceof Float) {
				rawValues[i] = ((Float) rawValue).doubleValue();
			} else if (rawValue instanceof Double) {
				rawValues[i] = ((Double) rawValue).doubleValue();
			} else {
				throw new IllegalStateException("The value " + rawValue + " is not supported.");
			}
		}

		if (rawValues.length == 0 || normalizedValues.length == 0)
			return;

		rawStats = AdvancedDoubleStatistics.of(rawValues);
		normalizedStats = AdvancedDoubleStatistics.of(normalizedValues);
		double lowerIQRBounds = normalizedStats.getQuartile25() - normalizedStats.getIQR() * 1.5;
		double upperIQRBounds = normalizedStats.getQuartile75() + normalizedStats.getIQR() * 1.5;

		for (int i = 0; i < va.size(); i++) {
			Integer id = va.get(i);
			Float value = contentRenderer.dataDomain.getNormalizedValue(contentRenderer.resolvedRowIDType,
					contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, id);
			Object rawValue = contentRenderer.dataDomain.getRaw(contentRenderer.resolvedRowIDType,
					contentRenderer.resolvedRowID, contentRenderer.resolvedColumnIDType, id);
			if (value < normalizedIQRMin && value >= lowerIQRBounds) {
				normalizedIQRMin = value;

				if (rawValue instanceof Integer) {
					rawIQRMin = ((Integer) rawValue).floatValue();
				} else if (rawValue instanceof Float) {
					rawIQRMin = ((Float) rawValue).floatValue();
				} else if (rawValue instanceof Double) {
					rawIQRMin = ((Double) rawValue).floatValue();
				}
			}
			if (value > normalizedIQRMax && value <= upperIQRBounds) {
				normalizedIQRMax = value;
				if (rawValue instanceof Integer) {
					rawIQRMax = ((Integer) rawValue).floatValue();
				} else if (rawValue instanceof Float) {
					rawIQRMax = ((Float) rawValue).floatValue();
				} else if (rawValue instanceof Double) {
					rawIQRMax = ((Double) rawValue).floatValue();
				}
			}
		}

		registerPickingListeners();
	}

	protected void registerPickingListeners() {
		contentRenderer.parent.pickingListenerManager.addIDPickingTooltipListener(new ILabelProvider() {

			@Override
			public String getLabel() {
				DecimalFormat df = new DecimalFormat("#.##");
				return "Median: " + df.format(rawStats.getMedian()) + "\n1st Quartile: "
						+ df.format(rawStats.getQuartile25()) + "\n3rd Quartile: "
						+ df.format(rawStats.getQuartile75()) + "\nLowest value in 1.5xIQR range: "
						+ df.format(rawIQRMin) + "\nHighest value in 1.5xIQR range: " + df.format(rawIQRMax);
			}

			@Override
			public String getProviderName() {
				return "";
			}
		}, getPickingType(), getPickingID());
	}

	protected String getPickingType() {
		return EPickingType.SUMMARY_STATISTICS.name();
	}

	protected int getPickingID() {
		return hashCode();
	}

}
