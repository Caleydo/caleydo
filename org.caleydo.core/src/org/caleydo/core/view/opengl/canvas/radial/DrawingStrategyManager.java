package org.caleydo.core.view.opengl.canvas.radial;

import org.caleydo.core.manager.picking.PickingManager;

/**
 * The DrawingStrategyFactory (Singleton) holds the instance of the current default partial disc drawing
 * strategy, which represents the current color mode. It also functions as factory for the drawing strategies.
 * 
 * @author Christian Partl
 */
public final class DrawingStrategyManager {

	private static DrawingStrategyManager instance;
	private APDDrawingStrategy dsDefault;
	private PickingManager pickingManager;
	private int iViewID;

	/**
	 * Constructor.
	 */
	private DrawingStrategyManager() {
	}

	/**
	 * Initializes the DrawingStrategyManager.
	 * 
	 * @param pickingManager
	 *            PickingManager that shall be used for creating drawing strategies.
	 * @param iViewID
	 *            ViewID that shall be used for creating drawing strategies.
	 */
	public static void init(PickingManager pickingManager, int iViewID) {
		if (instance == null) {
			instance = new DrawingStrategyManager();
		}
		
		instance.initStrategies(pickingManager, iViewID);
	}

	/**
	 * Creates an instance of all drawing strategies and saves them in a hashmap.
	 * 
	 * @param pickingManager
	 *            PickingManager that shall be used for creating drawing strategies.
	 * @param iViewID
	 *            ViewID that shall be used for creating drawing strategies.
	 */
	private void initStrategies(PickingManager pickingManager, int iViewID) {
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;

		dsDefault = new PDDrawingStrategyExpressionColor(pickingManager, iViewID);
	}

	/**
	 * Gets the instance of the singleton.
	 * 
	 * @return Instance of the DrawingStrategyManager.
	 */
	public static DrawingStrategyManager get() {
		return instance;
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
