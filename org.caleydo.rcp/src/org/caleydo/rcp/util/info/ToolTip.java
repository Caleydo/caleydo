package org.caleydo.rcp.util.info;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.caleydo.core.data.mapping.EIDType;
import org.caleydo.core.data.mapping.EMappingType;
import org.caleydo.core.data.selection.ESelectionType;
import org.caleydo.core.data.selection.SelectionItem;
import org.caleydo.core.manager.IIDMappingManager;
import org.caleydo.core.manager.general.GeneralManager;
import org.caleydo.core.manager.specialized.glyph.GlyphManager;
import org.caleydo.core.view.opengl.canvas.glyph.gridview.GlyphEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * Tool tip like box for multi-line info output.
 * 
 * @author Friederich Kupzog
 * @author Marc Streit
 */
public class ToolTip
	implements Listener
{
	static private Shell tip = null;
	static private Composite comp = null;
	static private StyledText label = null;
	static private Label icon = null;
	static private Control lastControl = null;

	private static final int MAX_LINES = 80;
	private static final int MAX_CHARS_PER_LINE = 50;

	private Control parentControl;
	private InfoArea infoArea;
	private EInfoType infoType;

	private String sText;
	private String[] sArLines;
	int iLineIndex;

	/**
	 * Constructor
	 */
	public ToolTip(Control parentControl, String sText)
	{
		this.parentControl = parentControl;
		this.sText = sText;
		parentControl.addListener(SWT.Dispose, this);
		parentControl.addListener(SWT.KeyDown, this);
		parentControl.addListener(SWT.MouseMove, this);
		parentControl.addListener(SWT.MouseHover, this);
		iLineIndex = 0;
	}

	/**
	 * Constructor
	 */
	public ToolTip(Control control, String sText, InfoArea infoArea, EInfoType infoType)
	{
		this(control, sText);

		this.infoArea = infoArea;
		this.infoType = infoType;
	}

	@Override
	public void handleEvent(Event event)
	{
		switch (event.type)
		{
			case SWT.Dispose:
			case SWT.KeyDown:
			case SWT.MouseMove:
			{
				// Tip abbauen
				if (tip == null)
					break;
				tip.dispose();
				tip = null;
				label = null;
				icon = null;
				comp = null;
				lastControl = null;
				sArLines = null;
				break;
			}
			case SWT.MouseHover:
			{
				// Fetch current text
				if (infoArea != null)
				{
					if (infoType == EInfoType.VIEW_INFO)
					{
						sText = infoArea.getUpdateTriggeringView().getDetailedInfo();
					}
					else if (infoType == EInfoType.DETAILED_INFO)
					{

						String sDetailText = "";

						Iterator<SelectionItem> iterSelectionItems = infoArea
								.getSelectionDelta().getSelectionData().iterator();
						EIDType eIDType = infoArea.getSelectionDelta().getIDType();
						SelectionItem item;
						GlyphManager gman = (GlyphManager) GeneralManager.get()
								.getGlyphManager();
						IIDMappingManager genomeIDManager = GeneralManager.get()
								.getIDMappingManager();

						while (iterSelectionItems.hasNext())
						{
							item = iterSelectionItems.next();

							if (item.getSelectionType() == ESelectionType.MOUSE_OVER
									|| item.getSelectionType() == ESelectionType.SELECTION)
							{

								if (eIDType == EIDType.DAVID)
								{
									Set<String> sSetRefSeqID = GeneralManager.get()
											.getIDMappingManager().getMultiID(
													EMappingType.DAVID_2_REFSEQ_MRNA,
													item.getSelectionID());

									sDetailText = sDetailText + "<b>Gene symbol: </b>";
									sDetailText = sDetailText
											+ genomeIDManager.getID(
													EMappingType.DAVID_2_GENE_SYMBOL, item
															.getSelectionID());
									sDetailText = sDetailText + "\n";

									sDetailText = sDetailText + "<b>Gene name: </b>";
									sDetailText = sDetailText
											+ genomeIDManager.getID(
													EMappingType.DAVID_2_GENE_NAME, item
															.getSelectionID());
									sDetailText = sDetailText + "\n";

									sDetailText = sDetailText + "<b>RefSeq: </b>";

									for (String sRefSeqID : sSetRefSeqID)
									{
										sDetailText = sDetailText + sRefSeqID;
										sDetailText = sDetailText + ", ";
									}

									// Remove last comma
									sDetailText = sDetailText.substring(0, sDetailText
											.length() - 2);
									sDetailText = sDetailText + "\n\n";
								}
								else if (eIDType == EIDType.EXPERIMENT_INDEX)
								{
									GlyphEntry glyph = gman.getGlyphs().get(
											item.getSelectionID());

									if (glyph != null)
										sDetailText = glyph.getGlyphDescription("\n");
									else
										sDetailText = "glyph not found";
								}
								else
								{
									continue;
								}

							}
						}

						sText = sDetailText;
					}
				}

				if (lastControl != parentControl)
				{
					prepareText();
					createContents();
					showContents(event);

				}
			}
		}
	}

	private void createContents()
	{
		final Listener myListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				Shell shell = ((Composite) event.widget).getShell();
				switch (event.type)
				{
					// case SWT.MouseDown:
					case SWT.MouseExit:
						shell.dispose();
						break;
					case SWT.MouseDoubleClick:
						shell.dispose();
						break;
					case SWT.KeyDown:
						// if (event.keyCode == 16777218) // Cursor Down
						// {
						if (iLineIndex <= sArLines.length - MAX_LINES)
						{
							iLineIndex++;
							label.setText(getNextLine(iLineIndex));
						}
						// }
						// else if (event.keyCode == 16777217) // Cursor Up
						// {
						// if (currentLine > 0)
						// {
						// currentLine--;
						// label.setText(getMyLinesFrom(currentLine));
						// }
						// }
						break;
				}
			}
		};

		lastControl = parentControl;
		if (tip != null && !tip.isDisposed())
			tip.dispose();
		tip = new Shell(parentControl.getShell(), SWT.ON_TOP);
		tip.setLayout(new FillLayout());

		comp = new Composite(tip, SWT.NONE);
		comp.addListener(SWT.MouseExit, myListener);
		comp.addListener(SWT.MouseDoubleClick, myListener);
		comp.addListener(SWT.KeyDown, myListener);

		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		comp.setLayout(rowLayout);
		comp.setBackground(parentControl.getDisplay()
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

		// Label
		label = new StyledText(comp, SWT.NONE);
		label.setForeground(parentControl.getDisplay().getSystemColor(
				SWT.COLOR_INFO_FOREGROUND));
		label.setBackground(parentControl.getDisplay().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND));
		iLineIndex = 0;

		String sLineText = getNextLine(iLineIndex);

		// Add line breaks if lines are too long
		StringTokenizer tokenizer = new StringTokenizer(sLineText, "\n", true);
		String sTmp;
		sLineText = "";
		while (tokenizer.hasMoreTokens())
		{
			sTmp = tokenizer.nextToken();

			if (sTmp.length() > MAX_CHARS_PER_LINE && sTmp.contains(" "))
			{
				int bla = MAX_CHARS_PER_LINE + sTmp.substring(MAX_CHARS_PER_LINE).indexOf(" ");
				bla++;

				if (sTmp.substring(MAX_CHARS_PER_LINE).indexOf(" ") != -1)
				{
					sLineText = sLineText
							+ new StringBuffer(sTmp).insert(
									MAX_CHARS_PER_LINE
											+ sTmp.substring(MAX_CHARS_PER_LINE).indexOf(" ")
											+ 1, "\n").toString();
				}
				else
				{
					sLineText = sLineText + sTmp;
				}
			}
			else
			{
				sLineText = sLineText + sTmp;
			}

			// sLineText = sLineText + "\n";
		}

		ArrayList<Integer> iAlStartIndex = new ArrayList<Integer>();
		ArrayList<Integer> iAlLength = new ArrayList<Integer>();
		int iStartIndex = 0;

		// Handle bold text parts
		while (sLineText.contains("<b>"))
		{
			iStartIndex = sLineText.indexOf("<b>");
			iAlStartIndex.add(iStartIndex);
			iAlLength.add(sLineText.indexOf("</b>") - iStartIndex - 3);
			sLineText = sLineText.replaceFirst("<b>", "");
			sLineText = sLineText.replaceFirst("</b>", "");
		}

		label.setText(sLineText);

		StyleRange styleRange;
		for (int iOccurenceIndex = 0; iOccurenceIndex < iAlStartIndex.size(); iOccurenceIndex++)
		{
			styleRange = new StyleRange();
			styleRange.fontStyle = SWT.BOLD;
			styleRange.start = iAlStartIndex.get(iOccurenceIndex);
			styleRange.length = iAlLength.get(iOccurenceIndex);
			label.setStyleRange(styleRange);
		}

		// Icon
		icon = new Label(comp, SWT.NONE);
		icon.setBackground(parentControl.getDisplay()
				.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		icon.setForeground(parentControl.getDisplay().getSystemColor(SWT.COLOR_BLUE));
	}

	private void showContents(Event event)
	{
		Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		size.x += 20;
		Point pt = parentControl.toDisplay(event.x, event.y);
		Rectangle pos = new Rectangle(pt.x + 10, pt.y - size.y, size.x, size.y);
		if (pos.x + pos.width > parentControl.getDisplay().getBounds().width)
			pos.x = parentControl.getDisplay().getBounds().width - pos.width;
		if (pos.y + pos.height > parentControl.getDisplay().getBounds().height)
			pos.y = parentControl.getDisplay().getBounds().height - pos.height;
		if (pos.x <= 0)
			pos.x = 1;
		if (pos.y <= 0)
			pos.y = 1;
		tip.setBounds(pos);
		Point lsize = label.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		label.setSize(lsize.x + 20, lsize.y);

		if (tip != null)
		{
			tip.setVisible(true);
			if (sArLines != null)
				tip.forceFocus();
		}
	}

	private void prepareText()
	{
		StringTokenizer textTokenizer = new StringTokenizer(sText + "\n", "\n", true);
		StringTokenizer lineTokenizer = new StringTokenizer(sText + "\n", "\n", false);
		int iLineCount = textTokenizer.countTokens() - lineTokenizer.countTokens();

		if (iLineCount > MAX_LINES)
		{
			sArLines = new String[iLineCount];
			for (int i = 0; i < sArLines.length; i++)
			{
				String tmp = textTokenizer.nextToken();
				if (!tmp.equals("\n"))
				{
					sArLines[i] = tmp + textTokenizer.nextToken();
				}
				else
				{
					sArLines[i] = tmp;
				}

			}
		}
	}

	private String getNextLine(int iLineIndex)
	{
		if (sArLines == null)
			return sText;
		else
		{
			StringBuffer erg = new StringBuffer();
			int to = iLineIndex + 10;
			if (to > sArLines.length)
				to = sArLines.length;
			for (int i = iLineIndex; i < to; i++)
			{
				erg.append(sArLines[i]);
				// erg.append("\n");
			}
			return erg.toString();
		}
	}

	public void updateText(String sText)
	{
		this.sText = sText;
		label.update();
	}

	public static void main(String[] args)
	{
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(200, 300);
		Button button = new Button(shell, SWT.PUSH);
		button.setText("Hallo");
		new ToolTip(
				button,
				"Dies ist ein <b>Test</b> text!\nDies ist<b>ein</b>Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nDies ist ein Test text!\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\nHALLO HALLO\n");
		button.setBounds(40, 50, 50, 20);

		shell.open();
		while (!shell.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
