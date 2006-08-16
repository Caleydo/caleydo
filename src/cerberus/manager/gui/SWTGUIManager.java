/*
 * Project: GenView
 * 
 * Author: Marc Streit
 * 
 * Creation date: 26-07-2006
 *  
 */

package cerberus.manager.gui;

import java.util.HashMap;
import java.util.Iterator;
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

import cerberus.manager.IGeneralManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.swt.ISWTWidget;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;
import cerberus.view.gui.swt.widget.SWTNativeWidget;
import cerberus.view.gui.swt.widget.ASWTWidget;
import cerberus.manager.gui.CerberusWindow;

/**
 * The SWTGUIManager is responsible for the creation 
 * and the administration of the windows and composites.
 * Also the overall layout is defined here and the
 * menues are added to the windows.
 * 
 * @author Marc Streit
 */
public class SWTGUIManager 
extends AAbstractManager
implements ISWTGUIManager
{	
	protected final WindowManager refWindowManager;
	
	protected final Vector<ISWTWidget> refWidgetContainer;

	/**
	 * SWT Display represents a thread.
	 */
	protected final Display refDisplay;
	
	protected Shell refShell;
	
	protected Menu refMenuBar;
	
	protected final HashMap<Integer, Shell> refWindowMap; 
	
	/**
	 * Call createApplicationWindow() before using this object.
	 * 
	 * @see cerberus.manager.gui.SWTGUIManager#createApplicationWindow()
	 * 
	 * @param setGeneralManager Reference to IGeneralManager
	 */
	public SWTGUIManager(IGeneralManager setGeneralManager)
	{
		super(setGeneralManager,
				IGeneralManager.iUniqueId_TypeOffset_GuiSWT );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		refGeneralManager.getSingelton().setSWTGUIManager(this);

		refWidgetContainer = new Vector<ISWTWidget>();
		
		refWindowManager = new WindowManager();
		
		refDisplay = new Display();
		
		refWindowMap = new HashMap<Integer, Shell>();
	}

	/**
	 * Method cretes an unique window ID and calls createWindow(iUniqueId)
	 * 
	 * @return Newly created shell.
	 */
	public Shell createWindow()
	{
		// Register shell in the window map
		final int iUniqueId = this.createNewId(ManagerObjectType.GUI_WINDOW);
		
		return createWindow(iUniqueId);
	}
	
	/**
	 * Method takes a window ID and creates a shell using this ID.
	 * Also the layout is set here.
	 * 
	 * @return Newly created shell.
	 */	
	public Shell createWindow(int iUniqueId)
	{
		Shell refNewShell = new Shell(refDisplay);
		refNewShell.setLayout(new GridLayout());
		refNewShell.setMaximized(true);
		refNewShell.setImage(new Image(refDisplay, "data/icons/Cerberus.ico"));

		refWindowMap.put(iUniqueId, refNewShell);
		
		//refMenuBar = createMenuBar(refShell);
		//refShell.setMenuBar(refMenuBar); 
		
		setUpLayout(refNewShell);
		
		//FIXME: just for testing
		refShell = refNewShell;
		
		return refNewShell;
	}
	
	public Shell createWindow(final ManagerObjectType useWindowType)
	{
		Shell refNewShell = new Shell(refDisplay);
		refNewShell.setLayout(new GridLayout());
		refNewShell.setMaximized(true);
		refNewShell.setImage(new Image(refDisplay, "data/icons/Cerberus.ico"));
		
		// Register shell in the window map
		final int iUniqueId = this.createNewId(useWindowType);
		refWindowMap.put(iUniqueId, refNewShell);
		
		//refMenuBar = createMenuBar(refShell);
		//refShell.setMenuBar(refMenuBar); 
		
		setUpLayout(refNewShell);
		
		return refNewShell;
	}
	
	public ISWTWidget createWidget(final ManagerObjectType useWidgetType)
	{
		final int iUniqueId = this.createNewId(useWidgetType);
		ASWTWidget newSWTWidget;
		
		switch (useWidgetType)
		{
		case GUI_SWT_NATIVE_WIDGET:
			newSWTWidget = new SWTNativeWidget(refShell);
			newSWTWidget.setId(iUniqueId);
			refWidgetContainer.add(newSWTWidget);
			return newSWTWidget;
		case GUI_SWT_EMBEDDED_JOGL_WIDGET:
			newSWTWidget = new SWTEmbeddedJoglWidget(refShell);
			refWidgetContainer.add(newSWTWidget);
			return newSWTWidget;
		case GUI_SWT_EMBEDDED_JGRAPH_WIDGET:
			newSWTWidget = new SWTEmbeddedGraphWidget(refShell);
			refWidgetContainer.add(newSWTWidget);
			return newSWTWidget;
		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useWidgetType.toString() + "]");
		}
	}
	
	public ISWTWidget createWidget(final ManagerObjectType useWidgetType, int iUniqueParentWindowId)
	{
		final int iUniqueId = this.createNewId(useWidgetType);
		ASWTWidget newSWTWidget;
		
		// TODO Check if window id is valid and print error message
		refShell = refWindowMap.get(iUniqueParentWindowId);
		
		switch (useWidgetType)
		{
		case GUI_SWT_NATIVE_WIDGET:
			newSWTWidget = new SWTNativeWidget(refShell);
			newSWTWidget.setId(iUniqueId);
			refWidgetContainer.add(newSWTWidget);
			return newSWTWidget;
		case GUI_SWT_EMBEDDED_JOGL_WIDGET:
			newSWTWidget = new SWTEmbeddedJoglWidget(refShell);
			refWidgetContainer.add(newSWTWidget);
			return newSWTWidget;
		case GUI_SWT_EMBEDDED_JGRAPH_WIDGET:
			newSWTWidget = new SWTEmbeddedGraphWidget(refShell);
			refWidgetContainer.add(newSWTWidget);
			return newSWTWidget;
		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useWidgetType.toString() + "]");
		}
	}
	
//	/**
//	 * Method to initialize this application window.
//	 * Must be called before using this class.
//	 * 
//	 */
//	public void createApplicationWindow()
//	{		
//		refShell = new Shell(refDisplay);
//		refShell.setLayout(new GridLayout());
//		refShell.setMaximized(true);
//		refShell.setImage(new Image(refDisplay, "data/icons/Cerberus.ico"));
//		
//		refMenuBar = createMenuBar(refShell);
//		refShell.setMenuBar(refMenuBar); 
//		
//		//setUpLayout();
//	}
	
	protected void setUpLayout(Shell refNewShell)
	{
		GridLayout gridLayout = new GridLayout();
		//gridLayout.numColumns = 1;
		gridLayout.makeColumnsEqualWidth = true;
		refNewShell.setLayout(gridLayout);
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
		Iterator<Shell> shellIterator;
		Shell refCurrentShell;
		
		shellIterator = refWindowMap.values().iterator();
		while(shellIterator.hasNext())
		{
			refCurrentShell = shellIterator.next();
			refCurrentShell.open();
		}
		
		shellIterator = refWindowMap.values().iterator();
		
		// TODO Don't know if this is ok like this!
		while(shellIterator.hasNext())
		{
			refCurrentShell = shellIterator.next();
			while (!refCurrentShell.isDisposed())
			{
				if (!refDisplay.readAndDispatch())
					refDisplay.sleep();
			}
		}
		
		refDisplay.dispose();
		
//		refShell.open();
//		while (!refShell.isDisposed())
//		{
//			if (!refDisplay.readAndDispatch())
//				refDisplay.sleep();
//		}
//
//		refDisplay.dispose();
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