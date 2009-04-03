package org.caleydo.core.view.opengl.canvas.hierarchy;

import java.util.HashMap;

public final class DrawingStrategyManager {

	public static final int PD_DRAWING_STRATEGY_NORMAL = 0;
	public static final int PD_DRAWING_STRATEGY_SELECTED = 1;
	public static final int PD_DRAWING_STRATEGY_FIXED_COLOR = 2;

	private static DrawingStrategyManager instance;
	private HashMap<Integer, PDDrawingStrategy> hashDrawingStrategies;

	private DrawingStrategyManager() {
		hashDrawingStrategies = new HashMap<Integer, PDDrawingStrategy>();
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_NORMAL, new PDDrawingStrategyRainbow());
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_SELECTED, new PDDrawingStrategySelected());
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_FIXED_COLOR, new PDDrawingStrategyFixedColor());
	}

	public synchronized static DrawingStrategyManager getInstance() {
		if (instance == null) {
			instance = new DrawingStrategyManager();
		}
		return instance;
	}

	public PDDrawingStrategy getDrawingStrategy(int iDrawingStrategyType) {
		return hashDrawingStrategies.get(iDrawingStrategyType);
	}
}
