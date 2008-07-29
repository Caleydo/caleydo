package org.caleydo.core.view.swt.glyph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.specialized.glyph.EGlyphSettingIDs;
import org.caleydo.core.manager.specialized.glyph.IGlyphManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.opengl.canvas.glyph.GlyphAttributeType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * 
 * @see org.caleydo.core.view.IView
 * 
 * @author Sauer Stefan
 */
public class GlyphMappingConfigurationViewRep 
extends AView 
implements IView {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6402651939169536561L;
	private ArrayList<String> columnNames    = null;
	private HashMap<String, Integer> extColNumToArrayIndex = null;
	private HashMap<Integer, String> arrayIndexToExtColNum = null;

	SelectionListener listener = null;
	CCombo cComboD1_1 = null;
	CCombo cComboD1_2 = null;
	
	CCombo cComboD2_1 = null;
	CCombo cComboD2_2 = null;
	CCombo cComboD2_3 = null;

	
	Color headerColor = new Color(null, 153, 153, 153);
	
	public GlyphMappingConfigurationViewRep(IGeneralManager generalManager, 
			int iViewId, int iParentContainerId, String sLabel) {
		
		super(generalManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_GLYPH_MAPPINGCONFIGURATION);
		
		columnNames    = new ArrayList<String>();
		//columnIndices = new ArrayList<Integer>();
		extColNumToArrayIndex = new HashMap<String, Integer>();
		arrayIndexToExtColNum = new HashMap<Integer, String>();
		


	}
	
	/**
	 * 
	 * @see org.caleydo.core.view.AView#retrieveGUIContainer()
	 * @see org.caleydo.core.view.IView#initView()
	 */
	protected void initViewSwtComposit(Composite swtContainer) {
		
		IGlyphManager man = generalManager.getGlyphManager();
		
		//String stc = man.getSetting(EGlyphSettingIDs.TOPCOLOR);
		//int itc = Integer.parseInt(stc);
		
		//get all combo box entrys
		Iterator it = man.getGlyphAttributes().iterator();
		int counter=0;
		while(it.hasNext()) {
			GlyphAttributeType at = (GlyphAttributeType)it.next();
			columnNames.add(at.getName());
			//columnIndices.add();
			String colnum = String.valueOf(at.getExternalColumnNumber());
			extColNumToArrayIndex.put(colnum , counter);
			arrayIndexToExtColNum.put(counter, colnum);
			counter++;
		}
		/*
		test2 test = new test2();
		Shell awts = swtContainer.getShell();
		
		test.updateShell( awts );
		*/
		
		

		initComponents(man);

	}
	
	
	
	

	public void drawView() {
		
	}
	
	
	
	

	private void initComponents(IGlyphManager man) {
		
		// create listener
		listener = new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}

			public void widgetSelected(SelectionEvent event) {
				if(event.widget == cComboD1_1) { //scatterplot x
					int selectedindex = cComboD1_1.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					//System.out.println("scatterX " + selectedCol + " " + columnNames.get(selectedindex));
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTX, selectedCol);
				}
				if(event.widget == cComboD1_2) { //scatterplot y
					int selectedindex = cComboD1_2.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					//System.out.println("scatterY " + selectedCol + " " + columnNames.get(selectedindex));
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.SCATTERPLOTY, selectedCol);
				}
				if(event.widget == cComboD2_1) { //topcolor
					int selectedindex = cComboD2_1.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					//System.out.println("topcolor " + selectedCol + " " + columnNames.get(selectedindex));
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.TOPCOLOR, selectedCol);
				}
				if(event.widget == cComboD2_2) { //boxheight
					int selectedindex = cComboD2_2.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					//System.out.println("topheight " + selectedCol + " " + columnNames.get(selectedindex));
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.BOXHEIGHT, selectedCol);
				}
				if(event.widget == cComboD2_3) { //topcolor
					int selectedindex = cComboD2_3.getSelectionIndex();
					String selectedCol = arrayIndexToExtColNum.get(selectedindex);
					//System.out.println("boxcolor " + selectedCol + " " + columnNames.get(selectedindex));
					generalManager.getGlyphManager().setSetting(EGlyphSettingIDs.BOXCOLOR, selectedCol);
				}

			}
		
		};
		
		

		GridData gridData = makeGridData();
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		swtContainer.setLayoutData( gridData );
		swtContainer.setLayout(gridLayout);
		gridLayout.marginTop = 0;
		gridData.widthHint += 10; 

		
		//swtContainer.setLayout(rowLayout0);
		swtContainer.setBackground(new Color(null, 255, 0, 0));
		
		
		
		//header 1
		Composite compositeH1 = new Composite(swtContainer, SWT.NONE);
		RowLayout rowLayoutH1 = new RowLayout();
		//rowLayoutH1.pack = true;
		rowLayoutH1.fill = true;
		
		compositeH1.setLayout(rowLayoutH1);
		compositeH1.setLayoutData( makeGridData() );
		
		compositeH1.setBackground(headerColor);
		compositeH1.setSize(new Point(800,30));
		
		Label labelH1 = new Label(compositeH1, SWT.NONE);
		labelH1.setText("Scatterplot Axis Definition");
		labelH1.setBackground(headerColor);
		labelH1.setSize(new Point(800,30));
		//labelH1.computeSize(600, 30);
		//labelH1.
		
		
		
		//data 1
		{
			Composite  block           = new Composite(swtContainer, SWT.BORDER);
			GridData   blockGridData   = makeGridData();
			GridLayout blockGridLayout = new GridLayout();
			
			blockGridLayout.numColumns = 2;
			block.setLayout(blockGridLayout);
			block.setLayoutData(blockGridData);
			
			{
				String id = man.getSetting(EGlyphSettingIDs.SCATTERPLOTX);
				int sid = extColNumToArrayIndex.get(id);
				cComboD1_1 = makeComboGridRow(block, sid, "X Axis");
			}
			{
				String id = man.getSetting(EGlyphSettingIDs.SCATTERPLOTY);
				int sid = extColNumToArrayIndex.get(id);
				cComboD1_2 = makeComboGridRow(block, sid, "Y Axis");
			}
			
		}
		
		
		
		//header2
		Composite compositeH2 = new Composite(swtContainer, SWT.NONE);
		RowLayout rowLayoutH2 = new RowLayout();
		//rowLayoutH2.pack = true;
		compositeH2.setLayout(rowLayoutH2);
		compositeH2.setLayoutData( makeGridData() );
		compositeH2.setBackground(headerColor);
		compositeH2.setSize(new Point(600,30));
		
		Label labelH2 = new Label(compositeH2, SWT.NONE);
		labelH2.setBackground(headerColor);
		labelH2.setText("Glyph Mappging Definition");
		labelH2.setSize(new Point(600,30));
		

		{
			Composite  block           = new Composite(swtContainer, SWT.BORDER);
			GridData   blockGridData   = makeGridData();
			GridLayout blockGridLayout = new GridLayout();
			
			blockGridLayout.numColumns = 2;
			block.setLayout(blockGridLayout);
			block.setLayoutData(blockGridData);
			
			{
				String id = man.getSetting(EGlyphSettingIDs.TOPCOLOR);
				int sid = extColNumToArrayIndex.get(id);
				cComboD2_1 = makeComboGridRow(block, sid, "Topcolor");
			}
			{
				String id = man.getSetting(EGlyphSettingIDs.BOXHEIGHT);
				int sid = extColNumToArrayIndex.get(id);
				cComboD2_2 = makeComboGridRow(block, sid, "Height");
			}
			{
				String id = man.getSetting(EGlyphSettingIDs.BOXCOLOR);
				int sid = extColNumToArrayIndex.get(id);
				cComboD2_3 = makeComboGridRow(block, sid, "Color Main Element");
			}
			
		}

		
	  }
	
	private GridData makeGridData() {
		GridData gridData = new GridData();
		gridData.widthHint = 400;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.CENTER;

		return gridData;
	}
	
	private CCombo makeComboGridRow(Composite block, int selectedid, String text) {
		Label label = new Label(block, SWT.NONE);
		label.setText(text);
		label.setAlignment( SWT.CENTER );
		label.setSize(200, 30);
		
		CCombo cComboD1_2 = new CCombo(block, SWT.NONE);
		cComboD1_2.setEditable(false);
		cComboD1_2.removeAll();
		for(int i=0;i<columnNames.size();++i)
			cComboD1_2.add(columnNames.get(i));
		if(selectedid >= 0)
			cComboD1_2.select(selectedid);
		cComboD1_2.addSelectionListener(listener);
		return cComboD1_2;
	}
	
	
	
	  
}
