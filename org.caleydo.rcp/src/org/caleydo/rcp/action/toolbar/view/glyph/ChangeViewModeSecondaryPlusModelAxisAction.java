package org.caleydo.rcp.action.toolbar.view.glyph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.caleydo.core.manager.event.view.glyph.GlyphUpdatePositionModelEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EPositionModel;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

public class ChangeViewModeSecondaryPlusModelAxisAction
	extends AToolBarAction
	implements IMenuCreator {
	public static final String TEXT = "Switch Scatterplot Axis definition";
	// public static final String ICON =
	// "resources/icons/view/glyph/sort_scatterplot.png";

	private Menu menu;
	private int axisnum;

	/**
	 * Constructor.
	 */
	public ChangeViewModeSecondaryPlusModelAxisAction(int iViewID, int axis) {
		super(iViewID);
		this.axisnum = axis;

		if (axis == 0) {
			setText("left / right");
		}

		if (axis == 1) {
			setText("Y");
		}

		setToolTipText(TEXT);

		setMenuCreator(this);
	}

	@Override
	public void run() {
		super.run();
	}

	@Override
	public void dispose() {
		if (menu != null) {
			menu.dispose();
			menu = null;
		}
	}

	/**
	 * This is called in a tool bar
	 */
	public Menu getMenu(Control parent) {
		return null;
	}

	/**
	 * This is called in a menu bar
	 */
	public Menu getMenu(Menu parent) {
		if (menu != null) {
			menu.dispose();
		}

		menu = new Menu(parent);

		// get all combo box entrys
		final HashMap<String, Integer> list =
			GeneralManager.get().getGlyphManager().getGlyphAttributeComboboxEntryList();

		ArrayList<String> names = new ArrayList<String>(list.keySet());
		Collections.sort(names);

		for (final String name : names) {
			Action axisAction = new Action(name) {
				@Override
				public void run() {
					GeneralManager.get().getEventPublisher().triggerEvent(
						new GlyphUpdatePositionModelEvent(iViewID, EPositionModel.DISPLAY_PLUS, axisnum, list
							.get(name)));
				}
			};
			addActionToMenu(menu, axisAction);
		}

		return menu;
	}

	protected void addActionToMenu(Menu parent, IAction action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}

}
