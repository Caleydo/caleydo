package org.caleydo.rcp.splashHandlers;

import org.caleydo.core.manager.GeneralManager;
import org.caleydo.rcp.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * Caleydo splash screen with integrated progress bar.
 * 
 * @author Marc Streit
 */
public class InteractiveSplashHandler
	extends AbstractSplashHandler {
	private ProgressBar progressBar;

	private static Shell shell;

	@Override
	public void init(Shell splash) {
		// Store the shell
		super.init(splash);

		shell = splash;

		// Create UI
		createUI();

		// Enter event loop and prevent the RCP application from
		// loading until all work is done
		doEventLoop();

		// splash.setImage(GeneralManager.get().getResourceLoader().getImage(splash.getDisplay(),
		// "resources/icons/caleydo16.gif"));

		splash.setText("Loading Caleydo...");
	}

	public static Shell getShell() {
		return shell;
	}

	private void createUI() {
		Shell splash = getSplash();

		progressBar = new ProgressBar(splash, SWT.SMOOTH | SWT.BORDER);
		progressBar.setBounds(20, 200, getSplash().getSize().x - 40, 25);

		splash.setBackgroundMode(SWT.INHERIT_DEFAULT);

		Label progressMessageLabel = new Label(splash, SWT.NONE);
		progressMessageLabel.setText(" Loading...");
		// label.setForeground(splash.getDisplay().getSystemColor
		// (SWT.COLOR_BLACK));
		progressMessageLabel.setFont(new Font(splash.getDisplay(), "Arial", 10, SWT.NONE));
		progressMessageLabel.setBounds(20, 230, getSplash().getSize().x - 40, 25);
		progressMessageLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label versionLabel = new Label(splash, SWT.NONE);
		versionLabel.setText(" Version " + Activator.sVersion + " BETA");
		versionLabel.setFont(new Font(splash.getDisplay(), "Arial", 9, SWT.NONE));
		versionLabel.setBounds(336, 185, 150, 20);
		versionLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelTUGCaption = new Label(splash, SWT.NONE);
		brandingLabelTUGCaption.setText(" Graz University of Technology");
		brandingLabelTUGCaption.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
		brandingLabelTUGCaption.setBounds(20, 253, 500, 15);
		brandingLabelTUGCaption.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelTUGNames = new Label(splash, SWT.NONE);
		brandingLabelTUGNames
			.setText(" Marc Streit, Alexander Lex, Michael Kalkusch, Bernhard Schlegl, \n Werner Puff, Christian Partl, Dieter Schmalstieg");
		brandingLabelTUGNames.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.NONE));
		brandingLabelTUGNames.setBounds(20, 270, 500, 30);
		brandingLabelTUGNames.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelMUGCaption = new Label(splash, SWT.NONE);
		brandingLabelMUGCaption.setText(" Medical University of Graz");
		brandingLabelMUGCaption.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
		brandingLabelMUGCaption.setBounds(20, 298, 500, 15);
		brandingLabelMUGCaption.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelMUGNames = new Label(splash, SWT.NONE);
		brandingLabelMUGNames.setText(" Heimo Mueller, Stefan Sauer, Wilhelm Steiner, Kurt Zatloukal");
		brandingLabelMUGNames.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.NONE));
		brandingLabelMUGNames.setBounds(20, 312, 300, 15);
		brandingLabelMUGNames.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label copyrightLabel = new Label(splash, SWT.NONE);
		copyrightLabel.setText(" (c) 2005 - 2010  www.caleydo.org");
		copyrightLabel.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
		copyrightLabel.setBounds(385, 312, 180, 16);
		copyrightLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GeneralManager.get().getSWTGUIManager()
			.setExternalProgressBarAndLabel(progressBar, progressMessageLabel);

		// Application.startCaleydoCore();
	}

	private void doEventLoop() {
		final Shell splash = getSplash();
		if (splash.getDisplay().readAndDispatch() == false) {
			splash.getDisplay().sleep();
		}

		// Make sure that splash screen remains the active window
		splash.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				// nothing to do
			}

			@Override
			public void focusLost(FocusEvent e) {
				splash.forceActive();
			}
		});
	}

	@Override
	public void dispose() {
		// if (!Application.bDoExit) {

		// Start OpenGL rendering
		GeneralManager.get().getViewGLCanvasManager().startAnimator();
		// }
	}
}
