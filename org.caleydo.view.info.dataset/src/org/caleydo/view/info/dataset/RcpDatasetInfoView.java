/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.dataset;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.DataSetSelectedEvent;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.core.util.system.BrowserUtils;
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
import org.eclipse.swt.widgets.Link;

/**
 * Data meta view showing details about a data table.
 *
 * @author Marc Streit
 */
public class RcpDatasetInfoView extends CaleydoRCPViewPart implements IDataDomainBasedView<IDataDomain> {

	public static final String VIEW_TYPE = "org.caleydo.view.info.dataset";

	private IDataDomain dataDomain;
	private TablePerspective tablePerspective;

	private ExpandItem dataSetItem;
	private ExpandItem tablePerspectiveItem;

	private Label recordPerspectiveLabel;
	private Label recordPerspectiveCount;
	private Label dimensionPerspectiveLabel;
	private Label dimensionPerspectiveCount;

	private Label recordLabel;
	private Label recordCount;
	private Label dimensionLabel;
	private Label dimensionCount;

	private ExpandItem histogramItem;
	private RcpGLColorMapperHistogramView histogramView;

	private final EventListenerManager listeners = EventListenerManagers.wrap(this);

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
		ExpandBar expandBar = new ExpandBar(parent, SWT.V_SCROLL | SWT.NO_BACKGROUND);
		expandBar.setSpacing(1);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		parentComposite = expandBar;

		createProjectInfos(expandBar);
		createDataSetInfos(expandBar);

		createTablePerspectiveInfos(expandBar);

		createHistogramInfos(expandBar);

		if (dataDomain == null) {
			setDataDomain(DataDomainManager.get().getDataDomainByID(
					((ASerializedSingleTablePerspectiveBasedView) serializedView).getDataDomainID()));
		}

