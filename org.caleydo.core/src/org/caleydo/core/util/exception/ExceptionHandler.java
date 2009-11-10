package org.caleydo.core.util.exception;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

/**
 * Catches exceptions and logs them, hides them when in release mode
 * 
 * @author Alexander Lex
 * @author Marc Streit
 */
public class ExceptionHandler {
	/**
	 * Determine whether to hide exceptions and log them, or whether to throw them
	 */
	public static final boolean HIDE_EXCEPTIONS = false;

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
	 * Handle view exceptions. Depending on {@link ExceptionHandler#HIDE_EXCEPTIONS} exceptions are either
	 * rethrown or hidden and logged
	 * 
	 * @param exception
	 */
	public void handleViewException(RuntimeException exception, final AGLEventListener glEventListener) {
		if (HIDE_EXCEPTIONS) {
			GeneralManager.get().getLogger().log(
				new Status(Status.ERROR, GeneralManager.PLUGIN_ID, "Caught Exception: "
					+ exception.getMessage(), exception));
		}

		glEventListener.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				MessageBox messageBox = new MessageBox(glEventListener.getParentGLCanvas().getParentComposite().getShell(), SWT.OK);
				messageBox.setText("Error in view");
				messageBox.setMessage("An unexpected error occured in view " +glEventListener.getShortInfo() +". The view will be closed now. See the error log for details. You can try to re-open it.");
				messageBox.open();
			}
		});
		
		// Unregister view from GL event queue
		GeneralManager.get().getGUIBridge().closeView(glEventListener.getViewGUIID());
		GeneralManager.get().getViewGLCanvasManager().unregisterGLCanvas(glEventListener.getParentGLCanvas());
		GeneralManager.get().getViewGLCanvasManager().unregisterGLEventListener(glEventListener);
	}
}
