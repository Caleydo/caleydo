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
package org.caleydo.core.util.clusterer.gui;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.event.AEventListener;
import org.caleydo.core.event.IListenerOwner;
import org.caleydo.core.event.data.ClusterProgressEvent;
import org.caleydo.core.event.data.ClustererCanceledEvent;
import org.caleydo.core.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

/**
 * Progress bar visualizing the progress during a cluster process.
 * 
 * @author
 */
public class ClusteringProgressBar
	implements IListenerOwner {

	private ProgressBar pbOverall;
	private ProgressBar pbClusterer;
	private String algorithmName;
	private ClusterProgressListener clusterProgressListener;


	private Shell shell;
	private Label lbProgressBarClusterer;

	public ClusteringProgressBar(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public void run() {
		buildProgressBar();
		registerEventListeners();
	}

	/**
	 * Sets the label of the second progress bar
	 * 
	 * @param stProgressBarLable
	 */
	public void setProgressBarLabel(String stProgressBarLable) {
		if (lbProgressBarClusterer.isDisposed())
			return;
		lbProgressBarClusterer.setText(stProgressBarLable);
	}

	/**
	 * Sets the progress bar. Depending on forSimilaritiesBar the overall or the second progress bar will be
	 * updated.
	 * 
	 * @param forSimilaritiesBar
	 *            If true overall progress bar will be updated. If false second progress bar
	 * @param progress
	 */
	public void setProgress(boolean forSimilaritiesBar, int progress) {

		if (pbOverall.isDisposed())
			return;

		if (forSimilaritiesBar) {
			if (progress >= 99)
				close();
			else
				pbOverall.setSelection(progress);
		}
		else {

			pbClusterer.setSelection(progress);
		}
	}

	private void buildProgressBar() {

		shell = new Shell();
		shell.setText(algorithmName);
		shell.setImage(GeneralManager.get().getResourceLoader()
			.getImage(shell.getDisplay(), "resources/icons/view/tablebased/clustering.png"));

		// Center shell on screen
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Label label = new Label(composite, SWT.NULL);
		label.setText("Overall progress");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 310;
		label.setLayoutData(gridData);

		pbOverall = new ProgressBar(composite, SWT.SMOOTH);
		pbOverall.setMinimum(0);
		pbOverall.setMaximum(100);
		pbOverall.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		lbProgressBarClusterer = new Label(composite, SWT.NULL);
		lbProgressBarClusterer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//		if (algorithmType == EClustererAlgo.COBWEB_CLUSTERER
//			|| algorithmType == EClustererAlgo.KMEANS_CLUSTERER)
//			pbClusterer = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
//		else
			pbClusterer = new ProgressBar(composite, SWT.SMOOTH);

		pbClusterer.setMinimum(0);
		pbClusterer.setMaximum(100);
		pbClusterer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button cancelButton = new Button(composite, SWT.PUSH);
		cancelButton.setText("Abort");
		cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				GeneralManager.get().getEventPublisher().triggerEvent(new ClustererCanceledEvent());
				shell.close();
			}
		});

		composite.pack();
		shell.pack();
		shell.open();
	}

	private void close() {
		unregisterEventListeners();
		if (!shell.isDisposed())
			shell.close();
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

	@Override
	public void registerEventListeners() {
		clusterProgressListener = new ClusterProgressListener();
		clusterProgressListener.setHandler(this);
		GeneralManager.get().getEventPublisher()
			.addListener(ClusterProgressEvent.class, clusterProgressListener);
		GeneralManager.get().getEventPublisher()
			.addListener(RenameProgressBarEvent.class, clusterProgressListener);
	}

	@Override
	public void unregisterEventListeners() {
		if (clusterProgressListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(clusterProgressListener);
			clusterProgressListener = null;
		}
	}

}
