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

import org.caleydo.core.gui.toolbar.RcpToolBarView;
import org.caleydo.core.startup.StartupProcessor;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class GenomePerspective
	implements IPerspectiveFactory {

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

		// IPlaceholderFolderLayout bottomLayout =
		// layout.createPlaceholderFolder("bottom", IPageLayout.BOTTOM, 0.8f,
		// "main");
		// bottomLayout.addPlaceholder("org.caleydo.view.filter");
		// bottomLayout.addPlaceholder("org.caleydo.view.filterpipeline");

		StartupProcessor.get().getStartupProcedure().addDefaultStartViews(mainLayout);
	}
}
