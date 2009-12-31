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

public class DrawingStrategyManager {

	private HashMap<EGroupDrawingStrategyType, IGroupDrawingStrategy> hashGroupDrawingStrategies;

	public DrawingStrategyManager(PickingManager pickingManager, int iViewID, GrouperRenderStyle renderStyle) {

		hashGroupDrawingStrategies = new HashMap<EGroupDrawingStrategyType, IGroupDrawingStrategy>();

		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.NORMAL, new GroupDrawingStrategyNormal(
			pickingManager, iViewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.MOUSE_OVER,
			new GroupDrawingStrategyMouseOver(pickingManager, iViewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.SELECTION,
			new GroupDrawingStrategySelection(pickingManager, iViewID, renderStyle));
		hashGroupDrawingStrategies.put(EGroupDrawingStrategyType.DRAGGED, new GroupDrawingStrategyDragged(
			renderStyle));

	}

	public IGroupDrawingStrategy getGroupDrawingStrategy(EGroupDrawingStrategyType type) {
		return hashGroupDrawingStrategies.get(type);
	}

}
