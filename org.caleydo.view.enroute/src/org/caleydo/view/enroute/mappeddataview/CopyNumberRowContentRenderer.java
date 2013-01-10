package org.caleydo.view.enroute.mappeddataview;

import java.util.ArrayList;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.perspective.variable.AVariablePerspective;
import org.caleydo.core.data.perspective.variable.RecordPerspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.DimensionVirtualArray;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.datadomain.genetic.GeneticDataDomain;
import org.caleydo.view.enroute.EPickingType;

public class CopyNumberRowContentRenderer
	extends ACategoricalRowContentRenderer {

	public CopyNumberRowContentRenderer(IContentRendererInitializor contentRendererInitializor) {
		super(contentRendererInitializor);
	}

	public CopyNumberRowContentRenderer(Integer geneID, Integer davidID, GeneticDataDomain dataDomain,
			TablePerspective tablePerspective, AVariablePerspective<?, ?, ?, ?> experimentPerspective,
			AGLView parentView, MappedDataRenderer parent, Group group, boolean isHighlightMode) {

		super(geneID, davidID, dataDomain, tablePerspective, experimentPerspective, parentView, parent, group,
				isHighlightMode);
	}

	@Override
	public void init() {
		if (geneID == null)
			return;
		if (experimentPerspective instanceof RecordPerspective) {

			DimensionVirtualArray dimensionVirtualArray = new DimensionVirtualArray();
			dimensionVirtualArray.append(geneID);
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(),
					(RecordVirtualArray) experimentPerspective.getVirtualArray(), dimensionVirtualArray, 5);
		}
		else {
			RecordVirtualArray recordVirtualArray = new RecordVirtualArray();
			recordVirtualArray.append(geneID);
			histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(), recordVirtualArray,
					(DimensionVirtualArray) experimentPerspective.getVirtualArray(), 5);
		}
		registerPickingListener();

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderAllBars(GL2 gl, ArrayList<SelectionType> geneSelectionTypes) {
		if (x / experimentPerspective.getVirtualArray().size() < parentView.getPixelGLConverter()
				.getGLWidthForPixelWidth(3)) {
			useShading = false;
		}
		float xIncrement = x / experimentPerspective.getVirtualArray().size();
		int experimentCount = 0;

		// float[] tempTopBarColor = topBarColor;
		// float[] tempBottomBarColor = bottomBarColor;

		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, 0.5f * y, z);
		gl.glVertex3f(x, 0.5f * y, z);
		gl.glEnd();

		for (Integer sampleID : experimentPerspective.getVirtualArray()) {

			float value;
			if (geneID != null) {
				value = dataDomain.getNormalizedGeneValue(geneID, sampleID);

				if (value < 0.5001 && value > 0.499) {
					experimentCount++;
					continue;
				}

				ArrayList<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager.getSelectionTypes(
						sampleIDType, sampleID);

				float[] baseColor = dataDomain.getColorMapper().getColor(value);
				float[] topBarColor = baseColor;
				float[] bottomBarColor = baseColor;

				colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
						geneSelectionTypes));

				ArrayList<SelectionType> selectionTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
						geneSelectionTypes);

				if (isHighlightMode
						&& !(selectionTypes.contains(SelectionType.MOUSE_OVER) || selectionTypes
								.contains(SelectionType.SELECTION))) {
					experimentCount++;
					continue;
				}

				if (isHighlightMode) {
					colorCalculator.setBaseColor(new Color(baseColor[0], baseColor[1], baseColor[2]));

					colorCalculator.calculateColors(selectionTypes);

					topBarColor = colorCalculator.getPrimaryColor().getRGB();
					bottomBarColor = colorCalculator.getSecondaryColor().getRGB();
				}

				float leftEdge = xIncrement * experimentCount;

				float upperEdge = value * y;

				// gl.glPushName(parentView.getPickingManager().getPickingID(
				// parentView.getID(), PickingType.GENE.name(), davidID));

				Integer resolvedSampleID = sampleIDMappingManager.getID(dataDomain.getSampleIDType(),
						parent.sampleIDType, sampleID);
				gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
						EPickingType.SAMPLE.name(), resolvedSampleID));

				gl.glColor3fv(bottomBarColor, 0);
				gl.glBegin(GL2.GL_QUADS);

				gl.glVertex3f(leftEdge, 0.5f * y, z);
				if (useShading) {
					gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
				}
				gl.glVertex3f(leftEdge + xIncrement, 0.5f * y, z);

				if (useShading) {
					gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
				}
				else {
					gl.glColor3fv(topBarColor, 0);
				}
				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				gl.glColor3fv(topBarColor, 0);

				gl.glVertex3f(leftEdge, upperEdge, z);

				gl.glEnd();

				gl.glPopName();
				// gl.glPopName();
				experimentCount++;
				// topBarColor = tempTopBarColor;
				// bottomBarColor = tempBottomBarColor;
			}

		}
	}

}
