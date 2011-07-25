package org.caleydo.core.io.gui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.caleydo.core.data.collection.export.DataTableExporter;
import org.caleydo.core.data.collection.export.DataTableExporter.WhichViewToExport;
import org.caleydo.core.data.collection.table.DataTable;
import org.caleydo.core.data.collection.table.SubDataTable;
import org.caleydo.core.data.id.IDType;
import org.caleydo.core.manager.GeneralManager;
import org.caleydo.core.manager.datadomain.ATableBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.datadomain.IDataDomain;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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

	private Button[] radios = new Button[3];

	private Composite composite;

	private Text txtFileName;

	private String sFileName = "";
	private String sFilePath = "";

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

		boolean bDoesBucketExist = false;
		// boolean doesHeatMapExist = false;
		// boolean doParallelCoordinatesExist = false;
		for (AGLView view : GeneralManager.get().getViewGLCanvasManager().getAllGLViews()) {
			if (view.getViewType().equals("org.caleydo.view.bucket")) {
				bDoesBucketExist = true;
			}
			// if (view instanceof GLHierarchicalHeatMap && !view.isRenderedRemote()) {
			// doesHeatMapExist = true;
			// }
			// if (view instanceof GLParallelCoordinates && !view.isRenderedRemote()) {
			// doParallelCoordinatesExist = true;
			// }
		}

		radios[0] = new Button(composite, SWT.RADIO);
		radios[0].setText("Export bucket contents");
		radios[0].setBounds(10, 5, 75, 30);
		if (!bDoesBucketExist) {
			radios[0].setEnabled(false);
		}

		radios[1] = new Button(composite, SWT.RADIO);
		radios[1].setText("Export data as shown in the standalone views");
		radios[1].setBounds(10, 30, 75, 30);
		if (experimentsToExport == null)
			radios[1].setSelection(true);

		radios[2] = new Button(composite, SWT.RADIO);
		radios[2].setText("Export group data");
		radios[2].setBounds(10, 30, 75, 30);
		if (experimentsToExport == null) {
			radios[2].setEnabled(false);
		}
		else {
			radios[2].setSelection(true);
		}

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

	@Override
	protected void okPressed() {

		for (IDataDomain dataDomain : DataDomainManager.get().getDataDomains()) {
			ATableBasedDataDomain setBasedDataDomain = null;
			if (dataDomain instanceof ATableBasedDataDomain)
				setBasedDataDomain = (ATableBasedDataDomain) dataDomain;
			else
				continue;

			DataTableExporter exporter = new DataTableExporter();
			// exporter.export(this, sFileName, eWichViewToExport);

			DataTable table = setBasedDataDomain.getDataTable();
			IDType targetIDType = setBasedDataDomain.getPrimaryRecordMappingType();
			if (table instanceof SubDataTable)
				continue;
			if (radios[0].getSelection()) {
				exporter.export(table, sFileName, WhichViewToExport.BUCKET, targetIDType);
			}
			else if (radios[1].getSelection()) {
				exporter.export(table, sFileName, WhichViewToExport.WHOLE_DATA, targetIDType);
			}
			else if (radios[2].getSelection()) {
				exporter.exportGroups(table, sFileName, genesToExport, experimentsToExport, targetIDType);
			}
		}

		super.okPressed();
	}
}
