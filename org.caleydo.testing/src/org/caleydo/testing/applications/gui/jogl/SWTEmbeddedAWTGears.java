/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *  
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *  
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.testing.applications.gui.jogl;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.media.opengl.GLCanvas;
import org.caleydo.testing.applications.gui.jogl.gears.Gears;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import com.sun.opengl.util.Animator;

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
