package org.geneview.core.command.view.swt;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.IViewManager;
import org.geneview.core.manager.type.ManagerObjectType;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.util.exception.GeneViewRuntimeException;
import org.geneview.core.view.swt.mixer.MixerViewRep;

/**
 * Class implementes the command for creating a mixer view.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateMixer 
extends ACmdCreate_IdTargetLabelParentXY {
	
	int iNumberOfSliders = 1;
	
	/**
	 * Constructor.
	 * 
	 */
	public CmdViewCreateMixer(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType) {
		
		super(refGeneralManager, 
				refCommandManager,
				refCommandQueueSaxType);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		MixerViewRep mixerView = (MixerViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_MIXER,
						iUniqueId, 
						iParentContainerId, 
						iGlForwarderId,
						sLabel);
		
		viewManager.registerItem(
				mixerView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		mixerView.setAttributes(iWidthX, iHeightY, iNumberOfSliders);
		mixerView.initView();
		mixerView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	

		refParameterHandler.setValueAndTypeAndDefault("iNumberOfSliders",
				refParameterHandler.getValueString(CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey()),
				IParameterHandler.ParameterHandlerType.INT, "-1");
		
		iNumberOfSliders = refParameterHandler.getValueInt("iNumberOfSliders");
	}
	
	public void undoCommand() throws GeneViewRuntimeException {
		
		refCommandManager.runUndoCommand(this);
	}
}
