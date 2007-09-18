/**
 * 
 */
package org.geneview.core.command.base;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
import org.geneview.core.parser.parameter.IParameterHandler;


/**
 * @author Michael Kalkusch
 *
 */
public abstract class ACmdCreate_IdTargetLabelParentAttrOpenGL
		extends ACmdCreate_IdTargetLabelParentXY {

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
	
	/**
	 * Overwrite this in derived classes if OpenGL settings are not required.
	 */
	protected void checkOpenGLSetting() {
		
		if ( iGlForwarderId < 1) {
			this.refGeneralManager.getSingelton().logMsg(" tag [" + 
					CommandQueueSaxType.TAG_GLCANVAS_FORWARDER.getXmlKey() +
					"] is not assinged!",
					LoggerType.MINOR_ERROR_XML);
		}
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
				
		iGlForwarderId = refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_GLCANVAS_FORWARDER.getXmlKey() );
				
		checkOpenGLSetting();
	}

}
