/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.info.dataset;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.EventListenerManager;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventListenerManagers;
import org.caleydo.core.event.data.DataSetSelectedEvent;
import org.caleydo.core.io.MetaDataElement;
import org.caleydo.core.io.MetaDataElement.AttributeType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.serialize.ASerializedSingleTablePerspectiveBasedView;
import org.caleydo.core.serialize.ProjectMetaData;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.function.DoubleStatistics;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.core.view.CaleydoRCPViewPart;
import org.caleydo.core.view.IDataDomainBasedView;
import org.caleydo.view.histogram.GLHistogram;
import org.caleydo.view.histogram.RcpGLColorMapperHistogramView;
import org.caleydo.view.histogram.SerializedHistogramView;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Data meta view showing details about a data table.
 *
 * @author Marc Streit
 * @author Alexander Lex
 */
public class RcpDatasetInfoView extends CaleydoRCPViewPart implements IDataDomainBasedView<IDataDomain> {

	public static final String VIEW_TYPE = "org.caleydo.view.info.dataset";

	/** String used for formatting floats to 3 digits after the . using String.format() */
	public static final String THREE_DIGIT_FORMAT = "%.3f";
	public static final String TWO_DIGIT_FORMAT = "%.2f";

	private IDataDomain dataDomain;
	private TablePerspective tablePerspective;

	private ExpandItem metaDataItem;
	private ExpandItem dataSetItem;
	private ExpandItem processingItem;
	private ExpandItem statsItem;
	private ExpandItem tablePerspectiveItem;

	private Label recordPerspectiveLabel;
	private Label recordPerspectiveCount;
	// private Label unmappedRecordElements;
	private Label dimensionPerspectiveLabel;
	private Label dimensionPerspectiveCount;
	// private Label unmappedDimensionElements;

	private Label recordLabel;
	private Label recordCount;
	private Label dimensionLabel;
	private Label dimensionCount;

	private StyledText processingInfo;
	private StyledText stats;

	private TreeViewer metaDataTree;
	// private StyledText metaDataInfo;

	private ExpandItem histogramItem;
	private RcpGLColorMapperHistogramView histogramView;

	private final EventListenerManager listeners = EventListenerManagers.createSWTDirect();

	/**
	 * Constructor.
	 */
	public RcpDatasetInfoView() {
		super(SerializedDatasetInfoView.class);
		listeners.register(this);
	}

	@Override
	public void dispose() {
		listeners.unregisterAll();
		super.dispose();
	}

	@Override
	public boolean isSupportView() {
		return true;
	}

