package org.caleydo.core.manager.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import org.caleydo.core.manager.AManager;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.type.EManagerObjectType;
import org.caleydo.core.manager.type.EManagerType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.ISWTWidget;
import org.caleydo.core.view.swt.widget.ASWTWidget;
import org.caleydo.core.view.swt.widget.SWTEmbeddedGraphWidget;
import org.caleydo.core.view.swt.widget.SWTEmbeddedJoglWidget;
import org.caleydo.core.view.swt.widget.SWTNativeWidget;
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

/**
 * The SWTGUIManager is responsible for the creation and the administration of
 * the windows and composites. Also the overall layout is defined here and the
 * menus are added to the windows.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class SWTGUIManager
	extends AManager
	implements ISWTGUIManager
{

	public static final int PROGRESSBAR_MAXIMUM = 200;

	/**
	 * SWT Display represents a thread.
	 */
	protected final Display display;

	protected Composite composite;

	protected final HashMap<Integer, Shell> windowMap;

	protected final HashMap<Integer, Composite> compositeMap;

	protected final Vector<ISWTWidget> widgetMap;

	protected Shell loadingProgressBarWindow;

	protected ProgressBar loadingProgressBar;

	/**
	 * Constructor.
	 */
	public SWTGUIManager(final IGeneralManager generalManager)
	{

		super(generalManager, IGeneralManager.iUniqueId_TypeOffset_GUI_SWT,
				EManagerType.VIEW_GUI_SWT);

		widgetMap = new Vector<ISWTWidget>();

		display = new Display();

		windowMap = new HashMap<Integer, Shell>();

		compositeMap = new HashMap<Integer, Composite>();

		createLoadingProgressBar();
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.ISWTGUIManager#createWindow()
	 */
	public Shell createWindow()
	{

		// Register shell in the window map
		final int iUniqueId = this.createId(EManagerObjectType.GUI_SWT_WINDOW);

		// use default layout
		return createWindow(iUniqueId, "Caleydo", "ROW VERTICAL");
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.ISWTGUIManager#createWindow(int, Stringt,
	 * Stringt)
	 */
	public Shell createWindow(int iUniqueId, String sLabel, String sLayoutAttributes)
	{

		assert iUniqueId != 0 : "createWindow() iUniqueId must not be 0!";

		Shell newShell = new Shell(display);
		newShell.setLayout(new GridLayout());
		newShell.setMaximized(true);
		newShell.setImage(new Image(display, "resources/icons/caleydo/caleydo16.ico"));
		newShell.setText(sLabel);

		windowMap.put(iUniqueId, newShell);

		setUpLayout(newShell, sLayoutAttributes);

		return newShell;
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.ISWTGUIManager#createComposite(int, int,
	 * Stringt)
	 */
	public void createComposite(int iUniqueId, int iUniqueParentContainerId,
			String layoutAttributes)
	{

		// TODO check if parent exists
		Shell parentWindow = windowMap.get(iUniqueParentContainerId);

		Composite newComposite = new Composite(parentWindow, SWT.NONE);

		compositeMap.put(iUniqueId, newComposite);

		setUpLayout(newComposite, layoutAttributes);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		newComposite.setLayoutData(gridData);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.ISWTGUIManager#createWidget(org.caleydo.core
	 * .manager.type.ManagerObjectType, int, int, int)
	 */
	public ISWTWidget createWidget(final EManagerObjectType useWidgetType,
			int iUniqueParentContainerId, int iWidth, int iHeight)
	{

		// TODO Check if window id is valid and print error message

		// Check if the parent is a window
		composite = windowMap.get(iUniqueParentContainerId);

		if (composite == null)
		{
			// Check if the parent is a composite
			composite = compositeMap.get(iUniqueParentContainerId);

			if (composite == null)
			{
				// generalManager.logMsg( getClass().getSimpleName() +
				// ".createWidget(" +
				// useWidgetType.toString() + ", parentId=" +
				// iUniqueParentContainerId +
				// ", iWidth, iHeight) parent SWT canvas does not exist!",
				// LoggerType.MINOR_ERROR_XML);
				return null;
			}
		}

		return (createWidget(useWidgetType, composite, iWidth, iHeight));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.ISWTGUIManager#createWidget(org.caleydo.core
	 * .manager.type.ManagerObjectType, org.eclipse.swt.widgets.Composite, int,
	 * int)
	 */
	public synchronized ISWTWidget createWidget(final EManagerObjectType useWidgetType,
			final Composite externalParentComposite, int iWidth, int iHeight)
	{

		assert externalParentComposite != null : "can not handel null-pointer";

		final int iUniqueId = this.createId(useWidgetType);
		ASWTWidget newSWTWidget;

		switch (useWidgetType)
		{
			case GUI_SWT_NATIVE_WIDGET:
				newSWTWidget = new SWTNativeWidget(externalParentComposite);
				newSWTWidget.setId(iUniqueId);
				widgetMap.add(newSWTWidget);
				return newSWTWidget;
			case GUI_SWT_EMBEDDED_JOGL_WIDGET:
				newSWTWidget = new SWTEmbeddedJoglWidget(externalParentComposite);
				widgetMap.add(newSWTWidget);
				return newSWTWidget;
			case GUI_SWT_EMBEDDED_JGRAPH_WIDGET:
				newSWTWidget = new SWTEmbeddedGraphWidget(externalParentComposite);
				widgetMap.add(newSWTWidget);
				return newSWTWidget;
			default:
				throw new CaleydoRuntimeException(
						"StorageManagerSimple.createView() failed due to unhandled type ["
								+ useWidgetType.toString() + "]");
		}
	}

	protected void setUpLayout(Composite newComposite, String sLayoutAttributes)
	{

		String layoutType; // GRID or ROW
		String layoutDirection;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;

		StringTokenizer token = new StringTokenizer(sLayoutAttributes,
				IGeneralManager.sDelimiter_Parser_DataItems);

		layoutType = token.nextToken();

		if (layoutType.equals("ROW"))
		{
			layoutDirection = token.nextToken();
			if (layoutDirection.equals("HORIZONTAL"))
			{
				gridLayout.numColumns += 1;
			}
		}
		else if (layoutType.equals("GRID"))
		{
			// real GRID layout is now implemented yet
		}
		else
		{
			// ERROR
		}

		// gridLayout.makeColumnsEqualWidth = true;
		newComposite.setLayout(gridLayout);
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.ISWTGUIManager#runApplication()
	 */
	public void runApplication()
	{

		Iterator<Shell> shellIterator;
		Shell currentShell;

		// Close loading progress bar after bootstrapping is completed.
		setProgressbarVisible(false);

		shellIterator = windowMap.values().iterator();
		while (shellIterator.hasNext())
		{
			currentShell = shellIterator.next();
			currentShell.setVisible(true);// open();
		}

		generalManager.getViewGLCanvasManager().createAnimator();

		shellIterator = windowMap.values().iterator();

		while (shellIterator.hasNext())
		{
			currentShell = shellIterator.next();
			while (!currentShell.isDisposed())
			{
				if (!display.readAndDispatch())
					display.sleep();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.caleydo.core.manager.ISWTGUIManager#createLoadingProgressBar()
	 */
	public void createLoadingProgressBar()
	{

		loadingProgressBarWindow = new Shell(display, SWT.TITLE | SWT.BORDER);
		loadingProgressBarWindow.setMaximized(false);
		loadingProgressBarWindow.setText("Loading org.caleydo.core...");

		loadingProgressBar = new ProgressBar(loadingProgressBarWindow, SWT.SMOOTH);
		loadingProgressBar.setBounds(10, 10, 430, 40);
		loadingProgressBar.setSelection(10);

		loadingProgressBarWindow.setBounds(500, 500, 460, 90);
		loadingProgressBarWindow.open();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.ISWTGUIManager#setLoadingProgressBarPercentage
	 * (int)
	 */
	public boolean setLoadingProgressBarPercentage(int iPercentage)
	{

		if (loadingProgressBar == null)
			return false;

		if (iPercentage < 0 || iPercentage > PROGRESSBAR_MAXIMUM)
			return false;

		loadingProgressBar.setSelection(iPercentage);
		loadingProgressBar.update();

		return true;
	}

	public synchronized String setLoadingProgressBarTitle(final String sText,
			final int iPosition)
	{

		assert sText != null : "can not set 'null' text";

		if (loadingProgressBarWindow == null)
			return "--";

		loadingProgressBar.setSelection(iPosition);

		String sCurrentText = loadingProgressBarWindow.getText();

		loadingProgressBarWindow.setText(sText);
		loadingProgressBarWindow.update();

		return sCurrentText;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.caleydo.core.manager.ISWTGUIManager#getLoadingProgressBarPercentage()
	 */
	public synchronized int getLoadingProgressBarPercentage()
	{

		return loadingProgressBar.getSelection();
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

	public boolean registerItem(Object registerItem, int iItemId)
	{

		// TODO Auto-generated method stub
		return false;
	}

	public boolean unregisterItem(int iItemId)
	{

		// TODO Auto-generated method stub
		return false;
	}

	public synchronized void setProgressbarVisible(final boolean state)
	{

		if (this.loadingProgressBarWindow.isVisible() == state)
		{
			/* state is already set */
			return;
		}

		/* toggle current state.. */
		if (!loadingProgressBarWindow.isVisible())
		{
			this.display.wake();
		}
		loadingProgressBarWindow.setVisible(state);
		loadingProgressBar.setVisible(state);
	}
}