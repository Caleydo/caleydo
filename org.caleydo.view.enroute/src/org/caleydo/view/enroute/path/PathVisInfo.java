package org.caleydo.view.enroute.path;

import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;

/**
 * VisInfo for {@link APathwayPathRenderer}.
 *
 * @author Christian Partl
 *
 */
public class PathVisInfo implements IEmbeddedVisualizationInfo {

	public PathVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return null;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return EScalingEntity.PATHWAY_VERTEX;
	}

}
