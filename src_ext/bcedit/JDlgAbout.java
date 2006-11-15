// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

// Referenced classes of package bcedit:
//            ListLabel, GPanel, GPanelAnimation, BCImages, 
//            FPanel2, bcedit, MainFrame

class JDlgAbout extends JDialog
    implements MouseListener
{

    public JDlgAbout()
    {
        fp2 = null;
        frame = new JPanel();
        btn_ok = new ListLabel("Ok");
        btn_cancel = new ListLabel("Cancel");
        bg = new Color(0x4a7597);
        fg = new Color(0xffd800);
        gpImage = new GPanel(BCImages.getImage("LOGO Behringer.png"));
        anim = null;
        imglist = new Image[20];
        confirmed = false;
        fp2 = FPanel2.createPanel(this, frame);
        fp2.setResizable(false);
        if(fp2 != null)
            setContentPane(fp2);
        else
            setContentPane(frame);
        frame.setLayout(new BorderLayout(0, 0));
        btn_ok.setBControlButtonDefault2();
        btn_ok.setName("OK-Button");
        btn_ok.addMouseListener(this);
        JPanel pselect = new JPanel();
        pselect.setLayout(new GridLayout(2, 1));
        JLabel ll1 = new JLabel("B-Control Series Editor");
        pselect.add(ll1);
        ll1 = new JLabel("Version 0.2 beta 3");
        pselect.add(ll1);
        pselect.setOpaque(true);
        pselect.setBackground(new Color(0x4a7597));
        pselect.setBorder(BorderFactory.createEtchedBorder(0));
        gpImage.bAllowResize = false;
        gpImage.setLayout(null);
        gpImage.add(pselect);
        frame.add("South", btn_ok);
        frame.add("Center", gpImage);
        setModal(true);
        setResizable(false);
        setTitle("B-Control Series Editor");
        setUndecorated(true);
        frame.setBackground(bg);
        pack();
        Rectangle r = gpImage.getBounds();
        Dimension d = pselect.getPreferredSize();
        r.x = (r.width - d.width) / 2;
        r.y = (r.height - d.height) / 2;
        r.height = d.height + 2;
        r.width = d.width + 2;
        pselect.setBounds(r);
        centerOnParent();
        frame.addMouseListener(this);
        gpImage.addMouseListener(this);
        for(int i = 1; i <= 20; i++)
        {
            String name = "logo-a";
            if(i < 10)
                name = name + "0";
            name = name + Integer.toString(i) + ".png";
            imglist[i - 1] = BCImages.getImage(name);
        }

        anim = new GPanelAnimation(imglist, 120L, gpImage);
        anim.start();
    }

    private void centerOnParent()
    {
        Rectangle r = bcedit.app.getBounds();
        Rectangle d = getBounds();
        if(d.height > r.height)
            d.height = r.height;
        setBounds(r.x + (r.width - d.width) / 2, r.y + (r.height - d.height) / 2, d.width, d.height);
    }

    public void mouseClicked(MouseEvent e)
    {
        anim.stop_anim();
        hide();
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    FPanel2 fp2;
    JPanel frame;
    JPanel jpBorder[] = {
        null, null, null, null, null
    };
    ListLabel btn_ok;
    ListLabel btn_cancel;
    Color bg;
    Color fg;
    GPanel gpImage;
    GPanelAnimation anim;
    Image imglist[];
    boolean confirmed;
}
