/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.enroute.mappeddataview;

import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.enroute.EPickingType;
import org.caleydo.view.enroute.GLEnRoutePathway;
import org.caleydo.view.enroute.SelectionColorCalculator;
import org.caleydo.view.enroute.correlation.DataCellInfo;
import org.caleydo.view.enroute.correlation.DataCellSelectionEvent;
import org.caleydo.view.enroute.correlation.IDataClassifier;
import org.caleydo.view.enroute.correlation.ShowDataClassificationEvent;
import org.caleydo.view.enroute.mappeddataview.MappedDataRenderer.IDisposeListener;

/**
 * @author alexsb
 *
 */
public class ContentRenderer extends ALayoutRenderer implements IDisposeListener, IEventBasedSelectionManagerUser {

	/**
	 * ID that identifies this data cell
	 */
	private final int cellID;

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
	GLEnRoutePathway parentView;

	Perspective foreignColumnPerspective;

	protected IDataRenderer overviewRenderer;
	protected IDataRenderer detailRenderer;

	private EventBasedSelectionManager dataCellSelectionManager;
	private SelectionColorCalculator colorCalculator = new SelectionColorCalculator(new Color());
	IDataClassifier dataClassifier;

	public ContentRenderer(IDType rowIDType, Integer rowID, IDType resolvedRowIDType, Integer resolvedRowID,
			ATableBasedDataDomain dataDomain, Perspective columnPerspective, GLEnRoutePathway parentView,
			MappedDataRenderer parent, Group group, boolean isHighlightMode, Perspective foreignColumnPerspective,
			int cellID) {

		this.cellID = cellID;
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
		parentView.addEventListener(this);

		dataCellSelectionManager = new EventBasedSelectionManager(this,
				IDType.getIDType(MappedDataRenderer.DATA_CELL_ID));
		dataCellSelectionManager.registerEventListeners();

		if (isHighlightMode) {
			parent.pickingListenerManager.addIDPickingListener(new APickingListener() {
				@Override
				protected void mouseOver(Pick pick) {
					dataCellSelectionManager.addToType(SelectionType.MOUSE_OVER, ContentRenderer.this.cellID);
					dataCellSelectionManager.triggerSelectionUpdateEvent();
					ContentRenderer.this.parentView.setDisplayListDirty();
				}

				@Override
				protected void mouseOut(Pick pick) {
					dataCellSelectionManager.removeFromType(SelectionType.MOUSE_OVER, ContentRenderer.this.cellID);
					dataCellSelectionManager.triggerSelectionUpdateEvent();
					ContentRenderer.this.parentView.setDisplayListDirty();
				}

				@Override
				protected void clicked(Pick pick) {
					dataCellSelectionManager.clearSelection(SelectionType.SELECTION);
					dataCellSelectionManager.addToType(SelectionType.SELECTION, ContentRenderer.this.cellID);
					dataCellSelectionManager.triggerSelectionUpdateEvent();
					ContentRenderer.this.parentView.setDisplayListDirty();
					EventPublisher.trigger(new DataCellSelectionEvent(new DataCellInfo(ContentRenderer.this.cellID,
							ContentRenderer.this.dataDomain, ContentRenderer.this.columnPerspective,
							ContentRenderer.this.resolvedRowIDType, ContentRenderer.this.resolvedRowID)));
				}

			}, EPickingType.DATA_CELL.name(), cellID);
		}
	}

	@Override
	protected void renderContent(GL2 gl) {
		// List<SelectionType> rowSelectionTypes;
		//
		// rowSelectionTypes = parent.getSelectionManager(rowIDType).getSelectionTypes(rowIDType, rowID);
		// render invisible rect in non-highlight mode, use id that can be
		if (!isHighlightMode) {
			gl.glPushName(parentView.getPickingManager().getPickingID(parentView.getID(),
					EPickingType.DATA_CELL.name(), cellID));

			gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
			gl.glColor4f(0, 0, 0, 0);
			gl.glBegin(GL2GL3.GL_QUADS);
			gl.glVertex3f(0, 0, 0);
			gl.glVertex3f(x, 0, 0);
			gl.glVertex3f(x, y, 0);
			gl.glVertex3f(0, y, 0);
			gl.glEnd();
			gl.glPopAttrib();
		}

		List<SelectionType> selectionTypes = parent.sampleGroupSelectionManager.getSelectionTypes(group.getID());
		if (selectionTypes.size() > 0 && selectionTypes.contains(MappedDataRenderer.abstractGroupType)) {
			overviewRenderer.render(gl, x, y, selectionTypes);
		} else {
			detailRenderer.render(gl, x, y, selectionTypes);
		}
		if (!isHighlightMode) {
			gl.glPopName();
		}
		if (isHighlightMode && parentView.isDataCellSelection()) {
			List<SelectionType> selTypes = dataCellSelectionManager.getSelectionTypes(cellID);
			if (!selTypes.isEmpty() && selTypes.get(0) != SelectionType.NORMAL) {
				// colorCalculator.setBaseColor(new Color());

				colorCalculator.calculateColors(selTypes);

				float[] topColor = colorCalculator.getPrimaryColor().getRGBA();
				float[] bottomColor = colorCalculator.getSecondaryColor().getRGBA();

				gl.glPushAttrib(GL2.GL_LINE_BIT);
				gl.glLineWidth(2f);
				gl.glBegin(GL.GL_LINE_LOOP);
				gl.glColor4fv(bottomColor, 0);
				gl.glVertex3f(0, 0, 1f);
				gl.glVertex3f(x, 0, 1f);
				gl.glColor4fv(topColor, 0);
				gl.glVertex3f(x, y, 1f);
				gl.glVertex3f(0, y, 1f);

				gl.glEnd();
				gl.glPopAttrib();
			}
		}
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

	@Override
	public void onDispose() {
		dataCellSelectionManager.unregisterEventListeners();
		parentView.removeEventListener(this);
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		// if (selectionManager == dataCellSelectionManager) {
		//
		// }
	}

	@ListenTo
	public void onShowDataClassification(ShowDataClassificationEvent event) {
		if (event.getDataCellID() == cellID && isHighlightMode) {
			dataClassifier = event.getClassifier();
			parentView.setDisplayListDirty();
		}
	}

	public boolean isShowDataClassification() {
		return dataClassifier != null && dataCellSelectionManager.checkStatus(SelectionType.SELECTION, cellID);
	}

}
