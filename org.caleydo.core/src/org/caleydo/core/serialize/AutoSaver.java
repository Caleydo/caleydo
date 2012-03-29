package org.caleydo.core.serialize;

import java.util.Date;
import org.caleydo.core.util.execution.DisplayLoopExecution;
import org.caleydo.core.view.ViewManager;
import org.eclipse.ui.PlatformUI;

/**
 * Saves the project-state periodically. Should only be added as multiple-execution object to a
 * {@link DisplayLoopExecution} as usually managed by {@link ViewManager}.
 * 
 * @author Werner Puff
 * @author Marc Streit
 */
public class AutoSaver
	implements Runnable {

	/** Default interval to wait between 2 auto-saves in milliseconds */
	public static final long DEFAULT_INTERVAL = 100000;

	/** Time stamp of the last save operation, initialized with the object creation time stamp */
	protected Date lastSaveTimeStamp;

	/** interval to wait between 2 auto-saves in milliseconds */
	protected long interval;

	public AutoSaver() {
		lastSaveTimeStamp = new Date();
		interval = DEFAULT_INTERVAL;
	}

	/**
	 * Saves the project if enough time has passed since the last auto save operation.
	 */
	@Override
	public void run() {

		// Call this the memento saver with async because otherwise the render thread crashes
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				if ((new Date()).getTime() > lastSaveTimeStamp.getTime() + DEFAULT_INTERVAL) {
					// FIXME re-enable
					// Date start = new Date();
					// ProjectSaver projectSaver = new ProjectSaver();
					// projectSaver.saveRecentProject();
					// Date stop = new Date();
					// Logger.log(new Status(IStatus.INFO, this.toString(), "AutoSaver: auto save took "
					// + (stop.getTime() - start.getTime()) + " ms"));
					//
					// lastSaveTimeStamp = new Date();
				}
			}
		});
	}

	/**
	 * Gets the interval to wait between 2 auto-saves in milliseconds.
	 * 
	 * @return auto save interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * Sets the interval to wait between 2 auto-saves in milliseconds.
	 * 
	 * @param interval
	 *            new auto save interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * Gets the timestamp when the most recent auto save operation has been performed. If no auto save
	 * operation has been performed yet, the initialization time of this instance is returned.
	 * 
	 * @return timestamp of most recent recent auto save operation.
	 */
	public Date getLastSaveTimeStamp() {
		return lastSaveTimeStamp;
	}
}
