package org.caleydo.core.view.opengl.util.overlay.contextmenu;

import gleem.linalg.Vec3f;

import java.awt.Font;
import java.util.HashMap;

import javax.media.opengl.GL2;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.picking.PickingManager;
import org.caleydo.core.manager.picking.PickingMode;
import org.caleydo.core.manager.picking.PickingType;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.caleydo.core.view.opengl.renderstyle.GeneralRenderStyle;
import org.caleydo.core.view.opengl.util.GLCoordinateUtils;
import org.caleydo.core.view.opengl.util.overlay.AOverlayManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;
import org.caleydo.core.view.opengl.util.texture.TextureManager;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;

/**
 * <p>
 * Renders a context menu of dynamically specified items. ContextMenu is based on {@link AOverlayManager}
 * where all the location relevant information is table. Since only one context menu can be active at a time it
 * is implemented as a singleton. It needs to be provided with a location and 1-n instances derived from
 * {@link AContextMenuItem}. On a left click it triggers the event specified in AContextMenuItem
 * </p>
 * <p>
 * Use {@link #addContextMenueItem(AContextMenuItem)}, {@link #addHeading(String)}, {@link #addSeparator()} to
 * add items individually, or use a pre-defined {@link AItemContainer} to add a bunch of items at the same
 * time.
 * </p>
 * <p>
 * Internally, context menues are based on sub-menus ({@link SubMenu}, which contain the items on one level of
 * the menu hierarchy, plus meta-information. Every instance of {@link AContextMenuItem} can contain a
 * sub-menu, which is rendered recursively if necessary.
 * </p>
 * <p>
 * The context menu is scaling-invariant - i.e. it does not change the size when the window is rescaled.
 * </p>
 * <p>
 * When the number of elements in a list exceeds the available space, {@link ScrollButtons} are added to the
 * list of rendered items which allow to navigate up and down in the context menu.
 * </p>
 * 
 * @author Alexander Lex
 */
