// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GPanel.java

package bcedit;

import bcedit.BCL.Circle;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.JPanel;
import javax.swing.JToolTip;

// Referenced classes of package bcedit:
//            JMultiLineToolTip, PImage, RoundRect

public class GPanel extends JPanel
    implements ImageObserver
{

    public GPanel(String filename)
    {
        highlightedObject = null;
        markObject = null;
        persistentOverlay = new Vector();
        img = null;
        oimg = null;
        odim = new Dimension(0, 0);
        xAlignment = 0;
        yAlignment = 0;
        bAllowResize = true;
        bKeepAspectRatio = true;
        lastRescale = 1.0D;
        bgColor = new Color(0x2f4260);
        Toolkit toolkit = getToolkit();
        img = toolkit.getImage(filename);
        checknwaitImage();
    }

    public GPanel(Image image)
    {
        highlightedObject = null;
        markObject = null;
        persistentOverlay = new Vector();
        img = null;
        oimg = null;
        odim = new Dimension(0, 0);
        xAlignment = 0;
        yAlignment = 0;
        bAllowResize = true;
        bKeepAspectRatio = true;
        lastRescale = 1.0D;
        bgColor = new Color(0x2f4260);
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

    public JToolTip createToolTip()
    {
        return new JMultiLineToolTip();
    }

    public void removeAllPersistentOverlay()
    {
        for(; persistentOverlay.size() > 0; persistentOverlay.remove(0));
    }

    public void removePersistentOverlay(Object o)
    {
        persistentOverlay.remove(o);
    }

    public void addPersistentOverlay(Object o)
    {
        persistentOverlay.add(o);
    }

    private void calcRepaint(Object o)
    {
        if(o != null)
        {
            Rectangle r = getPaintRect();
            if(o instanceof Circle)
            {
                Circle c = new Circle((Circle)o);
                if(bAllowResize && bKeepAspectRatio && lastRescale != 1.0D)
                {
                    c.r = (int)((double)c.r * lastRescale);
                    c.x = (int)((double)c.x * lastRescale);
                    c.y = (int)((double)c.y * lastRescale);
                }
                repaint((c.x + r.x) - c.r, (c.y + r.y) - c.r, 2 * c.r, 2 * c.r);
            } else
            if(o instanceof Rectangle)
            {
                Rectangle c = new Rectangle((Rectangle)o);
                if(bAllowResize && bKeepAspectRatio && lastRescale != 1.0D)
                {
                    c.width = (int)((double)c.width * lastRescale);
                    c.height = (int)((double)c.height * lastRescale);
                    c.x = (int)((double)c.x * lastRescale);
                    c.y = (int)((double)c.y * lastRescale);
                }
                repaint(c.x + r.x, c.y + r.y, c.width, c.height);
            } else
            if(o instanceof PImage)
            {
                PImage _p = (PImage)o;
                Rectangle c = new Rectangle(_p.x, _p.y, _p.img[0].getWidth(null), _p.img[0].getHeight(null));
                if(bAllowResize && bKeepAspectRatio && lastRescale != 1.0D)
                {
                    c.width = (int)((double)c.width * lastRescale);
                    c.height = (int)((double)c.height * lastRescale);
                    c.x = (int)((double)c.x * lastRescale);
                    c.y = (int)((double)c.y * lastRescale);
                }
                repaint(c.x + r.x, c.y + r.y, c.width, c.height);
            }
        }
    }

    public void setMarkObject(Object o)
    {
        calcRepaint(markObject);
        calcRepaint(o);
        markObject = o;
    }

    public void setHighlightObject(Object o)
    {
        calcRepaint(highlightedObject);
        calcRepaint(o);
        highlightedObject = o;
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

    public void update(Graphics g)
    {
        if(!isVisible())
            return;
        if(img != null)
        {
            Rectangle r = getPaintRect();
            if(isOpaque())
            {
                Dimension d = getSize();
                g.setColor(bgColor);
                g.fillRect(0, 0, d.width, d.height);
                g.drawImage(img, r.x, r.y, r.width, r.height, this);
            } else
            {
                g.drawImage(img, r.x, r.y, r.width, r.height, this);
            }
            highlightAndMark(g, r, highlightedObject, new Color(0x80ff0000, true), 1);
            highlightAndMark(g, r, markObject, new Color(0x50ffffff, true), 2);
            for(int i = 0; i < persistentOverlay.size(); i++)
                highlightAndMark(g, r, persistentOverlay.elementAt(i), new Color(0x500000ff, true), 0);

        } else
        {
            System.out.println("img = null?");
            if(isOpaque())
            {
                Dimension d = getSize();
                g.setColor(bgColor);
                g.fillRect(0, 0, d.width, d.height);
            }
        }
    }

    private void highlightAndMark(Graphics g, Rectangle r, Object o, Color color, int fkt)
    {
        if(o != null)
            if(o instanceof Circle)
            {
                Circle c = new Circle((Circle)o);
                if(bAllowResize && bKeepAspectRatio && lastRescale != 1.0D)
                {
                    c.r = (int)((double)c.r * lastRescale);
                    c.x = (int)((double)c.x * lastRescale);
                    c.y = (int)((double)c.y * lastRescale);
                }
                g.setColor(color);
                g.fillOval((c.x + r.x) - c.r, (c.y + r.y) - c.r, 2 * c.r, 2 * c.r);
            } else
            if(o instanceof Rectangle)
            {
                Rectangle c = new Rectangle((Rectangle)o);
                if(bAllowResize && bKeepAspectRatio && lastRescale != 1.0D)
                {
                    c.width = (int)((double)c.width * lastRescale);
                    c.height = (int)((double)c.height * lastRescale);
                    c.x = (int)((double)c.x * lastRescale);
                    c.y = (int)((double)c.y * lastRescale);
                }
                g.setColor(color);
                g.fillRect(c.x + r.x, c.y + r.y, c.width, c.height);
            } else
            if(o instanceof RoundRect)
            {
                RoundRect c = new RoundRect((RoundRect)o);
                if(bAllowResize && bKeepAspectRatio && lastRescale != 1.0D)
                {
                    c.arcWidth = (int)((double)c.arcWidth * lastRescale);
                    c.arcHeight = (int)((double)c.arcHeight * lastRescale);
                    c.width = (int)((double)c.width * lastRescale);
                    c.height = (int)((double)c.height * lastRescale);
                    c.x = (int)((double)c.x * lastRescale);
                    c.y = (int)((double)c.y * lastRescale);
                }
                g.setColor(color);
                g.fillRoundRect(c.x + r.x, c.y + r.y, c.width, c.height, c.arcWidth, c.arcHeight);
            } else
            if(o instanceof PImage)
            {
                PImage p = (PImage)o;
                if(fkt >= p.img.length)
                    return;
                if(p.img[fkt] == null)
                    return;
                Point c = new Point(p.x, p.y);
                Dimension d = new Dimension(p.img[fkt].getWidth(this), p.img[fkt].getHeight(this));
                if(bAllowResize && bKeepAspectRatio && lastRescale != 1.0D)
                {
                    d.width = (int)((double)d.width * lastRescale);
                    d.height = (int)((double)d.height * lastRescale);
                    c.x = (int)((double)c.x * lastRescale);
                    c.y = (int)((double)c.y * lastRescale);
                }
                g.drawImage(p.img[fkt], c.x + r.x, c.y + r.y, d.width, d.height, this);
            }
    }

    public void paintComponent(Graphics g)
    {
        update(g);
    }

    public Image getImage()
    {
        return img;
    }

    public synchronized void setImage(Image nImg)
    {
        img = nImg;
        repaint();
    }

    public void setImage(String filename)
    {
        img = getToolkit().getImage(filename);
        repaint();
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

    public boolean imageUpdate(Image image, int infoflags, int x, int y, int width, int height)
    {
        img = image;
        repaint();
        return true;
    }

    Object highlightedObject;
    Object markObject;
    Vector persistentOverlay;
    public static final int LEFT = 0;
    public static final int TOP = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 2;
    private Image img;
    private Image oimg;
    private Dimension odim;
    public int xAlignment;
    public int yAlignment;
    public boolean bAllowResize;
    public boolean bKeepAspectRatio;
    public double lastRescale;
    public Color bgColor;
}
