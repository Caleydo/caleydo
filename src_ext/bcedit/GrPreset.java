// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorPanel.java

package bcedit;

import bcedit.BCL.BCDevice;
import bcedit.BCL.BCL;
import bcedit.BCL.BCPreset;
import bcedit.BCL.BCPresetHead;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// Referenced classes of package bcedit:
//            JPanelMy, BCActionListener, ListLabel, GrPreset2, 
//            CbListCellRenderer, JDlgRenamePreset, BCSettings, JDlgAssignPresetSlot, 
//            BCInternalNotify, BCDefaults, bcedit, EditorPanel

class GrPreset extends JPanelMy
    implements ListSelectionListener, ActionListener, MouseListener
{

    public GrPreset(EditorPanel ep)
    {
        super(null);
        editorPanel = null;
        Listener = new BCActionListener();
        presetListIndex = -1;
        presetInfo = null;
        fgColor = new Color(0xffffff);
        bgColor = new Color(0x5375aa);
        BControl = null;
        editorPanel = ep;
        setLayout(new BorderLayout());
        hPreset = new ListLabel("Controller Presets");
        presetScroll = new JScrollPane();
        presetList = new JList();
        JPanel p = new JPanel();
        ListLabel ll1 = new ListLabel("Auto");
        hPreset.setBControlDefault();
        hPreset.setLayout(new BorderLayout(0, 0));
        ll1.setOpaque(false);
        ll1.setBControlButtonDefault();
        ll1.setAutoRepaint(true);
        ll1.deselectItem();
        ll1.setName("ElementAutoSend");
        hPreset.add("West", ll1);
        ll1.addMouseListener(this);
        setJListDefault(presetList);
        p.setLayout(new BorderLayout());
        p.setOpaque(false);
        presetScroll.getViewport().setView(presetList);
        p.add("North", hPreset);
        p.add("Center", presetScroll);
        ListLabel pb = new ListLabel("");
        pb.setBControlDefault();
        pb.setLayout(new FlowLayout(1, 4, 0));
        ll1 = new ListLabel("Import SysEx");
        ll1.setBControlButtonDefault2();
        ll1.setName("ImportPreset");
        ll1.addMouseListener(this);
        pb.add(ll1);
        ll1 = new ListLabel("Export SysEx");
        ll1.setBControlButtonDefault2();
        ll1.setName("ExportPreset");
        ll1.addMouseListener(this);
        pb.add(ll1);
        ll1 = new ListLabel("Rename");
        ll1.setBControlButtonDefault2();
        ll1.setName("RenamePreset");
        ll1.addMouseListener(this);
        pb.add(ll1);
        ll1 = new ListLabel("Delete");
        ll1.setBControlButtonDefault2();
        ll1.setName("DeletePreset");
        ll1.addMouseListener(this);
        pb.add(ll1);
        p.add("South", pb);
        add("Center", p);
        presetInfo = new GrPreset2(this);
        add("South", presetInfo);
        presetList.addListSelectionListener(this);
        presetList.addMouseListener(this);
    }

    public Object getSelectedValue()
    {
        return presetList.getSelectedValue();
    }

    public void setFlags(int presetFlags)
    {
        presetInfo.setFlags(presetFlags);
    }

    public int getFlags(int presetFlags)
    {
        return presetInfo.getFlags(presetFlags);
    }

    void deviceChanged(BCDevice BControl)
    {
        Vector v = new Vector();
        if(BControl != null && this.BControl != BControl)
        {
            this.BControl = BControl;
            presetListIndex = 0;
        }
        for(int i = 1; i <= 32; i++)
        {
            String s2 = Integer.toString(i);
            if(s2.length() < 2)
                s2 = "0" + s2 + ": " + this.BControl.getPresetName(i);
            else
                s2 = s2 + ": " + this.BControl.getPresetName(i);
            v.add(s2);
        }

        presetList.setListData(v);
        presetList.setSelectedIndex(presetListIndex);
        presetSelected(true);
    }

    public void setJListDefault(JList l)
    {
        l.setPrototypeCellValue("00:Noname1234 Erfg Wert Yxcv");
        l.setDragEnabled(true);
        l.setVisibleRowCount(4);
        l.setBackground(BCDefaults.bgColor);
        l.setForeground(BCDefaults.fgColor);
        l.setSelectionBackground(BCDefaults.fgColor);
        l.setSelectionForeground(BCDefaults.bgColor);
        l.setCellRenderer(new CbListCellRenderer());
    }

    public void setHeadLineText(String s)
    {
        hPreset.setText(s);
    }

    public void renamePreset()
    {
        int idx = presetList.getSelectedIndex();
        JDlgRenamePreset x = new JDlgRenamePreset(presetList);
        x.setTitle("Rename Preset(s)");
        x.show();
        if(x.wasConfirmed())
        {
            JFormattedTextField textField[] = x.getTextFields();
            int list[] = presetList.getSelectedIndices();
            for(int i = 0; i < textField.length; i++)
                try
                {
                    textField[i].commitEdit();
                    BControl.setPresetName(list[i] + 1, textField[i].getValue().toString());
                }
                catch(ParseException e) { }

            deviceChanged(BControl);
            presetList.setSelectedIndex(idx);
        }
    }

    public void deletePreset()
    {
        int idx[] = presetList.getSelectedIndices();
        if(0 != JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to delete the selected preset(s)?", "delete?", 0))
            return;
        for(int i = 0; i < idx.length; i++)
        {
            BControl.setPreset(idx[i] + 1, new BCPreset(BControl.getModelID()));
            deviceChanged(BControl);
            presetList.setSelectedIndex(idx[i]);
        }

    }

    public void importPreset()
    {
        BCSettings settings = new BCSettings();
        String startpath = settings.getValue("last import location");
        if(startpath == null)
            startpath = settings.getValue("storage location");
        String sep = System.getProperty("file.separator");
        FileDialog dialog = new FileDialog(bcedit.app, "Import SysEx-Preset-File (Single/All) ", 0);
        dialog.setDirectory(startpath);
        dialog.show();
        String file = dialog.getFile();
        String path = dialog.getDirectory();
        if(path != null)
        {
            if(path.endsWith(sep))
                path = path.substring(0, path.length() - sep.length());
            settings.store("last import location", path);
        }
        boolean retval = false;
        if(file != null)
        {
            if(path != null)
            {
                if(!path.endsWith(sep))
                    path = path + sep;
                file = path + file;
                char buffer[] = new char[17];
                try
                {
                    BufferedReader in = new BufferedReader(new FileReader(file));
                    try
                    {
                        in.read(buffer, 0, 17);
                        in.close();
                        if(buffer[0] == '\360' && buffer[1] == 0 && buffer[2] == ' ' && buffer[3] == '2' && (buffer[5] == BControl.getModelID() || buffer[5] == '\177') && buffer[9] == '$' && buffer[10] == 'r' && buffer[11] == 'e' && buffer[12] == 'v' && buffer[13] == ' ' && (new String(buffer, 14, 2)).equals(BCL.getRevFromId(BControl.getModelID())))
                            retval = true;
                    }
                    catch(IOException e2)
                    {
                        System.out.print("Exception caught while reading: ");
                        System.out.print(e2.getMessage());
                    }
                }
                catch(FileNotFoundException e1)
                {
                    System.out.println("File \"" + file + "\" not found.");
                }
            }
            if(retval)
            {
                retval = false;
                BCDevice bcd = null;
                BCPreset bcp = null;
                Vector vsrc = Listener.readSysExFile(file, true);
                String dest[] = new String[vsrc.size()];
                int idx = 0;
                int countPresets = 0;
                Vector vPreset = new Vector();
                String sPreset = null;
                while(!vsrc.isEmpty()) 
                {
                    String s1 = new String((byte[])vsrc.remove(0));
                    s1 = s1.substring(9, s1.length() - 1);
                    if(sPreset != null)
                        sPreset = sPreset + s1 + ";";
                    if(s1.startsWith("$"))
                    {
                        String tmp[] = BCL.lineToToken(s1);
                        int context = BCL.GetToken(tmp[0]);
                        switch(context)
                        {
                        case 257: 
                        case 260: 
                        case 261: 
                        case 262: 
                        case 263: 
                        case 264: 
                        default:
                            break;

                        case 258: 
                            retval = true;
                            break;

                        case 259: 
                            countPresets++;
                            sPreset = dest[0] + ";" + s1 + ";";
                            break;

                        case 256: 
                        case 265: 
                            if(sPreset != null)
                            {
                                sPreset = sPreset + BCL.GetToken(256);
                                vPreset.add(BCL.initPresetFromScript(sPreset));
                                sPreset = null;
                            }
                            break;
                        }
                    }
                    dest[idx++] = s1;
                }
                JList list = new JList(vPreset);
                list.getSelectionModel().addSelectionInterval(0, list.getModel().getSize() - 1);
                JDlgAssignPresetSlot x = new JDlgAssignPresetSlot(list);
                x.setTitle("SysEx import");
                x.show();
                if(x.wasConfirmed())
                {
                    int sel[] = x.getSelection();
                    for(int i = 0; i < sel.length; i++)
                        editorPanel.newPresetAt((BCPreset)vPreset.elementAt(i), sel[i] + 1, false);

                    deviceChanged(null);
                    JOptionPane.showMessageDialog(bcedit.app, "Successfully imported " + countPresets + " presets", "SysEx Import", 1);
                }
            } else
            {
                System.out.println("File is rejected");
                JOptionPane.showMessageDialog(bcedit.app, "Sorry, the file you selected is not usable", "Wrong Filetype", 0);
            }
        }
    }

    public void exportPreset()
    {
        int sel1[] = presetList.getSelectedIndices();
        int sel2[] = null;
        boolean confirm = false;
        if(sel1.length == 0)
        {
            JOptionPane.showMessageDialog(bcedit.app, "There is no PRESET selected.", "Export what?", 0);
            return;
        }
        if(sel1.length == 1)
        {
            sel2 = new int[1];
            sel2[0] = -1;
        } else
        {
            JDlgAssignPresetSlot x = new JDlgAssignPresetSlot(presetList);
            x.setTitle("Export as SysEx...");
            x.show();
            if(x.wasConfirmed())
                sel2 = x.getSelection();
        }
        if(sel2 != null)
        {
            Vector msgHolder = new Vector();
            String pr = "";
            int seq = 0;
            for(int i = 0; i < sel1.length; i++)
            {
                BCPreset bcp = BControl.getPreset(sel1[i] + 1);
                pr = pr + bcp.getScript(sel2[i] + 1, false);
                if(!pr.endsWith(";"))
                    pr = pr + ";";
            }

            if(!pr.endsWith(";"))
                pr = pr + ";";
            pr = BCL.GetToken(257) + " " + BCL.getRevFromId(BControl.getModelID()) + ";" + pr + BCL.GetToken(256);
            char msg[];
            for(StringTokenizer tok = new StringTokenizer(pr, ";"); tok.hasMoreTokens(); msgHolder.add(msg))
            {
                char smsg[] = tok.nextToken().toCharArray();
                msg = new char[smsg.length + 10];
                msg[0] = '\360';
                msg[1] = '\0';
                msg[2] = ' ';
                msg[3] = '2';
                msg[4] = '\177';
                msg[5] = '\177';
                msg[6] = ' ';
                msg[7] = (char)(seq >> 7 & 0x7f);
                msg[8] = (char)(seq & 0x7f);
                for(int len = 0; len < smsg.length; len++)
                    msg[9 + len] = smsg[len];

                msg[9 + smsg.length] = '\367';
                seq++;
            }

            BCSettings settings = new BCSettings();
            String startpath = settings.getValue("last export location");
            if(startpath == null)
                startpath = settings.getValue("storage location");
            String sep = System.getProperty("file.separator");
            FileDialog dialog = new FileDialog(bcedit.app, "Export SysEx-Preset-File (Single/Multiple) ", 1);
            dialog.setDirectory(startpath);
            dialog.show();
            String file = dialog.getFile();
            String path = dialog.getDirectory();
            if(file != null)
            {
                if(path != null)
                {
                    if(path.endsWith(sep))
                        path = path.substring(0, path.length() - sep.length());
                    settings.store("last export location", path);
                }
                if(!file.endsWith(".syx"))
                    file = file + ".syx";
                file = path + sep + file;
                try
                {
                    BufferedWriter out = new BufferedWriter(new FileWriter(file));
                    for(int i = 0; i < msgHolder.size(); i++)
                    {
                        char msg2[] = (char[])msgHolder.elementAt(i);
                        out.write(msg2, 0, msg2.length);
                    }

                    out.close();
                }
                catch(Exception io1)
                {
                    JOptionPane.showMessageDialog(bcedit.app, io1.getMessage(), "I/O Error", 0);
                }
            }
        }
    }

    public void setPresetListIndex(int index)
    {
        presetList.setSelectedIndex(index);
        presetSelected(false);
    }

    private void presetSelected(boolean bForceLoad)
    {
        int index = presetList.getSelectedIndex();
        if(!bForceLoad && (index == presetListIndex || index < 0 && presetListIndex >= 0))
            return;
        presetListIndex = index;
        if(editorPanel != null)
            editorPanel.setPresetIndex(presetListIndex);
        if(presetInfo != null)
            presetInfo.setFlags(editorPanel.getDevice().getPreset(index + 1).getPreset().flags);
    }

    public synchronized void valueChanged(ListSelectionEvent e)
    {
        if(e.getSource() == presetList)
            presetSelected(false);
    }

    public synchronized void actionPerformed(ActionEvent e)
    {
        if(e.getSource() instanceof JButton)
        {
            if(e.getActionCommand().equals("SendPresetToHardware"))
            {
                BCInternalNotify notify = new BCInternalNotify(this, e.getActionCommand());
                notify.start();
            } else
            if(e.getActionCommand().equals("Rename Preset"))
                renamePreset();
        } else
        if(e.getSource() instanceof JCheckBox)
        {
            JCheckBox jcb = (JCheckBox)e.getSource();
            if(jcb.getName() == null)
                return;
            if(jcb.getName().equals("fkeys") || jcb.getName().equals("lock") || jcb.getName().equals("request") || jcb.getName().equals("snapshot"))
                editorPanel.getactivePreset().getPreset().flags = presetInfo.getFlags(editorPanel.getactivePreset().getPreset().flags);
        } else
        if(e.getSource() instanceof JComboBox)
        {
            JComboBox jcb = (JComboBox)e.getSource();
            if(jcb.getName() == null)
                return;
            if(jcb.getName().equals("EncoderGroups"))
                editorPanel.getactivePreset().getPreset().flags = presetInfo.getFlags(editorPanel.getactivePreset().getPreset().flags);
        }
    }

    public void mouseClicked(MouseEvent e)
    {
        if(e.getSource() instanceof ListLabel)
        {
            ListLabel ll1 = (ListLabel)e.getSource();
            if(ll1.getName().equals("RenamePreset"))
                renamePreset();
            else
            if(ll1.getName().equals("DeletePreset"))
                deletePreset();
            else
            if(ll1.getName().equals("ImportPreset"))
                importPreset();
            else
            if(ll1.getName().equals("ExportPreset"))
                exportPreset();
            else
            if(ll1.getName().equals("ElementAutoSend"))
            {
                if(!ll1.isEnabled())
                    return;
                boolean flag = !ll1.isHighlighted();
                (new BCInternalNotify(ll1, ll1.getName() + ":" + Boolean.toString(flag))).start();
            }
        } else
        if((e.getSource() instanceof JList) && e.getSource() == presetList && e.getClickCount() == 2)
        {
            int index = presetList.getSelectedIndex();
            if(index != presetListIndex)
            {
                presetListIndex = index;
                if(editorPanel != null)
                    editorPanel.setPresetIndex(presetListIndex);
                if(presetInfo != null)
                    presetInfo.setFlags(editorPanel.getDevice().getPreset(index + 1).getPreset().flags);
            }
            if(editorPanel != null)
                editorPanel.activateTab("Graphical Editor");
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

    EditorPanel editorPanel;
    BCActionListener Listener;
    ListLabel hPreset;
    JScrollPane presetScroll;
    JList presetList;
    int presetListIndex;
    GrPreset2 presetInfo;
    Color fgColor;
    Color bgColor;
    BCDevice BControl;
}
