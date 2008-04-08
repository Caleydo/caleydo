package org.caleydo.core.command.view.swt;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.command.base.ACmdCreate_IdTargetLabelParentXY;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.image.ImageViewRep;

/**
 * Class implementes the command for importing
 * an existing image.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateImage 
extends ACmdCreate_IdTargetLabelParentXY {
	
	String sImagePath = "";
	
	/**
	 * Constructor
	 * 
	 */
	public CmdViewCreateImage(
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
	public void doCommand() throws CaleydoRuntimeException {
		
		IViewManager viewManager = ((IViewManager) generalManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		ImageViewRep imageView = (ImageViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_IMAGE,
							iUniqueId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				imageView, 
				iUniqueId, 
				ManagerObjectType.VIEW);

		imageView.setAttributes(iWidthX, iHeightY, sImagePath);
		imageView.initView();
		imageView.drawView();
		
		refCommandManager.runDoCommand(this);
	}

	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		assert refParameterHandler != null: "ParameterHandler object is null!";	
		
		super.setParameterHandler(refParameterHandler);	

		refParameterHandler.setValueAndTypeAndDefault("sImagePath",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.STRING,
				"");
		
		sImagePath = refParameterHandler.getValueString("sImagePath");
	}
	
	public void undoCommand() throws CaleydoRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
