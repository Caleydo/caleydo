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

import java.awt.Color;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Font;
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
		progressMessageLabel
				.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		Label versionLabel = new Label(splash, SWT.NONE);
		versionLabel.setText(" Version " + Activator.version + " BETA");
		versionLabel.setFont(new Font(splash.getDisplay(), "Arial", 9, SWT.NONE));
		versionLabel.setBounds(336, 185, 150, 20);
		versionLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

//		Label brandingLabelTUGCaption = new Label(splash, SWT.NONE);
//		brandingLabelTUGCaption.setText(" Graz University of Technology, Austria:");
//		brandingLabelTUGCaption.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
//		brandingLabelTUGCaption.setBounds(20, 253, 220, 15);
//		brandingLabelTUGCaption.setForeground(splash.getDisplay().getSystemColor(
//				SWT.COLOR_WHITE));

//		Label brandingLabelTUGTeamCaption = new Label(splash, SWT.NONE);
//		brandingLabelTUGTeamCaption
//				.setText(" Alexander Lex, Christian Partl, Dieter Schmalstieg ");
//		brandingLabelTUGTeamCaption.setFont(new Font(splash.getDisplay(), "Arial", 8,
//				SWT.NORMAL));
//		brandingLabelTUGTeamCaption.setBounds(240, 253, 250, 15);
//		brandingLabelTUGTeamCaption.setForeground(splash.getDisplay().getSystemColor(
//				SWT.COLOR_WHITE));
//
//		Label brandingLabelJKUCaption = new Label(splash, SWT.NONE);
//		brandingLabelJKUCaption.setText(" Johannes Kepler University Linz, Austria:");
//		brandingLabelJKUCaption.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
//		brandingLabelJKUCaption.setBounds(20, 270, 220, 15);
//		brandingLabelJKUCaption.setForeground(splash.getDisplay().getSystemColor(
//				SWT.COLOR_WHITE));
//
//		Label brandingLabelJKUTeamCaption = new Label(splash, SWT.NONE);
//		brandingLabelJKUTeamCaption.setText(" Marc Streit ");
//		brandingLabelJKUTeamCaption.setFont(new Font(splash.getDisplay(), "Arial", 8,
//				SWT.NORMAL));
//		brandingLabelJKUTeamCaption.setBounds(240, 270, 100, 15);
//		brandingLabelJKUTeamCaption.setForeground(splash.getDisplay().getSystemColor(
//				SWT.COLOR_WHITE));

//		Label brandingLabelHarvardCaption = new Label(splash, SWT.NONE);
//		brandingLabelHarvardCaption.setText(" Harvard University, US: ");
//		brandingLabelHarvardCaption
//				.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
//		brandingLabelHarvardCaption.setBounds(20, 287, 220, 15);
//		brandingLabelHarvardCaption.setForeground(splash.getDisplay().getSystemColor(
//				SWT.COLOR_WHITE));
//
//		Label brandingLabelHarvardTeamCaption = new Label(splash, SWT.NONE);
//		brandingLabelHarvardTeamCaption.setText(" Nils Gehlenborg ");
//		brandingLabelHarvardTeamCaption.setFont(new Font(splash.getDisplay(), "Arial", 8,
//				SWT.NORMAL));
//		brandingLabelHarvardTeamCaption.setBounds(240, 287, 100, 15);
//		brandingLabelHarvardTeamCaption.setForeground(splash.getDisplay().getSystemColor(
//				SWT.COLOR_WHITE));

		// Label brandingLabelTUGNames = new Label(splash, SWT.NONE);
		// brandingLabelTUGNames
		// .setText(" Marc Streit, Alexander Lex, Michael Kalkusch, Bernhard Schlegl, \n Werner Puff, Christian Partl, Dieter Schmalstieg");
		// brandingLabelTUGNames.setFont(new Font(splash.getDisplay(), "Arial",
		// 8, SWT.NONE));
		// brandingLabelTUGNames.setBounds(20, 270, 500, 30);
		// brandingLabelTUGNames.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		// Label brandingLabelMUGCaption = new Label(splash, SWT.NONE);
		// brandingLabelMUGCaption.setText(" Medical University of Graz");
		// brandingLabelMUGCaption.setFont(new Font(splash.getDisplay(),
		// "Arial", 8, SWT.BOLD));
		// brandingLabelMUGCaption.setBounds(20, 298, 500, 15);
		// brandingLabelMUGCaption.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		//
		// Label brandingLabelMUGNames = new Label(splash, SWT.NONE);
		// brandingLabelMUGNames.setText(" Heimo Mueller, Stefan Sauer, Wilhelm Steiner, Kurt Zatloukal");
		// brandingLabelMUGNames.setFont(new Font(splash.getDisplay(), "Arial",
		// 8, SWT.NONE));
		// brandingLabelMUGNames.setBounds(20, 312, 300, 15);
		// brandingLabelMUGNames.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

//		Label copyrightLabel = new Label(splash, SWT.NONE);
//		copyrightLabel.setText(" (c) 2005 - 2012  www.caleydo.org");
//		copyrightLabel.setFont(new Font(splash.getDisplay(), "Arial", 8, SWT.BOLD));
//		copyrightLabel.setBounds(385, 312, 180, 16);
//		copyrightLabel.setForeground(splash.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		GeneralManager.get().getSWTGUIManager()
				.setExternalProgressBarAndLabel(progressBar, progressMessageLabel);
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
}
