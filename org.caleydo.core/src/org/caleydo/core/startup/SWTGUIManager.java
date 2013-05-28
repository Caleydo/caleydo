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

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;

/**
 * The SWTGUIManager is responsible for the creation and the administration of the windows and composites.
 * Also the overall layout is defined here and the menus are added to the windows. This class is not derived
 * from AManager since it does not manages IUniqueObjects.
 *
 * @author Marc Streit
 */
public class SWTGUIManager {

	private ProgressBar loadingProgressBar;

	private Label loadingProgressBarLabel;

	public void setProgressBarPercentage(int iPercentage) {
		if (loadingProgressBar == null || loadingProgressBar.isDisposed())
			return;

		loadingProgressBar.setSelection(iPercentage);
	}

	public void setProgressBarText(String text) {

		if (loadingProgressBarLabel == null || loadingProgressBarLabel.isDisposed())
			return;

		loadingProgressBarLabel.setText(text);
		loadingProgressBarLabel.update();
	}

	public void setProgressBarTextFromExternalThread(final String sText) {
	}

	public void setExternalProgressBarAndLabel(ProgressBar progressBar, Label progressLabel) {
		this.loadingProgressBar = progressBar;
		this.loadingProgressBarLabel = progressLabel;
	}

}