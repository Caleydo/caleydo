package org.caleydo.core.view.opengl.canvas.radial;

import java.util.HashMap;

import org.caleydo.core.manager.picking.PickingManager;

public final class DrawingStrategyManager {

	public static final int PD_DRAWING_STRATEGY_RAINBOW = 0;
	public static final int PD_DRAWING_STRATEGY_SELECTED = 1;
	public static final int PD_DRAWING_STRATEGY_FIXED_COLOR = 2;
	public static final int PD_DRAWING_STRATEGY_TRANSPARENT = 3;
	public static final int PD_DRAWING_STRATEGY_LABEL_DECORATOR = 4;
	public static final int PD_DRAWING_STRATEGY_EXPRESSION_COLOR = 5;

	private static DrawingStrategyManager instance;
	private HashMap<Integer, PDDrawingStrategy> hashDrawingStrategies;
	private PDDrawingStrategy dsDefault;
	private PDDrawingStrategyChildIndicator dsRainbow;
	private PDDrawingStrategyChildIndicator dsExpressionColor;
	private PickingManager pickingManager;
	private int iViewID;
	private int iDefaultStrategyType;
	

	private DrawingStrategyManager(PickingManager pickingManager, int iViewID) {
		
		this.pickingManager = pickingManager;
		this.iViewID = iViewID;
		
		hashDrawingStrategies = new HashMap<Integer, PDDrawingStrategy>();
		dsRainbow = new PDDrawingStrategyRainbow(pickingManager, iViewID);
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_RAINBOW, dsRainbow);
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_SELECTED, new PDDrawingStrategySelected(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_FIXED_COLOR, new PDDrawingStrategyFixedColor(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_TRANSPARENT, new PDDrawingStrategyTransparent(pickingManager, iViewID));
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_LABEL_DECORATOR, new PDDrawingStrategyLabelDecorator());
		dsExpressionColor = new PDDrawingStrategyExpressionColor(pickingManager, iViewID);
		hashDrawingStrategies.put(PD_DRAWING_STRATEGY_EXPRESSION_COLOR, dsExpressionColor);
		dsDefault = dsExpressionColor;
		iDefaultStrategyType = PD_DRAWING_STRATEGY_EXPRESSION_COLOR;
	}
	
	public static void init(PickingManager pickingManager, int iViewID) {
		if (instance == null) {
			instance = new DrawingStrategyManager(pickingManager, iViewID);
		}
	}

	public static DrawingStrategyManager get() {
		return instance;
	}
	
	public PDDrawingStrategy createDrawingStrategy(int iDrawingStrategyType) {
		switch(iDrawingStrategyType) {
			case PD_DRAWING_STRATEGY_RAINBOW: return new PDDrawingStrategyRainbow(pickingManager, iViewID);
			case PD_DRAWING_STRATEGY_SELECTED: return new PDDrawingStrategySelected(pickingManager, iViewID);
			case PD_DRAWING_STRATEGY_FIXED_COLOR: return new PDDrawingStrategyFixedColor(pickingManager, iViewID);
			case PD_DRAWING_STRATEGY_TRANSPARENT: return new PDDrawingStrategyTransparent(pickingManager, iViewID);
			case PD_DRAWING_STRATEGY_LABEL_DECORATOR: return new PDDrawingStrategyLabelDecorator();
			case PD_DRAWING_STRATEGY_EXPRESSION_COLOR: return new PDDrawingStrategyExpressionColor(pickingManager, iViewID);
			default: return null;
		}
	}

	public PDDrawingStrategy getDrawingStrategy(int iDrawingStrategyType) {
		return hashDrawingStrategies.get(iDrawingStrategyType);
	}

	public PDDrawingStrategy getDefaultDrawingStrategy() {
		return dsDefault;
	}

	public void setDefaultStrategy(int iDrawingStrategyType) {
		dsDefault = hashDrawingStrategies.get(iDrawingStrategyType);
		iDefaultStrategyType = iDrawingStrategyType;
	}
	
	public int getDefaultStrategyType() {
		return iDefaultStrategyType;
	}
}
