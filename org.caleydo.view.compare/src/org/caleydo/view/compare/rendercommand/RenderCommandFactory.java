package org.caleydo.view.compare.rendercommand;

import java.util.HashMap;

import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.sun.opengl.util.j2d.TextRenderer;

public class RenderCommandFactory {
	private HashMap<ERenderCommandType, IHeatMapRenderCommand> hashRenderCommands;

	public RenderCommandFactory(int viewID, PickingManager pickingManager,
			TextureManager textureManager, TextRenderer textRenderer) {

		hashRenderCommands = new HashMap<ERenderCommandType, IHeatMapRenderCommand>();
		hashRenderCommands.put(ERenderCommandType.OVERVIEW_GROUP_BAR,
				new OverviewGroupBarRenderCommand(viewID, pickingManager,
						textureManager));
		hashRenderCommands.put(ERenderCommandType.OVERVIEW_HEATMAP,
				new OverviewHeatMapRenderCommand(viewID, pickingManager));
		hashRenderCommands.put(ERenderCommandType.OVERVIEW_SLIDER,
				new OverviewSliderRenderCommand(viewID, pickingManager,
						textureManager));
		hashRenderCommands.put(ERenderCommandType.DETAIL_HEATMAPS,
				new DetailHeatMapsRenderCommand(viewID, pickingManager));
		hashRenderCommands.put(ERenderCommandType.CAPTION_LABEL,
				new CaptionLabelRenderCommand(textRenderer));
		hashRenderCommands.put(ERenderCommandType.DENDROGRAM_BUTTON,
				new DendrogramButtonRenderCommand(viewID, pickingManager,
						textureManager));
		hashRenderCommands.put(ERenderCommandType.DENDROGRAM,
				new DendrogramRenderCommand());
	}

	public IHeatMapRenderCommand getRenderCommand(
			ERenderCommandType renderCommandType) {
		return hashRenderCommands.get(renderCommandType);
	}
}
