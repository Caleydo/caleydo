package org.caleydo.util.r.filter;

import java.util.ArrayList;

import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.filter.ContentFilter;
import org.caleydo.core.data.filter.ContentMetaFilter;
import org.caleydo.core.data.filter.event.RemoveContentFilterEvent;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.virtualarray.ContentVirtualArray;
import org.caleydo.core.data.virtualarray.delta.ContentVADelta;
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
		AFilterRepresentation<ContentVADelta, ContentFilter> {

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

		if (filter instanceof ContentMetaFilter) {
			for (ContentFilter subFilter : ((ContentMetaFilter) filter).getFilterList()) {

				createVADelta(subFilter);
			}
		} else
			createVADelta(filter);
	}

	private void createVADelta(ContentFilter subFilter) {

		ContentVADelta contentVADelta = new ContentVADelta(DataTable.CONTENT, subFilter
				.getDataDomain().getContentIDType());
		ContentVirtualArray contentVA = subFilter.getDataDomain()
				.getContentFilterManager().getBaseVA();

		ArrayList<Double> tTestResult = ((FilterRepresentationTwoSidedTTest) subFilter
				.getFilterRep())
				.getSet1()
				.getStatisticsResult()
				.getTwoSidedTTestResult(
						((FilterRepresentationTwoSidedTTest) subFilter.getFilterRep())
								.getSet2());

		for (int contentIndex = 0; contentIndex < contentVA.size(); contentIndex++) {

			if (tTestResult != null && tTestResult.get(contentIndex) > pValue)
				contentVADelta
						.add(VADeltaItem.removeElement(contentVA.get(contentIndex)));
		}
		subFilter.setVADelta(contentVADelta);
	}

	@Override
	protected void triggerRemoveFilterEvent() {
		RemoveContentFilterEvent filterEvent = new RemoveContentFilterEvent();
		filterEvent.setDataDomainID(filter.getDataDomain().getDataDomainID());
		filterEvent.setFilter(filter);
		GeneralManager.get().getEventPublisher().triggerEvent(filterEvent);
	}

	public void setSet1(DataTable set1) {
		this.set1 = set1;
	}

	public void setSet2(DataTable set2) {
		this.set2 = set2;
	}

	public DataTable getSet1() {
		return set1;
	}

	public DataTable getSet2() {
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
