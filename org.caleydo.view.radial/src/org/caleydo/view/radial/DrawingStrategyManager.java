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
package org.caleydo.view.radial;

import java.util.ArrayList;

import org.caleydo.core.util.clusterer.EPDDrawingStrategyType;
import org.caleydo.core.util.color.mapping.ColorMapper;
import org.caleydo.core.view.opengl.picking.PickingManager;

/**
 * The DrawingStrategyFactory (Singleton) holds the instance of the current
 * default partial disc drawing strategy, which represents the current color
 * mode. It also functions as factory for the drawing strategies.
 * 
 * @author Christian Partl
 */
public final class DrawingStrategyManager {

	private APDDrawingStrategy dsDefault;
	private PickingManager pickingManager;
	private int viewID;
	private ArrayList<EPDDrawingStrategyType> alColorModes;
	private int iColorModeIndex;

	private ColorMapper colorMapper;

	/**
	 * Constructor.
	 */
	public DrawingStrategyManager(ColorMapper colorMapper) {
		this.colorMapper = colorMapper;
		alColorModes = new ArrayList<EPDDrawingStrategyType>();
		iColorModeIndex = 0;
	}

	/**
	 * Initializes the DrawingStrategyManager.
	 * 
	 * @param pickingManager
	 *            PickingManager that shall be used for creating drawing
	 *            strategies.
	 * @param viewID
	 *            ViewID that shall be used for creating drawing strategies.
	 * @param alColorModes
	 *            List that specifies the drawing strategies which are used for
	 *            color modes of the radial hierarchy view. It must at least
	 *            contain one valid drawing strategy type.
	 */
	public void init(PickingManager pickingManager, int viewID,
			ArrayList<EPDDrawingStrategyType> alColorModes) {

		this.pickingManager = pickingManager;
		this.viewID = viewID;
		this.alColorModes.clear();
		this.alColorModes.addAll(alColorModes);
		iColorModeIndex = 0;
		dsDefault = createDrawingStrategy(this.alColorModes.get(iColorModeIndex));
	}

	/**
	 * Sets the next drawing strategy specified in the StrategyManager's color
	 * mode strategy list as default drawing strategy.
	 */
	public void setNextColorModeStrategyDefault() {
		iColorModeIndex++;
		if (iColorModeIndex >= alColorModes.size())
			iColorModeIndex = 0;
		dsDefault = createDrawingStrategy(this.alColorModes.get(iColorModeIndex));
	}

	/**
	 * Creates a new drawing strategy instance of the specified type.
	 * 
	 * @param eDrawingStrategyType
	 *            Type of the drawing strategy that shall be created.
	 * @return A new instance of a drawing strategy of the specified type.
	 */
	public APDDrawingStrategy createDrawingStrategy(
			EPDDrawingStrategyType eDrawingStrategyType) {
		switch (eDrawingStrategyType) {
		case RAINBOW_COLOR:
			return new PDDrawingStrategyRainbow(pickingManager, viewID);
		case SELECTED:
			return new PDDrawingStrategySelected(pickingManager, viewID);
		case FIXED_COLOR:
			return new PDDrawingStrategyFixedColor(pickingManager, viewID);
		case LABEL_DECORATOR:
			return new PDDrawingStrategyLabelDecorator(colorMapper);
		case EXPRESSION_COLOR:
			return new PDDrawingStrategyExpressionColor(colorMapper, pickingManager,
					viewID);
		case INVISIBLE:
			return new PDDrawingStrategyInvisible(pickingManager, viewID);
		default:
			return null;
		}
	}

	/**
	 * Gets the current default drawing strategy.
	 * 
	 * @return Current default drawing strategy.
	 */
	public APDDrawingStrategy getDefaultDrawingStrategy() {
		return dsDefault;
	}

	/**
	 * Sets the current default drawing strategy to an instance of the specified
	 * type.
	 * 
	 * @param eDrawingStrategyType
	 *            Type of the drawing strategy that shall be the new default
	 *            drawing strategy.
	 */
	public void setDefaultStrategy(EPDDrawingStrategyType eDrawingStrategyType) {
		dsDefault = createDrawingStrategy(eDrawingStrategyType);
	}

}
