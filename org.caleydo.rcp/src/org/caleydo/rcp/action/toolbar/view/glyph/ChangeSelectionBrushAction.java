package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

public class ChangeSelectionBrushAction
	extends AToolBarAction
	implements IMenuCreator, IToolBarItem {
	
	public static final String TEXT_SELECTIONBRUSH_DISABLE = "disable selection brush";
	public static final String TEXT_SELECTIONBRUSH_SELECT_ALL = "select all";
	public static final String TEXT_SELECTIONBRUSH_DESELECT_ALL = "deselect all";

	public static final String ICON_SELECTIONBRUSH_DISABLE =
		"resources/icons/view/glyph/selection_brush_hide.png";
	public static final String ICON_SELECTIONBRUSH_SELECT_ALL =
		"resources/icons/view/glyph/selection_brush_all.png";
	public static final String ICON_SELECTIONBRUSH_DESELECT_ALL =
		"resources/icons/view/glyph/selection_brush_none.png";

	public static final String TEXT_SELECTIONBRUSH_01 = "1";
	public static final String TEXT_SELECTIONBRUSH_02 = "2";
	public static final String TEXT_SELECTIONBRUSH_03 = "3";
	public static final String TEXT_SELECTIONBRUSH_04 = "4";

	public static final String ICON_SELECTIONBRUSH_01 = "resources/icons/view/glyph/selection_brush_01.png";
	public static final String ICON_SELECTIONBRUSH_02 = "resources/icons/view/glyph/selection_brush_02.png";
	public static final String ICON_SELECTIONBRUSH_03 = "resources/icons/view/glyph/selection_brush_03.png";
	public static final String ICON_SELECTIONBRUSH_04 = "resources/icons/view/glyph/selection_brush_04.png";

	private Menu menu;
	private int iViewID;

	/**
	 * Constructor.
	 */
	public ChangeSelectionBrushAction(int iViewID) {
		super(iViewID);
		this.iViewID = iViewID;

		setText(TEXT_SELECTIONBRUSH_DISABLE);
		setToolTipText(TEXT_SELECTIONBRUSH_DISABLE);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON_SELECTIONBRUSH_DISABLE)));

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
		if (menu != null) {
			menu.dispose();
		}

		menu = new Menu(parent);

		addActionToMenu(menu, new ChangeSelectionBrush(iViewID, this, 0, TEXT_SELECTIONBRUSH_DISABLE,
			ICON_SELECTIONBRUSH_DISABLE));

		addActionToMenu(menu, new ChangeSelectionBrush(iViewID, this, -1, TEXT_SELECTIONBRUSH_SELECT_ALL,
			ICON_SELECTIONBRUSH_SELECT_ALL));
		addActionToMenu(menu, new ChangeSelectionBrush(iViewID, this, -2, TEXT_SELECTIONBRUSH_DESELECT_ALL,
			ICON_SELECTIONBRUSH_DESELECT_ALL));

		addActionToMenu(menu, new ChangeSelectionBrush(iViewID, this, 1, TEXT_SELECTIONBRUSH_01,
			ICON_SELECTIONBRUSH_01));
		addActionToMenu(menu, new ChangeSelectionBrush(iViewID, this, 2, TEXT_SELECTIONBRUSH_02,
			ICON_SELECTIONBRUSH_02));
		addActionToMenu(menu, new ChangeSelectionBrush(iViewID, this, 3, TEXT_SELECTIONBRUSH_03,
			ICON_SELECTIONBRUSH_03));
		addActionToMenu(menu, new ChangeSelectionBrush(iViewID, this, 4, TEXT_SELECTIONBRUSH_04,
			ICON_SELECTIONBRUSH_04));

		return menu;
	}

	/**
	 * This is called in a menu bar
	 */
	public Menu getMenu(Menu parent) {
		return null;
	}

	protected void addActionToMenu(Menu parent, IAction action) {
		ActionContributionItem item = new ActionContributionItem(action);
		item.fill(parent, -1);
	}
}
