package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspectiveStatistics;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Algorithms;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.view.enroute.EPickingType;

public class MutationStatusRowContentRenderer extends ACategoricalRowContentRenderer {

	public MutationStatusRowContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType,
			Integer resolvedRowID, ATableBasedDataDomain dataDomain, Perspective columnPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode) {

		super(rowIDType, rowID, resolvedRowIDType, resolvedRowID, dataDomain, columnPerspective, parentView, parent,
				group, isHighlightMode);

	}

	@Override
	public void init() {
		if (rowID == null)
			return;

		VirtualArray dimensionVirtualArray = new VirtualArray(resolvedRowIDType);
		dimensionVirtualArray.append(rowID);
		histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(),
				columnPerspective.getVirtualArray(), dimensionVirtualArray, 2);

		registerPickingListener();

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderAllBars(GL2 gl, List<SelectionType> geneSelectionTypes) {
		float xIncrement = x / columnPerspective.getVirtualArray().size();
		int experimentCount = 0;

		for (Integer columnID : columnPerspective.getVirtualArray()) {

			float value;
			if (rowID != null) {
				value = dataDomain.getNormalizedValue(resolvedRowIDType, rowID, resolvedColumnIDType, columnID);

				List<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager.getSelectionTypes(
						columnIDType, columnID);

				float[] mappedColor = dataDomain.getColorMapper().getColor(value);
				float[] baseColor = new float[] { mappedColor[0], mappedColor[1], mappedColor[2], 1f };

				float[] topBarColor = baseColor;
				float[] bottomBarColor = baseColor;

				List<SelectionType> selectionTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
						geneSelectionTypes);

				if (isHighlightMode
						&& !(selectionTypes.contains(SelectionType.MOUSE_OVER) || selectionTypes
								.contains(SelectionType.SELECTION))) {
					experimentCount++;
					continue;
				}

				if (isHighlightMode) {
					colorCalculator.setBaseColor(new Color(baseColor));

					colorCalculator.calculateColors(selectionTypes);

					topBarColor = colorCalculator.getPrimaryColor().getRGBA();
					bottomBarColor = colorCalculator.getSecondaryColor().getRGBA();
				}

				float leftEdge = xIncrement * experimentCount;
				float upperEdge = value * y;

				// gl.glPushName(parentView.getPickingManager().getPickingID(
				// parentView.getID(), PickingType.GENE.name(), rowID));

				Integer resolvedSampleID = columnIDMappingManager.getID(resolvedColumnIDType, parent.sampleIDType,
						columnID);
				if (resolvedSampleID != null) {
					gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
							EPickingType.SAMPLE.name(), resolvedSampleID));
				}

				gl.glBegin(GL2.GL_QUADS);

				gl.glColor4fv(bottomBarColor, 0);
				gl.glVertex3f(leftEdge, 0, z);
				if (useShading) {
					gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);

				}
				gl.glVertex3f(leftEdge + xIncrement, 0, z);
				if (useShading) {
					gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
				} else {
					gl.glColor4fv(topBarColor, 0);
				}

				gl.glVertex3f(leftEdge + xIncrement, upperEdge, z);
				gl.glColor4fv(topBarColor, 0);

				gl.glVertex3f(leftEdge, upperEdge, z);

				gl.glEnd();
				if (resolvedSampleID != null)
					gl.glPopName();

				// gl.glPopName();
				experimentCount++;
			}

		}
	}

}
