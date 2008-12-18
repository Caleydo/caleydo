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
	extends AbstractSplashHandler
{
	private ProgressBar progressBar;

	@Override
	public void init(Shell splash)
	{
		// Store the shell
		super.init(splash);

		// TODO: remove this when webstart splash bug is solved
		if (Application.bIsWebstart)
			return;

		// Create UI
		createUI();

		// Enter event loop and prevent the RCP application from
		// loading until all work is done
		doEventLoop();

		// splash.setImage(GeneralManager.get().getResourceLoader().getImage(splash.getDisplay(),
		// "resources/icons/caleydo16.gif"));

		splash.setText("Loading Caleydo...");
	}

	private void createUI()
	{
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
		progressMessageLabel.setForeground(splash.getDisplay().getSystemColor(
				SWT.COLOR_DARK_GRAY));

		Label versionLabel = new Label(splash, SWT.NONE);
		versionLabel.setText("Version " + Activator.sBundleVersion + " BETA");
		versionLabel.setFont(new Font(splash.getDisplay(), "Arial", 10, SWT.NONE));
		versionLabel.setBounds(185, 177, 300, 20);
		versionLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));

		GeneralManager.get().getSWTGUIManager().setExternalProgressBarAndLabel(progressBar,
				progressMessageLabel);
	}

	private void doEventLoop()
	{
		final Shell splash = getSplash();
		if (splash.getDisplay().readAndDispatch() == false)
		{
			splash.getDisplay().sleep();
		}

		// Make sure that splash screen remains the active window
		splash.addFocusListener(new FocusListener()
		{
			public void focusGained(FocusEvent e)
			{
				// nothing to do
			}

			public void focusLost(FocusEvent e)
			{
				splash.forceActive();
			}
		});
	}

	@Override
	public void dispose()
	{
		if (!Application.bIsWebstart && !Application.bDoExit)
		{
			Application.startCaleydoCore();

			// Start OpenGL rendering
			GeneralManager.get().getViewGLCanvasManager().startAnimator();
		}
		// super.dispose();
	}
}
