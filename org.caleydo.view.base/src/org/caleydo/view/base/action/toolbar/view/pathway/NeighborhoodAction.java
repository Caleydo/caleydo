package org.caleydo.view.base.action.toolbar.view.pathway;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.base.swt.toolbar.content.pathway.PathwayToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class NeighborhoodAction
	extends Action {
	public static final String TEXT = "Turn on/off neighborhood";
	public static final String ICON = "resources/icons/view/pathway/neighborhood.png";

	private boolean neighborhoodEnabled = false;

	/** mediator to handle actions triggered by instances of this class */
	private PathwayToolBarMediator pathwayToolbarMediator;

	/**
	 * Constructor.
	 */
	public NeighborhoodAction(PathwayToolBarMediator mediator) {
		pathwayToolbarMediator = mediator;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		setChecked(neighborhoodEnabled);
	}

	@Override
	public void run() {
		super.run();

		neighborhoodEnabled = !neighborhoodEnabled;
		if (neighborhoodEnabled) {
			pathwayToolbarMediator.enableNeighborhood();
		}
		else {
			pathwayToolbarMediator.disableNeighborhood();
		}
	}

	public boolean isNeighborhoodEnabled() {
		return neighborhoodEnabled;
	}

	public void setNeighborhoodEnabled(boolean neighborhoodEnabled) {
		this.neighborhoodEnabled = neighborhoodEnabled;
		super.setChecked(neighborhoodEnabled);
	};

}
