/**
 * 
 */
package cerberus.command.base;

import gleem.linalg.Vec3f;
import gleem.linalg.Rotf;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.IParameterHandler.ParameterHandlerType;



/**
 * @author java
 *
 */
public abstract class ACmdCreate_IdTargetParentGLObject 
extends	ACmdCreate_IdTargetLabelParent 
implements ICommand
{

	protected Vec3f vec3fOrigin;
	
	protected Rotf  rotOrentation;
	
	protected String sDetail;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	public ACmdCreate_IdTargetParentGLObject(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager, refParameterHandler);
		
		setAttributesOpenGL_Object( refParameterHandler );
	}
	
	/**
	 * Note: This methode calles setAttributesBase(IParameterHandler) and setAttributesBaseParent(IParameterHandler) internal.
	 * Please do not call methode setAttributesBase(IParameterHandler) after calling this methode.
	 * 
	 * @see cerberus.command.base.ACmdCreate_IdTargetLabel#setAttributesBase(IParameterHandler)
	 * 
	 * @param refParameterHandler
	 */
	protected final IParameterHandler setAttributesOpenGL_Object( IParameterHandler refParameterHandler ) {
		
		//super.setAttributesBaseParent( refParameterHandler );
		
		String sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		String sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
		
		sDetail = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		
		
		refParameterHandler.setValueAndTypeAndDefault( 
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey(),
				sAttribute1, 
				ParameterHandlerType.VEC3F,
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getDefault() );
		
		refParameterHandler.setValueAndTypeAndDefault( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey(),
				sAttribute2, 
				ParameterHandlerType.VEC4F,
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getDefault() );
		
		return refParameterHandler;
	}
	
	

}
