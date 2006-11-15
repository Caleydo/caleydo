// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GPanel.java

package bcedit;

import java.awt.*;
import java.io.PrintStream;
import javax.swing.JPanel;

class ImagePanel extends JPanel
{

    public ImagePanel(String filename)
    {
        bgColor = new Color(0x2f4260);
        img = null;
        xAlignment = 0;
        yAlignment = 0;
        bAllowResize = true;
        bKeepAspectRatio = true;
        lastRescale = 1.0D;
        Toolkit toolkit = getToolkit();
        img = toolkit.getImage(filename);
        checknwaitImage();
    }

    public ImagePanel(Image image)
    {
        bgColor = new Color(0x2f4260);
        img = null;
        xAlignment = 0;
        yAlignment = 0;
        bAllowResize = true;
        bKeepAspectRatio = true;
        lastRescale = 1.0D;
        img = image;
        checknwaitImage();
    }

    private synchronized void checknwaitImage()
    {
        setOpaque(false);
        if(!isDoubleBuffered())
            setDoubleBuffered(true);
        if(!prepareImage(img, this))
            while((checkImage(img, this) & 3) != 3) 
                try
                {
                    wait(2L);
                }
                catch(IllegalArgumentException e1)
                {
                    System.out.println(e1.getMessage());
                }
                catch(IllegalMonitorStateException e2)
                {
                    System.out.println(e2.getMessage());
                }
                catch(InterruptedException e3)
                {
                    System.out.println(e3.getMessage());
                }
    }

    public void paintComponent(Graphics g)
    {
        update(g);
    }

    public void update(Graphics g)
    {
        if(!isVisible())
            return;
        Rectangle r = getPaintRect();
        if(isOpaque())
        {
            Dimension d = getSize();
            g.setColor(bgColor);
            g.fillRect(0, 0, d.width, d.height);
            g.drawImage(img, r.x, r.y, r.width, r.height, this);
        } else
        if(img != null)
            g.drawImage(img, r.x, r.y, r.width, r.height, this);
    }

    public Rectangle getPaintRect()
    {
        Dimension d = getSize();
        if(img == null)
            return new Rectangle(0, 0, d.width, d.height);
        Dimension d1 = new Dimension(img.getWidth(this), img.getHeight(this));
        Insets i = getInsets();
        lastRescale = 1.0D;
        if(bAllowResize)
            if(!bKeepAspectRatio)
            {
                d1.width = d.width;
                d1.height = d.height;
            } else
            {
                double f1 = (double)d1.width / (double)d1.height;
                double f2 = (double)d.width / (double)d.height;
                if(f1 >= f2)
                {
                    double scaleme = (double)(d.width - i.left - i.right) / (double)d1.width;
                    d1.width = (int)((double)d1.width * scaleme);
                    d1.height = (int)((double)d1.height * scaleme);
                    lastRescale = scaleme;
                } else
                {
                    double scaleme = (double)(d.height - i.top - i.bottom) / (double)d1.height;
                    d1.width = (int)((double)d1.width * scaleme);
                    d1.height = (int)((double)d1.height * scaleme);
                    lastRescale = scaleme;
                }
            }
        int x;
        switch(xAlignment)
        {
        case 1: // '\001'
            x = (d.width - i.left - i.right - d1.width) / 2;
            break;

        case 2: // '\002'
            x = d.width - i.right - d1.width;
            break;

        default:
            x = i.left;
            break;
        }
        int y;
        switch(yAlignment)
        {
        case 1: // '\001'
            y = (d.height - i.top - i.bottom - d1.height) / 2;
            break;

        case 2: // '\002'
            y = d.height - i.bottom - d1.height;
            break;

        default:
            y = i.top;
            break;
        }
        return new Rectangle(x, y, d1.width, d1.height);
    }

    public Dimension getMinimumSize()
    {
        if(getComponentCount() != 0)
            return super.getMinimumSize();
        if(bAllowResize && img != null)
            return new Dimension(img.getWidth(this) / 2, img.getHeight(this) / 2);
        else
            return getPreferredSize();
    }

    public Dimension getMaximumSize()
    {
        if(getComponentCount() != 0)
            return super.getMaximumSize();
        if(bAllowResize && img != null)
            return new Dimension(img.getWidth(this) * 2, img.getHeight(this) * 2);
        else
            return getPreferredSize();
    }

    public Dimension getPreferredSize()
    {
        if(bAllowResize && getComponentCount() != 0)
        {
            Dimension d = super.getPreferredSize();
            if(bKeepAspectRatio)
            {
                double f1 = (double)img.getWidth(this) / (double)img.getHeight(this);
                double f2 = (double)d.width / (double)d.height;
                if(f2 > f1)
                    d.height = (int)((double)d.width * f1);
                else
                    d.width = (int)((double)d.height * f1);
            }
            return d;
        }
        if(img != null)
            return new Dimension(img.getWidth(this), img.getHeight(this));
        else
            return new Dimension(32, 32);
    }

    public Color bgColor;
    public static final int LEFT = 0;
    public static final int TOP = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 2;
    private Image img;
    public int xAlignment;
    public int yAlignment;
    public boolean bAllowResize;
    public boolean bKeepAspectRatio;
    public double lastRescale;
}
