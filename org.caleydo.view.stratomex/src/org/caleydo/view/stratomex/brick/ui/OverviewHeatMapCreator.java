package org.caleydo.view.stratomex.brick.ui;

import java.util.List;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.IRemoteRendererCreator;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;

/**
 * @author Christian
 *
 */
public class OverviewHeatMapCreator implements IRemoteRendererCreator {

	public OverviewHeatMapCreator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public ALayoutRenderer createRemoteView(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace) {
		TablePerspective tablePerspective = tablePerspectives.get(0);
		Table table = tablePerspective.getDataDomain().getTable();

		return new OverviewHeatMapRenderer(tablePerspectives.get(0), table, true);
	}

}
