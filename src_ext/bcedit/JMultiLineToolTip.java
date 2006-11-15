// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JMultiLineToolTip.java

package bcedit;

import javax.swing.JComponent;
import javax.swing.JToolTip;

// Referenced classes of package bcedit:
//            MultiLineToolTipUI

public class JMultiLineToolTip extends JToolTip
{

    public JMultiLineToolTip()
    {
        columns = 0;
        fixedwidth = 0;
        updateUI();
    }

    public void updateUI()
    {
        setUI(MultiLineToolTipUI.createUI(this));
    }

    public void setColumns(int columns)
    {
        this.columns = columns;
        fixedwidth = 0;
    }

    public int getColumns()
    {
        return columns;
    }

    public void setFixedWidth(int width)
    {
        fixedwidth = width;
        columns = 0;
    }

    public int getFixedWidth()
    {
        return fixedwidth;
    }

    private static final String uiClassID = "ToolTipUI";
    String tipText;
    JComponent component;
    protected int columns;
    protected int fixedwidth;
}
