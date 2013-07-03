/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.startup;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * The SWTGUIManager is responsible for the creation and the administration of the windows and composites. Also the
 * overall layout is defined here and the menus are added to the windows. This class is not derived from AManager since
 * it does not manages IUniqueObjects.
 *
 * @author Marc Streit
 */
public class SWTGUIManager {

	private ProgressBar loadingProgressBar;

	private Label loadingProgressBarLabel;

	public void setProgressBarPercentage(final int iPercentage) {
		if (loadingProgressBar == null || loadingProgressBar.isDisposed())
			return;
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!loadingProgressBar.isDisposed())
					loadingProgressBar.setSelection(iPercentage);
			}
		});
	}

	public void setProgressBarText(final String text) {

		if (loadingProgressBarLabel == null || loadingProgressBarLabel.isDisposed())
			return;
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				if (!loadingProgressBarLabel.isDisposed()) {
					loadingProgressBarLabel.setText(text);
					loadingProgressBarLabel.update();
				}
			}
		});
	}

	public void setProgressBarTextFromExternalThread(final String sText) {
	}

	public void setExternalProgressBarAndLabel(ProgressBar progressBar, Label progressLabel) {
		this.loadingProgressBar = progressBar;
		this.loadingProgressBarLabel = progressLabel;
	}

}
