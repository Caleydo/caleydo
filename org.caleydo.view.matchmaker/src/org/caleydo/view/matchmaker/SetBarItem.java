package org.caleydo.view.matchmaker;

import gleem.linalg.Vec3f;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Set;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.draganddrop.DragAndDropController;
import org.caleydo.core.view.opengl.util.draganddrop.IDraggable;
import org.caleydo.core.view.opengl.util.draganddrop.IDropArea;

import com.sun.opengl.util.j2d.TextRenderer;

public class SetBarItem implements IDraggable, IDropArea {

	public static final int SELECTION_STATUS_NORMAL = 0;
	public static final int SELECTION_STATUS_MOUSE_OVER = 1;
	private static final int SELECTION_STATUS_DRAGGED = 2;

	private static final float HORIZONTAL_SPACING = 0.1f;
	private static final float VERTICAL_SPACING = 0.1f;
	private static final float HORIZONTAL_TEXT_PADDING_PORTION = 0.1f;
	private static final float VERTICAL_TEXT_PADDING_PORTION = 0.7f;
	private static final String TEXT_FOR_HEIGHT_CALCULATION = "Text without characters below the bottom textline";

	private ISet set;
	private Vec3f position;
	private float height;
	private float width;
	private PickingManager pickingManager;
	private TextRenderer textRenderer;
	private SetBar setBar;
	private int viewID;
	private int id;
	private float draggingSpacingX;
	private float draggingSpacingY;
	private HashMap<Integer, float[]> hashSelectionColors;
	private int selectionStatus;

	public SetBarItem(int id, int viewID, PickingManager pickingManager,
			TextRenderer textRenderer, SetBar setBar) {
		this.id = id;
		this.viewID = viewID;
		this.pickingManager = pickingManager;
		this.textRenderer = textRenderer;
		this.setBar = setBar;
		hashSelectionColors = new HashMap<Integer, float[]>();
		hashSelectionColors.put(SELECTION_STATUS_NORMAL, new float[] { 0.5f, 0.5f, 0.5f,
				1.0f });
		float[] mouseOverColor = SelectionType.MOUSE_OVER.getColor();
		hashSelectionColors.put(SELECTION_STATUS_MOUSE_OVER, mouseOverColor);
		hashSelectionColors.put(SELECTION_STATUS_DRAGGED, new float[] {
				mouseOverColor[0], mouseOverColor[1], mouseOverColor[2], 0.5f });
	}

	public void render(GL gl) {

		gl.glPushName(pickingManager.getPickingID(viewID,
				EPickingType.COMPARE_SET_BAR_ITEM_SELECTION, id));
		renderItemBody(gl, selectionStatus, position.x(), position.y(), position.z());
		gl.glPopName();

		float fontScaling = determineFontScaling();
		String caption = getTruncatedCaption(set.getLabel(), fontScaling);
		Vec3f textPosition = calculateTextPosition(caption, fontScaling);

		textRenderer.setColor(0, 0, 0, 1);
		textRenderer.begin3DRendering();

		textRenderer.draw3D(caption, textPosition.x(), textPosition.y(),
				textPosition.z(), fontScaling);

		textRenderer.end3DRendering();
		textRenderer.flush();
	}

	private void renderItemBody(GL gl, int selectionStatus, float positionX,
			float positionY, float positionZ) {

		float horizontalspacingWidth = width * HORIZONTAL_SPACING;
		float bodyWidth = width - (2.0f * horizontalspacingWidth);
		float verticalSpacingWidth = height * VERTICAL_SPACING;
		float bodyHeight = height - (2.0f * verticalSpacingWidth);

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);

		gl.glColor4fv(hashSelectionColors.get(selectionStatus), 0);
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex3f(positionX + horizontalspacingWidth, positionY
				+ verticalSpacingWidth, positionZ);
		gl.glVertex3f(positionX + horizontalspacingWidth + bodyWidth, positionY
				+ verticalSpacingWidth, positionZ);
		gl.glVertex3f(positionX + horizontalspacingWidth + bodyWidth, positionY
				+ verticalSpacingWidth + bodyHeight, positionZ);
		gl.glVertex3f(positionX + horizontalspacingWidth, positionY
				+ verticalSpacingWidth + bodyHeight, positionZ);
		gl.glEnd();

