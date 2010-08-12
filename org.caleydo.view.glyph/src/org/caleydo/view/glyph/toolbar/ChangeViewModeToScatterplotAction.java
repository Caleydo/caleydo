package org.caleydo.rcp.view.toolbar.action.glyph;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ChangeViewModeToScatterplotAction
	extends AToolBarAction {
	public static final String TEXT = "Switch View To Scatterplot";
	public static final String ICON = "resources/icons/view/glyph/sort_scatterplot.png";

	private ChangeViewModeAction parent;

	/**
	 * Constructor.
	 */
	public ChangeViewModeToScatterplotAction(int iViewID, ChangeViewModeAction parent) {
		super(iViewID);
		this.parent = parent;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));

	}

	@Override
	public void run() {
		super.run();

		if (parent != null) {
			parent.setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		}

		parent.getSecondaryAction().setAction(this);

		GeneralManager.get().getEventPublisher().triggerEvent(
			new SetPositionModelEvent(iViewID, EPositionModel.DISPLAY_SCATTERPLOT));
	};

}
