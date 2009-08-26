package org.caleydo.core.view.opengl.canvas.storagebased;

import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_TIP_HEIGHT;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_Z;
import static org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle.getDecimalFormat;
import gleem.linalg.Vec3f;

import javax.media.opengl.GL;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.util.AGLGUIElement;
import org.caleydo.core.view.opengl.util.text.CaleydoTextRenderer;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

public class Gate
	extends AGLGUIElement {

	private int gateID;
	private float currentPosition;
	private float upperValue;
	private float lowerValue;
	private ISet set;
	private ParCoordsRenderStyle renderStyle;
	private float mouseTopSpacing;
	private float mouseBottomSpacing;
	private float currentTop;
	private float currentBottom;

	public Gate(int gateID, float lowerValue, float upperValue, ISet set, ParCoordsRenderStyle renderStyle) {
		this.gateID = gateID;
		this.upperValue = upperValue;
		this.lowerValue = lowerValue;
		this.set = set;
		this.renderStyle = renderStyle;
		currentTop = (float) set.getNormalizedForRaw(upperValue) * renderStyle.getAxisHeight();
		currentBottom = (float) set.getNormalizedForRaw(lowerValue) * renderStyle.getAxisHeight();
		minSize = 100;
	}

	public void draw(GL gl, PickingManager pickingManager, TextureManager textureManager,
		CaleydoTextRenderer textRenderer, int iViewID) {

		currentTop = (float) set.getNormalizedForRaw(upperValue) * renderStyle.getAxisHeight();

		// Scaled bottom = unscaled bottom !
		currentBottom = (float) set.getNormalizedForRaw(lowerValue) * renderStyle.getAxisHeight();
		float unscaledTop = getRealCoordinateFromScaledCoordinate(gl, currentTop, currentBottom);



		Vec3f scalingPivot = new Vec3f(currentPosition, currentBottom, GATE_Z);

		beginGUIElement(gl, scalingPivot);

		gl.glColor4f(1, 1, 1, 0f);
		int PickingID = pickingManager.getPickingID(iViewID, EPickingType.REMOVE_GATE, gateID);
		gl.glPushName(PickingID);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(currentPosition + GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		gl.glVertex3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop, GATE_Z);
		gl.glVertex3f(currentPosition + GATE_WIDTH, unscaledTop, GATE_Z);
		gl.glEnd();
		gl.glPopName();

		// The tip of the gate
		Vec3f lowerLeftCorner =
			new Vec3f(currentPosition - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		Vec3f lowerRightCorner =
			new Vec3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		Vec3f upperRightCorner = new Vec3f(currentPosition + 0.1828f - GATE_WIDTH, unscaledTop, GATE_Z);
		Vec3f upperLeftCorner = new Vec3f(currentPosition - GATE_WIDTH, unscaledTop, GATE_Z);

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GATE_TIP_SELECTION, gateID));

		textureManager.renderTexture(gl, EIconTextures.GATE_TOP, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		float menuHeight = 8 * GATE_WIDTH / 3.5f;

		lowerLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledTop + menuHeight, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop + menuHeight, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop, GATE_Z);
		upperLeftCorner.set(currentPosition - 7 * GATE_WIDTH, unscaledTop, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_MENUE, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		textRenderer.setColor(1, 1, 1, 1);
		renderNumber(gl, textRenderer, renderStyle, getDecimalFormat().format(upperValue), currentPosition
			- 5 * GATE_WIDTH, unscaledTop + 0.02f);
		gl.glPopName();

		// if (set.isSetHomogeneous())
		// {
		// // renderBoxedYValues(gl, fCurrentPosition, fTop,
		// // getDecimalFormat().format(
		// // set.getRawForNormalized(fTop / renderStyle.getAxisHeight())),
		// // ESelectionType.NORMAL);
		// }
		// else
		// {
		// // TODO storage based acces
		// }

		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GATE_BODY_SELECTION, gateID));

		lowerLeftCorner.set(currentPosition - GATE_WIDTH, currentBottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, currentBottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);
		upperLeftCorner.set(currentPosition - GATE_WIDTH, unscaledTop - GATE_TIP_HEIGHT, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_BODY, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		gl.glPopName();

		
		gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.GATE_BOTTOM_SELECTION, gateID));

		lowerLeftCorner.set(currentPosition - GATE_WIDTH, currentBottom, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, currentBottom, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, currentBottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);
		upperLeftCorner.set(currentPosition - GATE_WIDTH, currentBottom
			+ ParCoordsRenderStyle.GATE_BOTTOM_HEIGHT, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_BOTTOM, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);
		
		
		lowerLeftCorner.set(currentPosition - 7 * GATE_WIDTH, currentBottom - menuHeight, GATE_Z);
		lowerRightCorner.set(currentPosition + GATE_WIDTH, currentBottom - menuHeight, GATE_Z);
		upperRightCorner.set(currentPosition + GATE_WIDTH, currentBottom, GATE_Z);
		upperLeftCorner.set(currentPosition - 7 * GATE_WIDTH, currentBottom, GATE_Z);

		textureManager.renderTexture(gl, EIconTextures.GATE_MENUE, lowerLeftCorner, lowerRightCorner,
			upperRightCorner, upperLeftCorner, 1, 1, 1, 1);

		textRenderer.setColor(1, 1, 1, 1);
		renderNumber(gl, textRenderer, renderStyle, getDecimalFormat().format(lowerValue), currentPosition
			- 5 * GATE_WIDTH, currentBottom - menuHeight + 0.02f);
		gl.glPopName();

		endGUIElement(gl);

	}

	private void renderNumber(GL gl, CaleydoTextRenderer textRenderer, ParCoordsRenderStyle renderStyle,
		String rawValue, float xOrigin, float yOrigin) {

		textRenderer.begin3DRendering();
		float scaling = 0.004f;
		textRenderer.draw3D(rawValue, xOrigin, yOrigin, ParCoordsRenderStyle.TEXT_ON_LABEL_Z, scaling);
		textRenderer.end3DRendering();
	}

	public int getGateID() {
		return gateID;
	}

	public void setGateID(int gateID) {
		this.gateID = gateID;
	}

	public float getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(float currentPosition) {
		this.currentPosition = currentPosition;
	}

	public void handleDragging(GL gl, float mousePositionX, float mousePositionY, EPickingType draggedObject,
		boolean isGateDraggingFirstTime) {

		if (isGateDraggingFirstTime) {
			mouseTopSpacing = currentTop - mousePositionY;
			mouseBottomSpacing = mousePositionY - currentBottom;
			isGateDraggingFirstTime = false;
		}

		float tipUpperLimit = renderStyle.getAxisHeight();
		float tipLowerLimit = currentBottom + getScaledSizeOf(gl, GATE_TIP_HEIGHT);
		float bottomLowerLimit = 0;
		float bottomUpperLimit = currentTop - GATE_TIP_HEIGHT;

		switch (draggedObject) {

			case GATE_TIP_SELECTION:
				currentTop = mousePositionY;
				setTop(currentTop);
				break;

			case GATE_BOTTOM_SELECTION:
				if (mousePositionY <= bottomLowerLimit) {
					currentBottom = bottomLowerLimit;
				}
				else if (mousePositionY <= bottomUpperLimit) {
					currentBottom = mousePositionY;
				}
				else {
					currentBottom = bottomUpperLimit;
				}
				setBottom(currentBottom);
				break;

			case GATE_BODY_SELECTION:
				currentBottom = mousePositionY - mouseBottomSpacing;
				currentTop = mousePositionY + mouseTopSpacing;
				setBottom(currentBottom);
				setTop(currentTop);

				break;

			default:
				return;
		}

		if (currentTop > tipUpperLimit) {
			currentTop = tipUpperLimit;
			setTop(currentTop);
		}
		if (currentTop < tipLowerLimit) {
			currentTop = tipLowerLimit;
			setTop(currentTop);
		}
		if (currentBottom > currentTop - getScaledSizeOf(gl, GATE_TIP_HEIGHT)) {
			currentBottom = currentTop - getScaledSizeOf(gl, GATE_TIP_HEIGHT);
			setBottom(currentBottom);
		}
		if (currentBottom < bottomLowerLimit) {
			currentBottom = bottomLowerLimit;
			setBottom(currentBottom);
		}
	}

	public void setBottom(float bottom) {
		lowerValue = (float) set.getRawForNormalized(bottom / renderStyle.getAxisHeight());

		double setMin = set.getMin();

		if (lowerValue < setMin) {
			lowerValue = (float) setMin;
		}
	}

	public void setTop(float top) {
		upperValue = (float) set.getRawForNormalized(top / renderStyle.getAxisHeight());

		double setMax = set.getMax();

		if (upperValue > setMax) {
			upperValue = (float) setMax;
		}
	}

	public void setUpperValue(float upperValue) {
		this.upperValue = upperValue;
		currentTop = (float) set.getNormalizedForRaw(upperValue) * renderStyle.getAxisHeight();
		// gate.setSecond(new Float(set.getNormalizedForRaw(upperValue) * renderStyle.getAxisHeight()));
	}

	public float getUpperValue() {
		return upperValue;
	}

	public void setLowerValue(float lowerValue) {
		this.lowerValue = lowerValue;
		currentBottom = (float) set.getNormalizedForRaw(lowerValue) * renderStyle.getAxisHeight();
	}

	public float getLowerValue() {
		return lowerValue;
	}

	public float getCurrentTop() {
		return currentTop;
	}

	public void setCurrentTop(float currentTop) {
		this.currentTop = currentTop;
	}

	public float getCurrentBottom() {
		return currentBottom;
	}

	public void setCurrentBottom(float currentBottom) {
		this.currentBottom = currentBottom;
	}
}
