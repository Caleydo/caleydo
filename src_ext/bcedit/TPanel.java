// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TPanel.java

package bcedit;

import java.awt.*;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            BCDefaults

public class TPanel extends JPanel
{

    public TPanel(String text)
    {
        color = Color.black;
        fg = Color.black;
        bg = new Color(0x5a7eb8);
        normalBg = new Color(0x5a7eb8);
        raisedBg = BCDefaults.bgPanelColor;
        bRaised = false;
        bFocused = false;
        this.text = text;
        setFocusable(true);
    }

    public void setRaised(boolean b)
    {
        if(b && !bRaised)
        {
            bRaised = b;
            bg = raisedBg;
            fg = BCDefaults.fgColor;
            repaint();
        } else
        if(!b && bRaised)
        {
            bRaised = b;
            bg = normalBg;
            fg = color;
            repaint();
        }
    }

    public void setFocused(boolean b)
    {
        bFocused = b;
        repaint();
    }

    public Dimension getMaximumSize()
    {
        return getPreferredSize();
    }

    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    public Dimension getPreferredSize()
    {
        Dimension d = new Dimension(0, 0);
        FontMetrics f = getFontMetrics(getFont());
        d.width = f.stringWidth(text) + 2 * f.charWidth(' ');
        d.height = f.getHeight() + 4;
        return d;
    }

    public void paintComponent(Graphics g)
    {
        Dimension d = getSize();
        Insets i = getInsets();
        int nw = d.width - i.left - i.right;
        int nh = d.height - i.top - i.bottom;
        if(isOpaque())
        {
            g.setColor(bg);
            g.fillRect(i.left, i.top, nw, nh);
        }
        if(bRaised)
            g.setColor(new Color(0xffffff));
        else
            g.setColor(new Color(0));
        g.drawLine(i.left, i.top, nw - 1, i.top);
        g.drawLine(i.left, i.top, i.left, nh - 1);
        if(bRaised)
            g.setColor(new Color(0));
        else
            g.setColor(new Color(0xffffff));
        g.drawLine((i.left + nw) - 1, i.top, (i.left + nw) - 1, (i.top + nh) - 1);
        g.drawLine(i.left, (i.top + nh) - 1, (i.left + nw) - 1, (i.top + nh) - 1);
        Dimension dText = getPreferredSize();
        FontMetrics f = getFontMetrics(getFont());
        int xpos = i.left + f.charWidth(' ');
        int ypos = (dText.height + (d.height - dText.height) / 2) - f.getDescent();
        xpos = (d.width - f.stringWidth(text)) / 2;
        ypos -= 2 + (bRaised ? 1 : 0);
        g.setColor(fg);
        g.drawString(text, xpos, ypos);
        if(bFocused)
        {
            g.setColor(new Color(0));
            g.drawRect(i.left + 2, i.top + 2, nw - 5, nh - 5);
        }
        if(!isEnabled())
        {
            g.setColor(new Color(0x80808080, true));
            g.fillRect(i.left + 1, i.top + 1, nw - 2, nh - 2);
        }
    }

    public String text;
    public Color color;
    public Color fg;
    public Color bg;
    public Color normalBg;
    public Color raisedBg;
    boolean bRaised;
    boolean bFocused;
}
