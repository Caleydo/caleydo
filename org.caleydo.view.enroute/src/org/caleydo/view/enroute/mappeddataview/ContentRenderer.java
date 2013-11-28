/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;

/**
 * @author alexsb
 *
 */
public class ContentRenderer extends ALayoutRenderer {

	/** The primary mapping type of the id category for rows */
	IDType rowIDType;
	/** The id for this row in the primary mapping tytpe */
	Integer rowID;
	/** The id type matching the {@link #rowIDType} resolved for the specific {@link #dataDomain} */
	IDType resolvedRowIDType;
	/** The resolved row ID */
	Integer resolvedRowID;

	/** The sample ID Type of the local sample VA */
	IDType columnIDType;
	/** The id type matching the {@link #columnIDType} resolved for the specific {@link #dataDomain} */
	IDType resolvedColumnIDType;

	// TablePerspective tablePerspective;
	Perspective columnPerspective;
	ATableBasedDataDomain dataDomain;
	float z = 0.05f;
	Group group;

	APickingListener pickingListener;

	/**
	 * Determines whether the renderer should render in highlight mode.
	 */
	boolean isHighlightMode = false;

	MappedDataRenderer parent;

	IDMappingManager columnIDMappingManager;
	AGLView parentView;

	Perspective foreignColumnPerspective;

	protected IDataRenderer overviewRenderer;
	protected IDataRenderer detailRenderer;

	public ContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType, Integer resolvedRowID,
			ATableBasedDataDomain dataDomain, Perspective columnPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode, Perspective foreignColumnPerspective) {
		this.parentView = parentView;

		this.parent = parent;

		this.rowIDType = rowIDType;
		this.rowID = rowID;

		this.resolvedRowIDType = resolvedRowIDType;
		this.resolvedRowID = resolvedRowID;

		this.dataDomain = dataDomain;
		// this.tablePerspective = tablePerspective;
		this.columnPerspective = columnPerspective;
		this.group = group;
		this.isHighlightMode = isHighlightMode;
		this.foreignColumnPerspective = foreignColumnPerspective;
		columnIDType = columnPerspective.getIdType();
		resolvedColumnIDType = dataDomain.getPrimaryIDType(columnIDType);
		columnIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(columnIDType);
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		unRegisterPickingListener();
	}

	@Override
	protected void renderContent(GL2 gl) {
		// List<SelectionType> rowSelectionTypes;
		//
		// rowSelectionTypes = parent.getSelectionManager(rowIDType).getSelectionTypes(rowIDType, rowID);

		List<SelectionType> selectionTypes = parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		if (selectionTypes.size() > 0 && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
			overviewRenderer.render(gl, x, y, selectionTypes);
		} else {
			detailRenderer.render(gl, x, y, selectionTypes);
		}
	}

	private void unRegisterPickingListener() {
		parentView.removePickingListener(pickingListener);
	}

	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}

	/**
	 * @param overviewRenderer
	 *            setter, see {@link overviewRenderer}
	 */
	public void setOverviewRenderer(IDataRenderer overviewRenderer) {
		this.overviewRenderer = overviewRenderer;
	}

	/**
	 * @param detailRenderer
	 *            setter, see {@link detailRenderer}
	 */
	public void setDetailRenderer(IDataRenderer detailRenderer) {
		this.detailRenderer = detailRenderer;
	}
}
