package org.caleydo.rcp.splashHandlers;

import java.awt.event.FocusAdapter;

import org.caleydo.rcp.Application;
import org.caleydo.rcp.progress.PathwayLoadingProgressIndicatorAction;
import org.caleydo.rcp.wizard.project.CaleydoProjectWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * Caleydo splash screen with integrated progress bar.
 */
public class ExtensibleSplashHandler
	extends AbstractSplashHandler
{
	private ProgressBar progressBar;
	
	@Override
	public void init(Shell splash)
	{
		// Store the shell
		super.init(splash);

		// Create UI
		createUI();

		// Enter event loop and prevent the RCP application from
		// loading until all work is done
		doEventLoop();
	}

	/**
	 * 
	 */
	private void createUI()
	{
		Shell splash = getSplash();
		splash.setLayout(new FillLayout());
		// Force shell to inherit the splash background
		splash.setBackgroundMode(SWT.INHERIT_DEFAULT);
		
		// Create the composite
		final Composite composite = new Composite(splash, SWT.NONE);
		
		// Configure layout
		RowLayout layout = new RowLayout(SWT.VERTICAL);
//	    layout.marginLeft = 12;
	    layout.marginTop = 2;
//	    layout.justify = true;
		composite.setLayout(layout);
		
		progressBar = new ProgressBar(composite, SWT.SMOOTH);
		progressBar.setLayoutData(new RowData(getSplash().getSize().x - 5, 20));
		
		Label label = new Label(composite, SWT.NONE);
		label.setText("Loading...");
		label.setForeground(splash.getDisplay().getSystemColor (SWT.COLOR_WHITE));
		label.setLayoutData(new RowData(getSplash().getSize().x - 5, 20));
		
		Application.generalManager.getSWTGUIManager().setExternalProgressBarAndLabel(
				progressBar, label);
	}
	
	/**
	 * 
	 */
	private void doEventLoop()
	{
		final Shell splash = getSplash();
		if (splash.getDisplay().readAndDispatch() == false)
		{
			splash.getDisplay().sleep();
		}
		
		// Make sure that splash screen remains the active window
		splash.addFocusListener(new FocusListener() {

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
		startCaleydoCore("data/bootstrap/shared/kashofer/hcc/all_hcc/rcp/bootstrap.xml");
		
		// Trigger pathway loading
		new PathwayLoadingProgressIndicatorAction().run(null);
		
//		super.dispose();
	}
	
	public void startCaleydoCore(final String sXmlFileName)
	{
		// If no file is provided as command line argument a XML file open
		// dialog is opened
		if (sXmlFileName == "")
		{
			Display display = PlatformUI.createDisplay();
			Shell shell = new Shell(display);
			shell.setText("Open project file");

			WizardDialog projectWizardDialog = new WizardDialog(shell,
					new CaleydoProjectWizard());
			projectWizardDialog.open();

			// FileOpenProjectAction openProjectAction = new
			// FileOpenProjectAction(shell);
			// openProjectAction.run();

			shell.dispose();
		}
		// Load as command line argument provided XML config file name.
		else
		{
			Application.caleydoCore.setXmlFileName(sXmlFileName);
			Application.caleydoCore.start();
		}
	}
}
