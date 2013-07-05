/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.layout.util.multiform;

import org.caleydo.core.util.base.ILabeled;

/**
 * Provides information such as scaling characteristics about an embedded visualization that is rendered by a
 * {@link MultiFormRenderer}.
 *
 * @author Christian Partl
 *
 */
public interface IEmbeddedVisualizationInfo extends ILabeled {



	/**
	 * Entities whose counts cause a visualization to scale either in width or height.
	 *
	 * @author Christian
	 *
	 */
	public enum EScalingEntity {
		RECORD, DIMENSION, RECORD_PERSPECTIVE, DIMENSION_PERSPECTIVE, DATA_DOMAIN, IMAGE_PIXEL, PATHWAY_VERTEX;

	}

	/**
	 * @return The entity whose count that is primarily responsible for the visualization to scale in width. Null, if
	 *         the visualization's width is independent of such an entity.
	 */
	public EScalingEntity getPrimaryWidthScalingEntity();

	/**
	 * @return The entity whose count that is primarily responsible for the visualization to scale in height. Null, if
	 *         the visualization's height is independent of such an entity.
	 */
	public EScalingEntity getPrimaryHeightScalingEntity();

}
