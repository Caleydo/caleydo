/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.manager.gui;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.window.WindowManager;

import cerberus.manager.GeneralManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.ISWTWidget;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;
import cerberus.view.gui.swt.widget.SWTNativeWidget;
import cerberus.view.gui.swt.widget.ASWTWidget;

/**
 * The SWTGUIManager is responsible for the creation 
 * and the administration of the windows and composites.
 * Also the overall layout is defined here and the
 * menues are added to the windows.
 * 
 * @author Marc Streit
 */
public class SWTGUIManager 
extends AbstractManagerImpl
implements ISWTGUIManager
{	
	protected final WindowManager refWindowManager;
	
	protected final Vector<ISWTWidget> refWidgetContainer;

	protected Shell refShell;

	protected Display refDisplay;
	
	protected Menu refMenuBar;
	
	/**
	 * Call createApplicationWindow() before using this object.
	 * 
	 * @see cerberus.manager.gui.SWTGUIManager#createApplicationWindow()
	 * 
	 * @param setGeneralManager Reference to GeneralManager
	 */
	public SWTGUIManager(GeneralManager setGeneralManager)
	{
		super(setGeneralManager,
				GeneralManager.iUniqueId_TypeOffset_GuiSWT );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		refGeneralManager.getSingelton().setSWTGUIManager(this);

		refWidgetContainer = new Vector<ISWTWidget>();
		
		refWindowManager = new WindowManager();
	}

//	public void createWindow()
//	{
//		refWindowManager.add()
//	}
	
	/**
	 * Method to initialize this application window.
	 * Must be called before using this class.
	 * 
	 */
	public void createApplicationWindow()
	{
		//refWidgetContainer = new Vector<Widget>();
		
		refDisplay = new Display();
		
		refShell = new Shell(refDisplay);
		refShell.setLayout(new GridLayout());
		refShell.setMaximized(true);
		refShell.setImage(new Image(refDisplay, "data/icons/Cerberus.ico"));
		
		refMenuBar = createMenuBar(refShell);
		refShell.setMenuBar(refMenuBar); 
		
		setUpLayout();
	}
	
	protected void setUpLayout()
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		refShell.setLayout(gridLayout);
	}
	
	protected Menu createMenuBar(Shell refShell)
	{
	    Menu menuBar = new Menu(refShell, SWT.BAR);
	    
	    MenuItem fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
	    fileMenuHeader.setText("&File");
	    Menu fileMenu = new Menu(refShell, SWT.DROP_DOWN);
	    fileMenuHeader.setMenu(fileMenu);

	    MenuItem newWindowMenuItem = new MenuItem(fileMenu, SWT.NULL);
	    newWindowMenuItem.setText("&New window");
	    
	    MenuItem addViewMenuItem = new MenuItem(fileMenu, SWT.NULL);
	    addViewMenuItem.setText("&Add view");
//	    addViewMenuItem.addSelectionListener(new SelectionAdapter()
//	    {
//	    	public void widgetSelected(SelectionEvent event)
//	    	{
//	    		if (((MenuItem) event.widget).getText().equals("&Add view"))
//	    		{
//	    			CmdViewNewPathway commandNewPathway = new CmdViewNewPathway(refGeneralManager);
//	    			commandNewPathway.doCommand();
//	    		}
//	    	}
//	    });
	    
	    MenuItem exitMenuItem = new MenuItem(fileMenu, SWT.NULL);
	    exitMenuItem.setText("&Exit");
	    
	    MenuItem aboutMenu = new MenuItem(menuBar, SWT.CASCADE);
	    aboutMenu.setText("&About");
	    
	    return menuBar;
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

	public ISWTWidget createWidget(final ManagerObjectType useWidgetType)
	{
		if (useWidgetType.getGroupType() != ManagerType.GUI_SWT)
		{
			throw new CerberusRuntimeException(
					"try to create object with wrong type "
							+ useWidgetType.name());
		}

		// TODO: save id somewhere
		final int iNewId = this.createNewId(useWidgetType);
		final Composite composite;
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 700;
		
		switch (useWidgetType)
		{
		case GUI_SWT_NATIVE_WIDGET:
			composite = new Composite(refShell, SWT.NONE);
			composite.setLayoutData(gridData);
			ISWTWidget newSWTNativeWidget = 
				new SWTNativeWidget(composite);
			refWidgetContainer.add(newSWTNativeWidget);
			return newSWTNativeWidget;
		case GUI_SWT_EMBEDDED_JOGL_WIDGET:
			composite = new Composite(refShell, SWT.EMBEDDED);
			composite.setLayoutData(gridData);
			ISWTWidget newSWTEmbeddedJoglWidget = 
				new SWTEmbeddedJoglWidget(composite);
			refWidgetContainer.add(newSWTEmbeddedJoglWidget);
			return newSWTEmbeddedJoglWidget;
		case GUI_SWT_EMBEDDED_JGRAPH_WIDGET:
			composite = new Composite(refShell, SWT.EMBEDDED);
			ISWTWidget newSWTEmbeddedGraphWidget = 
				new SWTEmbeddedGraphWidget(composite);
			composite.setLayoutData(gridData);
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

	public Shell getActiveWindow()
	{
		return refShell;
	}
}