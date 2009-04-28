package org.caleydo.core.view.opengl.util.overlay.contextmenu;

import java.awt.Font;
import java.util.ArrayList;

import javax.media.opengl.GL;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.picking.EPickingMode;
import org.caleydo.core.manager.picking.EPickingType;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.AOverlayManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.GLIconTextureManager;

import com.sun.opengl.util.j2d.TextRenderer;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;

/**
 * Renders a context menu of dynamically specified items. ContextMenu is based on {@link AOverlayManager}
 * where all the location relevant information is set. Since only one context menu can be active at a time it
 * is implemented as a singleton. It needs to be provided with a location and 1-n instances derived from
 * {@link AContextMenuItem}. On a left click it triggers the event specified in AContextMenuItem
 * 
 * @author Alexander Lex
 */
public class ContextMenu
	extends AOverlayManager {

	// Coordinates stuff
	private static final float ITEM_SIZE = 0.1f;
	private static final float ICON_SIZE = 0.08f;
	private static final float SPACER_SIZE = 0.02f;
	private static final float SPACING = 0.02f;
	private static final float FONT_SCALING = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	private static final float BASIC_Z = 0.01f;
	private static final float BUTTON_Z = BASIC_Z + 0.001f;
	private static final float TEXT_Z = BUTTON_Z + 0.001f;

	/** The list of items that should be displayed and triggered */
	private ArrayList<AContextMenuItem> contextMenueItems;

	private float xOrigin;
	private float yOrigin;
	private float width;
	private float height;

	private TextRenderer textRenderer;

	private float longestTextWidth = 0;

	private GLIconTextureManager iconManager;

	private PickingManager pickingManager;

	private int masterViewID;

	private int mouseOverElement = -1;

	private int displayListIndex = -1;

	private boolean isDisplayListDirty = true;

	/** The singleton instance */
	private static ContextMenu instance;

	/**
	 * Private constructor since this is a singleton
	 */
	private ContextMenu() {
		super();
		contextMenueItems = new ArrayList<AContextMenuItem>();
		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 32), true, true);
		textRenderer.setSmoothing(true);
		iconManager = new GLIconTextureManager();
		pickingManager = GeneralManager.get().getViewGLCanvasManager().getPickingManager();
	}

	/**
	 * Static access method to the instance of the singleton. This is not thread-safe.
	 * 
	 * @return The sole instance of the ContextMenu
	 */
	public static ContextMenu get() {
		if (instance == null)
			instance = new ContextMenu();

		return instance;
	}

	/**
	 * Set the view ID which is currently rendering the context menu. Only in the view with the specified ID
	 * will the context menu be rendered. For remote rendering the ID of the remote renderer is required,
	 * while the rest of the data probably needs to be set in the embedded view.
	 * 
	 * @param masterViewID
	 *            the id of the view where the menu should be rendered
	 */
	public void setMasterViewID(int masterViewID) {
		this.masterViewID = masterViewID;
	}

	/**
	 * Adds an instance of AContextMenuItem to the context menu. The order items are supplied will be the same
	 * in which they appear.
	 * 
	 * @param item
	 *            an instance of AContextMenuItem
	 */
	public void addContextMenueItem(AContextMenuItem item) {
		contextMenueItems.add(item);

		float textWidth = (float) textRenderer.getBounds(item.getText()).getWidth() * FONT_SCALING;
		if (textWidth > longestTextWidth)
			longestTextWidth = textWidth;
	}

	/**
	 * Renders the context menu if it is set up for the respective caller. It is safe to call this method in
	 * the display method regardless of whether a context menu should be rendered at the time. It first checks
	 * whether the calling view is the master view at the moment and then whether it is enabled. If both
	 * conditions are true the context menu is displayed. A display list is used which is automatically set
	 * dirty when an element is picked.
	 * 
	 * @param gl
	 * @param viewID
	 */
	public void render(GL gl, int viewID) {
		if (viewID != masterViewID)
			return;
		if (!isEnabled)
			return;
		if (isFirstTime) {
			isFirstTime = false;

			if (contextMenueItems.size() == 0)
				return;

			if (displayListIndex == -1) {
				displayListIndex = gl.glGenLists(1);
			}

			float[] fArWorldCoords =
				GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);
			xOrigin = fArWorldCoords[0];
			yOrigin = fArWorldCoords[1];

			width = longestTextWidth + 3 * SPACING + ICON_SIZE;
			height = contextMenueItems.size() * ITEM_SIZE + SPACING;

		}

		if (isDisplayListDirty) {
			gl.glNewList(displayListIndex, GL.GL_COMPILE);
			drawMenue(gl);
			gl.glEndList();
			isDisplayListDirty = false;
		}

		gl.glCallList(displayListIndex);
	}

	/**
	 * Drawing the actual menu when rebuilding the display list
	 * 
	 * @param gl
	 */
	private void drawMenue(GL gl) {

		gl.glColor4f(0.6f, 0.6f, 0.6f, 1f);
		gl.glBegin(GL.GL_POLYGON);
		gl.glVertex3f(xOrigin, yOrigin, BASIC_Z);
		gl.glVertex3f(xOrigin, yOrigin - height, BASIC_Z);
		gl.glVertex3f(xOrigin + width, yOrigin - height, BASIC_Z);
		gl.glVertex3f(xOrigin + width, yOrigin, BASIC_Z);
		gl.glEnd();

		float yPosition = yOrigin;

		int count = 0;
		for (AContextMenuItem item : contextMenueItems) {

			float xPosition = xOrigin + SPACING;
			yPosition -= ITEM_SIZE;

			float alpha = 0f;
			if (count == mouseOverElement)
				alpha = 0.8f;

			gl.glColor4f(1, 1, 1, alpha);

			int iPickingID =
				pickingManager.getPickingID(masterViewID, EPickingType.CONTEXT_MENUE_SELECTION, count);
			gl.glPushName(iPickingID);
			gl.glBegin(GL.GL_POLYGON);
			gl.glVertex3f(xPosition, yPosition - SPACING / 2, BUTTON_Z);
			gl.glVertex3f(xPosition, yPosition + ITEM_SIZE - SPACING / 2, BUTTON_Z);

			gl.glColor4f(1, 1, 1, 0.0f);
			gl.glVertex3f(xPosition + width - 2 * SPACING, yPosition + ITEM_SIZE - SPACING / 2, BUTTON_Z);
			gl.glVertex3f(xPosition + width - 2 * SPACING, yPosition - SPACING / 2, BUTTON_Z);

			gl.glEnd();
			gl.glPopName();

			EIconTextures iconTexture = item.getIconTexture();

			// it is legal to specify no icons
			if (iconTexture != null) {
				Texture tempTexture = iconManager.getIconTexture(gl, iconTexture);
				tempTexture.enable();
				tempTexture.bind();
				TextureCoords texCoords = tempTexture.getImageTexCoords();

				gl.glColor4f(1, 1, 1, 1);
				gl.glPushName(iPickingID);
				gl.glBegin(GL.GL_POLYGON);
				gl.glTexCoord2f(texCoords.left(), texCoords.top());
				gl.glVertex3f(xPosition, yPosition, TEXT_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.top());
				gl.glVertex3f(xPosition + ICON_SIZE, yPosition, TEXT_Z);
				gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
				gl.glVertex3f(xPosition + ICON_SIZE, yPosition + ICON_SIZE, TEXT_Z);
				gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
				gl.glVertex3f(xPosition, yPosition + ICON_SIZE, TEXT_Z);
				gl.glEnd();
				tempTexture.disable();
				gl.glPopName();
			}
			xPosition += ICON_SIZE + SPACING;

			textRenderer.begin3DRendering();
			textRenderer.setColor(0, 0, 0, 1);
			gl.glDisable(GL.GL_DEPTH_TEST);

			textRenderer.draw3D(item.getText(), xPosition, yPosition, TEXT_Z, FONT_SCALING);
			// textRenderer.flush();
			textRenderer.end3DRendering();
			count++;

		}
		// gl.glEnable(GL.GL_DEPTH_TEST);

	}

	/**
	 * The handling of the picking. This has to be called when an element of the type
	 * {@link EPickingType#CONTEXT_MENUE_SELECTION} is picked.
	 * 
	 * @param ePickingMode
	 *            the mode of the picking, eg. mouse-over or clicked. Only mouse-over and clicked are handled.
	 * @param iExternalID
	 *            the id which has to match one of the ids specified in {@link #display}
	 */
	public void handleEvents(EPickingMode ePickingMode, int iExternalID) {
		switch (ePickingMode) {
			case MOUSE_OVER:
				mouseOverElement = iExternalID;
				isDisplayListDirty = true;
				break;
			case CLICKED:
				contextMenueItems.get(iExternalID).triggerEvent();
				isDisplayListDirty = true;
				break;
		}
	}

	/**
	 * Overrides the disable method of {@link AOverlayManager}. Disables the context menu and clears the list
	 * of items supplied as well as the masterViewID.
	 */
	@Override
	public void disable() {
		super.disable();
		contextMenueItems.clear();
		mouseOverElement = -1;
		isDisplayListDirty = true;
		masterViewID = -1;
	}
}
