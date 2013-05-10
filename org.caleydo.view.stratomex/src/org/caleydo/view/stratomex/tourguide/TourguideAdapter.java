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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.media.opengl.GL2;
import javax.media.opengl.GLContext;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ElementLayout;
import org.caleydo.core.view.opengl.layout.ElementLayouts;
import org.caleydo.core.view.opengl.layout2.LayoutRendererAdapter;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.stratomex.Activator;
import org.caleydo.view.stratomex.GLStratomex;
import org.caleydo.view.stratomex.column.BrickColumn;
import org.caleydo.view.stratomex.column.BrickColumnManager;
import org.caleydo.view.stratomex.tourguide.event.AddNewColumnEvent;
import org.caleydo.view.stratomex.tourguide.event.ConfirmCancelNewColumnEvent;

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
	private final IAddWizardElementFactory factory = ExtensionUtils.findFirstImplementation(EXTENSION_POINT, "class",
			IAddWizardElementFactory.class);

	private final Map<Integer, ElementLayout> templateColumns = new TreeMap<>();

	public TourguideAdapter(GLStratomex stratomex) {
		this.stratomex = stratomex;
	}

	public void renderAddButton(GL2 gl, float x, float y, float w, float h, int id) {
		if (factory == null)
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
		id = view.getPickingManager().getPickingID(view.getID(), pickingType, id);
		// stratomex.addIDPickingTooltipListener("Add another column", pickingType, pickedObjectID)
		gl.glPushName(id);
		float wi = view.getPixelGLConverter().getGLWidthForPixelWidth(32);
		float hi = view.getPixelGLConverter().getGLHeightForPixelHeight(32);
		float xi = x + w * 0.5f - wi * 0.5f;
		float yi = y + h * 0.5f - hi * 0.5f;
		Vec3f lowerLeftCorner = new Vec3f(xi, yi, 0.5f);
		Vec3f lowerRightCorner = new Vec3f(xi + wi, yi, 0.5f);
		Vec3f upperRightCorner = new Vec3f(xi + wi, yi + hi, 0.5f);
		Vec3f upperLeftCorner = new Vec3f(xi, yi + hi, 0.5f);

		view.getTextureManager().renderTexture(gl, texture, lowerLeftCorner, lowerRightCorner, upperRightCorner,
				upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();
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
					EventPublisher.trigger(new AddNewColumnEvent(pick.getObjectID()).to(receiver).from(this));
			}
		}, ADD_PICKING_TYPE);

		stratomex.addTypePickingTooltipListener("Confirm the current previewed element", CONFIRM_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(true, pick.getObjectID()).to(receiver)
							.from(this));
			}
		}, CONFIRM_PICKING_TYPE);

		stratomex.addTypePickingTooltipListener("Cancel temporary column", CANCEL_PICKING_TYPE);
		stratomex.addTypePickingListener(new IPickingListener() {
			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED)
					EventPublisher.trigger(new ConfirmCancelNewColumnEvent(false, pick.getObjectID()).to(receiver)
							.from(this));
			}
		}, CANCEL_PICKING_TYPE);
	}

	/**
	 * @param index
	 * @return
	 */
	private ElementLayout createTemplateElement(int index) {
		assert factory != null;
		ElementLayout l = ElementLayouts.wrap(new LayoutRendererAdapter(stratomex, Activator.getResourceLocator(),
 factory.create(), null), 120);
		l.addBackgroundRenderer(new ConfirmCancelLayoutRenderer(stratomex, index, this));
		return l;
	}

	@ListenTo(sendToMe = true)
	private void onAddEmptyColumn(AddNewColumnEvent event) {
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
			index = col == null ? 0 : brickColumnManager.getBrickColumns().indexOf(col);
		}

		templateColumns.put(index, createTemplateElement(index + 1));
		stratomex.relayout();
	}

	@ListenTo(sendToMe = true)
	private void onConfirmCancelColumn(ConfirmCancelNewColumnEvent event) {
		ElementLayout layout = templateColumns.remove(event.getObjectID() - 1);
		if (layout != null)
			layout.destroy(GLContext.getCurrent().getGL().getGL2());

		if (event.isConfirm()) {

		} else {

		}
		stratomex.relayout();
	}

	/**
	 * @param columns
	 * @param i
	 */
	public void add(List<Object> columns, int index) {
		if (templateColumns.containsKey(index))
			columns.add(templateColumns.get(index));
	}

	public boolean isEmpty() {
		return templateColumns.isEmpty();
	}

}
