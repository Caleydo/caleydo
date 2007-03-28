/**
 * 
 */
package cerberus.command.base;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import cerberus.command.CommandQueueSaxType;
import cerberus.command.ICommand;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.math.MathUtil;
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
	
	//protected Vec4f vec4fRotation;
	
	protected Rotf cameraRotation;
	
	protected String sDetail;
	
	protected String sAttribute3;
	
	protected String sAttribute4;

	
	public ACmdCreate_IdTargetParentGLObject(
			final IGeneralManager refGeneralManager,
			final ICommandManager refCommandManager,
			final CommandQueueSaxType refCommandQueueSaxType)
	{
		super(refGeneralManager,
				refCommandManager,
				refCommandQueueSaxType);
		
		cameraRotation = new Rotf();
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		String sAttribute1 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE1.getXmlKey() );
		
		String sAttribute2 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE2.getXmlKey() );
		
		sAttribute3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
		
		sAttribute4 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE4.getXmlKey() );
		
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
		
		/* convert Vec4f to roation Rotf */
		Vec4f vec4fRotation = refParameterHandler.getValueVec4f( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		
		cameraRotation.set( new Vec3f(vec4fRotation.x(),vec4fRotation.y(),vec4fRotation.z()),
				MathUtil.grad2radiant(vec4fRotation.w()));
	}

}
