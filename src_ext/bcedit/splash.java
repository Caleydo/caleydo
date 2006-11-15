// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   splash.java

package bcedit;

import java.awt.*;
import javax.swing.*;

// Referenced classes of package bcedit:
//            loadSingleGraphic, ListLabel

public class splash extends JWindow
{

    public splash()
    {
        super(new Frame());
        loadSingleGraphic gl = new loadSingleGraphic();
        JLabel l = new JLabel(new ImageIcon(gl.getImage("splash.png")));
        getContentPane().add(l, "Center");
        ListLabel ll1 = new ListLabel("Version 0.2 beta 3");
        ll1.setBControlDefault();
        getContentPane().add(ll1, "South");
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width / 2 - labelSize.width / 2, screenSize.height / 2 - labelSize.height / 2);
        setVisible(true);
        screenSize = null;
        labelSize = null;
    }
}
