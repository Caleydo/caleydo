/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.enroute.correlation;

import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.io.gui.dataimport.widget.table.NatTableToolTip;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.RowGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.model.IRowGroupModel;
import org.eclipse.nebula.widgets.nattable.group.model.RowGroup;
import org.eclipse.nebula.widgets.nattable.group.model.RowGroupModel;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultRowHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.layer.GridLineCellLayerPainter;
import org.eclipse.nebula.widgets.nattable.painter.layer.ILayerPainter;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.collect.Lists;

import edu.northwestern.at.utils.math.statistics.FishersExactTest;

/**
 * @author Christian
 *
 */
public class CorrelationResultPage extends WizardPage implements IPageChangedListener {

	protected boolean visited = false;

	protected NatTable table;

	protected int[][] contingencyTable = new int[2][2];

	protected CategoryHeaderProvider columnHeaderProvider = new CategoryHeaderProvider(true);
	protected CategoryHeaderProvider rowHeaderProvider = new CategoryHeaderProvider(false);
	protected ContingencyTableBodyProvider bodyProvider = new ContingencyTableBodyProvider();

	protected Composite parentComposite;

	Label matrixLabel;
	Label resultLabel;

	private static class CategoryHeaderProvider implements IDataProvider {

		private boolean isColumnHeader;
		private IDataClassifier classifier;

		/**
		 * @param isColumnHeader
		 * @param classifier
		 */
		public CategoryHeaderProvider(boolean isColumnHeader) {
			this.isColumnHeader = isColumnHeader;
		}

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			if (classifier == null) {
				return "C" + (isColumnHeader ? columnIndex : rowIndex);
			}
			List<SimpleCategory> categories = classifier.getDataClasses();
			return isColumnHeader ? categories.get(columnIndex).name : categories.get(rowIndex).name;
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getColumnCount() {

			return isColumnHeader ? 2 : 1;
		}

		@Override
		public int getRowCount() {
			return isColumnHeader ? 1 : 2;
		}

		/**
		 * @param classifier
		 *            setter, see {@link classifier}
		 */
		public void setClassifier(IDataClassifier classifier) {
			this.classifier = classifier;
		}

	}

	private class ContingencyTableBodyProvider implements IRowDataProvider<Object> {

		// Dummy row objects
		private List<Object> rowObjects = Lists.newArrayList(new Object(), new Object());

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			Integer value = contingencyTable[columnIndex][rowIndex];
			return value.toString();
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			// TODO Auto-generated method stub

		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public int getRowCount() {
			return 2;
		}

		@Override
		public Object getRowObject(int rowIndex) {
			return rowObjects.get(rowIndex);
		}

		@Override
		public int indexOfRowObject(Object rowObject) {
			return rowObjects.indexOf(rowObject);
		}

	}

	private static class MyRowGroupHeaderLayer<T> extends RowGroupHeaderLayer<T> {

		private ILayerPainter myLayerPainter = new GridLineCellLayerPainter();

		/**
		 * @param rowHeaderLayer
		 * @param selectionLayer
		 * @param rowGroupModel
		 */
		public MyRowGroupHeaderLayer(ILayer rowHeaderLayer, SelectionLayer selectionLayer,
				IRowGroupModel<T> rowGroupModel) {
			super(rowHeaderLayer, selectionLayer, rowGroupModel);
		}

		@Override
		public ILayerPainter getLayerPainter() {
			return myLayerPainter;
		}

	}

	/**
	 * @param pageName
	 */
	protected CorrelationResultPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, false));

		buildTable(parentComposite);

		matrixLabel = new Label(parentComposite, SWT.NONE);
		matrixLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		matrixLabel.setText("0,0: 0,1: 1,0: 1,1:");

		resultLabel = new Label(parentComposite, SWT.NONE);
		resultLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		resultLabel.setText("Two-Sided: Left-Tail: Right-Tail: ");

		setControl(parentComposite);

	}

	protected void buildTable(Composite parentComposite) {
		final DataLayer bodyDataLayer = new DataLayer(bodyProvider, 200, 36);
		// bodyDataLayer.addLayerListener(this);

		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		// selectionLayer.addLayerListener(this);
		ViewportLayer bodyLayer = new ViewportLayer(selectionLayer);

		final DataLayer columnDataLayer = new DataLayer(columnHeaderProvider, 200, 36);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnDataLayer, bodyLayer, selectionLayer);

		ColumnGroupModel columnGroupModel = new ColumnGroupModel();

		ColumnGroupHeaderLayer columnGroupHeaderLayer = new ColumnGroupHeaderLayer(columnHeaderLayer, selectionLayer,
				columnGroupModel);

		CalculateCorrelationWizard wizard = (CalculateCorrelationWizard) getWizard();

		columnGroupHeaderLayer.addColumnsIndexesToGroup(getInfoString(wizard.getInfo1()), 0, 1);
		columnGroupHeaderLayer.setRowHeight(64);
		columnGroupHeaderLayer.clearConfiguration();

		DataLayer rowDataLayer = new DataLayer(rowHeaderProvider, 200, 36);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyLayer, selectionLayer, true,
				new GridLineCellLayerPainter());

		RowGroupModel<Object> rowGroupModel = new RowGroupModel<>();
		rowGroupModel.setDataProvider(bodyProvider);
		MyRowGroupHeaderLayer<Object> rowGroupHeaderLayer = new MyRowGroupHeaderLayer<Object>(rowHeaderLayer,
				selectionLayer, rowGroupModel);
		rowGroupHeaderLayer.setColumnWidth(200);
		rowGroupHeaderLayer.clearConfiguration();
		rowGroupHeaderLayer.addConfiguration(new DefaultRowHeaderStyleConfiguration());

		RowGroup<Object> rowGroup = new RowGroup<Object>(rowGroupModel, getInfoString(wizard.getInfo2()), false);
		rowGroup.addMemberRow(bodyProvider.getRowObject(0));
		rowGroup.addStaticMemberRow(bodyProvider.getRowObject(1));
		rowGroupModel.addRowGroup(rowGroup);

		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderProvider,
				rowHeaderProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowGroupHeaderLayer,
				columnGroupHeaderLayer);
		GridLayer gridLayer = new GridLayer(bodyLayer, columnGroupHeaderLayer, rowGroupHeaderLayer, cornerLayer);
		if (table == null) {
			table = new NatTable(parentComposite, gridLayer, false);
		} else {
			table.setLayer(gridLayer);
		}
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 800;
		table.setLayoutData(gd);

		table.addConfiguration(new ContingencyTableConfiguration());
		DefaultToolTip toolTip = new NatTableToolTip(table);
		toolTip.activate();
		toolTip.setShift(new Point(10, 10));

