// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ComputerPanel.java

package bcedit;

import bcedit.BCL.BCDevice;
import bcedit.BCL.BCElement;
import bcedit.BCL.BCL;
import bcedit.BCL.BCPreset;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// Referenced classes of package bcedit:
//            GPanel, BCSettings, CPTab, BCActionListener, 
//            ListLabel, CbListCellRenderer, BCInternalNotify, BCImages, 
//            MyTabbedPanel

public class ComputerPanel extends JPanel
    implements ListSelectionListener, ActionListener, MouseListener
{

    public ComputerPanel()
    {
        sep = System.getProperty("file.separator");
        tabbedPanel = null;
        headline = new GPanel(BCImages.getImage("Text_COMPUTER_unit.png"));
        settings = new BCSettings();
        tab = new CPTab[3];
        bcDevice = null;
        bcPreset = null;
        bcElement = null;
        actionListener = null;
        actionListener = new BCActionListener();
        myCheckPath();
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        headline.bAllowResize = false;
        setBackground(new Color(0x2f4260));
        setLayout(gb);
        c.fill = 1;
        c.anchor = 10;
        c.weightx = 0.0D;
        c.weighty = 0.0D;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridy = 0;
        c.gridx = 0;
        c.insets = new Insets(0, 0, 4, 0);
        headline.xAlignment = 1;
        gb.setConstraints(headline, c);
        add(headline);
        c.gridy++;
        c.weightx = 1.0D;
        c.weighty = 1.0D;
        Vector stringLabel = new Vector();
        stringLabel.add("Devices");
        // right "presets"
        stringLabel.add("Presets");
        stringLabel.add("Elements");
        JPanel pselect = new JPanel();
        ListLabel ll1 = new ListLabel("Device Type");
        ll1.setBControlDefault();
        pselect.add(ll1);
        pselect.add(createComboBox("DeviceSelector"));
        pselect.setOpaque(true);
        pselect.setBackground(new Color(0x4a7597));
        pselect.setBorder(BorderFactory.createEtchedBorder(0));
        tab[0] = new CPTab(stringLabel, pselect, false);
        tab[0].list[0].setSelectionMode(0);
        tab[1] = null;
        tab[2] = null;
        gb.setConstraints(tab[0], c);
        add(tab[0]);
        for(int i = 0; i < 3; i++)
        {
            if(tab[i] == null)
                continue;
            if(tab[i].list != null)
            {
                for(int cnt = 0; cnt < tab[i].list.length; cnt++)
                    tab[i].list[cnt].addListSelectionListener(this);

            }
            if(tab[i].lbutton == null)
                continue;
            for(int cnt = 0; cnt < tab[i].lbutton.length; cnt++)
            {
                tab[i].lbutton[cnt].addMouseListener(this);
                tab[i].lbutton[cnt].setEnabled(false);
            }

        }

        actionListener.setComputerPanel(this);
        readDeviceDirectory();
    }

    public int getDeviceType()
    {
        int deviceType = 0;
        JComboBox jc1 = findComboBox(getActiveTab());
        if(jc1 != null)
        {
            String as1 = (String)jc1.getSelectedItem();
            try
            {
                deviceType = Integer.parseInt(as1.substring(as1.indexOf('\u0100') + 1));
            }
            catch(Exception ni1) { }
        }
        return deviceType;
    }

    public String getActiveDeviceFilename()
    {
        char bsep = '\u0100';
        String s = (String)tab[0].list[0].getSelectedValue();
        return s.substring(0, s.indexOf(bsep));
    }

    public BCDevice getActiveDevice()
    {
        return bcDevice;
    }

    public void setActivePresetIndex(int idx)
    {
        tab[0].list[1].setSelectedIndex(idx);
    }

    public int getActivePresetIndex()
    {
        return tab[0].list[1].getSelectedIndex();
    }

    public BCPreset getActivePreset()
    {
        return bcPreset;
    }

    public Rectangle getBoundsOfList(int i)
    {
        if(tab[0] == null)
        {
            System.out.println("ComputerPanel::getBoundsOfList(): tab[0] = null");
            return null;
        }
        if(tab[0].scrollpane == null)
        {
            System.out.println("ComputerPanel::getBoundsOfList(): tab[0].scrollpane = null");
            return null;
        }
        if(i >= tab[0].scrollpane.length)
        {
            System.out.println("ComputerPanel::getBoundsOfList(): i >= tab[0].scrollpane.length");
            return null;
        }
        Component c = tab[0].scrollpane[i].getParent();
        int y = 0;
        for(; c != this; c = c.getParent())
            y += c.getY();

        y += getY();
        Rectangle rc = tab[0].scrollpane[i].getParent().getBounds();
        rc.y = y;
        return rc;
    }

    private JComboBox createComboBox(String actionString)
    {
        char bsep = '\u0100';
        String strDeviceName[] = {
            "BCR2000" + bsep + Integer.toString(21), "BCF2000" + bsep + Integer.toString(20)
        };
        JComboBox cb1 = new JComboBox(strDeviceName);
        if(actionString != null)
        {
            cb1.setActionCommand(actionString);
            cb1.addActionListener(this);
        }
        cb1.setBackground(new Color(0x4a7597));
        cb1.setForeground(new Color(0xffd800));
        cb1.setRenderer(new CbListCellRenderer());
        return cb1;
    }

    private boolean checkAndCreateDir(String s1)
    {
        try
        {
            File f1 = new File(s1);
            if(!f1.exists())
                f1.mkdir();
        }
        catch(Exception e)
        {
            System.out.println("Could not create Directory " + s1);
            System.out.println("The System reports:" + e.getMessage());
            return false;
        }
        return true;
    }

    private int myCheckPath()
    {
        int retval = 0;
        startpath = settings.getValue("storage location");
        if(startpath == null)
            startpath = System.getProperty("user.home") + sep + "BCEdit";
        return !checkAndCreateDir(startpath) || !checkAndCreateDir(startpath + sep + "Devices") || !checkAndCreateDir(startpath + sep + "Presets") || !checkAndCreateDir(startpath + sep + "Elements") ? 1 : 0;
    }

    private String scanForElementInLib(BCElement bce)
    {
    	try {
        File fe;
        long crc = bce.getCRC();
        String startpath = settings.getValue("storage location") + sep + "Elements" + sep + Long.toString(crc) + ".bce";
        fe = new File(startpath);
        
//        if(!fe.exists())
//            break MISSING_BLOCK_LABEL_185;
        
        BufferedReader in;
        StringTokenizer tok;
        in = new BufferedReader(new FileReader(fe));        
        tok = new StringTokenizer(in.readLine(), ";");
        String n = null;
        String s1;
        do
        {
//            if(!tok.hasMoreElements())
//                break MISSING_BLOCK_LABEL_175;
        	
            s1 = tok.nextToken();
        } while(!s1.startsWith(".name "));
        
        int spos = s1.indexOf(' ');
        s1 = s1.substring(spos + 1).trim();
        in.close();        
        return s1;
        
    	} catch ( Exception e ) {
//        break MISSING_BLOCK_LABEL_185;
//        Exception e;
//        e;
        return null;
    	}
    }

    public void disableLoadElements()
    {
        tab[0].lbutton[4].setEnabled(false);
        tab[1].lbutton[2].setEnabled(false);
        tab[2].lbutton[0].setEnabled(false);
    }

    public void enableLoadElements()
    {
        tab[0].lbutton[4].setEnabled(true);
        tab[1].lbutton[2].setEnabled(true);
        tab[2].lbutton[0].setEnabled(true);
    }

    public void enableLoadButtons(boolean bDevice, boolean bPreset, boolean bElement)
    {
        if(tab[0].lbutton == null)
        {
            return;
        } else
        {
            tab[0].lbutton[4].setEnabled(bElement);
            tab[1].lbutton[2].setEnabled(bElement);
            tab[2].lbutton[0].setEnabled(bElement);
            tab[1].lbutton[0].setEnabled(bPreset);
            tab[0].lbutton[2].setEnabled(bPreset);
            tab[0].lbutton[0].setEnabled(bDevice);
            return;
        }
    }

    public void readElementLibrary()
    {
        BCSettings settings = new BCSettings();
        char bsep = '\u0100';
        String sep = System.getProperty("file.separator");
        String startpath = settings.getValue("storage location") + sep + "Elements";
        File f = new File(startpath);
        String filenames[] = f.list();
        Vector v = new Vector();
        for(int i = 0; i < filenames.length; i++)
        {
            if(!filenames[i].endsWith(".bce"))
                continue;
            String fn = startpath + sep + filenames[i];
            try
            {
                BufferedReader in = new BufferedReader(new FileReader(fn));
                StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
                String n = null;
                do
                {
                    if(!tok.hasMoreElements())
                        break;
                    String s1 = tok.nextToken();
                    if(n == null && s1.charAt(0) == '$')
                        n = s1.substring(1, s1.indexOf(' '));
                    if(s1.startsWith(".name "))
                    {
                        int spos = s1.indexOf(' ');
                        if(spos >= 0)
                            v.add(s1.substring(spos + 1).trim() + " (" + n + ")" + bsep + fn);
                    }
                } while(true);
                in.close();
            }
            catch(Exception e) { }
        }

        if(tab[2].list[0] != null)
            tab[2].list[0].removeListSelectionListener(this);
        int sel = tab[2].list[0].getSelectedIndex();
        tab[2].list[0].setListData(v);
        tab[2].list[0].addListSelectionListener(this);
        tab[2].setJListDefault(tab[2].list[0]);
        tab[2].scrollpane[0].getViewport().setView(tab[2].list[0]);
        tab[2].repaint();
        if(sel < 0)
            sel = 0;
        if(tab[2].list[0].getModel().getSize() <= sel)
            sel = 0;
        tab[2].list[0].setSelectedIndex(sel);
    }

    private JComboBox findComboBox(int tabnumber)
    {
        if(tab[tabnumber].selectJPanel != null)
        {
            for(int i = 0; i < tab[tabnumber].selectJPanel.getComponentCount(); i++)
                if(tab[tabnumber].selectJPanel.getComponent(i) instanceof JComboBox)
                    return (JComboBox)tab[tabnumber].selectJPanel.getComponent(i);

        }
        return null;
    }

    public void readDeviceDirectory()
    {
        BCSettings settings = new BCSettings();
        String sep = System.getProperty("file.separator");
        String startpath = settings.getValue("storage location");
        File f = new File(startpath);
        if(!f.exists())
            return;
        startpath = startpath + sep + "Devices";
        f = new File(startpath);
        int deviceType = 0;
        JComboBox jc1 = findComboBox(0);
        if(jc1 != null)
        {
            String as1 = (String)jc1.getSelectedItem();
            try
            {
                deviceType = Integer.parseInt(as1.substring(as1.indexOf('\u0100') + 1));
            }
            catch(Exception ni1) { }
        }
        (new BCInternalNotify(this, "COMPUTER:activeDeviceType:" + Integer.toString(deviceType))).start();
        String filenames[] = f.list();
        Vector v = new Vector();
        String ext = "." + BCL.getDeviceNameFromId(deviceType) + ".bc";
        for(int i = 0; i < filenames.length;)
        {
            if(!filenames[i].endsWith(ext))
                continue;
            char bsep = '\u0100';
            String fn = startpath + sep + filenames[i];
            try
            {
                BufferedReader in;
label0:
                {
                    in = new BufferedReader(new FileReader(fn));
                    StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
                    boolean revOK = false;
                    boolean globalOK = false;
                    do
                    {
                        if(!tok.hasMoreElements())
                            break label0;
                        String s1 = tok.nextToken().trim();
                        if(s1.length() == 0)
                            break label0;
                        if(s1.startsWith("BCL"))
                        {
                            StringTokenizer t2 = new StringTokenizer(s1, ",");
                            if(!t2.nextToken().equals("BCL") || Integer.parseInt(t2.nextToken()) != 2 || !t2.nextToken().equals("DEVICE"))
                                break label0;
                            continue;
                        }
                        if(s1.startsWith("$deviceType "))
                        {
                            int check = 0;
                            try
                            {
                                check = Integer.parseInt(s1.substring(s1.indexOf(' ') + 1));
                            }
                            catch(NumberFormatException nfe) { }
                            if(check != deviceType)
                                break label0;
                            revOK = true;
                            continue;
                        }
                        if(s1.startsWith("$rev "))
                        {
                            String tmp = s1.substring(s1.indexOf(" ") + 1).trim();
                            int check = 0;
                            if(tmp.equals("R1"))
                                check = 21;
                            else
                            if(tmp.equals("F1"))
                                check = 20;
                            if(check != deviceType)
                                break label0;
                            revOK = true;
                            continue;
                        }
                        if(!revOK)
                            break label0;
                        if(!s1.startsWith("$global"))
                            break;
                        globalOK = true;
                    } while(true);
                    if(globalOK)
                        v.add(filenames[i].substring(0, filenames[i].length() - ext.length()) + bsep + fn);
                }
                in.close();
                continue;
            }
            catch(Exception e)
            {
                i++;
            }
        }

        CPTab c = tab[0];
        JList cList = c.list[0];
        if(cList != null)
            cList.removeListSelectionListener(this);
        int sel = cList.getSelectedIndex();
        cList.setListData(v);
        cList.addListSelectionListener(this);
        c.setJListDefault(cList);
        c.scrollpane[0].getViewport().setView(cList);
        if(sel < 0)
            sel = 0;
        if(cList.getModel().getSize() <= sel)
            sel = 0;
        cList.setSelectedIndex(sel);
    }

    private void generatePresetListFromDevice(int tabidx, int listidx)
    {
        Vector v = new Vector();
        if(bcDevice != null)
        {
            for(int i = 1; i < 33; i++)
                v.add(bcDevice.getPreset(i));

        }
        tab[tabidx].list[listidx].setListData(v);
        if(v.size() > 0)
            tab[tabidx].list[listidx].setSelectedIndex(0);
        else
            tab[tabidx].list[listidx].setSelectedIndex(-1);
    }

    private void generateElementListFromPreset(int tabidx, int listidx)
    {
        Vector v = new Vector();
        if(bcPreset != null)
        {
            int i = 0;
            do
            {
                BCElement bce = bcPreset.getElement(i++, 0);
                if(bce == null)
                    break;
                v.add(bce);
            } while(true);
            i = 0;
            do
            {
                BCElement bce = bcPreset.getElement(i++, 1);
                if(bce == null)
                    break;
                v.add(bce);
            } while(true);
            i = 0;
            do
            {
                BCElement bce = bcPreset.getElement(i++, 2);
                if(bce == null)
                    break;
                v.add(bce);
            } while(true);
        }
        tab[tabidx].list[listidx].setListData(v);
        if(v.size() > 0)
            tab[tabidx].list[listidx].setSelectedIndex(0);
        else
            tab[tabidx].list[listidx].setSelectedIndex(-1);
    }

    public void readPresetDirectory()
    {
        BCSettings settings = new BCSettings();
        String sep = System.getProperty("file.separator");
        String startpath = settings.getValue("storage location") + sep + "Presets";
        File f = new File(startpath);
        int deviceType = 0;
        JComboBox jc1 = findComboBox(1);
        if(jc1 != null)
        {
            String as1 = (String)jc1.getSelectedItem();
            try
            {
                deviceType = Integer.parseInt(as1.substring(as1.indexOf('\u0100') + 1));
            }
            catch(Exception ni1) { }
        }
        String filenames[] = f.list();
        Vector v = new Vector();
        for(int i = 0; i < filenames.length; i++)
        {
            if(!filenames[i].endsWith(".bcp"))
                continue;
            char bsep = '\u0100';
            String fn = startpath + sep + filenames[i];
            try
            {
                BufferedReader in = new BufferedReader(new FileReader(fn));
                StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
                do
                {
                    if(!tok.hasMoreElements())
                        break;
                    String s1 = tok.nextToken();
                    if(s1.startsWith("$deviceType "))
                    {
                        int check = 0;
                        try
                        {
                            check = Integer.parseInt(s1.substring(s1.indexOf(' ') + 1));
                        }
                        catch(NumberFormatException nfe) { }
                        if(check != deviceType)
                            break;
                    } else
                    if(s1.startsWith(".name "))
                    {
                        int spos = s1.indexOf('\'');
                        int epos = s1.substring(spos + 1).indexOf('\'');
                        if(spos >= 0)
                            v.add(s1.substring(spos + 1, spos + epos).trim() + bsep + fn);
                    }
                } while(true);
                in.close();
            }
            catch(Exception e) { }
        }

        if(tab[1].list[0] != null)
            tab[1].list[0].removeListSelectionListener(this);
        int sel = tab[1].list[0].getSelectedIndex();
        tab[1].list[0].setListData(v);
        tab[1].list[0].addListSelectionListener(this);
        tab[1].setJListDefault(tab[1].list[0]);
        tab[1].scrollpane[0].getViewport().setView(tab[1].list[0]);
        if(sel < 0)
            sel = 0;
        if(tab[1].list[0].getModel().getSize() <= sel)
            sel = 0;
        tab[1].list[0].setSelectedIndex(sel);
    }

    public BCPreset loadPresetFromLibrary()
    {
        if(tab[1].list[0].getSelectedIndex() < 0)
            return null;
        if(tab[1].list[0].getSelectedValue() == null)
            return null;
        int cnt = 0;
        char bsep = '\u0100';
        BCPreset bcp = null;
        String script[] = null;
        String s = (String)tab[1].list[0].getSelectedValue();
        s = s.substring(s.indexOf(bsep) + 1);
        File f = new File(s);
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(f));
            StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
            script = new String[tok.countTokens()];
            while(tok.hasMoreElements()) 
                script[cnt++] = tok.nextToken();
            in.close();
        }
        catch(Exception fe1)
        {
            script = null;
        }
        if(script != null)
            bcp = BCL.initPresetFromScript(script);
        return bcp;
    }

    public BCElement loadElementFromPreset()
    {
        BCElement bce = null;
        String s = (String)tab[1].list[1].getSelectedValue();
        int type = -1;
        int num = 0;
        if(s.startsWith("Enc"))
            type = 0;
        else
        if(s.startsWith("Btn"))
            type = 1;
        else
        if(s.startsWith("Fdr"))
            type = 2;
        try
        {
            num = Integer.parseInt(s.substring(s.indexOf(' ') + 1, s.indexOf(' ') + 3));
        }
        catch(Exception ne1)
        {
            num = -1;
        }
        if(num > 0)
            bce = bcPreset.getElement(num - 1, type);
        return bce;
    }

    public BCElement loadElementFromLibrary()
    {
        if(tab[2].list[0].getSelectedIndex() < 0)
            return null;
        if(tab[2].list[0].getSelectedValue() == null)
            return null;
        int cnt = 0;
        char bsep = '\u0100';
        BCElement bce = null;
        String script[] = null;
        String s = (String)tab[2].list[0].getSelectedValue();
        s = s.substring(s.indexOf(bsep) + 1);
        File f = new File(s);
        try
        {
            BufferedReader in = new BufferedReader(new FileReader(f));
            StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
            script = new String[tok.countTokens()];
            while(tok.hasMoreElements()) 
                script[cnt++] = tok.nextToken();
            in.close();
        }
        catch(Exception fe1)
        {
            script = null;
        }
        if(script != null)
            bce = BCL.initElementFromScript(script);
        return bce;
    }

    public int getActiveTab()
    {
        if(tabbedPanel == null)
            return 0;
        else
            return tabbedPanel.getActiveTab();
    }

    private void clearAllLists()
    {
        Vector v = new Vector();
        for(int tabCtrl = 0; tabCtrl < tab.length; tabCtrl++)
        {
            if(tab[tabCtrl] == null)
                continue;
            for(int listCtrl = 0; listCtrl < tab[tabCtrl].list.length; listCtrl++)
                tab[tabCtrl].list[listCtrl].setListData(v);

        }

    }

    public synchronized void valueChanged(ListSelectionEvent e)
    {
        if(e.getValueIsAdjusting())
            return;
        int tabCtrl = 0;
        int listCtrl = 0;
        int num = -1;
        char bsep = '\u0100';
        tabCtrl = 0;
        do
        {
            if(tabCtrl >= tab.length)
                break;
            if(tab[tabCtrl] != null)
            {
                listCtrl = 0;
                do
                {
                    if(listCtrl >= tab[tabCtrl].list.length)
                        break;
                    if(e.getSource() == tab[tabCtrl].list[listCtrl])
                    {
                        num = (tabCtrl << 8) + listCtrl;
                        break;
                    }
                    listCtrl++;
                } while(true);
            }
            if(num >= 0)
                break;
            tabCtrl++;
        } while(true);
        if(num >= 0)
            switch(num)
            {
            case 2: // '\002'
            default:
                break;

            case 512: 
            {
                bcElement = loadElementFromLibrary();
                break;
            }

            case 256: 
            {
                bcElement = null;
                if((bcPreset = loadPresetFromLibrary()) == null)
                    break;
                int sel = tab[tabCtrl].list[listCtrl + 1].getSelectedIndex();
                tab[tabCtrl].list[listCtrl + 1].setListData(bcPreset.getElementAsList(settings.getValue("storage location") + sep + "Elements", sep));
                if(sel < 0)
                    sel = 0;
                if(tab[tabCtrl].list[listCtrl + 1].getModel().getSize() <= sel)
                    sel = 0;
                tab[tabCtrl].list[listCtrl + 1].setSelectedIndex(sel);
                break;
            }

            case 257: 
            {
                String s = (String)tab[tabCtrl].list[listCtrl].getSelectedValue();
                int type = -1;
                if(s.startsWith("Enc"))
                    type = 0;
                else
                if(s.startsWith("Btn"))
                    type = 1;
                else
                if(s.startsWith("Fdr"))
                    type = 2;
                try
                {
                    num = Integer.parseInt(s.substring(s.indexOf(' ') + 1, s.indexOf(' ') + 3));
                }
                catch(Exception ne1)
                {
                    num = -1;
                }
                if(num > 0)
                    bcElement = bcPreset.getElement(num - 1, type);
                break;
            }

            case 0: // '\0'
            {
                if(tab[tabCtrl].list[listCtrl].getModel().getSize() <= 0)
                    return;
                String s = (String)tab[tabCtrl].list[listCtrl].getSelectedValue();
                if(s == null)
                    return;
                String fname = s.substring(s.indexOf(bsep) + 1);
                bcDevice = BCL.initDeviceFromFile(fname);
                generatePresetListFromDevice(tabCtrl, listCtrl + 1);
                break;
            }

            case 1: // '\001'
            {
                if(tab[tabCtrl].list[listCtrl].getModel().getSize() <= 0)
                    return;
                if(tab[tabCtrl].list[listCtrl].getSelectedIndex() < 0)
                    return;
                bcPreset = (BCPreset)tab[tabCtrl].list[listCtrl].getSelectedValue();
                generateElementListFromPreset(tabCtrl, listCtrl + 1);
                break;
            }
            }
    }

    public void actionPerformed(ActionEvent e)
    {
        String cmd = e.getActionCommand();
        if(cmd.equals("DeviceSelector"))
        {
            bcDevice = null;
            bcPreset = null;
            clearAllLists();
            readDeviceDirectory();
        } else
        if(cmd.equals("PresetSelector"))
            readPresetDirectory();
    }

    public void mouseClicked(MouseEvent e)
    {
        if(e.getSource() instanceof ListLabel)
        {
            BCInternalNotify notify = null;
            int i = 0;
            int cnt = 0;
            boolean b = false;
            ListLabel l = (ListLabel)e.getSource();
            if(!l.isEnabled())
                return;
            i = 0;
            do
            {
                if(i >= 3)
                    break;
                cnt = 0;
                do
                {
                    if(cnt >= tab[i].lbutton.length)
                        break;
                    if(l == tab[i].lbutton[cnt])
                    {
                        b = true;
                        break;
                    }
                    cnt++;
                } while(true);
                if(b)
                    break;
                i++;
            } while(true);
            if(b)
            {
                switch(i << 8 + cnt)
                {
                case 512: 
                    notify = new BCInternalNotify(this, "loadSingleElementFromLibrary");
                    break;

                case 513: 
                    notify = new BCInternalNotify(this, "deleteSingleElementFromLibrary");
                    break;

                case 256: 
                    notify = new BCInternalNotify(this, "loadSinglePresetFromPreset");
                    break;

                case 258: 
                    notify = new BCInternalNotify(this, "loadSingleElementFromPreset");
                    break;
                }
                if(notify != null)
                    notify.start();
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

    public static final int DEVICE_LIBRARY = 0;
    public static final int PRESET_LIBRARY = 1;
    public static final int ELEMEMT_LIBRARY = 2;
    private String startpath;
    private String sep;
    private MyTabbedPanel tabbedPanel;
    private GPanel headline;
    private BCSettings settings;
    private CPTab tab[];
    private BCDevice bcDevice;
    private BCPreset bcPreset;
    private BCElement bcElement;
    private BCActionListener actionListener;
}
