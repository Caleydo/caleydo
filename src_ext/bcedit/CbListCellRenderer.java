// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CbListCellRenderer.java

package bcedit;

import java.awt.Color;
import java.awt.Component;
import javax.swing.*;

public class CbListCellRenderer extends JLabel
    implements ListCellRenderer
{

    public CbListCellRenderer()
    {
        bgnorm = new Color(0x4a7597);
        bghigh = new Color(0xffd800);
        fgnorm = new Color(0xffd800);
        fghigh = new Color(0x4a7597);
        bgfocus = new Color(0xc0c0c0);
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        String s = "";
        if(value != null)
        {
            s = value.toString();
            int sidx = s.indexOf('\u0100');
            if(sidx >= 0)
                s = s.substring(0, sidx);
            setText(s);
        }
        if(s.equals("-"))
        {
            isSelected = false;
            cellHasFocus = false;
        }
        setBackground(isSelected ? cellHasFocus ? bgfocus : bghigh : bgnorm);
        setForeground(isSelected ? fghigh : fgnorm);
        if(isSelected)
            setBorder(BorderFactory.createRaisedBevelBorder());
        else
            setBorder(BorderFactory.createEmptyBorder());
        return this;
    }

    Color bgnorm;
    Color bghigh;
    Color fgnorm;
    Color fghigh;
    Color bgfocus;
}
