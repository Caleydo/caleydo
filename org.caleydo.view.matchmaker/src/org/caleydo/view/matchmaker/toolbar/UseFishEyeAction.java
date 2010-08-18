package org.caleydo.view.matchmaker.toolbar;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.view.toolbar.IToolBarItem;
import org.caleydo.rcp.view.toolbar.action.AToolBarAction;
import org.caleydo.view.matchmaker.event.UseFishEyeEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

/**
 * @author Alexander Lex
 * 
 */
public class UseFishEyeAction extends AToolBarAction implements IToolBarItem {
	public static final String TEXT = "Use fish eye";
	public static final String ICON = "resources/icons/view/storagebased/parcoords/angular_brush.png";

	private boolean useFishEye = true;

	/**
	 * Constructor.
	 */
	public UseFishEyeAction(int iViewID) {
		super(iViewID);

		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader()
				.getImage(PlatformUI.getWorkbench().getDisplay(), ICON)));
		super.setChecked(useFishEye);
	}

	public void setUseFishEye(boolean useFishEye) {
		this.useFishEye = useFishEye;
	}

	@Override
	public void run() {
		super.run();
		if (useFishEye)
			useFishEye = false;
		else
			useFishEye = true;

		GeneralManager.get().getEventPublisher().triggerEvent(
				new UseFishEyeEvent(useFishEye));
	};
}
