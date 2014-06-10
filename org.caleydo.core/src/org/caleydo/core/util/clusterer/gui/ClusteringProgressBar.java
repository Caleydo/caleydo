/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.core.util.clusterer.gui;

import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.EventPublisher;
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

/**
 * Progress bar visualizing the progress during a cluster process.
 *
 * @author
 */
public class ClusteringProgressBar implements Runnable {

	private final String algorithmName;

	private Shell shell;

	private Label label;
	private ProgressBar overall;
	private ProgressBar clusterer;

	private final EventListenerManager eventManager = EventListenerManagers.createSWTDirect();


	public ClusteringProgressBar(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	@Override
	public void run() {
		buildProgressBar();

		eventManager.register(this);
	}

	/**
	 * Sets the label of the second progress bar
	 *
	 * @param label
	 */
	public void setProgressBarLabel(String label) {
		if (this.label == null || this.label.isDisposed())
			return;
		this.label.setText(label);
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

		if (overall == null || overall.isDisposed())
			return;

		if (forSimilaritiesBar) {
			if (progress >= 99)
				close();
			else
				overall.setSelection(progress);
		}
		else {

			clusterer.setSelection(progress);
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

		Label progressLabel = new Label(composite, SWT.NULL);
		progressLabel.setText("Overall progress");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 310;
		progressLabel.setLayoutData(gridData);

		overall = new ProgressBar(composite, SWT.SMOOTH);
		overall.setMinimum(0);
		overall.setMaximum(100);
		overall.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.label = new Label(composite, SWT.NULL);
		this.label.setText("");
		this.label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

//		if (algorithmType == EClustererAlgo.COBWEB_CLUSTERER
//			|| algorithmType == EClustererAlgo.KMEANS_CLUSTERER)
//			pbClusterer = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
//		else
		clusterer = new ProgressBar(composite, SWT.SMOOTH);

		clusterer.setMinimum(0);
		clusterer.setMaximum(100);
		clusterer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button cancelButton = new Button(composite, SWT.PUSH);
		cancelButton.setText("Abort");
		cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				
				EventPublisher.trigger(new ClustererCanceledEvent());
				shell.close();
			}
		});

		composite.pack();
		shell.pack();
		shell.open();
	}

	private void close() {
		eventManager.unregisterAll();
		if (!shell.isDisposed())
			shell.close();
	}

	@ListenTo
	private void onProgressEvent(ClusterProgressEvent event) {
		setProgress(event.isForSimilaritiesBar(), event.getPercentCompleted());
	}

	@ListenTo
	private void onRenameEvent(RenameProgressBarEvent event) {
		setProgressBarLabel(event.getProgressbarTitle());
	}
}
