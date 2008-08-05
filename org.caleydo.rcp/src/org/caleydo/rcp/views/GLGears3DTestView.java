package org.caleydo.rcp.views;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import com.sun.opengl.util.Animator;
import demos.gears.Gears;

/**
 * Test OpenGL view that renders GL gears.
 * 
 * @author Marc Streit
 */
public class GLGears3DTestView
	extends AGLViewPart
{

	public static final String ID = "org.caleydo.rcp.views.GLGears3DTestView";

	protected int iGLCanvasDirectorId;

	/**
	 * Constructor.
	 */
	public GLGears3DTestView()
	{
		super();
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent)
	{

		super.createPartControlSWT(parent);

		Composite composite = new Composite(parent, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(composite);

		GLCanvas canvas = new GLCanvas();

		canvas.addGLEventListener(new Gears());
		frame.add(canvas);
		// frame.setSize(300, 300);
		final Animator animator = new Animator(canvas);
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				// Run this on another thread than the AWT event queue to
				// make sure the call to Animator.stop() completes before
				// exiting
				new Thread(new Runnable()
				{
					public void run()
					{
						animator.stop();
						System.exit(0);
					}
				}).start();
			}
		});
		frame.setVisible(true);
		animator.start();
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose()
	{

		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	public void setFocus()
	{

	}
}