		gl.glPopAttrib();

	}

	private void renderDropPositionMarker(GL gl, float positionX) {
		gl.glPushAttrib(GL.GL_LINE_BIT | GL.GL_COLOR_BUFFER_BIT);

		gl.glLineWidth(2.0f);

		gl.glColor3f(0.0f, 0.0f, 0.0f);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3f(positionX, position.y(), position.z() + 0.1f);
		gl.glVertex3f(positionX, position.y() + height, position.z() + 0.1f);
		gl.glEnd();

		gl.glPopAttrib();
	}

	/**
	 * Determines the scaling of a specified text that is needed for this text
	 * to fit into the item.
	 * 
	 * @param sText
	 *            Text the scaling shall be calculated for.
	 * @return Scaling factor for the specified text.
	 */
	private float determineFontScaling() {
		Rectangle2D bounds = textRenderer.getBounds(TEXT_FOR_HEIGHT_CALCULATION);
		float scaling = (height - VERTICAL_TEXT_PADDING_PORTION * height)
				/ (float) bounds.getHeight();

		return scaling;
	}

	private String getTruncatedCaption(String text, float fontScaling) {
		Rectangle2D bounds = textRenderer.getBounds(text);
		String caption = text;

		while (bounds.getWidth() * fontScaling > (width - (HORIZONTAL_TEXT_PADDING_PORTION * width))
				&& caption.length() != 0) {
			caption = caption.substring(0, caption.length() - 1);
			bounds = textRenderer.getBounds(caption);
		}

		return caption;
	}

	public Vec3f calculateTextPosition(String text, float fontScaling) {
		Rectangle2D bounds = textRenderer.getBounds(text);

		return new Vec3f((position.x() + width / 2.0f)
				- ((float) bounds.getWidth() * fontScaling / 2.0f), position.y()
				+ (VERTICAL_TEXT_PADDING_PORTION / 2.0f * height), position.z() + 0.1f);
	}

	public ISet getSet() {
		return set;
	}

	public void setSet(ISet set) {
		this.set = set;
	}

	public Vec3f getPosition() {
		return position;
	}

	public void setPosition(Vec3f position) {
		this.position = position;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	@Override
	public void handleDragging(GL gl, float mouseCoordinateX, float mouseCoordinateY) {

		float draggedPositionX = mouseCoordinateX + draggingSpacingX;
		float draggedPositionY = mouseCoordinateY + draggingSpacingY;

		gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		renderItemBody(gl, SELECTION_STATUS_DRAGGED, draggedPositionX, draggedPositionY,
				1.0f);
		gl.glEnd();
		gl.glPopAttrib();
	}

	@Override
	public void setDraggingStartPoint(float mouseCoordinateX, float mouseCoordinateY) {
		draggingSpacingX = position.x() - mouseCoordinateX;
		draggingSpacingY = position.y() - mouseCoordinateY;

	}

	@Override
	public void handleDragOver(GL gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY) {
		if (draggables.size() != 1)
			return;
		IDraggable draggable = (IDraggable) draggables.toArray()[0];

		if (draggable instanceof SetBarItem) {
			if (this == draggable)
				return;

			SetBarItem item = (SetBarItem) draggable;

			if (Math.abs(mouseCoordinateX - position.x()) < Math.abs(mouseCoordinateX
					- (position.x() + width))) {
				if (item.getID() == id - 1)
					return;
				renderDropPositionMarker(gl, position.x());

			} else {
				if (item.getID() == id + 1)
					return;
				renderDropPositionMarker(gl, position.x() + width);
			}
		}

	}

	@Override
	public void handleDrop(GL gl, Set<IDraggable> draggables, float mouseCoordinateX,
			float mouseCoordinateY, DragAndDropController dragAndDropController) {

		if (draggables.size() != 1)
			return;
		IDraggable draggable = (IDraggable) draggables.toArray()[0];

		if (draggable instanceof SetBarItem) {
			if (this == draggable)
				return;

			SetBarItem item = (SetBarItem) draggable;

			if (Math.abs(mouseCoordinateX - position.x()) < Math.abs(mouseCoordinateX
					- (position.x() + width))) {
				if (item.getID() == id - 1)
					return;
				setBar.moveItem(item, id);

			} else {
				if (item.getID() == id + 1)
					return;

				setBar.moveItem(item, id + 1);

			}
		}
	}

	public void setSelectionStatus(int selectionStatus) {
		this.selectionStatus = selectionStatus;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	@Override
	public void handleDrop(GL gl, float mouseCoordinateX, float mouseCoordinateY) {
	}

}
