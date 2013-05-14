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
package org.caleydo.view.stratomex.tourguide;

import gleem.linalg.Vec3f;

import java.util.Collections;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Colors;
import org.caleydo.core.util.color.IColor;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.LayoutRendererAdapter;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.stratomex.Activator;
import org.caleydo.view.stratomex.EPickingType;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.brick.GLBrick;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.event.HighlightBrickEvent;
import org.caleydo.view.stratomex.tourguide.event.ConfirmedCancelNewColumnEvent;
import org.caleydo.view.stratomex.tourguide.event.SelectGroupEvent;
import org.caleydo.view.stratomex.tourguide.event.SelectGroupReplyEvent;
import org.caleydo.view.stratomex.tourguide.event.SelectStratificationEvent;
import org.caleydo.view.stratomex.tourguide.event.SelectStratificationReplyEvent;
import org.caleydo.view.stratomex.tourguide.event.UpdatePreviewEvent;
import org.caleydo.view.stratomex.tourguide.internal.ConfirmCancelLayoutRenderer;
import org.caleydo.view.stratomex.tourguide.internal.ESelectionMode;
import org.caleydo.view.stratomex.tourguide.internal.event.AddNewColumnEvent;
import org.caleydo.view.stratomex.tourguide.internal.event.ConfirmCancelNewColumnEvent;

import com.google.common.collect.Iterables;

/**
 * @author Samuel Gratzl
 *
 */
public class TourguideAdapter {
	private static final String EXTENSION_POINT = "org.caleydo.view.stratomex.AddWizardElementFactory";

	private static final String ADD_PICKING_TYPE = "templateAdd";
	private static final String CONFIRM_PICKING_TYPE = "templateConfirm";
	private static final String CANCEL_PICKING_TYPE = "templateAbort";

	private final GLStratomex stratomex;

	/**
	 * factory of the wizard
	 */
	private final IAddWizardElementFactory factory = ExtensionUtils.findFirstImplementation(EXTENSION_POINT, "class",
			IAddWizardElementFactory.class);

	private int templateIndex;
	private ElementLayout templateColumn;
	private BrickColumn preview;

	/**
	 * the current selection and related information
	 */
	private ESelectionMode selectionMode = null;
	private GLBrick selectionCurrent = null;
	private Object selectionReceiver = null;

	public TourguideAdapter(GLStratomex stratomex) {
		this.stratomex = stratomex;
	}

	public void renderAddButton(GL2 gl, float x, float y, float w, float h, int id) {
		if (factory == null || templateColumn != null || preview != null) // not more than one at the sam etime
			return;
		renderButton(gl, x, y, w, h, stratomex, ADD_PICKING_TYPE, id, "resources/icons/stratomex/template/add.png");
	}

	public void renderConfirmButton(GL2 gl, float x, float y, float w, float h, int id) {
		renderButton(gl, x, y, w, h, stratomex, CONFIRM_PICKING_TYPE, id,
				"resources/icons/stratomex/template/accept.png");
	}

	public void renderCancelButton(GL2 gl, float x, float y, float w, float h, int id) {
		renderButton(gl, x, y, w, h, stratomex, CANCEL_PICKING_TYPE, id,
				"resources/icons/stratomex/template/cancel.png");
	}

