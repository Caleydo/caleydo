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

public class ChangeViewModeSecondaryAction
	extends AToolBarAction
	implements IMenuCreator, IToolBarItem {
	public static final String TEXT = "disabled";
	public static final String TEXT_SCATTERPLOT = "Scatterplot Axis Definition";
	public static final String TEXT_PLUSMODEL = "Distribution Model Axis Definition";

	// public static final String ICON = ChangeViewModeToScatterplotAction.ICON;
	public static final String ICON = "resources/icons/view/glyph/xy_mapping.png";

	private Menu menu;
	private int iViewID;
	private AToolBarAction primaryAction;

	/**
	 * Constructor.
	 */
	public ChangeViewModeSecondaryAction(int iViewID) {
		super(iViewID);
		this.iViewID = iViewID;

		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));

		setMenuCreator(this);
	}

	public void setAction(AToolBarAction action) {
		primaryAction = action;

		if (action instanceof ChangeViewModeToScatterplotAction) {
			setToolTipText(TEXT_SCATTERPLOT);

			// setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
			// PlatformUI.getWorkbench().getDisplay(),
			// ChangeViewModeToScatterplotAction.ICON)));

			this.setEnabled(true);
		}
		else if (primaryAction instanceof ChangeViewModeToPlusModelAction) {
			setToolTipText(TEXT_PLUSMODEL);

			// setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
			// PlatformUI.getWorkbench().getDisplay(),
			// ChangeViewModeToPlusModelAction.ICON)));

			this.setEnabled(true);
		}
		else {
			this.setEnabled(false);
		}
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

		if (primaryAction instanceof ChangeViewModeToScatterplotAction) {
			addActionToMenu(menu, new ChangeViewModeSecondaryScatterplotAxisAction(iViewID, 0));
			addActionToMenu(menu, new ChangeViewModeSecondaryScatterplotAxisAction(iViewID, 1));
		}
		else if (primaryAction instanceof ChangeViewModeToPlusModelAction) {
			addActionToMenu(menu, new ChangeViewModeSecondaryPlusModelAxisAction(iViewID, 0));
			addActionToMenu(menu, new ChangeViewModeSecondaryPlusModelAxisAction(iViewID, 1));
		}
		else {
		}

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
