package org.caleydo.util.r.filter;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.RecordFilter;
import org.caleydo.core.data.filter.RecordMetaFilter;
import org.caleydo.core.data.filter.event.RemoveRecordFilterEvent;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.RecordVirtualArray;
import org.caleydo.core.data.virtualarray.delta.RecordVADelta;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;

public class FilterRepresentationTwoSidedTTest extends
		AFilterRepresentation<RecordVADelta, RecordFilter> {

	private final static String TITLE = "Two-sided T-Test Filter";
	
	private DataTable set1;
	private DataTable set2;

	private float pValue = 1f;

	public boolean create() {
		
		if( !super.create() )
			return false;

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				((Shell)parentComposite).setText(TITLE);
				
				final Slider pValueSlider = new Slider(parentComposite, SWT.HORIZONTAL);

				final Label pValueLabel = new Label(parentComposite, SWT.NULL);
				pValueLabel.setText("p-Value: " + pValue);
				pValueSlider.setMinimum(0);
				pValueSlider.setMaximum(1000);
				pValueSlider.setIncrement(5);
				pValueSlider.setPageIncrement(1);
				pValueSlider.setSelection((int) (pValue * 1000));
				pValueSlider.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseUp(MouseEvent e) {
						pValue = pValueSlider.getSelection() / 1000.00f;
						pValueLabel.setText("p-Value: " + pValue);
						parentComposite.pack();

						isDirty = true;

						// if (reducedVA != null)
						// reducedNumberLabel.setText("# Genes: " +
						// reducedVA.size());

						// parentComposite.layout();
					}
				});
				
				final Button applyFilterButton = new Button(parentComposite, SWT.PUSH);
				applyFilterButton.setText("Apply");
				applyFilterButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {

						applyFilter();
					}
				});
			}
		});
		
		addOKCancel();
		
		return true;
	}

	@Override
	protected void createVADelta() {

		if (filter instanceof RecordMetaFilter) {
			for (RecordFilter subFilter : ((RecordMetaFilter) filter).getFilterList()) {

				createVADelta(subFilter);
			}
		} else
			createVADelta(filter);
	}

	private void createVADelta(RecordFilter subFilter) {

		RecordVADelta recordVADelta = new RecordVADelta(DataTable.RECORD, subFilter
				.getDataDomain().getRecordIDType());
		RecordVirtualArray recordVA = subFilter.getDataDomain()
				.getRecordFilterManager().getBaseVA();

		ArrayList<Double> tTestResult = ((FilterRepresentationTwoSidedTTest) subFilter
				.getFilterRep())
				.getDataTable1()
				.getStatisticsResult()
				.getTwoSidedTTestResult(
						((FilterRepresentationTwoSidedTTest) subFilter.getFilterRep())
								.getDataTable2());

		for (int recordIndex = 0; recordIndex < recordVA.size(); recordIndex++) {

			if (tTestResult != null && tTestResult.get(recordIndex) > pValue)
				recordVADelta
						.add(VADeltaItem.removeElement(recordVA.get(recordIndex)));
		}
		subFilter.setVADelta(recordVADelta);
	}

	@Override
	protected void triggerRemoveFilterEvent() {
		RemoveRecordFilterEvent filterEvent = new RemoveRecordFilterEvent();
		filterEvent.setDataDomainID(filter.getDataDomain().getDataDomainID());
		filterEvent.setFilter(filter);
		GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
	}

	public void setDataTable1(DataTable set1) {
		this.set1 = set1;
	}

	public void setDataTable2(DataTable set2) {
		this.set2 = set2;
	}

	public DataTable getDataTable1() {
		return set1;
	}

	public DataTable getDataTable2() {
		return set2;
	}
	
	@Override
	protected void applyFilter() {
		if (isDirty)
		{
			createVADelta();
			filter.updateFilterManager();
		}
		isDirty = false;
	}
}
