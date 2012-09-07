/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 * 
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander
 * Lex, Christian Partl, Johannes Kepler University Linz </p>
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.info.dataset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.histogram.RcpGLColorMapperHistogramView;
import org.caleydo.view.histogram.SerializedHistogramView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;

/**
 * Data meta view showing details about a data table.
 * 
 * @author Marc Streit
 */
public class RcpDatasetInfoView
	extends CaleydoRCPViewPart
	implements IDataDomainBasedView<ATableBasedDataDomain> {

	public static String VIEW_TYPE = "org.caleydo.view.info.dataset";

	private ATableBasedDataDomain dataDomain;

	private Label nameLabel;

	private Label recordLabel;

	private Label dimensionLabel;

	private Label sourceLabel;

	private RcpGLColorMapperHistogramView histogramView;

	/**
	 * Constructor.
	 */
	public RcpDatasetInfoView() {
		super();

		eventPublisher = GeneralManager.get().getEventPublisher();
		isSupportView = true;

		try {
			viewContext = JAXBContext.newInstance(SerializedDatasetInfoView.class);
		}
		catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		if (dataDomain == null) {
			dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(
					((ASerializedSingleTablePerspectiveBasedView) serializedView)
							.getDataDomainID());
		}

		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Composite infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayout(new GridLayout(1, false));
		infoComposite.setLayoutData(gridData);

		if (dataDomain == null) {
			nameLabel = new Label(infoComposite, SWT.NONE);
			nameLabel.setText("No data set active");
		}
		else {
			nameLabel = new Label(infoComposite, SWT.NONE);
			recordLabel = new Label(infoComposite, SWT.NONE);
			dimensionLabel = new Label(infoComposite, SWT.NONE);
			sourceLabel = new Label(infoComposite, SWT.NONE);

			ExpandBar bar = new ExpandBar(parentComposite, SWT.V_SCROLL);
			gridData = new GridData(GridData.FILL_BOTH);
			bar.setLayoutData(gridData);

			// Third item
			Composite composite = new Composite(bar, SWT.NONE);
			composite.setLayout(new FillLayout());

			histogramView = new RcpGLColorMapperHistogramView();
			histogramView.setDataDomain(dataDomain);
			SerializedHistogramView serializedHistogramView = new SerializedHistogramView();
			serializedHistogramView.setDataDomainID(dataDomain.getDataDomainID());
			serializedHistogramView
					.setTablePerspectiveKey(((ASerializedSingleTablePerspectiveBasedView) serializedView)
							.getTablePerspectiveKey());

			histogramView.setExternalSerializedView(serializedHistogramView);
			histogramView.createPartControl(composite);
			// Usually the canvas is registered to the GL2 animator in the
			// PartListener. Because the GL2 histogram is no usual RCP view we
			// have to do it on our own
			GeneralManager.get().getViewManager()
					.registerGLCanvasToAnimator(histogramView.getGLCanvas());
			ExpandItem item2 = new ExpandItem(bar, SWT.NONE, 0);
			item2.setText("Histogram");
			item2.setHeight(200);
			item2.setControl(composite);
			item2.setExpanded(true);

			bar.setSpacing(2);
		
			updateDataSetInfo();
		}

		parent.layout();
	}

	@Override
	public void setDataDomain(ATableBasedDataDomain dataDomain) {

		// Do nothing if new datadomain is the same as the current one
		if (dataDomain == this.dataDomain)
			return;

		this.dataDomain = dataDomain;
		ASerializedSingleTablePerspectiveBasedView dcSerializedView = (ASerializedSingleTablePerspectiveBasedView) serializedView;
		dcSerializedView.setDataDomainID(dataDomain.getDataDomainID());
		TablePerspective container = dataDomain.getDefaultTablePerspective();
		dcSerializedView.setTablePerspectiveKey(container.getTablePerspectiveKey());

		updateDataSetInfo();
	}
	
	private void updateDataSetInfo() {
		nameLabel.setText("Name: " + dataDomain.getLabel());

		recordLabel.setText(dataDomain.getRecordDenomination(true, true) + ": "
				+ dataDomain.getTable().getMetaData().depth());

		dimensionLabel.setText(dataDomain.getDimensionDenomination(true, true) + ": "
				+ dataDomain.getTable().getMetaData().size());

		sourceLabel.setText("Source: "
				+ dataDomain.getDataSetDescription().getDataSourcePath());
		
		((GLHistogram)histogramView.getGLView()).setHistogram(dataDomain.getDefaultTablePerspective().getContainerStatistics().getHistogram());
		((GLHistogram)histogramView.getGLView()).setDisplayListDirty();
	}

	@Override
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDatasetInfoView();
		determineDataConfiguration(serializedView, false);
	}
}