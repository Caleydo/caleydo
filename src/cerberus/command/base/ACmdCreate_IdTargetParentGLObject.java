/**
 * 
 */
package cerberus.command.base;

import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import cerberus.command.ICommand;
import cerberus.manager.IGeneralManager;
import cerberus.xml.parser.command.CommandQueueSaxType;
import cerberus.xml.parser.parameter.IParameterHandler;
import cerberus.xml.parser.parameter.IParameterHandler.ParameterHandlerType;



/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetParentGLObject 
extends	ACmdCreate_IdTargetLabelParent 
implements ICommand
{

	protected Vec3f vec3fOrigin;
	
	protected Vec4f vec4fRotation;
	
	protected String sDetail;
	
	protected String sAttribute3;
	
	/**
	 * @param refGeneralManager
	 * @param refParameterHandler
	 */
	public ACmdCreate_IdTargetParentGLObject(IGeneralManager refGeneralManager,
			IParameterHandler refParameterHandler)
	{
		super(refGeneralManager);
		
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		String sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		String sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
		
		sAttribute3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
		
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
		
		
		vec3fOrigin = refParameterHandler.getValueVec3f( 
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		
		vec4fRotation = refParameterHandler.getValueVec4f( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
	}

}
