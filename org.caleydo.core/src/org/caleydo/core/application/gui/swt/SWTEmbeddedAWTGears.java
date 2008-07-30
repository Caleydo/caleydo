package org.caleydo.core.application.gui.swt;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCanvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.sun.opengl.util.Animator;

import demos.gears.Gears;

public class SWTEmbeddedAWTGears
{

	public static void main(String args[])
	{

		// Creating the display
		// Instances of this class are responsible for managing the connection
		// between SWT and the underlying operating system.
		Display display = new Display();

		// Creating the shell
		// Instances of this class represent the "windows" which the desktop or
		// "window manager" is managing.
		Shell shell = new Shell(display);
		shell.setLayout(new GridLayout());
		shell.setSize(800, 600);

		Composite composite = new Composite(shell, SWT.EMBEDDED);
		Frame frame = SWT_AWT.new_Frame(composite);

		Button ok = new Button(composite, SWT.PUSH);
		ok.setText("OK");
		ok.setSize(300, 300);

		GLCanvas canvas = new GLCanvas();

		canvas.addGLEventListener(new Gears());
		frame.add(canvas);
		frame.setSize(300, 300);
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

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
