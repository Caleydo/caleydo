/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.pathway;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.layout.util.multiform.DefaultVisInfo;
import org.caleydo.core.view.opengl.layout.util.multiform.MultiFormRenderer;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.entourage.EEmbeddingID;
import org.caleydo.view.entourage.GLEntourage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * @author Christian
 *
 */
public final class PathwayViews {

	private static final String EXTENSION_POINT = "org.caleydo.view.entourage.pathway";

	public static int addPathwayView(GLEntourage entourage, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace, EEmbeddingID embeddingID, MultiFormRenderer multiFormRenderer) {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT);
		IExtension[] extensions = point.getExtensions();
		String iconPath = null;
		ALayoutRenderer renderer = null;
		AGLView view = null;
		for (IExtension extension : extensions) {
			IConfigurationElement[] pathwayViews = extension.getConfigurationElements();
			for (IConfigurationElement pathwayView : pathwayViews) {
				if (pathwayView.getAttribute("embeddingID").equals(embeddingID.id())) {
					iconPath = pathwayView.getAttribute("icon");
					IConfigurationElement[] viewCreators = pathwayView.getChildren("viewCreator");
					IConfigurationElement[] rendererCreators = pathwayView.getChildren("rendererCreator");
					try {
						if (viewCreators.length > 0) {

							IPathwayViewCreator creator = (IPathwayViewCreator) viewCreators[0]
									.createExecutableExtension("class");
							view = creator.create(entourage, pathway, tablePerspectives, mappingTablePerspective,
									embeddingEventSpace);

							break;
						} else if (rendererCreators.length > 0) {
							IPathwayRendererCreator creator = (IPathwayRendererCreator) rendererCreators[0]
									.createExecutableExtension("class");
							renderer = creator.create(entourage, pathway, tablePerspectives, mappingTablePerspective,
									embeddingEventSpace);
							break;
						}
					} catch (CoreException e) {
						Logger.log(new Status(IStatus.WARNING, "entourage",
								"Could not create executable pathway creator."));
					}
				}
			}
			if (renderer != null || view != null)
				break;
		}

		if (renderer != null)
			return multiFormRenderer.addLayoutRenderer(renderer, iconPath, new DefaultVisInfo(), false);
		else if (view != null) {
			return multiFormRenderer.addView(view, iconPath, new DefaultVisInfo(), false, false);
		}
		return -1;
	}
}
