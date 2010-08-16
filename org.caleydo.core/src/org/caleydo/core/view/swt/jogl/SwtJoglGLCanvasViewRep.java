package org.caleydo.core.view.swt.jogl;

import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.IViewManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.serialize.SerializedDummyView;
import org.caleydo.core.view.opengl.canvas.GLCaleydoCanvas;
import org.caleydo.core.view.swt.ASWTView;
import org.caleydo.core.view.swt.ISWTView;
import org.caleydo.core.view.swt.widget.SWTEmbeddedJoglWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public class SwtJoglGLCanvasViewRep
	extends ASWTView
	implements ISWTView {
	protected int iGLCanvasID;

	protected GLCaleydoCanvas gLCanvas;

	/**
	 * Constructor.
	 */
	public SwtJoglGLCanvasViewRep(int iParentContainerId, String sLabel) {
		super(iParentContainerId, sLabel, GeneralManager.get().getIDManager().createID(
			EManagedObjectType.VIEW_SWT_JOGL_CONTAINER));
	}

	@Override
	public void initViewSWTComposite(Composite parentComposite) {
		ISWTGUIManager iSWTGUIManager = generalManager.getSWTGUIManager();

		SWTEmbeddedJoglWidget sWTEmbeddedJoglWidget =
			(SWTEmbeddedJoglWidget) iSWTGUIManager.createWidget(
				EManagedObjectType.GUI_SWT_EMBEDDED_JOGL_WIDGET, parentContainerID);

		parentComposite = sWTEmbeddedJoglWidget.getParentComposite();

		sWTEmbeddedJoglWidget.createEmbeddedComposite();

		gLCanvas = sWTEmbeddedJoglWidget.getGLCanvas();
		iGLCanvasID = gLCanvas.getID();

		IViewManager canvasManager = generalManager.getViewGLCanvasManager();

		// Register GL canvas to view manager
		canvasManager.registerGLCanvas(gLCanvas);
	}

	@Override
	public final void initView() {
	}

	@Override
	public void drawView() {
	}

	public int getGLCanvasID() {
		return iGLCanvasID;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		SerializedDummyView serializedForm = new SerializedDummyView();
		serializedForm.setViewID(this.getID());
		return serializedForm;
	}

	@Override
	public void initFromSerializableRepresentation(ASerializedView ser) {
		// this implementation does not initialize anything yet
	}
}
