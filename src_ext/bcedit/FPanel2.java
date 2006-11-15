// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FPanel.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            GPanel, JLabel2, BCImages, BCDefaults

class FPanel2 extends JPanel
    implements MouseListener, MouseMotionListener
{

    public FPanel2(Component parent)
    {
        resizeable = true;
        startpoint = new Point(0, 0);
        moveFlag = -1;
        gpTopLeft = new GPanel(BCImages.getImage("WindowFrame_upperleft.png"));
        gpTop = new GPanel(BCImages.getImage("WindowFrame_uppercenter.png"));
        gpTopRight = new GPanel(BCImages.getImage("WindowFrame_upperright.png"));
        gpLeft = new GPanel(BCImages.getImage("WindowFrame_centerleft.png"));
        gpRight = new GPanel(BCImages.getImage("WindowFrame_centerright.png"));
        gpBottomLeft = new GPanel(BCImages.getImage("WindowFrame_lowerleft.png"));
        gpBottom = new GPanel(BCImages.getImage("WindowFrame_lowercenter.png"));
        gpBottomRight = new GPanel(BCImages.getImage("WindowFrame_lowerright.png"));
        pTop = new JPanel();
        pBottom = new JPanel();
        pLeft = new JPanel();
        pRight = new JPanel();
        centerObject = null;
        this.parent = null;
        height = 0;
        width = 0;
        this.parent = parent;
        init(0, 0);
    }

    public FPanel2()
    {
        resizeable = true;
        startpoint = new Point(0, 0);
        moveFlag = -1;
        gpTopLeft = new GPanel(BCImages.getImage("WindowFrame_upperleft.png"));
        gpTop = new GPanel(BCImages.getImage("WindowFrame_uppercenter.png"));
        gpTopRight = new GPanel(BCImages.getImage("WindowFrame_upperright.png"));
        gpLeft = new GPanel(BCImages.getImage("WindowFrame_centerleft.png"));
        gpRight = new GPanel(BCImages.getImage("WindowFrame_centerright.png"));
        gpBottomLeft = new GPanel(BCImages.getImage("WindowFrame_lowerleft.png"));
        gpBottom = new GPanel(BCImages.getImage("WindowFrame_lowercenter.png"));
        gpBottomRight = new GPanel(BCImages.getImage("WindowFrame_lowerright.png"));
        pTop = new JPanel();
        pBottom = new JPanel();
        pLeft = new JPanel();
        pRight = new JPanel();
        centerObject = null;
        parent = null;
        height = 0;
        width = 0;
        init(0, 0);
    }

    public FPanel2(int width, int height, int t)
    {
        resizeable = true;
        startpoint = new Point(0, 0);
        moveFlag = -1;
        gpTopLeft = new GPanel(BCImages.getImage("WindowFrame_upperleft.png"));
        gpTop = new GPanel(BCImages.getImage("WindowFrame_uppercenter.png"));
        gpTopRight = new GPanel(BCImages.getImage("WindowFrame_upperright.png"));
        gpLeft = new GPanel(BCImages.getImage("WindowFrame_centerleft.png"));
        gpRight = new GPanel(BCImages.getImage("WindowFrame_centerright.png"));
        gpBottomLeft = new GPanel(BCImages.getImage("WindowFrame_lowerleft.png"));
        gpBottom = new GPanel(BCImages.getImage("WindowFrame_lowercenter.png"));
        gpBottomRight = new GPanel(BCImages.getImage("WindowFrame_lowerright.png"));
        pTop = new JPanel();
        pBottom = new JPanel();
        pLeft = new JPanel();
        pRight = new JPanel();
        centerObject = null;
        parent = null;
        this.height = 0;
        this.width = 0;
        init(width, height);
    }

    public FPanel2(int width, int height)
    {
        resizeable = true;
        startpoint = new Point(0, 0);
        moveFlag = -1;
        gpTopLeft = new GPanel(BCImages.getImage("WindowFrame_upperleft.png"));
        gpTop = new GPanel(BCImages.getImage("WindowFrame_uppercenter.png"));
        gpTopRight = new GPanel(BCImages.getImage("WindowFrame_upperright.png"));
        gpLeft = new GPanel(BCImages.getImage("WindowFrame_centerleft.png"));
        gpRight = new GPanel(BCImages.getImage("WindowFrame_centerright.png"));
        gpBottomLeft = new GPanel(BCImages.getImage("WindowFrame_lowerleft.png"));
        gpBottom = new GPanel(BCImages.getImage("WindowFrame_lowercenter.png"));
        gpBottomRight = new GPanel(BCImages.getImage("WindowFrame_lowerright.png"));
        pTop = new JPanel();
        pBottom = new JPanel();
        pLeft = new JPanel();
        pRight = new JPanel();
        centerObject = null;
        parent = null;
        this.height = 0;
        this.width = 0;
        init(width, height);
    }

    private void init(int width, int height)
    {
        this.height = height;
        this.width = width;
        pTop.setName("pTop");
        pBottom.setName("pBottom");
        pLeft.setName("pLeft");
        pRight.setName("pRight");
        BorderLayout layout = new BorderLayout();
        BorderLayout layoutTop = new BorderLayout();
        BorderLayout layoutLeft = new BorderLayout();
        BorderLayout layoutRight = new BorderLayout();
        BorderLayout layoutBottom = new BorderLayout();
        pTop.setOpaque(false);
        pLeft.setOpaque(false);
        pRight.setOpaque(false);
        pBottom.setOpaque(false);
        gpTop.bKeepAspectRatio = false;
        gpBottom.bKeepAspectRatio = false;
        gpLeft.bKeepAspectRatio = false;
        gpRight.bKeepAspectRatio = false;
        setLayout(layout);
        add("North", pTop);
        pTop.setLayout(layoutTop);
        pTop.add("West", gpTopLeft);
        pTop.add("Center", gpTop);
        pTop.add("East", gpTopRight);
        pLeft.setLayout(layoutLeft);
        add("West", gpLeft);
        add("East", gpRight);
        pBottom.setLayout(layoutBottom);
        add("South", pBottom);
        pBottom.add("West", gpBottomLeft);
        pBottom.add("Center", gpBottom);
        pBottom.add("East", gpBottomRight);
        setCursor(BCDefaults.curDefault);
        if(parent != null)
        {
            if(parent instanceof Dialog)
                resizeable = ((Dialog)parent).isResizable();
            if(parent instanceof Frame)
                resizeable = ((Frame)parent).isResizable();
            if(parent instanceof JInternalFrame)
                resizeable = ((JInternalFrame)parent).isResizable();
        }
        setCursorShapesAndListener();
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void setTitle(String newTitle)
    {
        if(gpTop instanceof Container)
        {
            Component cc[] = gpTop.getComponents();
            for(int i = 0; i < cc.length; i++)
                gpTop.remove(cc[i]);

        }
        if(newTitle != null)
        {
            JLabel2 l = new JLabel2(newTitle);
            gpTop.setLayout(new FlowLayout(0, 0, 0));
            l.setForeground(BCDefaults.bgColor);
            gpTop.add(l);
        }
    }

    public static FPanel2 createPanel(Component c)
    {
        FPanel2 fp = new FPanel2();
        fp.setOpaque(false);
        fp.setCenterObject(c);
        return fp;
    }

    public static FPanel2 createPanel(Component o, Component c)
    {
        FPanel2 fp = new FPanel2(o);
        fp.setOpaque(false);
        fp.setCenterObject(c);
        return fp;
    }

    public void setResizable(boolean flag)
    {
        resizeable = flag;
        setCursorShapesAndListener();
    }

    private void setCursorShapesAndListener()
    {
        if(parent != null && resizeable)
        {
            gpRight.setCursor(BCDefaults.curResizeH);
            gpLeft.setCursor(BCDefaults.curResizeH);
            gpBottomLeft.setCursor(BCDefaults.curResizeD2);
            gpBottom.setCursor(BCDefaults.curResizeV);
            gpBottomRight.setCursor(BCDefaults.curResizeD1);
            gpRight.addMouseListener(this);
            gpRight.addMouseMotionListener(this);
            gpLeft.addMouseListener(this);
            gpLeft.addMouseMotionListener(this);
            gpBottomLeft.addMouseListener(this);
            gpBottomLeft.addMouseMotionListener(this);
            gpBottom.addMouseListener(this);
            gpBottom.addMouseMotionListener(this);
            gpBottomRight.addMouseListener(this);
            gpBottomRight.addMouseMotionListener(this);
        } else
        {
            gpRight.setCursor(BCDefaults.curDefault);
            gpLeft.setCursor(BCDefaults.curDefault);
            gpBottomLeft.setCursor(BCDefaults.curDefault);
            gpBottom.setCursor(BCDefaults.curDefault);
            gpBottomRight.setCursor(BCDefaults.curDefault);
            gpRight.removeMouseListener(this);
            gpRight.removeMouseMotionListener(this);
            gpLeft.removeMouseListener(this);
            gpLeft.removeMouseMotionListener(this);
            gpBottomLeft.removeMouseListener(this);
            gpBottomLeft.removeMouseMotionListener(this);
            gpBottom.removeMouseListener(this);
            gpBottom.removeMouseMotionListener(this);
            gpBottomRight.removeMouseListener(this);
            gpBottomRight.removeMouseMotionListener(this);
        }
    }

    public Component getMyParent()
    {
        return parent;
    }

    public void setMyParent(Component p)
    {
        parent = p;
        setCursorShapesAndListener();
    }

    public void setCenterObject(Component o)
    {
        if(centerObject != null)
            remove(centerObject);
        centerObject = o;
        if(centerObject != null)
            add("Center", centerObject);
        invalidate();
    }

    public Component getCenterObject()
    {
        return centerObject;
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent e)
    {
        if(e.getButton() == 1)
        {
            Component c = e.getComponent();
            if(c == gpRight && resizeable)
            {
                moveFlag = 2;
                startpoint = e.getPoint();
            } else
            if(c == gpLeft && resizeable)
            {
                moveFlag = 1;
                startpoint = e.getPoint();
            } else
            if(c == gpBottomLeft && resizeable)
            {
                moveFlag = 9;
                startpoint = e.getPoint();
            } else
            if(c == gpBottom && resizeable)
            {
                moveFlag = 8;
                startpoint = e.getPoint();
            } else
            if(c == gpBottomRight && resizeable)
            {
                moveFlag = 10;
                startpoint = e.getPoint();
            } else
            if(moveFlag == -1 && parent != null)
            {
                moveFlag = 0;
                startpoint = e.getPoint();
            }
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        if(moveFlag != -1)
            moveFlag = -1;
    }

    public void mouseMoved(MouseEvent mouseevent)
    {
    }

    public void mouseDragged(MouseEvent e)
    {
        if(moveFlag == -1)
            return;
        if(moveFlag == 0)
        {
            if(e.getPoint().x != startpoint.x || e.getPoint().y != startpoint.y)
            {
                Point cl1 = parent.getLocationOnScreen();
                cl1.x += e.getPoint().x - startpoint.x;
                cl1.y += e.getPoint().y - startpoint.y;
                parent.setLocation(cl1.x, cl1.y);
            }
        } else
        {
            Rectangle b = parent.getBounds();
            Point p = e.getPoint();
            boolean bx = false;
            p.x -= startpoint.x;
            p.y -= startpoint.y;
            if((moveFlag & 1) != 0)
            {
                b.x += p.x;
                b.width -= p.x;
                bx = true;
            }
            if((moveFlag & 2) != 0)
            {
                b.width += p.x;
                bx = true;
            }
            if((moveFlag & 4) != 0)
            {
                b.y += p.y;
                b.height -= p.y;
                bx = true;
            }
            if((moveFlag & 8) != 0)
            {
                b.height += p.y;
                bx = true;
            }
            if(bx)
            {
                if(b.width < 32)
                    b.width = 32;
                if(b.height < 32)
                    b.height = 32;
                parent.setBounds(b);
                ((Container)parent).validate();
            }
        }
    }

    public boolean resizeable;
    static final int NOMOVE = -1;
    static final int MOVE = 0;
    static final int RESIZE_LEFT = 1;
    static final int RESIZE_RIGHT = 2;
    static final int RESIZE_TOP = 4;
    static final int RESIZE_BOTTOM = 8;
    Point startpoint;
    int moveFlag;
    GPanel gpTopLeft;
    GPanel gpTop;
    GPanel gpTopRight;
    GPanel gpLeft;
    GPanel gpRight;
    GPanel gpBottomLeft;
    GPanel gpBottom;
    GPanel gpBottomRight;
    JPanel pTop;
    JPanel pBottom;
    JPanel pLeft;
    JPanel pRight;
    Component centerObject;
    Component parent;
    int height;
    int width;
}
