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
package org.caleydo.view.pathway.toolbar.actions;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.view.pathway.toolbar.PathwayToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class GeneMappingAction extends Action implements IToolBarItem {
	public static final String TEXT = "Turn on/off gene mapping";
	public static final String ICON = "resources/icons/view/pathway/gene_mapping.png";

	/** status of the gene mapping, true = enabled, false = disabled */
	private boolean geneMappingEnabled = true;

	/** mediator to handle actions triggered by instances of this class */
	private PathwayToolBarMediator pathwayToolbarMediator;

	/**
	 * Constructor.
	 */
	public GeneMappingAction(PathwayToolBarMediator mediator) {
		pathwayToolbarMediator = mediator;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(GeneralManager.get()
				.getResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		geneMappingEnabled = !geneMappingEnabled;
		if (geneMappingEnabled) {
			pathwayToolbarMediator.enableGeneMapping();
		} else {
			pathwayToolbarMediator.disableGeneMapping();
		}
	}

	public boolean isGeneMappingEnabled() {
		return geneMappingEnabled;
	}

	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		this.geneMappingEnabled = geneMappingEnabled;
		super.setChecked(geneMappingEnabled);
	};
}
