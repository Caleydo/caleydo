package cerberus.manager.gui;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.jface.window.WindowManager;
import org.eclipse.jface.window.Window;

import cerberus.manager.GeneralManager;
import cerberus.manager.SWTGUIManager;
import cerberus.manager.base.AbstractManagerImpl;
import cerberus.manager.type.ManagerObjectType;
import cerberus.manager.type.ManagerType;
import cerberus.util.exception.CerberusRuntimeException;
import cerberus.view.gui.Widget;
import cerberus.view.gui.swt.widget.SWTEmbeddedGraphWidget;
import cerberus.view.gui.swt.widget.SWTEmbeddedJoglWidget;
import cerberus.view.gui.swt.widget.SWTNativeWidget;

public class SWTGUIManagerSimple 
extends AbstractManagerImpl
implements SWTGUIManager
{	
	protected Vector<Widget> refWidgetContainer;

	protected Shell refShell;
	
	protected Shell refShell2;

	protected Display refDisplay;
	
	protected Menu refMenuBar;
	
	/**
	 * Call createApplicationWindow() before using this object.
	 * 
	 * @see cerberus.manager.gui.SWTGUIManagerSimple#createApplicationWindow()
	 * 
	 * @param setGeneralManager reference to GeneralManager
	 */
	public SWTGUIManagerSimple(GeneralManager setGeneralManager)
	{
		super(setGeneralManager,
				GeneralManager.iUniqueId_TypeOffset_GuiSWT );

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		refGeneralManager.getSingelton().setSWTGUIManager(this);

		refWidgetContainer = new Vector<Widget>();
	}

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
		
		refMenuBar = createMenuBar(refShell);
			    
		refShell.setMenuBar(refMenuBar); 
		
		setUpLayout();
	}
	
	protected void setUpLayout()
	{
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.makeColumnsEqualWidth = true;
		refShell.setLayout(gridLayout);
	}
	
	protected Menu createMenuBar(Shell refShell)
	{
	    Menu menuBar = new Menu(refShell, SWT.BAR);
	    
	    MenuItem viewMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
	    viewMenuHeader.setText("&Views");

	    Menu viewMenu = new Menu(refShell, SWT.DROP_DOWN);
	    viewMenuHeader.setMenu(viewMenu);

	    MenuItem heatmapViewMenuItem = new MenuItem(viewMenu, SWT.CHECK);
	    heatmapViewMenuItem.setText("&Heatmap");
	    MenuItem scatterplotViewMenuItem = new MenuItem(viewMenu, SWT.CHECK);
	    scatterplotViewMenuItem.setText("&Scatterplot");
	    MenuItem dendrogramViewMenuItem = new MenuItem(viewMenu, SWT.CHECK);
	    dendrogramViewMenuItem.setText("&Dendrogram");
	    MenuItem histogramViewMenuItem = new MenuItem(viewMenu, SWT.CHECK);
	    histogramViewMenuItem.setText("&Histogram");
	    MenuItem pathwayViewMenuItem = new MenuItem(viewMenu, SWT.CHECK);
	    pathwayViewMenuItem.setText("&Pathway");   
	    
	    MenuItem aboutMenu = new MenuItem(menuBar, SWT.CASCADE);
	    aboutMenu.setText("&About");
	    
//	    fileExitItem.addSelectionListener(new fileExitItemListener());
//	    fileSaveItem.addSelectionListener(new fileSaveItemListener());
//	    helpGetHelpItem.addSelectionListener(new helpGetHelpItemListener());
	    
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
}
