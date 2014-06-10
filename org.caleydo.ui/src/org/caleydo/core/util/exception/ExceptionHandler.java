/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.exception;

import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.ViewManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Catches exceptions and logs them
 *
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ExceptionHandler {

	private static ExceptionHandler singletonInstance = new ExceptionHandler();

	private ExceptionHandler() {

	}

	/**
	 * Get singleton instance of exception handler
	 *
	 * @return
	 */
	public static ExceptionHandler get() {
		return singletonInstance;
	}

	/**
	 * Handle view exceptions.
	 *
	 * @param exception
	 */
	public void handleViewException(RuntimeException exception, final AGLView glEventListener) {

		Logger.log(new Status(IStatus.ERROR, this.toString(), "Caught Exception: "
				+ exception.getMessage(), exception));
		

		ViewManager.get().unregisterGLCanvas(glEventListener.getParentGLCanvas());

		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {

				CaleydoRCPViewPart viewToClose = ViewManager.get().getViewPartFromView(
						glEventListener);
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.hideView(viewToClose);

				MessageBox messageBox = new MessageBox(new Shell(), SWT.OK);
				messageBox.setText("Error in view");
				messageBox.setMessage("An unexpected error occured in view "
						+ glEventListener.getViewName()
						+ ". The view will be closed now. See the error log for details. You can try to re-open it.");
				messageBox.open();
			}
		});
	}
}
