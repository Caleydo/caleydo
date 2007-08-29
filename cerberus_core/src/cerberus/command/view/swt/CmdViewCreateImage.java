package cerberus.command.view.swt;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.parser.parameter.IParameterHandler;
import cerberus.util.exception.GeneViewRuntimeException;
import cerberus.view.swt.image.ImageViewRep;

/**
 * Class implementes the command for importing
 * an existing image.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public class CmdViewCreateImage 
extends ACmdCreate_IdTargetLabelParentXY 
implements ICommand {
	
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
	public void doCommand() throws GeneViewRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		ImageViewRep imageView = (ImageViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_IMAGE,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel,
							iGLCanvasId,
							iGLEventListernerId);
		
		viewManager.registerItem(
				imageView, 
				iUniqueTargetId, 
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
	
	public void undoCommand() throws GeneViewRuntimeException {

		refCommandManager.runUndoCommand(this);
	}
}
