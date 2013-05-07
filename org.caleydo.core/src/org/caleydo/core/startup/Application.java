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
package org.caleydo.core.startup;

import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	@SuppressWarnings("unchecked")
	@Override
	public Object start(IApplicationContext context) throws Exception {
		final Logger log = Logger.create(Application.class);
		try {
			log.info("Starting Caleydo");
			dumpEnv(log);
			GeneralManager.get().getPreferenceStore();

			int returnCode = StartupProcessor.get().initStartupProcudure(context.getArguments());
			if (returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;

		} catch (Exception e) {
			Logger.log(new Status(IStatus.ERROR, this.toString(),
					"Caught exception, crashing.", e));
			throw e;
		}
	}

	/**
	 * @param log
	 * 
	 */
	private void dumpEnv(Logger log) {
		StringBuilder b = new StringBuilder();
		Map<String, String> env = System.getenv();
		for (String key : new TreeSet<>(env.keySet())) {
			b.append(key).append('=').append(env.get(key)).append('\n');
		}
		log.debug("Environment Variables:\n%s", b);

		b = new StringBuilder();
		Properties props = System.getProperties();
		for (String key : new TreeSet<>(props.stringPropertyNames())) {
			b.append(key).append('=').append(props.getProperty(key)).append('\n');
		}
		log.debug("System Properties:\n%s", b);
	}

	@Override
	public void stop() {
		final IWorkbench workbench = PlatformUI.getWorkbench();

		if (workbench == null)
			return;

		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			@Override
			public void run() {
				if (!display.isDisposed()) {
					workbench.close();
				}
			}
		});
	}
}