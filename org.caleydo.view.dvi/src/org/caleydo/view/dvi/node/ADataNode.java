/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.dvi.node;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.event.data.DataSetSelectedEvent;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.view.dvi.GLDataViewIntegrator;
import org.caleydo.view.dvi.contextmenu.RenameLabelHolderItem;
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
			public void clicked(Pick pick) {
				DataSetSelectedEvent event = new DataSetSelectedEvent(dataDomain);
				event.setSender(view);
				
				EventPublisher.trigger(event);
			}

			@Override
			public void rightClicked(Pick pick) {
				ContextMenuCreator contextMenuCreator = view.getContextMenuCreator();
				contextMenuCreator
						.addContextMenuItem(new RenameLabelHolderItem(dataDomain));
			}

		}, DATA_GRAPH_NODE_PICKING_TYPE, id);
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

	public List<TablePerspective> getVisibleTablePerspectives() {
		List<TablePerspective> visibleTablePerspectives = new ArrayList<>();
		for (TablePerspective tablePerspective : getTablePerspectives()) {
			for (ViewNode viewNode : view.getViewNodes()) {
				if (viewNode.getTablePerspectives().contains(tablePerspective)) {
					visibleTablePerspectives.add(tablePerspective);
					break;
				}
			}
		}

		return visibleTablePerspectives;
	}

}
