package org.caleydo.rcp.dialog.file;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.export.SetExporter.EWhichViewToExport;
import org.caleydo.core.data.collection.set.MetaSet;
import org.caleydo.core.manager.IDataDomain;
import org.caleydo.core.manager.datadomain.ASetBasedDataDomain;
import org.caleydo.core.manager.datadomain.DataDomainManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLView;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action responsible for exporting data to current Caleydo project.
 * 
 * @author Marc Streit
 * @author Alexander Lex
 */
public class ExportDataAction
	extends Action
	implements ActionFactory.IWorkbenchAction {

	public final static String ID = "org.caleydo.rcp.ExportDataAction";

	private Composite parentComposite;

	private IWorkbenchWindow window;

	private Button[] radios = new Button[3];

	private Composite composite;

	private Text txtFileName;

	private String sFileName = "";
	private String sFilePath = "";

	private ArrayList<Integer> genesToExport = null;
	private ArrayList<Integer> experimentsToExport = null;

	/**
	 * Constructor.
	 */
	public ExportDataAction(final Composite parentComposite) {
		super("Export Data");
		this.parentComposite = parentComposite;
	}

	@Override
	public void run() {
		// Check if load data GUI is embedded in a wizard or if a own dialog
		// must be created.
		if (parentComposite == null && window != null) {
			LoadDataDialog loadDataFileDialog = new LoadDataDialog(window.getShell());
			loadDataFileDialog.open();
		}
		else {
			createGUI();
		}
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

	private void createGUI() {
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Button buttonFileChooser = new Button(composite, SWT.PUSH);
		buttonFileChooser.setText("Choose export destination..");

		txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		buttonFileChooser.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				FileDialog fileDialog = new FileDialog(parentComposite.getShell(), SWT.SAVE);
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

	public void execute() {
		for (IDataDomain dataDomain : DataDomainManager.getInstance().getDataDomains()) {
			ASetBasedDataDomain setBasedDataDomain = null;
			if (dataDomain instanceof ASetBasedDataDomain)
				setBasedDataDomain = (ASetBasedDataDomain) dataDomain;
			else
				continue;

			ISet set = setBasedDataDomain.getSet();
			if (set instanceof MetaSet)
				continue;
			if (radios[0].getSelection()) {
				set.export(sFileName, EWhichViewToExport.BUCKET);
			}
			else if (radios[1].getSelection()) {
				set.export(sFileName, EWhichViewToExport.WHOLE_DATA);
			}
			else if (radios[2].getSelection()) {
				set.exportGroups(sFileName, genesToExport, experimentsToExport);
			}

		}

		// TODO: review
		// Application.applicationMode = EApplicationMode.STANDARD;
	}

	/**
	 * For testing purposes
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		ExportDataDialog dialog = new ExportDataDialog(new Shell());
		dialog.open();
	}

	@Override
	public void dispose() {
	}
}
