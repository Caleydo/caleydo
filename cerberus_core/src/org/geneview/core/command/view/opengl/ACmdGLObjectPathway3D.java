/**
 * 
 */
package org.geneview.core.command.view.opengl;

import org.geneview.core.command.CommandQueueSaxType;
import org.geneview.core.command.base.ACmdCreate_GlCanvasUser;
import org.geneview.core.manager.ICommandManager;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;


/**
 * @author michael
 *
 */
public abstract class ACmdGLObjectPathway3D extends ACmdCreate_GlCanvasUser {

	protected float fSetTransparencyValue = 1.0f;
	
	
	/**
	 * @param refGeneralManager
	 * @param refCommandManager
	 * @param refCommandQueueSaxType
	 */
	protected ACmdGLObjectPathway3D(IGeneralManager refGeneralManager,
			ICommandManager refCommandManager,
			CommandQueueSaxType refCommandQueueSaxType) {

		super(refGeneralManager, refCommandManager, refCommandQueueSaxType);
	}

	protected void setParameterHandler_DetailsPathway3D() {
		final String sTransparency = "transparency ";
		
		if (sAttribute4.startsWith(sTransparency) ) {
			if (sAttribute4.startsWith("transparency off")) {
				fSetTransparencyValue = 1.0f;
			}
			else 
			{
				try
				{
					fSetTransparencyValue = Float.valueOf( sAttribute4.substring(sTransparency.length()) );
				} catch (NumberFormatException e)
				{
					refGeneralManager.getSingelton().logMsg("Error while parsing XML-file section=\"" +
							CommandQueueSaxType.CREATE_GL_PANEL_PATHWAY_3D.toString() + "\" in \"sAttribute4\" use default transparency " +
							fSetTransparencyValue,
							LoggerType.MINOR_ERROR); 
					e.printStackTrace();
				}
			}
		}
	}

}
