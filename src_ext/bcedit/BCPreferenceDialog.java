// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCPreferenceDialog.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Referenced classes of package bcedit:
//            MyTabbedPanel, ListLabel, BCSettings, SizedPanel, 
//            StartUpPreferencePanel, HintingPreferencePanel, ConfirmationPreferencePanel, FPanel2, 
//            FPanel, BCDefaults, bcedit, MainFrame, 
//            BCImages

public class BCPreferenceDialog extends JDialog
    implements ItemListener, ActionListener, MouseListener, KeyListener
{

    public BCPreferenceDialog()
    {
        useStandardFrame = true;
        tabbedPanel = new MyTabbedPanel("PreferenceDialog");
        frame = new JPanel();
        south = new JPanel();
        center = new JPanel();
        btn_ok = new JButton();
        btn_cancel = new ListLabel("Cancel");
        confirmed = false;
        settings = new BCSettings();
        setTitle("B-Control Editor Preferences");
        if(useStandardFrame)
            setContentPane(frame);
        else
            setContentPane(FPanel2.createPanel(this, frame));
        frame.setLayout(new BorderLayout(0, 0));
        makeButtons();
        frame.add("North", new SizedPanel(8, 8));
        frame.add("West", new SizedPanel(8, 8));
        frame.add("East", new SizedPanel(8, 8));
        frame.add("South", south);
        frame.add("Center", tabbedPanel);
        recolor(this);
        tabbedPanel.addCard("Startup behaviour", FPanel.createPanel(new StartUpPreferencePanel()));
        tabbedPanel.addCard("Help & Hints", FPanel.createPanel(new HintingPreferencePanel()));
        tabbedPanel.addCard("Confirm dialogs", FPanel.createPanel(new ConfirmationPreferencePanel()));
        setModal(true);
        setResizable(true);
        if(!useStandardFrame)
            setUndecorated(true);
        pack();
        setCursor(BCDefaults.curDefault);
        setBackground(BCDefaults.bgColor);
        Rectangle r = bcedit.app.getBounds();
        Rectangle d = getBounds();
        if(d.height > r.height)
            d.height = r.height;
        setBounds(r.x + (r.width - d.width) / 2, r.y + (r.height - d.height) / 2, d.width, d.height);
    }

    private void makeButtons()
    {
        ImageIcon img = new ImageIcon(BCImages.getImage("button_72x25.png"));
        ImageIcon img2 = new ImageIcon(BCImages.getImage("button_pressed_72x25.png"));
        ImageIcon img3 = new ImageIcon(BCImages.getImage("button_selected_72x25.png"));
        btn_ok.setText("Close");
        btn_ok.setHorizontalTextPosition(0);
        btn_ok.setIcon(img);
        btn_ok.setPressedIcon(img2);
        btn_ok.setSelectedIcon(img3);
        btn_ok.setPreferredSize(new Dimension(img.getIconWidth(), img.getIconHeight()));
        btn_ok.setBorderPainted(false);
        btn_ok.setName("OK-Button");
        btn_ok.addMouseListener(this);
        btn_ok.addActionListener(this);
        btn_ok.setOpaque(false);
        south.add(btn_ok);
    }

    private void recolor(Container c)
    {
        Component cc[] = c.getComponents();
        for(int i = 0; i < cc.length; i++)
        {
            cc[i].setForeground(BCDefaults.fgColor);
            cc[i].setBackground(BCDefaults.bgColor);
            if(cc[i] instanceof Container)
                recolor((Container)cc[i]);
        }

    }

    public void setTitle(String nt)
    {
        if(isUndecorated())
        {
            Container c = getContentPane();
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
            JComponent c = (JComponent)e.getSource();
            boolean bFlag = false;
            if(e.getSource() instanceof JCheckBox)
                bFlag = ((JCheckBox)e.getSource()).isSelected();
            if(!c.isEnabled())
                return;
            String s = c.getName();
            if(s == null)
                return;
            if(s.equals("scanOnStart"))
                settings.store("scan MIDI on startup", Boolean.toString(bFlag));
            else
            if(s.equals("restoreSession"))
                settings.store("restore session", Boolean.toString(bFlag));
            else
            if(s.equals("showHintForElements"))
            {
                BCDefaults.showElementHint = ((JCheckBox)e.getSource()).isSelected();
                settings.store("Element hint", Boolean.toString(BCDefaults.showElementHint));
            } else
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

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    public void keyPressed(KeyEvent keyevent)
    {
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    public void keyTyped(KeyEvent e)
    {
        switch(e.getKeyCode())
        {
        case 10: // '\n'
            confirmed = true;
            hide();
            break;

        case 27: // '\033'
            confirmed = false;
            hide();
            break;
        }
    }

    boolean useStandardFrame;
    private MyTabbedPanel tabbedPanel;
    private JPanel frame;
    private JPanel south;
    private JPanel center;
    private JButton btn_ok;
    private ListLabel btn_cancel;
    public boolean confirmed;
    private BCSettings settings;
}
