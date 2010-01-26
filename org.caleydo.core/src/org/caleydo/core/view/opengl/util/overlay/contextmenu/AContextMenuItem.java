package org.caleydo.core.view.opengl.util.overlay.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.EventPublisher;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.util.texture.EIconTextures;

/**
 * Abstract base class for items in the context menu. A item must be supplied with a string to display its
 * function in the context menu as well as with an event which can be triggered. The events are of type
 * {@link AEvent} and are published via the {@link EventPublisher}. Optionally an icon can be supplied.
 * 
 * @author Alexander Lex
 */
public abstract class AContextMenuItem
	implements IContextMenuEntry {
	private String text;
	private EIconTextures iconTexture;
	private AEvent event;

	private static final int MAX_TEXT_LENGTH = 25;

	private SubMenu subMenuData;

	// private ArrayList<IContextMenuEntry> subItems;

	/**
	 * Sets the text which is shown when the item is rendered in a context menu. It is mandatory to set a text
	 * 
	 * @param text
	 *            The text containing a brief description of the event
	 */
	public void setText(String text) {
		if (text.length() < MAX_TEXT_LENGTH)
			this.text = text;
		else
			this.text = text.substring(0, MAX_TEXT_LENGTH - 3) + "...";
	}

	/**
	 * Sets the event which is associated with the item. This event will be triggered when requested by the
	 * context menue. It is mandatory to set an event.
	 * 
	 * @param event
	 *            the event triggered when requested by the context menu
	 */
	public void registerEvent(AEvent event) {
		this.event = event;
	}

	/**
	 * Sets a texture listed in {@link EIconTextures}. This is optional
	 * 
	 * @param iconTexture
	 *            The reference to an entry in EIconTextures, where the path of the texture is specified.
	 */
	public void setIconTexture(EIconTextures iconTexture) {
		this.iconTexture = iconTexture;
	}

	/**
	 * Add sub items to the item, which will be displayed as an extended context menu. The same rules as for
	 * {@link ContextMenu#addContextMenueItem(AContextMenuItem)} apply. Most importantly the order specified
	 * is relevant.
	 * 
	 * @param subItem
	 *            the sub-item to be added
	 */
	public void addSubItem(AContextMenuItem subItem) {
		if (subMenuData == null)
			subMenuData = new SubMenu();

		subMenuData.add(subItem);

	}

	/**
	 * Returns the description of the item
	 * 
	 * @return The description of the item
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the icon associated with the event
	 * 
	 * @return the reference to the texture for the icon
	 */
	public EIconTextures getIconTexture() {
		return iconTexture;
	}

	/**
	 * Returns true if the item contains sub-items, else false
	 * 
	 * @return flag determining whether this item contains sub items
	 */
	public boolean hasSubItems() {
		if (subMenuData == null)
			return false;
		return true;
	}

	/**
	 * Returns a list of sub items or null if no sub items were specified
	 * 
	 * @return
	 */
	public SubMenu getSubMenu() {
		return subMenuData;
	}

	/**
	 * Triggers the supplied event via the event publishing system
	 */
	public void triggerEvent() {
		if (event != null)
			GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

}
