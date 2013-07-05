package org.caleydo.view.histogram;
import org.caleydo.core.view.opengl.layout.util.multiform.IEmbeddedVisualizationInfo;


/**
 * Visualization info for {@link GLHistogram}.
 *
 * @author Christian Partl
 *
 */
public class HistogramVisInfo implements IEmbeddedVisualizationInfo {

	public HistogramVisInfo() {
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