// NatTableUtil.applyDefaultNatTableStyling(table);
		//
		// table.addConfiguration(new AbstractRegistryConfiguration() {
		//
		// @Override
		// public void configureRegistry(IConfigRegistry configRegistry) {
		// Style cellStyle = new Style();
		// cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// GridRegion.ROW_GROUP_HEADER);
		//
		// cellStyle = new Style();
		// cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
		// GridRegion.ROW_HEADER);
		//
		// }
		// });

		table.configure();


	}

	private String getInfoString(DataCellInfo info) {
		if (info == null)
			return "undefined";
		StringBuilder b = new StringBuilder("Dataset: " + info.getDataDomainLabel() + System.lineSeparator());
		b.append("Group: " + info.getGroupLabel() + System.lineSeparator());
		b.append("Row: " + info.getRowLabel());
		return b.toString();
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			CalculateCorrelationWizard wizard = (CalculateCorrelationWizard) getWizard();
			DataCellInfo info1 = wizard.getInfo1();
			DataCellInfo info2 = wizard.getInfo2();
			IDataClassifier classifier1 = wizard.getCell1Classifier();
			IDataClassifier classifier2 = wizard.getCell2Classifier();

			IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
					info1.columnPerspective.getIdType());
			IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(info1.columnPerspective.getIdType(),
					info2.columnPerspective.getIdType());
			contingencyTable = new int[2][2];

			for (int cell1ColumnID : info1.columnPerspective.getVirtualArray()) {

				int index1 = getContingencyIndex(info1.dataDomain, info1.columnPerspective.getIdType(), cell1ColumnID,
						info1.rowIDType, info1.rowID, classifier1);
				if (index1 == -1)
					continue;

				Set<Object> cell2ColumnIDs = mapper.apply(cell1ColumnID);
				if (cell2ColumnIDs != null && !cell2ColumnIDs.isEmpty()) {
					Integer cell2ColumnID = (Integer) cell2ColumnIDs.iterator().next();
					if (info2.columnPerspective.getVirtualArray().contains(cell2ColumnID)) {
						int index2 = getContingencyIndex(info2.dataDomain, info2.columnPerspective.getIdType(),
								cell2ColumnID, info2.rowIDType, info2.rowID, classifier2);
						if (index2 == -1)
							continue;
						contingencyTable[index1][index2]++;
					}
				}

			}

			double[] result = FishersExactTest.fishersExactTest(contingencyTable[0][0], contingencyTable[0][1],
					contingencyTable[1][0], contingencyTable[1][1]);
			matrixLabel.setText("(0,0): " + contingencyTable[0][0] + " (0,1): " + contingencyTable[0][1] + " (1,0): "
					+ contingencyTable[1][0] + " (1,1): " + contingencyTable[1][1]);
			resultLabel.setText("Two-Sided: " + result[0] + " Left-Tail: " + result[1] + " Right-Tail: " + result[2]);
			visited = true;

			columnHeaderProvider.setClassifier(classifier1);
			rowHeaderProvider.setClassifier(classifier2);

			buildTable(parentComposite);

			table.refresh();
			getShell().layout(true, true);
			getShell().pack();
			getWizard().getContainer().updateButtons();
		}

	}

	private int getContingencyIndex(ATableBasedDataDomain dataDomain, IDType columnIDType, int columnID,
			IDType rowIDType, int rowID, IDataClassifier classifier) {
		Object value = dataDomain.getRaw(columnIDType, columnID, rowIDType, rowID);
		List<SimpleCategory> classes = classifier.getDataClasses();
		SimpleCategory c = classifier.apply(value);
		if (c == null)
			return -1;
		return classes.indexOf(c);
	}

	@Override
	public boolean isPageComplete() {

		return visited && super.isPageComplete();
	}

}
