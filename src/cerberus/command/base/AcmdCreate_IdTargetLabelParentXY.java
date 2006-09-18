/**
 * 
 */
package cerberus.command.base;

import java.util.StringTokenizer;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.util.system.StringConversionTool;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * @author java
 *
 */
public abstract class AcmdCreate_IdTargetLabelParentXY 
extends ACmdCreate_IdTargetLabelParent 
implements ICommand
{

	/**
	 * Width of the widget.
	 */
	protected int iWidthX;
	
	/**
	 * Height of the widget;
	 */
	protected int iHeightY;
	
	protected String sAttribute1;
	
	protected String sAttribute2;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	protected AcmdCreate_IdTargetLabelParentXY(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
		setAttributesBaseParentXY( refParameterHandler );
	}
	
	/**
	 * Note: This methode calles setAttributesBase(IParameterHandler) and setAttributesBaseParent(IParameterHandler) internal.
	 * Please do not call methode setAttributesBase(IParameterHandler) after calling this methode.
	 * 
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabel#setAttributesBase(IParameterHandler)
	 * 
	 * @param refParameterHandler
	 */
	protected final IParameterHandler setAttributesBaseParentXY( IParameterHandler refParameterHandler ) {
		
		//super.setAttributesBaseParent( refParameterHandler );
		
		sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
		
		StringTokenizer token = new StringTokenizer(
				sAttribute2,
				CommandFactory.sDelimiter_CreateView_Size);
		
		refParameterHandler.setValueAndTypeAndDefault( 
				CommandQueueSaxType.TAG_POS_WIDTH_X.getXmlKey(),
				token.nextToken(), 
				ParameterHandlerType.INT,
				"-1" );
		
		refParameterHandler.setValueAndTypeAndDefault( 
				CommandQueueSaxType.TAG_POS_HEIGHT_Y.getXmlKey(),
				token.nextToken(), 
				ParameterHandlerType.INT,
				"-1" );
		
		return refParameterHandler;
	}
	
	public final int getWidthX() {
		return this.iWidthX;
	}
	
	public final int getHeightX() {
		return this.iHeightY;
	}

}
