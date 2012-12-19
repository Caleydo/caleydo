package org.caleydo.view.parcoords;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;


/**
 * Visualization Info for {@link GLParallelCoordinates}.
 *
 * @author Christian Partl
 *
 */
public class ParCoordsVisInfo implements IEmbeddedVisualizationInfo {

	public ParCoordsVisInfo() {
	}

	@Override
	public EScalingEntity getPrimaryWidthScalingEntity() {
		return EScalingEntity.DIMENSION;
	}

	@Override
	public EScalingEntity getPrimaryHeightScalingEntity() {
		return null;
	}

}
