package org.geneview.core.command.base;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.parser.parameter.IParameterHandler;


/**
 * @author Michael Kalkusch
 * @author Marc Streit
 *
 */
public abstract class ACmdCreate_IdTargetLabelParentAttrOpenGL
extends ACmdCreate_IdTargetLabelParentXY {

	 
	/**
	 * GLEventListener Id used for OpenGL
	 *  
	 */
	protected int iGLCanvasID = 0;
	
	/**
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	public ACmdCreate_IdTargetLabelParentAttrOpenGL(IGeneralManager refGeneralManager,
			ICommandManager refCommandManager,
			CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
				
		iGLCanvasID = refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() );
				
		checkOpenGLSetting();
	}

	/**
	 * Overwrite this in derived classes if OpenGL settings are not required.
	 */
	protected void checkOpenGLSetting() {
		
		if ( iGLCanvasID < 1) {
			this.generalManager.getSingelton().logMsg(" tag [" + 
					CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() +
					"] is not assinged!",
					LoggerType.MINOR_ERROR_XML);
		}
	}
}
