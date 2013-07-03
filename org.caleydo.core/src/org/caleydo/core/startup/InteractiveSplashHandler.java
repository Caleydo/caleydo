/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

/**
 * Caleydo splash screen with integrated progress bar.
 *
 * @author Marc Streit
 */
public class InteractiveSplashHandler extends AbstractSplashHandler {

	private GC gc;

	private int progressLabelX;
	private int progressLabelY;

	private ProgressBar progressBar;

	@Override
	public void init(Shell splash) {

		super.init(splash);

		createUI();

		// Force the splash screen to layout
		splash.layout(true);
	}

	private void createUI() {
		Shell splash = getSplash();

		progressBar = new ProgressBar(splash, SWT.NONE);
		progressBar.setBounds(20, 200, progressLabelX, progressLabelY);

		splash.setBackgroundMode(SWT.INHERIT_DEFAULT);

		progressLabelX = splash.getSize().x - 40;
		progressLabelY = 25;

		final Display display = splash.getDisplay();
		gc = new GC(splash);
		gc.setFont(new Font(display, "Arial", 10, SWT.NONE));
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));

		updateProgressLabel("Loading...");

		gc.drawString(" Version " + GeneralManager.VERSION, 336, 185, true);

		GeneralManager.get().setSplash(this);

		splash.setText("Loading Caleydo...");
	}

	public void updateProgress(int percentage) {

		if (progressBar.isDisposed())
			return;

		progressBar.setSelection(percentage);
	}

	public void updateProgressLabel(String message) {
		gc.drawString(message, progressLabelX, progressLabelY, true);
	}
}
