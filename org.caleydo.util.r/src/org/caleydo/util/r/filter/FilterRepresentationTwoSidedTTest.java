/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.util.r.filter;

import java.util.ArrayList;

import org.caleydo.core.data.filter.Filter;
import org.caleydo.core.data.filter.MetaFilter;
import org.caleydo.core.data.filter.event.RemoveFilterEvent;
import org.caleydo.core.data.filter.representation.AFilterRepresentation;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.data.virtualarray.delta.VADeltaItem;
import org.caleydo.core.data.virtualarray.delta.VirtualArrayDelta;
import org.caleydo.core.event.EventPublisher;
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

public class FilterRepresentationTwoSidedTTest
 extends AFilterRepresentation {

	private final static String TITLE = "Two-sided T-Test Filter";

	private TablePerspective tablePerspective1;
	private TablePerspective tablePerspective2;

	private float pValue = 1f;

	@Override
	public synchronized boolean create() {

		if (!super.create())
			return false;

		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {

				((Shell) parentComposite).setText(TITLE);

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

		if (filter instanceof MetaFilter) {
			for (Filter subFilter : ((MetaFilter) filter).getFilterList()) {

				createVADelta(subFilter);
			}
		} else
			createVADelta(filter);
	}

	private void createVADelta(Filter subFilter) {

		VirtualArrayDelta recordVADelta = new VirtualArrayDelta(tablePerspective1
				.getRecordPerspective().getPerspectiveID(), subFilter.getDataDomain()
				.getRecordIDType());
		VirtualArray recordVA = tablePerspective1.getRecordPerspective()
				.getVirtualArray();

		ArrayList<Double> tTestResult = ((FilterRepresentationTwoSidedTTest) subFilter
				.getFilterRep())
				.getTablePerspective1()
				.getContainerStatistics()
				.getTTest()
				.getTwoSidedTTestResult(
						((FilterRepresentationTwoSidedTTest) subFilter.getFilterRep())
								.getTablePerspective2());

		for (int recordIndex = 0; recordIndex < recordVA.size(); recordIndex++) {

			if (tTestResult != null && tTestResult.get(recordIndex) > pValue)
				recordVADelta.add(VADeltaItem.removeElement(recordVA.get(recordIndex)));
		}
		subFilter.setVADelta(recordVADelta);
	}

	@Override
	protected void triggerRemoveFilterEvent() {
		RemoveFilterEvent filterEvent = new RemoveFilterEvent();
		filterEvent.setEventSpace(filter.getDataDomain().getDataDomainID());
		filterEvent.setFilter(filter);
		
		EventPublisher.trigger(filterEvent);
	}

	@Override
	protected void applyFilter() {
		if (isDirty) {
			createVADelta();
			filter.updateFilterManager();
		}
		isDirty = false;
	}

	public void setTablePerspective1(TablePerspective tablePerspective1) {
		this.tablePerspective1 = tablePerspective1;
	}

	/**
	 * @return the tablePerspective1, see {@link #tablePerspective1}
	 */
	public TablePerspective getTablePerspective1() {
		return tablePerspective1;
	}

	public void setTablePerspective2(TablePerspective tablePerspective2) {
		this.tablePerspective2 = tablePerspective2;
	}

	/**
	 * @return the tablePerspective2, see {@link #tablePerspective2}
	 */
	public TablePerspective getTablePerspective2() {
		return tablePerspective2;
	}
}
