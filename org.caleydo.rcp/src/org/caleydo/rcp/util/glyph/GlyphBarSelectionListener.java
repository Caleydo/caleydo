package org.caleydo.rcp.util.glyph;

import org.caleydo.core.command.ECommandType;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.remote.GLRemoteRendering;
import org.caleydo.rcp.EStartViewType;
import org.caleydo.rcp.action.view.AToolBarAction;
import org.caleydo.rcp.action.view.glyph.ChangeSelectionBrush;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToCircleAction;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToRandomAction;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToRectangleAction;
import org.caleydo.rcp.action.view.glyph.ChangeViewModeToScatterplotAction;
import org.caleydo.rcp.command.handler.view.OpenGlyphSliderViewHandler;
import org.caleydo.rcp.views.GLGlyphView;
import org.caleydo.rcp.views.GLRemoteRenderingView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

public class GlyphBarSelectionListener
	extends SelectionAdapter
{
	private ToolItem dropdown;
	private MenuItem selectedItem;
	private String actualItemText;
	private Menu menu;
	private int iViewID;

	/**
	 * Constructs a DropdownSelectionListener
	 * 
	 * @param dropdown
	 *            the dropdown this listener belongs to
	 */
	public GlyphBarSelectionListener(ToolItem dropdown, int iViewID)
	{
		this.iViewID = iViewID;
		this.dropdown = dropdown;
		this.actualItemText = dropdown.getText();
		menu = new Menu(dropdown.getParent().getShell());
	}

	/**
	 * Adds an item to the dropdown list
	 * 
	 * @param icon
	 *            the new item icon image
	 * @param text
	 *            the new item text
	 */
	public void add(Image icon, String text)
	{
		MenuItem menuItem = new MenuItem(menu, SWT.NONE);
		menuItem.setText(text);
		menuItem.setImage(icon);
		menuItem.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent event)
			{
				selectedItem = (MenuItem) event.widget;

				actualItemText = selectedItem.getText();
				// display text, if no icon was set
				if (selectedItem.getImage() == null)
					dropdown.setText(selectedItem.getText());

				dropdown.setImage(selectedItem.getImage());
				dropdown.setHotImage(selectedItem.getImage());

				// dropdown.getParent().getParent().pack();
				handleButtonFired();
			}
		});
	}

	/**
	 * Called when either the button itself or the dropdown arrow is clicked
	 * 
	 * @param event
	 *            the event that trigged this call
	 */
	public void widgetSelected(SelectionEvent event)
	{
		// If they clicked the arrow, we show the list
		if (event.detail == SWT.ARROW)
		{
			// Determine where to put the dropdown list
			ToolItem item = (ToolItem) event.widget;
			Rectangle rect = item.getBounds();
			Point pt = item.getParent().toDisplay(new Point(rect.x, rect.y));
			menu.setLocation(pt.x, pt.y + rect.height);
			menu.setVisible(true);
		}
		else
		{
			// They pushed the button; take appropriate action
			handleButtonFired();
		}

	}

	public void handleButtonFired()
	{
		AToolBarAction action = null;

		if (actualItemText.equals(GlyphBar.TEXT_POSMODEL_RECTANGLE))
			action = new ChangeViewModeToRectangleAction(iViewID);

		if (actualItemText.equals(GlyphBar.TEXT_POSMODEL_CIRCLE))
			action = new ChangeViewModeToCircleAction(iViewID);

		if (actualItemText.equals(GlyphBar.TEXT_POSMODEL_RANDOM))
			action = new ChangeViewModeToRandomAction(iViewID);

		if (actualItemText.equals(GlyphBar.TEXT_POSMODEL_SCATTERPLOT))
			action = new ChangeViewModeToScatterplotAction(iViewID);

		if (actualItemText.equals(GlyphBar.TEXT_SELECTIONBRUSH_DISABLE))
			action = new ChangeSelectionBrush(iViewID, 0);

		if (actualItemText.equals(GlyphBar.TEXT_SELECTIONBRUSH_SELECT_ALL))
		{
			action = new ChangeSelectionBrush(iViewID, -1);
			dropdown.setImage(GlyphBar.imgSelectionBrushDisable);
			// dropdown.setText(GlyphBar.TEXT_SELECTIONBRUSH_DISABLE);
		}

		if (actualItemText.equals(GlyphBar.TEXT_SELECTIONBRUSH_DESELECT_ALL))
		{
			action = new ChangeSelectionBrush(iViewID, -2);
			dropdown.setImage(GlyphBar.imgSelectionBrushDisable);
			// dropdown.setText(GlyphBar.TEXT_SELECTIONBRUSH_DISABLE);
		}

		if (actualItemText.equals(GlyphBar.TEXT_SELECTIONBRUSH_01))
			action = new ChangeSelectionBrush(iViewID, 1);

		if (actualItemText.equals(GlyphBar.TEXT_SELECTIONBRUSH_02))
			action = new ChangeSelectionBrush(iViewID, 2);

		if (actualItemText.equals(GlyphBar.TEXT_SELECTIONBRUSH_03))
			action = new ChangeSelectionBrush(iViewID, 3);

		if (actualItemText.equals(GlyphBar.TEXT_SELECTIONBRUSH_04))
			action = new ChangeSelectionBrush(iViewID, 4);

		try
		{
			String rcpid = EStartViewType.GLYPHVIEW.getRCPViewID();

			if (actualItemText.equals(GlyphBar.TEXT_WINDOW_NEW))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
						rcpid, rcpid + Integer.toString(GLGlyphView.viewCount),
						IWorkbenchPage.VIEW_CREATE);

				IViewPart viewpart = (IViewPart) PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage().findView(rcpid);

				// viewpart.s

				// String t = viewpart.getTitle();
				// System.out.println(t);
			}

		}
		catch (PartInitException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (action != null)
			action.run();

	}

}
