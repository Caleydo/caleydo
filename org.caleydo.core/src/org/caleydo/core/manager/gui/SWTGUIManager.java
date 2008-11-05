package org.caleydo.core.manager.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import org.caleydo.core.manager.IGeneralManager;
import org.caleydo.core.manager.ISWTGUIManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.id.EManagedObjectType;
import org.caleydo.core.util.exception.CaleydoRuntimeException;
import org.caleydo.core.view.swt.ISWTWidget;
import org.caleydo.core.view.swt.widget.ASWTWidget;
import org.caleydo.core.view.swt.widget.SWTEmbeddedGraphWidget;
import org.caleydo.core.view.swt.widget.SWTEmbeddedJoglWidget;
import org.caleydo.core.view.swt.widget.SWTNativeWidget;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;

/**
 * The SWTGUIManager is responsible for the creation and the administration of
 * the windows and composites. Also the overall layout is defined here and the
 * menus are added to the windows.
 * 
 * This class is not derived from AManager since it does not manages
 * IUniqueObjects.
 * 
 * @author Marc Streit
 * @author Michael Kalkusch
 */
public class SWTGUIManager
	implements ISWTGUIManager
{
	protected IGeneralManager generalManager;

	/**
	 * SWT Display represents a thread.
	 */
	protected Display display = null;

	protected Composite composite;

	protected final HashMap<Integer, Shell> windowMap;

	protected final HashMap<Integer, Composite> compositeMap;

	protected final Vector<ISWTWidget> widgetMap;

	protected Shell loadingProgressBarWindow;

	protected ProgressBar loadingProgressBar;

	protected Label loadingProgressBarLabel;

	protected IStatusLineManager externalRCPStatusLine;

	/**
	 * Constructor.
	 */
	public SWTGUIManager()
	{
		generalManager = GeneralManager.get();

		widgetMap = new Vector<ISWTWidget>();
		windowMap = new HashMap<Integer, Shell>();
		compositeMap = new HashMap<Integer, Composite>();

		// Only create popup window with progress bar when not in RCP mode
		if (generalManager.isStandalone())
		{
			display = new Display();
			createLoadingProgressBar();
		}
	}

	@Override
	public int createWindow(String sLabel, String sLayoutAttributes)
	{
		Shell newShell = new Shell(display);
		newShell.setLayout(new GridLayout());
		newShell.setMaximized(true);
		newShell.setImage(new Image(display, "resources/icons/caleydo/caleydo16.gif"));
		newShell.setText(sLabel);

		int iShellID = generalManager.getIDManager().createID(
				EManagedObjectType.GUI_SWT_WINDOW);

		windowMap.put(iShellID, newShell);

		setUpLayout(newShell, sLayoutAttributes);

		return iShellID;
	}

	@Override
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

	@Override
	public ISWTWidget createWidget(final EManagedObjectType useWidgetType,
			int iUniqueParentContainerId)
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

		return (createWidget(useWidgetType, composite));
	}

	@Override
	public synchronized ISWTWidget createWidget(final EManagedObjectType useWidgetType,
			final Composite externalParentComposite)
	{
		ASWTWidget newSWTWidget;

		switch (useWidgetType)
		{
			case GUI_SWT_NATIVE_WIDGET:
				newSWTWidget = new SWTNativeWidget(externalParentComposite);
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

	@Override
	public void runApplication()
	{
		Iterator<Shell> shellIterator;
		Shell currentShell;

		// Close loading progress bar in standalone mode after bootstrapping is
		// completed.
		if (loadingProgressBarWindow != null)
			setProgressBarVisible(false);

		shellIterator = windowMap.values().iterator();
		while (shellIterator.hasNext())
		{
			currentShell = shellIterator.next();
			currentShell.setVisible(true);// open();
		}

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

	private void createLoadingProgressBar()
	{
		loadingProgressBarWindow = new Shell(display, SWT.TITLE | SWT.BORDER);
		loadingProgressBarWindow.setMaximized(false);
		loadingProgressBarWindow.setText("Loading Caleydo...");
//		loadingProgressBarWindow.setImage(new Image(display,
//				this.getClass().getClassLoader().getResourceAsStream(
//						"resources/icons/caleydo/caleydo16.gif")));

		loadingProgressBar = new ProgressBar(loadingProgressBarWindow, SWT.SMOOTH);
		loadingProgressBar.setBounds(10, 10, 430, 40);
		loadingProgressBar.setSelection(10);

		loadingProgressBarWindow.setBounds(500, 500, 460, 90);
		loadingProgressBarWindow.open();
	}

	@Override
	public void setProgressBarPercentage(int iPercentage)
	{
		if (loadingProgressBar.isDisposed())
			return;

		loadingProgressBar.setSelection(iPercentage);
	}

	@Override
	public void setProgressBarPercentageFromExternalThread(final int iPercentage)
	{
		if (loadingProgressBar.isDisposed())
			return;

		loadingProgressBar.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				if (loadingProgressBar.isDisposed())
					return;
				loadingProgressBar.setSelection(iPercentage);
			}
		});
	}

	@Override
	public void setProgressBarText(String sText)
	{
		if (generalManager.isStandalone())
		{
			loadingProgressBarWindow.setText(sText);
			loadingProgressBarWindow.update();
		}
		else
		{
			// // If in RCP mode and the splash is already gone
			// // a new progress bar has to be created
			// if (loadingProgressBarWindow == null)
			// createLoadingProgressBar();

			if (loadingProgressBarLabel.isDisposed())
				return;

			loadingProgressBarLabel.setText(sText);
			loadingProgressBarLabel.update();
		}
	}

	@Override
	public void setProgressBarTextFromExternalThread(final String sText)
	{
		if (loadingProgressBar.isDisposed())
			return;

		loadingProgressBar.getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				setProgressBarText(sText);
			}
		});
	}

	public void setProgressBarVisible(final boolean state)
	{
		loadingProgressBarWindow.setVisible(state);
		loadingProgressBar.setVisible(state);
	}

	@Override
	public void setExternalProgressBarAndLabel(ProgressBar progressBar, Label progressLabel)
	{
		this.loadingProgressBar = progressBar;
		this.loadingProgressBarLabel = progressLabel;
	}

	@Override
	public void setExternalRCPStatusLine(IStatusLineManager statusLine, Display display)
	{
		this.display = display;
		this.externalRCPStatusLine = statusLine;
	}

	@Override
	public void setExternalRCPStatusLineMessage(final String sMessage)
	{
		if (externalRCPStatusLine == null)
			return;

		display.asyncExec(new Runnable()
		{
			public void run()
			{
				externalRCPStatusLine.setMessage(sMessage);
			}
		});
	}
}