public class ContextMenu
	extends AOverlayManager {

	// Coordinates stuff
	private static final float ITEM_HEIGHT = 0.11f;
	private static final float ICON_SIZE = 0.08f;
	private static final float TEXTURE_SIZE = 0.08f;
	// private static final float SPACER_SIZE = 0.02f;
	private static final float SIDE_SPACING = 0.05f;
	private static final float SPACING = 0.04f;
	private static final float FONT_SCALING = GeneralRenderStyle.SMALL_FONT_SCALING_FACTOR;

	private static final float BASIC_Z = 0.1f;
	private static final float BUTTON_Z = BASIC_Z + 0.001f;
	private static final float TEXT_Z = BUTTON_Z + 0.001f;

	/** Overhead for width for the context menu which should be added to the maximum text width */
	private static final float WIDHT_OVERHEAD = 2 * SIDE_SPACING + SPACING + 2 * ICON_SIZE;
	/** Overhead for height for the context menu which should be added to {@literal NrElements * ITEM_HEIGHT} */
	private static final float HEIGHT_OVERHEAD = 2 * SIDE_SPACING;

	/** The first-level sub-menu */
	private SubMenu baseMenu;
	/**
	 * Each context sub-menu (i.e. everything that has its own meta-data) is associated with a ID. This is a
	 * counter to provide new IDs for the context menus.
	 */
	private int contextMenuCounter = 0;

	private TextRenderer textRenderer;

	private TextureManager iconManager;

	private PickingManager pickingManager;

	private AGLView masterGLView;

	private int mouseOverElement = -1;

	private int displayListIndex = -1;

	private boolean isDisplayListDirty = true;

	/** The singleton instance */
	private static ContextMenu instance;

	private int iPickingIDCounter = 0;

	private HashMap<AContextMenuItem, Integer> hashContextMenuItemToUniqueID;
	private HashMap<Integer, AContextMenuItem> hashUniqueIDToContextMenuItem;

	/** Hashes sub-context menu IDs to its metadata */
	private HashMap<Integer, SubMenu> hashContextMenuIDToSubMenu;

	/**
	 * Private constructor since this is a singleton
	 */
	private ContextMenu() {
		super();

		baseMenu = new SubMenu();

		pickingManager = GeneralManager.get().getViewGLCanvasManager().getPickingManager();

		hashContextMenuItemToUniqueID = new HashMap<AContextMenuItem, Integer>();
		hashUniqueIDToContextMenuItem = new HashMap<Integer, AContextMenuItem>();
		hashContextMenuIDToSubMenu = new HashMap<Integer, SubMenu>();

		minSize = 200;
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
	 * Set the GL2 view which is currently rendering the context menu. Only in this view will the context menu
	 * be rendered. For remote rendering the remote rendering view is required, while the rest of the data
	 * probably needs to be set in the embedded view.
	 * 
	 * @param masterViewID
	 *            the id of the view where the menu should be rendered
	 */
	public void setMasterGLView(AGLView masterGLView) {
		this.masterGLView = masterGLView;

		textRenderer = new TextRenderer(new Font("Arial", Font.PLAIN, 18), true, true);
		textRenderer.setSmoothing(true);
		iconManager = new TextureManager();
	}

	/**
	 * Adds an instance of AContextMenuItem to the context menu. The order items are supplied will be the same
	 * in which they appear.
	 * 
	 * @param item
	 *            an instance of AContextMenuItem
	 */
	public void addContextMenueItem(AContextMenuItem item) {
		baseMenu.contextMenuEntries.add(item);
	}

	/**
	 * Adds a separator at the next space
	 */
	public void addSeparator() {
		baseMenu.contextMenuEntries.add(new Separator());
	}

	/**
	 * Adds a heading at the next space
	 * 
	 * @param text
	 *            the text to be displayed for the heading
	 */
	public void addHeading(String text) {
		baseMenu.contextMenuEntries.add(new Heading(text));
	}

	/**
	 * Add a item container to the context menu. The items in the context menu are added automatically
	 * 
	 * @param itemContainer
	 *            the container which holds a list of items
	 */
	public void addItemContanier(AItemContainer itemContainer) {

		if (baseMenu.contextMenuEntries.size() != 0)
			addSeparator();
		for (IContextMenuEntry entry : itemContainer) {
			baseMenu.contextMenuEntries.add(entry);
		}
	}

	/**
	 * Renders the context menu if it is set up for the respective caller. It is safe to call this method in
	 * the display method regardless of whether a context menu should be rendered at the time. It first checks
	 * whether the calling view is the master view at the moment and then whether it is enabled. If both
	 * conditions are true the context menu is displayed. A display list is used which is automatically set
	 * dirty when an element is picked.
	 * 
	 * @param gl
	 * @param masterGLView
	 */
	public void render(GL2 gl, AGLView masterGLView) {

		if (this.masterGLView != masterGLView)
			return;
		if (!isEnabled)
			return;
		if (baseMenu.contextMenuEntries.size() == 0)
			return;
		if (isFirstTime) {
			isFirstTime = false;

			if (baseMenu.contextMenuEntries.size() == 0)
				return;

			if (displayListIndex == -1) {
				displayListIndex = gl.glGenLists(1);
			}

			float[] fArWorldCoords =
				GLCoordinateUtils
					.convertWindowCoordinatesToWorldCoordinates(gl, pickedPoint.x, pickedPoint.y);

			float[] fArLeftLimitWorldCoords =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, 0, 0);

			fLeftBorder = fArLeftLimitWorldCoords[0];
			// notice that top and bottom are inverse in opengl vs window coordinates
			fTopBorder = fArLeftLimitWorldCoords[1];

			float[] fArRightLimitWorldCoords =
				GLCoordinateUtils.convertWindowCoordinatesToWorldCoordinates(gl, windowWidth, windowHeight);

			fRightBorder = fArRightLimitWorldCoords[0];
			fBottomBorder = fArRightLimitWorldCoords[1];

			baseMenu.xOrigin = fArWorldCoords[0];
			baseMenu.yOrigin = fArWorldCoords[1];

			// This is necessary because of the problems
			// with the frustum and picking in the Bucket view.
			// FIXME: Find clean solution!!
			// if (masterGLView instanceof GLRemoteRendering) {
			// baseMenuMetaData.xOrigin *= 2f;
			// baseMenuMetaData.yOrigin *= 2f;
			// fRightBorder *= 2f;
			// fLeftBorder *= 2f;
			// fTopBorder *= 2f;
			// fBottomBorder *= 2f;
			// }

			initializeSubMenus(gl, baseMenu);

			if ((fRightBorder - baseMenu.xOrigin) < getScaledSizeOf(gl, baseMenu.width))
				baseMenu.xOrigin -= baseMenu.width;

			if ((fBottomBorder + baseMenu.yOrigin) < getScaledSizeOf(gl, baseMenu.height))
				baseMenu.yOrigin += baseMenu.height;

		}

		if (isDisplayListDirty) {
			gl.glNewList(displayListIndex, GL2.GL_COMPILE);
			gl.glDisable(GL2.GL_DEPTH_TEST);

			Vec3f scalingPivot = new Vec3f(baseMenu.xOrigin, baseMenu.yOrigin, BASIC_Z);

			beginGUIElement(gl, scalingPivot);
			drawMenu(gl, baseMenu, true);
			endGUIElement(gl);

			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glEndList();
			isDisplayListDirty = false;
		}

		gl.glCallList(displayListIndex);

	}

	/**
	 * <p>
	 * Initializes a sub menu and recursively initializes the sub menus of the items in contextMenuEntries.
	 * Sets unique IDs for every element and creates the ContextMenuMetaData objects for sub menus.
	 * </p>
	 * <p>
	 * Sets the width and height of a sub menu. The origin in X and Y have to be set at another place before
	 * calling this method.
	 * </p>
	 * 
	 * @param contextMenuEntries
	 *            the list of context menu items for the current sub menu
	 * @param metaData
	 *            The metaData information for the current sub menu. height and widht are set, origin hast to
	 *            be set before.
	 * @throws IllegalStateException
	 *             if xOrigin and yOrigin in metaData have not been initialized.
	 */
	private void initializeSubMenus(GL2 gl, SubMenu metaData) {

		metaData.contextMenuID = contextMenuCounter++;
		hashContextMenuIDToSubMenu.put(metaData.contextMenuID, metaData);

		metaData.maxTextWidth = 0;

		for (IContextMenuEntry entry : metaData.contextMenuEntries) {
			if (entry instanceof AContextMenuItem) {
				AContextMenuItem item = (AContextMenuItem) entry;

				hashUniqueIDToContextMenuItem.put(iPickingIDCounter, item);
				hashContextMenuItemToUniqueID.put(item, iPickingIDCounter++);

				float textWidth = (float) textRenderer.getBounds(item.getText()).getWidth() * FONT_SCALING;
				if (textWidth > metaData.maxTextWidth)
					metaData.maxTextWidth = textWidth;

				if (item.hasSubItems()) {
					initializeSubMenus(gl, item.getSubMenu());
				}
			}
			else if (entry instanceof Heading) {
				Heading heading = (Heading) entry;
				float textWidth = (float) textRenderer.getBounds(heading.getText()).getWidth() * FONT_SCALING;
				// headings don't have a icon
				textWidth -= ICON_SIZE - SPACING;
				if (textWidth > metaData.maxTextWidth)
					metaData.maxTextWidth = textWidth;
			}
		}
		metaData.width = metaData.maxTextWidth + WIDHT_OVERHEAD;
		metaData.height = metaData.contextMenuEntries.size() * ITEM_HEIGHT + HEIGHT_OVERHEAD;

		float availableHeight = fTopBorder - fBottomBorder;
		if (getScaledSizeOf(gl, metaData.height) > availableHeight) {
			metaData.isScrollingNecessary = true;

			metaData.contextMenuEntries.add(0, new ScrollButton(true));
			metaData.contextMenuEntries.add(new ScrollButton(false));

			metaData.nrVisibleElements =
				(int) ((availableHeight - HEIGHT_OVERHEAD) / getScaledSizeOf(gl, ITEM_HEIGHT));
			metaData.height = metaData.nrVisibleElements * ITEM_HEIGHT + HEIGHT_OVERHEAD;

			metaData.scrollButtonDownActive = true;
		}
	}

	/**
	 * Drawing the actual menu when rebuilding the display list
	 * 
	 * @param gl
	 */
	private void drawMenu(GL2 gl, SubMenu metaData, boolean isBaseMenu) {

		// This is necessary because of the problems
		// with the frustum and picking in the Bucket view.
		// FIXME: Find clean solution!!
		// if (!(masterGLView instanceof GLBucket))
		// gl.glTranslatef(0, 0, 2);

		drawBackground(gl, metaData);

		float yPosition = metaData.yOrigin - SIDE_SPACING;

		float xPosition = metaData.xOrigin + SIDE_SPACING;

		if (!metaData.isScrollingNecessary) {

			for (int count = 0; count < metaData.contextMenuEntries.size(); count++) {
				yPosition -= ITEM_HEIGHT;
				renderEntry(gl, metaData, count, xPosition, yPosition);

			}
		}
		else {

			yPosition -= ITEM_HEIGHT;
			renderEntry(gl, metaData, 0, xPosition, yPosition);
			for (int counter = metaData.elementRangeStart; counter < metaData.elementRangeStart
				+ metaData.nrVisibleElements - 2; counter++) {
				yPosition -= ITEM_HEIGHT;
				renderEntry(gl, metaData, counter, xPosition, yPosition);
			}
			yPosition -= ITEM_HEIGHT;
			renderEntry(gl, metaData, metaData.contextMenuEntries.size() - 1, xPosition, yPosition);

		}

		// This is necessary because of the problems
		// with the frustum and picking in the Bucket view.
		// FIXME: Find clean solution!!
		// if (!(masterGLView instanceof IGLRemoteRenderingView))
		// gl.glTranslatef(0, 0, -2);
	}

	private void renderEntry(GL2 gl, SubMenu subMenu, int elementIndex, float xPosition, float yPosition) {

		IContextMenuEntry entry = subMenu.contextMenuEntries.get(elementIndex);
		if (entry instanceof AContextMenuItem) {
			renderItem(gl, subMenu, entry, xPosition, yPosition);
		}
		else if (entry instanceof Separator) {

			gl.glPushAttrib(GL2.GL_LINE_BIT);
			gl.glColor3f(1, 1, 1);
			gl.glLineStipple(2, (short) 0xAAAA);
			gl.glEnable(GL2.GL_LINE_STIPPLE);
			gl.glBegin(GL2.GL_LINES);
			gl.glVertex3f(subMenu.xOrigin + 2 * SPACING, yPosition + ITEM_HEIGHT / 2, BUTTON_Z);
			gl.glVertex3f(subMenu.xOrigin + subMenu.width - 2 * SPACING, yPosition + ITEM_HEIGHT / 2,
				BUTTON_Z);
			gl.glEnd();
			gl.glPopAttrib();

		}
		else if (entry instanceof Heading) {
			Heading heading = (Heading) entry;

			textRenderer.begin3DRendering();
			textRenderer.setColor(1, 1, 1, 1);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			textRenderer.draw3D(heading.getText(), xPosition, yPosition + SPACING, TEXT_Z, FONT_SCALING);
			// textRenderer.flush();
			textRenderer.end3DRendering();
		}
		else if (entry instanceof ScrollButton) {

			ScrollButton button = (ScrollButton) entry;
			float top;
			float bottom;
			int iPickingID;
			Texture tempTexture;

			if (button.isUp()) {
				if (!subMenu.scrollButtonUpActive)
					return;
				iPickingID =
					pickingManager.getPickingID(masterGLView.getID(), PickingType.CONTEXT_MENU_SCROLL_UP,
						subMenu.contextMenuID);
				bottom = yPosition;
				top = yPosition + ICON_SIZE;

				if (subMenu.scrollButtonUpOver)
					tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_SCROLL_BUTTON_OVER);
				else
					tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_SCROLL_BUTTON);

			}
			else {
				if (!subMenu.scrollButtonDownActive)
					return;
				iPickingID =
					pickingManager.getPickingID(masterGLView.getID(), PickingType.CONTEXT_MENU_SCROLL_DOWN,
						subMenu.contextMenuID);
				top = yPosition;
				bottom = yPosition + ICON_SIZE;
				if (subMenu.scrollButtonDownOver)
					tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_SCROLL_BUTTON_OVER);
				else
					tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_SCROLL_BUTTON);
			}

			gl.glPushName(iPickingID);

			gl.glColor4f(1, 1, 1, 0f);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3f(subMenu.xOrigin + 2 * SPACING, yPosition, BUTTON_Z);
			gl.glVertex3f(subMenu.xOrigin + subMenu.width - 2 * SPACING, yPosition, BUTTON_Z);
			gl.glVertex3f(subMenu.xOrigin + subMenu.width - 2 * SPACING, yPosition + ITEM_HEIGHT, BUTTON_Z);
			gl.glVertex3f(subMenu.xOrigin + 2 * SPACING, yPosition + ITEM_HEIGHT, BUTTON_Z);
			gl.glEnd();

			float center = subMenu.xOrigin + subMenu.width / 2;

			tempTexture.enable();
			tempTexture.bind();
			TextureCoords texCoords = tempTexture.getImageTexCoords();
			gl.glColor4f(1, 1, 1, 1f);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(center - ITEM_HEIGHT / 2, bottom, BUTTON_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(center + ITEM_HEIGHT / 2, bottom, BUTTON_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(center + ITEM_HEIGHT / 2, top, BUTTON_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(center - ITEM_HEIGHT / 2, top, BUTTON_Z);
			gl.glEnd();

			tempTexture.disable();
			gl.glPopName();
		}
	}

	/**
	 * Renders an item in the context menu and recursively starts rendering its sub-menus if there are any
	 * 
	 * @param gl
	 * @param subMenu
	 * @param entry
	 * @param xPosition
	 * @param yPosition
	 */
	private void renderItem(GL2 gl, SubMenu subMenu, IContextMenuEntry entry, float xPosition, float yPosition) {
		AContextMenuItem item = (AContextMenuItem) entry;

		Integer itemID = hashContextMenuItemToUniqueID.get(entry);

		if (itemID == mouseOverElement || isSubElementSelected(item))
			renderHighlighting(gl, subMenu, yPosition);

		gl.glColor4f(0, 0, 0, 0);

		int iPickingID =
			pickingManager.getPickingID(masterGLView.getID(), PickingType.CONTEXT_MENU_SELECTION, itemID);
		gl.glPushName(iPickingID);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex3f(xPosition, yPosition - SPACING / 2, BUTTON_Z * 2);
		gl.glVertex3f(xPosition, yPosition + ITEM_HEIGHT - SPACING / 2, BUTTON_Z * 2);
		gl.glVertex3f(xPosition + subMenu.width - 2 * SPACING, yPosition + ITEM_HEIGHT - SPACING,
			BUTTON_Z * 2);
		gl.glVertex3f(xPosition + subMenu.width - 2 * SPACING, yPosition - SPACING / 2, BUTTON_Z * 2);
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
			gl.glBegin(GL2.GL_POLYGON);
			gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
			gl.glVertex3f(xPosition, yPosition, TEXT_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
			gl.glVertex3f(xPosition + ICON_SIZE, yPosition, TEXT_Z);
			gl.glTexCoord2f(texCoords.right(), texCoords.top());
			gl.glVertex3f(xPosition + ICON_SIZE, yPosition + ICON_SIZE, TEXT_Z);
			gl.glTexCoord2f(texCoords.left(), texCoords.top());
			gl.glVertex3f(xPosition, yPosition + ICON_SIZE, TEXT_Z);
			gl.glEnd();
			tempTexture.disable();
			gl.glPopName();
		}
		xPosition += ICON_SIZE + SPACING;

		textRenderer.begin3DRendering();
		textRenderer.setColor(1, 1, 1, 1);
		gl.glDisable(GL2.GL_DEPTH_TEST);

		textRenderer.draw3D(item.getText(), xPosition, yPosition + SPACING / 2, TEXT_Z, FONT_SCALING);
		// textRenderer.flush();
		textRenderer.end3DRendering();

		xPosition += subMenu.maxTextWidth;
		if (item.hasSubItems()) {

			Texture tempTexture =
				iconManager.getIconTexture(gl, EIconTextures.CM_SELECTION_RIGHT_EXTENSIBLE_BLACK);
			tempTexture.enable();
			tempTexture.bind();
			TextureCoords texCoords = tempTexture.getImageTexCoords();

			gl.glColor4f(1, 1, 1, 1);
			gl.glPushName(iPickingID);
			gl.glBegin(GL2.GL_POLYGON);
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

			if (itemID == mouseOverElement || isSubElementSelected(item)) {

				SubMenu subSubMenu = item.getSubMenu();
				subSubMenu.xOrigin = subMenu.xOrigin + subMenu.width;
				subSubMenu.yOrigin = yPosition + ITEM_HEIGHT;

				float remainingXSpace = fRightBorder - (subMenu.xOrigin + getScaledSizeOf(gl, subMenu.width));
				float scaledWidth = getScaledSizeOf(gl, subSubMenu.width);

				if (remainingXSpace < scaledWidth)
					subSubMenu.xOrigin = subMenu.xOrigin - subSubMenu.width;

				float scaledHeight = getScaledSizeOf(gl, subSubMenu.height);

				if (getScaledCoordinate(gl, subSubMenu.yOrigin, subMenu.yOrigin) - fBottomBorder < scaledHeight) {

					float distance = Math.abs(subMenu.yOrigin - fBottomBorder);
					float scaledDistance = getUnscaledSizeOf(gl, distance);

					subSubMenu.yOrigin = subMenu.yOrigin - scaledDistance + subSubMenu.height;

				}
				drawMenu(gl, item.getSubMenu(), false);
			}
		}
	}

	private boolean isSubElementSelected(AContextMenuItem item) {
		if (!item.hasSubItems())
			return false;
		for (IContextMenuEntry tempEntry : item.getSubMenu().contextMenuEntries) {
			if (tempEntry instanceof AContextMenuItem) {
				AContextMenuItem tempItem = (AContextMenuItem) tempEntry;

				if (hashContextMenuItemToUniqueID.get(tempEntry) == mouseOverElement)
					return true;

				if (isSubElementSelected(tempItem))
					return true;
			}

		}
		return false;
	}

	/**
	 * The handling of the picking. This has to be called when an element of the type
	 * {@link PickingType#CONTEXT_MENU_SELECTION} or other CONTEXT_MENU_* types are picked.
	 * 
	 * @param ePickingMode
	 *            the mode of the picking, eg. mouse-over or clicked. Only mouse-over and clicked are handled.
	 * @param externalID
	 *            the id which has to match one of the ids specified in {@link #display}
	 */
	public void handlePickingEvents(PickingType pickingType, PickingMode ePickingMode, int externalID) {

		for (SubMenu subMenu : hashContextMenuIDToSubMenu.values()) {
			subMenu.scrollButtonDownOver = false;
			subMenu.scrollButtonUpOver = false;
		}
		if (externalID == Integer.MAX_VALUE)
			return;
		SubMenu metaData;
		switch (pickingType) {
			case CONTEXT_MENU_SELECTION:

				switch (ePickingMode) {
					case MOUSE_OVER:
						mouseOverElement = externalID;
						isDisplayListDirty = true;
						break;
					case CLICKED:
						hashUniqueIDToContextMenuItem.get(externalID).triggerEvent();
						isDisplayListDirty = true;
						flush();
						break;
				}
				break;

			case CONTEXT_MENU_SCROLL_DOWN:
				metaData = hashContextMenuIDToSubMenu.get(externalID);
				switch (ePickingMode) {

					case CLICKED:
						if (metaData.elementRangeStart + metaData.nrVisibleElements - 2 < metaData.contextMenuEntries
							.size() - 1) {
							metaData.elementRangeStart++;
							metaData.scrollButtonUpActive = true;
							isDisplayListDirty = true;
							if (metaData.elementRangeStart + metaData.nrVisibleElements - 2 == metaData.contextMenuEntries
								.size() - 1)
								metaData.scrollButtonDownActive = false;
						}
						// we want to do mouse over every time
					case MOUSE_OVER:
						metaData.scrollButtonDownOver = true;
						isDisplayListDirty = true;
						break;
				}
				break;
			case CONTEXT_MENU_SCROLL_UP:
				metaData = hashContextMenuIDToSubMenu.get(externalID);
				switch (ePickingMode) {

					case CLICKED:
						if (metaData.elementRangeStart > 1) {
							metaData.elementRangeStart--;
							isDisplayListDirty = true;
							metaData.scrollButtonDownActive = true;
						}
						if (metaData.elementRangeStart == 1)
							metaData.scrollButtonUpActive = false;
						// we want to do mouse over every time
					case MOUSE_OVER:
						metaData.scrollButtonUpOver = true;
						isDisplayListDirty = true;
						break;
				}
				break;
		}

	}

	/**
	 * Overrides the flush method of {@link AOverlayManager}. Disables the context menu and clears the list of
	 * items supplied as well as the masterViewID.
	 */
	@Override
	public void flush() {
		super.flush();
		baseMenu.contextMenuEntries.clear();
		mouseOverElement = -1;
		isDisplayListDirty = true;
		masterGLView = null;
	}

	private void drawBackground(GL2 gl, SubMenu metaData) {
		// the body
		// gl.glBlendFunc(GL2.GL_DST_ALPHA, GL2.GL_ONE_MINUS_DST_ALPHA);

		gl.glPushName(pickingManager.getPickingID(masterGLView.getID(), PickingType.CONTEXT_MENU_SELECTION,
			Integer.MAX_VALUE));
		gl.glColor4f(0f, 0f, 0f, 0.9f);
		gl.glBegin(GL2.GL_POLYGON);

		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE, BASIC_Z);
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - metaData.height + TEXTURE_SIZE,
			BASIC_Z);
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - metaData.height
			+ TEXTURE_SIZE, BASIC_Z);
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE,
			BASIC_Z);
		gl.glEnd();

		drawCorners(gl, metaData);
		drawEdges(gl, metaData);
		gl.glPopName();

	}

	private void drawCorners(GL2 gl, SubMenu metaData) {
		Texture tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_CORNER_BLACK);
		tempTexture.enable();
		tempTexture.bind();
		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// top left corner
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin, metaData.yOrigin, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin, metaData.yOrigin - TEXTURE_SIZE, BUTTON_Z);
		gl.glEnd();

		// top right corner
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width, metaData.yOrigin, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width, metaData.yOrigin - TEXTURE_SIZE, BUTTON_Z);
		gl.glEnd();

		// bottom left corner

		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin, metaData.yOrigin - metaData.height, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - metaData.height, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - metaData.height + TEXTURE_SIZE,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin, metaData.yOrigin - metaData.height + TEXTURE_SIZE, BUTTON_Z);
		gl.glEnd();

		// bottom right corner
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width, metaData.yOrigin - metaData.height, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - metaData.height,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - metaData.height
			+ TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width, metaData.yOrigin - metaData.height + TEXTURE_SIZE,
			BUTTON_Z);
		gl.glEnd();

		tempTexture.disable();
	}

	private void drawEdges(GL2 gl, SubMenu metaData) {
		Texture tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_EDGE_BLACK);
		tempTexture.enable();
		tempTexture.bind();
		TextureCoords texCoords = tempTexture.getImageTexCoords();

		// top
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE, BUTTON_Z);
		gl.glEnd();

		// bottom
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - metaData.height, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - metaData.height,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - metaData.height
			+ TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - metaData.height + TEXTURE_SIZE,
			BUTTON_Z);
		gl.glEnd();

		// left
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin, metaData.yOrigin - metaData.height + TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin, metaData.yOrigin - TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + TEXTURE_SIZE, metaData.yOrigin - metaData.height + TEXTURE_SIZE,
			BUTTON_Z);
		gl.glEnd();

		// right
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width, metaData.yOrigin - TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width, metaData.yOrigin - metaData.height + TEXTURE_SIZE,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - metaData.height
			+ TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - TEXTURE_SIZE, metaData.yOrigin - TEXTURE_SIZE,
			BUTTON_Z);
		gl.glEnd();

		tempTexture.disable();
	}

	private void renderHighlighting(GL2 gl, SubMenu metaData, float yPosition) {
		Texture tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_SELECTION_SIDE_BLACK);
		tempTexture.enable();
		tempTexture.bind();
		TextureCoords texCoords = tempTexture.getImageTexCoords();

		float TEXTURE_SIZE = 0.12f;

		yPosition -= SPACING / 2;
		float width = 0.06f;
		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + SPACING / 2, yPosition + TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + SPACING / 2 + width, yPosition + TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + SPACING / 2 + width, yPosition, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + SPACING / 2, yPosition, BUTTON_Z);
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width - SPACING / 2, yPosition + TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width - SPACING / 2 - width, yPosition + TEXTURE_SIZE,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - SPACING / 2 - width, yPosition, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - SPACING / 2, yPosition, BUTTON_Z);
		gl.glEnd();

		tempTexture = iconManager.getIconTexture(gl, EIconTextures.CM_SELECTION_LINES_BLACK);
		tempTexture.enable();
		tempTexture.bind();
		texCoords = tempTexture.getImageTexCoords();

		gl.glColor4f(1, 1, 1, 1);
		gl.glBegin(GL2.GL_POLYGON);
		gl.glTexCoord2f(texCoords.left(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + SPACING / 2 + width, yPosition + TEXTURE_SIZE, BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.top());
		gl.glVertex3f(metaData.xOrigin + metaData.width - SPACING / 2 - width, yPosition + TEXTURE_SIZE,
			BUTTON_Z);
		gl.glTexCoord2f(texCoords.right(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + metaData.width - SPACING / 2 - width, yPosition, BUTTON_Z);
		gl.glTexCoord2f(texCoords.left(), texCoords.bottom());
		gl.glVertex3f(metaData.xOrigin + SPACING / 2 + width, yPosition, BUTTON_Z);
		gl.glEnd();

		tempTexture.disable();
	}

}
