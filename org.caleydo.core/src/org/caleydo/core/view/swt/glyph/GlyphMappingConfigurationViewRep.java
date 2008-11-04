package org.caleydo.core.view.swt.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.data.GlyphAttributeType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @see org.caleydo.core.view.IView
 * @author Sauer Stefan
 */
public class GlyphMappingConfigurationViewRep
	extends AView
	implements IView
{

	private static final long serialVersionUID = 6402651939169536561L;

	private IGlyphManager gman = null;

	private ArrayList<String> columnNames = null;
	private HashMap<String, Integer> extColNumToArrayIndex = null;
	private HashMap<Integer, String> arrayIndexToExtColNum = null;

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

	/**
	 * Constructor.
	 */
	public GlyphMappingConfigurationViewRep(int iParentContainerId, String sLabel)
	{
		super(iParentContainerId, sLabel, ViewType.SWT_GLYPH_MAPPINGCONFIGURATION);

		columnNames = new ArrayList<String>();
		extColNumToArrayIndex = new HashMap<String, Integer>();
		arrayIndexToExtColNum = new HashMap<Integer, String>();

		gman = generalManager.getGlyphManager();
	}

	/**
	 * @see org.caleydo.core.view.AView#retrieveGUIContainer()
	 * @see org.caleydo.core.view.IView#initView()
	 */
	@Override
	protected void initViewSwtComposite(Composite swtContainer)
	{

		// get all combo box entrys
		Iterator<GlyphAttributeType> it = gman.getGlyphAttributes().iterator();
		int counter = 0;
		while (it.hasNext())
		{
			GlyphAttributeType at = it.next();

			if (at.doesAutomaticAttribute())
				continue;

			columnNames.add(at.getName());
			// columnIndices.add();
			String colnum = String.valueOf(at.getExternalColumnNumber());
			extColNumToArrayIndex.put(colnum, counter);
			arrayIndexToExtColNum.put(counter, colnum);
			counter++;
		}

		initComponents();

	}

	public void drawView()
	{

	}

	private void initComponents()
	{
		// create listener
		listener = new SelectionListener()
		{

			public void widgetDefaultSelected(SelectionEvent arg0)
			{

			}

			public void widgetSelected(SelectionEvent event)
			{
				if (event.widget == ccomboScatterplotX)
				{
					int selectedindex = ccomboScatterplotX.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTX,
							selectedCol);
				}

				if (event.widget == ccomboScatterplotY)
				{
					int selectedindex = ccomboScatterplotY.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);

					System.out.println(selectedCol);

					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTY,
							selectedCol);
				}
				if (event.widget == ccomboTopColor)
				{
					int selectedindex = ccomboTopColor.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.TOPCOLOR,
							selectedCol);
				}
				if (event.widget == ccomboBoxHeight)
				{
					int selectedindex = ccomboBoxHeight.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.BOXHEIGHT,
							selectedCol);
				}
				if (event.widget == ccomboBoxColor)
				{
					int selectedindex = ccomboBoxColor.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.BOXCOLOR,
							selectedCol);
				}
			}

		};

		GridLayout layout = new GridLayout();
		parent.setBackground(new Color(null, 255, 255, 255));

		addHeaderScatterplot(parent);
		addBodyScatterplotAxisDefinition(parent);
		addHeaderGlyphDefinition(parent);
		addBodyGlyphDefinition(parent);

		parent.setLayout(layout);
		layout.numColumns = 1;

		parent.getParent().setSize(600, 300);

	}

	private void switchBody(Composite comp)
	{

		if (comp.getVisible() == false)
		{

			GridData data = (GridData) comp.getLayoutData();
			data.exclude = false;

			comp.setVisible(true);
			parent.layout(false);

		}
		else
		{

			GridData data = (GridData) comp.getLayoutData();
			data.exclude = true;

			comp.setVisible(false);
			parent.layout(false);

		}
	}

	private void addHeaderScatterplot(Composite parent)
	{
		Composite comp = getHeaderComposite(parent);
		// Image bgimg = new Image(null,
		// "C:\\dev\\Eclipse workspace\\test_window_001\\src\\icon.gif");

		Button button = new Button(comp, SWT.PUSH);
		button.setText("+");

		// button.setImage(bgimg);

		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				switchBody(compositeScatterplotBody);
			}
		});
		CLabel label = new CLabel(comp, SWT.LEFT);
		label.setText("Scatterplot Axis Definition");
		label.setBackground(headerBackgroundColor);
		label.computeSize(300, 30);
	}

	private void addHeaderGlyphDefinition(Composite parent)
	{
		Composite comp = getHeaderComposite(parent);

		Button button = new Button(comp, SWT.PUSH);
		button.setText("+");
		button.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				switchBody(compositeGlyphDefinition);
			}
		});
		CLabel label = new CLabel(comp, SWT.LEFT);
		label.setText("Glyph Definition");
		label.setBackground(headerBackgroundColor);
		label.computeSize(300, 30);
	}

	private Composite getHeaderComposite(Composite parent)
	{
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		Composite comp = new Composite(parent, SWT.NO_FOCUS);
		comp.setLayout(layout);
		comp.setLayoutData(gd);
		comp.setBackground(headerBackgroundColor);

		return comp;
	}

	private void addBodyGlyphDefinition(Composite parent)
	{
		Composite comp = getBodyComposite(parent);

		GridLayout layout = new GridLayout();
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		// 1st line
		{
			String id = gman.getSetting(EGlyphSettingIDs.TOPCOLOR);
			int sid = extColNumToArrayIndex.get(id);
			ccomboTopColor = makeLabelComboBoxLine(layout, gd, comp, sid, "Top Color: ");
		}

		// 2nd line
		{
			String id = gman.getSetting(EGlyphSettingIDs.BOXCOLOR);
			int sid = extColNumToArrayIndex.get(id);
			ccomboBoxColor = makeLabelComboBoxLine(layout, gd, comp, sid, "Box Color: ");
		}

		// 3rd line
		{
			String id = gman.getSetting(EGlyphSettingIDs.BOXHEIGHT);
			int sid = extColNumToArrayIndex.get(id);
			ccomboBoxHeight = makeLabelComboBoxLine(layout, gd, comp, sid, "Box Height: ");
		}

		compositeGlyphDefinition = comp;
	}

	private void addBodyScatterplotAxisDefinition(Composite parent)
	{
		Composite comp = getBodyComposite(parent);

		GridLayout layout = new GridLayout();
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		// 1st line
		{
			String id = gman.getSetting(EGlyphSettingIDs.SCATTERPLOTX);
			int sid = extColNumToArrayIndex.get(id);
			ccomboScatterplotX = makeLabelComboBoxLine(layout, gd, comp, sid, "X Axis: ");
		}

		// 2nd line
		{
			String id = gman.getSetting(EGlyphSettingIDs.SCATTERPLOTY);
			int sid = extColNumToArrayIndex.get(id);
			ccomboScatterplotY = makeLabelComboBoxLine(layout, gd, comp, sid, "Y Axis: ");
		}
		compositeScatterplotBody = comp;
	}

	private Composite getBodyComposite(Composite parent)
	{
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;

		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		Composite comp = new Composite(parent, SWT.NO_FOCUS);
		comp.setLayout(layout);
		comp.setLayoutData(gd);
		comp.setBackground(bodyBackgroundColor);

		return comp;
	}

	private CCombo makeLabelComboBoxLine(GridLayout layout, GridData gd, Composite block,
			int selectedid, String text)
	{
		CLabel label = new CLabel(block, SWT.LEFT);
		label.setText(text);
		label.setBackground(bodyBackgroundColor);
		label.setLayout(layout);
		label.setLayoutData(gd);

		CCombo combo = new CCombo(block, SWT.NONE);
		combo.setEditable(false);
		combo.setLayout(layout);
		combo.setLayoutData(gd);

		combo.removeAll();
		for (int i = 0; i < columnNames.size(); ++i)
			combo.add(columnNames.get(i));
		if (selectedid >= 0)
			combo.select(selectedid);

		combo.addSelectionListener(listener);

		return combo;
	}

}
