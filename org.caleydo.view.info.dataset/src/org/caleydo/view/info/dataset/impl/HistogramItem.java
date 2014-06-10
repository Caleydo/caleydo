/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.info.dataset.impl;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.view.ViewManager;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.histogram.RcpGLColorMapperHistogramView;
import org.caleydo.view.histogram.SerializedHistogramView;
import org.caleydo.view.info.dataset.spi.ITablePerspectiveDataSetItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;

/**
 * histogram of the current data
 * 
 * @author Samuel Gratzl
 * 
 */
public class HistogramItem implements ITablePerspectiveDataSetItem {
	private ExpandItem histogramItem;
	private RcpGLColorMapperHistogramView histogramView;

	@Override
	public ExpandItem create(ExpandBar expandBar) {
		histogramItem = new ExpandItem(expandBar, SWT.NONE);
		histogramItem.setText("Histogram");
		histogramItem.setHeight(200);
		Composite wrapper = new Composite(expandBar, SWT.NONE);
		wrapper.setLayout(new FillLayout());
		histogramItem.setControl(wrapper);

		histogramItem.setExpanded(false);
		histogramItem.getControl().setEnabled(false);

		return histogramItem;
	}

	@Override
	public void update(IDataDomain dataDomain, TablePerspective tablePerspective) {
		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain tableBasedDD = (ATableBasedDataDomain) dataDomain;
			if (!tableBasedDD.getTable().isDataHomogeneous() && tablePerspective == null) {
				histogramItem.getControl().setEnabled(false);
				histogramItem.setExpanded(false);
				return;
			}

			histogramItem.getControl().setEnabled(true);
			histogramItem.setExpanded(true);

			if (histogramView == null) {
				histogramView = new RcpGLColorMapperHistogramView();
				histogramView.setDataDomain(tableBasedDD);
				if (tablePerspective != null)
					histogramView.setTablePerspective(tablePerspective);

				SerializedHistogramView serializedHistogramView = new SerializedHistogramView();
				serializedHistogramView.setDataDomainID(dataDomain.getDataDomainID());

				histogramView.setExternalSerializedView(serializedHistogramView);
				histogramView.createPartControl((Composite) histogramItem.getControl());
				
				// Usually the canvas is registered to the GL2 animator in the
				// PartListener. Because the GL2 histogram is no usual RCP view
				// we
				// have to do it on our own
				ViewManager.get().registerGLCanvasToAnimator(histogramView.getGLCanvas());
				((Composite) histogramItem.getControl()).layout();
			}

			// If the default table perspective does not exist yet, we
			// create it and set it to private so that it does not show up
			// in the DVI
			if (!tableBasedDD.hasTablePerspective(tableBasedDD.getTable().getDefaultRecordPerspective(false)
					.getPerspectiveID(), tableBasedDD.getTable().getDefaultDimensionPerspective(false)
					.getPerspectiveID())) {
				tableBasedDD.getDefaultTablePerspective().setPrivate(true);
			}
			histogramView.setDataDomain(tableBasedDD);
			((GLHistogram) histogramView.getGLView()).setDataDomain(tableBasedDD);
			histogramView.setTablePerspective(tablePerspective);
			((GLHistogram) histogramView.getGLView()).setTablePerspective(tablePerspective);

			((GLHistogram) histogramView.getGLView()).setDisplayListDirty();

		} else {
			histogramItem.setExpanded(false);
			histogramItem.getControl().setEnabled(false);
		}
	}
}
