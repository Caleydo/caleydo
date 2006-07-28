package cerberus.manager.gui;

import org.eclipse.swt.widgets.Composite;

import cerberus.manager.GeneralManager;
import cerberus.manager.SWTGUIManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.Widget;
import cerberus.view.gui.swt.SWTNativeWidget;
import cerberus.view.gui.swt.SWTEmbeddedJoglWidget;
import cerberus.view.gui.swt.SWTEmbeddedGraphWidget;

public class SWTGUIManagerSimple 
extends AbstractManagerImpl
implements SWTGUIManager
{
	protected Composite refRootComposite;
	
	public SWTGUIManagerSimple(GeneralManager setGeneralManager) 
	{	
		super( setGeneralManager );
		
		assert setGeneralManager != null : "Constructor with null-pointer to singelton";
		
		refGeneralManager.getSingelton().setSWTGUIManager( this );
		
		//refRootComposite = new Composite();
			
	}

	public void createApplicationFrame()
	{
		
	}
	
	public Widget createWidget(final ManagerObjectType useWidgetType)
	{	
		if (useWidgetType.getGroupType() != ManagerType.VIEW)
		{
			throw new CerberusRuntimeException(
					"try to create object with wrong type "
							+ useWidgetType.name());
		}

		//TODO: save id somewhere
		final int iNewId = this.createNewId(useWidgetType);

		switch (useWidgetType)
		{
		case GUI_SWT_NATIVE_WIDGET:
			return new SWTNativeWidget();
		case GUI_SWT_EMBEDDED_JOGL_WIDGET:
			return new SWTEmbeddedJoglWidget();
		case GUI_SWT_EMBEDDED_JGRAPH_WIDGET:
			//return new SWTEmbeddedGraphWidget();
		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useWidgetType.toString() + "]");
		}
	}
	
	public boolean hasItem(int iItemId) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId) {
		// TODO Auto-generated method stub
		return null;
	}

	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public int createNewId(ManagerObjectType setNewBaseType) {
		// TODO Auto-generated method stub
		return 0;
	}
}
