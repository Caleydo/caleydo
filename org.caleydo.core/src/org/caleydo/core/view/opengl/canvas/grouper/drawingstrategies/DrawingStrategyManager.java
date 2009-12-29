package org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies;

import java.util.HashMap;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.canvas.grouper.GrouperRenderStyle;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.EGroupDrawingStrategyType;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.GroupDrawingStrategyDragged;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.GroupDrawingStrategyMouseOver;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.GroupDrawingStrategyNormal;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.GroupDrawingStrategySelection;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.group.IGroupDrawingStrategy;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.EVAElementDrawingStrategyType;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.IVAElementDrawingStrategy;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.VAElementDrawingStrategyDragged;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.VAElementDrawingStrategyMouseOver;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.VAElementDrawingStrategyNormal;
import org.caleydo.core.view.opengl.canvas.grouper.drawingstrategies.vaelement.VAElementDrawingStrategySelection;

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
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.DRAGGED, new GroupDrawingStrategyDragged(renderStyle));
		
		hashVAElementDrawingStrategies.put(EVAElementDrawingStrategyType.NORMAL, new VAElementDrawingStrategyNormal(
			pickingManager, iViewID));
		hashVAElementDrawingStrategies.put(EVAElementDrawingStrategyType.MOUSE_OVER, new VAElementDrawingStrategyMouseOver(
			pickingManager, iViewID));
		hashVAElementDrawingStrategies.put(EVAElementDrawingStrategyType.SELECTION, new VAElementDrawingStrategySelection(
			pickingManager, iViewID));
		hashVAElementDrawingStrategies.put(EVAElementDrawingStrategyType.DRAGGED, new VAElementDrawingStrategyDragged());

	}
	
	public IGroupDrawingStrategy getGroupDrawingStrategy(EGroupDrawingStrategyType type) {
		return hashGroupDrawingStrategies.get(type);
	}
	
	public IVAElementDrawingStrategy getVAElementDrawingStrategy(EVAElementDrawingStrategyType type) {
		return hashVAElementDrawingStrategies.get(type);
	}

}
