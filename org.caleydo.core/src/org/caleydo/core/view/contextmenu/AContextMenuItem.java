package org.caleydo.core.view.contextmenu;

import java.util.ArrayList;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.EventPublisher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Abstract base class for items in the context menu. A item must be supplied with a string to display its
 * function in the context menu as well as with a list of event which can be triggered. The events are of type
 * {@link AEvent} and are published via the {@link EventPublisher}.
 * 
 * @author Marc Streit
 */
public abstract class AContextMenuItem {

	private MenuItem menuItem;

	private ArrayList<AContextMenuItem> subMenuItems = new ArrayList<AContextMenuItem>();

	private int style = SWT.PUSH;

	private String label = "<not set>";

	private ArrayList<AEvent> events = new ArrayList<AEvent>();

	public void setStyle(int style) {
		this.style = style;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void create(Menu parent) {

		if (!subMenuItems.isEmpty()) {
			
			menuItem = new MenuItem(parent, SWT.CASCADE);
			final Menu submenu = new Menu(parent.getParent(), SWT.DROP_DOWN);
			menuItem.setMenu(submenu);

			for (AContextMenuItem subMenuItem : subMenuItems) {
				subMenuItem.create(submenu);
			}
		}
		else 
			menuItem = new MenuItem(parent, style);

	
		menuItem.setText(label);

		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				triggerEvent();
				menuItem.getParent().getShell().dispose();
			}
		});

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
}
