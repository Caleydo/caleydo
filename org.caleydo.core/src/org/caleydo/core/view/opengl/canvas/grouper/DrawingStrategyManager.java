package org.caleydo.core.view.opengl.canvas.grouper;

import java.util.HashMap;

import org.caleydo.core.manager.picking.PickingManager;

public class DrawingStrategyManager {

	private HashMap<EGroupDrawingStrategyType, IGroupDrawingStrategy> hashGroupDrawingStrategies;
	private HashMap<EVAElementDrawingStrategyType, IVAElementDrawingStrategy> hashVAElementDrawingStrategies;

	public DrawingStrategyManager(PickingManager pickingManager, int iViewID, GrouperRenderStyle renderStyle) {

		hashGroupDrawingStrategies = new HashMap<EGroupDrawingStrategyType, IGroupDrawingStrategy>();
		hashVAElementDrawingStrategies = new HashMap<EVAElementDrawingStrategyType, IVAElementDrawingStrategy>();
		
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.NORMAL, new GroupDrawingStrategyNormal(
			pickingManager, iViewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.MOUSE_OVER, new GroupDrawingStrategyMouseOver(
			pickingManager, iViewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.SELECTION, new GroupDrawingStrategySelection(
			pickingManager, iViewID, renderStyle));
		
		hashVAElementDrawingStrategies.put(EVAElementDrawingStrategyType.NORMAL, new VAElementDrawingStrategyNormal(
			pickingManager, iViewID));
		hashVAElementDrawingStrategies.put(EVAElementDrawingStrategyType.MOUSE_OVER, new VAElementDrawingStrategyMouseOver(
			pickingManager, iViewID));
		hashVAElementDrawingStrategies.put(EVAElementDrawingStrategyType.SELECTION, new VAElementDrawingStrategySelection(
			pickingManager, iViewID));

	}
	
	public IGroupDrawingStrategy getGroupDrawingStrategy(EGroupDrawingStrategyType type) {
		return hashGroupDrawingStrategies.get(type);
	}
	
	public IVAElementDrawingStrategy getVAElementDrawingStrategy(EVAElementDrawingStrategyType type) {
		return hashVAElementDrawingStrategies.get(type);
	}

}
