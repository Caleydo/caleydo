/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation.wilcoxon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.enroute.correlation.ShowOverlayEvent;
import org.caleydo.view.enroute.correlation.wilcoxon.WilcoxonUtil.WilcoxonResult;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian
 *
 */
public class WilcoxonAutoResultPage extends WizardPage implements IPageChangedListener {

	private WilcoxonResultsWidget resultsWidget;
	// private Map<Integer, WilcoxonResult> resultsMap = new HashMap<>();
	private List<WilcoxonResult> results = new ArrayList<>();
	private TableViewer resultsList;
	private Text pValueThresholdText;
	private Button enableFilterButton;

	private static class PValueFilter extends ViewerFilter {

		private double threshold;
		private boolean enabled = false;

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (!enabled)
				return true;
			WilcoxonResult result = (WilcoxonResult) element;

			return result.p <= threshold;
		}

		/**
		 * @param threshold
		 *            setter, see {@link threshold}
		 */
		public void setThreshold(double threshold) {
			this.threshold = threshold;
		}

		/**
		 * @param enabled
		 *            setter, see {@link enabled}
		 */
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}

	private static class ResultComparator extends ViewerComparator {
		private int columnIndex;
		private static final int DESCENDING = 1;
		private int direction = DESCENDING;

		public ResultComparator() {
			this.columnIndex = 0;
			direction = DESCENDING;
		}

		public int getDirection() {
			return direction == 1 ? SWT.DOWN : SWT.UP;
		}

		public void setColumn(int index) {
			if (index == this.columnIndex) {
				direction = 1 - direction;
			} else {
				this.columnIndex = index;
				direction = DESCENDING;
			}
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {

			WilcoxonResult r1 = (WilcoxonResult) e1;
			WilcoxonResult r2 = (WilcoxonResult) e2;

			int result = 0;

			switch (columnIndex) {
			case 0:
				result = r1.derivedClassifier.getDataClasses().get(0).name.compareTo(r2.derivedClassifier
						.getDataClasses().get(0).name);
				break;
			case 1:
				result = r1.derivedClassifier.getDataClasses().get(1).name.compareTo(r2.derivedClassifier
						.getDataClasses().get(1).name);
				break;
			case 2:
				result = Double.compare(r1.u, r2.u);
				break;
			case 3:
				result = Double.compare(r1.p, r2.p);
				break;

			default:
				return super.compare(viewer, e1, e2);
			}
			if (direction == DESCENDING)
				return -result;

			return result;
		}
	}

	private PValueFilter filter = new PValueFilter();
	private ResultComparator comparator = new ResultComparator();

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected WilcoxonAutoResultPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));
		// Group summaryGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		// summaryGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// summaryGroup.setLayout(new GridLayout(2, false));
		// summaryGroup.setText("Summary");

		Group classificationsGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		classificationsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		classificationsGroup.setLayout(new GridLayout(2, false));
		classificationsGroup.setText("Classifications");

		enableFilterButton = new Button(classificationsGroup, SWT.CHECK);
		enableFilterButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		enableFilterButton.setText("Only show classifications with P-value " + Character.toString((char) 0x2264));
		enableFilterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				pValueThresholdText.setEnabled(enableFilterButton.getSelection());
				updateFilter();
			}
		});

		pValueThresholdText = new Text(classificationsGroup, SWT.BORDER);
		GridData gd = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gd.widthHint = 150;
		pValueThresholdText.setLayoutData(gd);
		pValueThresholdText.setText("0.05");
		pValueThresholdText.setEnabled(false);
		pValueThresholdText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateFilter();
			}
		});

		resultsList = new TableViewer(classificationsGroup, SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
				| SWT.BORDER);
		Table table = resultsList.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		resultsList.setContentProvider(new ArrayContentProvider());
		resultsList.setInput(results);
		resultsList.addFilter(filter);
		resultsList.setComparator(comparator);

		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		gd.heightHint = 200;
		gd.widthHint = 400;
		resultsList.getControl().setLayoutData(gd);

		TableViewerColumn column = createColumn("Class 1", 0);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WilcoxonResult result = (WilcoxonResult) element;
				return result.derivedClassifier.getDataClasses().get(0).name;
			}
		});

		column = createColumn("Class 2", 1);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WilcoxonResult result = (WilcoxonResult) element;
				return result.derivedClassifier.getDataClasses().get(1).name;
			}
		});

		column = createColumn("U", 2);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WilcoxonResult result = (WilcoxonResult) element;
				return String.format(String.format(Locale.ENGLISH, "%.2f", result.u));
			}
		});

		column = createColumn("P", 3);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				WilcoxonResult result = (WilcoxonResult) element;
				return String.format(String.format(Locale.ENGLISH, "%.6f", result.p));
			}
		});

		resultsList.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				StructuredSelection selection = (StructuredSelection) event.getSelection();
				WilcoxonResult result = (WilcoxonResult) selection.getFirstElement();
				if (result == null)
					return;
				WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();

				if (resultsWidget == null) {
					resultsWidget = new WilcoxonResultsWidget(parentComposite);


				}
				double[] values1 = WilcoxonUtil.getSampleValuesArray(wizard.getTargetInfo(),
						result.derivedClassifier.getClass1IDs());
				double[] values2 = WilcoxonUtil.getSampleValuesArray(wizard.getTargetInfo(),
						result.derivedClassifier.getClass2IDs());
				resultsWidget.updateClassSummary(0, values1, result.derivedClassifier.getDataClasses().get(0));
				resultsWidget.updateClassSummary(1, values2, result.derivedClassifier.getDataClasses().get(1));
				resultsWidget.updateStatistics(result.u, result.p);
				getShell().layout(true, true);
				getShell().pack(true);

				EventPublisher.trigger(new ShowOverlayEvent(wizard.getSourceInfo(), result.classifier
						.getOverlayProvider(), true));
				EventPublisher.trigger(new ShowOverlayEvent(wizard.getTargetInfo(), result.derivedClassifier
						.getOverlayProvider(), false));
			}
		});

		// resultsList = new List(resultsGroup, SWT.BORDER | SWT.V_SCROLL);
		//
		// resultsList.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {

		// }
		// });

		setControl(parentComposite);

	}

	private void updateFilter() {
		filter.setEnabled(enableFilterButton.getSelection());
		try {
			double threshold = Double.parseDouble(pValueThresholdText.getText());
			filter.setThreshold(threshold);
			resultsList.refresh();
		} catch (NumberFormatException e) {

		}
	}

	private TableViewerColumn createColumn(String caption, final int columnIndex) {
		TableViewerColumn viewerColumn = new TableViewerColumn(resultsList, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(caption);
		column.setResizable(true);
		column.setMoveable(false);
		column.setWidth(100);
		column.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				comparator.setColumn(columnIndex);
				int dir = comparator.getDirection();
				resultsList.getTable().setSortDirection(dir);
				resultsList.getTable().setSortColumn(column);
				resultsList.refresh();
			}
		});
		return viewerColumn;
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			if (resultsWidget != null) {
				resultsWidget.dispose();
				resultsWidget = null;
			}
			WilcoxonRankSumTestWizard wizard = (WilcoxonRankSumTestWizard) getWizard();
			results = WilcoxonUtil.calcAllWilcoxonCombinations(wizard.getSourceInfo(), wizard.getTargetInfo());

			resultsList.setInput(results);
			resultsList.refresh();
			// resultsMap.clear();
			// int index = 0;
			// for (WilcoxonResult result : results) {
			// resultsList.add(String.format(Locale.ENGLISH, "U: %.2f, P: %.5f", result.u, result.p));
			// resultsMap.put(index, result);
			// index++;
			// }
			getShell().layout(true, true);
			getShell().pack(true);
		}

	}

	@Override
	public boolean isPageComplete() {
		return true;
	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((WilcoxonRankSumTestWizard) getWizard()).getAutoTargetDataCellPage();
	}

	@Override
	public IWizardPage getNextPage() {
		return null;
	}

}
