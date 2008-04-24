package org.caleydo.core.view.swt.jogl;

import javax.media.opengl.GLCanvas;

import org.eclipse.swt.widgets.Composite;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.view.AViewRep;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.widget.SWTEmbeddedJoglWidget;


/**
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 *
 */
public class SwtJoglGLCanvasViewRep 
extends AViewRep {
	
	protected int iGLCanvasID;
	
	protected GLCanvas gLCanvas;
	
	/**
	 * Constructor.
	 * 
	 * @param refGeneralManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param type
	 */
	public SwtJoglGLCanvasViewRep(final IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId,
			int iGLCanvasID,
			String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId,
				sLabel, 
				ViewType.SWT_JOGL_VIEW );
						
		this.iGLCanvasID = iGLCanvasID;
	}
	
	public void initViewSwtComposit(Composite swtContainer) {
			
		ISWTGUIManager refISWTGUIManager = generalManager.getSingleton().getSWTGUIManager();
		
		SWTEmbeddedJoglWidget refSWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget) refISWTGUIManager.createWidget(
						ManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
						iParentContainerId, -1, -1);
				
		refSWTContainer = refSWTEmbeddedJoglWidget.getParentComposite();
	
		refSWTEmbeddedJoglWidget.createEmbeddedComposite(generalManager, iGLCanvasID);

		gLCanvas = refSWTEmbeddedJoglWidget.getGLCanvas();
		
		assert gLCanvas != null : "GLCanvas was not be created";
		
		// Add canvas as listener to itself so that init(), display() etc are called.
		//gLCanvas.addGLEventListener((GLEventListener)gLCanvas);
		
		IViewGLCanvasManager canvasManager = 
			generalManager.getSingleton().getViewGLCanvasManager();
		
		// Register GL canvas to view manager
		canvasManager.registerGLCanvas(gLCanvas, iGLCanvasID);
	}
	
	public final void initView() {
		assert false : "Do not call this method! Call SwtJoglGLCanvasViewRep.initViewSwtComposite()";
	}
	

	public void destroyDirector() {
		
		generalManager.getSingleton().logMsg("SwtJoglCanvasViewRep.destroyDirector()  id=" +
				iUniqueId,
				LoggerType.STATUS );
	}

	
	public void setAttributes(int iWidth, int iHeight, int iGLCanvasID) {
		
		super.setAttributes(iWidth, iHeight);
		
		if ( iGLCanvasID != -1 ) 
		{
			this.iGLCanvasID = iGLCanvasID;
		}
	}
	
	public void drawView() {
		 generalManager.getSingleton().logMsg(
					"SwtJoglGLCanvasViewRep.drawView() [" + 
					this.iUniqueId + "]"
					,LoggerType.VERBOSE );
	}
}
