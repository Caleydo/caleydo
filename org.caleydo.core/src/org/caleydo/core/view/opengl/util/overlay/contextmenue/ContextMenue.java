package org.caleydo.core.view.opengl.util.overlay.contextmenue;

import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_TIP_HEIGHT;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_WIDTH;
import static org.caleydo.core.view.opengl.canvas.storagebased.ParCoordsRenderStyle.GATE_Z;

import java.awt.Font;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.AOverlayManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.GLIconTextureManager;
import org.eclipse.swt.internal.gtk.XWindowChanges;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

public class ContextMenue
	extends AOverlayManager {

	private static final float ITEM_SIZE = 0.1f;
	private static final float ICON_SIZE = 0.08f;
	private static final float SPACER_SIZE = 0.02f;
	private static final float SPACING = 0.02f;
	private static final float FONT_SCALING = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	private ArrayList<AContextMenueItem> contextMenueItems;

	String sData = "test";
	private float xOrigin;
	private float yOrigin;
	private float width;
	private float height;

	private TextRenderer textRenderer;

	private float longestTextWidth = 0;

	private GLIconTextureManager iconManager;

	private PickingManager pickingManager;

	private int iViewID;

	public ContextMenue(int iViewID) {
		super();
		this.iViewID = iViewID;
		contextMenueItems = new ArrayList<AContextMenueItem>();
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 32), true, true);
		textRenderer.setSmoothing(true);
		iconManager = new GLIconTextureManager();
		pickingManager = GeneralManager.get().getViewGLCanvasManager().getPickingManager();

	}

	public void addContextMenueItem(AContextMenueItem item) {
		contextMenueItems.add(item);

		float textWidth = (float) textRenderer.getBounds(item.getText()).getWidth() * FONT_SCALING;
		if (textWidth > longestTextWidth)
			longestTextWidth = textWidth;
	}

	public void render(GL gl) {
		if (!isEnabled)
			return;
		if (isFirstTime) {
			isFirstTime = false;
			if (contextMenueItems.size() == 0)
				return;

			float[] fArWorldCoords =
				GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);
			xOrigin = fArWorldCoords[0];
			yOrigin = fArWorldCoords[1];

			width = longestTextWidth + 3 * SPACING + ICON_SIZE;
			height = contextMenueItems.size() * ITEM_SIZE + SPACING;
			// FONT_SCALING = 1.0f / (windowWidth * 1.1f);
			// fXElementOrigin = fXOrigin + 0.2f;
			// fYElementOrigin = fYOrigin + 0.2f;
			// vecLowerLeft.set(xOrigin, yOrigin, 0);

		}

		drawMenue(gl);
	}

	private void drawMenue(GL gl) {

		gl.glColor4f(0.6f, 0.6f, 0.6f, 1f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(xOrigin, yOrigin, 0.01f);
		gl.glVertex3f(xOrigin, yOrigin - height, 0.01f);
		gl.glVertex3f(xOrigin + width, yOrigin - height, 0.01f);
		gl.glVertex3f(xOrigin + width, yOrigin, 0.01f);
		gl.glEnd();

		float yPosition = yOrigin;

		int count = 0;
		for (AContextMenueItem item : contextMenueItems) {

			float xPosition = xOrigin + SPACING;
			yPosition -= ITEM_SIZE;

			float alpha = 0.5f;
			if (count % 2 == 0)
				alpha = 0.8f;

			gl.glColor4f(1, 1, 1, alpha);

			gl.glPushName(pickingManager.getPickingID(iViewID, EPickingType.CONTEXT_MENUE_SELECTION, count));
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(xPosition, yPosition - SPACING / 2, 0.011f);
			gl.glVertex3f(xPosition + width - 2 * SPACING, yPosition - SPACING / 2, 0.011f);
			gl.glColor4f(1, 1, 1, 0.0f);
			gl.glVertex3f(xPosition + width - 2 * SPACING, yPosition + ITEM_SIZE - SPACING / 2, 0.011f);
			gl.glVertex3f(xPosition, yPosition + ITEM_SIZE - SPACING / 2, 0.011f);
			gl.glEnd();
			gl.glPopName();

			Texture tempTexture = iconManager.getIconTexture(gl, item.getIconTexture());
			tempTexture.enable();
			tempTexture.bind();
			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glColor4f(1, 1, 1, 1);

			gl.glBegin(GL.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(xPosition, yPosition, 0.012f);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(xPosition + ICON_SIZE, yPosition, 0.012f);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(xPosition + ICON_SIZE, yPosition + ICON_SIZE, 0.012f);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(xPosition, yPosition + ICON_SIZE, 0.012f);
			gl.glEnd();
			tempTexture.disable();

			xPosition += ICON_SIZE + SPACING;

			textRenderer.begin3DRendering();
			textRenderer.setColor(0, 0, 0, 1);
			gl.glDisable(GL.GL_DEPTH_TEST);

			textRenderer.draw3D(item.getText(), xPosition, yPosition, 0.0f, FONT_SCALING);
			// textRenderer.flush();
			textRenderer.end3DRendering();
			count++;

		}
		// gl.glEnable(GL.GL_DEPTH_TEST);

	}
}
