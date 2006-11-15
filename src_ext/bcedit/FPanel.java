// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FPanel.java

package bcedit;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            GPanel, BCImages

class FPanel extends JPanel
{

    public FPanel()
    {
        pTop = new JPanel();
        pBottom = new JPanel();
        pLeft = new JPanel();
        pRight = new JPanel();
        centerObject = null;
        height = 0;
        width = 0;
        init(0, 0, -1);
    }

    public FPanel(int width, int height, int t)
    {
        pTop = new JPanel();
        pBottom = new JPanel();
        pLeft = new JPanel();
        pRight = new JPanel();
        centerObject = null;
        this.height = 0;
        this.width = 0;
        init(width, height, t);
    }

    private void init(int width, int height, int t)
    {
        this.height = height;
        this.width = width;
        BorderLayout layout = new BorderLayout();
        BorderLayout layoutTop = new BorderLayout();
        BorderLayout layoutLeft = new BorderLayout();
        BorderLayout layoutRight = new BorderLayout();
        BorderLayout layoutBottom = new BorderLayout();
        pTop.setOpaque(false);
        pLeft.setOpaque(false);
        pRight.setOpaque(false);
        pBottom.setOpaque(false);
        for(int i = 0; i < gp.length; i++)
            gp[i].bKeepAspectRatio = false;

        setLayout(layout);
        add("North", pTop);
        pTop.setLayout(layoutTop);
        pTop.add("West", gp[0]);
        pTop.add("Center", gp[1]);
        pTop.add("East", gp[2]);
        pLeft.setLayout(layoutLeft);
        pLeft.add("North", gp[3]);
        pLeft.add("Center", gp[4]);
        pLeft.add("South", gp[5]);
        add("West", pLeft);
        pRight.setLayout(layoutRight);
        pRight.add("North", gp[6]);
        pRight.add("Center", gp[7]);
        pRight.add("South", gp[8]);
        add("East", pRight);
        pBottom.setLayout(layoutBottom);
        add("South", pBottom);
        pBottom.add("West", gp[9]);
        pBottom.add("Center", gp[10]);
        pBottom.add("East", gp[11]);
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

    public static FPanel createPanel(Component c)
    {
        FPanel fp = new FPanel();
        fp.setOpaque(false);
        fp.setCenterObject(c);
        return fp;
    }

    GPanel gp[] = {
        new GPanel(BCImages.getImage("Panel_Layout_01.png")), new GPanel(BCImages.getImage("Panel_Layout_02.png")), new GPanel(BCImages.getImage("Panel_Layout_03.png")), new GPanel(BCImages.getImage("Panel_Layout_04_top.png")), new GPanel(BCImages.getImage("Panel_Layout_04_center.png")), new GPanel(BCImages.getImage("Panel_Layout_04_bottom.png")), new GPanel(BCImages.getImage("Panel_Layout_06_top.png")), new GPanel(BCImages.getImage("Panel_Layout_06_center.png")), new GPanel(BCImages.getImage("Panel_Layout_06_bottom.png")), new GPanel(BCImages.getImage("Panel_Layout_07.png")), 
        new GPanel(BCImages.getImage("Panel_Layout_08.png")), new GPanel(BCImages.getImage("Panel_Layout_09.png"))
    };
    JPanel pTop;
    JPanel pBottom;
    JPanel pLeft;
    JPanel pRight;
    Component centerObject;
    int height;
    int width;
}
