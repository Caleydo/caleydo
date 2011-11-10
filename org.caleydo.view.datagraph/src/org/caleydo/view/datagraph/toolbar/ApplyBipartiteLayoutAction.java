package org.caleydo.view.datagraph.toolbar;

import org.caleydo.core.gui.toolbar.IToolBarItem;
import org.caleydo.core.gui.toolbar.action.AToolBarAction;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.datagraph.event.ApplySpecificGraphLayoutEvent;
import org.caleydo.view.datagraph.layout.BipartiteGraphLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class ApplyBipartiteLayoutAction extends AToolBarAction implements IToolBarItem {

	public static final String TEXT = "Apply Bipartite Layout";
	public static final String ICON = "resources/icons/view/radial/radial_color_mapping.png";

	public ApplyBipartiteLayoutAction() {
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(
				PlatformUI.getWorkbench().getDisplay(), ICON)));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();

		ApplySpecificGraphLayoutEvent event = new ApplySpecificGraphLayoutEvent();
		event.setGraphLayoutClass(BipartiteGraphLayout.class);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
		setChecked(false);
	};

}
