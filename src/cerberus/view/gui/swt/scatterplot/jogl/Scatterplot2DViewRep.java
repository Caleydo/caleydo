package cerberus.view.gui.swt.scatterplot.jogl;

import javax.media.opengl.GLCanvas;

import com.sun.opengl.util.Animator;

import cerberus.manager.IGeneralManager;
import cerberus.view.gui.AViewRep;
import cerberus.view.gui.IView;
import cerberus.view.gui.ViewType;

import demos.gears.Gears;

public class Scatterplot2DViewRep 
extends AViewRep 
implements IView {
	
	protected IGeneralManager refGeneralManager;
	protected GLCanvas refGLCanvas;
	
	public Scatterplot2DViewRep(IGeneralManager refGeneralManager, 
			int iViewId, int iParentContainerId, String sLabel) {
		
		super(refGeneralManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_SCATTERPLOT2D);

		initView();
		drawView();
	}
	
	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.IView#initView()
	 */
	public void initView() {
		
		retrieveGUIContainer();
	}

	/*
	 * (non-Javadoc)
	 * @see cerberus.view.gui.IView#drawView()
	 */
	public void drawView() {
		
	    refGLCanvas.addGLEventListener(new Gears());

	    final Animator animator = new Animator(refGLCanvas);
	    animator.start();
		
	}
}
