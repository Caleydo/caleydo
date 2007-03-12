package cerberus.view.gui.swt.histogram;

import javax.media.opengl.GLCanvas;

import cerberus.manager.IGeneralManager;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;
import cerberus.view.gui.awt.jogl.Histogram2DMain;

import com.sun.opengl.util.Animator;

/**
 * @deprecated
 *
 */
public class Histogram2DViewRep 
extends AViewRep 
implements IView {

	protected GLCanvas refGLCanvas;

	public Histogram2DViewRep(IGeneralManager refGeneralManager,
			int iViewId,
			int iParentContainerId,
			String sLabel) {

		super(refGeneralManager, iViewId, iParentContainerId, sLabel,
				ViewType.SWT_HISTOGRAM2D);

		initView();
		drawView();
	}

	public void initView() {

		retrieveGUIContainer();
	}

	public void drawView() {

		Histogram2DMain newCanvas = new Histogram2DMain();
		newCanvas.runMain();

		refGLCanvas.addGLEventListener(newCanvas);

		final Animator animator = new Animator(refGLCanvas);
		animator.start();

		//newCanvas.runMain();

	}
}
