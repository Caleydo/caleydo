package org.caleydo.core.view.opengl.canvas.radial;

import java.util.ArrayList;

import org.caleydo.core.manager.picking.PickingManager;

/**
 * The DrawingStrategyFactory (Singleton) holds the instance of the current default partial disc drawing
 * strategy, which represents the current color mode. It also functions as factory for the drawing strategies.
 * 
 * @author Christian Partl
 */
public final class DrawingStrategyManager {

	private APDDrawingStrategy dsDefault;
	private PickingManager pickingManager;
	private int iViewID;
	private ArrayList<EPDDrawingStrategyType> alColorModes;
	private int iColorModeIndex;

	/**
	 * Constructor.
	 */
	public DrawingStrategyManager() {
		alColorModes = new ArrayList<EPDDrawingStrategyType>();
		iColorModeIndex = 0;
	}

	/**
	 * Initializes the DrawingStrategyManager.
	 * 
	 * @param pickingManager
	 *            PickingManager that shall be used for creating drawing strategies.
	 * @param iViewID
	 *            ViewID that shall be used for creating drawing strategies.
	 * @param alColorModes
	 *            List that specifies the drawing strategies which are used for color modes of the radial
	 *            hierarchy view. It must at least contain one valid drawing strategy type.
	 */
	public void init(PickingManager pickingManager, int iViewID,
		ArrayList<EPDDrawingStrategyType> alColorModes) {

		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		this.alColorModes.clear();
		this.alColorModes.addAll(alColorModes);
		iColorModeIndex = 0;
		dsDefault = createDrawingStrategy(this.alColorModes.get(iColorModeIndex));
	}

	/**
	 * Sets the next drawing strategy specified in the StrategyManager's color mode strategy list as default
	 * drawing strategy.
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
	public APDDrawingStrategy createDrawingStrategy(EPDDrawingStrategyType eDrawingStrategyType) {
		switch (eDrawingStrategyType) {
			case RAINBOW_COLOR:
				return new PDDrawingStrategyRainbow(pickingManager, iViewID);
			case SELECTED:
				return new PDDrawingStrategySelected(pickingManager, iViewID);
			case FIXED_COLOR:
				return new PDDrawingStrategyFixedColor(pickingManager, iViewID);
			case LABEL_DECORATOR:
				return new PDDrawingStrategyLabelDecorator();
			case EXPRESSION_COLOR:
				return new PDDrawingStrategyExpressionColor(pickingManager, iViewID);
			case INVISIBLE:
				return new PDDrawingStrategyInvisible(pickingManager, iViewID);
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
	 * Sets the current default drawing strategy to an instance of the specified type.
	 * 
	 * @param eDrawingStrategyType
	 *            Type of the drawing strategy that shall be the new default drawing strategy.
	 */
	public void setDefaultStrategy(EPDDrawingStrategyType eDrawingStrategyType) {
		dsDefault = createDrawingStrategy(eDrawingStrategyType);
	}

}
