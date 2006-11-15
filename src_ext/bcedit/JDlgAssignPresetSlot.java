// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.*;

// Referenced classes of package bcedit:
//            ListLabel, CbListCellRenderer, SizedPanel, FPanel2, 
//            BCDefaults, FPanel, bcedit, MainFrame

class JDlgAssignPresetSlot extends JDialog
    implements ItemListener, ActionListener, MouseListener, MouseMotionListener
{

    public JDlgAssignPresetSlot(JList srcList)
    {
        useStandardFrame = true;
        startpoint = new Point(0, 0);
        bMoveMe = false;
        frame = new JPanel();
        vcombo = new Vector();
        center = new JPanel();
        south = new JPanel();
        north = new JPanel();
        btn_ok = new ListLabel("Ok");
        btn_cancel = new ListLabel("Cancel");
        bg = BCDefaults.bgColor;
        fg = BCDefaults.fgColor;
        cg = new Color(0xff0000);
        confirmed = false;
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
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        frame.setLayout(new BorderLayout(0, 0));
        center.setLayout(new GridLayout((selection.length + 1) / 2, 2, 0, 0));
        for(int i = 0; i < selection.length; i++)
        {
            JPanel b = new JPanel();
            b.setBackground(BCDefaults.bgPanelColor);
            b.setLayout(new FlowLayout(0));
            Vector v = new Vector(32);
            for(int j = 1; j < 33; j++)
            {
                String s1;
                if(j < 10)
                    s1 = "0" + Integer.toString(j);
                else
                    s1 = Integer.toString(j);
                v.add(s1);
            }

            JComboBox cb1 = new JComboBox(v);
            cb1.setBackground(bg);
            cb1.setForeground(fg);
            cb1.setRenderer(new CbListCellRenderer());
            cb1.setBorder(BorderFactory.createEmptyBorder());
            cb1.addItemListener(this);
            cb1.setPrototypeDisplayValue(new String("0000"));
            vcombo.add(cb1);
            b.add((JComboBox)vcombo.elementAt(i));
            ((JComboBox)vcombo.elementAt(i)).setSelectedIndex(selection[i]);
            String name1 = srcList.getModel().getElementAt(selection[i]).toString();
            name1 = name1.substring(name1.indexOf(":") + 1);
            ListLabel ll1 = new ListLabel(name1);
            ll1.setBControlDefault();
            b.add(ll1);
            b.addMouseListener(this);
            b.addMouseMotionListener(this);
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
        frame.add("North", north);
        north.setLayout(new GridLayout(2, 1));
        north.add(new JLabel(" You may assign new preset numbers now"));
        north.getComponent(0).setForeground(BCDefaults.fgColor);
        north.add(new JLabel(" if you are not lucky with the default."));
        north.getComponent(1).setForeground(BCDefaults.fgColor);
        setModal(true);
        setResizable(false);
        frame.setBackground(bg);
        center.setBackground(BCDefaults.bgPanelColor);
        south.setBackground(bg);
        north.setBackground(bg);
        center.setForeground(fg);
        south.setForeground(fg);
        north.setForeground(fg);
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
        sp.addMouseListener(this);
        sp.addMouseMotionListener(this);
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

    public int[] getSelection()
    {
        int sel[] = new int[vcombo.size()];
        for(int i = 0; i < vcombo.size(); i++)
            sel[i] = ((JComboBox)vcombo.elementAt(i)).getSelectedIndex();

        return sel;
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

    public void itemStateChanged(ItemEvent e)
    {
        if(e.getStateChange() == 1)
        {
            int check[] = new int[vcombo.size()];
            for(int i = 0; i < vcombo.size(); i++)
                ((JComboBox)vcombo.elementAt(i)).setForeground(fg);

            for(int i = 0; i < vcombo.size(); i++)
            {
                check[i] = ((JComboBox)vcombo.elementAt(i)).getSelectedIndex();
                for(int j = 0; j < i; j++)
                    if(check[i] == check[j])
                    {
                        ((JComboBox)vcombo.elementAt(i)).setForeground(cg);
                        ((JComboBox)vcombo.elementAt(j)).setForeground(cg);
                        btn_ok.setEnabled(false);
                        btn_ok.repaint();
                        return;
                    }

            }

            btn_ok.setEnabled(true);
            btn_ok.repaint();
        }
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
    Vector vcombo;
    JPanel center;
    JPanel south;
    JPanel north;
    ListLabel btn_ok;
    ListLabel btn_cancel;
    Color bg;
    Color fg;
    Color cg;
    boolean confirmed;
}
