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
package org.caleydo.core.util.execution;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Shell;
import com.jogamp.opengl.util.FPSAnimator;

/**
 * Provides execution of {@link Runnable}'s within the openGL's display loop. During creation of the singleton
 * reference for this class, the singleton is added to GL's display loop.
 * 
 * @author Werner Puff
 */
public class DisplayLoopExecution
	implements GLEventListener {

	private static final long serialVersionUID = 8184493525384213630L;

	/** singleton reference */
	private static DisplayLoopExecution displayLoopExecution;

	/** {@link GLCanvas} for adding to gl's {@link FPSAnimator} */
	private GLCanvas displayLoopCanvas;

	/** {@link Shell} to add the canvas, otherwise the display method is not called */
	private Shell displayLoopShell;

	/** {@link List} of the {@link Runnable}'s to execute only once */
	private List<Runnable> once;

	/** {@link List} of the {@link Runnable}'s to execute during each display-loop cycle */
	private List<Runnable> multiple;

	/**
	 * Hidden default constructor which creates an instance with empty once- and multiple-exec lists to a GL
	 * display loop.
	 */
	private DisplayLoopExecution() {
		once = new ArrayList<Runnable>();
		multiple = new ArrayList<Runnable>();
	}

	/**
	 * Retrieves the singleton reference of this class. If no singleton exists yet, it will be created.
	 * 
	 * @return
	 */
	public static DisplayLoopExecution get() {
		if (displayLoopExecution == null) {
			displayLoopExecution = new DisplayLoopExecution();

			GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL2));
			glCapabilities.setStencilBits(1);
			displayLoopExecution.displayLoopCanvas = new GLCanvas(glCapabilities);
			displayLoopExecution.displayLoopCanvas.addGLEventListener(displayLoopExecution);

			displayLoopExecution.displayLoopShell = new Shell(SWT.EMBEDDED | SWT.NO_TRIM | SWT.ON_TOP);
			displayLoopExecution.displayLoopShell.setSize(1, 1);
			displayLoopExecution.displayLoopShell.open();

			Region region = new Region();
			displayLoopExecution.displayLoopShell.setRegion(region);
			Frame frame = SWT_AWT.new_Frame(displayLoopExecution.displayLoopShell);
			frame.add(displayLoopExecution.displayLoopCanvas);
		}
		return displayLoopExecution;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// nothing to do here
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		// System.out.println("DisplayLoopExecution(): display() called");
		for (Runnable r : multiple) {
			r.run();
		}
	}

	// @Override
	// public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
	// // nothing to do as there is no related drawing object
	// }

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {
		// nothing to do as there is no related drawing object
	}

	/**
	 * Retrieves the related {@link GLCanvas} of which the {@link GLEventListener}'s display method is used to
	 * execute the contained {@link Runnable}s.
	 * 
	 * @return
	 */
	public GLCanvas getDisplayLoopCanvas() {
		return displayLoopCanvas;
	}

	/**
	 * Queues the given {@link Runnable} for one time execution during the display loop.
	 * 
	 * @param runnable
	 *            {@link Runnable} to execute
	 */
	public void executeOnce(Runnable runnable) {
		once.add(runnable);
	}

	/**
	 * Queues the given {@link Runnable} for execution during the display loop. The {@link Runnable}'s
	 * <code>run()</code> method will be called once during each display loop cycle.
	 * 
	 * @param runnable
	 *            {@link Runnable} to execute
	 */
	public void executeMultiple(Runnable runnable) {
		if (runnable != null) {
			multiple.add(runnable);
		}
		else {
			throw new NullPointerException("the Runnable to execute was null");
		}
	}

	/**
	 * Stops executing the given {@link Runnable} during each display loop cycle. Execution of currently
	 * executed {@link Runnable}s will not be interrupted.
	 * 
	 * @param runnable
	 *            {@link Runnable} to remove from the multiple execution list.
	 */
	public void stopMultipleExecution(Runnable runnable) {
		multiple.remove(runnable);
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub

	}

}
