package org.caleydo.rcp.action.file;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.export.SetExporter.EWhichViewToExport;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.dialog.file.LoadDataDialog;
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
 */
public class ExportDataAction
	extends Action
	implements ActionFactory.IWorkbenchAction {

	public final static String ID = "org.caleydo.rcp.ExportDataAction";

	private Composite parentComposite;

	private IWorkbenchWindow window;

	private Button[] radios = new Button[2];

	private Composite composite;

	private Text txtFileName;

	private String sFileName = "";
	private String sFilePath = "";

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
//		boolean doesHeatMapExist = false;
//		boolean doParallelCoordinatesExist = false;
		for (AGLEventListener view : GeneralManager.get().getViewGLCanvasManager().getAllGLEventListeners()) {
			if (view instanceof GLRemoteRendering) {
				bDoesBucketExist = true;
			}
//			if (view instanceof GLHierarchicalHeatMap && !view.isRenderedRemote()) {
//				doesHeatMapExist = true;
//			}
//			if (view instanceof GLParallelCoordinates && !view.isRenderedRemote()) {
//				doParallelCoordinatesExist = true;
//			}
		}

		radios[0] = new Button(composite, SWT.RADIO);
		radios[0].setText("Export Bucket Contents");
		radios[0].setBounds(10, 5, 75, 30);
		if (!bDoesBucketExist) {
			radios[0].setEnabled(false);
		}

		radios[1] = new Button(composite, SWT.RADIO);
		radios[1].setText("Export data as shown in the standalone views");
		radios[1].setBounds(10, 30, 75, 30);
		radios[1].setSelection(true);
//		if (!doesHeatMapExist) {
//			radios[1].setEnabled(false);
//		}
//		else if (!bDoesBucketExist) {
//			radios[1].setSelection(true);
//		}

//		radios[2] = new Button(composite, SWT.RADIO);
//		radios[2].setText("Export Parallel Coordinates");
//		radios[2].setBounds(10, 30, 75, 30);
//		if (!doParallelCoordinatesExist) {
//			radios[1].setEnabled(false);
//		}
//		else if (!bDoesBucketExist) {
//			radios[1].setSelection(true);
//		}

	}

	public void execute() {
		for (ISet set : GeneralManager.get().getSetManager().getAllItems()) {
			if (radios[0].getSelection()) {
				set.export(sFileName, EWhichViewToExport.BUCKET);
			}
			else if (radios[1].getSelection()) {
				set.export(sFileName, EWhichViewToExport.WHOLE_DATA);
			}
//			else if (radios[2].getSelection()) {
//				set.export(sFileName, EWhichViewToExport.PARALLEL_COORDINATES);
//			}

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

		LoadDataDialog dialog = new LoadDataDialog(new Shell());
		dialog.open();
	}

	@Override
	public void dispose() {
	}
}
