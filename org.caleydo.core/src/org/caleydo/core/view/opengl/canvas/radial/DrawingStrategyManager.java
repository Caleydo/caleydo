package org.caleydo.core.view.opengl.canvas.radial;

import java.util.HashMap;

import org.caleydo.core.view.opengl.canvas.radial.DrawingStrategyManager;
import org.caleydo.core.view.opengl.canvas.radial.PDDrawingStrategy;

public final class DrawingStrategyManager {

	public static final int PD_DRAWING_STRATEGY_RAINBOW = 0;
	public static final int PD_DRAWING_STRATEGY_SELECTED = 1;
	public static final int PD_DRAWING_STRATEGY_FIXED_COLOR = 2;
	public static final int PD_DRAWING_STRATEGY_TRANSPARENT = 3;
	public static final int PD_DRAWING_STRATEGY_LABEL_DECORATOR = 4;

	private static DrawingStrategyManager instance;
	private HashMap<Integer, PDDrawingStrategy> hashDrawingStrategies;

	private DrawingStrategyManager() {
		hashDrawingStrategies = new HashMap<Integer, PDDrawingStrategy>();
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_RAINBOW, new PDDrawingStrategyRainbow());
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_SELECTED, new PDDrawingStrategySelected());
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_FIXED_COLOR, new PDDrawingStrategyFixedColor());
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_TRANSPARENT, new PDDrawingStrategyTransparent());
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_LABEL_DECORATOR, new PDDrawingStrategyLabelDecorator());
	}

	public synchronized static DrawingStrategyManager get() {
		if (instance == null) {
			instance = new DrawingStrategyManager();
		}
		return instance;
	}

	public PDDrawingStrategy getDrawingStrategy(int iDrawingStrategyType) {
		return hashDrawingStrategies.get(iDrawingStrategyType);
	}
}
