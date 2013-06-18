/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.core.gui.perspective;

import java.util.Arrays;
import java.util.List;

import org.caleydo.core.gui.toolbar.RcpToolBarView;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IPlaceholderFolderLayout;

public class GenomePerspective
	implements IPerspectiveFactory {
	public static final String PERSPECTIVE_ID = "org.caleydo.core.gui.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout) {

		layout.setEditorAreaVisible(false);

		Rectangle rectDisplay = Display.getCurrent().getPrimaryMonitor().getBounds();
		float ratio = (float) RcpToolBarView.TOOLBAR_WIDTH / rectDisplay.width;

		IFolderLayout topLeft =
			layout.createFolder("topLeft", IPageLayout.LEFT, ratio, IPageLayout.ID_EDITOR_AREA);
		topLeft.addView(RcpToolBarView.ID);

		IFolderLayout middleLeft = layout.createFolder("middleLeft", IPageLayout.BOTTOM, 0.2f, "topLeft");
		middleLeft.addView("org.caleydo.view.info.selection");
		// middleLeft.addPlaceholder("org.caleydo.view.statistics");

		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.4f, "middleLeft");
		bottomLeft.addView("org.caleydo.view.info.dataset");

		IFolderLayout mainLayout =
			layout.createFolder("main", IPageLayout.RIGHT, 0.3f, IPageLayout.ID_EDITOR_AREA);

		// IPlaceholderFolderLayout rightLayout =
		// layout.createPlaceholderFolder("right", IPageLayout.RIGHT, 1 - 2 * fRatio, "main");
		//rightLayout.addPlaceholder("org.caleydo.view.bookmark");
		//rightLayout.addPlaceholder("org.caleydo.view.grouper");

		IPlaceholderFolderLayout bottomLayout = layout.createPlaceholderFolder("bottom", IPageLayout.BOTTOM, 0.75f,
				"main");
		// bottomLayout.addPlaceholder("org.caleydo.view.filter");
		// bottomLayout.addPlaceholder("org.caleydo.view.filterpipeline");

		// all the views that should be initially shown
		List<String> initialViews = Arrays.asList("org.caleydo.view.dvi", "org.caleydo.view.stratomex");
		for (String initialView : initialViews) {
			mainLayout.addView(initialView);
		}

		// create placeholders for all registered caleydo views depending on their prefix: tool, info or main
		for (IConfigurationElement elem : RegistryFactory.getRegistry().getConfigurationElementsFor("org.eclipse.ui.views")) {
			if(!"view".equals(elem.getName()))
				continue;
			if (!"org.caleydo.core.views".equals(elem.getAttribute("category"))) // wrong category
				continue;
			String id = elem.getAttribute("id");
			boolean allowMultiple = "true".equalsIgnoreCase(elem.getAttribute("allowMultiple"));
			if (id == null || (initialViews.contains(id) && !allowMultiple)) // part of the initial views skip
				continue;
			if (id.startsWith("org.caleydo.view.info")) {
				// manual
			} else if (id.startsWith("org.caleydo.view.tool")) {
				bottomLayout.addPlaceholder(id + (allowMultiple ? ":*" : ""));
			} else if (id.startsWith("org.caleydo.view")) {
				mainLayout.addPlaceholder(id + (allowMultiple ? ":*" : ""));
			}
		}
	}
}
