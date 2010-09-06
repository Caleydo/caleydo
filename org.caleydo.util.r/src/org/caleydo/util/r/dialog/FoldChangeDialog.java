package org.caleydo.util.r.dialog;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.set.statistics.FoldChangeSettings;
import org.caleydo.core.data.collection.set.statistics.FoldChangeSettings.FoldChangeEvaluator;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.event.data.StatisticsResultFinishedEvent;
import org.caleydo.core.util.logging.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public class FoldChangeDialog extends Dialog {

	private ISet set1;
	private ISet set2;

	/**
	 * @param parent
	 */
	public FoldChangeDialog(Shell parent, ISet set1, ISet set2) {
		super(parent);

		this.set1 = set1;
		this.set2 = set2;
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return
	 */
	public void open() {

		try {
			GeneralManager.get().getRStatisticsPerformer().foldChange(set1, set2);
		} catch (Exception e) {

			Logger.log(new Status(IStatus.WARNING, this.toString(),
					"R Statistics plugin could not be loaded. The statistics reduction will be skipped."));
			return;
		}

		Shell parent = getParent();
		parent.setSize(600, 200);
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER
				| SWT.APPLICATION_MODAL);
		shell.setText("Fold change reduction");

		shell.setLayout(new RowLayout(SWT.VERTICAL));

		final Slider slider = new Slider(shell, SWT.HORIZONTAL);

		final Label label = new Label(shell, SWT.NULL);
		label.setText("                                                                                                        ");

		int initialFoldchange = 2;
		final Label foldChangeLabel = new Label(shell, SWT.NULL);
		foldChangeLabel.setText("" + initialFoldchange);

		final Button[] evaluatorCheckBox = new Button[3];

		evaluatorCheckBox[0] = new Button(shell, SWT.CHECK);
		evaluatorCheckBox[0].setSelection(true);
		evaluatorCheckBox[0].setText("Greater (up regulated)");

		evaluatorCheckBox[1] = new Button(shell, SWT.CHECK);
		evaluatorCheckBox[1].setText("Less (down regulated)");

		evaluatorCheckBox[2] = new Button(shell, SWT.CHECK);
		evaluatorCheckBox[2].setText("Equal");

		evaluatorCheckBox[0].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				evaluatorCheckBox[2].setSelection(false);
			}
		});

		evaluatorCheckBox[1].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				evaluatorCheckBox[2].setSelection(false);
			}
		});

		evaluatorCheckBox[2].addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				evaluatorCheckBox[1].setSelection(false);
				evaluatorCheckBox[0].setSelection(false);
			}
		});

		slider.setMinimum(0);
		slider.setMaximum(50);
		slider.setIncrement(1);
		slider.setPageIncrement(10);
		slider.setSelection(initialFoldchange * 10);

		slider.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				Double foldChangeRatio = slider.getSelection() / 10d;
				foldChangeLabel.setText("" + foldChangeRatio);

				if (evaluatorCheckBox[0].getSelection() == true) {
					FoldChangeSettings foldChangeSettings = new FoldChangeSettings(
							foldChangeRatio, FoldChangeEvaluator.GREATER);

					set1.getStatisticsResult().setFoldChangeSettings(set2,
							foldChangeSettings);
					set2.getStatisticsResult().setFoldChangeSettings(set1,
							foldChangeSettings);
				}

				if (evaluatorCheckBox[1].getSelection() == true) {
					FoldChangeSettings foldChangeSettings = new FoldChangeSettings(
							foldChangeRatio, FoldChangeEvaluator.LESS);

					set1.getStatisticsResult().setFoldChangeSettings(set2,
							foldChangeSettings);
					set2.getStatisticsResult().setFoldChangeSettings(set1,
							foldChangeSettings);
				}

				if (evaluatorCheckBox[2].getSelection() == true) {
					FoldChangeSettings foldChangeSettings = new FoldChangeSettings(
							foldChangeRatio, FoldChangeEvaluator.GREATER);

					set1.getStatisticsResult().setFoldChangeSettings(set2,
							foldChangeSettings);
					set2.getStatisticsResult().setFoldChangeSettings(set1,
							foldChangeSettings);
				}

				int reducedNumberOfElements = set1.getStatisticsResult()
						.getElementNumberOfFoldChangeReduction(set2);

				label.setText("The fold change reduced results in a dataset of the size "
						+ reducedNumberOfElements);
				shell.layout();
			}

			@Override
			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("Ok");
		// buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				triggerStatisticsFinishedEvent();
				shell.dispose();
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.dispose();
			}
		});

		shell.pack();
		shell.open();

		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	private void triggerStatisticsFinishedEvent() {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView("org.caleydo.view.statistics");
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ArrayList<ISet> sets = new ArrayList<ISet>();
		sets.add(set1);
		sets.add(set2);
		StatisticsResultFinishedEvent event = new StatisticsResultFinishedEvent(sets);
		event.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(event);
	}

	// public static void main(String[] args) {
	// Shell shell = new Shell();
	// FoldChangeDialog dialog = new FoldChangeDialog(shell, null);
	// dialog.open();
	// }
}
