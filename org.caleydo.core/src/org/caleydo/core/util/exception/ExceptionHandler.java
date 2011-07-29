package org.caleydo.core.util.exception;

import org.caleydo.core.manager.GeneralManager;
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
					new MessageBox(glEventListener.getParentComposite().getShell(),
						SWT.OK);
				messageBox.setText("Error in view");
				messageBox.setMessage("An unexpected error occured in view "
					+ glEventListener.getShortInfo()
					+ ". The view will be closed now. See the error log for details. You can try to re-open it.");
				messageBox.open();
			}
		});

		// Unregister view from GL2 event queue
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				IViewPart viewToClose =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(glEventListener.getViewType());
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(viewToClose);

			}
		});
		GeneralManager.get().getViewManager().unregisterGLCanvas(glEventListener.getParentGLCanvas());
		GeneralManager.get().getViewManager().unregisterGLView(glEventListener);
	}
}
