/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.LayoutRendererAdapter;
import org.caleydo.data.loader.ResourceLocators;

public abstract class ARemoteGLElementCreator implements IRemoteRendererCreator {
	@Override
	public final ALayoutRenderer createRemoteView(AGLView remoteRenderingView,
			List<TablePerspective> tablePerspectives, String embeddingEventSpace) {
		GLElement elem = create(remoteRenderingView, tablePerspectives, embeddingEventSpace);
		if (elem == null)
			return null;
		return new LayoutRendererAdapter(remoteRenderingView, ResourceLocators.DATA_CLASSLOADER, elem,
				embeddingEventSpace);
	}

	protected abstract GLElement create(AGLView remoteRenderingView, List<TablePerspective> tablePerspectives,
			String embeddingEventSpace);
}
