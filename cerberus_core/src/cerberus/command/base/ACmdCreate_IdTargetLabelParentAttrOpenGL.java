/**
 * 
 */
package cerberus.command.base;

import cerberus.command.CommandQueueSaxType;
import cerberus.manager.ICommandManager;
import cerberus.manager.IGeneralManager;
import cerberus.manager.ILoggerManager.LoggerType;
import cerberus.parser.parameter.IParameterHandler;


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
		if ( iGLCanvasId < 1) {
			this.refGeneralManager.getSingelton().logMsg(" tag [" + 
					CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() +
					"] is not assinged!",
					LoggerType.MINOR_ERROR_XML);
		}
		
		if ( iGLEventListernerId < 1) {
			this.refGeneralManager.getSingelton().logMsg(" tag [" + 
					CommandQueueSaxType.TAG_GLCANVAS_LISTENER.getXmlKey() +
					"] is not assinged!",
					LoggerType.MINOR_ERROR_XML);
		}
	}
	
	public void setParameterHandler( final IParameterHandler refParameterHandler ) {
		
		super.setParameterHandler(refParameterHandler);
		
		iGLCanvasId = refParameterHandler.getValueInt(
				CommandQueueSaxType.TAG_GLCANVAS.getXmlKey() );
				
		iGLEventListernerId = refParameterHandler.getValueInt( 
				CommandQueueSaxType.TAG_GLCANVAS_LISTENER.getXmlKey() );
				
		checkOpenGLSetting();
	}

}
