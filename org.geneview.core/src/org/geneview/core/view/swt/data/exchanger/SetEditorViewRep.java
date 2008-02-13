/**
 * 
 */
package org.geneview.core.view.swt.data.exchanger;

import java.util.ArrayList;
import java.util.Collection;
//import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

//import org.eclipse.core.internal.resources.Container;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Color;
//import org.eclipse.swt.graphics.ImageData;

import org.geneview.core.data.collection.ISet;
import org.geneview.core.data.collection.IStorage;
import org.geneview.core.data.collection.IVirtualArray;
import org.geneview.core.manager.IGeneralManager;
import org.geneview.core.manager.ILoggerManager.LoggerType;
//import org.geneview.core.util.system.StringConversionTool;
import org.geneview.core.view.AViewRep;
import org.geneview.core.view.IView;
//import org.geneview.core.view.opengl.IGLCanvasUser;
import org.geneview.core.view.ViewType;

/**
 * Data Exchanger View makes it possible
 * to swap the data of views.
 * 
 * @author Michael Kalkusch
 */
public class SetEditorViewRep 
extends AViewRep 
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
    
    private GC refGC;
    
	public SetEditorViewRep(
			IGeneralManager refGeneralManager, 
			int iViewId, 
			int iParentContainerId, 
			String sLabel) {
		
		super(refGeneralManager, 
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
		
		refGC.drawRectangle( ifirst,0,ilast,20 );
		
		refGC.drawLine(100,5,50,25);
	}
	
	public void setPercantage(final ISet refSet) {
		
		if ( refSet.getReadToken() ) {
			
			int iDim = refSet.getDimensions();
			iDim = 1;
			
			for(int i=0; i<iDim; i++)
			{
				IVirtualArray[] refVA= refSet.getVirtualArrayByDim(i);
				IStorage[] refST = refSet.getStorageByDim(i);
				
				int iOffset = refVA[0].getOffset();
				int iLength = refVA[0].length();
				int iRealLength = refST[0].getMaximumLengthOfAllArrays();
				
				if ( (iOffset+ iLength) > iRealLength ) 
				{
					drawRange(iOffset,iLength,iOffset+ iLength);
				}
				else
				{
					drawRange(iOffset,iLength,iRealLength);
				}
			
			}
			
			refSet.returnReadToken();
		}
		
	}
	
	protected void initViewSwtComposit(Composite swtContainer) {

		refSWTContainer.setLayout(new FillLayout(SWT.VERTICAL));

		viewCombo = new Combo(refSWTContainer, SWT.SIMPLE |
                SWT.V_SCROLL | SWT.H_SCROLL| SWT.READ_ONLY);
        		
	    //viewCombo.setBounds(50, 85, 150, 65);
	    //viewCombo.setEnabled(true);
		
		labelStorage = new Label(refSWTContainer,SWT.BORDER);
		labelId = new Label(refSWTContainer,SWT.BORDER);
		labelDimension = new Label(refSWTContainer,SWT.BORDER);
		
		Display display = refSWTContainer.getDisplay();
	    image = new Image (display, 200, 30);
		Color color = display.getSystemColor (SWT.COLOR_RED);
		refGC = new GC (image);
		refGC.setBackground (color);
		refGC.fillRectangle (image.getBounds ());
		refGC.drawLine(50,0,50,20);
		//gc.dispose ();
		
		labelStorage.setImage(image);
		labelStorage.setEnabled(true);
		labelStorage.pack();
		
		refSWTContainer.pack();
		
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
		
		ISet detailsOnSet = generalManager.getSingelton().
			getSetManager().getItemSet(iSetIndex);
		
		labelDimension.setText( 
				Integer.toString(detailsOnSet.getDimensions()) );
		labelId.setText( 
				Integer.toString(detailsOnSet.getId()) );
		
		setPercantage(detailsOnSet);
	}
	
	public void drawView() {
		
		generalManager.getSingelton().logMsg(
				this.getClass().getSimpleName() + 
				": drawView()", 
				LoggerType.VERBOSE );		
	}
	
	public void setAttributes(int iWidth, int iHeight, String sImagePath) {
		
		super.setAttributes(iWidth, iHeight);
	}
	
	protected void fillCombos() {
		
		Collection<ISet> arSets = generalManager.getSingelton().
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
			generalManager.getSingelton().getSetManager().getAllSetItems();
		
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
