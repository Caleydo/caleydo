package org.caleydo.core.view.swt.glyph;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @see org.caleydo.core.view.IView
 * @author Sauer Stefan
 */
public class GlyphDataExportViewRep
	extends AView
	implements IView
{

	private GlyphManager gman = null;

	SelectionListener listener = null;

	Color headerBackgroundColor = new Color(null, 153, 153, 153);
	Color bodyBackgroundColor = new Color(null, 255, 255, 255);

	Composite compositeScatterplotBody = null;
	Composite compositeGlyphDefinition = null;

	CCombo ccomboScatterplotX = null;
	CCombo ccomboScatterplotY = null;

	CCombo ccomboTopColor = null;
	CCombo ccomboBoxColor = null;
	CCombo ccomboBoxHeight = null;

	private Table viewTable;
	private HashMap<String, GLGlyph> myViews;

	/**
	 * Constructor.
	 */
	public GlyphDataExportViewRep(int iParentContainerId, String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_GLYPH_DATAEXPORT);

		gman = generalManager.getGlyphManager();

		myViews = new HashMap<String, GLGlyph>();

	}

	/**
	 * @see org.caleydo.core.view.AView#retrieveGUIContainer()
	 * @see org.caleydo.core.view.IView#initView()
	 */
	@Override
	protected void initViewSwtComposite(Composite swtContainer)
	{

		initComponents();

	}

	public void drawView()
	{

	}

	private void initComponents()
	{
		parent.setBackground(new Color(null, 255, 0, 0));

		GridLayout layout = new GridLayout();

		createViewTable(parent);

		Button button = new Button(parent, SWT.PUSH);
		button.setText("OK");

		// button.setImage(bgimg);

		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.SAVE);
				dialog.setFilterNames(new String[] { "CSV" });
				dialog.setFilterExtensions(new String[] { "*.csv" });
				// dialog.setFilterPath("c:\\");
				dialog.setFileName("output.csv");

				String filename = dialog.open();

				if (filename == null)
					return;

				writeFile(filename);

			}
		});

		parent.setLayout(layout);
		layout.numColumns = 1;

		parent.getParent().setSize(600, 300);

	}

	private void createViewTable(Composite parent)
	{
		Table table = new Table(parent, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		myViews.clear();

		Collection<AGLEventListener> views = GeneralManager.get().getViewGLCanvasManager()
				.getAllGLEventListeners();

		for (AGLEventListener v : views)
		{
			if (v instanceof GLGlyph)
			{
				TableItem item = new TableItem(table, SWT.NONE);

				GLGlyph view = (GLGlyph) v;

				String text = view.getPersonalName();

				if (text == null)
					text = view.getShortInfo();

				if (text == null)
					item.setText("missing view info");
				else
				{
					item.setText(text);

					myViews.put(text, view);
				}
			}
		}
		viewTable = table;
	}

	/**
	 * This writes all the data to the file. TODO: change this to save storage
	 * if thats finished
	 * 
	 * @param filename
	 */
	protected void writeFile(String filename)
	{
		System.out.println("Save to: " + filename);

		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(filename));

			// for(int i=0;i<10;++i)
			// out.write("test " + i + "\r\n");

			boolean first = true;
			for (TableItem item : viewTable.getItems())
			{

				if (item.getChecked())
				{

					String text = item.getText();

					GLGlyph glyphview = myViews.get(text);

					String content = glyphview.getContainingDataAsCSV(first, text);

					out.write(content);

					first = false;
				}
			}

			out.flush();
			out.close();

		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
