package org.caleydo.core.gui;

import java.util.HashMap;

import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
	protected GeneralManager generalManager;

	/**
	 * SWT Display represents a thread.
	 */
	protected Display display = null;

	protected Composite composite;

	protected final HashMap<Integer, Composite> compositeMap;

	protected ProgressBar loadingProgressBar;

	protected Label loadingProgressBarLabel;

	protected IStatusLineManager externalRCPStatusLine;

	/**
	 * Constructor.
	 */
	public SWTGUIManager() {
		generalManager = GeneralManager.get();
		compositeMap = new HashMap<Integer, Composite>();
	}

	public void setProgressBarPercentage(int iPercentage) {
		if (loadingProgressBar == null || loadingProgressBar.isDisposed())
			return;

		loadingProgressBar.setSelection(iPercentage);
	}

	public void setProgressBarText(String sText) {

		if (loadingProgressBarLabel == null || loadingProgressBarLabel.isDisposed())
			return;

		loadingProgressBarLabel.setText(sText);
		loadingProgressBarLabel.update();
	}

	public void setProgressBarTextFromExternalThread(final String sText) {
	}

	public void setExternalProgressBarAndLabel(ProgressBar progressBar, Label progressLabel) {
		this.loadingProgressBar = progressBar;
		this.loadingProgressBarLabel = progressLabel;
	}

	public void setExternalRCPStatusLine(IStatusLineManager statusLine, Display display) {
		this.display = display;
		this.externalRCPStatusLine = statusLine;
	}
}