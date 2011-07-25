package org.caleydo.view.matchmaker.toolbar;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.view.matchmaker.CreateSelectionTypesEvent;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class CreateSelectionTypesAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Brush with colors";
	public static final String ICON = "resources/icons/view/tablebased/parcoords/occlusion_prevention.png";

	private boolean createSelectionTypes = false;

	/**
	 * Constructor.
	 */
	public CreateSelectionTypesAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
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

		GeneralManager.get().getEventPublisher()
				.triggerEvent(new CreateSelectionTypesEvent(createSelectionTypes));
	};

}
