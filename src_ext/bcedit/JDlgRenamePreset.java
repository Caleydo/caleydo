// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

// Referenced classes of package bcedit:
//            ListLabel, SizedPanel, FPanel2, BCDefaults, 
//            FPanel, bcedit, MainFrame

class JDlgRenamePreset extends JDialog
    implements ItemListener, ActionListener, MouseListener, MouseMotionListener
{

    public JDlgRenamePreset(JList srcList)
    {
        useStandardFrame = true;
        startpoint = new Point(0, 0);
        bMoveMe = false;
        frame = new JPanel();
        center = new JPanel();
        south = new JPanel();
        btn_ok = new ListLabel("Ok");
        btn_cancel = new ListLabel("Cancel");
        bg = BCDefaults.bgColor;
        fg = BCDefaults.fgColor;
        cg = new Color(0xff0000);
        confirmed = false;
        textField = null;
        int selection[] = srcList.getSelectedIndices();
        JPanel frame1 = new JPanel(new FlowLayout(0, 0, 0));
        frame1.setLayout(new BorderLayout(0, 0));
        frame1.add("North", makeSizedPanel(8, 8, bg));
        frame1.add("West", makeSizedPanel(8, 8, bg));
        frame1.add("East", makeSizedPanel(8, 8, bg));
        frame1.add("Center", frame);
        frame1.setOpaque(true);
        frame1.setBackground(bg);
        if(!useStandardFrame)
        {
            FPanel2 fp2 = FPanel2.createPanel(this, frame1);
            fp2.setResizable(false);
            setContentPane(fp2);
        } else
        {
            setContentPane(frame1);
        }
        frame.setOpaque(false);
        frame.setLayout(new BorderLayout(0, 0));
        center.setLayout(new GridLayout((selection.length + 1) / 2, 2, 0, 0));
        textField = new JFormattedTextField[selection.length];
        for(int i = 0; i < selection.length; i++)
        {
            JPanel b = new JPanel();
            b.setBackground(BCDefaults.bgPanelColor);
            b.setLayout(new FlowLayout(0));
            String name1 = srcList.getModel().getElementAt(selection[i]).toString();
            String name2 = name1.substring(0, name1.indexOf(":"));
            name1 = name1.substring(name1.indexOf(":") + 1).trim();
            MaskFormatter fmt = null;
            try
            {
                fmt = new MaskFormatter("************************");
            }
            catch(ParseException e) { }
            fmt.setPlaceholderCharacter(' ');
            fmt.setValidCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-+ 0123456789/\\()<>[]=!\247$%&@,.;:|*");
            textField[i] = new JFormattedTextField(fmt);
            textField[i].setValue(name1);
            ListLabel ll1 = new ListLabel(name2);
            ll1.setBControlDefault();
            b.add(ll1);
            b.add(textField[i]);
            center.add(b);
        }

        btn_ok.setBControlButtonDefault2();
        btn_ok.setName("OK-Button");
        btn_ok.addMouseListener(this);
        south.add(btn_ok);
        btn_cancel.setBControlButtonDefault2();
        btn_cancel.setName("CANCEL-Button");
        btn_cancel.addMouseListener(this);
        south.add(btn_cancel);
        frame.add("Center", FPanel.createPanel(center));
        frame.add("South", south);
        setModal(true);
        setResizable(false);
        frame.setBackground(bg);
        center.setBackground(BCDefaults.bgPanelColor);
        south.setBackground(bg);
        center.setForeground(fg);
        south.setForeground(fg);
        if(!isDisplayable() && !useStandardFrame)
            setUndecorated(true);
        pack();
        Rectangle r = bcedit.app.getBounds();
        Rectangle d = getBounds();
        if(d.height > r.height)
            d.height = r.height;
        setBounds(r.x + (r.width - d.width) / 2, r.y + (r.height - d.height) / 2, d.width, d.height);
        setCursor(BCDefaults.curDefault);
    }

    private SizedPanel makeSizedPanel(int w, int h, Color bgColor)
    {
        SizedPanel sp = new SizedPanel(w, h);
        sp.setOpaque(true);
        sp.setBackground(bgColor);
        return sp;
    }

    public boolean wasCancelled()
    {
        return !confirmed;
    }

    public boolean wasConfirmed()
    {
        return confirmed;
    }

    public JFormattedTextField[] getTextFields()
    {
        return textField;
    }

    public void setTitle(String nt)
    {
        if(isUndecorated())
        {
            java.awt.Container c = getContentPane();
            if(c instanceof FPanel2)
                ((FPanel2)c).setTitle(nt);
        } else
        {
            super.setTitle(nt);
        }
    }

    public void actionPerformed(ActionEvent e)
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

    public void itemStateChanged(ItemEvent itemevent)
    {
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

    public void mousePressed(MouseEvent e)
    {
        if(!bMoveMe)
        {
            java.awt.Container c = null;
            bMoveMe = true;
            Point p1 = getLocationOnScreen();
            Point p2 = e.getComponent().getLocationOnScreen();
            p2.translate(e.getX(), e.getY());
            startpoint = e.getPoint();
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        bMoveMe = false;
    }

    public void mouseMoved(MouseEvent mouseevent)
    {
    }

    public void mouseDragged(MouseEvent e)
    {
        if(bMoveMe && (e.getPoint().x != startpoint.x || e.getPoint().y != startpoint.y))
        {
            Point cl1 = getLocationOnScreen();
            cl1.x += e.getPoint().x - startpoint.x;
            cl1.y += e.getPoint().y - startpoint.y;
            setLocation(cl1.x, cl1.y);
        }
    }

    boolean useStandardFrame;
    Point startpoint;
    boolean bMoveMe;
    JPanel frame;
    JPanel center;
    JPanel south;
    ListLabel btn_ok;
    ListLabel btn_cancel;
    Color bg;
    Color fg;
    Color cg;
    boolean confirmed;
    JFormattedTextField textField[];
}
