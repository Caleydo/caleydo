package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
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

public class CopyNumberRowContentRenderer extends ACategoricalRowContentRenderer {

	public CopyNumberRowContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType,
			Integer resolvedRowID, ATableBasedDataDomain dataDomain, Perspective columnPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode) {
		super(rowIDType, rowID, resolvedRowIDType, resolvedRowID, dataDomain, columnPerspective, parentView, parent,
				group, isHighlightMode);
	}

	@Override
	public void init() {
		if (resolvedRowID == null)
			return;

		VirtualArray dimensionVirtualArray = new VirtualArray(resolvedRowIDType);
		dimensionVirtualArray.append(resolvedRowID);
		histogram = TablePerspectiveStatistics.calculateHistogram(dataDomain.getTable(),
				columnPerspective.getVirtualArray(), dimensionVirtualArray);

		registerPickingListener();

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void renderAllBars(GL2 gl, List<SelectionType> geneSelectionTypes) {
		if (resolvedRowID == null)
			return;
		if (x / columnPerspective.getVirtualArray().size() < parentView.getPixelGLConverter()
				.getGLWidthForPixelWidth(3)) {
			useShading = false;
		}
		float xIncrement = x / columnPerspective.getVirtualArray().size();
		int experimentCount = 0;

		// float[] tempTopBarColor = topBarColor;
		// float[] tempBottomBarColor = bottomBarColor;

		gl.glColor3f(0, 0, 0);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(0, 0.5f * y, z);
		gl.glVertex3f(x, 0.5f * y, z);
		gl.glEnd();

		for (Integer columnID : columnPerspective.getVirtualArray()) {

			float value;
			if (rowID != null) {
				value = dataDomain.getNormalizedValue(resolvedRowIDType, resolvedRowID, resolvedColumnIDType, columnID);

				if (value < 0.5001 && value > 0.499) {
					experimentCount++;
					continue;
				}

				List<SelectionType> experimentSelectionTypes = parent.sampleSelectionManager.getSelectionTypes(
						columnIDType, columnID);

				float[] baseColor = dataDomain.getColorMapper().getColor(value);
				float[] topBarColor = baseColor;
				float[] bottomBarColor = baseColor;

				colorCalculator.calculateColors(Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
						geneSelectionTypes));

				List<SelectionType> selectionTypes = Algorithms.mergeListsToUniqueList(experimentSelectionTypes,
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
				// parentView.getID(), PickingType.ROW_PRIMARY.name(), rowID));

				Integer resolvedColumnID = columnIDMappingManager.getID(dataDomain.getPrimaryIDType(columnIDType),
						parent.sampleIDType, columnID);
				gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
						EPickingType.SAMPLE.name(), resolvedColumnID));

				gl.glColor3fv(bottomBarColor, 0);
				gl.glBegin(GL2.GL_QUADS);

				gl.glVertex3f(leftEdge, 0.5f * y, z);
				if (useShading) {
					gl.glColor3f(bottomBarColor[0] * 0.9f, bottomBarColor[1] * 0.9f, bottomBarColor[2] * 0.9f);
				}
				gl.glVertex3f(leftEdge + xIncrement, 0.5f * y, z);

				if (useShading) {
					gl.glColor3f(topBarColor[0] * 0.9f, topBarColor[1] * 0.9f, topBarColor[2] * 0.9f);
				} else {
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
