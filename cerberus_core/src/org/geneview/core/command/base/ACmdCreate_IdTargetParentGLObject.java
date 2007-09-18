/**
 * 
 */
package org.geneview.core.command.base;

import gleem.linalg.Rotf;
import gleem.linalg.Vec3f;
import gleem.linalg.Vec4f;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.math.MathUtil;
import org.geneview.core.parser.parameter.IParameterHandler;
import org.geneview.core.parser.parameter.IParameterHandler.ParameterHandlerType;



/**
 * @see org.geneview.core.command.ICommand
 * 
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetParentGLObject 
extends	ACmdCreate_IdTargetLabelParent {

	protected Vec3f cameraOrigin;
	
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
		
		String sPositionGLOrigin = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		
		String sPositionGLRotation = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		
		sAttribute3 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE3.getXmlKey() );
		
		sAttribute4 = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_ATTRIBUTE4.getXmlKey() );
		
		sDetail = refParameterHandler.getValueString( 
				CommandQueueSaxType.TAG_DETAIL.getXmlKey() );
		
		/* convert values.. */
		if ( sPositionGLOrigin != null ) 
		{
			refParameterHandler.setValueAndTypeAndDefault( 
					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey(),
					sPositionGLOrigin, 
					ParameterHandlerType.VEC3F,
					CommandQueueSaxType.TAG_POS_GL_ORIGIN.getDefault() );
		}
		
		if ( sPositionGLRotation != null ) 
		{
			refParameterHandler.setValueAndTypeAndDefault( 
					CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey(),
					sPositionGLRotation, 
					ParameterHandlerType.VEC4F,
					CommandQueueSaxType.TAG_POS_GL_ROTATION.getDefault() );
		}
		
		cameraOrigin = refParameterHandler.getValueVec3f( 
				CommandQueueSaxType.TAG_POS_GL_ORIGIN.getXmlKey() );
		
		/* convert Vec4f to roation Rotf */
		Vec4f vec4fRotation = refParameterHandler.getValueVec4f( 
				CommandQueueSaxType.TAG_POS_GL_ROTATION.getXmlKey() );
		
		cameraRotation.set( new Vec3f(vec4fRotation.x(),vec4fRotation.y(),vec4fRotation.z()),
				MathUtil.grad2radiant(vec4fRotation.w()));
	}

}
