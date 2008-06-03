package org.caleydo.core.view.swt.data.exchanger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.IStorage;
import org.caleydo.core.data.collection.IVirtualArray;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.view.AView;
import org.caleydo.core.view.IView;
import org.caleydo.core.view.ViewType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

/**
 * Data Exchanger View makes it possible
 * to swap the data of views.
 * 
 * @author Michael Kalkusch
 */
public class SetEditorViewRep 
extends AView 
implements IView {
	
    protected String viewComboItems[];
    
    protected String dataComboItems[];
    
	protected ArrayList<String> arViewData;
	
	protected ArrayList<String> arFilteredViews;
	
	protected ArrayList<String> arSetIDs;
	
	private Vector<Integer> arSetIdList; 

    protected Label labelStorage;
    
    protected Label labelId;
    protected Label labelDimension;
    
    protected boolean bIsDataInitialized = false;
    
    protected Image image;
    
    private Combo viewCombo;
    
    private GC gC;
    
	public SetEditorViewRep(
			IGeneralManager generalManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(generalManager, 
				iViewId, 
				iParentContainerId, 
				sLabel,
				ViewType.SWT_SET_EDITOR);	

		arViewData = new ArrayList<String>();
		arFilteredViews = new ArrayList<String>();
		arSetIDs = new ArrayList<String>();
		arSetIdList = new Vector<Integer>();
	
	}

	private void drawRange(int offset, int lengthVA, int maxLength ) {
		
		int ifirst = (int)((float) offset/ (float) maxLength) * 100;
		int ilast = (int)((float) (offset+lengthVA)/ (float) maxLength) * 100;
		
		gC.drawRectangle( ifirst,0,ilast,20 );
		
		gC.drawLine(100,5,50,25);
	}
	
	public void setPercantage(final ISet set) {
		
		if ( set.getReadToken() ) {
			
			int iDim = set.getDimensions();
			iDim = 1;
			
			for(int i=0; i<iDim; i++)
			{
				IVirtualArray[] vA= set.getVirtualArrayByDim(i);
				IStorage[] sT = set.getStorageByDim(i);
				
				int iOffset = vA[0].getOffset();
				int iLength = vA[0].length();
				int iRealLength = sT[0].getMaximumLengthOfAllArrays();
				
				if ( (iOffset+ iLength) > iRealLength ) 
				{
					drawRange(iOffset,iLength,iOffset+ iLength);
				}
				else
				{
					drawRange(iOffset,iLength,iRealLength);
				}
			
			}
			
			set.returnReadToken();
		}
		
	}
	
	protected void initViewSwtComposit(Composite swtContainer) {

		swtContainer.setLayout(new FillLayout(SWT.VERTICAL));

		viewCombo = new Combo(swtContainer, SWT.SIMPLE |
                SWT.V_SCROLL | SWT.H_SCROLL| SWT.READ_ONLY);
        		
	    //viewCombo.setBounds(50, 85, 150, 65);
	    //viewCombo.setEnabled(true);
		
		labelStorage = new Label(swtContainer,SWT.BORDER);
		labelId = new Label(swtContainer,SWT.BORDER);
		labelDimension = new Label(swtContainer,SWT.BORDER);
		
		Display display = swtContainer.getDisplay();
	    image = new Image (display, 200, 30);
		Color color = display.getSystemColor (SWT.COLOR_RED);
		gC = new GC (image);
		gC.setBackground (color);
		gC.fillRectangle (image.getBounds ());
		gC.drawLine(50,0,50,20);
		//gc.dispose ();
		
		labelStorage.setImage(image);
		labelStorage.setEnabled(true);
		labelStorage.pack();
		
		swtContainer.pack();
		
	    viewCombo.addFocusListener(new FocusAdapter() {
	        public void focusGained(FocusEvent e) {
	        	
	        	if (bIsDataInitialized == true)
	        		return;
	        	
	    	    fillCombos();
	    	    viewCombo.removeAll();
	    	    viewCombo.setItems(viewComboItems);
	    	    bIsDataInitialized = true;
	        }
	    });
	    
	    viewCombo.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {

	    		fillDataSets(viewCombo);
	    	    viewCombo.select(viewCombo.getSelectionIndex());
	    	    
	    	    showDetailsByIndex(viewCombo.getSelectionIndex());
	    	    

//	    		dataCombo.select(arViewData.get(viewCombo.getSelectionIndex()));
	    		//dataCombo.select();
	    	}
	    });
	    
	}

	private void showDetailsByIndex( final int iIndex ) {
		if ( iIndex < 0 ) return;
		
		int iSetIndex = arSetIdList.get(iIndex).intValue();
		
		ISet detailsOnSet = generalManager.
			getSetManager().getItemSet(iSetIndex);
		
		labelDimension.setText( 
				Integer.toString(detailsOnSet.getDimensions()) );
		labelId.setText( 
				Integer.toString(detailsOnSet.getId()) );
		
		setPercantage(detailsOnSet);
	}
	
	public void drawView() {
		
//		generalManager.logMsg(
//				this.getClass().getSimpleName() + 
//				": drawView()", 
//				LoggerType.VERBOSE );		
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
	}
	
	protected void fillCombos() {
		
		Collection<ISet> arSets = generalManager.
			getSetManager().getAllSetItems();
		
		Iterator<ISet> iterSets = arSets.iterator();
		ISet tmpSet = null;

		String sItemText = "";
		arFilteredViews.clear();
		arSetIdList.clear();
		
		while (iterSets.hasNext())
		{
			tmpSet = iterSets.next();
			
			sItemText = tmpSet.getLabel() + "-"+
				Integer.toString( tmpSet.getId()) 
						+ " - " 
						+ tmpSet.getDimensions();
			
			arSetIdList.add(tmpSet.getId());
			
			arFilteredViews.add(sItemText);
		}
		
//		while (iterGLCanvasUsers.hasNext())
//		{
//			tmpGLCanvasUser = iterGLCanvasUsers.next();
//			
//			if (tmpGLCanvasUser.get)
//		}

		viewComboItems = arFilteredViews.toArray(new String[arFilteredViews.size()]);
	}
	
	protected void fillDataSets(Combo viewCombo) {
		
		Collection<ISet> allSets = 
			generalManager.getSetManager().getAllSetItems();
		
		Iterator<ISet> iterSets = allSets.iterator();
		int iTmpSetId = 0;

		arSetIDs.clear();
		
		while (iterSets.hasNext())
		{
			iTmpSetId = iterSets.next().getId();
			
//			//FIXME: why is the loop entered in every iteration?
//			if (iTmpSetId == hashComboText2View.get(arFilteredViews.get(viewCombo.getSelectionIndex())).getDataSetId());
//			{
				arSetIDs.add(Integer.toString(iTmpSetId));
//			}
		}
		
		dataComboItems = arSetIDs.toArray(new String[arSetIDs.size()]);
	}
}
