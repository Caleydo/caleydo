// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JLabel2.java

package bcedit;

import java.awt.*;
import javax.swing.JLabel;

public class JLabel2 extends JLabel
{

    public JLabel2()
    {
        use3D = true;
    }

    public JLabel2(String s)
    {
        super(s);
        use3D = true;
    }

    public Dimension getPreferredSize()
    {
        Dimension d = super.getPreferredSize();
        d.width += 2;
        d.height += 2;
        return d;
    }

    public void paintComponent(Graphics g)
    {
        update(g);
    }

    public void update(Graphics g)
    {
        if(!isVisible())
            return;
        String text = getText();
        if(text == null)
            return;
        FontMetrics f = getFontMetrics(getFont());
        int y = (f.getHeight() + 1) - f.getDescent();
        Color cl[] = new Color[3];
        cl[2] = getForeground();
        cl[1] = Color.black;
        cl[0] = Color.white;
        if(use3D)
        {
            g.setColor(cl[0]);
            g.drawString(text, 0, y);
            g.setColor(cl[1]);
            g.drawString(text, 2, y + 2);
        }
        g.setColor(cl[2]);
        g.drawString(text, 1, y + 1);
    }

    public boolean use3D;
}
