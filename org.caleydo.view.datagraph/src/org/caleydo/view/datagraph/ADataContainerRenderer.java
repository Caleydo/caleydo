package org.caleydo.view.datagraph;

import java.awt.geom.Point2D;
import java.util.List;

import org.caleydo.core.data.container.ADimensionGroupData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout.LayoutRenderer;

public abstract class ADataContainerRenderer extends LayoutRenderer {

	public abstract void setDimensionGroups(
			List<ADimensionGroupData> dimensionGroupDatas);

	public abstract Pair<Point2D, Point2D> getAnchorPointsOfDimensionGroup(
			ADimensionGroupData dimensionGroupData);

}
