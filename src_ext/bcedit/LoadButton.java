// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LoadButton.java

package bcedit;

import java.awt.BorderLayout;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            RedButton, GPanel, BCImages

public class LoadButton extends JPanel
{

    public LoadButton()
    {
        btn = null;
        gp1 = null;
        gp2 = null;
        gp3 = null;
        layout = null;
        layout = new BorderLayout();
        setLayout(layout);
        btn = new RedButton();
        gp1 = new GPanel(BCImages.getImage("Pfeilrechts.png"));
        gp2 = new GPanel(BCImages.getImage("Pfeilrechts.png"));
        gp3 = new GPanel(BCImages.getImage("load.gif"));
        setOpaque(false);
        gp3.xAlignment = 1;
        add("West", gp1);
        add("Center", btn);
        add("East", gp2);
        add("South", gp3);
    }

    public GPanel getWestImage()
    {
        return gp1;
    }

    public RedButton getCenterImage()
    {
        return btn;
    }

    public GPanel getEastImage()
    {
        return gp2;
    }

    public GPanel getSouthImage()
    {
        return gp3;
    }

    private RedButton btn;
    private GPanel gp1;
    private GPanel gp2;
    private GPanel gp3;
    private BorderLayout layout;
}
