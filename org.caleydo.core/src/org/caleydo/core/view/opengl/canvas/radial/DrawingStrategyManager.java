package org.caleydo.core.view.opengl.canvas.radial;

import java.util.HashMap;

import org.caleydo.core.manager.picking.PickingManager;

/**
 * The DrawingStrategyManager (Singleton) holds the instance of the current default partial disc drawing
 * strategy, which represents the current color mode. Since in many cases only one standard version of a
 * drawing strategy is needed the DrawingStrategyManager holds an instance of all drawing strategies and
 * provides methods for accessing them. In some cases it is necessary to create new instances of though, so
 * the DrawingStrategyManager also functions as factory for the drawing strategies.
 * 
 * @author Christian Partl
 */
public final class DrawingStrategyManager {

	private static DrawingStrategyManager instance;
	private HashMap<EPDDrawingStrategyType, APDDrawingStrategy> hashDrawingStrategies;
	private APDDrawingStrategy dsDefault;
	private PickingManager pickingManager;
	private int iViewID;

	/**
	 * Constructor.
	 */
	private DrawingStrategyManager() {
		hashDrawingStrategies = new HashMap<EPDDrawingStrategyType, APDDrawingStrategy>();
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
		hashDrawingStrategies.clear();
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;

		hashDrawingStrategies.put(EPDDrawingStrategyType.RAINBOW_COLOR, new PDDrawingStrategyRainbow(
			pickingManager, iViewID));
		hashDrawingStrategies.put(EPDDrawingStrategyType.SELECTED, new PDDrawingStrategySelected(
			pickingManager, iViewID));
		hashDrawingStrategies.put(EPDDrawingStrategyType.FIXED_COLOR, new PDDrawingStrategyFixedColor(
			pickingManager, iViewID));
		hashDrawingStrategies.put(EPDDrawingStrategyType.LABEL_DECORATOR,
			new PDDrawingStrategyLabelDecorator());
		hashDrawingStrategies.put(EPDDrawingStrategyType.INVISIBLE, new PDDrawingStrategyInvisible(
			pickingManager, iViewID));

		dsDefault = new PDDrawingStrategyExpressionColor(pickingManager, iViewID);
		hashDrawingStrategies.put(EPDDrawingStrategyType.EXPRESSION_COLOR, dsDefault);

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
	 * Gets the instance of the drawing strategy of the specified type held by the DrawingStrategyManager.
	 * 
	 * @param eDrawingStrategyType
	 *            Type of the drawing strategy that shall be returned.
	 * @return Instance of the drawing strategy of the specified type.
	 */
	public APDDrawingStrategy getDrawingStrategy(EPDDrawingStrategyType eDrawingStrategyType) {
		return hashDrawingStrategies.get(eDrawingStrategyType);
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
	 * Sets the current default drawing strategy to the instance of the specified type that is held by the
	 * DrawingStrategyManager.
	 * 
	 * @param eDrawingStrategyType
	 *            Type of the drawing strategy that shall be the new default drawing strategy.
	 */
	public void setDefaultStrategy(EPDDrawingStrategyType eDrawingStrategyType) {
		dsDefault = hashDrawingStrategies.get(eDrawingStrategyType);
	}

}
