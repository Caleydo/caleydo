// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

// Referenced classes of package bcedit:
//            ActionAndText, CbListCellRenderer, JCheckBox2, BCInternalNotify, 
//            BCDefaults, BCImages

class GrPreset2 extends JPanel
    implements ActionListener
{

    public GrPreset2()
    {
        jcb = new JCheckBox[4];
        jcbb = null;
        init(null);
    }

    public GrPreset2(JComponent parent)
    {
        jcb = new JCheckBox[4];
        jcbb = null;
        init(parent);
    }

    public void init(JComponent parent)
    {
        String hint[] = {
            "When off, the buttons\nSTORE, LEARN, EDIT and EXIT\nare user definable", "When the preset is locked\nthe buttons EDIT, LEARN, STORE and EXIT\nand the preset select buttons are user definable"
        };
        ActionAndText at[] = {
            new ActionAndText("fkeys", "Function Buttons"), new ActionAndText("lock", "Lock Preset")
        };
        Point gridxy[] = {
            new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)
        };
        String str_egroups[] = {
            "1", "2", "3", "4"
        };
        jcbb = new JComboBox(str_egroups);
        jcbb.setBackground(BCDefaults.bgColor);
        jcbb.setForeground(BCDefaults.fgColor);
        jcbb.setRenderer(new CbListCellRenderer());
        jcbb.setName("EncoderGroups");
        if(parent != null)
            jcbb.addActionListener((ActionListener)parent);
        jcbb.addActionListener(this);
        setOpaque(false);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.weightx = 1.0D;
        c.weighty = 0.0D;
        JPanel p1 = new JPanel();
        p1.setOpaque(false);
        p1.setLayout(layout);
        p1.setBackground(BCDefaults.bgPanelColor);
        p1.setBorder(BorderFactory.createEtchedBorder(0));
        for(int i = 0; i < at.length; i++)
        {
            c.gridx = gridxy[i].x;
            c.gridy = gridxy[i].y;
            jcb[i] = prepareCheckBox(new JCheckBox2(), at[i].getText(), at[i].getAction(), true);
            jcb[i].setToolTipText(hint[i]);
            layout.setConstraints(jcb[i], c);
            p1.add(jcb[i]);
            if(parent != null)
                jcb[i].addActionListener((ActionListener)parent);
        }

        JPanel p = new JPanel();
        p.setOpaque(false);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        JLabel _l = new JLabel("Encoder groups");
        _l.setForeground(BCDefaults.fgColor);
        p.add(_l);
        p.add(jcbb);
        layout.setConstraints(p, c);
        p1.add(p);
        add(p1);
    }

    public int getFlags(int presetFlags)
    {
        int flagsToSet[] = {
            256, 2048, 512, 1024
        };
        presetFlags &= 0xfffff0fc;
        for(int i = 0; i < 4; i++)
            if(jcb[i] != null && jcb[i].isSelected())
                presetFlags |= flagsToSet[i];

        presetFlags |= jcbb.getSelectedIndex() & 3;
        return presetFlags;
    }

    public void setFlags(int presetFlags)
    {
        boolean bFlags[] = {
            (presetFlags & 0x100) != 0, (presetFlags & 0x800) != 0, (presetFlags & 0x200) != 0, (presetFlags & 0x400) != 0
        };
        for(int i = 0; i < jcb.length; i++)
            if(jcb[i] != null)
                jcb[i].setSelected(bFlags[i]);

        jcb[0].setEnabled(!jcb[1].isSelected());
        int fl = presetFlags & 3;
        jcbb.setSelectedIndex(fl);
    }

    private JRadioButton prepareRadioButton(JRadioButton jc, String text, String name, boolean bEnabled)
    {
        ImageIcon imgIcon[] = {
            new ImageIcon(BCImages.getImage("switch_off_36x25.png")), new ImageIcon(BCImages.getImage("switch_sel_36x25.png")), new ImageIcon(BCImages.getImage("switch_on_36x25.png"))
        };
        if(jc == null)
            jc = new JRadioButton();
        jc.setOpaque(false);
        jc.setIcon(imgIcon[0]);
        jc.setPressedIcon(imgIcon[1]);
        jc.setSelectedIcon(imgIcon[2]);
        jc.setText(text);
        jc.setSelected(bEnabled);
        jc.addActionListener(this);
        jc.setName(name);
        jc.setBackground(BCDefaults.bgColor);
        jc.setForeground(BCDefaults.fgColor);
        return jc;
    }

    private JCheckBox prepareCheckBox(JCheckBox jc, String text, String name, boolean bEnabled)
    {
        ImageIcon imgIcon[] = {
            new ImageIcon(BCImages.getImage("switch_off_36x25.png")), new ImageIcon(BCImages.getImage("switch_sel_36x25.png")), new ImageIcon(BCImages.getImage("switch_on_36x25.png"))
        };
        if(jc == null)
            jc = new JCheckBox();
        jc.setOpaque(false);
        jc.setIcon(imgIcon[0]);
        jc.setPressedIcon(imgIcon[1]);
        jc.setSelectedIcon(imgIcon[2]);
        jc.setText(text);
        jc.setSelected(bEnabled);
        jc.addActionListener(this);
        jc.setName(name);
        jc.setBackground(BCDefaults.bgColor);
        jc.setForeground(BCDefaults.fgColor);
        return jc;
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
            if((e.getSource() instanceof JCheckBox) && (JCheckBox)e.getSource() == jcb[1])
                jcb[0].setEnabled(!jcb[1].isSelected());
            if(s.equals("fkeys") || s.equals("lock"))
                (new BCInternalNotify(this, "SendPresetGlobalsToHardware:" + s + ":" + Boolean.toString(bFlag))).start();
            if(s.equals("EncoderGroups"))
                (new BCInternalNotify(this, "SendPresetGlobalsToHardware:egroups:" + Integer.toString(((JComboBox)c).getSelectedIndex()))).start();
        }
    }

    JCheckBox jcb[];
    JComboBox jcbb;
}
