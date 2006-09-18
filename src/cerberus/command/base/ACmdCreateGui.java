package cerberus.command.base;

import cerberus.command.ICommand;
import cerberus.command.base.ACmdCreate_IdTargetLabelParent;
import cerberus.manager.IGeneralManager;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;

public abstract class ACmdCreateGui 
extends ACmdCreate_IdTargetLabelParent
implements ICommand
{
	/*  deprecated by iCommandId */
	//protected int iCommandId;
	
	/* deprecated by iTargetId */
	//protected int iCreatedObjectId;

	//protected int iParentContainerId;
	
	//protected String sLabel;

	protected String sAttribute1;
	
	protected String sAttribute2;

	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param listAttributes
	 */
	protected ACmdCreateGui(final IGeneralManager refGeneralManager,
			final IParameterHandler refParameterHandler )
	{
		// set unique ID to -1 because it is unknown at this moment
		super(refGeneralManager, refParameterHandler);

		setAttributes(refParameterHandler);
	}

	/**
	 * Nothing to undo at this time.
	 */
	public final void undoCommand() throws CerberusRuntimeException
	{
		//TODO: fix this bug!
	}


	protected final void setAttributes(final IParameterHandler refParameterHandler)
	{
		super.setAttributesBaseParent( refParameterHandler );
		
		sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
		
	}
}
