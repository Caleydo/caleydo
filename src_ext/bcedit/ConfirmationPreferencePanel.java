// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCPreferenceDialog.java

package bcedit;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.TitledBorder;

// Referenced classes of package bcedit:
//            BCSettings, FPanel, BCDefaults, BCImages

class ConfirmationPreferencePanel extends JPanel
    implements ActionListener
{

    public ConfirmationPreferencePanel()
    {
        settings = new BCSettings();
        int cbStrIdx = 0;
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.fill = 2;
        c.weightx = 1.0D;
        c.weighty = 0.0D;
        c.gridx = 0;
        c.gridy = 0;
        setLayout(layout);
        setBackground(BCDefaults.bgPanelColor);
        for(int idx = 0; idx < cbStrText.length; idx++)
        {
            c.gridx = gridxy[idx].x;
            c.gridy = gridxy[idx].y;
            JCheckBox jcb = prepareCheckBox(null, cbStrText[idx], cbStrAction[idx], settings.getBooleanValue(cbStrAction[idx], true), true);
            if(idx < 2)
                jcb.setEnabled(false);
            if(useFrames)
            {
                FPanel f = new FPanel();
                f.setOpaque(false);
                f.setCenterObject(jcb);
                layout.setConstraints(f, c);
                add(f);
            } else
            {
                layout.setConstraints(jcb, c);
                add(jcb);
            }
        }

        c.gridy++;
        c.gridx = 0;
        c.weighty = 1.0D;
        c.gridwidth = 2;
        JPanel jp = new JPanel();
        jp.setOpaque(false);
        layout.setConstraints(jp, c);
        add(jp);
    }

    private TitledBorder createTitledBorder(String Title)
    {
        TitledBorder b = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BCDefaults.fgColor), Title);
        b.setTitleColor(BCDefaults.fgColor);
        return b;
    }

    private JCheckBox prepareCheckBox(JCheckBox jc, String text, String name, boolean bSelected, boolean bEnabled)
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
        jc.setSelected(bSelected);
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
            for(int idx = 0; idx < cbStrAction.length; idx++)
                if(s.equals(cbStrAction[idx]))
                    settings.store(cbStrAction[idx], Boolean.toString(bFlag));

        }
    }

    BCSettings settings;
    static boolean useFrames = false;
    String cbStrText[] = {
        "load preset from controller", "save preset to controller", "load preset from computer", "save preset to computer"
    };
    String cbStrAction[] = {
        "confirmControllerLoad", "confirmControllerSave", "confirmComputerLoad", "confirmComputerSave"
    };
    Point gridxy[] = {
        new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2)
    };

}
