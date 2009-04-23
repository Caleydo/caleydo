package org.caleydo.rcp.action.toolbar.view.pathway;

import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.views.swt.toolbar.content.IToolBarItem;
import org.caleydo.rcp.views.swt.toolbar.content.pathway.PathwayToolBarMediator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class GeneMappingAction
	extends Action
	implements IToolBarItem {
	public static final String TEXT = "Turn on/off gene mapping";
	public static final String ICON = "resources/icons/view/pathway/gene_mapping.png";

	/** status of the gene mapping, true = enabled, false = disabled */
	private boolean geneMappingEnabled = true;

	/** mediator to handle actions triggered by instances of this class */
	private PathwayToolBarMediator pathwayToolbarMediator;
	
	/**
	 * Constructor.
	 */
	public GeneMappingAction(PathwayToolBarMediator mediator) {
		pathwayToolbarMediator = mediator;

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
	}

	@Override
	public void run() {
		super.run();

		geneMappingEnabled = !geneMappingEnabled;
		if (geneMappingEnabled) {
			pathwayToolbarMediator.enableGeneMapping();
		} else {
			pathwayToolbarMediator.disableGeneMapping();
		}
	}

	public boolean isGeneMappingEnabled() {
		return geneMappingEnabled;
	}

	public void setGeneMappingEnabled(boolean geneMappingEnabled) {
		this.geneMappingEnabled = geneMappingEnabled;
		super.setChecked(geneMappingEnabled);
	};
}
