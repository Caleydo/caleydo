// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import bcedit.BCL.BCDevice;
import bcedit.BCL.BCElement;
import bcedit.BCL.BCL;
import bcedit.BCL.BCPreset;
import java.awt.*;
import java.io.*;
import java.util.StringTokenizer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

// Referenced classes of package bcedit:
//            MyTabbedPanel, GrSelectDevice, GrPreset, GrEditNormal, 
//            GPanel, BCSettings, BCActionListener, ListLabel, 
//            GPanelAnimation, BCImages, ElementEditor, BCDefaults

public class EditorPanel extends JPanel
{

    public EditorPanel()
    {
        tabbedPanel = new MyTabbedPanel("Editor");
        newDevicePanel = new GrSelectDevice(false);
        presetScroll = new JScrollPane();
        presetPanel = new GrPreset(this);
        graphicPanel = new GrEditNormal(this);
        bcr2000 = new BCDevice(21);
        bcf2000 = new BCDevice(20);
        activeModel = 0;
        activeDevice = null;
        activePreset = -1;
        activeElement = 0;
        headline = new GPanel(BCImages.getImage("Text_EDITOR_unit.png"));
        animationCounter = 0;
        settings = new BCSettings();
        ctrl_anim = null;
        bAutoSend = false;
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        headline.bAllowResize = false;
        setLayout(gb);
        c.fill = 1;
        c.anchor = 10;
        c.weightx = 0.0D;
        c.weighty = 0.0D;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(0, 4, 0, 6);
        headline.xAlignment = 1;
        gb.setConstraints(headline, c);
        add(headline);
        c.gridy++;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        c.insets = new Insets(0, 4, 6, 6);
        gb.setConstraints(tabbedPanel, c);
        add(tabbedPanel);
        tabbedPanel.addCard("Editor", newDevicePanel);
        presetScroll.getViewport().setView(presetPanel);
        presetScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        BCActionListener x = new BCActionListener();
        x.setEditorPanel(this);
        tabbedPanel.showCard(0, false);
    }

    public GrPreset getPresetPanel()
    {
        return presetPanel;
    }

    public void enableAutoSendButton(boolean bEnable)
    {
        ListLabel ll1 = null;
        if(graphicPanel.disp1.getComponent(0) instanceof ListLabel)
        {
            ll1 = (ListLabel)graphicPanel.disp1.getComponent(0);
            ll1.setEnabled(bEnable);
            if(!ll1.isEnabled() && ll1.isHighlighted())
                ll1.deselectItem();
        }
        if(presetPanel.hPreset.getComponent(0) instanceof ListLabel)
        {
            ll1 = (ListLabel)presetPanel.hPreset.getComponent(0);
            ll1.setEnabled(bEnable);
            if(!ll1.isEnabled() && ll1.isHighlighted())
                ll1.deselectItem();
        }
    }

    public void setAutoSend(boolean flag)
    {
        bAutoSend = flag;
        ((ListLabel)graphicPanel.disp1.getComponent(0)).setSelectItem(flag);
        ((ListLabel)presetPanel.hPreset.getComponent(0)).setSelectItem(flag);
    }

    public boolean isAutoSendActive()
    {
        return bAutoSend;
    }

    public void newElement(BCElement bce)
    {
        int atype = activeElement >> 8 & 0xff;
        int anum = activeElement & 0xff;
        bce.id = anum + 1;
        if(atype != bce.ctrlType)
            return;
        BCPreset bcp = getactivePreset();
        if(bcp != null)
            bcp.setElement(bce);
        graphicPanel.getElementEditor().evaluate1(false);
    }

    public void newPresetAt(BCPreset bcp, int idx, boolean bDisplay)
    {
        getDevice().setPreset(idx, bcp);
        if(!bDisplay)
        {
            return;
        } else
        {
            presetPanel.deviceChanged(null);
            graphicPanel.disp1.setText(presetPanel.getSelectedValue().toString());
            graphicPanel.disp1.repaint();
            graphicPanel.getElementEditor().reinit(0);
            return;
        }
    }

    public void newPreset(BCPreset bcp)
    {
        if(bcp != null)
        {
            getDevice().setPreset(activePreset + 1, bcp);
            presetPanel.deviceChanged(null);
            graphicPanel.disp1.setText(presetPanel.getSelectedValue().toString());
            graphicPanel.disp1.repaint();
            graphicPanel.getElementEditor().reinit(0);
        }
    }

    public boolean newDevice(BCDevice nDevice)
    {
        switch(nDevice.getModelID())
        {
        case 20: // '\024'
            bcf2000 = nDevice;
            break;

        case 21: // '\025'
            bcr2000 = nDevice;
            break;

        default:
            return false;
        }
        setEditorModel(nDevice.getModelID());
        activeDevice = nDevice;
        activeElement = 0;
        presetPanel.deviceChanged(activeDevice);
        return true;
    }

    public void importPreset()
    {
        presetPanel.importPreset();
    }

    public void exportPreset()
    {
        presetPanel.exportPreset();
    }

