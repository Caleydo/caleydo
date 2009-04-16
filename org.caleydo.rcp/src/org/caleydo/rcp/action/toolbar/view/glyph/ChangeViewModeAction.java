package org.caleydo.rcp.action.toolbar.view.glyph;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EIconIDs;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.PlatformUI;

public class ChangeViewModeAction
	extends AToolBarAction
	implements IMenuCreator, IToolBarItem {

	private Menu menu;
	private int iViewID;
	private GLGlyph glyphview;
	private ChangeViewModeSecondaryAction cvm2a;

	/**
	 * Constructor.
	 * 
	 * @param cvm2a
	 */
	public ChangeViewModeAction(int iViewID, ChangeViewModeSecondaryAction cvm2a) {
		super(iViewID);
		this.cvm2a = cvm2a;
		setViewID(iViewID);

		EIconIDs type = EIconIDs.DISPLAY_RECTANGLE;

		String usedText = ChangeViewModeToRectangleAction.TEXT;
		String usedImage = ChangeViewModeToRectangleAction.ICON;

		if (glyphview != null) {
			type = glyphview.getPositionModel();
		}

		if (type == EIconIDs.DISPLAY_CIRCLE) {
			usedText = ChangeViewModeToCircleAction.TEXT;
			usedImage = ChangeViewModeToCircleAction.ICON;
		}
		if (type == EIconIDs.DISPLAY_PLUS) {
			usedText = ChangeViewModeToPlusModelAction.TEXT;
			usedImage = ChangeViewModeToPlusModelAction.ICON;
			cvm2a.setAction(new ChangeViewModeToPlusModelAction(iViewID, this));
		}
		if (type == EIconIDs.DISPLAY_RANDOM) {
			usedText = ChangeViewModeToRandomAction.TEXT;
			usedImage = ChangeViewModeToRandomAction.ICON;
		}
		if (type == EIconIDs.DISPLAY_SCATTERPLOT) {
			usedText = ChangeViewModeToScatterplotAction.TEXT;
			usedImage = ChangeViewModeToScatterplotAction.ICON;
			cvm2a.setAction(new ChangeViewModeToScatterplotAction(iViewID, this));
		}

		setText(usedText);
		setToolTipText(usedText);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), usedImage)));

		setMenuCreator(this);

	}

	public ChangeViewModeSecondaryAction getSecondaryAction() {
		return cvm2a;
	}

	private void setViewID(int id) {
		this.iViewID = id;
		glyphview = null;
		for (AGLEventListener l : GeneralManager.get().getViewGLCanvasManager().getAllGLEventListeners()) {
			if (l.getID() == iViewID && l instanceof GLGlyph) {
				glyphview = (GLGlyph) l;
			}
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

		addActionToMenu(menu, new ChangeViewModeToCircleAction(iViewID, this));
		addActionToMenu(menu, new ChangeViewModeToPlusModelAction(iViewID, this));
		addActionToMenu(menu, new ChangeViewModeToRandomAction(iViewID, this));
		addActionToMenu(menu, new ChangeViewModeToRectangleAction(iViewID, this));
		addActionToMenu(menu, new ChangeViewModeToScatterplotAction(iViewID, this));

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
