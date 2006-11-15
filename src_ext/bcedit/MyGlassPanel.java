// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MainFrame.java

package bcedit;

import java.awt.*;
import javax.swing.JPanel;

class MyGlassPanel extends JPanel
{

    public MyGlassPanel()
    {
        setOpaque(false);
    }

    public void paintComponent(Graphics g)
    {
        if(isVisible())
        {
            Color wdc = new Color(0x80808080, false);
            Dimension d = getSize();
            Insets i = getInsets();
            int nw = d.width - i.left - i.right;
            int nh = d.height - i.top - i.bottom;
            if(nw >= 400 && nh >= 300)
            {
                i.left = (nw - 400) / 2;
                i.top = (nh - 300) / 2;
                nw = 400;
                nh = 300;
            }
            g.setColor(wdc);
            g.fillRect(i.left, i.top, nw, nh);
            g.draw3DRect(i.left, i.top, nw, nh, true);
        }
    }

    static final int msgX = 400;
    static final int msgY = 300;
}
