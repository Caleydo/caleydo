package org.caleydo.rcp.action.toolbar.view.pathway;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.view.swt.toolbar.content.pathway.PathwayToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class TextureAction
	extends Action
	implements IToolBarItem {
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
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		texturesEnabled = !texturesEnabled;
		if (texturesEnabled) {
			pathwayToolbarMediator.enableTextures();
		}
		else {
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
