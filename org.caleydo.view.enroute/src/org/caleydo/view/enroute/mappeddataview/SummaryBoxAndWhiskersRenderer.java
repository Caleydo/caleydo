/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.view.enroute.correlation.NumericalDataClassifier;
import org.caleydo.view.enroute.correlation.SimpleCategory;
import org.caleydo.view.enroute.mappeddataview.overlay.IDataCellOverlayProvider;

/**
 * @author Christian
 *
 */
public class SummaryBoxAndWhiskersRenderer extends AMedianBasedSummaryRenderer {

	/**
	 * @param contentRenderer
	 */
	public SummaryBoxAndWhiskersRenderer(ContentRenderer contentRenderer) {
		super(contentRenderer);
	}

	@Override
	public void render(GL2 gl, float x, float y, List<SelectionType> selectionTypes) {

		if (contentRenderer.isHighlightMode && inferredNormalizeFunction != null) {
			IDataCellOverlayProvider provider = contentRenderer.parentView.getCorrelationManager().getOverlayProvider(
					contentRenderer);
			if (provider != null) {
				IBoxAndWhiskersOverlay overlay = provider.getOverlay(this);
				if (overlay != null)
					overlay.render(gl, x, y);
			}
		}

		if (contentRenderer.resolvedRowID == null || contentRenderer.isHighlightMode || normalizedStats == null)
			return;

		// float[] color = MappedDataRenderer.SUMMARY_BAR_COLOR.getRGBA();

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		// float[] color = MappedDataRenderer.SUMMARY_BAR_COLOR.getRGBA();

		gl.glPushName(contentRenderer.parentView.getPickingManager().getPickingID(contentRenderer.parentView.getID(),
				getPickingType(), getPickingID()));

		float firstQuantrileBoundary = (float) (normalizedStats.getQuartile25()) * x;
		float thirdQuantrileBoundary = (float) (normalizedStats.getQuartile75()) * x;

		gl.glBegin(GL2GL3.GL_QUADS);
		// gl.glColor4fv(color, 0);
		gl.glColor3fv(contentRenderer.dataDomain.getColor().getRGB(), 0);
		gl.glVertex3f(firstQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3 * 2, z);
		gl.glVertex3f(firstQuantrileBoundary, y / 3 * 2, z);
		gl.glEnd();

		float iqrMin = normalizedIQRMin * x;
		float iqrMax = normalizedIQRMax * x;

		float iqrLineTailHeight = contentRenderer.parentView.getPixelGLConverter().getGLHeightForPixelHeight(5);

		// gl.glPushAttrib(GL2.GL_LINE_BIT);
		// gl.glLineWidth(2);
		// Median
		gl.glColor3f(1f, 1f, 1f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3d(normalizedStats.getMedian() * x, y / 3 * 2, z);
		gl.glVertex3d(normalizedStats.getMedian() * x, y / 3, z);

		// Whiskers
		gl.glColor3f(0, 0, 0);
		gl.glVertex3f(iqrMin, y / 2, z);
		gl.glVertex3f(firstQuantrileBoundary, y / 2, z);

		gl.glVertex3f(thirdQuantrileBoundary, y / 2, z);
		gl.glVertex3f(iqrMax, y / 2, z);

		// gl.glLineWidth(0.6f);
		//
		gl.glVertex3f(iqrMin, y / 2 - iqrLineTailHeight, z);
		gl.glVertex3f(iqrMin, y / 2 + iqrLineTailHeight, z);

		gl.glVertex3f(iqrMax, y / 2 - iqrLineTailHeight, z);
		gl.glVertex3f(iqrMax, y / 2 + iqrLineTailHeight, z);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_LOOP);
		// gl.glColor4fv(color, 0);
		gl.glColor3f(0f, 0f, 0f);
		gl.glVertex3f(firstQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3, z);
		gl.glVertex3d(thirdQuantrileBoundary, y / 3 * 2, z);
		gl.glVertex3f(firstQuantrileBoundary, y / 3 * 2, z);
		gl.glEnd();

		// gl.glPopAttrib();

		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		gl.glColor4f(0f, 0f, 0f, outlierAlhpa(outliers.size()));

		gl.glPushAttrib(GL2.GL_POINT_BIT);
		gl.glPointSize(2f);

		gl.glBegin(GL.GL_POINTS);

		for (float outlier : outliers) {
			gl.glVertex3f(outlier * x, y / 2, z);
		}

		gl.glEnd();

		gl.glPopAttrib();

		gl.glPopName();
	}

	private void renderColorOverlay(GL2 gl, Color color, float left, float bottom, float right, float top) {
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glBegin(GL2GL3.GL_QUADS);

		gl.glColor4fv(color.getRGBA(), 0);
		gl.glVertex3f(left, bottom, z);
		gl.glVertex3f(right, bottom, z);
		gl.glVertex3f(right, top, z);
		gl.glVertex3f(left, top, z);

		gl.glEnd();
	}

	private float outlierAlhpa(int size) {
		if (size < 10)
			return 1.0f;
		float v = 5.0f / size;
		if (v < 0.05f)
			return 0.05f;
		if (v > 1)
			return 1;
		return v;
	}

	public interface IBoxAndWhiskersOverlay {
		public void render(GL2 gl, float x, float y);
	}

	public class BoxAndWhiskersNumericalClassificationOverlay implements IBoxAndWhiskersOverlay {
		private final NumericalDataClassifier classifier;

		public BoxAndWhiskersNumericalClassificationOverlay(NumericalDataClassifier classifier) {
			this.classifier = classifier;
		}

		@Override
		public void render(GL2 gl, float x, float y) {
			float threshold = classifier.getThreshold();
			float normalizedThreshold = (float) inferredNormalizeFunction.apply(threshold);
			float thresholdX = normalizedThreshold * x;
			List<SimpleCategory> categories = classifier.getDataClasses();

			renderColorOverlay(gl, categories.get(0).color.transparentCopy(0.6f), 0, 0, thresholdX, y);
			renderColorOverlay(gl, categories.get(1).color.transparentCopy(0.6f), thresholdX, 0, x, y);
		}
	}

	public class ColorOverlay implements IBoxAndWhiskersOverlay {

		private final Color color;

		public ColorOverlay(Color color) {
			this.color = color;
		}

		@Override
		public void render(GL2 gl, float x, float y) {
			renderColorOverlay(gl, color.transparentCopy(0.6f), 0, 0, x, y);

		}

	}

}
