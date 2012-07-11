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
package org.caleydo.core.util.exception;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * Catches exceptions and logs them
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ExceptionHandler {

	private static ExceptionHandler singletonInstance = null;

	private ExceptionHandler() {

	}

	/**
	 * Get singleton instance of exception handler
	 * 
	 * @return
	 */
	public static ExceptionHandler get() {
		if (singletonInstance == null) {
			singletonInstance = new ExceptionHandler();
		}

		return singletonInstance;
	}

	/**
	 * Handle view exceptions.
	 * 
	 * @param exception
	 */
	public void handleViewException(RuntimeException exception, final AGLView glEventListener) {

		Logger.log(new Status(IStatus.ERROR, this.toString(), "Caught Exception: " + exception.getMessage(),
			exception));

		glEventListener.getParentComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox messageBox =
					new MessageBox(glEventListener.getParentComposite().getShell(), SWT.OK);
				messageBox.setText("Error in view");
				messageBox.setMessage("An unexpected error occured in view "
					+ glEventListener.getViewName()
					+ ". The view will be closed now. See the error log for details. You can try to re-open it.");
				messageBox.open();
			}
		});

		// Unregister view from GL2 event queue
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IViewPart viewToClose =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.findView(glEventListener.getViewType());
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(viewToClose);

			}
		});
//		GeneralManager.get().getViewManager().unregisterGLCanvas(glEventListener.getParentGLCanvas());
//		GeneralManager.get().getViewManager().unregisterGLView(glEventListener);
	}
}
