/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import org.caleydo.core.internal.gui.CaleydoProjectWizard;
import org.caleydo.core.internal.startup.StartupAddons;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.startup.CacheInitializers;
import org.caleydo.core.startup.IStartUpDocumentListener;
import org.caleydo.core.startup.IStartupAddon;
import org.caleydo.core.startup.IStartupProcedure;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.kohsuke.args4j.ClassParser;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {
	private static Application instance;

	public static Application get() {
		return instance;
	}

	private IStartupProcedure startup;
	private volatile boolean startupDone;

	@Override
	public Object start(IApplicationContext context) throws Exception {
		final Logger log = Logger.create(Application.class);
		try {
			instance = this;
			log.info("Starting Caleydo");

			dumpEnvironment(log);

			changeWorkspaceLocation();

			GeneralManager.get(); // stupid but needed for initialization

			Map<String, IStartupAddon> startups = StartupAddons.findAll();

			parseArgs(startups.values(), (String[]) context.getArguments().get("application.args"));
			// ) {
			// return EXIT_OK; // invalid args
			// }


			// create a select the startup pro
			Display display = PlatformUI.createDisplay();
			for (IStartupAddon addon : startups.values()) {
				if (addon instanceof IStartUpDocumentListener) {
					display.addListener(SWT.OpenDocument, (IStartUpDocumentListener) addon);
				}
			}
			display.addListener(SWT.OpenDocument, new Listener() {
				@Override
				public void handleEvent(Event event) {
					System.err.println("Openening: " + event.text);
					log.error("Opening" + event.text);
				}
			});
			while (!display.isDisposed() && display.readAndDispatch()) { // dispatch all pending
				// dispatch events
			}

			startup = selectStartupProcedure(startups, display, log);
			if (startup == null)
				return EXIT_OK; // unstartable
			startups = null; // cleanup

			if (!startup.preWorkbenchOpen()) {
				log.info("error during pre workbench open of: " + startup);
				return EXIT_OK;
			}

			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());

			log.info("Bye bye!");

			if (returnCode == PlatformUI.RETURN_RESTART) {
				return EXIT_RESTART;
			}
			return EXIT_OK;

		} catch (Exception e) {
			log.error("Caught exception, crashing.", e);
			throw e;
		} finally {
			instance = null;
		}
	}


	/**
	 * @param startups
	 * @param display
	 * @return
	 */
	private IStartupProcedure selectStartupProcedure(Map<String, IStartupAddon> startups, Display display, Logger log) {
		for (IStartupAddon startup : startups.values()) {
			if (startup.init())
				return startup.create();
		}

		log.info("creating wizard shell");
		// not yet configured choose one
		Shell shell = new Shell(display);
		shell.moveAbove(null);
		CaleydoProjectWizard wizardImpl = new CaleydoProjectWizard(startups);
		WizardDialog wizard = new WizardDialog(shell, wizardImpl);
		wizard.setMinimumPageSize(750, 500);
		shell.forceActive();

		log.info("open wizard");
		boolean ok = wizard.open() == Window.OK;
		if (!shell.isDisposed())
			shell.dispose();
		log.info("wizard done");
		return ok ? wizardImpl.getResult() : null;
	}

	/**
	 * @param values
	 * @param strings
	 */
	private static boolean parseArgs(Collection<IStartupAddon> startups, String[] args) {
		CmdLineParser parser = new CmdLineParser(null);
		ClassParser beanParser = new ClassParser();
		// scan all addons for command line args
		for (IStartupAddon startup : startups)
			beanParser.parse(startup, parser);

		try {
			parser.parseArgument(args);
			return true;
		} catch (CmdLineException e) {
			// if there's a problem in the command line,
			// you'll get this exception. this will report
			// an error message.
			System.err.println(e.getMessage());
			System.err.println("Caleydo [options...] arguments...");
			// print the list of available options
			parser.printUsage(System.err);
			System.err.println();
			return false;
		}
	}

	/**
	 * @param log
	 *
	 */
	private static void dumpEnvironment(Logger log) {
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

	/**
	 * Changing the workspace location in order to be able to store and restore the workbench state (also in combination
	 * with serialized projects).
	 */
	private static void changeWorkspaceLocation() {

		final Location instanceLoc = Platform.getInstanceLocation();

		File workspacePath = new File(GeneralManager.CALEYDO_HOME_PATH);
		try {
			URL workspaceURL = workspacePath.toURI().toURL();
			instanceLoc.set(workspaceURL, false);
		} catch (Exception e) {
			// throw new IllegalStateException
			System.err.println("Cannot set workspace location at " + workspacePath);
		}
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


	public void runStartup() {
		assert startup != null;
		// run the startup in an own thread but wait in the gui thread till its done
		final Runnable dummy = new Runnable() {
			@Override
			public void run() {
				//mark as done
				startupDone = true;
			}
		};
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					CacheInitializers.runInitializers(GeneralManager.get().createSubProgressMonitor());
					startup.run();
				} finally {
					//notify and wake up main thread
					Display.getDefault().asyncExec(dummy);
				}
			}
		});
		t.start();
		final Display display = Display.getCurrent();
		while (!startupDone && !display.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		//strange issue #1413 the workbench shell needs to be active to start running. Try to start it up
		display.addFilter(SWT.Show, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (event.widget instanceof Shell) {
					System.out.println("show: "+
							((Shell)event.widget).getText());
					display.removeFilter(SWT.Show, this);
					((Shell)event.widget).forceActive();
				}
			}
		});
	}

	/**
	 * @param windowConfigurer
	 */
	public void postWorkbenchOpen(IWorkbenchWindowConfigurer windowConfigurer) {
		startup.postWorkbenchOpen(windowConfigurer);
		windowConfigurer.getWindow().getShell().setMaximized(true);
		startup = null;
	}

	public class OpenDocumentEventProcessor implements Listener {
		private ArrayList<String> filesToOpen = new ArrayList<String>(1);

		@Override
		public void handleEvent(Event event) {
			if (event.text != null)
				filesToOpen.add(event.text);
		}

		public void openFiles() {
			if (filesToOpen.isEmpty())
				return;

			String[] filePaths = filesToOpen.toArray(new String[filesToOpen.size()]);
			filesToOpen.clear();

			for (String path : filePaths) {
				// open the file path
			}
		}
	}
}
