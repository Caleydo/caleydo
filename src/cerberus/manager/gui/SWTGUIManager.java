package cerberus.manager.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

import cerberus.manager.IGeneralManager;
import cerberus.manager.ISWTGUIManager;
import cerberus.manager.base.AAbstractManager;
import cerberus.manager.command.factory.CommandFactory;
import cerberus.manager.type.ManagerObjectType;
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
 * @author Michael Kalkusch
 * 
 * 
 */
public class SWTGUIManager 
extends AAbstractManager 
implements ISWTGUIManager {

	public static final int PROGRESSBAR_MAXIMUM = 200;
	
	/**
	 * SWT Display represents a thread.
	 */
	protected final Display refDisplay;

	protected Composite refComposite;

	protected Menu refMenuBar;

	protected final HashMap<Integer, Shell> refWindowMap;

	protected final HashMap<Integer, Composite> refCompositeMap;

	protected final Vector<ISWTWidget> refWidgetMap;
	
	protected Shell refLoadingProgressBarWindow;
	
	protected ProgressBar refLoadingProgressBar;

	/**
	 * Call createApplicationWindow() before using this object.
	 * 
	 * @see cerberus.manager.gui.SWTGUIManager#createApplicationWindow()
	 * 
	 * @param setGeneralManager Reference to IGeneralManager
	 */
	public SWTGUIManager(IGeneralManager setGeneralManager) {

		super(setGeneralManager, IGeneralManager.iUniqueId_TypeOffset_GUI_SWT);

		assert setGeneralManager != null : "Constructor with null-pointer to singelton";

		refGeneralManager.getSingelton().setSWTGUIManager(this);

		refWidgetMap = new Vector<ISWTWidget>();

		refDisplay = new Display();

		refWindowMap = new HashMap<Integer, Shell>();

		refCompositeMap = new HashMap<Integer, Composite>();
		
		createLoadingProgressBar();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#createWindow()
	 */
	public Shell createWindow() {

		// Register shell in the window map
		final int iUniqueId = this.createNewId(ManagerObjectType.GUI_WINDOW);

		// use default layout
		return createWindow(iUniqueId, "Cerberus", "ROW VERTICAL");
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#createWindow(int, java.lang.String, java.lang.String)
	 */
	public Shell createWindow(int iUniqueId,
			String sLabel,
			String sLayoutAttributes) {

		assert iUniqueId != 0 :"createWindow() iUniqueId must not be 0!";
		
		Shell refNewShell = new Shell(refDisplay);
		refNewShell.setLayout(new GridLayout());
		refNewShell.setMaximized(true);
		refNewShell.setImage(new Image(refDisplay, "data/icons/Cerberus.ico"));
		refNewShell.setText(sLabel);

		refWindowMap.put(iUniqueId, refNewShell);

		//refMenuBar = createMenuBar(refShell);
		//refShell.setMenuBar(refMenuBar); 

		setUpLayout(refNewShell, sLayoutAttributes);

		return refNewShell;
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#createComposite(int, int, java.lang.String)
	 */
	public void createComposite(int iUniqueId, int iUniqueParentContainerId,
			String refLayoutAttributes) {

		// TODO check if parent exists
		Shell parentWindow = refWindowMap.get(iUniqueParentContainerId);

		Composite newComposite = new Composite(parentWindow, SWT.NONE);

		refCompositeMap.put(iUniqueId, newComposite);

		setUpLayout(newComposite, refLayoutAttributes);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		newComposite.setLayoutData(gridData);
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#createWidget(cerberus.manager.type.ManagerObjectType, int, int, int)
	 */
	public ISWTWidget createWidget(final ManagerObjectType useWidgetType,
			int iUniqueParentContainerId, int iWidth, int iHeight) {

		// TODO Check if window id is valid and print error message

		// Check if the parent is a window
		refComposite = refWindowMap.get(iUniqueParentContainerId);

		if (refComposite == null)
		{
			// Check if the parent is a composite
			refComposite = refCompositeMap.get(iUniqueParentContainerId);
		}

		return (createWidget(useWidgetType, refComposite, iWidth, iHeight));

	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#createWidget(cerberus.manager.type.ManagerObjectType, org.eclipse.swt.widgets.Composite, int, int)
	 */
	public ISWTWidget createWidget(final ManagerObjectType useWidgetType,
			final Composite refExternalParentComposite, int iWidth, int iHeight) {

		final int iUniqueId = this.createNewId(useWidgetType);
		ASWTWidget newSWTWidget;

		switch (useWidgetType)
		{
		case GUI_SWT_NATIVE_WIDGET:
			newSWTWidget = new SWTNativeWidget(refExternalParentComposite,
					iWidth, iHeight);
			newSWTWidget.setId(iUniqueId);
			refWidgetMap.add(newSWTWidget);
			return newSWTWidget;
		case GUI_SWT_EMBEDDED_JOGL_WIDGET:
			newSWTWidget = new SWTEmbeddedJoglWidget(
					refExternalParentComposite, iWidth, iHeight);
			refWidgetMap.add(newSWTWidget);
			return newSWTWidget;
		case GUI_SWT_EMBEDDED_JGRAPH_WIDGET:
			newSWTWidget = new SWTEmbeddedGraphWidget(
					refExternalParentComposite, iWidth, iHeight);
			refWidgetMap.add(newSWTWidget);
			return newSWTWidget;
		default:
			throw new CerberusRuntimeException(
					"StorageManagerSimple.createView() failed due to unhandled type ["
							+ useWidgetType.toString() + "]");
		}
	}

	protected void setUpLayout(Composite refNewComposite,
			String sLayoutAttributes) {

		String layoutType; // GRID or ROW
		String layoutDirection;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		StringTokenizer token = new StringTokenizer(sLayoutAttributes,
				CommandFactory.sDelimiter_Parser_DataItems);

		layoutType = token.nextToken();

		if (layoutType.equals("ROW"))
		{
			layoutDirection = token.nextToken();
			if (layoutDirection.equals("HORIZONTAL"))
			{
				gridLayout.numColumns += 1;
			}
		} else if (layoutType.equals("GRID"))
		{
			// real GRID layout is now implemented yet
		} else
		{
			// ERROR
		}

		//gridLayout.makeColumnsEqualWidth = true;
		refNewComposite.setLayout(gridLayout);

		//		FillLayout fillLayout = new FillLayout();
		//		
		//		if (layoutDirection == LayoutDirection.HORIZONTAL)
		//		{
		//			fillLayout.type = SWT.HORIZONTAL;
		//		}
		//		else if (layoutDirection == LayoutDirection.VERTICAL)
		//		{
		//			fillLayout.type = SWT.VERTICAL;
		//		} 
	}

	protected Menu createMenuBar(Shell refShell) {

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

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#runApplication()
	 */
	public void runApplication() {

		Iterator<Shell> shellIterator;
		Shell refCurrentShell;

		// Close loading progress bar after bootstrapping is completed.
		refLoadingProgressBarWindow.close();
		refLoadingProgressBar = null;

		shellIterator = refWindowMap.values().iterator();
		while (shellIterator.hasNext())
		{
			refCurrentShell = shellIterator.next();
			refCurrentShell.open();
		}

		shellIterator = refWindowMap.values().iterator();
		
		// TODO Don't know if this is ok like this!
		while (shellIterator.hasNext())
		{
			refCurrentShell = shellIterator.next();
			while (!refCurrentShell.isDisposed())
			{
				if (!refDisplay.readAndDispatch())
					refDisplay.sleep();
			}
		}

		refDisplay.dispose();
	}

	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#createLoadingProgressBar()
	 */
	public void createLoadingProgressBar() {
				
		refLoadingProgressBarWindow = new Shell(refDisplay, SWT.TITLE | SWT.BORDER);
		refLoadingProgressBarWindow.setMaximized(false);
		refLoadingProgressBarWindow.setText("Loading cerberus...");
		
		refLoadingProgressBar = 
			new ProgressBar(refLoadingProgressBarWindow, SWT.SMOOTH );
		refLoadingProgressBar.setBounds(10, 10, 430, 40);
		refLoadingProgressBar.setSelection(10);
		
		refLoadingProgressBarWindow.setBounds(500, 500, 460, 90);
		refLoadingProgressBarWindow.open();			
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#setLoadingProgressBarPercentage(int)
	 */
	public boolean setLoadingProgressBarPercentage(int iPercentage) {
		
		if (refLoadingProgressBar == null)
			return false;
		
		if (iPercentage < 0 || iPercentage > PROGRESSBAR_MAXIMUM)
			return false;
		
		refLoadingProgressBar.setSelection(iPercentage);
		refLoadingProgressBar.update();
			
		return true;
	}
	
	public String setLoadingProgressBarTitle(final String sText, final int iPosition ) {
		
		if (refLoadingProgressBarWindow == null)
			return null;
		
		refLoadingProgressBar.setSelection( iPosition );
		
		String sCurrentText = refLoadingProgressBarWindow.getText();
		
		refLoadingProgressBarWindow.setText( sText );
		refLoadingProgressBarWindow.update();
					
		return sCurrentText;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see cerberus.manager.ISWTGUIManager#getLoadingProgressBarPercentage()
	 */
	public int getLoadingProgressBarPercentage() {
		
		return refLoadingProgressBar.getSelection();
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

	public boolean registerItem(Object registerItem, int iItemId,
			ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId, ManagerObjectType type) {

		// TODO Auto-generated method stub
		return false;
	}
}