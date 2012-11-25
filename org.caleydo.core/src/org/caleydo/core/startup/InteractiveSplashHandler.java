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

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.SWT;
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

		ProgressBar progressBar = new ProgressBar(splash, SWT.SMOOTH | SWT.BORDER);
		progressBar.setBounds(20, 200, splash.getSize().x - 40, 25);

		splash.setBackgroundMode(SWT.INHERIT_DEFAULT);

		Label progressMessageLabel = new Label(splash, SWT.NONE);
		progressMessageLabel.setText(" Loading...");
		progressMessageLabel
				.setFont(new Font(splash.getDisplay(), "Arial", 10, SWT.NONE));
		progressMessageLabel.setBounds(20, 230, splash.getSize().x - 40, 25);
		progressMessageLabel.setForeground(splash.getDisplay().getSystemColor(
				SWT.COLOR_WHITE));

		Label versionLabel = new Label(splash, SWT.NONE);
		versionLabel.setText(" Version " + Activator.version);
		versionLabel.setFont(new Font(splash.getDisplay(), "Arial", 9, SWT.NONE));
		versionLabel.setBounds(336, 185, 150, 20);
		versionLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GeneralManager.get().getSWTGUIManager()
				.setExternalProgressBarAndLabel(progressBar, progressMessageLabel);
		
		splash.setText("Loading Caleydo...");
	}
}
