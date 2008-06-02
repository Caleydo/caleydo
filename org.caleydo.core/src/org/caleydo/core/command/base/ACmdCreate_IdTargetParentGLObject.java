package org.caleydo.core.command.base;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import org.caleydo.core.command.CommandQueueSaxType;
import org.caleydo.core.manager.ICommandManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.math.MathUtil;
import org.caleydo.core.parser.parameter.IParameterHandler;
import org.caleydo.core.parser.parameter.IParameterHandler.ParameterHandlerType;

/**
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class ACmdCreate_IdTargetParentGLObject 
extends	ACmdCreate_IdTargetLabelParent {

	protected Vec3f cameraOrigin;
	
	protected Rotf cameraRotation;
	
	protected String sDetail;

	
	public ACmdCreate_IdTargetParentGLObject(
			final IGeneralManager generalManager,
			final ICommandManager commandManager,
			final CommandQueueSaxType commandQueueSaxType)
	{
		super(generalManager,
				commandManager,
				commandQueueSaxType);
		
		cameraRotation = new Rotf();
		cameraOrigin = new Vec3f(0,0,0);
	}
	
	public void setParameterHandler( final IParameterHandler parameterHandler ) {
		
		super.setParameterHandler(parameterHandler);
		
		String sPositionGLOrigin = parameterHandler.getValueString( 
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		
		String sPositionGLRotation = parameterHandler.getValueString( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		
		sAttribute3 = parameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
		
		sAttribute4 = parameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE4.getXmlKey() );
		
		sDetail = parameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		
		/* convert values.. */
		if ( sPositionGLOrigin != null ) 
		{
			parameterHandler.setValueAndTypeAndDefault( 
					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey(),
					sPositionGLOrigin, 
					ParameterHandlerType.VEC3F,
					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getDefault() );
		}
		
		if ( sPositionGLRotation != null ) 
		{
			parameterHandler.setValueAndTypeAndDefault( 
					CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey(),
					sPositionGLRotation, 
					ParameterHandlerType.VEC4F,
					CommandQueueSaxType.TAG_POS_GL_ROTATION.getDefault() );
		}
		
		cameraOrigin = parameterHandler.getValueVec3f( 
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		
		/* convert Vec4f to roation Rotf */
		Vec4f vec4fRotation = parameterHandler.getValueVec4f( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		
		cameraRotation.set( new Vec3f(vec4fRotation.x(),vec4fRotation.y(),vec4fRotation.z()),
				MathUtil.grad2radiant(vec4fRotation.w()));
	}

}