	private static void renderButton(GL2 gl, float x, float y, float w, float h, AGLView view, String pickingType,
			int id, String texture) {
		GLGraphics.checkError(gl);

		id = view.getPickingManager().getPickingID(view.getID(), pickingType, id + 1);
		// stratomex.addIDPickingTooltipListener("Add another column", pickingType, pickedObjectID)
		gl.glPushName(id);

		final float wi = view.getPixelGLConverter().getGLWidthForPixelWidth(32);
		final float hi = view.getPixelGLConverter().getGLHeightForPixelHeight(32);
		final float xi = x + w * 0.5f - wi * 0.5f;
		final float yi = y + h * 0.5f - hi * 0.5f;
		final float z = 1.5f;

		Vec3f lowerLeftCorner = new Vec3f(xi, yi, z);
		Vec3f lowerRightCorner = new Vec3f(xi + wi, yi, z);
		Vec3f upperRightCorner = new Vec3f(xi + wi, yi + hi, z);
		Vec3f upperLeftCorner = new Vec3f(xi, yi + hi, z);

		view.getTextureManager().renderTexture(gl, texture, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();
		GLGraphics.checkError(gl);
	}

	/**
	 * @param glStratomex
	 */
	public void registerPickingListeners() {
		final Object receiver = TourguideAdapter.this;
		stratomex.addTypePickingTooltipListener("Add another column at this position", ADD_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new AddNewColumnEvent(pick.getObjectID() - 1).to(receiver).from(this));
			}
		}, ADD_PICKING_TYPE);

		stratomex.addTypePickingTooltipListener("Confirm the current previewed element", CONFIRM_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(true, pick.getObjectID() - 1).to(receiver)
							.from(this));
			}
		}, CONFIRM_PICKING_TYPE);

		stratomex.addTypePickingTooltipListener("Cancel temporary column", CANCEL_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(false, pick.getObjectID() - 1).to(receiver)
							.from(this));
			}
		}, CANCEL_PICKING_TYPE);

		IPickingListener brickPicker = new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				onBrickPick(pick);
			}
		};
		stratomex.addTypePickingListener(brickPicker, EPickingType.BRICK.name());
		stratomex.addTypePickingListener(brickPicker, EPickingType.BRICK_TITLE.name());
	}

	/**
	 * if we pick an brick
	 *
	 * @param pick
	 */
	protected void onBrickPick(Pick pick) {
		// don't need to select
		if (pick.getPickingMode() != PickingMode.CLICKED || selectionMode == null)
			return;
		GLBrick brick = findBick(pick.getObjectID());
		if (brick == null)
			return;
		boolean isHeader = brick.isHeaderBrick();
		if (isHeader != (selectionMode == ESelectionMode.STRATIFICATION))
			return;
		if (this.selectionCurrent == brick)
			return;

		if (this.selectionCurrent != null) {
			changeHighlight(this.selectionCurrent, Colors.GREEN);
		}
		changeHighlight(brick, Colors.YELLOW);
		this.selectionCurrent = brick;

		stratomex.setDisplayListDirty();

		// fire selection
		TablePerspective tablePerspective = brick.getBrickColumn().getTablePerspective();
		if (selectionMode == ESelectionMode.GROUP) {
			Group group = brick.getTablePerspective().getRecordGroup();
			EventPublisher.trigger(new SelectGroupReplyEvent(tablePerspective, group).to(selectionReceiver).from(this));
		} else {
			EventPublisher.trigger(new SelectStratificationReplyEvent(tablePerspective).to(selectionReceiver)
					.from(this));

		}
	}

	private void changeHighlight(GLBrick brick, IColor color) {
		// select brick by changing highlight
		for (BrickHighlightRenderer glow : Iterables.filter(brick.getLayout().getBackgroundRenderer(),
				BrickHighlightRenderer.class)) {
			glow.setColor(color.getRGBA());
		}
	}

	@ListenTo(sendToMe = true)
	private void onSelectBrickRequest(SelectStratificationEvent event) {
		this.selectionMode = ESelectionMode.STRATIFICATION;
		selectionReceiver = event.getSender();

		// highlight all possibles
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			if (event.getFilter().apply(col.getTablePerspective()))
				addHighlight(col.getHeaderBrick());
		}
		repaint();
	}

	@ListenTo(sendToMe = true)
	private void onSelectBrickRequest(SelectGroupEvent event) {
		this.selectionMode = ESelectionMode.GROUP;
		selectionReceiver = event.getSender();

		// highlight all possibles
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			TablePerspective tablePerspective = col.getTablePerspective();
			for (GLBrick brick : col.getSegmentBricks()) {
				if (event.getFilter().apply(Pair.make(tablePerspective, brick.getTablePerspective().getRecordGroup())))
					addHighlight(brick);
			}
		}
		repaint();
	}

	@ListenTo(sendToMe = true)
	private void onHighlight(HighlightBrickEvent event) {
		BrickColumnManager manager = stratomex.getBrickColumnManager();
		BrickColumn brickColumn = manager.getBrickColumn(event.getStratification());
		if (brickColumn == null)
			return;

		ElementLayout layout = null;
		if (event.getGroup() == null) {
			layout = brickColumn.getLayout();
		} else {
			Group g = event.getGroup();
			for (GLBrick brick : brickColumn.getSegmentBricks()) {
				if (g.equals(brick.getTablePerspective().getRecordGroup())) {
					layout = brick.getLayout();
					break;
				}
			}
		}
		if (layout == null)
			return;

		if (!event.isHighlight()) {
			layout.clearBackgroundRenderers();
		} else {
			layout.addBackgroundRenderer(new BrickHighlightRenderer(event.getColor().getRGBA()));
		}
		if (layout.getLayoutManager() != null)
			layout.updateSubLayout();
	}

	private void repaint() {
		stratomex.updateLayout();
		stratomex.setDisplayListDirty();
	}

	/**
	 * @param headerBrick
	 */
	private void addHighlight(GLBrick brick) {
		brick.getLayout().addBackgroundRenderer(new BrickHighlightRenderer(Colors.GREEN.getRGBA()));
	}

	/**
	 * @param brickId
	 * @return
	 */
	private GLBrick findBick(int brickId) {
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			if (col.getHeaderBrick().getID() == brickId)
				return col.getHeaderBrick();
			for (GLBrick brick : col.getSegmentBricks()) {
				if (brick.getID() == brickId)
					return brick;
			}
		}
		return null;
	}

	/**
	 * @param index
	 * @return
	 */
	private ElementLayout createTemplateElement(int index) {
		assert factory != null;
		ElementLayout l = ElementLayouts.wrap(new LayoutRendererAdapter(stratomex, Activator.getResourceLocator(),
				factory.create(this, stratomex.getTablePerspectives()), null), 120);
		l.addBackgroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, index, this));
		return l;
	}

	@ListenTo(sendToMe = true)
	private void onAddEmptyColumn(AddNewColumnEvent event) {
		if (preview != null || templateColumn != null)
			return;
		// BrickColumn brick = createBrickColumn(new TemplateDataConfigurer(), null);
		//
		int index;
		if (event.getObjectId() <= 0) {
			// left or first
			index = -1;
		} else {
			// right of
			BrickColumnManager brickColumnManager = stratomex.getBrickColumnManager();
			BrickColumn col = brickColumnManager.getBrickColumnSpacers().get(event.getObjectId()).getLeftDimGroup();
			index = col == null ? -1 : brickColumnManager.getBrickColumns().indexOf(col);
		}

		templateIndex = index;
		templateColumn = createTemplateElement(index + 1);

		stratomex.relayout();
	}

	@ListenTo(sendToMe = true)
	private void onConfirmCancelColumn(ConfirmCancelNewColumnEvent event) {
		boolean confirm = event.isConfirm();

		if (templateColumn == null && preview == null) // nothing todo
			return;

		// reset
		reset();

		if (confirm) {
			// remove the preview buttons
			if (preview != null)
				preview.getLayout().clearForegroundRenderers();
		} else {
			destroyTemplate();
			if (preview != null)
				stratomex.removeTablePerspective(preview.getTablePerspective());
		}
		preview = null;
		stratomex.relayout();
		EventPublisher.trigger(new ConfirmedCancelNewColumnEvent().from(this));
	}

	/**
	 *
	 */
	private void reset() {
		selectionMode = null;
		selectionReceiver = null;
		selectionCurrent = null;
		// clear highlights
		for (BrickColumn col : stratomex.getBrickColumnManager().getBrickColumns()) {
			col.getHeaderBrick().getLayout().clearBackgroundRenderers();
			for (GLBrick brick : col.getSegmentBricks()) {
				brick.getLayout().clearBackgroundRenderers();
			}
		}
	}

	/**
	 * @return
	 */
	private int destroyTemplate() {
		if (templateColumn != null)
			templateColumn.destroy(GLContext.getCurrent().getGL().getGL2());
		templateColumn = null;
		return templateIndex;
	}

	@ListenTo(sendToMe = true)
	private void onUpdatePreview(UpdatePreviewEvent event) {
		if (templateColumn == null)
			return;
		//convert the template column to a brick column
		List<Pair<Integer, BrickColumn>> added;
		if (preview == null) {
			int index = destroyTemplate();
			BrickColumnManager bcm = stratomex.getBrickColumnManager();
			BrickColumn left = index < 0 ? null : bcm.getBrickColumns().get(bcm.getCenterColumnStartIndex() + index);
			added = stratomex.addTablePerspectives(
Collections.singletonList(event.getTablePerspective()), null, left,
					false);
		} else {
			added = stratomex.addTablePerspectives(Collections.singletonList(event.getTablePerspective()), null,
					preview, true);
			stratomex.removeTablePerspective(preview.getTablePerspective());
		}

		preview = added.get(0).getSecond();
		preview.getLayout().addForeGroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, templateIndex, this));

	}

	/**
	 * @param columns
	 */
	public void addTemplateColumns(List<Object> columns) {
		if (templateColumn == null)
			return;
		if (templateIndex < 0)
			columns.add(0,templateColumn);
		else {
			int index = templateIndex - stratomex.getBrickColumnManager().getCenterColumnStartIndex();
			columns.add(index + 1, templateColumn);
		}
	}

	/**
	 * whether a template column exists or not
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return templateColumn == null;
	}


}
