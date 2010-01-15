package org.caleydo.rcp.progress;

import org.caleydo.core.manager.event.AEvent;
import org.caleydo.core.manager.event.AEventListener;
import org.caleydo.core.manager.event.IListenerOwner;
import org.caleydo.core.manager.event.data.ClusterProgressEvent;
import org.caleydo.core.manager.event.data.ClustererCanceledEvent;
import org.caleydo.core.manager.event.data.RenameProgressBarEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.util.clusterer.EClustererAlgo;
import org.caleydo.core.util.clusterer.EClustererType;
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
	private EClustererAlgo algorithmType;
	// private EClustererType clusterType;;
	private ClusterProgressListener clusterProgressListener;

	// private boolean bOpenDendrogram = true;
	// private boolean bOpenRadialHierarchy = true;

	private Shell shell;
	private Label lbProgressBarClusterer;

	public ClusteringProgressBar(EClustererAlgo algorithmType, EClustererType clustererType) {
		this.algorithmType = algorithmType;
		// this.clusterType = clustererType;
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

		if (algorithmType == EClustererAlgo.COBWEB_CLUSTERER
			|| algorithmType == EClustererAlgo.KMEANS_CLUSTERER)
			pbClusterer = new ProgressBar(composite, SWT.SMOOTH | SWT.INDETERMINATE);
		else
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

		// in case of hierarchical clustering a new shell should be opened
		// if (algorithmType == EClustererAlgo.TREE_CLUSTERER
		// || algorithmType == EClustererAlgo.COBWEB_CLUSTERER) {
		//
		// GeneralManager.get().getGUIBridge().getDisplay().asyncExec(new Runnable() {
		// public void run() {
		// try {
		//
		// shell = new Shell();
		// shell.setText(algorithmType.getName());
		// shell.setImage(GeneralManager.get().getResourceLoader().getImage(shell.getDisplay(),
		// "resources/icons/view/storagebased/clustering.png"));
		//
		// // Center shell on screen
		// Monitor primary = shell.getDisplay().getPrimaryMonitor();
		// Rectangle bounds = primary.getBounds();
		// Rectangle rect = shell.getBounds();
		// int x = bounds.x + (bounds.width - rect.width) / 2;
		// int y = bounds.y + (bounds.height - rect.height) / 2;
		// shell.setLocation(x, y);
		//
		// Composite composite = new Composite(shell, SWT.NONE);
		// composite.setLayout(new GridLayout(1, false));
		//
		// Label label = new Label(composite, SWT.NULL);
		// label.setText("Do you want to explore the hierarchy?");
		// GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		// gridData.widthHint = 310;
		// label.setLayoutData(gridData);
		//
		// Composite viewComposite = new Composite(composite, SWT.SHADOW_ETCHED_IN);
		// viewComposite.setLayout(new RowLayout());
		//
		// Composite selectedViewsComposite = new Composite(viewComposite, SWT.SHADOW_ETCHED_IN);
		// selectedViewsComposite.setLayout(new GridLayout());
		//
		// final Button[] buttonViews = new Button[2];
		//
		// buttonViews[0] = new Button(selectedViewsComposite, SWT.CHECK);
		// buttonViews[0].setSelection(true);
		// buttonViews[0].setText("Open Dendrogram");
		// buttonViews[0].setBounds(10, 5, 75, 30);
		//
		// buttonViews[1] = new Button(selectedViewsComposite, SWT.CHECK);
		// buttonViews[1].setSelection(true);
		// buttonViews[1].setText("Open Radial Hierarchy");
		// buttonViews[1].setBounds(10, 5, 75, 30);
		//
		// buttonViews[0].addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// bOpenDendrogram = (bOpenDendrogram == true) ? false : true;
		// }
		// });
		//
		// buttonViews[1].addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// bOpenRadialHierarchy = (bOpenRadialHierarchy == true) ? false : true;
		// }
		// });
		//
		// Composite buttonComposite = new Composite(viewComposite, SWT.SHADOW_ETCHED_IN);
		// buttonComposite.setLayout(new RowLayout());
		//
		// Button okButton = new Button(buttonComposite, SWT.PUSH);
		// okButton.setText("Yes");
		// okButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// try {
		// if (clusterType == EClustererType.GENE_CLUSTERING) {
		// if (bOpenRadialHierarchy)
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage().showView(GLRadialHierarchyView.ID);
		// if (bOpenDendrogram)
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage().showView(GLDendrogramHorizontalView.ID);
		// }
		// else if (clusterType == EClustererType.EXPERIMENTS_CLUSTERING) {
		// if (bOpenDendrogram)
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage().showView(GLDendrogramVerticalView.ID);
		// }
		// else if (clusterType == EClustererType.BI_CLUSTERING) {
		// if (bOpenRadialHierarchy)
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage().showView(GLRadialHierarchyView.ID);
		// if (bOpenDendrogram) {
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage().showView(GLDendrogramHorizontalView.ID);
		// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
		// .getActivePage().showView(GLDendrogramVerticalView.ID);
		//
		// }
		// }
		// UpdateViewEvent event = new UpdateViewEvent();
		// event.setSender(this);
		// GeneralManager.get().getEventPublisher().triggerEvent(event);
		//
		// }
		// catch (PartInitException e1) {
		// e1.printStackTrace();
		// }
		// shell.close();
		//
		// }
		// });
		//
		// Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		// cancelButton.setText("No");
		// cancelButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// shell.close();
		// }
		// });
		//
		// composite.pack();
		// shell.pack();
		// shell.open();
		//
		// }
		// catch (Exception e) {
		//
		// }
		// }
		// });
		// }
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
