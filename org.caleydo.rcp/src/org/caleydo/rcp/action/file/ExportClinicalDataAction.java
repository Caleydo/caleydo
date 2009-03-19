package org.caleydo.rcp.action.file;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.rcp.action.toolbar.view.glyph.OpenDataExportAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Action responsible for exporting clinical data.
 * 
 * @author Sauer Stefan
 */
public class ExportClinicalDataAction
	extends Action
	implements ActionFactory.IWorkbenchAction {

	public final static String ID = "org.caleydo.rcp.ExportClinicalDataAction";

	private Composite parentComposite;

	private IWorkbenchWindow window;

	private Button[] radios = new Button[4];

	private Composite composite;

	private Text txtFileName;

	private String sFileName = "";
	private String sFilePath = "";

	private GLGlyph glyphview = null;

	/**
	 * Constructor.
	 */
	public ExportClinicalDataAction(final Composite parentComposite, final int iViewID) {
		super("Load Data");
		setId(ID);
		setToolTipText("Export Clinical Data");
		setImageDescriptor(ImageDescriptor.createFromImage(new ResourceLoader().getImage(PlatformUI
			.getWorkbench().getDisplay(), OpenDataExportAction.ICON)));

		this.parentComposite = parentComposite;

		for (AGLEventListener view : GeneralManager.get().getViewGLCanvasManager().getAllGLEventListeners()) {
			if (view instanceof GLGlyph) {
				if (view.getID() == iViewID) {
					glyphview = (GLGlyph) view;
				}
			}
		}

		if (glyphview == null)
			throw new IllegalStateException(
				"Clinical Data Export in Toolbar wants to export a view witch doesn't exist");

	}

	@Override
	public void run() {
		// Check if load data GUI is embedded in a wizard or if a own dialog
		// must be created.
		if (parentComposite == null && window != null) {
		}
		else {
			createGUI();
		}
	}

	private void createGUI() {

		final String name = glyphview.getPersonalName();

		composite = new Composite(parentComposite, SWT.NO_RADIO_GROUP);
		composite.setLayout(new GridLayout(2, false));

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
					"caleydo_export_" + new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())
						+ "_clinical_" + name + ".csv";

				fileDialog.setFileName(sFilePath);
				sFileName = fileDialog.open();

				txtFileName.setText(sFileName);

			}
		});

		radios[0] = new Button(composite, SWT.RADIO);
		radios[0].setText("Export Selected Data");
		radios[0].setBounds(10, 30, 75, 30);
		radios[0].setEnabled(true);
		radios[0].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				radios[0].setSelection(true);
				radios[1].setSelection(false);
			}

		});

		radios[1] = new Button(composite, SWT.RADIO);
		radios[1].setText("Export All Data");
		radios[1].setBounds(10, 30, 75, 30);
		radios[1].setEnabled(true);
		radios[1].setSelection(true);
		radios[1].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				radios[0].setSelection(false);
				radios[1].setSelection(true);
			}

		});

		// radios[2] = new Button(composite, SWT.RADIO);
		// radios[2].setText("Export Original Data");
		// radios[2].setBounds(10, 30, 75, 30);
		// radios[2].setEnabled(true);
		// radios[2].addSelectionListener(new SelectionListener()
		// {
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e)
		// {
		// }
		//
		// @Override
		// public void widgetSelected(SelectionEvent e)
		// {
		// radios[2].setSelection(true);
		// radios[3].setSelection(false);
		// }
		//
		// });
		//
		// radios[3] = new Button(composite, SWT.RADIO);
		// radios[3].setText("Export Modefied Data");
		// radios[3].setBounds(10, 30, 75, 30);
		// radios[3].setEnabled(true);
		// radios[3].setSelection(true);
		// radios[3].addSelectionListener(new SelectionListener()
		// {
		//
		// @Override
		// public void widgetDefaultSelected(SelectionEvent e)
		// {
		// }
		//
		// @Override
		// public void widgetSelected(SelectionEvent e)
		// {
		// radios[2].setSelection(false);
		// radios[3].setSelection(true);
		// }
		//
		// });
	}

	public void execute() {
		glyphview.exportAsCSV(sFileName, true, radios[0].getSelection(), false);
	}

	@Override
	public void dispose() {
	}
}
