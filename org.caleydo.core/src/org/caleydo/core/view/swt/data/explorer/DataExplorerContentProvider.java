package org.caleydo.core.view.swt.data.explorer;

import java.util.Iterator;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import org.caleydo.core.view.swt.data.explorer.model.DeltaEvent;
import org.caleydo.core.view.swt.data.explorer.model.IDeltaListener;
import org.caleydo.core.view.swt.data.explorer.model.AModel;
import org.caleydo.core.view.swt.data.explorer.model.DataCollectionModel;

public class DataExplorerContentProvider 
implements ITreeContentProvider, IDeltaListener
{
	private static Object[] EMPTY_ARRAY = new Object[0];
	protected TreeViewer viewer;
	
	/*
	 * @see IContentProvider#dispose()
	 */
	public void dispose() {}

	/*
	 * @see IContentProvider#inputChanged(Viewer, Object, Object)
	 */
	/**
	* Notifies this content provider that the given viewer's input
	* has been switched to a different element.
	* <p>
	* A typical use for this method is registering the content provider as a listener
	* to changes on the new input (using model-specific means), and deregistering the viewer 
	* from the old input. In response to these change notifications, the content provider
	* propagates the changes to the viewer.
	* </p>
	*
	* @param viewer the viewer
	* @param oldInput the old input element, or <code>null</code> if the viewer
	*   did not previously have an input
	* @param newInput the new input element, or <code>null</code> if the viewer
	*   does not have an input
	*/
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) 
	{
		this.viewer = (TreeViewer)viewer;
		if(oldInput != null) {
			removeListenerFrom((DataCollectionModel)oldInput);
		}
		if(newInput != null) {
			addListenerTo((DataCollectionModel)newInput);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively remove this listener
	 * from each child box of the given box. */
	protected void removeListenerFrom(DataCollectionModel set) 
	{
		set.removeListener(this);
		for (Iterator <DataCollectionModel> iterator = set.getSets().iterator(); iterator.hasNext();) {
			DataCollectionModel aSet = (DataCollectionModel) iterator.next();
			removeListenerFrom(aSet);
		}
	}
	
	/** Because the domain model does not have a richer
	 * listener model, recursively add this listener
	 * to each child box of the given box. */
	protected void addListenerTo(DataCollectionModel set) 
	{
		set.addListener(this);
		for (Iterator <DataCollectionModel> iterator = set.getSets().iterator(); iterator.hasNext();) {
			DataCollectionModel aSet = (DataCollectionModel) iterator.next();
			addListenerTo(aSet);
		}
	}

	/*
	 * @see ITreeContentProvider#getChildren(Object)
	 */
	public Object[] getChildren(Object parentElement) 
	{
		if(parentElement instanceof DataCollectionModel) 
		{
			DataCollectionModel set = (DataCollectionModel)parentElement;
			return concat(set.getSets().toArray(), 
				set.getStorages().toArray(), set.getSelections().toArray());
		}
		return EMPTY_ARRAY;
	}
	
	protected Object[] concat(Object[] object, Object[] more, Object[] more2) 
	{
		Object[] both = new Object[object.length + more.length + more2.length];
		System.arraycopy(object, 0, both, 0, object.length);
		System.arraycopy(more, 0, both, object.length, more.length);
		System.arraycopy(more2, 0, both, object.length + more.length, more2.length);		
		return both;
	}

	/*
	 * @see ITreeContentProvider#getParent(Object)
	 */
	public Object getParent(Object element) 
	{
		if(element instanceof AModel) {
			return ((AModel)element).getParent();
		}
		return null;
	}

	/*
	 * @see ITreeContentProvider#hasChildren(Object)
	 */
	public boolean hasChildren(Object element)
	{
		return getChildren(element).length > 0;
	}

	/*
	 * @see IStructuredContentProvider#getElements(Object)
	 */
	public Object[] getElements(Object inputElement) 
	{
		return getChildren(inputElement);
	}

	/*
	 * @see IDeltaListener#add(DeltaEvent)
	 */
	public void add(DeltaEvent event) 
	{
		Object setModel = ((AModel)event.receiver()).getParent();
		viewer.refresh(setModel, false);
	}

	/*
	 * @see IDeltaListener#remove(DeltaEvent)
	 */
	public void remove(DeltaEvent event) 
	{
		add(event);
	}
}
