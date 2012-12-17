package org.caleydo.view.pathway;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

/**
 * VisInfo for {@link GLPathway}
 *
 * @author Christian Partl
 *
 */
public class PathwayVisInfo implements IEmbeddedVisualizationInfo {

	public PathwayVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return null;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return null;
	}

}
