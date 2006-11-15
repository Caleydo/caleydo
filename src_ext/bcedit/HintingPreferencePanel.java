// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCPreferenceDialog.java

package bcedit;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;

// Referenced classes of package bcedit:
//            BCSettings, BCDefaults, BCImages

class HintingPreferencePanel extends JPanel
    implements ActionListener
{

    public HintingPreferencePanel()
    {
        settings = new BCSettings();
        setLayout(new FlowLayout(0));
        setBackground(BCDefaults.bgPanelColor);
        add(prepareCheckBox(null, "element info", "showHintForElements", settings.getBooleanValue("Element hint", false)));
    }

    private TitledBorder createTitledBorder(String Title)
    {
        TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BCDefaults.fgColor), Title);
        b.setTitleColor(BCDefaults.fgColor);
        return b;
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
            if(s.equals("showHintForElements"))
            {
                settings.store("Element hint", Boolean.toString(bFlag));
                BCDefaults.showElementHint = bFlag;
            }
        }
    }

    BCSettings settings;
}
