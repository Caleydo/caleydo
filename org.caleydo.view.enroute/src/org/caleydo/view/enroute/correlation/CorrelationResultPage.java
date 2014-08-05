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
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

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
			if(classifier == null) {
				return "C"+(isColumnHeader ? columnIndex : rowIndex);
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

			return isColumnHeader ? 2 : 0;
		}

		@Override
		public int getRowCount() {
			return isColumnHeader ? 0 : 2;
		}

		/**
		 * @param classifier
		 *            setter, see {@link classifier}
		 */
		public void setClassifier(IDataClassifier classifier) {
			this.classifier = classifier;
		}

	}

	private class ContingencyTableBodyProvider implements IDataProvider {

		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {

			return contingencyTable[columnIndex][rowIndex];
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

	}

	/**
	 * @param pageName
	 */
	protected CorrelationResultPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, false));

		// final DataLayer bodyDataLayer = new DataLayer(bodyProvider, 120, 36);
		//
		// SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		// ViewportLayer bodyLayer = new ViewportLayer(selectionLayer);
		//
		// final DataLayer columnDataLayer = new DataLayer(columnHeaderProvider, 120, 25);
		// ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnDataLayer, bodyLayer, selectionLayer);
		//
		// DataLayer rowDataLayer = new DataLayer(rowHeaderProvider, 80, 36);
		// RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowDataLayer, bodyLayer, selectionLayer);
		// DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderProvider,
		// rowHeaderProvider);
		// CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer,
		// columnHeaderLayer);
		// GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		// if (table == null) {
		// table = new NatTable(parentComposite, gridLayer, false);
		// } else {
		// table.setLayer(gridLayer);
		// }
		// table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// table.addConfiguration(new DefaultCaleydoNatTableConfiguration());
		// DefaultToolTip toolTip = new NatTableToolTip(table);
		// // toolTip.setBackgroundColor(natTable.getDisplay().getSystemColor(SWT.COLOR_RED));
		// // toolTip.setPopupDelay(500);
		// toolTip.activate();
		// toolTip.setShift(new Point(10, 10));
		//
		// NatGridLayerPainter layerPainter = new NatGridLayerPainter(table);
		// table.setLayerPainter(layerPainter);

		matrixLabel = new Label(parentComposite, SWT.NONE);
		matrixLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		matrixLabel.setText("0,0: 0,1: 1,0: 1,1:");

		resultLabel = new Label(parentComposite, SWT.NONE);
		resultLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		resultLabel.setText("Two-Sided: Left-Tail: Right-Tail: ");

		setControl(parentComposite);

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

			// columnHeaderProvider.setClassifier(classifier1);
			// rowHeaderProvider.setClassifier(classifier2);
			//
			// table.refresh();
			// getShell().layout(true, true);
			// getShell().pack();
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
