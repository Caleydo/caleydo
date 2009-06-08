package org.caleydo.rcp.progress;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.ClustererCanceledEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.EClustererAlgo;
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

public class ClusteringProgressBar
	implements IListenerOwner {

	private ProgressBar pbOverall;
	private ProgressBar pbClusterer;
	private EClustererAlgo algorithmType;
	private ClusterProgressListener clusterProgressListener;

	private Shell shell;
	private Label lbProgressBarClusterer;

	public ClusteringProgressBar(EClustererAlgo algorithmType) {
		this.algorithmType = algorithmType;
	}

	public void run() {
		buildProgressBar();

		clusterProgressListener = new ClusterProgressListener();
		clusterProgressListener.setHandler(this);
		GeneralManager.get().getEventPublisher().addListener(ClusterProgressEvent.class,
			clusterProgressListener);
		GeneralManager.get().getEventPublisher().addListener(RenameProgressBarEvent.class,
			clusterProgressListener);

	}

	public void setProgressBarLabel(String stProgressBarLable) {
		if (lbProgressBarClusterer.isDisposed())
			return;
		lbProgressBarClusterer.setText(stProgressBarLable);
	}

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
		shell.setText(algorithmType.getName());
		shell.setImage(GeneralManager.get().getResourceLoader().getImage(shell.getDisplay(),
			"resources/icons/view/storagebased/clustering.png"));

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
