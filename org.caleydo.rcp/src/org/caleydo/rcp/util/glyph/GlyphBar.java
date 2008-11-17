package org.caleydo.rcp.util.glyph;

import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.view.opengl.canvas.AGLEventListener;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.EIconIDs;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GLGlyph;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.PlatformUI;

/**
 * Glyph Tool Bar
 * 
 * @author Sauer Stefan
 */
public class GlyphBar
	extends ControlContribution
{

	// Images to use on our tool items
	public static final String TEXT_POSMODEL_RECTANGLE = "Switch View To rectangle Positioning";
	public static final String TEXT_POSMODEL_PLUS = "Switch View To ? Positioning";
	public static final String TEXT_POSMODEL_CIRCLE = "Switch View To spiral Positioning";
	public static final String TEXT_POSMODEL_RANDOM = "Switch View To random Positioning";
	public static final String TEXT_POSMODEL_SCATTERPLOT = "Switch View To Scatterplot";

	public static final String ICON_POSMODEL_RECTANGLE = "resources/icons/view/glyph/sort_zickzack.png";
	public static final String ICON_POSMODEL_PLUS = "resources/icons/view/glyph/sort_spirale.png";
	public static final String ICON_POSMODEL_CIRCLE = "resources/icons/view/glyph/sort_spirale.png";
	public static final String ICON_POSMODEL_RANDOM = "resources/icons/view/glyph/sort_random.png";
	public static final String ICON_POSMODEL_SCATTERPLOT = "resources/icons/view/glyph/sort_scatterplot.png";

	public static final String TEXT_SELECTIONBRUSH_DISABLE = "disable selection brush";
	public static final String TEXT_SELECTIONBRUSH_SELECT_ALL = "select all";
	public static final String TEXT_SELECTIONBRUSH_DESELECT_ALL = "deselect all";

	public static final String ICON_SELECTIONBRUSH_DISABLE = "resources/icons/view/glyph/selection_brush_disable.png";
	public static final String ICON_SELECTIONBRUSH_SELECT_ALL = "resources/icons/view/glyph/selection_brush_all.png";
	public static final String ICON_SELECTIONBRUSH_DESELECT_ALL = "resources/icons/view/glyph/selection_brush_none.png";

	public static final String TEXT_SELECTIONBRUSH_01 = "1";
	public static final String TEXT_SELECTIONBRUSH_02 = "2";
	public static final String TEXT_SELECTIONBRUSH_03 = "3";
	public static final String TEXT_SELECTIONBRUSH_04 = "4";

	public static final String ICON_SELECTIONBRUSH_01 = "resources/icons/view/glyph/selection_brush_01.png";
	public static final String ICON_SELECTIONBRUSH_02 = "resources/icons/view/glyph/selection_brush_02.png";
	public static final String ICON_SELECTIONBRUSH_03 = "resources/icons/view/glyph/selection_brush_03.png";
	public static final String ICON_SELECTIONBRUSH_04 = "resources/icons/view/glyph/selection_brush_04.png";

	public static final String TEXT_WINDOW_NEW = " new window ";

	private static Image imgPositionModelRectangle;
	private static Image imgPositionModelPlus;
	private static Image imgPositionModelCircle;
	private static Image imgPositionModelRandom;
	private static Image imgPositionModelScatterplot;

	private static Image imgSelectionBrush01;
	private static Image imgSelectionBrush02;
	private static Image imgSelectionBrush03;
	private static Image imgSelectionBrush04;
	public static Image imgSelectionBrushAll;
	public static Image imgSelectionBrushNone;
	public static Image imgSelectionBrushDisable;

	private int iViewID = -1;
	private GLGlyph glyphview;

	/**
	 * Constructor.
	 */
	public GlyphBar(String id)
	{
		super(id);

		ResourceLoader loader = new ResourceLoader();
		Display display = PlatformUI.getWorkbench().getDisplay();

		imgPositionModelRectangle = loader.getImage(display, ICON_POSMODEL_RECTANGLE);
		imgPositionModelCircle = loader.getImage(display, ICON_POSMODEL_CIRCLE);
		imgPositionModelRandom = loader.getImage(display, ICON_POSMODEL_RANDOM);
		imgPositionModelScatterplot = loader.getImage(display, ICON_POSMODEL_SCATTERPLOT);

		imgSelectionBrush01 = loader.getImage(display, ICON_SELECTIONBRUSH_01);
		imgSelectionBrush02 = loader.getImage(display, ICON_SELECTIONBRUSH_02);
		imgSelectionBrush03 = loader.getImage(display, ICON_SELECTIONBRUSH_03);
		imgSelectionBrush04 = loader.getImage(display, ICON_SELECTIONBRUSH_04);
		imgSelectionBrushAll = loader.getImage(display, ICON_SELECTIONBRUSH_SELECT_ALL);
		imgSelectionBrushNone = loader.getImage(display, ICON_SELECTIONBRUSH_DESELECT_ALL);
		imgSelectionBrushDisable = loader.getImage(display, ICON_SELECTIONBRUSH_DISABLE);

	}

	public void setViewID(int id)
	{
		this.iViewID = id;
		glyphview = null;
		for (AGLEventListener l : GeneralManager.get().getViewGLCanvasManager()
				.getAllGLEventListeners())
		{
			if (l.getID() == iViewID && l instanceof GLGlyph)
				glyphview = (GLGlyph) l;
		}
	}

	protected Control createControl(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		ToolBar toolBar = new ToolBar(composite, SWT.RIGHT);
		// ToolBar toolBar = (ToolBar) parent;

		{
			ToolItem item = createToolItem(toolBar, SWT.PUSH, TEXT_WINDOW_NEW, null, null,
					TEXT_WINDOW_NEW);
			GlyphBarSelectionListener listener = new GlyphBarSelectionListener(item, iViewID);
			item.addSelectionListener(listener);
		}

		new ToolItem(toolBar, SWT.SEPARATOR);

		// Create selection brush dropdown
		{
			ToolItem item = createToolItem(toolBar, SWT.DROP_DOWN, "",
					imgSelectionBrushDisable, imgSelectionBrushDisable,
					TEXT_SELECTIONBRUSH_DISABLE);
			GlyphBarSelectionListener listener = new GlyphBarSelectionListener(item, iViewID);
			listener.add(imgSelectionBrushDisable, TEXT_SELECTIONBRUSH_DISABLE);
			listener.add(imgSelectionBrushAll, TEXT_SELECTIONBRUSH_SELECT_ALL);
			listener.add(imgSelectionBrushNone, TEXT_SELECTIONBRUSH_DESELECT_ALL);

			listener.add(imgSelectionBrush01, TEXT_SELECTIONBRUSH_01);
			listener.add(imgSelectionBrush02, TEXT_SELECTIONBRUSH_02);
			listener.add(imgSelectionBrush03, TEXT_SELECTIONBRUSH_03);
			listener.add(imgSelectionBrush04, TEXT_SELECTIONBRUSH_04);

			item.addSelectionListener(listener);
		}

		new ToolItem(toolBar, SWT.SEPARATOR);

		// Create position model dropdown
		{
			EIconIDs type = EIconIDs.DISPLAY_RECTANGLE;

			String usedText = TEXT_POSMODEL_RECTANGLE;
			Image usedImage = imgPositionModelRectangle;

			if (glyphview != null)
				type = glyphview.getPositionModel();

			if (type == EIconIDs.DISPLAY_CIRCLE)
			{
				usedText = TEXT_POSMODEL_CIRCLE;
				usedImage = imgPositionModelCircle;
			}
			if (type == EIconIDs.DISPLAY_PLUS)
			{
				usedText = TEXT_POSMODEL_PLUS;
				usedImage = imgPositionModelPlus;
			}
			if (type == EIconIDs.DISPLAY_RANDOM)
			{
				usedText = TEXT_POSMODEL_RANDOM;
				usedImage = imgPositionModelRandom;

			}
			if (type == EIconIDs.DISPLAY_SCATTERPLOT)
			{
				usedText = TEXT_POSMODEL_SCATTERPLOT;
				usedImage = imgPositionModelScatterplot;
			}

			ToolItem item = createToolItem(toolBar, SWT.DROP_DOWN, "", usedImage, usedImage,
					usedText);
			GlyphBarSelectionListener listenerTwo = new GlyphBarSelectionListener(item,
					iViewID);
			listenerTwo.add(imgPositionModelRectangle, TEXT_POSMODEL_RECTANGLE);
			listenerTwo.add(imgPositionModelCircle, TEXT_POSMODEL_CIRCLE);
			listenerTwo.add(imgPositionModelRandom, TEXT_POSMODEL_RANDOM);
			listenerTwo.add(imgPositionModelScatterplot, TEXT_POSMODEL_SCATTERPLOT);
			item.addSelectionListener(listenerTwo);
		}

		// composite.pack();
		return composite;
	}

	private ToolItem createToolItem(ToolBar parent, int type, String text, Image image,
			Image hotImage, String toolTipText)
	{
		ToolItem item = new ToolItem(parent, type);
		item.setText(text);
		item.setImage(image);
		item.setHotImage(hotImage);
		item.setToolTipText(toolTipText);
		return item;
	}

}
