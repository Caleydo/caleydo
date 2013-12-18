/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.entourage.pathway;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.embedding.IPathwayRepresentationCreator;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.entourage.EEmbeddingID;
import org.caleydo.view.entourage.GLEntourage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * @author Christian
 *
 */
public final class PathwayViews {

	private static final String EXTENSION_POINT = "org.caleydo.datadomain.pathway.embeddedPathway";

	public static IPathwayRepresentation getPathwayRepresenation(GLEntourage entourage, PathwayGraph pathway,
			List<TablePerspective> tablePerspectives, TablePerspective mappingTablePerspective,
			String embeddingEventSpace, EEmbeddingID embeddingID) {

		IConfigurationElement pathwayView = getPathwayConfigElement(entourage, embeddingID);

		if (pathwayView != null) {
			IConfigurationElement[] creators = pathwayView.getChildren("creator");
			try {
				if (creators.length > 0) {

					IPathwayRepresentationCreator creator = (IPathwayRepresentationCreator) creators[0]
							.createExecutableExtension("class");

					return creator.create(entourage, pathway, tablePerspectives, mappingTablePerspective,
							embeddingEventSpace);

				}
			} catch (CoreException e) {
				Logger.log(new Status(IStatus.WARNING, "entourage", "Could not create executable pathway creator."));
			}
		}

		return null;
	}

	public static String getPathwayIconPath(GLEntourage entourage, EEmbeddingID embeddingID) {
		String iconPath = null;

		IConfigurationElement pathwayView = getPathwayConfigElement(entourage, embeddingID);

		if (pathwayView != null) {
			try {
				iconPath = pathwayView.getAttribute("icon");
				Bundle viewPlugin = Platform.getBundle(pathwayView.getContributor().getName());
				URL iconURL = viewPlugin.getEntry(iconPath);
				iconPath = FileLocator.toFileURL(iconURL).getPath();
			} catch (IOException e) {
				Logger.log(new Status(IStatus.WARNING, "entourage", "Could not find icon for pathway."));
				iconPath = EIconTextures.NO_ICON_AVAILABLE.getFileName();
			}
		}

		return iconPath;
	}

	private static IConfigurationElement getPathwayConfigElement(GLEntourage entourage, EEmbeddingID embeddingID) {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint point = registry.getExtensionPoint(EXTENSION_POINT);
		IExtension[] extensions = point.getExtensions();
		for (IExtension extension : extensions) {
			IConfigurationElement[] parents = extension.getConfigurationElements();
			for (IConfigurationElement parent : parents) {
				if (parent.getAttribute("viewID").equals(entourage.getViewType())) {
					IConfigurationElement[] pathwayViews = parent.getChildren("pathway");
					for (IConfigurationElement pathwayView : pathwayViews) {
						if (pathwayView.getAttribute("embeddingID").equals(embeddingID.id())) {
							return pathwayView;
						}
					}
				}
			}
		}
		return null;
	}
}
