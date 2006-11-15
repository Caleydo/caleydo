// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import java.awt.*;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            BCDefaults

class JPanelMy extends JPanel
{

    public JPanelMy(boolean nFrame)
    {
        img = null;
        noFrame = false;
        img = null;
        noFrame = nFrame;
    }

    public JPanelMy(Image nimg)
    {
        img = null;
        noFrame = false;
        img = nimg;
    }

    public void paintComponent(Graphics g)
    {
        Dimension d = getSize();
        Insets i = new Insets(0, 0, 0, 0);
        int nw = d.width - i.left - i.right;
        int nh = d.height - i.top - i.bottom;
        if(isOpaque())
        {
            g.setColor(BCDefaults.bgPanelColor);
            g.fillRect(i.left, i.top, nw, nh);
        }
        if(img != null)
            g.drawImage(img, i.left, i.top, nw, nh, this);
        else
        if(!noFrame)
        {
            g.setColor(new Color(0));
            g.drawRoundRect(i.left + 1, i.top + 1, nw - 2, nh - 2, 8, 8);
            g.setColor(new Color(0x435e89));
            g.drawRoundRect(i.left, i.top, nw - 2, nh - 2, 8, 8);
        }
    }

    public Insets getInsets()
    {
        return new Insets(8, 8, 8, 8);
    }

    Image img;
    boolean noFrame;
}
