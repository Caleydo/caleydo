package org.caleydo.view.compare.rendercommand;

import java.util.HashMap;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class RenderCommandFactory {
	private HashMap<ERenderCommandType, IHeatMapRenderCommand> hashRenderCommands;

	public RenderCommandFactory(int viewID, PickingManager pickingManager,
			TextureManager textureManager) {

		hashRenderCommands = new HashMap<ERenderCommandType, IHeatMapRenderCommand>();
		hashRenderCommands.put(ERenderCommandType.OVERVIEW_GROUP_BAR,
				new OverviewGroupBarRenderCommand(viewID, pickingManager,
						textureManager));
		hashRenderCommands.put(ERenderCommandType.OVERVIEW_HEATMAP,
				new OverviewHeatMapRenderCommand());
		hashRenderCommands.put(ERenderCommandType.OVERVIEW_SLIDER,
				new OverviewSliderRenderCommand(viewID, pickingManager,
						textureManager));
		hashRenderCommands.put(ERenderCommandType.DETAIL_HEATMAPS,
				new DetailHeatMapsRenderCommand(viewID, pickingManager));
	}

	public IHeatMapRenderCommand getRenderCommand(
			ERenderCommandType renderCommandType) {
		return hashRenderCommands.get(renderCommandType);
	}
}
