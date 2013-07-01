/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.view.enroute.SelectionColorCalculator;

/**
 * @author alexsb
 *
 */
public abstract class ContentRenderer extends ALayoutRenderer {

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

	SelectionColorCalculator colorCalculator;

	/**
	 * Determines whether the renderer should render in highlight mode.
	 */
	boolean isHighlightMode = false;

	MappedDataRenderer parent;

	IDMappingManager columnIDMappingManager;
	AGLView parentView;

	public ContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType, Integer resolvedRowID,
			ATableBasedDataDomain dataDomain, Perspective columnPerspective, AGLView parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode) {
		this.parentView = parentView;
		Color barColor;
		// FIXME - bad hack
		if (rowIDType.getIDCategory().getCategoryName().equals("GENE")) {
			barColor = MappedDataRenderer.BAR_COLOR;
		} else {
			barColor = MappedDataRenderer.CONTEXT_BAR_COLOR;
		}
		colorCalculator = new SelectionColorCalculator(barColor);

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
		columnIDType = columnPerspective.getIdType();
		resolvedColumnIDType = dataDomain.getPrimaryIDType(columnIDType);
		columnIDMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(columnIDType);
		init();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		unRegisterPickingListener();
	}

	public abstract void init();

	private void unRegisterPickingListener() {
		parentView.removePickingListener(pickingListener);
	}

}
