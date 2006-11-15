// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JMultiLineToolTip.java

package bcedit;

import java.awt.*;
import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolTipUI;

// Referenced classes of package bcedit:
//            JMultiLineToolTip

class MultiLineToolTipUI extends BasicToolTipUI
{

    public static ComponentUI createUI(JComponent c)
    {
        return sharedInstance;
    }

    public MultiLineToolTipUI()
    {
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);
        tip = (JToolTip)c;
        rendererPane = new CellRendererPane();
        c.add(rendererPane);
    }

    public void uninstallUI(JComponent c)
    {
        super.uninstallUI(c);
        c.remove(rendererPane);
        rendererPane = null;
    }

    public void paint(Graphics g, JComponent c)
    {
        Dimension size = c.getSize();
        textArea.setBackground(c.getBackground());
        rendererPane.paintComponent(g, textArea, c, 1, 1, size.width - 1, size.height - 1, true);
    }

    public Dimension getPreferredSize(JComponent c)
    {
        String tipText = ((JToolTip)c).getTipText();
        if(tipText == null)
            return new Dimension(0, 0);
        textArea = new JTextArea(tipText);
        rendererPane.removeAll();
        rendererPane.add(textArea);
        textArea.setWrapStyleWord(true);
        int width = ((JMultiLineToolTip)c).getFixedWidth();
        int columns = ((JMultiLineToolTip)c).getColumns();
        if(columns > 0)
        {
            textArea.setColumns(columns);
            textArea.setSize(0, 0);
            textArea.setLineWrap(true);
            textArea.setSize(textArea.getPreferredSize());
        } else
        if(width > 0)
        {
            textArea.setLineWrap(true);
            Dimension d = textArea.getPreferredSize();
            d.width = width;
            d.height++;
            textArea.setSize(d);
        } else
        {
            textArea.setLineWrap(false);
        }
        Dimension dim = textArea.getPreferredSize();
        dim.height++;
        dim.width += 4;
        return dim;
    }

    public Dimension getMinimumSize(JComponent c)
    {
        return getPreferredSize(c);
    }

    public Dimension getMaximumSize(JComponent c)
    {
        return getPreferredSize(c);
    }

    static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
    Font smallFont;
    static JToolTip tip;
    protected CellRendererPane rendererPane;
    private static JTextArea textArea;

}
