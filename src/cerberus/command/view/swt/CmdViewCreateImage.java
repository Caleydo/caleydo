package cerberus.command.view.swt;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParentXY;
import cerberus.manager.IGeneralManager;
import cerberus.manager.IViewManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.image.ImageViewRep;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

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
	
	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param listAttributes List of attributes
	 */
	public CmdViewCreateImage(
			final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler) {
		
		super(refGeneralManager, refParameterHandler);
		
		setAttributes(refParameterHandler);
	}

	/**
	 * Method creates a slider view, sets the attributes 
	 * and calls the init and draw method.
	 */
	public void doCommand() throws CerberusRuntimeException {
		
		IViewManager viewManager = ((IViewManager) refGeneralManager
				.getManagerByBaseType(ManagerObjectType.VIEW));
		
		ImageViewRep imageView = (ImageViewRep)viewManager
				.createView(ManagerObjectType.VIEW_SWT_IMAGE,
							iUniqueTargetId, 
							iParentContainerId, 
							sLabel);
		
		viewManager.registerItem(
				imageView, 
				iUniqueTargetId, 
				ManagerObjectType.VIEW);

		imageView.readInAttributes(refParameterHandler);	
		imageView.extractAttributes();
		imageView.retrieveGUIContainer();
		imageView.initView();
		imageView.drawView();
	}

	protected void setAttributes( final IParameterHandler refParameterHandler ) {
	
		refParameterHandler.setValueAndTypeAndDefault("sImagePath",
				refParameterHandler.getValueString( 
						CommandQueueSaxType.TAG_DETAIL.getXmlKey() ),
				IParameterHandler.ParameterHandlerType.STRING,
				"");
	}
	
	public void undoCommand() throws CerberusRuntimeException {

		// TODO Auto-generated method stub
	}
}
