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
package org.caleydo.view.pathway;

import org.caleydo.core.view.opengl.camera.ViewFrustum;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;

public class PathwayRenderStyle
	extends GeneralRenderStyle {

	public static final int neighborhoodNodeColorArraysize = 4;

	public static final int ENZYME_NODE_PIXEL_WIDTH = 46;
	public static final int ENZYME_NODE_PIXEL_HEIGHT = 17;
	public static final int COMPOUND_NODE_PIXEL_WIDTH = 8;
	public static final int COMPOUND_NODE_PIXEL_HEIGHT = 8;

	public static final float Z_OFFSET = 0.01f;

	public static final float[] ENZYME_NODE_COLOR = new float[] { 0.3f, 0.3f, 0.3f, 1 };
	public static final float[] COMPOUND_NODE_COLOR = new float[] { 0.3f, 0.3f, 0.3f, 1 };
	public static final float[] PATHWAY_NODE_COLOR = new float[] { 0.7f, 0.7f, 1f, 1 };



	public static final float[] PATH_COLOR = new float[] { 1, 1, 1, 0.0f };

	public static final float[] STD_DEV_COLOR = new float[] { 49f / 255f, 163f / 255, 84f / 255, 1f };
	public static final int STD_DEV_BAR_PIXEL_WIDTH = 6;
	public static final int STD_DEV_BAR_PIXEL_HEIGHT = 6;


	public enum NodeShape {
		RECTANGULAR,
		ROUND,
		ROUNDRECTANGULAR
	}



	protected NodeShape enzymeNodeShape;

	protected NodeShape compoundNodeShape;

	protected NodeShape pathwayNodeShape;



	public PathwayRenderStyle(ViewFrustum viewFrustum) {
		super(viewFrustum);
		init();
	}

	/**
	 * Constructor. Initializes the pathway render style. TODO: load pathway
	 * style from XML file.
	 */
	private void init() {
		// TODO: use float[] constants for colors

		enzymeNodeShape = NodeShape.RECTANGULAR;
		compoundNodeShape = NodeShape.ROUND;
		pathwayNodeShape = NodeShape.ROUNDRECTANGULAR;

	}

	public NodeShape getCompoundNodeShape() {
		return compoundNodeShape;
	}

	public NodeShape getEnzymeNodeShape() {
		return enzymeNodeShape;
	}


	public NodeShape getPathwayNodeShape() {
		return pathwayNodeShape;
	}

}