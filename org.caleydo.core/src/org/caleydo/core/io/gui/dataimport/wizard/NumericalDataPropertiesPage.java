/**
 *
 */
package org.caleydo.core.io.gui.dataimport.wizard;

import java.util.ArrayList;

import org.caleydo.core.data.collection.EDataClass;
import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.io.ColumnDescription;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.io.gui.dataimport.widget.DataTranspositionWidget;
import org.caleydo.core.io.gui.dataimport.widget.NumericalDataPropertiesWidget;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Page that offers the possibility to transform the dataset, such as data logarithmation or table transpose.
 *
 * @author Christian Partl
 *
 */
public class NumericalDataPropertiesPage extends AImportDataPage implements Listener {

	public static final String PAGE_NAME = "Numerical Dataset Properties";

	public static final String PAGE_DESCRIPTION = "Specify properties for the numerical dataset.";
	/**
	 * Parent composite of all widgets in this page.
	 */
	protected Composite parentComposite;

	// /**
	// * Text field that specifies the minimum data clipping value.
	// */
	// protected Text minTextField;
	// /**
	// * Text field that specifies the minimum data clipping value.
	// */
	// protected Text maxTextField;
	//
	// /**
	// * Button to determine whether a data center is used.
	// */
	// protected Button useDataCenterButton;
	//
	// /**
	// * Text field used to define the data center.
	// */
	// protected Text dataCenterTextField;
	//
	// /**
	// * Button to enable the {@link #maxTextField};
	// */
	// protected Button maxButton;
	//
	// /**
	// * Button to enable the {@link #minTextField};
	// */
	// protected Button minButton;
	//
	// /**
	// * Combo to define the scaling method that should be applied to the data.
	// */
	// protected Combo scalingCombo;
	//
	// /**
	// * Group that contains widgets associated with determining the data center.
	// */
	// protected Group dataCenterGroup;

	protected NumericalDataPropertiesWidget numericalDataPropertiesWidget;

	protected DataTranspositionWidget dataTranspositionWidget;

	/**
	 * @param pageName
	 * @param dataSetDescription
	 */
	// protected TransformDataPage(String pageName, DataSetDescription
	// dataSetDescription) {
	// super(pageName, dataSetDescription);
	// }

	public NumericalDataPropertiesPage(DataSetDescription dataSetDescription) {
		super(PAGE_NAME, dataSetDescription);
		setDescription(PAGE_DESCRIPTION);
	}

	@Override
	public void createControl(Composite parent) {

		parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		numericalDataPropertiesWidget = new NumericalDataPropertiesWidget(parentComposite, dataSetDescription
				.getDataDescription().getNumericalProperties(), this);

		// createScalingGroup(parentComposite);
		//
		// createClippingGroup(parentComposite);
		//
		// createDataCenterGroup(parentComposite);

		dataTranspositionWidget = new DataTranspositionWidget(parentComposite, (DataImportWizard) getWizard(),
				dataSetDescription.isTransposeMatrix());
		// mediator.guiCreated();

		setControl(parentComposite);
	}

	@Override
	public boolean isPageComplete() {

		if (numericalDataPropertiesWidget.isDataValid()) {
			return super.isPageComplete();
		}
		return false;
	}

	@Override
	public void fillDataSetDescription() {

		DataDescription dataDescription = new DataDescription(EDataClass.REAL_NUMBER, EDataType.FLOAT,
				numericalDataPropertiesWidget.getNumericalProperties());
		dataSetDescription.setDataDescription(dataDescription);
		dataSetDescription.setTransposeMatrix(dataTranspositionWidget.isTransposition());

		ArrayList<ColumnDescription> inputPattern = new ArrayList<ColumnDescription>();
		DataImportWizard wizard = (DataImportWizard) getWizard();

		for (Integer selected : wizard.getSelectedColumns()) {
			int columnIndex = selected.intValue();
			if (columnIndex == dataSetDescription.getColumnOfRowIds())
				continue;
			inputPattern.add(new ColumnDescription(columnIndex, dataDescription));

		}

		dataSetDescription.setParsingPattern(inputPattern);
	}

	@Override
	public void pageActivated() {
		((DataImportWizard) getWizard()).setChosenDataTypePage(this);
		((DataImportWizard) getWizard()).getContainer().updateButtons();
		dataTranspositionWidget.update();
	}

	@Override
	public void handleEvent(Event event) {
		if (getWizard().getContainer().getCurrentPage() != null)
			getWizard().getContainer().updateButtons();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return ((DataImportWizard) getWizard()).getDataSetTypePage();
	}

	@Override
	public IWizardPage getNextPage() {
		return ((DataImportWizard) getWizard()).getAddGroupingsPage();
	}

}
