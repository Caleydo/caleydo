package org.caleydo.view.pathway.toolbar.actions;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.view.pathway.toolbar.PathwayToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class TextureAction extends Action implements IToolBarItem {
	public static final String TEXT = "Turn on/off pathway textures";
	public static final String ICON = "resources/icons/view/pathway/texture_on_off.png";

	/** status of the pathway textures, true = enabled, false = disabled */
	private boolean texturesEnabled = true;

	/** mediator to handle actions triggered by instances of this class */
	private PathwayToolBarMediator pathwayToolbarMediator;

	/**
	 * Constructor.
	 */
	public TextureAction(PathwayToolBarMediator mediator) {
		pathwayToolbarMediator = mediator;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(GeneralManager.get()
				.getResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		texturesEnabled = !texturesEnabled;
		if (texturesEnabled) {
			pathwayToolbarMediator.enableTextures();
		} else {
			pathwayToolbarMediator.disableTextures();
		}
	}

	public boolean isTexturesEnabled() {
		return texturesEnabled;
	}

	public void setTexturesEnabled(boolean texturesEnabled) {
		this.texturesEnabled = texturesEnabled;
		super.setChecked(texturesEnabled);
	};
}
