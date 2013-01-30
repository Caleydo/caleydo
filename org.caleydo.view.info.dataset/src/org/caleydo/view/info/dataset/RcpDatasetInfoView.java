/*******************************************************************************
 * Caleydo - visualization for molecular biology - http://caleydo.org
 *
 * Copyright(C) 2005, 2012 Graz University of Technology, Marc Streit, Alexander Lex, Christian Partl, Johannes Kepler
 * University Linz </p>
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 *******************************************************************************/
package org.caleydo.view.info.dataset;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.DataDomainUpdateEvent;
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
public class RcpDatasetInfoView extends CaleydoRCPViewPart implements IDataDomainBasedView<IDataDomain> {

	public static final String VIEW_TYPE = "org.caleydo.view.info.dataset";

	private IDataDomain dataDomain;

	private Label nameLabel;
	private Label recordLabel;
	private Label dimensionLabel;

	private Composite infoComposite;

	private Composite histogramComposite;

	private RcpGLColorMapperHistogramView histogramView;

	private boolean isGUIInitialized = false;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

	private ExpandBar histogramExpandBar;

	/**
	 * Constructor.
	 */
	public RcpDatasetInfoView() {
		super();

		eventPublisher = GeneralManager.get().getEventPublisher();
		isSupportView = true;

		try {
			viewContext = JAXBContext.newInstance(SerializedDatasetInfoView.class);
		} catch (JAXBException ex) {
			throw new RuntimeException("Could not create JAXBContext", ex);
		}
	}

	@Override
	public void createPartControl(Composite parent) {

		parentComposite = new Composite(parent, SWT.NULL);
		parentComposite.setLayout(new GridLayout(1, false));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		infoComposite = new Composite(parentComposite, SWT.NULL);
		infoComposite.setLayout(new GridLayout(1, false));
		infoComposite.setLayoutData(gridData);

		nameLabel = new Label(infoComposite, SWT.NONE);
		nameLabel.setText("No data set active");
		nameLabel.setLayoutData(gridData);

		if (dataDomain == null) {
			setDataDomain(DataDomainManager.get().getDataDomainByID(
					((ASerializedSingleTablePerspectiveBasedView) serializedView).getDataDomainID()));
		}

		parent.layout();
	}

	@Override
	public void setDataDomain(IDataDomain dataDomain) {

		// Do nothing if new datadomain is the same as the current one, or if dd
		// is null
		if (dataDomain == this.dataDomain || dataDomain == null)
			return;

		this.dataDomain = dataDomain;

		updateDataSetInfo();

	}

	private void updateDataSetInfo() {

		if (!isGUIInitialized) {
			initGUI();
		}

		nameLabel.setText("Name: " + dataDomain.getLabel());

		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain tableBasedDD = (ATableBasedDataDomain) dataDomain;

			histogramExpandBar.setVisible(true);
			recordLabel.setVisible(true);
			dimensionLabel.setVisible(true);

			recordLabel.setText(tableBasedDD.getRecordDenomination(true, true) + ": "
 + tableBasedDD.getTable().depth());

			dimensionLabel.setText(tableBasedDD.getDimensionDenomination(true, true) + ": "
					+ tableBasedDD.getTable().size());

			if (!tableBasedDD.getTable().isDataHomogeneous()) {
				histogramExpandBar.setVisible(false);
				return;
			}

			if (histogramView == null) {
				histogramView = new RcpGLColorMapperHistogramView();
				histogramView.setDataDomain(tableBasedDD);
				SerializedHistogramView serializedHistogramView = new SerializedHistogramView();
				serializedHistogramView.setDataDomainID(dataDomain.getDataDomainID());
				serializedHistogramView
						.setTablePerspectiveKey(((ASerializedSingleTablePerspectiveBasedView) serializedView)
								.getTablePerspectiveKey());

				histogramView.setExternalSerializedView(serializedHistogramView);
				histogramView.createPartControl(histogramComposite);
				// Usually the canvas is registered to the GL2 animator in the
				// PartListener. Because the GL2 histogram is no usual RCP view
				// we
				// have to do it on our own
				GeneralManager.get().getViewManager().registerGLCanvasToAnimator(histogramView.getGLCanvas());
				ExpandItem item2 = new ExpandItem(histogramExpandBar, SWT.NONE, 0);
				item2.setText("Histogram");
				item2.setHeight(200);
				item2.setControl(histogramComposite);
				item2.setExpanded(true);

				histogramExpandBar.setSpacing(2);
			}
			// else {

			// If the default table perspective does not exist yet, we
			// create it and set it to private so that it does not show up
			// in the DVI
			if (!tableBasedDD.hasTablePerspective(tableBasedDD.getTable().getDefaultRecordPerspective()
					.getPerspectiveID(), tableBasedDD.getTable().getDefaultDimensionPerspective().getPerspectiveID())) {
				tableBasedDD.getDefaultTablePerspective().setPrivate(true);
			}
			histogramView.setDataDomain(tableBasedDD);
			((GLHistogram) histogramView.getGLView()).setDataDomain(tableBasedDD);
			((GLHistogram) histogramView.getGLView()).setDisplayListDirty();
			// }
		} else {
			histogramExpandBar.setVisible(false);
			recordLabel.setVisible(false);
			dimensionLabel.setVisible(false);
		}

		parentComposite.layout();
	}

	private void initGUI() {
		recordLabel = new Label(infoComposite, SWT.NONE);
		dimensionLabel = new Label(infoComposite, SWT.NONE);

		histogramExpandBar = new ExpandBar(parentComposite, SWT.V_SCROLL);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		histogramExpandBar.setLayoutData(gridData);

		histogramComposite = new Composite(histogramExpandBar, SWT.NONE);
		histogramComposite.setLayout(new FillLayout());

		isGUIInitialized = true;
	}

	@Override
	public IDataDomain getDataDomain() {
		return dataDomain;
	}

	@Override
	public void createDefaultSerializedView() {
		serializedView = new SerializedDatasetInfoView();
		determineDataConfiguration(serializedView, false);
	}

	@Override
	public void registerEventListeners() {
		super.registerEventListeners();
		listeners.register(this);
	}

	@Override
	public void unregisterEventListeners() {
		super.unregisterEventListeners();
		listeners.unregisterAll();
	}

	@ListenTo
	private void onDataDomainUpdate(DataDomainUpdateEvent event) {
		setDataDomain(event.getDataDomain());
	}

}