package org.caleydo.rcp.splashHandlers;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.Activator;
import org.caleydo.rcp.Application;
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

	@Override
	public void init(Shell splash) {
		// Store the shell
		super.init(splash);

		// // TODO: remove this when webstart splash bug is solved
		// if (Application.bIsWebstart)
		// return;

		// Create UI
		createUI();

		// Enter event loop and prevent the RCP application from
		// loading until all work is done
		doEventLoop();

		// splash.setImage(GeneralManager.get().getResourceLoader().getImage(splash.getDisplay(),
		// "resources/icons/caleydo16.gif"));

		splash.setText("Loading Caleydo...");
	}

	private void createUI() {
		Shell splash = getSplash();

		progressBar = new ProgressBar(splash, SWT.SMOOTH | SWT.BORDER);
		progressBar.setBounds(20, 200, getSplash().getSize().x - 40, 25);

		splash.setBackgroundMode(SWT.INHERIT_DEFAULT);

		Label progressMessageLabel = new Label(splash, SWT.NONE);
		progressMessageLabel.setText("Loading...");
		// label.setForeground(splash.getDisplay().getSystemColor
		// (SWT.COLOR_BLACK));
		progressMessageLabel.setFont(new Font(splash.getDisplay(), "Arial", 10, SWT.NONE));
		progressMessageLabel.setBounds(20, 230, getSplash().getSize().x - 40, 25);
		progressMessageLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label versionLabel = new Label(splash, SWT.NONE);
		versionLabel.setText("Version " + Activator.sBundleVersion + " BETA");
		versionLabel.setFont(new Font(splash.getDisplay(), "Arial", 9, SWT.NONE));
		versionLabel.setBounds(336, 185, 150, 20);
		versionLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelTUGCaption = new Label(splash, SWT.NONE);
		brandingLabelTUGCaption.setText("Graz University of Technology");
		brandingLabelTUGCaption.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
		brandingLabelTUGCaption.setBounds(20, 268, 500, 15);
		brandingLabelTUGCaption.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelTUGNames = new Label(splash, SWT.NONE);
		brandingLabelTUGNames
			.setText("Marc Streit, Alexander Lex, Michael Kalkusch, Bernhard Schlegl, Dieter Schmalstieg");
		brandingLabelTUGNames.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.NONE));
		brandingLabelTUGNames.setBounds(20, 280, 500, 14);
		brandingLabelTUGNames.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelMUGCaption = new Label(splash, SWT.NONE);
		brandingLabelMUGCaption.setText("Medical Uiversity of Graz");
		brandingLabelMUGCaption.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
		brandingLabelMUGCaption.setBounds(20, 298, 500, 15);
		brandingLabelMUGCaption.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label brandingLabelMUGNames = new Label(splash, SWT.NONE);
		brandingLabelMUGNames.setText("Heimo Müller, Stefan Sauer, Wilhelm Steiner, Kurt Zatloukal");
		brandingLabelMUGNames.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.NONE));
		brandingLabelMUGNames.setBounds(20, 310, 300, 14);
		brandingLabelMUGNames.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label copyrightLabel = new Label(splash, SWT.NONE);
		copyrightLabel.setText("© 2005 - 2009 - www.caleydo.org");
		copyrightLabel.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
		copyrightLabel.setBounds(390, 310, 175, 14);
		copyrightLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GeneralManager.get().getSWTGUIManager().setExternalProgressBarAndLabel(progressBar,
			progressMessageLabel);
	}

	private void doEventLoop() {
		final Shell splash = getSplash();
		if (splash.getDisplay().readAndDispatch() == false) {
			splash.getDisplay().sleep();
		}

		// Make sure that splash screen remains the active window
		splash.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				// nothing to do
			}

			public void focusLost(FocusEvent e) {
				splash.forceActive();
			}
		});
	}

	@Override
	public void dispose() {
		if (!Application.bIsWebstart && !Application.bDoExit) {
			Application.startCaleydoCore();

			// Start OpenGL rendering
			GeneralManager.get().getViewGLCanvasManager().startAnimator();
		}
		// super.dispose();
	}
}
