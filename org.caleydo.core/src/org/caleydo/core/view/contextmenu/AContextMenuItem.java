package org.caleydo.core.view.contextmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.contextmenu.item.SeparatorMenuItem;

/**
 * Abstract base class for items in the context menu. A item must be supplied with a string to display its
 * function in the context menu as well as with a list of event which can be triggered. The events are of type
 * {@link AEvent} and are published via the {@link EventPublisher}.
 * 
 * @author Marc Streit
 */
public abstract class AContextMenuItem {

	private JMenuItem menuItem;

	private ArrayList<AContextMenuItem> subMenuItems = new ArrayList<AContextMenuItem>();

	private String label = "<not set>";

	private ArrayList<AEvent> events = new ArrayList<AEvent>();

	public void setLabel(String label) {
		this.label = label;
	}

	public void create(JComponent parent) {

		if (!subMenuItems.isEmpty()) {

			final JMenu submenu = new JMenu();

			for (AContextMenuItem subMenuItem : subMenuItems) {
				subMenuItem.create(submenu);
			}

			submenu.setText(label);
			parent.add(submenu);
		}
		else {
			menuItem = new JMenuItem();
			menuItem.setText(label);

			ActionListener menuListener = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					triggerEvent();
				}
			};

			menuItem.addActionListener(menuListener);

			if (this instanceof SeparatorMenuItem)
				((JPopupMenu) parent).addSeparator();
			else
				parent.add(menuItem);
		}
	}

	/**
	 * Sets the event which is associated with the item. This event will be triggered when requested by the
	 * context menu. It is mandatory to set an event.
	 * 
	 * @param event
	 *            the event triggered when requested by the context menu
	 */
	public void registerEvent(AEvent event) {
		this.events.add(event);
	}

	/**
	 * Triggers the supplied event via the event publishing system
	 */
	public void triggerEvent() {
		if (events != null && events.size() > 0) {
			for (AEvent event : events) {
				GeneralManager.get().getEventPublisher().triggerEvent(event);
			}
		}
	}

	protected void addSubItem(AContextMenuItem contextMenuItem) {
		subMenuItems.add(contextMenuItem);
	}

	public JMenuItem getMenuItem() {
		return menuItem;
	}
}
