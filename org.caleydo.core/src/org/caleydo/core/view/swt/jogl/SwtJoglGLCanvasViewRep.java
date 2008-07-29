package org.caleydo.core.view.swt.jogl;

import javax.media.opengl.GLCanvas;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.widget.SWTEmbeddedJoglWidget;
import org.eclipse.swt.widgets.Composite;


/**
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 * 
 *
 */
public class SwtJoglGLCanvasViewRep 
extends AView {
	
	protected int iGLCanvasID;
	
	protected GLCanvas gLCanvas;
	
	/**
	 * Constructor.
	 * 
	 * @param generalManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param iGLCanvasID
	 * @param sLabel
	 * @param type
	 */
	public SwtJoglGLCanvasViewRep(final IGeneralManager generalManager, 
			int iViewId, 
			int iParentContainerId,
			int iGLCanvasID,
			String sLabel) {
		
		super(generalManager, 
				iViewId, 
				iParentContainerId,
				sLabel, 
				ViewType.SWT_JOGL_VIEW );
						
		this.iGLCanvasID = iGLCanvasID;
	}
	
	public void initViewSwtComposit(Composite swtContainer) {
			
		ISWTGUIManager iSWTGUIManager = generalManager.getSWTGUIManager();
		
		SWTEmbeddedJoglWidget sWTEmbeddedJoglWidget = 
			(SWTEmbeddedJoglWidget) iSWTGUIManager.createWidget(
						EManagerObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, 
						iParentContainerId, -1, -1);
				
		swtContainer = sWTEmbeddedJoglWidget.getParentComposite();
	
		sWTEmbeddedJoglWidget.createEmbeddedComposite(generalManager, iGLCanvasID);

		gLCanvas = sWTEmbeddedJoglWidget.getGLCanvas();
		
		assert gLCanvas != null : "GLCanvas was not be created";
		
		// Add canvas as listener to itself so that init(), display() etc are called.
		//gLCanvas.addGLEventListener((GLEventListener)gLCanvas);
		
		IViewGLCanvasManager canvasManager = 
			generalManager.getViewGLCanvasManager();
		
		// Register GL canvas to view manager
		canvasManager.registerGLCanvas(gLCanvas, iGLCanvasID);
	}
	
	public final void initView() {
		assert false : "Do not call this method! Call SwtJoglGLCanvasViewRep.initViewSwtComposite()";
	}
	

	public void destroyDirector() {
//		
//		generalManager.logMsg("SwtJoglCanvasViewRep.destroyDirector()  id=" +
//				iUniqueId,
//				LoggerType.STATUS );
	}

	
	public void setAttributes(int iWidth, int iHeight, int iGLCanvasID) {
		
		super.setAttributes(iWidth, iHeight);
		
		if ( iGLCanvasID != -1 ) 
		{
			this.iGLCanvasID = iGLCanvasID;
		}
	}
	
	public void drawView() {
//		 generalManager.logMsg(
//					"SwtJoglGLCanvasViewRep.drawView() [" + 
//					this.iUniqueId + "]"
//					,LoggerType.VERBOSE );
	}
}
