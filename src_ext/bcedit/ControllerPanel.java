// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ControllerPanel.java

package bcedit;

import bcedit.BCL.BCDevice;
import bcedit.BCL.BCPreset;
import bcedit.BCL.MsgBuilder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.PrintStream;
import java.util.Vector;
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

// Referenced classes of package bcedit:
//            CPTab, BCActionListener, GPanel, ListLabel, 
//            BCInternalNotify, MidiDeviceHolder, WaitCursorAni, CbListCellRenderer, 
//            GPanelAnimation, BCSettings, BCDefaults, BCImages

public class ControllerPanel extends JPanel
    implements ListSelectionListener, MouseListener
{

    public ControllerPanel()
    {
        tab = new CPTab[1];
        Listener = new BCActionListener();
        activeController = new Vector();
        enableStates = new Vector();
        BControl = null;
        loadingDevId = 0;
        loadingDevice = null;
        loadingBControl = null;
        mousePressed = 0;
        bcdevice = null;
        deviceListIndex = -1;
        presetListIndex = -1;
        elementListIndex = -1;
        bElementAutoLoad = false;
        animationCounter = 0;
        ctrl_anim = null;
        setBackground(BCDefaults.bgPanelColor);
        setOpaque(true);
        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        headline = new GPanel(BCImages.getImage("Text_HARDWARE_unit.png"));
        headline.bAllowResize = false;
        JPanel pselect = new JPanel();
        ListLabel scanBtn = new ListLabel("Scan");
        scanBtn.setBControlButtonDefault2();
        scanBtn.addMouseListener(this);
        scanBtn.setToolTipText("Press here to scan MIDI to find suitable devices.");
        pselect.add(scanBtn);
        pselect.setOpaque(true);
        pselect.setBackground(new Color(0x4a7597));
        pselect.setBorder(BorderFactory.createEtchedBorder(0));
        Vector stringLabel = new Vector();
        stringLabel.add("Devices");
        stringLabel.add("Presets");
        stringLabel.add("Elements");
        tab[0] = new CPTab(stringLabel, pselect, false);
        tab[0].sInfo = "ControllerPanel";
        tab[0].list[0].setSelectionMode(0);
        tab[0].lLabel[2].setLayout(new BorderLayout(0, 0));
        ListLabel ll1 = new ListLabel("Auto");
        ll1.setOpaque(false);
        ll1.setBControlButtonDefault();
        ll1.setAutoRepaint(true);
        ll1.deselectItem();
        ll1.setName("ElementAutoLoad");
        ll1.addMouseListener(this);
        ll1.setToolTipText("Enable to load elements automatically when changing presets (very slow)");
        tab[0].lLabel[2].add("East", ll1);
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
        c.weighty = 1.0D;
        c.gridy++;
        gb.setConstraints(tab[0], c);
        add(tab[0]);
        Vector v = new Vector();
        addController(v);
        Listener.setControllerPanel(this);
        for(int i = 0; i < 3; i++)
        {
            tab[0].list[i].addListSelectionListener(this);
            tab[0].list[i].addMouseListener(this);
        }

    }

    public void doLayout()
    {
        super.doLayout();
        BCInternalNotify notify = new BCInternalNotify(this, "CONTROLER:doLayout", 100L);
        notify.start();
    }

    public Rectangle getBoundsOfList(int i)
    {
        if(i >= tab[0].list.length)
            return null;
        Component c = tab[0].scrollpane[i].getParent();
        int y = 0;
        for(; c != this; c = c.getParent())
            y += c.getY();

        y += getY();
        Rectangle rc = tab[0].scrollpane[i].getParent().getBounds();
        rc.y = y;
        return rc;
    }

    public Point getPositionOfList(int i)
    {
        if(i >= tab[0].list.length)
            return null;
        Component c = tab[0].scrollpane[i];
        int x = 0;
        int y = 0;
        for(; c != this; c = c.getParent())
        {
            x += c.getX();
            y += c.getY();
        }

        return new Point(x, y);
    }

    public int[] getPresetListIndeces()
    {
        return tab[0].list[0].getSelectedIndices();
    }

    public int[] getPresetListIndices()
    {
        return tab[0].list[1].getSelectedIndices();
    }

    public int getPresetListIndex()
    {
        return presetListIndex;
    }

    public boolean isVirtual()
    {
        if(deviceListIndex < 0)
            return true;
        else
            return tab[0].list[0].getSelectedValue().toString().startsWith("Virtual");
    }

    public void setPresetList(Vector v)
    {
        presetListIndex = -1;
        tab[0].list[1].setListData(v);
        tab[0].list[1].setEnabled(true);
        tab[0].lLabel[1].setEnabled(true);
        getParent().validate();
    }

    public String getSelectedCtrlString()
    {
        if(tab[0].list[0].getSelectedValue() == null)
            return null;
        else
            return tab[0].list[0].getSelectedValue().toString();
    }

    public MidiDeviceHolder getSelectedCtrl()
    {
    	System.out.println("MidiDeviceHolder MichaelKalkusch");
    	
        Object o = tab[0].list[0].getSelectedValue();
        if(o == null)
            return null;
        if(o instanceof MidiDeviceHolder)
            return (MidiDeviceHolder)o;
        else
            return null;
    }

    private void saveState(Component c)
    {
        enableStates.add(new Boolean(c.isEnabled()));
        c.setEnabled(false);
        if(c instanceof JComponent)
        {
            for(int i = 0; i < ((JComponent)c).getComponentCount(); i++)
                saveState(((JComponent)c).getComponent(i));

        }
    }

    public boolean saveState()
    {
        if(enableStates.size() != 0)
            return false;
        for(int i = getComponentCount(); --i >= 0;)
            saveState(getComponent(i));

        WaitCursorAni wca = new WaitCursorAni(this);
        enableStates.add(wca);
        repaint();
        wca.start();
        return true;
    }

    private void restoreState(Component c)
    {
        c.setEnabled(((Boolean)enableStates.remove(0)).booleanValue());
        if(c instanceof JComponent)
        {
            for(int i = 0; i < ((JComponent)c).getComponentCount(); i++)
                restoreState(((JComponent)c).getComponent(i));

        }
    }

    public boolean restoreState()
    {
        if(enableStates.size() == 0)
            return false;
        for(int i = getComponentCount(); --i >= 0;)
            restoreState(getComponent(i));

        ((WaitCursorAni)enableStates.remove(0)).stopit();
        if(loadingBControl != null && loadingBControl.isReady())
        {
            BControl = loadingBControl;
            Vector v1 = new Vector();
            for(int i = 1; i <= 32; i++)
                v1.add(BControl.getPreset(i).toString());

            tab[0].list[1].setListData(v1);
            tab[0].list[1].setEnabled(true);
            loadingBControl = null;
        }
        repaint();
        return true;
    }

    public boolean isPanelDisabled()
    {
        return enableStates.size() != 0;
    }

    public void resetCtrl()
    {
        Vector empty = new Vector();
        presetListIndex = -1;
        tab[0].list[1].setListData(empty);
        tab[0].list[1].setEnabled(false);
        tab[0].list[2].setListData(empty);
        tab[0].list[2].setEnabled(false);
        repaint();
    }

    public void reset()
    {
        resetCtrl();
        repaint();
    }

    public void newDevice(BCDevice bcd)
    {
        bcdevice[deviceListIndex] = bcd;
    }

    public void newPreset(BCPreset bcp)
    {
        if(bcp == null)
        {
            System.out.println("ControllerPanel.newPreset(BCPreset null???)");
            return;
        } else
        {
            bcdevice[deviceListIndex].setPreset(presetListIndex + 1, bcp);
            tab[0].list[2].setListData(bcp.getElementAsList());
            tab[0].list[2].setEnabled(true);
            return;
        }
    }

    public void addController(Vector v)
    {
        deviceListIndex = -1;
        v.insertElementAt("Virtual BCR2000", 0);
        v.insertElementAt("Virtual BCF2000", 1);
        tab[0].list[0].setListData(v);
        bcdevice = new BCDevice[v.size()];
        for(int i = 0; i < v.size(); i++)
            bcdevice[i] = new BCDevice(v.elementAt(i).toString().indexOf("BCR2000") < 0 ? ((int) (v.elementAt(i).toString().indexOf("BCF2000") < 0 ? 0 : 20)) : 21);

    }

    public void update(Graphics g)
    {
        if(!isShowing())
            return;
        if(!isOpaque())
        {
            return;
        } else
        {
            Dimension d = getSize();
            Insets i = getInsets();
            int nw = d.width - i.left - i.right;
            int nh = d.height - i.top - i.bottom;
            g.setColor(new Color(0x2f4260));
            g.fillRect(i.left, i.top, nw, nh);
            return;
        }
    }

    protected void paintComponent(Graphics g)
    {
        update(g);
    }

    public void setJListDefault(JList l)
    {
        l.setPrototypeCellValue("00:Noname1234 Erfg Wert Yxcv");
        l.setDragEnabled(true);
        l.setVisibleRowCount(4);
        l.setBackground(new Color(0x4a7597));
        l.setForeground(new Color(0xffd800));
        l.setSelectionBackground(new Color(0xffd800));
        l.setSelectionForeground(new Color(0x4a7597));
        l.setCellRenderer(new CbListCellRenderer());
    }

    public void startAnimation()
    {
        String imgageNames[] = {
            "CTRL-anim-1.png", "CTRL-anim-2.png", "CTRL-anim-3.png", "CTRL-anim-4.png", "CTRL-anim-5.png", "CTRL-anim-6.png", "CTRL-anim-7.png", "CTRL-anim-8.png", "CTRL-anim-9.png", "CTRL-anim-10.png", 
            "CTRL-anim-11.png", "CTRL-anim-12.png"
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

    private void sendPresetChanged(MidiDeviceHolder mdh, int id, int model, int presetnumber)
    {
    	System.out.println("sendPresetChanged()  MichaelKalkusch breakpoint [id="
    			+ id + "  model=" + 
    			model + "  preset#=" + presetnumber);
    	
        if(mdh == null)
            return;
        
        BCSettings settings = new BCSettings();
        javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
        MidiDevice MidiOut = null;
        String fakeFileToLoad = settings.getValue("MidiDeviceFake");
        if(fakeFileToLoad != null)
            return;
        
        for(int i = 0; i < infos.length;)
            try
            {
                MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
                if(device != null) {
                	if ( device.getMaxReceivers() == 0 ) 
                	{
                		if ( infos[i].getName().equals(mdh.getOutDeviceName() )) {
                			MidiOut = device;                	
                			break;
                		}
                	}
                }
                
            }
            catch(MidiUnavailableException e)
            {
                i++;
            }

        if(MidiOut != null)
        {
            SysexMessage sysexMessage = new SysexMessage();
            try
            {
                MidiOut.open();
                Receiver rx = MidiOut.getReceiver();
                byte mmsg[] = MsgBuilder.selectPreset(id, model, presetnumber);
                sysexMessage.setMessage(mmsg, mmsg.length);
                rx.send(sysexMessage, -1L);
            }
            catch(MidiUnavailableException e)
            {
                System.out.println("Error: " + e.getMessage());
            }
            catch(IllegalStateException llegalStateEx)
            {
                System.out.println("Error: " + llegalStateEx.getMessage());
            }
            catch(InvalidMidiDataException invDataEx)
            {
                System.out.println("Error: " + invDataEx.getMessage());
            }
            try
            {
                MidiOut.close();
            }
            catch(Exception emio) { }
        }
    }

    public void valueChanged(ListSelectionEvent e)
    {
        JList list = null;
        if(e.getValueIsAdjusting())
            return;
        if(e.getSource() instanceof JList)
        {
            list = (JList)e.getSource();
            int indices[] = list.getSelectedIndices();
            if(list.getName().equals("Devices"))
            {
                if(indices == null)
                    return;
                if(indices.length != 1)
                    return;
                if(deviceListIndex == indices[0])
                    return;
                deviceListIndex = indices[0];
                if(indices[0] < 0)
                    return;
                for(int i = 1; i < 3; i++)
                {
                    tab[0].list[i].setListData(new Vector());
                    tab[0].lLabel[i].setEnabled(false);
                    tab[0].list[i].setEnabled(false);
                }

                String s = list.getSelectedValue().toString();
                String sEdit = null;
                if(!s.startsWith("Virtual"))
                {
                    sEdit = s;
                    int idx = s.indexOf(" ");
                    float f = 1.0F;
                    if(idx > 0)
                        try
                        {
                            f = Float.parseFloat(s.substring(idx + 1, idx + 5));
                        }
                        catch(Exception ef1)
                        {
                            f = 0.0F;
                        }
                    if(f < 1.05F)
                    {
                        JOptionPane.showMessageDialog(new JFrame(), "Sorry, the firmware is too old\nPlease Update to V 1.05 or higher.", "Controller Firmware incompatible", 0);
                        return;
                    }
                } else
                {
                    sEdit = s.substring(s.indexOf(" ") + 1);
                }
                BCInternalNotify notify = null;
                notify = new BCInternalNotify(this, "CONTROLER:BuildPresetNames:" + s);
                notify.start();
                notify = new BCInternalNotify(this, "EDITOR:Edit " + sEdit);
                notify.start();
            } else
            if(list.getName().equals("Presets"))
            {
                if(indices == null)
                {
                    presetListIndex = -1;
                    return;
                }
                if(indices.length != 1)
                {
                    presetListIndex = -2;
                    return;
                }
                if(presetListIndex == indices[0])
                    return;
                presetListIndex = indices[0];
                if(indices[0] < 0)
                    return;
                if(!isVirtual()) {
                	MidiDeviceHolder midiHolder = getSelectedCtrl();
                	
                    sendPresetChanged(midiHolder, 
                    		midiHolder.getDeviceInfo()[4], 
                    		midiHolder.getDeviceInfo()[5], 
                    		indices[0]);
                }
                tab[0].list[2].setListData(new Vector());
                tab[0].lLabel[2].setEnabled(false);
                tab[0].list[2].setEnabled(false);
                if(bElementAutoLoad)
                {
                    BCInternalNotify notify = null;
                    notify = new BCInternalNotify(this, "CONTROLER:RequestPreset:" + list.getSelectedValue().toString());
                    notify.start();
                }
            } else
            if(!list.getName().equals("Elements"));
        }
    }

    public void mouseClicked(MouseEvent e)
    {
        if(e.getSource() instanceof JList)
        {
            JList l = (JList)e.getSource();
            if(l.getName() == null)
                return;
            int idx[];
            if(!l.getName().equals("Devices"))
                if(l.getName().equals("Presets"))
                {
                    if(e.getButton() == 1)
                        idx = l.getSelectedIndices();
                } else
                if(!l.getName().equals("Elements"));
        } else
        if(e.getSource() instanceof ListLabel)
        {
            BCInternalNotify notify = null;
            int i = 0;
            int cnt = 0;
            boolean b = false;
            ListLabel l = (ListLabel)e.getSource();
            if(!l.isEnabled())
                return;
            if(l.getName() != null && l.getName().equals("ElementAutoLoad"))
            {
                bElementAutoLoad = !bElementAutoLoad;
                l.setSelectItem(bElementAutoLoad);
            } else
            if(l.getText().equals("Scan"))
                notify = new BCInternalNotify(this, "CONTROLER:ScanMidi");
            if(notify != null)
                notify.start();
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

    static final boolean bSaveAllComponents = true;
    GPanel headline;
    CPTab tab[];
    BCActionListener Listener;
    Vector activeController;
    Vector enableStates;
    public BCDevice BControl;
    public int loadingDevId;
    public Vector loadingDevice;
    public BCDevice loadingBControl;
    private int mousePressed;
    private BCDevice bcdevice[];
    int deviceListIndex;
    int presetListIndex;
    int elementListIndex;
    boolean bElementAutoLoad;
    private int animationCounter;
    GPanelAnimation ctrl_anim;
}
