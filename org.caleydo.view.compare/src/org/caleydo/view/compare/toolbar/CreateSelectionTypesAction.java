package org.caleydo.view.compare.toolbar;

import org.caleydo.core.manager.event.view.compare.CreateSelectionTypesEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class CreateSelectionTypesAction extends AToolBarAction implements
		IToolBarItem {

	public static final String TEXT = "Brush with colors";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/occlusion_prevention.png";

	private boolean createSelectionTypes = false;

	/**
	 * Constructor.
	 */
	public CreateSelectionTypesAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
		super.setChecked(createSelectionTypes);
	}

	public void setCreateSelectionTypes(boolean createSelectionTypes) {
		this.createSelectionTypes = createSelectionTypes;
	}

	@Override
	public void run() {
		super.run();
		if (createSelectionTypes)
			createSelectionTypes = false;
		else
			createSelectionTypes = true;

		GeneralManager.get().getEventPublisher().triggerEvent(
				new CreateSelectionTypesEvent(createSelectionTypes));
	};

}
