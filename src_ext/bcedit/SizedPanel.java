// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SizedPanel.java

package bcedit;

import java.awt.Dimension;
import javax.swing.JPanel;

public class SizedPanel extends JPanel
{

    SizedPanel(int width, int height)
    {
        this.height = 0;
        this.width = 0;
        panelName = null;
        this.height = height;
        this.width = width;
        setOpaque(false);
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(width, height);
    }

    int height;
    int width;
    public String panelName;
}
