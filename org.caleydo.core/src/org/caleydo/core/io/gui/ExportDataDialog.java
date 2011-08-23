package org.caleydo.core.io.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.caleydo.core.data.collection.export.DataTableExporter;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.SubDataTable;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.data.perspective.DimensionPerspective;
import org.caleydo.core.data.perspective.RecordPerspective;
import org.caleydo.core.manager.GeneralManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * File dialog for exporting data files.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ExportDataDialog
	extends Dialog {

	private ArrayList<Integer> genesToExport = null;
	private ArrayList<Integer> experimentsToExport = null;

	// private Button[] radios = new Button[3];

	private Composite composite;
	private Combo dataDomainChooser;
	private Combo recordPerspectiveChooser;
	private Combo dimensionPerspectiveChooser;

	private Text txtFileName;

	private String sFileName = "";
	private String sFilePath = "";

	private ATableBasedDataDomain dataDomain;
	private RecordPerspective recordPerspective;
	private DimensionPerspective dimensionPerspective;

	private String[] possibleDataDomains;
	private String[] possibleRecordPerspectives;
	private String[] possibleDimensionPerspectives;

	/**
	 * Constructor.
	 */
	public ExportDataDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Add data for group exports.
	 * 
	 * @param genesToExport
	 *            the list of genes to export
	 * @param experimentsToExport
	 *            the list of experiments to export
	 */
	public void addGroupData(ArrayList<Integer> genesToExport, ArrayList<Integer> experimentsToExport) {
		this.genesToExport = genesToExport;
		this.experimentsToExport = experimentsToExport;

	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Export Data");
		newShell.setImage(GeneralManager.get().getResourceLoader()
			.getImage(newShell.getDisplay(), "resources/icons/general/export_data.png"));
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		createGUI(parent);
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createButtonBar(Composite parent) {
		Control control = super.createButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);

		return control;
	}

	private void createGUI(final Composite parent) {

		composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Button buttonFileChooser = new Button(composite, SWT.PUSH);
		buttonFileChooser.setText("Choose export destination..");

		txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		buttonFileChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog fileDialog = new FileDialog(parent.getShell(), SWT.SAVE);
				fileDialog.setText("Save");
				fileDialog.setFilterPath(sFilePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);

				String sFilePath =
					"caleydo_export_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date()) + ".csv";

				fileDialog.setFileName(sFilePath);
				sFileName = fileDialog.open();

				txtFileName.setText(sFileName);

			}
		});

		dataDomainChooser = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
		dataDomainChooser.setText("Choose dataDomain");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.minimumWidth = 400;
		dataDomainChooser.setLayoutData(data);

		ArrayList<ATableBasedDataDomain> tDataDomains =
			DataDomainManager.get().getDataDomainsByType(ATableBasedDataDomain.class);

		possibleDataDomains = new String[tDataDomains.size() + 1];
		for (int count = 0; count < tDataDomains.size(); count++) {
			possibleDataDomains[count] = tDataDomains.get(count).getDataDomainID();
			String possibleDataDomain = possibleDataDomains[count];
			dataDomainChooser.add(possibleDataDomain, count);
		}
		if (possibleDataDomains.length == 1) {
			dataDomainChooser.select(0);
			dataDomain =
				(ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(possibleDataDomains[0]);
		}
		possibleDataDomains[tDataDomains.size()] = "wu";

		dataDomainChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				String dataDomainID = possibleDataDomains[dataDomainChooser.getSelectionIndex()];
				System.out.println(dataDomainID);
				dataDomain = (ATableBasedDataDomain) DataDomainManager.get().getDataDomainByID(dataDomainID);
				initDataPerspectiveChoosers(parent);
				checkOK();
			}
		});

		recordPerspectiveChooser = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
		recordPerspectiveChooser.setText("Choose record perspective");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.minimumWidth = 400;
		recordPerspectiveChooser.setLayoutData(data);

		recordPerspectiveChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String recordPerspectiveID =
					possibleRecordPerspectives[recordPerspectiveChooser.getSelectionIndex()];
				recordPerspective = dataDomain.getTable().getRecordPerspective(recordPerspectiveID);
				checkOK();
			}
		});

		dimensionPerspectiveChooser = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER);
		dimensionPerspectiveChooser.setText("Choose dimension perspective");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.minimumWidth = 400;
		dimensionPerspectiveChooser.setLayoutData(data);

		dimensionPerspectiveChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				String dimensionPerspectiveID =
					possibleDimensionPerspectives[dimensionPerspectiveChooser.getSelectionIndex()];
				dimensionPerspective = dataDomain.getTable().getDimensionPerspective(dimensionPerspectiveID);
				checkOK();
			}
		});

		initDataPerspectiveChoosers(parent);
		// --- old stuff

		// radios[0] = new Button(composite, SWT.RADIO);
		// radios[0].setText("Export bucket contents");
		// radios[0].setBounds(10, 5, 75, 30);
		//
		// radios[1] = new Button(composite, SWT.RADIO);
		// radios[1].setText("Export data as shown in the standalone views");
		// radios[1].setBounds(10, 30, 75, 30);
		// if (experimentsToExport == null)
		// radios[1].setSelection(true);
		//
		// radios[2] = new Button(composite, SWT.RADIO);
		// radios[2].setText("Export group data");
		// radios[2].setBounds(10, 30, 75, 30);
		// if (experimentsToExport == null) {
		// radios[2].setEnabled(false);
		// }
		// else {
		// radios[2].setSelection(true);
		// }

		// if (!doesHeatMapExist) {
		// radios[1].setEnabled(false);
		// }
		// else if (!bDoesBucketExist) {
		// radios[1].setSelection(true);
		// }

		// radios[2] = new Button(composite, SWT.RADIO);
		// radios[2].setText("Export Parallel Coordinates");
		// radios[2].setBounds(10, 30, 75, 30);
		// if (!doParallelCoordinatesExist) {
		// radios[1].setEnabled(false);
		// }
		// else if (!bDoesBucketExist) {
		// radios[1].setSelection(true);
		// }

	}

	private final boolean checkOK() {
		if (dataDomain == null || recordPerspective == null || dimensionPerspective == null
			|| sFileName == null) {
			return false;
		}
		getButton(IDialogConstants.OK_ID).setEnabled(true);
		return true;

	}

	private final void initDataPerspectiveChoosers(Composite parent) {
		if (dataDomain != null) {
			possibleRecordPerspectives = dataDomain.getRecordPerspectiveIDs().toArray(new String[0]);
			possibleDimensionPerspectives = dataDomain.getDimensionPerspectiveIDs().toArray(new String[0]);
		}
		else {
			possibleRecordPerspectives = new String[] { "Choose Datadomain first!" };
			possibleDimensionPerspectives = new String[] { "Choose Datadomain first!" };
		}

		recordPerspectiveChooser.removeAll();
		for (int index = 0; index < possibleRecordPerspectives.length; index++) {
			String possibleDataPerspective = possibleRecordPerspectives[index];
			recordPerspectiveChooser.add(possibleDataPerspective, index);
		}

		dimensionPerspectiveChooser.removeAll();
		for (int index = 0; index < possibleDimensionPerspectives.length; index++) {
			String possibleDataPerspective = possibleDimensionPerspectives[index];
			dimensionPerspectiveChooser.add(possibleDataPerspective, index);
		}

		if (dataDomain == null) {
			recordPerspectiveChooser.setEnabled(false);
			dimensionPerspectiveChooser.setEnabled(false);
		}
		else {
			recordPerspectiveChooser.setEnabled(true);
			dimensionPerspectiveChooser.setEnabled(true);
		}
	}

	@Override
	protected void okPressed() {

		DataTableExporter exporter = new DataTableExporter();

		DataTable table = dataDomain.getTable();
		IDType targetIDType = dataDomain.getPrimaryRecordMappingType();
		exporter.export(table, sFileName, recordPerspective, dimensionPerspective, targetIDType);

		super.okPressed();
	}

	public static void main(String[] args) {
		new ExportDataDialog(new Shell()).open();
	}
}
