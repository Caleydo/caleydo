/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.internal;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * Caleydo splash screen with integrated progress bar.
 *
 * @author Marc Streit
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {

	@Override
	public void init(Shell splash) {

		// Store the shell
		super.init(splash);

		// Create UI
		createUI();
	}

	private void createUI() {
		Shell splash = getSplash();

		ProgressBar progressBar = new ProgressBar(splash, SWT.NONE);
		progressBar.setBounds(20, 200, splash.getSize().x - 40, 25);

		splash.setBackgroundMode(SWT.INHERIT_DEFAULT);

		Label progressMessageLabel = new Label(splash, SWT.NONE);
		progressMessageLabel.setText(" Loading...");
		final Display display = splash.getDisplay();
		GC gc = new GC(splash);
		gc.drawString("TEST", 50, 50, true);
		progressMessageLabel.setFont(new Font(display, "Arial", 10, SWT.NONE));
		progressMessageLabel.setBounds(20, 230, splash.getSize().x - 40, 25);
		progressMessageLabel.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		progressMessageLabel.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));

		Label versionLabel = new Label(splash, SWT.NONE);
		versionLabel.setText(" Version " + GeneralManager.VERSION);
		versionLabel.setFont(new Font(display, "Arial", 9, SWT.NONE));
		versionLabel.setBounds(336, 185, 150, 20);
		versionLabel.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		versionLabel.setBackground(display.getSystemColor(SWT.COLOR_DARK_GRAY));

		GeneralManager.get().getSWTGUIManager().setExternalProgressBarAndLabel(progressBar, progressMessageLabel);

		splash.setText("Loading Caleydo...");
	}
}
