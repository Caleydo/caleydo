/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.caleydo.core.internal.Application;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * Caleydo splash screen with integrated progress bar.
 *
 * @author Marc Streit
 */
public class InteractiveSplashHandler extends BasicSplashHandler {
	@Override
	public void init(Shell splash) {
		super.init(splash);

		setProgressRect(new Rectangle(55, 351, 537, 20));
		setMessageRect(new Rectangle(55, 328, 537, 20));
		setForeground(new RGB(255, 255, 255));


		Label version = new Label(getContent(), SWT.RIGHT);
		version.setForeground(getForeground());
		version.setBounds(new Rectangle(55, 378, 537, 20));
		version.setText("Caleydo Version " + GeneralManager.VERSION);

		final int baseHeight = getPXHeight(splash, new FontData("Arial", 12, SWT.NORMAL));
		final float px2pt = 12.f / baseHeight;

		version.setFont(new Font(splash.getDisplay(), new FontData("Arial", (int) (12 * px2pt), SWT.NORMAL)));
		getContent().setFont(new Font(splash.getDisplay(), new FontData("Arial", (int) (15 * px2pt), SWT.NORMAL)));

		// publish the progress monitor
		IProgressMonitor monitor = this.getBundleProgressMonitor();
		monitor.beginTask("Loading Caleydo...", 100);

		GeneralManager.get().setSplashProgressMonitor(monitor);

		if (Application.get() != null)
			Application.get().runStartup();
	}

	private int getPXHeight(Shell splash, FontData fd) {
		Font f = new Font(splash.getDisplay(), fd);
		GC gc = new GC(splash);
		gc.setFont(f);
		FontMetrics m = gc.getFontMetrics();
		final int px = m.getHeight();
		gc.dispose();
		f.dispose();
		return px;
	}

}