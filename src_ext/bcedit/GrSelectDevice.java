// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            JPanelMy, MyButton, BCActionListener, GPanel, 
//            BCImages

class GrSelectDevice extends JPanelMy
    implements ActionListener
{

    public GrSelectDevice(boolean bChoice)
    {
        super(null);
        centerEdit = null;
        southEdit = null;
        Listener = null;
        choose = null;
        choose = new MyButton[2];
        southEdit = new JPanel();
        Listener = new BCActionListener();
        centerEdit = new GPanel(BCImages.getImage("Welcome_Main.png"));
        setOpaque(true);
        BorderLayout layout = new BorderLayout();
        southEdit.setOpaque(false);
        FlowLayout f = new FlowLayout(1, 8, 8);
        southEdit.setLayout(f);
        if(bChoice)
        {
            choose[0] = new MyButton("Edit BCF2000");
            choose[0].setActionCommand("EDITOR:Edit BCF2000");
            choose[0].addActionListener(Listener);
            choose[0].addActionListener(this);
            southEdit.add(choose[0]);
            choose[1] = new MyButton("Edit BCR2000");
            choose[1].setActionCommand("EDITOR:Edit BCR2000");
            choose[1].addActionListener(Listener);
            choose[1].addActionListener(this);
            southEdit.add(choose[1]);
        }
        setLayout(layout);
        add("South", southEdit);
        centerEdit.xAlignment = 1;
        centerEdit.yAlignment = 1;
        add("Center", centerEdit);
    }

    public synchronized void actionPerformed(ActionEvent e)
    {
        if(e.getSource() instanceof MyButton)
        {
            choose[0].setEnabled(true);
            choose[1].setEnabled(true);
            MyButton x = (MyButton)e.getSource();
            x.setEnabled(false);
        }
    }

    GPanel centerEdit;
    JPanel southEdit;
    BCActionListener Listener;
    MyButton choose[];
}
