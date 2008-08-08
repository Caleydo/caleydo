package org.caleydo.core.view.swt.jogl;

import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewGLCanvasManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.swt.widget.SWTEmbeddedJoglWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class SwtJoglGLCanvasViewRep
	extends AView
{
	protected int iGLCanvasID;

	protected GLCaleydoCanvas gLCanvas;

	/**
	 * Constructor.
	 * 
	 */
	public SwtJoglGLCanvasViewRep(int iParentContainerId, String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_JOGL);
	}

	public void initViewSwtComposite(Composite swtContainer)
	{

		ISWTGUIManager iSWTGUIManager = generalManager.getSWTGUIManager();

		SWTEmbeddedJoglWidget sWTEmbeddedJoglWidget = (SWTEmbeddedJoglWidget) iSWTGUIManager
				.createWidget(EManagedObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET,
						iParentContainerId, -1, -1);

		swtContainer = sWTEmbeddedJoglWidget.getParentComposite();

		sWTEmbeddedJoglWidget.createEmbeddedComposite();

		gLCanvas = sWTEmbeddedJoglWidget.getGLCanvas();
		iGLCanvasID = gLCanvas.getID();
		
		IViewGLCanvasManager canvasManager = generalManager.getViewGLCanvasManager();

		// Register GL canvas to view manager
		canvasManager.registerGLCanvas(gLCanvas);
	}

	public final void initView()
	{

		assert false : "Do not call this method! Call SwtJoglGLCanvasViewRep.initViewSwtComposite()";
	}

	public void destroyDirector()
	{

		//		
		// generalManager.logMsg("SwtJoglCanvasViewRep.destroyDirector()  id=" +
		// iUniqueID,
		// LoggerType.STATUS );
	}

	public void setAttributes(int iWidth, int iHeight)
	{
		super.setAttributes(iWidth, iHeight);
	}

	public void drawView()
	{
	}
	
	public int getGLCanvasID()
	{
		return iGLCanvasID;
	}
}
