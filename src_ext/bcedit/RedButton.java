// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RedButton.java

package bcedit;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            BCImages

public class RedButton extends JPanel
    implements MouseListener
{

    public RedButton()
    {
        state = 0;
        bAllowResize = true;
        img[0] = BCImages.getImage("Button_Led_red_dark.png");
        img[1] = BCImages.getImage("Button_Led_red_light.png");
        img[2] = BCImages.getImage("Button_Led_red_dark_highlight.png");
        img[3] = BCImages.getImage("Button_Led_red_light_highlight.png");
        setOpaque(false);
        addMouseListener(this);
    }

    public void setState(int which)
    {
        if(which >= 0 && which < 2)
            state = which;
        repaint();
    }

    public int getState()
    {
        return state & 1;
    }

    public Dimension getMinimumSize()
    {
        if(bAllowResize)
            return new Dimension(img[state].getWidth(this) / 2, img[state].getHeight(this) / 2);
        else
            return getPreferredSize();
    }

    public Dimension getMaximumSize()
    {
        if(bAllowResize)
            return new Dimension(img[state].getWidth(this) * 2, img[state].getHeight(this) * 2);
        else
            return getPreferredSize();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(img[state].getWidth(this), img[state].getHeight(this));
    }

    public void paintComponent(Graphics g)
    {
        Dimension d = getSize();
        Dimension d1 = new Dimension(img[state].getWidth(this), img[state].getHeight(this));
        Insets i = getInsets();
        if(bAllowResize)
        {
            if(d.width - i.left - i.right < d1.width)
            {
                double scaleme = (double)(d.width - i.left - i.right) / (double)d1.width;
                d1.width = (int)((double)d1.width * scaleme);
                d1.height = (int)((double)d1.height * scaleme);
            }
            if(d.height - i.top - i.bottom < d1.height)
            {
                double scaleme = (double)(d.height - i.top - i.bottom) / (double)d1.height;
                d1.width = (int)((double)d1.width * scaleme);
                d1.height = (int)((double)d1.height * scaleme);
            }
        }
        g.drawImage(img[state], i.left, i.top, d1.width, d1.height, this);
    }

    public Image getImage(int which)
    {
        return img[which];
    }

    public void mouseClicked(MouseEvent e)
    {
        state ^= 1;
        repaint();
    }

    public void mouseEntered(MouseEvent e)
    {
        if(state < 2)
        {
            state |= 2;
            repaint();
        }
    }

    public void mouseExited(MouseEvent e)
    {
        if(state >= 2)
        {
            state &= -3;
            repaint();
        }
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    private Image img[] = {
        null, null, null, null
    };
    private int state;
    public boolean bAllowResize;
}
