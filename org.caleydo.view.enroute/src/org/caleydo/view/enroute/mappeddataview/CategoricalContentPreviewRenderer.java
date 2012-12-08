/**
 *
 */
package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.Histogram;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.util.color.Color;

/**
 * @author Christian
 *
 */
public class CategoricalContentPreviewRenderer
	extends ContentRenderer {

	private Histogram histogram;

	/**
	 * @param contentRendererInitializor
	 */
	public CategoricalContentPreviewRenderer(IContentRendererInitializor contentRendererInitializor) {
		super(contentRendererInitializor);
	}

	@Override
	public void init() {
		if (geneID == null)
			return;

		if (experimentPerspective instanceof RecordPerspective) {

			DimensionVirtualArray dimensionVirtualArray = new DimensionVirtualArray();
			dimensionVirtualArray.append(geneID);
			// FIXME: Bad Hack for determination of bucket count
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(),
					(RecordVirtualArray) experimentPerspective.getVirtualArray(), dimensionVirtualArray, dataDomain
							.getLabel().toLowerCase().contains("copy") ? 5 : 2);
		}
		else {
			RecordVirtualArray recordVirtualArray = new RecordVirtualArray();
			recordVirtualArray.append(geneID);
			// FIXME: Bad Hack for determination of bucket count
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(), recordVirtualArray,
					(DimensionVirtualArray) experimentPerspective.getVirtualArray(), dataDomain.getLabel()
							.toLowerCase().contains("copy") ? 5 : 2);
		}
	}

	@Override
	public void renderContent(GL2 gl) {
		if (geneID == null)
			return;

		ArrayList<SelectionType> selectionTypes = parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		// if (selectionTypes.size() > 0
		// && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
		// topBarColor = MappedDataRenderer.SUMMARY_BAR_COLOR;
		// bottomBarColor = topBarColor;
		// }

		int bucketCount = 0;
		// float barWidth = y / histogram.size();
		// float renderWith = x
		// - parentView.getPixelGLConverter().getGLWidthForPixelWidth(20);

		int sumValues = 0;

		for (int i = 0; i < histogram.size(); i++) {
			sumValues += histogram.get(i);
		}

		float step = 0;
		if (sumValues != 0)
			step = 0.85f * y / sumValues;

		float currentPositionY = 0;

		for (int bucketNumber = 0; bucketNumber < histogram.size(); bucketNumber++) {
			ArrayList<SelectionType> sampleSelectionTypes = new ArrayList<SelectionType>();
			for (Integer sampleID : histogram.getIDsForBucket(bucketNumber)) {
				sampleSelectionTypes.addAll(parent.sampleSelectionManager.getSelectionTypes(sampleIDType, sampleID));
			}
			float[] baseColor = dataDomain.getColorMapper().getColor((float) bucketCount / (histogram.size() - 1));
			colorCalculator.setBaseColor(new Color(baseColor[0], baseColor[1], baseColor[2]));

			// colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(selectionTypes,
			// sampleSelectionTypes));

			float currentBarHeight = histogram.get(bucketNumber) * step;

			colorCalculator.calculateColors(selectionTypes);

			float[] topBarColor = colorCalculator.getPrimaryColor().getRGBA();
			float[] bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();

			gl.glBegin(GL2.GL_QUADS);
			gl.glColor3fv(topBarColor, 0);
			gl.glVertex3f(0, currentPositionY, z);
			gl.glColor3fv(bottomBarColor, 0);
			gl.glVertex3d(x, currentPositionY, z);
			gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
			gl.glVertex3d(x, currentPositionY + currentBarHeight, z);
			gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
			gl.glVertex3d(0, currentPositionY + currentBarHeight, z);
			gl.glEnd();

			gl.glColor3f(0.2f, 0.2f, 0.2f);
			gl.glLineWidth(0.1f);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(0, currentPositionY, z);
			gl.glVertex3d(x, currentPositionY, z);
			gl.glVertex3d(x, currentPositionY + currentBarHeight, z);
			gl.glVertex3d(0, currentPositionY + currentBarHeight, z);
			gl.glEnd();

			currentPositionY += currentBarHeight;
			bucketCount++;
		}
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
