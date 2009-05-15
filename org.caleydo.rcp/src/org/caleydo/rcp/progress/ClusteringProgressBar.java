package org.caleydo.rcp.progress;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.ClustererCanceledEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class ClusteringProgressBar
	implements IListenerOwner {

	private ProgressBar pbSimilarity;
	private ProgressBar pbClusterer;
	private EClustererAlgo algorithmType;
	private ClusterProgressListener clusterProgressListener;
	private Shell shell;

	public ClusteringProgressBar(EClustererAlgo algorithmType) {
		this.algorithmType = algorithmType;
	}

	public void run() {
		buildProgressBar();

		clusterProgressListener = new ClusterProgressListener();
		clusterProgressListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(ClusterProgressEvent.class,
			clusterProgressListener);

	}

	public void setProgress(boolean forSimilaritiesBar, int progress) {

		if (forSimilaritiesBar) {
			pbSimilarity.setSelection(progress);
		}
		else {
			if (progress >= 99)
				close();
			else
				pbClusterer.setSelection(progress);
		}
	}

	private void buildProgressBar() {

		shell = new Shell();

		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		composite.setLayout(layout);
		composite.setFocus();

		Group progressBarGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		progressBarGroup.setText(algorithmType.toString());
		progressBarGroup.setLayout(new RowLayout(1));
		GridData gridData = new GridData(GridData.FILL_VERTICAL);
		progressBarGroup.setLayoutData(gridData);

		Label label = new Label(progressBarGroup, SWT.NULL);
		label.setText("Determine similarties in progress");
		label.setAlignment(SWT.RIGHT);

		pbSimilarity = new ProgressBar(progressBarGroup, SWT.SMOOTH);
		pbSimilarity.setMinimum(0);
		pbSimilarity.setMaximum(100);

		Label label2 = new Label(progressBarGroup, SWT.NULL);
		label2.setText("Clusterer in progress");
		label2.setAlignment(SWT.RIGHT);

		pbClusterer = new ProgressBar(progressBarGroup, SWT.SMOOTH);
		pbClusterer.setMinimum(0);
		pbClusterer.setMaximum(100);

		Button cancelButton = new Button(progressBarGroup, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.setBounds(20, 35, 40, 25);
		cancelButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				GeneralManager.get().getEventPublisher().triggerEvent(new ClustererCanceledEvent());
			}
		});

		composite.pack();

		shell.pack();
		shell.open();
	}

	private void close() {
		if (clusterProgressListener != null) {
			GeneralManager.get().getEventPublisher().removeListener(clusterProgressListener);
			clusterProgressListener = null;
		}
		if (!shell.isDisposed())
			shell.close();
	}

	@Override
	public void queueEvent(final AEventListener<? extends IListenerOwner> listener, final AEvent event) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				listener.handleEvent(event);
			}
		});
	}

}
