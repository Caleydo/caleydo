package cerberus.view.gui;

import org.eclipse.swt.widgets.Composite;

import cerberus.data.AUniqueManagedObject;
import cerberus.data.collection.ISet;
import cerberus.manager.IGeneralManager;
import cerberus.manager.event.mediator.IMediatorReceiver;
import cerberus.manager.event.mediator.IMediatorSender;
import cerberus.manager.type.ManagerObjectType;

/**
 * Abstract class that is the base of all view representations.
 * It holds the the own view ID, the parent ID and the attributes that
 * needs to be processed.
 * 
 * @author Michael Kalkusch
 * @author Marc Streit
 */
public abstract class AViewRep 
extends AUniqueManagedObject
implements IView, IMediatorSender, IMediatorReceiver {
	
	public enum ViewType {
		DATA_EXPLORER,
		PATHWAY,
		HEATMAP
	}
	
	public enum EventState {
		NONE,
		NEW_VIRTUAL_ARRAY,
		VIRTUAL_ARRAY_CHANGED,
		DATA_CHANGED
	}
	
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
	
	protected ViewType viewType;
	
	protected EventState eventState;
	
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
			final int iViewId, 
			final int iSetParentContainerId, 
			final String sLabel) {
		
		super ( iViewId, refGeneralManager );
		
		assert iSetParentContainerId != 0 : "Constructor iParentContainerId must not be 0!";
		
		this.iParentContainerId = iSetParentContainerId;
		this.sLabel = sLabel;
		
		eventState = EventState.NONE;
	}
	

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
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#update(java.lang.Object)
	 */
	public void update( Object eventTrigger ) {
		
		//Implemented in subclasses		
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.event.mediator.IMediatorReceiver#updateSelection(java.lang.Object, cerberus.data.collection.ISet)
	 */
	public void updateSelection(Object eventTrigger, ISet updatedSelectionSet) {

		//Implemented in subclasses
	}
	
	public void setViewType(ViewType viewType) {
		
		this.viewType = viewType;
	}
}