    public boolean setEditorModel(int model_Id)
    {
        switch(model_Id)
        {
        case 20: // '\024'
        case 21: // '\025'
            if(activeModel != model_Id)
            {
                activePreset = 0;
                activeElement = 0;
                activeModel = model_Id;
                activeDevice = model_Id != 20 ? bcr2000 : bcf2000;
                if(!tabbedPanel.isCardPresent("Presets"))
                    tabbedPanel.addCard("Presets", presetScroll);
                if(!tabbedPanel.isCardPresent("Graphical Editor"))
                    tabbedPanel.addCard("Graphical Editor", graphicPanel);
                tabbedPanel.setContainer("Presets");
                presetPanel.deviceChanged(activeDevice);
                graphicPanel.disp1.setText(presetPanel.getSelectedValue().toString());
                graphicPanel.disp1.repaint();
                graphicPanel.getElementEditor().reinit(model_Id);
                graphicPanel.reinit(model_Id);
                graphicPanel.repaint();
                presetPanel.setHeadLineText(BCL.getDeviceNameFromId(model_Id));
            }
            return true;
        }
        return false;
    }

    public BCDevice getDevice()
    {
        return activeDevice;
    }

    public int getPresetIndex()
    {
        return activePreset;
    }

    public synchronized void setPresetIndex(int newIndex)
    {
        if(activePreset >= 0 && newIndex < 0)
        {
            return;
        } else
        {
            activePreset = newIndex;
            graphicPanel.disp1.setText(presetPanel.getSelectedValue().toString());
            graphicPanel.disp1.repaint();
            graphicPanel.getElementEditor().reinit(0);
            return;
        }
    }

    public BCPreset getPreset(int idx)
    {
        if(idx > 0 && idx < 33)
            return activeDevice.getPreset(idx);
        else
            return null;
    }

    public BCPreset getactivePreset()
    {
        if(activeDevice != null)
        {
            return activeDevice.getPreset(activePreset + 1);
        } else
        {
            System.out.println("getactivePreset returns null");
            return null;
        }
    }

    public String searchElementName()
    {
        int aElementType = activeElement >> 8 & 0xff;
        int aElement = activeElement & 0xff;
        BCElement bce = getactivePreset().getElement(aElement, aElementType);
        long crc = bce.getCRC();
        bce.name = "";
        String sep = System.getProperty("file.separator");
        String filename = settings.getValue("storage location") + sep + "Elements" + sep + Long.toString(crc) + ".bce";
        File fe = new File(filename);
        if(fe.exists())
            try
            {
                BufferedReader in = new BufferedReader(new FileReader(filename));
                String s = in.readLine();
                StringTokenizer tok = new StringTokenizer(s, ";");
                for(int i = 0; i < tok.countTokens(); i++)
                {
                    String s1 = tok.nextToken();
                    if(!s1.startsWith(".name "))
                        continue;
                    int spos = s1.indexOf(' ');
                    if(spos >= 0)
                        bce.name = s1.substring(spos + 1);
                }

                in.close();
            }
            catch(Exception e) { }
        return bce.name;
    }

    public void setActiveElementNumber(int ne)
    {
        activeElement = ne;
    }

    public int getActiveElementNumber()
    {
        return activeElement;
    }

    public int getDeviceType()
    {
        return activeModel;
    }

    public int getActiveTab()
    {
        return tabbedPanel.getActiveTab();
    }

    public void activateTab(String desc)
    {
        tabbedPanel.setContainer(desc);
    }

    public void activateTab(int i)
    {
        tabbedPanel.setContainer(i);
    }

    public void startAnimation()
    {
        String imgageNames[] = {
            "EDIT-anim-1.png", "EDIT-anim-2.png", "EDIT-anim-3.png", "EDIT-anim-4.png", "EDIT-anim-5.png", "EDIT-anim-6.png", "EDIT-anim-7.png", "EDIT-anim-8.png", "EDIT-anim-9.png", "EDIT-anim-10.png", 
            "EDIT-anim-11.png", "EDIT-anim-12.png"
        };
        if(++animationCounter != 1)
        {
            return;
        } else
        {
            ctrl_anim = new GPanelAnimation(imgageNames, 80L, headline);
            ctrl_anim.start();
            return;
        }
    }

    public void stopAnimation()
    {
        if(--animationCounter == 0)
        {
            ctrl_anim.stop_anim();
            while(ctrl_anim.isAlive()) ;
        }
        if(animationCounter < 0)
        {
            animationCounter = 0;
            System.out.println("ERROR:stopAnimation < 0");
        }
    }

    public void paintComponent(Graphics g)
    {
        if(!isOpaque())
            return;
        Dimension d = getSize();
        Insets i = getInsets();
        if(d.width >= 20 && d.height >= 20)
        {
            int nw = d.width - i.left - i.right;
            int nh = d.height - i.top - i.bottom;
            g.setColor(BCDefaults.bgPanelColor);
            g.fillRect(i.left, i.top, nw, nh);
        }
    }

    private MyTabbedPanel tabbedPanel;
    private GrSelectDevice newDevicePanel;
    private JScrollPane presetScroll;
    private GrPreset presetPanel;
    private GrEditNormal graphicPanel;
    private BCDevice bcr2000;
    private BCDevice bcf2000;
    private int activeModel;
    private BCDevice activeDevice;
    private int activePreset;
    private int activeElement;
    private GPanel headline;
    private int animationCounter;
    private BCSettings settings;
    private GPanelAnimation ctrl_anim;
    private boolean bAutoSend;
}
