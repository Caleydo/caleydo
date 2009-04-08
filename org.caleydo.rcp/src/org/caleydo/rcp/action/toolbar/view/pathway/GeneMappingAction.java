package org.caleydo.rcp.action.toolbar.view.pathway;

import org.caleydo.core.command.view.rcp.EExternalFlagSetterType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.AToolBarAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class GeneMappingAction
	extends AToolBarAction {
	public static final String TEXT = "Turn on/off gene mapping";
	public static final String ICON = "resources/icons/view/pathway/gene_mapping.png";

	private boolean bEnable = true;

	/**
	 * Constructor.
	 */
	public GeneMappingAction(int iViewID) {
		super(iViewID);
		setText(TEXT);
		setToolTipText(TEXT);
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), ICON)));
		
		if (GeneralManager.get().getIDMappingManager().hasMapping(EMappingType.REFSEQ_MRNA_INT_2_EXPRESSION_INDEX)) {
			bEnable = true;
		}
		else {
			bEnable = false;
		}
		
		setChecked(bEnable);
	}

	@Override
	public void run() {
		super.run();

		bEnable = !bEnable;

		triggerCmdExternalFlagSetter(bEnable, EExternalFlagSetterType.PATHWAY_GENE_MAPPING);
	};
}
