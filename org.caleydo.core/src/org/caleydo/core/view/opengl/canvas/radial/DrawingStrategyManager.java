package org.caleydo.core.view.opengl.canvas.radial;

import java.util.HashMap;

import org.caleydo.core.manager.picking.PickingManager;

public final class DrawingStrategyManager {

	public static final int PD_DRAWING_STRATEGY_RAINBOW = 0;
	public static final int PD_DRAWING_STRATEGY_SELECTED = 1;
	public static final int PD_DRAWING_STRATEGY_FIXED_COLOR = 2;
	public static final int PD_DRAWING_STRATEGY_TRANSPARENT = 3;
	public static final int PD_DRAWING_STRATEGY_LABEL_DECORATOR = 4;
	public static final int PD_DRAWING_STRATEGY_CHILD_INDICATOR_DECORATOR = 5;
	public static final int PD_DRAWING_STRATEGY_EXPRESSION_COLOR = 5;

	private static DrawingStrategyManager instance;
	private HashMap<Integer, PDDrawingStrategy> hashDrawingStrategies;
	PDDrawingStrategy dsDefault;
	PDDrawingStrategyChildIndicatorDecorator dsRainbowChildDecorator;
	PDDrawingStrategyChildIndicatorDecorator dsExpressionChildDecorator;

	private DrawingStrategyManager(PickingManager pickingManager, int iViewID) {
		
		hashDrawingStrategies = new HashMap<Integer, PDDrawingStrategy>();
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_RAINBOW, new PDDrawingStrategyRainbow(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_SELECTED, new PDDrawingStrategySelected(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_FIXED_COLOR, new PDDrawingStrategyFixedColor(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_TRANSPARENT, new PDDrawingStrategyTransparent(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_LABEL_DECORATOR, new PDDrawingStrategyLabelDecorator());
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_CHILD_INDICATOR_DECORATOR,
			new PDDrawingStrategyChildIndicatorDecorator(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_EXPRESSION_COLOR, new PDDrawingStrategyExpressionColor(pickingManager, iViewID));

		dsRainbowChildDecorator = new PDDrawingStrategyChildIndicatorDecorator(pickingManager, iViewID);
		dsRainbowChildDecorator.setDrawingStrategy(hashDrawingStrategies.get(PD_DRAWING_STRATEGY_RAINBOW));
		dsExpressionChildDecorator = new PDDrawingStrategyChildIndicatorDecorator(pickingManager, iViewID);
		dsExpressionChildDecorator.setDrawingStrategy(hashDrawingStrategies.get(PD_DRAWING_STRATEGY_EXPRESSION_COLOR));
	
		dsDefault = dsExpressionChildDecorator;
	}
	
	public synchronized static void init(PickingManager pickingManager, int iViewID) {
		if (instance == null) {
			instance = new DrawingStrategyManager(pickingManager, iViewID);
		}
	}

	public synchronized static DrawingStrategyManager get() {
		return instance;
	}

	public PDDrawingStrategy getDrawingStrategy(int iDrawingStrategyType) {
		return hashDrawingStrategies.get(iDrawingStrategyType);
	}

	public PDDrawingStrategy getDefaultDrawingStrategy() {
		return dsDefault;
	}

	public void setRainbowStrategyDefault() {
		dsDefault = dsRainbowChildDecorator;
	}
	
	public void setExpressionStrategyDefault() {
		dsDefault = dsExpressionChildDecorator;
	}
	
	boolean isRainbowStrategyDefault() {
		return dsDefault == dsRainbowChildDecorator;
	}
}