	@Override
	public void createPartControl(Composite parent) {
		ExpandBar expandBar = new ExpandBar(parent, SWT.V_SCROLL | SWT.NO_BACKGROUND);
		expandBar.setSpacing(1);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));

		parentComposite = expandBar;

		createProjectInfos(expandBar);
		createDataSetInfos(expandBar);
		createProcessingInfo(expandBar);
		createStatsInfo(expandBar);
		createMetaDataInfo(expandBar);

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

	private void createStatsInfo(ExpandBar expandBar) {
		this.statsItem = new ExpandItem(expandBar, SWT.WRAP);
		statsItem.setText("Dataset Stats");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(1, false));

		stats = new StyledText(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		stats.setBackgroundMode(SWT.INHERIT_FORCE);
		stats.setText("No processing");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 60;
		stats.setLayoutData(gd);
		stats.setEditable(false);
		stats.setWordWrap(true);

		// transformationLabel.set
		// transformationLabel.();

		statsItem.setControl(c);
		statsItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}

	private void createProcessingInfo(ExpandBar expandBar) {
		this.processingItem = new ExpandItem(expandBar, SWT.WRAP);
		processingItem.setText("Processing Info");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(1, false));

		processingInfo = new StyledText(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		processingInfo.setBackgroundMode(SWT.INHERIT_FORCE);
		processingInfo.setText("No processing");
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.heightHint = 60;
		processingInfo.setLayoutData(gd);
		processingInfo.setEditable(false);
		processingInfo.setWordWrap(true);

		// transformationLabel.set
		// transformationLabel.();

		processingItem.setControl(c);
		processingItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
	}

	private void createMetaDataInfo(ExpandBar expandBar) {
		this.metaDataItem = new ExpandItem(expandBar, SWT.WRAP);
		metaDataItem.setText("Meta Data");
		Composite c = new Composite(expandBar, SWT.NONE);
		c.setLayout(new GridLayout(1, false));

		metaDataTree = new TreeViewer(c, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		metaDataTree.setContentProvider(new MetaDataContentProvider());
		metaDataTree.setLabelProvider(new MetaDataLabelProvider());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessVerticalSpace = true;
		gridData.minimumHeight = 150;
		metaDataTree.getTree().setLayoutData(gridData);
		ColumnViewerToolTipSupport.enableFor(metaDataTree, ToolTip.NO_RECREATE);
		metaDataTree.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				TreeItem item = (TreeItem) e.item;
				Object element = item.getData();
				if (!(element instanceof MetaDataElement)) {
					@SuppressWarnings("unchecked")
					Entry<String, Pair<String, AttributeType>> attribute = (Entry<String, Pair<String, AttributeType>>) element;
					if (attribute.getValue().getSecond() == AttributeType.URL) {
						BrowserUtils.openURL(attribute.getValue().getFirst());
					}
				}
			}
		});
		// metaDataInfo = new StyledText(c, SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		// metaDataInfo.setBackgroundMode(SWT.INHERIT_FORCE);
		// metaDataInfo.setText("No meta data");
		// GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		// gd.heightHint = 160;
		// metaDataInfo.setLayoutData(gd);
		// metaDataInfo.setEditable(false);
		// metaDataInfo.setWordWrap(true);

		// transformationLabel.set
		// transformationLabel.();

		metaDataItem.setControl(c);
		metaDataItem.setHeight(c.computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
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

		// unmappedRecordElements = new Label(c, SWT.NONE);
		// unmappedRecordElements.setText("");
		// unmappedRecordElements.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 0));

		dimensionPerspectiveLabel = new Label(c, SWT.NONE);
		dimensionPerspectiveLabel.setText("");
		dimensionPerspectiveLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		dimensionPerspectiveCount = new Label(c, SWT.NONE);
		dimensionPerspectiveCount.setText("");
		dimensionPerspectiveCount.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));

		// unmappedDimensionElements = new Label(c, SWT.NONE);
		// unmappedDimensionElements.setText("");
		// unmappedDimensionElements.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 0));

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
			String recordName = tableBasedDD.getRecordDenomination(true, true);
			recordLabel.setText(recordName + ":");
			recordCount.setText("" + nrRecords);

			String dimensionName = tableBasedDD.getDimensionDenomination(true, true);
			dimensionLabel.setText(dimensionName + ":");
			dimensionCount.setText("" + nrDimensions);

			((Composite) dataSetItem.getControl()).layout();
			recordPerspectiveLabel.setText(recordName + ":");
			dimensionPerspectiveLabel.setText(dimensionName + ":");

			if (((ATableBasedDataDomain) dataDomain).getTable() instanceof NumericalTable) {
				NumericalProperties numProp = dataDomain.getDataSetDescription().getDataDescription()
						.getNumericalProperties();
				String processingMessage = "";

				if (numProp.getzScoreNormalization() != null) {
					if (numProp.getzScoreNormalization().equals(NumericalProperties.ZSCORE_COLUMNS)) {
						processingMessage += "Z-standardised on "
								+ ((ATableBasedDataDomain) dataDomain).getColumnIDCategory() + "\n" + "";
					} else if (numProp.getzScoreNormalization().equals(NumericalProperties.ZSCORE_ROWS)) {
						processingMessage += "Z-standardised on "
								+ ((ATableBasedDataDomain) dataDomain).getRowIDCategory() + System.lineSeparator();
					}

				}
				if (numProp.getDataTransformation() != null) {
					processingMessage += "Scale: " + numProp.getDataTransformation() + System.lineSeparator();
				}
				if (numProp.getClipToStdDevFactor() != null) {
					processingMessage += "Clipped to "
							+ String.format(TWO_DIGIT_FORMAT, numProp.getClipToStdDevFactor()) + " \u03C3"
							+ System.lineSeparator();
				} else if (numProp.getMax() != null || numProp.getMin() != null) {
					processingMessage += "Clipped to max:  " + String.format(TWO_DIGIT_FORMAT, numProp.getMax())
							+ ", min:"
							+ String.format("%.2f", numProp.getMin())
							+ System.lineSeparator();
				}
				NumericalTable table = (NumericalTable) ((ATableBasedDataDomain) dataDomain).getTable();
				if (table.getDataCenter() != null) {
					processingMessage += "Centered at " + String.format(TWO_DIGIT_FORMAT, table.getDataCenter())
							+ System.lineSeparator();
				}
				processingInfo.setText(processingMessage);

				String n = System.lineSeparator();

				DoubleStatistics dsStats = table.getDatasetStatistics();
				String statsMessage = "";
				statsMessage += "Mean:\t\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getMean()) + n;
				statsMessage += "Std. Dev.:\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getSd()) + n;
				statsMessage += "Max:\t\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getMax()) + n;
				statsMessage += "Min:\t\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getMin()) + n;
				statsMessage += "Skewness:\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getSkewness()) + n;
				statsMessage += "Kurtosis:\t\t" + String.format(THREE_DIGIT_FORMAT, dsStats.getKurtosis()) + n;

				stats.setText(statsMessage);
			} else {
				statsItem.setExpanded(false);
				stats.setText("");
				processingItem.setExpanded(false);
				processingInfo.setText("");
			}

			if (tablePerspective != null) {
				String tpLabel = "Persp.: " + tablePerspective.getLabel();
				if (tpLabel.length() > stringLength)
					tpLabel = tpLabel.substring(0, stringLength - 3) + "...";
				tablePerspectiveItem.setText(tpLabel);
				tablePerspectiveItem.setExpanded(true);

				recordPerspectiveCount.setText("" + tablePerspective.getNrRecords() + " ("
						+ String.format("%.2f", tablePerspective.getNrRecords() * 100f / nrRecords) + "%)");
				if (tablePerspective.getRecordPerspective().getUnmappedElements() > 0) {
					recordPerspectiveCount.setToolTipText("Unmapped: "
							+ tablePerspective.getRecordPerspective().getUnmappedElements() + " - The number of "
							+ recordName
							+ " that are in the original stratification but can't be mapped to this dataset");
				} else {
					recordPerspectiveCount.setToolTipText("");
				}

				dimensionPerspectiveCount.setText("" + tablePerspective.getNrDimensions() + " ("
						+ String.format("%.2f", tablePerspective.getNrDimensions() * 100f / nrDimensions) + "%)");
				if (tablePerspective.getDimensionPerspective().getUnmappedElements() > 1) {
					dimensionPerspectiveCount.setToolTipText("Unmapped: "
							+ tablePerspective.getDimensionPerspective().getUnmappedElements() + " - The number of "
							+ dimensionName
							+ " that are in the original stratification but can't be mapped to this dataset");
				} else {
					dimensionPerspectiveCount.setToolTipText("");
				}

				((Composite) tablePerspectiveItem.getControl()).layout();
			} else {

				tablePerspectiveItem.setText("Perspective: <no selection>");
				tablePerspectiveItem.setExpanded(false);
				recordPerspectiveCount.setText("<no selection>");
				recordPerspectiveCount.setToolTipText("");
				dimensionPerspectiveCount.setText("<no selection>");
				dimensionPerspectiveCount.setToolTipText("");
			}

			updateMetaDataInfo();

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
			dataSetItem.setExpanded(true);
			histogramItem.setExpanded(false);
			histogramItem.getControl().setEnabled(false);
		}
	}

	private void updateMetaDataInfo() {
		MetaDataElement metaData = dataDomain.getDataSetDescription().getMetaData();
		if (metaData != null) {
			// String text = new PlainTextFormatter().format(metaData);
			metaDataTree.setContentProvider(new MetaDataContentProvider(metaData));
			metaDataTree.setInput(metaData);
			// metaDataInfo.setText(text);
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

	private class MetaDataContentProvider implements ITreeContentProvider {

		private MetaDataElement root;

		/**
		 *
		 */
		public MetaDataContentProvider() {
			// TODO Auto-generated constructor stub
		}

		public MetaDataContentProvider(MetaDataElement root) {
			this.root = root;
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof MetaDataElement) {
				return getChildren((MetaDataElement) inputElement);
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof MetaDataElement) {
				return getChildren((MetaDataElement) parentElement);
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Object getParent(Object element) {
			if (element instanceof MetaDataElement) {
				if (element == root)
					return null;
				return getParent((MetaDataElement) element, root);
			} else {

				return getParent((Entry<String, Pair<String, AttributeType>>) element, root);
			}
		}

		private MetaDataElement getParent(MetaDataElement element, MetaDataElement parent) {
			List<MetaDataElement> elements = parent.getElements();
			if (elements != null && !elements.isEmpty()) {
				if (elements.contains(element))
					return parent;
				for (MetaDataElement child : elements) {
					MetaDataElement p = getParent(element, child);
					if (p != null)
						return p;
				}
			}
			return null;
		}

		private MetaDataElement getParent(Entry<String, Pair<String, AttributeType>> attribute, MetaDataElement parent) {
			Map<String, Pair<String, AttributeType>> attributes = parent.getAttributes();
			if (attributes != null && !attributes.isEmpty()) {
				if (attributes.entrySet().contains(attribute))
					return parent;
				List<MetaDataElement> elements = parent.getElements();
				if (elements != null && !elements.isEmpty()) {
					for (MetaDataElement child : elements) {
						MetaDataElement p = getParent(attribute, child);
						if (p != null)
							return p;
					}
				}
			}
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof MetaDataElement) {
				MetaDataElement e = (MetaDataElement) element;
				Map<String, Pair<String, AttributeType>> attributes = e.getAttributes();
				if (attributes != null && !attributes.isEmpty()) {
					return true;
				}
				List<MetaDataElement> elements = e.getElements();
				if (elements != null && !elements.isEmpty()) {
					return true;
				}
			}
			return false;
		}

		private Object[] getChildren(MetaDataElement element) {
			Map<String, Pair<String, AttributeType>> attributes = element.getAttributes();
			List<Object> children = new ArrayList<>();
			if (attributes != null && !attributes.isEmpty()) {
				children.addAll(attributes.entrySet());
			}
			List<MetaDataElement> elements = element.getElements();
			if (elements != null && !elements.isEmpty()) {
				children.addAll(elements);
			}
			return children.toArray();
		}

		/**
		 * @param root
		 *            setter, see {@link root}
		 */
		public void setRoot(MetaDataElement root) {
			this.root = root;
		}

	}

	private class MetaDataLabelProvider extends StyledCellLabelProvider {

		@Override
		public void update(ViewerCell cell) {
			Object element = cell.getElement();
			StyledString text = new StyledString();

			if (element instanceof MetaDataElement) {
				MetaDataElement e = (MetaDataElement) element;
				String name = e.getName();
				if (name != null)
					text.append(name);
			} else {
				@SuppressWarnings("unchecked")
				Entry<String, Pair<String, AttributeType>> attribute = (Entry<String, Pair<String, AttributeType>>) element;
				if (attribute.getValue().getSecond() == AttributeType.URL) {
					text.append(attribute.getKey() + ": ");
					String url = attribute.getValue().getFirst();
					if (url.length() > 17) {
						int index = url.lastIndexOf("/") + 1;
						url = url.substring(index, url.length());
					}
					text.append(url, StyledString.COUNTER_STYLER);
				} else {
					text.append(attribute.getKey() + ": " + attribute.getValue().getFirst());
				}
			}
			cell.setText(text.toString());
			cell.setStyleRanges(text.getStyleRanges());
			super.update(cell);
		}

		@Override
		public String getToolTipText(Object element) {
			if (!(element instanceof MetaDataElement)) {
				@SuppressWarnings("unchecked")
				Entry<String, Pair<String, AttributeType>> attribute = (Entry<String, Pair<String, AttributeType>>) element;
				if (attribute.getValue().getSecond() == AttributeType.URL) {
					return attribute.getValue().getFirst();
				}
			}
			return super.getToolTipText(element);
		}

	}

}
