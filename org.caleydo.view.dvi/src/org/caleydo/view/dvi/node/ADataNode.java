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
package org.caleydo.view.dvi.node;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.contextmenu.RenameDataDomainItem;
import org.caleydo.view.dvi.layout.AGraphLayout;

public abstract class ADataNode extends ADefaultTemplateNode {

	protected IDataDomain dataDomain;

	public ADataNode(AGraphLayout graphLayout, GLDataViewIntegrator view,
			final DragAndDropController dragAndDropController, Integer id,
			IDataDomain dataDomain) {
		super(graphLayout, view, dragAndDropController, id);

		this.dataDomain = dataDomain;

	}

	@Override
	protected void registerPickingListeners() {
		super.registerPickingListeners();

		view.addIDPickingListener(new APickingListener() {

			@Override
			public void rightClicked(Pick pick) {
				ContextMenuCreator contextMenuCreator = view.getContextMenuCreator();
				contextMenuCreator
						.addContextMenuItem(new RenameDataDomainItem(dataDomain));
			}

		}, DATA_GRAPH_NODE_PENETRATING_PICKING_TYPE, id);
	}

	public void setDataDomain(IDataDomain dataDomain) {
		this.dataDomain = dataDomain;
	}

	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public boolean showsTablePerspectives() {
		return true;
	}

	@Override
	public String getLabel() {
		return dataDomain.getLabel();
	}

	@Override
	public boolean isLabelDefault() {
		return false;
	}

}
