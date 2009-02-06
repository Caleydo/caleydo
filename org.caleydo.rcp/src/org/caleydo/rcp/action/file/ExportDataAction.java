package org.caleydo.rcp.action.file;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.caleydo.core.command.ECommandType;
import org.caleydo.core.command.data.CmdDataCreateSet;
import org.caleydo.core.command.data.CmdDataCreateStorage;
import org.caleydo.core.command.data.parser.CmdLoadFileLookupTable;
import org.caleydo.core.command.data.parser.CmdLoadFileNStorages;
import org.caleydo.core.data.collection.EExternalDataRepresentation;
import org.caleydo.core.data.collection.ESetType;
import org.caleydo.core.data.collection.INumericalStorage;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.core.view.opengl.canvas.storagebased.AStorageBasedView;
import org.caleydo.core.view.opengl.canvas.storagebased.heatmap.GLHierarchicalHeatMap;
import org.caleydo.core.view.opengl.canvas.storagebased.parcoords.GLParallelCoordinates;
import org.caleydo.rcp.dialog.file.FileLoadDataDialog;
import org.caleydo.rcp.image.IImageKeys;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Action responsible for importing data to current Caleydo project.
 * 
 * @author Marc Streit
 */
public class ExportDataAction
	extends Action
	implements ActionFactory.IWorkbenchAction
{

	public final static String ID = "org.caleydo.rcp.FileLoadDataAction";

	private Composite parentComposite;

	private IWorkbenchWindow window;

	private Button[] radios = new Button[2];

	private Composite composite;

	private Text txtFileName;

	private String sFileName = "";
	private String sFilePath = "";

	private int iCreatedSetID = -1;

	/**
	 * Constructor.
	 */
	public ExportDataAction(final Composite parentComposite)
	{
		super("Load Data");
		setId(ID);
		setToolTipText("Import data from text file");
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.caleydo.rcp",
				IImageKeys.FILE_OPEN_XML_CONFIG_FILE));

		this.parentComposite = parentComposite;

	}

	/**
	 * Constructor.
	 */
	// public ExportDataAction(final Composite parentComposite, String
	// sInputFile)
	// {
	// this(parentComposite);
	// this.sInputFile = sInputFile;
	// }
	@Override
	public void run()
	{
		// Check if load data GUI is embedded in a wizard or if a own dialog
		// must be created.
		if (parentComposite == null && window != null)
		{
			FileLoadDataDialog loadDataFileDialog = new FileLoadDataDialog(window.getShell());
			loadDataFileDialog.open();
		}
		else
		{
			createGUI();
		}
	}

	private void createGUI()
	{
		composite = new Composite(parentComposite, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);

		Button buttonFileChooser = new Button(composite, SWT.PUSH);
		buttonFileChooser.setText("Choose export destination..");

		txtFileName = new Text(composite, SWT.BORDER);
		txtFileName.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		buttonFileChooser.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent event)
			{

				FileDialog fileDialog = new FileDialog(parentComposite.getShell(), SWT.SAVE);
				fileDialog.setText("Save");
				fileDialog.setFilterPath(sFilePath);
				String[] filterExt = { "*.csv", "*.txt", "*.*" };
				fileDialog.setFilterExtensions(filterExt);
				fileDialog.setFileName("caleydo_export.csv");
				sFileName = fileDialog.open();

				txtFileName.setText(sFileName);

			}
		});

		boolean bDoesBucketExist = false;
		boolean bDoesStandaloneStorageBasedExist = false;
		for (AGLEventListener view : GeneralManager.get().getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (view instanceof GLRemoteRendering)
			{
				bDoesBucketExist = true;
			}
			if ((view instanceof GLParallelCoordinates || view instanceof GLHierarchicalHeatMap)
					&& !view.isRenderedRemote())
			{
				bDoesStandaloneStorageBasedExist = true;
			}
		}

		radios[0] = new Button(composite, SWT.RADIO);
		radios[0].setText("Export Bucket Contents");
		radios[0].setBounds(10, 5, 75, 30);
		if (!bDoesBucketExist)
		{
			radios[0].setEnabled(false);
		}
		else
		{
			radios[0].setSelection(true);
		}

		radios[1] = new Button(composite, SWT.RADIO);
		radios[1].setText("Export All Data");
		radios[1].setBounds(10, 30, 75, 30);
		if (!bDoesStandaloneStorageBasedExist)
		{
			radios[1].setEnabled(false);
		}
		else if (!bDoesBucketExist)
		{
			radios[1].setSelection(true);
		}

	}

	public void execute()
	{
		for (ISet set : GeneralManager.get().getSetManager().getAllItems())
		{
			if (set.getSetType() == ESetType.GENE_EXPRESSION_DATA)
			{
				if (radios[0].getSelection())
					set.export(sFileName, true);
				else
					set.export(sFileName, false);
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
	public static void main(String[] args)
	{

		FileLoadDataDialog dialog = new FileLoadDataDialog(new Shell());
		dialog.open();
	}

	@Override
	public void dispose()
	{
	}
}
