// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintStream;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

// Referenced classes of package bcedit:
//            ListLabel, BCDefaults

class JTestDialog extends JDialog
    implements MouseListener
{

    public JTestDialog()
    {
        frame = new JPanel();
        center = new JPanel();
        south = new JPanel();
        ftf = null;
        btn_ok = new ListLabel("Ok");
        btn_cancel = new ListLabel("Cancel");
        bg = BCDefaults.bgColor;
        fg = BCDefaults.fgColor;
        cg = new Color(0xff0000);
        confirmed = false;
        MaskFormatter fmt = null;
        try
        {
            fmt = new MaskFormatter("##':##':##'.##");
        }
        catch(ParseException e) { }
        fmt.setPlaceholderCharacter('0');
        ftf = new JFormattedTextField(fmt);
        ftf.setBackground(bg);
        ftf.setForeground(fg);
        setContentPane(frame);
        frame.setLayout(new BorderLayout(0, 0));
        frame.add("Center", ftf);
        frame.add("South", south);
        btn_ok.setBControlButtonDefault2();
        btn_ok.setName("OK-Button");
        btn_ok.addMouseListener(this);
        south.add(btn_ok);
        btn_cancel.setBControlButtonDefault2();
        btn_cancel.setName("CANCEL-Button");
        btn_cancel.addMouseListener(this);
        south.add(btn_cancel);
        setModal(true);
        setResizable(false);
        frame.setBackground(bg);
        center.setBackground(bg);
        south.setBackground(bg);
        center.setForeground(fg);
        south.setForeground(fg);
        pack();
    }

    public void mouseClicked(MouseEvent e)
    {
        if(e.getSource() instanceof JComponent)
        {
            JComponent btn = (JComponent)e.getSource();
            if(!btn.isEnabled())
                return;
            String s = btn.getName();
            if(s == null)
                return;
            if(s.equals("OK-Button"))
            {
                System.out.println(ftf.getValue());
                confirmed = true;
                hide();
            } else
            if(s.equals("CANCEL-Button"))
            {
                confirmed = false;
                hide();
            }
        }
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

    JPanel frame;
    JPanel center;
    JPanel south;
    JFormattedTextField ftf;
    ListLabel btn_ok;
    ListLabel btn_cancel;
    Color bg;
    Color fg;
    Color cg;
    boolean confirmed;
}
