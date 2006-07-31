package cerberus.manager.gui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jgraph.graph.GraphConstants;

import com.sun.opengl.util.Animator;

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

public class SWTGUIManagerSimple extends AbstractManagerImpl implements
		SWTGUIManager
{
	protected Vector<Widget> refWidgetContainer;

	protected Shell refShell;

	protected Display refDisplay;


	public SWTGUIManagerSimple(GeneralManager setGeneralManager)
	{
		super(setGeneralManager);

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		refGeneralManager.getSingelton().setSWTGUIManager(this);

		refWidgetContainer = new Vector<Widget>();

		createApplicationFrame();
	}

	public void createApplicationFrame()
	{
		refDisplay = new Display();

		refShell = new Shell(refDisplay);
		refShell.setLayout(new GridLayout());
		refShell.setSize(410, 1000);
	}

	public void runApplication()
	{
		refShell.open();
		while (!refShell.isDisposed())
		{
			if (!refDisplay.readAndDispatch())
				refDisplay.sleep();
		}
		refDisplay.dispose();
	}

	public Widget createWidget(final ManagerObjectType useWidgetType)
	{
		if (useWidgetType.getGroupType() != ManagerType.GUI_SWT)
		{
			throw new CerberusRuntimeException(
					"try to create object with wrong type "
							+ useWidgetType.name());
		}

		// TODO: save id somewhere
		final int iNewId = this.createNewId(useWidgetType);

		switch (useWidgetType)
		{
		case GUI_SWT_NATIVE_WIDGET:
			SWTNativeWidget newSWTNativeWidget = 
				new SWTNativeWidget(new Composite(refShell, SWT.NONE));
			refWidgetContainer.add(newSWTNativeWidget);
			return newSWTNativeWidget;
		case GUI_SWT_EMBEDDED_JOGL_WIDGET:
			SWTEmbeddedJoglWidget newSWTEmbeddedJoglWidget = new SWTEmbeddedJoglWidget(
					new Composite(refShell, SWT.EMBEDDED));
			refWidgetContainer.add(newSWTEmbeddedJoglWidget);
			return newSWTEmbeddedJoglWidget;
		case GUI_SWT_EMBEDDED_JGRAPH_WIDGET:
			SWTEmbeddedGraphWidget newSWTEmbeddedGraphWidget = new SWTEmbeddedGraphWidget(
					new Composite(refShell, SWT.EMBEDDED));
			refWidgetContainer.add(newSWTEmbeddedGraphWidget);
			return newSWTEmbeddedGraphWidget;
		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useWidgetType.toString() + "]");
		}
	}

	public boolean hasItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Object getItem(int iItemId)
	{
		// TODO Auto-generated method stub
		return null;
	}

	public int size()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public ManagerObjectType getManagerType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public int createNewId(ManagerObjectType setNewBaseType)
	{
		// TODO Auto-generated method stub
		return 0;
	}
}
