package org.caleydo.core.util.exception;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;

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

		GeneralManager.get().getLogger().log(
			new Status(IStatus.ERROR, IGeneralManager.PLUGIN_ID, "Caught Exception: "
				+ exception.getMessage(), exception));

		glEventListener.getParentGLCanvas().getParentComposite().getDisplay().asyncExec(new Runnable() {
			public void run() {
				MessageBox messageBox =
					new MessageBox(glEventListener.getParentGLCanvas().getParentComposite().getShell(),
						SWT.OK);
				messageBox.setText("Error in view");
				messageBox
					.setMessage("An unexpected error occured in view "
						+ glEventListener.getShortInfo()
						+ ". The view will be closed now. See the error log for details. You can try to re-open it.");
				messageBox.open();
			}
		});

		// Unregister view from GL event queue
		GeneralManager.get().getGUIBridge().closeView(glEventListener.getViewType());
		GeneralManager.get().getViewGLCanvasManager().unregisterGLCanvas(glEventListener.getParentGLCanvas());
		GeneralManager.get().getViewGLCanvasManager().unregisterGLView(glEventListener);
	}
}