		parent.layout();
	}

	private void createDataSetInfos(ExpandBar expandBar) {
		this.dataSetItem = new ExpandItem(expandBar, SWT.WRAP);
		dataSetItem.setText("Data Set: <no selection>");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		recordLabel = new Label(c, SWT.NONE);
		recordLabel.setText("");
		recordLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		recordCount = new Label(c, SWT.NONE);
		recordCount.setText("");
		recordCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		dimensionLabel = new Label(c, SWT.NONE);
		dimensionLabel.setText("");
		dimensionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		dimensionCount = new Label(c, SWT.NONE);
		dimensionCount.setText("");
		dimensionCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		dataSetItem.setControl(c);
		dataSetItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		dataSetItem.setExpanded(false);
	}

	private void createTablePerspectiveInfos(ExpandBar expandBar) {
		this.tablePerspectiveItem = new ExpandItem(expandBar, SWT.WRAP);
		tablePerspectiveItem.setText("Perspective: <no selection>");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(2, false));

		recordPerspectiveLabel = new Label(c, SWT.NONE);
		recordPerspectiveLabel.setText("");
		recordPerspectiveLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		recordPerspectiveCount = new Label(c, SWT.NONE);
		recordPerspectiveCount.setText("");
		recordPerspectiveCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		dimensionPerspectiveLabel = new Label(c, SWT.NONE);
		dimensionPerspectiveLabel.setText("");
		dimensionPerspectiveLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		dimensionPerspectiveCount = new Label(c, SWT.NONE);
		dimensionPerspectiveCount.setText("");
		dimensionPerspectiveCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		tablePerspectiveItem.setControl(c);
		tablePerspectiveItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		tablePerspectiveItem.setExpanded(false);
	}

	private void createProjectInfos(ExpandBar expandBar) {
		ProjectMetaData metaData = GeneralManager.get().getMetaData();
		if (metaData.keys().isEmpty())
			return;
		ExpandItem expandItem = new ExpandItem(expandBar, SWT.NONE);
		expandItem.setText("Project: " + metaData.getName());
		Composite g = new Composite(expandBar, SWT.NONE);
		g.setLayout(new GridLayout(2, false));
		createLine(
				g,
				"Creation Date",
				DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.ENGLISH).format(
						metaData.getCreationDate()));
		for (String key : metaData.keys()) {
			createLine(g, key, metaData.get(key));
		}

		expandItem.setControl(g);
		expandItem.setHeight(g.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		expandItem.setExpanded(false);
	}

	private void createHistogramInfos(ExpandBar expandBar) {
		histogramItem = new ExpandItem(expandBar, SWT.NONE);
		histogramItem.setText("Histogram");
		histogramItem.setHeight(200);
		Composite wrapper = new Composite(expandBar, SWT.NONE);
		wrapper.setLayout(new FillLayout());
		histogramItem.setControl(wrapper);

		histogramItem.setExpanded(false);
		histogramItem.getControl().setEnabled(false);
	}

	private void createLine(Composite parent, String label, String value) {
		if (label == null || label.trim().isEmpty() || value == null || value.trim().isEmpty())
			return;
		Label l = new Label(parent, SWT.NO_BACKGROUND);
		l.setText(label + ":");
		l.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		try {
			final URL url = new URL(value);
			Link v = new Link(parent, SWT.NO_BACKGROUND);

			value = url.toExternalForm();
			if (value.length() > 20)
				value = value.substring(0, 20 - 3) + "...";
			v.setText("<a href=\"" + url.toExternalForm() + "\">" + value + "</a>");
			v.setToolTipText(url.toExternalForm());
			v.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			v.addSelectionListener(BrowserUtils.LINK_LISTENER);
		} catch (MalformedURLException e) {
			Label v = new Label(parent, SWT.NO_BACKGROUND);
			v.setText(value);
			v.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		}

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
		short stringLength = 28;

		String dsLabel = "Dataset: " + dataDomain.getLabel();
		if (dsLabel.length() > stringLength)
			dsLabel = dsLabel.substring(0, stringLength - 3) + "...";

		// dataSetItem.setText("Data Set: " + dataDomain.getLabel().substring(0, 15));
		dataSetItem.setText(dsLabel);

		if (dataDomain instanceof ATableBasedDataDomain) {
			ATableBasedDataDomain tableBasedDD = (ATableBasedDataDomain) dataDomain;

			dataSetItem.setExpanded(true);

			int nrRecords = tableBasedDD.getTable().depth();
			int nrDimensions = tableBasedDD.getTable().size();

			recordLabel.setText(tableBasedDD.getRecordDenomination(true, true) + ":");
			recordCount.setText("" + nrRecords);

			dimensionLabel.setText(tableBasedDD.getDimensionDenomination(true, true) + ":");
			dimensionCount.setText("" + nrDimensions);

			((Composite) dataSetItem.getControl()).layout();

			if (tablePerspective != null) {
				String tpLabel = "Persp.: " + tablePerspective.getLabel();
				if (tpLabel.length() > stringLength)
					tpLabel = tpLabel.substring(0, stringLength - 3) + "...";
				tablePerspectiveItem.setText(tpLabel);
				tablePerspectiveItem.setExpanded(true);
				recordPerspectiveLabel.setText(tableBasedDD.getRecordDenomination(true, true) + ":");
				recordPerspectiveCount.setText("" + tablePerspective.getNrRecords() + " ("
						+ String.format("%.2f", tablePerspective.getNrRecords() * 100f / nrRecords) + "%)");

				dimensionPerspectiveLabel.setText(tableBasedDD.getDimensionDenomination(true, true) + ":");
				dimensionPerspectiveCount.setText("" + tablePerspective.getNrDimensions() + " ("
						+ String.format("%.2f", tablePerspective.getNrDimensions() * 100f / nrDimensions) + "%)");

				((Composite) tablePerspectiveItem.getControl()).layout();
			}

			if (!tableBasedDD.getTable().isDataHomogeneous()) {
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
				serializedHistogramView
						.setTablePerspectiveKey(((ASerializedSingleTablePerspectiveBasedView) serializedView)
								.getTablePerspectiveKey());

				histogramView.setExternalSerializedView(serializedHistogramView);
				histogramView.createPartControl((Composite) histogramItem.getControl());
				// Usually the canvas is registered to the GL2 animator in the
				// PartListener. Because the GL2 histogram is no usual RCP view
				// we
				// have to do it on our own
				GeneralManager.get().getViewManager().registerGLCanvasToAnimator(histogramView.getGLCanvas());
				((Composite) histogramItem.getControl()).layout();
			}
			// else {

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
			if (tablePerspective != null) {

				histogramView.setTablePerspective(tablePerspective);
				((GLHistogram) histogramView.getGLView()).setTablePerspective(tablePerspective);
			}
			((GLHistogram) histogramView.getGLView()).setDisplayListDirty();
			// }
		} else {
			dataSetItem.setExpanded(true);
			histogramItem.setExpanded(false);
			histogramItem.getControl().setEnabled(false);
		}
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
	private void onDataDomainUpdate(DataSetSelectedEvent event) {

		IDataDomain dd = event.getDataDomain();
		TablePerspective tp = event.getTablePerspective();
		// Do nothing if new datadomain is the same as the current one, or if dd
		// is null
		if (dd == null || (dd == this.dataDomain && tp == this.tablePerspective))
			return;

		this.dataDomain = dd;
		this.tablePerspective = tp;

		updateDataSetInfo();
		setDataDomain(event.getDataDomain());
	}

}
