package org.caleydo.core.view;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;

import org.caleydo.core.data.AUniqueManagedObject;
import org.caleydo.core.data.collection.ISet;
import org.caleydo.core.data.collection.SetType;
import org.caleydo.core.data.collection.set.selection.SetSelection;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ILoggerManager.LoggerType;
import org.caleydo.core.manager.data.ISetManager;
import org.caleydo.core.manager.type.ManagerObjectType;
import org.caleydo.core.view.ViewType;
import org.caleydo.core.view.swt.widget.SWTNativeWidget;

/**
 * Abstract class that is the base of all view representations.
 * It holds the the own view ID, the parent ID and the attributes that
 * needs to be processed.
 * 
 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver
 * @see org.caleydo.core.manager.event.mediator.IMediatorSender
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AViewRep 
extends AUniqueManagedObject
implements IViewRep {
	
	private final ISetManager refSetManager;
	
	/**
	 * List for all ISet objects providing data for this ViewRep.
	 */
	protected ArrayList <ISet> alSetData;
	
	/**
	 * List for all ISet objects providing data related to interactive selection for this ViewRep.	
	 */
	protected ArrayList <SetSelection> alSetSelection;
	
	protected final ViewType viewType;
	
	protected int iParentContainerId;
	
	protected String sLabel;
	
	/**
	 * Width of the widget.
	 */
	protected int iWidth;
	
	/**
	 * Height of the widget;
	 */
	protected int iHeight;
	
	protected Composite refSWTContainer;

	/**
	 * Constructor
	 * 
	 * @param refGeneralManager
	 * @param iViewId
	 * @param iParentContainerId
	 * @param sLabel
	 */
	public AViewRep(
			final IGeneralManager refGeneralManager, 
			final int iUniqueId,
			final int iParentContainerId,
			final String sLabel,
			final ViewType viewType ) {
		
		super ( iUniqueId, refGeneralManager );
		
		assert iParentContainerId != 0 : "Constructor iParentContainerId must not be 0!";
		
		this.iParentContainerId = iParentContainerId;
		this.sLabel = sLabel;
		
		this.viewType = viewType;
		
		alSetData = new ArrayList <ISet> ();
		alSetSelection = new ArrayList <SetSelection> ();
		
		refSetManager = refGeneralManager.getSingelton().getSetManager();
	}
	
	protected abstract void initViewSwtComposit(Composite swtContainer);
	
	
	public void setAttributes(int iWidth, int iHeight) {
		
		this.iWidth = iWidth;
		this.iHeight = iHeight;
	}
	
	public final ManagerObjectType getBaseType() {
		return null;
	}
	
	/**
	 * Sets the unique ID of the parent container.
	 * Normally it is already set in the constructor.
	 * Use this method only if you want to change the parent during runtime.
	 * 
	 * @param iParentContainerId
	 */
	public void setParentContainerId(int iParentContainerId) {
		
		this.iParentContainerId = iParentContainerId;
	}
	
	public final void initViewRCP(Composite refSWTContainer) {
		this.refSWTContainer = refSWTContainer;
		initViewSwtComposit( refSWTContainer );
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.IView#initView()
	 */
	public void initView() {
	
		/**
		 * Method uses the parent container ID to retrieve the 
		 * GUI widget by calling the createWidget method from
		 * the SWT GUI Manager.
		 * 
		 * formally this was the method: retrieveGUIContainer() 
		 */
		
		SWTNativeWidget refSWTNativeWidget = (SWTNativeWidget) generalManager
		.getSingelton().getSWTGUIManager().createWidget(
				ManagerObjectType.GUI_SWT_NATIVE_WIDGET,
				iParentContainerId, iWidth, iHeight);

		refSWTContainer = refSWTNativeWidget.getSWTWidget();

		assert refSWTContainer != null : "empty SWT container";
				
		initViewSwtComposit( refSWTContainer );
	}
	
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#update(java.lang.Object)
	 */
	public void updateReceiver( Object eventTrigger ) {
		
		//Implemented in subclasses		
		assert false : "This method must be overloaded in sub-class";
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.manager.event.mediator.IMediatorReceiver#updateReceiver(java.lang.Object, org.caleydo.core.data.collection.ISet)
	 */
	public void updateReceiver(Object eventTrigger, ISet updatedSet) {

		//Implemented in subclasses
		assert false : "This method must be overloaded in sub-class";
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.IView#getLabel()
	 */
	public final String getLabel() {
		
		return sLabel;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.view.IView#getLabel()
	 */
	public final void setLabel(String label) {
				
		this.sLabel = label;
		
		if  (refSWTContainer != null)
		{
			try 
			{
				refSWTContainer.getShell().setText(label);
			}
			catch (SWTException se) {
				
			}
		}
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.caleydo.core.view.IView#setViewType(org.caleydo.core.view.ViewType)
	 */
	public final void setViewType(ViewType viewType) {
		
		assert false : "viewType is final!";
	}
	
	/**
	 * @see org.caleydo.core.view.IViewRep#getViewType()
	 */
	public final ViewType getViewType() {
		return viewType;
	}
	

	/**
	 * @see org.caleydo.core.view.IView#addSetId(int[])
	 */
	public final void addSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = refSetManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
				generalManager.getSingelton().logMsg(
						"addSetId(" + iSet[i] + ") is not registered at SetManager!",
						LoggerType.MINOR_ERROR);
				
				continue;
			}
			
			if ( ! hasSetId_ByReference(refCurrentSet) )
			{
				switch (refCurrentSet.getSetType()) {
				case SET_RAW_DATA:
					alSetData.add(refCurrentSet);
					break;
					
				case SET_SELECTION:
					alSetSelection.add((SetSelection)refCurrentSet);
					break;
					
				default:
					generalManager.getSingelton().logMsg(
							"addSetId() unsupported SetType!",
							LoggerType.ERROR);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
				generalManager.getSingelton().logMsg(
						"addSetId(" + iSet[i] + ") ISet is already registered!",
						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
	}
	
	/**
	 * @see org.caleydo.core.view.IView#removeAllSetIdByType(org.caleydo.core.data.collection.SetType)
	 */
	public final void removeAllSetIdByType( SetType setType ) {
		
		switch (setType) {
		case SET_RAW_DATA:
			alSetData.clear();
			break;
			
		case SET_SELECTION:
			alSetSelection.clear();
			break;
			
		default:
			generalManager.getSingelton().logMsg(
					"addSetId() unsupported SetType!",
					LoggerType.ERROR);
		} // switch (setType) {
	}
	
	/**
	 * @see org.caleydo.core.view.IView#removeSetId(int[])
	 */
	public final void removeSetId( int [] iSet) {
		
		assert iSet != null : "Can not handle null-pointer!";
		
		for ( int i=0; i < iSet.length; i++)
		{
			ISet refCurrentSet = refSetManager.getItemSet(iSet[i]);
			
			if ( refCurrentSet == null ) 
			{
				generalManager.getSingelton().logMsg(
						"removeSetId(" + iSet[i] + ") is not registered at SetManager!",
						LoggerType.MINOR_ERROR);
				
				continue;
			}
			
			if ( hasSetId_ByReference(refCurrentSet) )
			{
				switch (refCurrentSet.getSetType()) {
				case SET_RAW_DATA:
					alSetData.remove(refCurrentSet);
					break;
					
				case SET_SELECTION:
					alSetSelection.remove(refCurrentSet);
					break;
					
				default:
					generalManager.getSingelton().logMsg(
							"removeSetId() unsupported SetType!",
							LoggerType.ERROR);
				} // switch (refCurrentSet.getSetType()) {
					
			} //if ( ! hasSetId_ByReference(refCurrentSet) )
			else 
			{ 
				generalManager.getSingelton().logMsg(
						"removeSetId(" + iSet[i] + ") ISet was not registered!",
						LoggerType.MINOR_ERROR);
			} //if ( ! hasSetId_ByReference(refCurrentSet) ) {...} else {...}
			
		} //for ( int i=0; i < iSet.length; i++)
		
	}
	

	/**
	 * @see org.caleydo.core.view.IView#getAllSetId()
	 */
	public final synchronized int[] getAllSetId() {
		
		//FIXME: thread safe access to ArrayLists!
		int iTotalSizeResultArray = alSetData.size() + alSetSelection.size();
		
		/* allocate int[] and copy from Arraylist*/
		int [] resultArray = new int [iTotalSizeResultArray];
		
		/* early exit */
		if ( iTotalSizeResultArray == 0) 
		{
			return resultArray;
		}
		
		int i=0;		
		Iterator <ISet> iter = alSetData.iterator();		
		for (;iter.hasNext();i++)
		{
			resultArray[i] = iter.next().getId();
		}
		
		Iterator <SetSelection> iterSelectionSet = alSetSelection.iterator();		
		for (;iterSelectionSet.hasNext();i++)
		{
			resultArray[i] = iterSelectionSet.next().getId();
		}
		
		return resultArray;
	}
	

	/**
	 * @see org.caleydo.core.view.IView#hasSetId(int)
	 */
	public final boolean hasSetId( int iSetId) {
		ISet refCurrentSet = refSetManager.getItemSet(iSetId);
		
		if ( refCurrentSet == null )
		{
			return false;
		}
		
		return hasSetId_ByReference(refCurrentSet);
	}
	
	
	/**
	 * Test both ArrayList's alSetData and alSetSelection for refSet.
	 * 
	 * @param refSet test if this ISet is refered to
	 * @return TRUE if exists in any of the two ArrayList's
	 */
	public final boolean hasSetId_ByReference(final ISet refSet) {
		
		assert refSet != null : "Can not handle null-pointer";
			
		if ( alSetData.contains(refSet) ) 
		{
			return true;
		}
		if ( alSetSelection.contains(refSet) ) 
		{
			return true;
		}
		
		return false;			
	}
}
