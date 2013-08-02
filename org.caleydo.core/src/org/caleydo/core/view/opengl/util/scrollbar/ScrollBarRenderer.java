/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.view.opengl.util.scrollbar;

import javax.media.opengl.GL2;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.layout.ALayoutRenderer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;

/**
 * Renderer for a {@link ScrollBar}. Notifies the {@link IScrollBarUpdateHandler} of the ScrollBar when it is
 * dragged.
 *
 * @author Partl
 */
public class ScrollBarRenderer
	extends ALayoutRenderer
	implements IDraggable {

	private AGLView view;
	private ScrollBar scrollBar;
	private boolean isHorizontal;
	private DragAndDropController dragAndDropController;
	private float prevDraggingMouseX;
	private float prevDraggingMouseY;
	private float positionX;
	private float positionY;
	private float scrollBarHeight;
	private float scrollBarWidth;
	private final Color color;

	/**
	 * Constructor.
	 *
	 * @param scrollBar
	 *            The ScrollBar this renderer shall be used for.
	 * @param view
	 *            The view that renders the ScrollBar.
	 * @param isHorizontal
	 *            Determines whether the ScrollBar shall be rendered horizontally.
	 * @param dragAndDropController
	 */
	public ScrollBarRenderer(ScrollBar scrollBar, AGLView view, boolean isHorizontal,
			DragAndDropController dragAndDropController, Color color) {
		this.view = view;
		this.scrollBar = scrollBar;
		this.isHorizontal = isHorizontal;
		this.dragAndDropController = dragAndDropController;
		this.color = color;

		createPickingListener();
	}

	private void createPickingListener() {
		view.addIDPickingListener(new APickingListener() {

			@Override
			public void clicked(Pick pick) {
				dragAndDropController.clearDraggables();
				dragAndDropController.setDraggingProperties(pick.getPickedPoint(), "ScrollbarDrag");
				dragAndDropController.addDraggable(ScrollBarRenderer.this);
			}

			@Override
			public void mouseOver(Pick pick) {
			}

//			@Override
//			public void dragged(Pick pick) {
//				if (!dragAndDropController.isDragging()) {
//					dragAndDropController.startDragging();
//				}
//			}

		}, scrollBar.getPickingType().name(), scrollBar.getID());
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {
		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

	}

	@Override
	public void handleDragging(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {

		float relativeSelection;

		if (isHorizontal) {
			if (prevDraggingMouseX >= mouseCoordinateX - 0.01
				&& prevDraggingMouseX <= mouseCoordinateX + 0.01)
				return;
			float mouseDelta = prevDraggingMouseX - mouseCoordinateX;
			positionX -= mouseDelta;
			relativeSelection = positionX / (x - scrollBarWidth);
		}
		else {
			if (prevDraggingMouseY >= mouseCoordinateY - 0.01
				&& prevDraggingMouseY <= mouseCoordinateY + 0.01)
				return;
			float mouseDelta = prevDraggingMouseY - mouseCoordinateY;

			positionY -= mouseDelta;
			relativeSelection = positionY / (y - scrollBarHeight);
		}

		int scrollBarSize = scrollBar.getMaxValue() - scrollBar.getMinValue();
		int selection = scrollBar.getMinValue() + (int) (relativeSelection * scrollBarSize);

		if (selection < scrollBar.getMinValue())
			selection = scrollBar.getMinValue();

		if (selection > scrollBar.getMaxValue())
			selection = scrollBar.getMaxValue();

		if (selection == scrollBar.getSelection())
			return;

		scrollBar.setSelection(selection);

		prevDraggingMouseX = mouseCoordinateX;
		prevDraggingMouseY = mouseCoordinateY;

		scrollBar.getScrollBarUpdateHandler().handleScrollBarUpdate(scrollBar);
	}

	@Override
	public void handleDrop(GL2 gl, float mouseCoordinateX, float mouseCoordinateY) {

		// float relativeSelection;
		//
		// if (isHorizontal) {
		// float mouseDelta = prevDraggingMouseX - mouseCoordinateX;
		// positionX += mouseDelta;
		// relativeSelection = positionX / (x - scrollBarWidth);
		// }
		// else {
		// float mouseDelta = prevDraggingMouseY - mouseCoordinateY;
		// positionY += mouseDelta;
		// relativeSelection = positionY / (y - scrollBarHeight);
		// }
		//
		// int scrollBarSize = scrollBar.getMaxValue() - scrollBar.getMinValue();
		// int selection = (int) relativeSelection / scrollBarSize;
		//
		// if (selection < scrollBar.getMinValue())
		// selection = scrollBar.getMinValue();
		//
		// if (selection > scrollBar.getMaxValue())
		// selection = scrollBar.getMaxValue();
		//
		// scrollBar.setSelection(selection);
		//
		// prevDraggingMouseX = mouseCoordinateX;
		// prevDraggingMouseY = mouseCoordinateY;

	}


	@Override
	protected void renderContent(GL2 gl) {
		int scrollBarSize = scrollBar.getMaxValue() - scrollBar.getMinValue();
		float relativePageSize = (float) scrollBar.getPageSize() / (float) scrollBarSize;
		float relativeSelection =
			((float) scrollBar.getSelection() - (float) scrollBar.getMinValue()) / scrollBarSize;

		if (isHorizontal) {
			scrollBarHeight = y;
			scrollBarWidth = relativePageSize * x;
			positionX = relativeSelection * (x - scrollBarWidth);
			positionY = 0;
		}
		else {
			scrollBarHeight = relativePageSize * y;
			scrollBarWidth = x;
			positionX = 0;
			positionY = relativeSelection * (y - scrollBarHeight);
		}
		// System.out.println("relative" + relativePageSize + " " + relativeSelection);
		//System.out.println("relative2 " + scrollBarWidth + " " + positionX);

		gl.glPushName(view.getPickingManager().getPickingID(view.getID(), scrollBar.getPickingType(),
			scrollBar.getID()));

		gl.glColor4fv(color.getRGBA(), 0);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex3f(positionX, positionY, 1);
		gl.glVertex3f(positionX + scrollBarWidth, positionY, 1);
		gl.glVertex3f(positionX + scrollBarWidth, positionY + scrollBarHeight, 1);
		gl.glVertex3f(positionX, positionY + scrollBarHeight, 1);
		gl.glEnd();

		gl.glPopName();


	}


	@Override
	protected boolean permitsWrappingDisplayLists() {
		return false;
	}
}
