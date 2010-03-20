package org.caleydo.rcp.util;

import java.util.ArrayList;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.mapping.EIDCategory;
import org.caleydo.core.data.selection.ContentVAType;
import org.caleydo.core.data.selection.ContentVirtualArray;
import org.caleydo.core.manager.event.data.ReplaceContentVAInUseCaseEvent;
import org.caleydo.core.manager.event.view.grouper.CompareGroupsEvent;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.rcp.Activator;
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

public class SetsPreFilterDialog
	extends Dialog {

	private ArrayList<ISet> setsToCompare;

	private float foldchange = 0;

	private ContentVirtualArray foldChangeFilteredVA;

	/**
	 * @param parent
	 */
	public SetsPreFilterDialog(Shell parent, ArrayList<ISet> setsToCompare) {
		super(parent);

		this.setsToCompare = setsToCompare;
	}

	/**
	 * @param parent
	 * @param style
	 */
	public SetsPreFilterDialog(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Makes the dialog visible.
	 * 
	 * @return
	 */
	public void open() {

		try {
			GeneralManager.get().getRStatisticsPerformer().foldChange(setsToCompare.get(0), setsToCompare.get(1));
			GeneralManager.get().getRStatisticsPerformer().foldChange(setsToCompare.get(2), setsToCompare.get(3));
//			GeneralManager.get().getRStatisticsPerformer().twoSidedTTest(setsToCompare);
		}
		catch (Exception e) {

			GeneralManager.get().getLogger().log(
				new Status(IStatus.WARNING, Activator.PLUGIN_ID,
					"R Statistics plugin could not be loaded. The statistics reduction will be skipped."));
			triggerCompareGroupsEvent();
			return;
		}

		Shell parent = getParent();
		parent.setSize(600, 200);
		final Shell shell = new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
		shell.setText("Fold change reduction");

		shell.setLayout(new RowLayout(SWT.VERTICAL));

		final Slider slider = new Slider(shell, SWT.HORIZONTAL);

		final Label label = new Label(shell, SWT.NULL);
		label
			.setText("                                                                                                              ");

		final Label foldChangeLabel = new Label(shell, SWT.NULL);
		foldChangeLabel.setText("2");

		slider.setMinimum(0);
		slider.setMaximum(50);
		slider.setIncrement(5);
		slider.setPageIncrement(10);
		slider.setSelection(20);

		slider.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent e) {
				foldchange = (float) slider.getSelection() / 10f;
				foldChangeLabel.setText("" + foldchange);

				ISet set1 = setsToCompare.get(0);
				ISet set2 = setsToCompare.get(1);

				ContentVirtualArray foldChangeFilteredVA1 = set1.getStatisticsResult().getVABasedOnFoldChangeResult(set2, foldchange);
				
				ISet set3 = setsToCompare.get(2);
				ISet set4 = setsToCompare.get(3);

				ContentVirtualArray foldChangeFilteredVA2 = set3.getStatisticsResult().getVABasedOnFoldChangeResult(set4, foldchange);
				
				foldChangeFilteredVA = intersectContentVAs(foldChangeFilteredVA1, foldChangeFilteredVA2);
				
				label.setText("The fold change reduced results in a dataset of the size "
					+ foldChangeFilteredVA.size());
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

		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// pValue = (float) slider.getSelection() / 100f;
				//
				// ISet set1 = setsToCompare.get(0);
				// ISet set2 = setsToCompare.get(1);
				//
				// pValueFilteredVA = set1.getStatisticsResult().getVABasedOnCompareResult(set2, pValue);
				// label.setText("The current p-Value selection would reduce your dataset to "
				// + pValueFilteredVA.size());
			}
		});

		final Button buttonOK = new Button(shell, SWT.PUSH);
		buttonOK.setText("Ok");
		// buttonOK.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		Button buttonCancel = new Button(shell, SWT.PUSH);
		buttonCancel.setText("Cancel");
		buttonOK.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.dispose();
				performReduction();
				shell.dispose();
				triggerCompareGroupsEvent();
			}
		});

		buttonCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				shell.dispose();
				triggerCompareGroupsEvent();
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

	private void performReduction() {

		ReplaceContentVAInUseCaseEvent event = null;

		for (ISet set : setsToCompare) {
			event =
				new ReplaceContentVAInUseCaseEvent(set, EIDCategory.GENE, ContentVAType.CONTENT,
					foldChangeFilteredVA);
			event.setSender(this);
			GeneralManager.get().getEventPublisher().triggerEvent(event);
		}
	}

	private void triggerCompareGroupsEvent() {

		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
				"org.caleydo.view.compare");
		}
		catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CompareGroupsEvent compareGroupsEvent = new CompareGroupsEvent(setsToCompare);
		compareGroupsEvent.setSender(this);
		GeneralManager.get().getEventPublisher().triggerEvent(compareGroupsEvent);
	}

	// public static void main(String[] args) {
	// Shell shell = new Shell();
	// SetsPreFilterDialog dialog = new SetsPreFilterDialog(shell, null);
	// }
	
	private ContentVirtualArray intersectContentVAs(ContentVirtualArray va1, ContentVirtualArray va2) {
		
		ContentVirtualArray filteredVA = new ContentVirtualArray(ContentVAType.CONTENT);	
		for (int contentID : va1) {
			if (va2.containsElement(contentID) > 0)
				filteredVA.appendUnique(contentID);
		}
		
		return filteredVA;
	}
}
