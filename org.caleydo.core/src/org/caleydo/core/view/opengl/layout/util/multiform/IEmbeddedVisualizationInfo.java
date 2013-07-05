/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.view.opengl.layout.util.multiform;

/**
 * Provides information such as scaling characteristics about an embedded visualization that is rendered by a
 * {@link MultiFormRenderer}.
 *
 * @author Christian Partl
 *
 */
public interface IEmbeddedVisualizationInfo {

	/**
	 * Entities whose counts cause a visualization to scale either in width or height.
	 *
	 * @author Christian
	 *
	 */
	public enum EScalingEntity {
		RECORD, DIMENSION, RECORD_PERSPECTIVE, DIMENSION_PERSPECTIVE, DATA_DOMAIN, IMAGE_PIXEL;

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
