// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import bcedit.BCL.BCDevice;
import bcedit.BCL.BCElement;
import bcedit.BCL.BCL;
import bcedit.BCL.BCPreset;
import bcedit.BCL.BCPresetHead;
import bcedit.BCL.MsgBuilder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.sound.midi.*;
import javax.swing.*;

// Referenced classes of package bcedit:
//            BCSettings, ReadControllerFull, MidiDeviceHolder, MySysExMessage, 
//            BcSysExListener, BCInternalNotify, performSysExRequest, ListLabel, 
//            JDlgAbout, BCPreferenceDialog, ThreadSendScript, MyTabbedPanel, 
//            SaveDialog, SizedPanel, ControllerPanel, bcedit, 
//            MainFrame, BCDefaults, ComputerPanel, EditorPanel, 
//            BCImages, BcEditMenu, EditorGlobals

public class BCActionListener
    implements ActionListener, MouseListener
{

    public BCActionListener()
    {
        settings = new BCSettings();
    }

    public void doLayout()
    {
label0:
        for(int j = 0; j < 2; j++)
        {
            int i = 0;
            do
            {
                if(i >= buttonPanel[j].getComponentCount())
                    continue label0;
                Rectangle rc = controllerPanel.getBoundsOfList(i);
                rc.width = buttonPanel[j].getWidth();
                rc.x = 0;
                try
                {
                    buttonPanel[j].getComponent(i).setBounds(rc);
                }
                catch(Exception E)
                {
                    System.out.println(E.getMessage());
                }
                if(i >= 1)
                    continue label0;
                i++;
            } while(true);
        }

    }

    public Vector readSysExFile(String fname)
    {
        return readSysExFile(fname, false);
    }

    public Vector readSysExFile(String fname, boolean bExternal)
    {
        ClassLoader cl = getClass().getClassLoader();
        int pos = 0;
        int pos2 = 0;
        int idx = 0;
        String t = null;
        BufferedReader in = null;
        boolean bError = false;
        Vector v = new Vector();
        try
        {
            if(bExternal)
                in = new BufferedReader(new FileReader(fname));
            else
                in = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(fname)));
            int len = 0;
            char _b[] = new char[1024];
            do
            {
                if(!in.ready() || bError)
                    break;
                len = pos2 + in.read(_b, pos2, 1024 - pos2);
                if(len <= 0)
                    break;
                pos = pos2 = 0;
                if((_b[pos] & 0xff) != 240)
                    bError = true;
                while(pos < len) 
                    if((_b[pos] & 0xff) == 247)
                    {
                        byte _x[] = new byte[(pos - pos2) + 1];
                        for(idx = pos2; idx <= pos; idx++)
                            _x[idx - pos2] = (byte)(_b[idx] & 0xff);

                        pos2 = ++pos;
                        v.add(_x);
                    } else
                    {
                        pos++;
                    }
                if(pos2 != pos)
                {
                    for(idx = pos2; idx < len; idx++)
                        _b[idx - pos2] = _b[idx];

                    pos2 = len - pos2;
                } else
                {
                    pos2 = pos = 0;
                }
            } while(true);
            in.close();
        }
        catch(Exception e)
        {
            System.out.println("File: " + fname + " not found!");
            System.out.println(e.getMessage());
            return null;
        }
        return v;
    }

    public synchronized int enterAction()
    {
        if(inActionCounter == 0)
            bcedit.app.setCursor(new Cursor(3));
        return ++inActionCounter;
    }

    public synchronized int leaveAction()
    {
        if(inActionCounter == 1)
            bcedit.app.setCursor(BCDefaults.curDefault);
        return --inActionCounter;
    }

    private synchronized boolean selectNewController()
    {
        MidiDeviceHolder mdh = controllerPanel.getSelectedCtrl();
        if(mdh == null)
        {
            controllerPanel.resetCtrl();
            return true;
        }
        if(controllerPanel.saveState())
        {
            ReadControllerFull p = new ReadControllerFull(this, mdh, controllerPanel);
            p.start();
        }
        return true;
    }

    private synchronized boolean scanMidiPorts()
    {
        boolean verbose;
        Vector foundController;
        int i;
        MySysExMessage semaphor;
        Vector listeners;
        Vector devMidiIn;
        Vector devMidiOut;
        verbose = true;
        System.gc();
        
//        if(!controllerPanel.saveState())
//            break MISSING_BLOCK_LABEL_1466;
        
        controllerPanel.startAnimation();
        foundController = new Vector();
        if(controllerPanel != null)
            controllerPanel.reset();
        BCSettings settings = new BCSettings();
        String fakeFileToLoad = settings.getValue("MidiDeviceFake");
        if(fakeFileToLoad != null)
        {
            int ctrlcounter = 0;
            try
            {
                BufferedReader in = new BufferedReader(new FileReader(fakeFileToLoad));
                try
                {
                    do
                    {
                        if(!in.ready())
                            break;
                        String line = in.readLine().trim();
                        if(line.startsWith("$DEVICE"))
                        {
                            StringTokenizer tokens = new StringTokenizer(line.substring(8), " ");
                            int offset = 0;
                            byte mmsg[] = new byte[tokens.countTokens()];
                            while(tokens.hasMoreElements()) 
                            {
                                try
                                {
                                    mmsg[offset] = (byte)(Integer.parseInt(tokens.nextToken()) & 0xff);
                                }
                                catch(NumberFormatException e)
                                {
                                    mmsg[offset] = 0;
                                }
                                offset++;
                            }
                            ctrlcounter++;
                            foundController.add(new MidiDeviceHolder("faked device " + Integer.toString(ctrlcounter), "faked device " + Integer.toString(ctrlcounter), mmsg));
                        }
                    } while(true);
                    in.close();
                }
                catch(IOException e2)
                {
                    System.out.print("Exception caught while reading: ");
                    System.out.print(e2.getMessage());
                }
            }
            catch(FileNotFoundException e1)
            {
                System.out.println("File \"" + fakeFileToLoad + "\" not found.");
            }
            controllerPanel.addController(foundController);
            
            //break MISSING_BLOCK_LABEL_1453;
            
        }
        semaphor = new MySysExMessage();
        Vector sysExListener = new Vector();
        BcSysExListener listener = null;
        listeners = new Vector();
        byte identifyMessage[] = null;
        devMidiIn = new Vector();
        devMidiOut = new Vector();
        javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
        for(i = 0; i < infos.length; i++)
        {
            if(verbose)
                System.out.println("Scanning: " + infos[i].getName());
            try
            {
                MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
                String testString1 = device.getDeviceInfo().getName().toLowerCase();
                if(device.getMaxTransmitters() != 0 && testString1.indexOf("synth") == -1 && testString1.indexOf("sequencer") == -1 && !testString1.equals("microsoft midi-mapper"))
                {
                    listener = new BcSysExListener(device, semaphor);
                    if(listener.isActive())
                    {
                        devMidiIn.add(device);
                        listeners.add(listener);
                        if(verbose)
                            System.out.println("Add listener for: " + infos[i].getName());
                    }
                }
                if(device.getMaxReceivers() == 0)
                    continue;
                try
                {
                    device.open();
                    if(testString1.indexOf("synth") != -1 || testString1.indexOf("sequencer") != -1 || testString1.equals("microsoft midi-mapper"))
                        continue;
                    devMidiOut.add(device);
                    if(verbose)
                        System.out.println("Device = " + infos[i].getName() + " is Midi OUT");
                    device.close();
                    continue;
                }
                catch(MidiUnavailableException MidiUnavailEx)
                {
                    if(verbose)
                    {
                        System.out.print("Error: ");
                        System.out.println(MidiUnavailEx.getMessage());
                    }
                }
            }
            catch(MidiUnavailableException e) { }
        }

        if(devMidiOut.size() == 0 || devMidiIn.size() == 0)
        {
            devMidiIn.clear();
            devMidiOut.clear();
            controllerPanel.addController(foundController);
            controllerPanel.stopAnimation();
            controllerPanel.restoreState();
            return false;
        }
        i = 0;
//_L3:
	while(1==1) {
        if(i >= devMidiOut.size()) {
        	controllerPanel.addController(foundController);
            devMidiIn.clear();
            devMidiOut.clear();
            for(i = 0; i < listeners.size(); i++)
                ((BcSysExListener)listeners.get(i)).close();

            listeners.clear();
            listeners = null;
            controllerPanel.stopAnimation();
            controllerPanel.restoreState();
            return true;
        }
        
        
//_L1:
        Receiver rx = null;
        MidiDevice device = (MidiDevice)devMidiOut.get(i);
        String devName = device.getDeviceInfo().getName();
        if(devName.equals("Microsoft MIDI-Mapper"))
            continue; /* Loop/switch isn't completed */
        try
        {
            device.open();
            for(long was = System.currentTimeMillis(); System.currentTimeMillis() - was < 10L;);
            rx = device.getReceiver();
label0:
            for(int cnt = 0; cnt < 2; cnt++)
            {
                byte identifyMessage2[];
                if(cnt < 1)
                {
                    if(verbose)
                        System.out.println("Scanning: \"" + devName + "\" - FADER");
                    identifyMessage2 = MsgBuilder.identify(127, 20);
                } else
                {
                    if(verbose)
                        System.out.println("Scanning: \"" + devName + "\" - ROTARY");
                    identifyMessage2 = MsgBuilder.identify(127, 21);
                }
                SysexMessage sysexMessage = new SysexMessage();
                sysexMessage.setMessage(identifyMessage2, identifyMessage2.length);
                rx.send(sysexMessage, -1L);
                long was = System.currentTimeMillis();
                do
                {
                    if(System.currentTimeMillis() - was >= 120L)
                        continue label0;
                    if(semaphor.peekNext() != null)
                    {
                        String tmpInDevice = null;
                        do
                        {
                            Object o;
                            if((o = semaphor.getNext()) == null)
                                break;
                            if(o instanceof String)
                            {
                                if(verbose)
                                    System.out.print((String)o + " - ");
                                tmpInDevice = (String)o;
                            } else
                            if(o instanceof byte[])
                            {
                                byte mmsg[] = (byte[])o;
                                if(mmsg[5] == identifyMessage2[5] && mmsg[6] == 
                                	identifyMessage2[6] + 1)
                                {
                                    was = System.currentTimeMillis() - was;
                                    if(verbose)
                                    {
                                        System.out.println(device.getDeviceInfo().getName() + " answered after " + Long.toString(was) + " ms - ");
                                        for(int mi = 0; mi < mmsg.length; mi++)
                                            System.out.print(Integer.toString(mmsg[mi] & 0xff, 16) + " ");

                                        System.out.println();
                                    }
                                    foundController.add(new MidiDeviceHolder(device.getDeviceInfo().getName(), tmpInDevice, mmsg));
                                }
                            }
                        } while(true);
                        was = System.currentTimeMillis();
                    }
                } while(true);
            }

            device.close();
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
        i++;
	}
//          goto _L3
//          
//       L2:
//        controllerPanel.addController(foundController);
//        devMidiIn.clear();
//        devMidiOut.clear();
//        for(i = 0; i < listeners.size(); i++)
//            ((BcSysExListener)listeners.get(i)).close();
//
//        listeners.clear();
//        listeners = null;
//        controllerPanel.stopAnimation();
//        controllerPanel.restoreState();
//        return true;
    }

    private void LoadPresetFromHardware()
    {
        if(controllerPanel.getPresetListIndex() < 0 && controllerPanel.isVirtual())
        {
            JOptionPane.showMessageDialog(bcedit.app, "There is no PRESET selected.", "???", 0);
            return;
        } else
        {
            (new BCInternalNotify(this, "EDITOR:RequestPreset:00:Dummy")).start();
            return;
        }
    }

    private void LoadDeviceFromComputer()
    {
        if(computerPanel.getActiveDevice() == null)
            return;
        if(editorPanel.getDeviceType() == computerPanel.getActiveDevice().getModelID())
        {
            boolean doIt = true;
            if(settings.getBooleanValue("confirmComputerLoad", true) && 0 != JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to overwrite your work?", "Question", 0))
                doIt = false;
            if(doIt)
            {
                editorPanel.startAnimation();
                editorPanel.newDevice(BCL.getCopy(computerPanel.getActiveDevice()));
                editorPanel.stopAnimation();
            }
        } else
        {
            JOptionPane.showMessageDialog(bcedit.app, "You cannot load a diffrent device type!", "Wrong device type", 0);
        }
    }

    private void LoadPresetFromComputer()
    {
        if(computerPanel.getActivePreset() == null)
            return;
        if(editorPanel.getDeviceType() == computerPanel.getActiveDevice().getModelID())
        {
            boolean doIt = true;
            if(settings.getBooleanValue("confirmComputerLoad", true) && 0 != JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to overwrite your current preset?", "Question", 0))
                doIt = false;
            if(doIt)
                editorPanel.newPreset(computerPanel.getActivePreset().getCopy());
        } else
        {
            JOptionPane.showMessageDialog(bcedit.app, "You cannot load a diffrent device type!", "Wrong device type", 0);
        }
    }

    private synchronized boolean requestPreset(String s, int which, boolean forceYes)
    {
        if(!forceYes)
            switch(which)
            {
            case 126: // '~'
                if(0 != JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to load all presets at once?\n(This is very time consuming)", "Load ALL PRESETS from controller", 0))
                    return false;
                break;

            default:
                if(0 != JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to overwrite your current preset?", "Load PRESET from controller", 0))
                    return false;
                break;
            }
        if(!controllerPanel.isVirtual())
        {
            if(which < 0)
                which = 127;
            MidiDeviceHolder midiDeviceHolder = controllerPanel.getSelectedCtrl();
            if(midiDeviceHolder == null)
                return false;
            if(!controllerPanel.saveState())
                return false;
            byte devinfo[] = midiDeviceHolder.getDeviceInfo();
            byte mmsg[] = MsgBuilder.requestPreset(devinfo[4] & 0xff, devinfo[5] & 0xff, (byte)which);
            byte answerEnd[] = {
                9, 36, 101, 110, 100
            };
            Vector v = new Vector();
            v.add(answerEnd);
            v.add(mmsg);
            controllerPanel.startAnimation();
            performSysExRequest x = new performSysExRequest(this, midiDeviceHolder, new MySysExMessage(), v, 0);
            x.start();
        } else
        {
            if(which < 0)
                return false;
            String fname = controllerPanel.getSelectedCtrlString();
            int idx = 127;
            if(fname.substring(fname.indexOf(" ") + 1).startsWith("BCR"))
            {
                fname = "syx/bcr_FACTORY_PRESETS.syx";
                idx = 21;
            } else
            if(fname.substring(fname.indexOf(" ") + 1).startsWith("BCF"))
            {
                fname = "syx/bcf_FACTORY_PRESETS.syx";
                idx = 20;
            } else
            {
                System.out.println("Bail out: " + fname.substring(fname.indexOf(" ") + 1));
                return false;
            }
            if(!controllerPanel.saveState())
                return false;
            controllerPanel.startAnimation();
            Vector v = readSysExFile(fname);
            MySysExMessage sysex = new MySysExMessage();
            byte mmsg1[] = MsgBuilder.requestPreset(127, idx, (byte)which);
            byte mmsg2[] = new byte[mmsg1.length + 1];
            mmsg2[0] = 0;
            for(idx = 0; idx < mmsg1.length; idx++)
                mmsg2[1 + idx] = mmsg1[idx];

            sysex.add(mmsg2);
            if(which == 126)
                for(idx = 0; idx < v.size(); idx++)
                    sysex.add(v.elementAt(idx));

            else
            if(which != 127)
                for(idx = 0; idx < v.size(); idx++)
                {
                    String s1 = new String((byte[])v.elementAt(idx));
                    s1 = s1.substring(9, s1.length() - 1);
                    if(s1.startsWith("$rev "))
                    {
                        sysex.add(v.elementAt(idx));
                        continue;
                    }
                    if(!s1.startsWith("$store ") || BCL.readval(s1.substring(7)) != which + 1)
                        continue;
                    int startidx;
                    for(startidx = idx - 1; !(new String((byte[])v.elementAt(startidx))).substring(9).startsWith("$preset"); startidx--);
                    while(startidx < idx) 
                        sysex.add(v.elementAt(startidx++));
                    sysex.add(new byte[] {
                        -16, 0, 32, 50, 127, 127, 32, 0, 0, 36, 
                        101, 110, 100, -9
                    });
                    break;
                }

            BCInternalNotify notify = new BCInternalNotify(sysex, "SysExRequestPerformed");
            notify.start();
            return false;
        }
        return false;
    }

    private void enableAllButtons(int panelidx, int compidx, boolean hideShow)
    {
        enableAllButtons(panelidx, compidx, hideShow, (String)null);
    }

    private void enableAllButtons(int panelidx, int compidx, boolean hideShow, String hint)
    {
        if(buttonPanel == null)
            return;
        JComponent c = (JComponent)buttonPanel[panelidx].getComponent(compidx);
        int i = c.getComponentCount();
        do
        {
            if(--i < 0)
                break;
            if(c.getComponent(i).getName() != null && (c.getComponent(i).getName().startsWith("Save") || c.getComponent(i).getName().startsWith("Load")))
            {
                c.getComponent(i).setEnabled(hideShow);
                if(c.getComponent(i) instanceof JComponent)
                    ((JComponent)c.getComponent(i)).setToolTipText(hint);
            }
        } while(true);
    }

    private void enableSaveButtons(int panelidx, int compidx, boolean hideShow, String hint)
    {
        if(panelidx == 0)
            editorPanel.enableAutoSendButton(hideShow);
        if(buttonPanel == null)
            return;
        JComponent c = (JComponent)buttonPanel[panelidx].getComponent(compidx);
        int i = c.getComponentCount();
        do
        {
            if(--i < 0)
                break;
            if(c.getComponent(i).getName() != null && c.getComponent(i).getName().startsWith("Save"))
            {
                c.getComponent(i).setEnabled(hideShow);
                if(c.getComponent(i) instanceof JComponent)
                    ((JComponent)c.getComponent(i)).setToolTipText(hint);
            }
        } while(true);
    }

    private void enableSaveButtons(int panelidx, int compidx, boolean hideShow)
    {
        enableSaveButtons(panelidx, compidx, hideShow, (String)null);
    }

    private synchronized boolean buildPresetNames(String s)
    {
        if(!s.startsWith("Virtual "))
        {
            MidiDeviceHolder midiDeviceHolder = controllerPanel.getSelectedCtrl();
            if(midiDeviceHolder == null)
                return false;
            if(!controllerPanel.saveState())
                return false;
            Vector v = new Vector();
            v.add(new Integer(32));
            v.add(MsgBuilder.requestPresetName(midiDeviceHolder.getDeviceInfo()[4] & 0x7f, midiDeviceHolder.getDeviceInfo()[5] & 0x7f, 126));
            controllerPanel.startAnimation();
            enableSaveButtons(0, 0, true);
            enableSaveButtons(0, 1, true);
            performSysExRequest x = new performSysExRequest(this, midiDeviceHolder, new MySysExMessage(), v, -1);
            x.start();
        } else
        {
            int pos = 0;
            int idx = 0;
            String t = null;
            Vector v = new Vector();
            Vector vsrc = null;
            enableSaveButtons(0, 0, false, "Virtual devices are READ ONLY");
            enableSaveButtons(0, 1, false, "Virtual devices are READ ONLY");
            if(s.substring(s.indexOf(" ") + 1).startsWith("BCR"))
                vsrc = readSysExFile("syx/bcr_FACTORY_PRESETS.syx");
            else
            if(s.substring(s.indexOf(" ") + 1).startsWith("BCF"))
                vsrc = readSysExFile("syx/bcf_FACTORY_PRESETS.syx");
            else
                return false;
            for(pos = 0; pos < vsrc.size(); pos++)
            {
                if(!(vsrc.elementAt(pos) instanceof byte[]))
                {
                    System.out.println("Vector(" + pos + ") is not of type byte[]");
                    continue;
                }
                t = new String((byte[])vsrc.elementAt(pos));
                if(t == null)
                    continue;
                t = t.substring(9, t.length() - 1).trim();
                if(t.length() == 0 || !t.startsWith(".name"))
                    continue;
                String sx1 = "";
                if(++idx < 10)
                    sx1 = "0" + Integer.toString(idx);
                else
                    sx1 = Integer.toString(idx);
                sx1 = sx1 + ": ";
                int num = t.indexOf("'") + 1;
                int num2 = t.indexOf("'", num);
                if(num2 < num)
                    num2 = num + 24;
                t = t.substring(num, num2);
                sx1 = sx1 + t;
                v.add(sx1);
            }

            while(idx < 32) 
            {
                String sx1 = "";
                if(++idx < 10)
                    sx1 = "0" + Integer.toString(idx);
                else
                    sx1 = Integer.toString(idx);
                sx1 = sx1 + ": ";
                v.add(sx1);
            }
            controllerPanel.setPresetList(v);
        }
        return true;
    }

    public void registerEditorGlobals(EditorGlobals n)
    {
        editorGlobals = n;
    }

    private void genButtonPanels(int panelidx, boolean visibility)
    {
        int idx = 0;
        String Name[] = {
            "Device", "Preset", "Element"
        };
        do
        {
            Rectangle rc;
            String loadString;
            String saveString;
            switch(panelidx)
            {
            case 0: // '\0'
                rc = controllerPanel.getBoundsOfList(idx);
                loadString = "Load";
                saveString = "Save";
                break;

            case 1: // '\001'
                rc = computerPanel.getBoundsOfList(idx);
                loadString = "Load";
                saveString = "Save";
                break;

            default:
                return;
            }
            if(rc != null)
            {
                int MaxRows = 5;
                JPanel jp1 = new JPanel(new GridLayout(MaxRows, 1));
                jp1.setOpaque(false);
                for(int i = 0; i < MaxRows / 2 - 1; i++)
                {
                    JPanel f = new JPanel();
                    f.setOpaque(false);
                    jp1.add(f);
                }

                rc.x = 0;
                rc.width = buttonPanel[panelidx].getWidth();
                jp1.setBounds(rc);
                ListLabel x = new ListLabel(loadString);
                x.setName("Load:" + panelidx + ":" + idx);
                x.setBControlButtonDefault2();
                x.addMouseListener(this);
                if(panelidx != 0)
                {
                    x.setImage(BCImages.getImage("Left-1.png"));
                    x.setImageOrientation(0);
                    x.setPosition(2);
                } else
                {
                    x.setImage(BCImages.getImage("Right-1.png"));
                    x.setImageOrientation(2);
                    x.setPosition(0);
                }
                jp1.add(x);
                ListLabel ll1 = new ListLabel(Name[idx]);
                ll1.setBControlDefault();
                ll1.useGradient(false, false);
                jp1.add(ll1);
                x = new ListLabel(saveString);
                x.setName("Save:" + panelidx + ":" + idx);
                x.setBControlButtonDefault2();
                x.addMouseListener(this);
                if(panelidx == 0)
                {
                    x.setImage(BCImages.getImage("Left-1.png"));
                    x.setImageOrientation(2);
                    x.setPosition(0);
                } else
                {
                    x.setImage(BCImages.getImage("Right-1.png"));
                    x.setImageOrientation(0);
                    x.setPosition(2);
                }
                jp1.add(x);
                for(int i = 0; i < MaxRows / 2 - 1; i++)
                {
                    JPanel f = new JPanel();
                    f.setOpaque(false);
                    jp1.add(f);
                }

                jp1.setVisible(visibility);
                buttonPanel[panelidx].add(jp1);
                idx++;
            } else
            {
                return;
            }
        } while(true);
    }

    public void setButtonPanels(SizedPanel btnpnls[])
    {
        buttonPanel = btnpnls;
        if(controllerPanel != null)
            genButtonPanels(0, false);
        if(computerPanel != null)
            genButtonPanels(1, false);
    }

    public void setEditorPanel(EditorPanel n)
    {
        editorPanel = n;
    }

    public void setControllerPanel(ControllerPanel n)
    {
        controllerPanel = n;
        if(buttonPanel == null)
            return;
        if(buttonPanel[0].getComponentCount() == 0)
            genButtonPanels(0, false);
    }

    public void setComputerPanel(ComputerPanel n)
    {
        computerPanel = n;
        if(buttonPanel == null)
            return;
        if(buttonPanel[1].getComponentCount() == 0)
            genButtonPanels(1, false);
    }

    public void setMainMenu(BcEditMenu mnu)
    {
        BCActionListener _tmp = this;
        MainMenuBar = mnu;
    }

    public SizedPanel getButtonPanel(int i)
    {
        if(i < 0 || i > 1)
            return null;
        else
            return buttonPanel[i];
    }

    public boolean SetLookAndFeel(String str)
    {
    	try {
	        UIManager.setLookAndFeel(str);
	        SwingUtilities.updateComponentTreeUI(bcedit.app);
	        bcedit.app.pack();
	        return true;
    	}
    	catch (Exception ex1) {        
	        System.out.println("set LookandFeel failed! Cause:");
	        System.out.println(ex1.getMessage());
	        return false;
    	}
    }

    public boolean RequestCloseApplication()
    {
        int x = JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to quit?", "Quit program", 2, 3);
        if(x != 0)
            return false;
        BCSettings settings = new BCSettings();
        settings.writeSettings();
        if(editorPanel.getDevice() != null)
            doSaveSingleDevice("session", editorPanel.getDevice(), null);
        System.exit(0);
        return true;
    }

    public boolean EditorAction(String cmd)
    {
        if(cmd.startsWith("SelectPreset "))
            return false;
        if(cmd.startsWith("Edit BCF2000"))
        {
            if(editorPanel == null)
                return false;
            boolean bflag = computerPanel.getDeviceType() == 20;
            String hint = bflag ? null : "Wrong device type";
            enableAllButtons(1, 0, bflag, hint);
            enableAllButtons(1, 1, bflag, hint);
            if(editorPanel.setEditorModel(20))
            {
                editorPanel.repaint();
                return true;
            }
        } else
        if(cmd.startsWith("Edit BCR2000"))
        {
            if(editorPanel == null)
                return false;
            boolean bflag = computerPanel.getDeviceType() == 21;
            String hint = bflag ? null : "Wrong device type";
            enableAllButtons(1, 0, bflag, hint);
            enableAllButtons(1, 1, bflag, hint);
            if(editorPanel.setEditorModel(21))
            {
                editorPanel.repaint();
                return true;
            }
        } else
        {
            if(cmd.startsWith("RequestPreset:"))
            {
                presetRequestor = 2;
                requestPreset(cmd.substring(cmd.indexOf(":")), controllerPanel.getPresetListIndex(), false);
                return true;
            }
            if(cmd.startsWith("RestoreSession"))
            {
                editorPanel.startAnimation();
                BCSettings settings = new BCSettings();
                String startpath = settings.getValue("storage location");
                File fe = new File(startpath);
                if(fe.exists())
                {
                    startpath = startpath + file_seperator + "session.bc";
                    fe = new File(startpath);
                    if(fe.exists())
                    {
                        BCDevice bcDevice = BCL.initDeviceFromFile(startpath);
                        if(bcDevice != null)
                            editorPanel.newDevice(bcDevice);
                    }
                }
                editorPanel.stopAnimation();
                return true;
            }
        }
        return false;
    }

    public boolean ComputerAction(String cmd)
    {
        if(cmd.startsWith("activeDeviceType:"))
        {
            if(editorPanel == null)
                return true;
            int modelId = BCL.readval(cmd.substring(cmd.indexOf(":") + 1));
            if(modelId == editorPanel.getDeviceType())
            {
                enableAllButtons(1, 0, true);
                enableAllButtons(1, 1, true);
            } else
            {
                enableAllButtons(1, 0, false, "Wrong device type");
                enableAllButtons(1, 1, false, "Wrong device type");
            }
            return true;
        } else
        {
            return false;
        }
    }

    public synchronized boolean ControllerAction(String actionString)
    {
        String nextCmd = "";
        int nextToken = actionString.indexOf(":");
        if(nextToken >= 0)
            nextCmd = actionString.substring(nextToken + 1);
        if(actionString.equals("SelectCtrl"))
        {
            selectNewController();
            return true;
        }
        if(actionString.equals("ScanMidi"))
        {
            scanMidiPorts();
            return true;
        }
        if(actionString.startsWith("BuildPresetNames:"))
        {
            buildPresetNames(nextCmd);
            return true;
        }
        if(actionString.startsWith("RequestPreset:"))
        {
            presetRequestor = 1;
            requestPreset(nextCmd, controllerPanel.getPresetListIndex(), true);
            return true;
        }
        if(actionString.startsWith("RequestDevice:"))
        {
            presetRequestor = 3;
            requestPreset(nextCmd, 126, false);
            return true;
        }
        if(actionString.startsWith("doLayout"))
        {
            doLayout();
            return true;
        } else
        {
            return false;
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        boolean bActionHandled = false;
        if(inActionCounter > 0)
            return;
        String cmd = e.getActionCommand();
        if(cmd.equals("CloseApplication"))
            bActionHandled = RequestCloseApplication();
        else
        if(cmd.startsWith("COMPUTER:"))
            bActionHandled = ComputerAction(cmd.substring(cmd.indexOf(":") + 1));
        else
        if(cmd.startsWith("EDITOR:"))
            bActionHandled = EditorAction(cmd.substring(cmd.indexOf(":") + 1));
        else
        if(cmd.startsWith("CONTROLER:"))
            bActionHandled = ControllerAction(cmd.substring(cmd.indexOf(":") + 1));
        else
        if(cmd.startsWith("SetLookAndFeel="))
            bActionHandled = SetLookAndFeel(cmd.substring(15));
        else
        if(!cmd.equals("ScanForMidiHardware"))
            if(cmd.equals("execwwwBehringer"))
            {
                BCSettings settings = new BCSettings();
                String sBrowser = settings.getValue("internet browser");
                String sHomepage = "http://www.behringer.com";
                if(sBrowser == null && file_seperator.equals("\\"))
                    sBrowser = "explorer.exe";
                if(sBrowser != null)
                {
                    sBrowser = sBrowser + " " + sHomepage;
                } else
                {
                    JOptionPane.showMessageDialog(bcedit.app, "Sorry, I do not know which browser to use.\nYou may specify it in your configuration-file:\n    " + System.getProperty("user.home") + System.getProperty("file.separator") + "bcedit.cfg" + "\nusing a line like:\n" + "    internet browser=C:\\WINNT\\explorer.exe" + "\nor\n" + "    internet browser=/opt/kde3/bin/konqueror" + "\nor visit our homepage \"" + sHomepage + "\" manually.", "Browser unknown", 0);
                    return;
                }
                try
                {
                    Runtime.getRuntime().exec(sBrowser);
                    bActionHandled = true;
                }
                catch(IOException rt_e)
                {
                    System.out.println("Error: " + rt_e.getMessage());
                    bActionHandled = false;
                }
            } else
            if(cmd.equals("NewHardwareSelected"))
            {
                Object obj = e.getSource();
                if(obj instanceof JComboBox)
                {
                    JComboBox cb = (JComboBox)obj;
                    String sItem = (String)cb.getSelectedItem();
                    if(sItem.equals(""))
                        bcedit.app.newHardwareSelected(0);
                    else
                    if(sItem.indexOf("BCF2000") >= 0)
                        bcedit.app.newHardwareSelected(1);
                    else
                    if(sItem.indexOf("BCR2000") >= 0)
                        bcedit.app.newHardwareSelected(2);
                    else
                        bcedit.app.newHardwareSelected(-1);
                }
            } else
            {
                if(cmd.equals("ExportSysEx"))
                {
                    editorPanel.exportPreset();
                    return;
                }
                if(cmd.equals("ImportSysEx"))
                {
                    editorPanel.importPreset();
                    return;
                }
                if(cmd.equals("LoadDeviceFromComputer"))
                {
                    LoadDeviceFromComputer();
                    return;
                }
                if(cmd.equals("LoadDeviceFromHardware"))
                {
                    if(controllerPanel != null && editorPanel != null && editorPanel.getDevice() != null)
                    {
                        BCInternalNotify notify = new BCInternalNotify(this, "CONTROLER:RequestDevice:" + controllerPanel.getSelectedCtrlString());
                        notify.start();
                        return;
                    }
                } else
                {
                    if(cmd.equals("LoadPresetFromHardware"))
                    {
                        LoadPresetFromHardware();
                        return;
                    }
                    if(cmd.equals("LoadPresetFromComputer"))
                    {
                        LoadPresetFromComputer();
                        return;
                    }
                    if(cmd.equals("SaveSingleElementAs"))
                    {
                        System.out.println("\"SaveSingleElementAs\" is not supported in this version!");
                    } else
                    {
                        if(cmd.equals("SaveSingleElementToComputer"))
                            return;
                        if(cmd.equals("SavePresetToComputer"))
                            return;
                        if(cmd.equals("SaveSingleDeviceToComputer"))
                        {
                            doSaveSingleDevice(null, editorPanel.getDevice(), "Devices");
                            return;
                        }
                        if(cmd.equals("showAbout"))
                        {
                            JDlgAbout x = new JDlgAbout();
                            x.show();
                            x.dispose();
                            return;
                        }
                        if(cmd.equals("PRINT_SCREENSHOT"))
                            bActionHandled = true;
                        else
                        if(cmd.equals("PopUpPreferences"))
                        {
                            BCPreferenceDialog x = new BCPreferenceDialog();
                            x.setTitle("Preferences");
                            x.show();
                            bActionHandled = true;
                        }
                    }
                }
            }
        if(!bActionHandled)
            System.out.println("Unhandled Command:" + cmd);
    }

    public boolean EnableMenuItemFor(String ActionString, boolean bEnable)
    {
label0:
        {
            BCActionListener _tmp = this;
            if(MainMenuBar == null)
                return false;
            int idx = 0;
            do
            {
                BCActionListener _tmp1 = this;
                if(idx >= MainMenuBar.getMenuCount())
                    break label0;
                BCActionListener _tmp2 = this;
                JMenu menu = MainMenuBar.getMenu(idx);
                for(int itemindex = 0; itemindex < menu.getItemCount(); itemindex++)
                {
                    JMenuItem item = menu.getItem(itemindex);
                    if(item == null)
                        continue;
                    String test = item.getActionCommand();
                    if(test != null && test.equals(ActionString))
                    {
                        item.setEnabled(bEnable);
                        return true;
                    }
                }

                idx++;
            } while(true);
        }
        return false;
    }

    public void internalNotify(Object o, String action)
    {
        if(action.startsWith("CONTROLER:"))
        {
            ControllerAction(action.substring(action.indexOf(":") + 1));
            return;
        }
        if(action.startsWith("EDITOR:"))
        {
            EditorAction(action.substring(action.indexOf(":") + 1));
            return;
        }
        if(action.startsWith("COMPUTER:"))
        {
            ComputerAction(action.substring(action.indexOf(":") + 1));
            return;
        }
        if(action.startsWith("SendPresetToHardware:") || action.equals("SendElementToHardware"))
        {
            int i = action.indexOf("Preset") <= 0 ? 0 : 1;
            if(i == 1)
            {
                int j = BCL.readval(action.substring(action.indexOf(":") + 1));
                j = Math.max(0, j);
                if(j >= 0x80000000)
                    if(j == 126)
                        i = 2;
                    else
                        i = (j << 8) + i;
                if(j == 0)
                {
                    if(0 != JOptionPane.showConfirmDialog(bcedit.app, "You are about to store to the controller temp area\nWas this your intention?", "Question", 0))
                        return;
                } else
                if(0 != JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to overwrite your current " + (i != 2 ? "preset?" : "device"), "Question", 0))
                    return;
            }
            if(controllerPanel.isPanelDisabled())
            {
                JOptionPane.showMessageDialog(bcedit.app, "Sorry, the Controller seems busy right now.", "Controller Busy", 0);
                return;
            }
            ThreadSendScript xxx = new ThreadSendScript(this, controllerPanel, editorPanel, i);
            xxx.start();
        } else
        if(action.startsWith("loadSingleElementFrom"))
        {
            BCElement bce = null;
            if(action.endsWith("Library"))
                bce = computerPanel.loadElementFromLibrary();
            else
            if(action.endsWith("Preset"))
                bce = computerPanel.loadElementFromPreset();
            else
                return;
            if(bce != null)
                editorPanel.newElement(bce);
        } else
        {
            if(action.equals("SysExRequestPerformed"))
            {
                sysExRequestPerformed((MySysExMessage)o);
                return;
            }
            if(action.startsWith("SendPresetGlobalsToHardware"))
            {
                if(!editorPanel.isAutoSendActive())
                    return;
                if(!controllerPanel.isVirtual())
                {
                    int which = 127;
                    MidiDeviceHolder midiDeviceHolder = controllerPanel.getSelectedCtrl();
                    if(midiDeviceHolder == null)
                        return;
                    if(!controllerPanel.saveState())
                        return;
                    controllerPanel.startAnimation();
                    byte devinfo[] = midiDeviceHolder.getDeviceInfo();
                    int id = devinfo[4] & 0xff;
                    int type = devinfo[5] & 0xff;
                    int lineno = 0;
                    String s = "$rev " + BCL.getRevFromId(type);
                    Vector v = new Vector();
                    v.add(MsgBuilder.StringtoScriptByte(s, id, type, lineno++));
                    v.add(MsgBuilder.StringtoScriptByte(BCL.GetToken(259), id, type, lineno++));
                    s = action.substring(action.indexOf(':') + 1);
                    if(s.substring(s.indexOf(':') + 1).equals("true"))
                        s = "  ." + s.substring(0, s.indexOf(':')) + " on";
                    else
                    if(s.substring(s.indexOf(':') + 1).equals("false"))
                    {
                        s = "  ." + s.substring(0, s.indexOf(':')) + " off";
                    } else
                    {
                        int i = -1;
                        try
                        {
                            i = Integer.parseInt(s.substring(s.indexOf(':') + 1));
                        }
                        catch(NumberFormatException e) { }
                        if(i < 0)
                        {
                            controllerPanel.restoreState();
                            controllerPanel.stopAnimation();
                            return;
                        }
                        s = "  ." + s.substring(0, s.indexOf(':')) + " " + Integer.toString(i + 1);
                    }
                    v.add(MsgBuilder.StringtoScriptByte(s, id, type, lineno++));
                    if(which != 127)
                    {
                        s = BCL.GetToken(265) + " " + (which + 1);
                        v.add(MsgBuilder.StringtoScriptByte(s, id, type, lineno++));
                    }
                    v.add(MsgBuilder.StringtoScriptByte("$end", id, type, lineno++));
                    (new performSysExRequest(this, midiDeviceHolder, new MySysExMessage(), v, 0)).start();
                }
                return;
            }
            if(action.startsWith("NewContainerTabActivated"))
            {
                MyTabbedPanel t = (MyTabbedPanel)o;
                String tabName = t.getActiveTabName();
                String oName = t.getName();
                if(oName != null && oName.equals("Editor"))
                    if(tabName.equals("Presets"))
                    {
                        EnableMenuItemFor("ImportSysEx", true);
                        EnableMenuItemFor("ExportSysEx", true);
                        EnableMenuItemFor("LoadDeviceFromHardware", true);
                        EnableMenuItemFor("LoadDeviceFromComputer", true);
                        if(buttonPanel[0].getComponentCount() == 0)
                            genButtonPanels(0, false);
                        for(int i = 0; i < 2; i++)
                        {
                            buttonPanel[i].getComponent(0).setVisible(true);
                            buttonPanel[i].getComponent(1).setVisible(true);
                        }

                    } else
                    {
                        EnableMenuItemFor("ImportSysEx", false);
                        EnableMenuItemFor("ExportSysEx", false);
                        EnableMenuItemFor("LoadDeviceFromHardware", false);
                        EnableMenuItemFor("LoadDeviceFromComputer", false);
                        if(buttonPanel[0].getComponentCount() == 0)
                            genButtonPanels(0, false);
                        for(int i = 0; i < 2; i++)
                        {
                            buttonPanel[i].getComponent(0).setVisible(false);
                            buttonPanel[i].getComponent(1).setVisible(true);
                        }

                    }
            } else
            if(action.startsWith("ElementAutoSend"))
            {
                boolean flag = action.substring(action.indexOf(':') + 1).equals("true");
                editorPanel.setAutoSend(flag);
            }
        }
        if(o instanceof MyTabbedPanel)
        {
            MyTabbedPanel t = (MyTabbedPanel)o;
            String tabName = t.getActiveTabName();
            if(t.getName().equals("Computer"))
            {
                if(tabName.equals("Elements"))
                    computerPanel.readElementLibrary();
                else
                if(tabName.equals("Presets"))
                    computerPanel.readPresetDirectory();
            } else
            if(t.getName().equals("Editor"))
            {
                if(editorPanel.getActiveTab() == 2)
                    computerPanel.enableLoadButtons(false, false, true);
                else
                if(editorPanel.getActiveTab() == 1)
                    computerPanel.enableLoadButtons(false, true, false);
            } else
            if(!t.getName().equals("PreferenceDialog"))
                System.out.println(t.getName() + " sends " + action + ":" + tabName);
        }
    }

    private boolean checkForSameElementName(String name)
    {
        BCSettings settings = new BCSettings();
        boolean bReturn = false;
        String startpath = settings.getValue("storage location");
        File fe = new File(startpath);
        if(!fe.exists())
            return bReturn;
        startpath = startpath + file_seperator + "Elements";
        fe = new File(startpath);
        String filenames[] = fe.list();
        int i = 0;
        do
        {
            if(i >= filenames.length)
                break;
            if(filenames[i].endsWith(".bce"))
            {
                String fn = startpath + file_seperator + filenames[i];
                try
                {
                    BufferedReader in = new BufferedReader(new FileReader(fn));
                    StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
                    do
                    {
                        if(!tok.hasMoreElements())
                            break;
                        String s1 = tok.nextToken();
                        if(!s1.startsWith(".name "))
                            continue;
                        int spos = s1.indexOf(' ');
                        if(spos < 0)
                            continue;
                        String n = s1.substring(spos + 1).trim();
                        if(n.equals(name))
                            bReturn = true;
                        break;
                    } while(true);
                    in.close();
                }
                catch(Exception e) { }
            }
            if(bReturn)
                break;
            i++;
        } while(true);
        return bReturn;
    }

    private String checkForSamePresetName(int deviceType, String name)
    {
        int i = 0;
        BCSettings settings = new BCSettings();
        boolean bReturn = false;
        String startpath = settings.getValue("storage location") + file_seperator + "Presets";
        String fn = null;
        File fe = new File(startpath);
        String filenames[] = fe.list();
        i = 0;
        do
        {
            if(i >= filenames.length)
                break;
            if(filenames[i].endsWith(".bcp"))
            {
                fn = startpath + file_seperator + filenames[i];
                try
                {
                    BufferedReader in = new BufferedReader(new FileReader(fn));
                    StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
label0:
                    do
                    {
                        String s1;
label1:
                        do
                        {
                            int check;
                            do
                            {
                                if(!tok.hasMoreElements())
                                    break;
                                s1 = tok.nextToken();
                                if(!s1.startsWith("$deviceType "))
                                    continue label1;
                                check = 0;
                                try
                                {
                                    check = Integer.parseInt(s1.substring(s1.indexOf(' ') + 1));
                                }
                                catch(NumberFormatException nfe) { }
                            } while(check == deviceType);
                            break label0;
                        } while(!s1.startsWith(".name "));
                        int spos = s1.indexOf('\'');
                        if(spos < 0)
                            continue;
                        int epos = s1.substring(spos + 1).indexOf('\'');
                        if(epos < 0)
                            continue;
                        String n = s1.substring(spos + 1, spos + epos).trim();
                        if(n.equals(name))
                            bReturn = true;
                        break;
                    } while(true);
                    in.close();
                }
                catch(Exception e) { }
            }
            if(bReturn)
                break;
            i++;
        } while(true);
        return !bReturn ? null : filenames[i];
    }

    private void doSaveSingleElement()
    {
        if(editorPanel == null)
        {
            System.out.println("SaveSingleElementToComputer::editorPanel is null!");
            JOptionPane.showMessageDialog(bcedit.app, "Please Name your Element before saving.", "No Name Error", 0);
            return;
        }
        BCPreset bcp = editorPanel.getactivePreset();
        if(bcp == null)
        {
            System.out.println("SaveSingleElementToComputer::BCPreset is null!");
            JOptionPane.showMessageDialog(bcedit.app, "Sorry, there is no Preset and therefor no element in the current Editor that I could save.", "No Preset Error", 0);
            return;
        }
        int activeElement = editorPanel.getActiveElementNumber();
        int aElement = activeElement & 0xff;
        int aElementType = activeElement >> 8 & 0xff;
        BCElement bce = bcp.getElement(aElement, aElementType);
        if(bce == null)
        {
            System.out.println("SaveSingleElementToComputer::BCElement (" + Integer.toString(aElement) + ", " + Integer.toString(aElementType) + ") is null! ");
            JOptionPane.showMessageDialog(bcedit.app, "Sorry, there is no valid element to save", "No valid element error", 0);
            return;
        }
        BCSettings settings = new BCSettings();
        long crc = bce.getCRC();
        String startpath = settings.getValue("storage location") + file_seperator + "Elements" + file_seperator + Long.toString(crc) + ".bce";
        File fe = new File(startpath);
        JTextField textField = new JTextField(16);
        textField.setText(bce.name);
        Object oArray[] = {
            new String("Please Name your Element...\nValid are any character except ';'"), textField
        };
        JOptionPane optionPane = new JOptionPane(((Object) (oArray)), 3, 2);
        JDialog dialog = optionPane.createDialog(bcedit.app, "Save Element...");
        dialog.setContentPane(optionPane);
        dialog.setDefaultCloseOperation(0);
        dialog.setModal(true);
        dialog.pack();
        do
            do
            {
                dialog.show();
                Object selectedValue = optionPane.getValue();
                if(((Integer)selectedValue).intValue() != 0)
                    return;
                if(checkForSameElementName(textField.getText().trim()))
                {
                    oArray[0] = new String("The name you entered is already in use by another Element.\nPlease rename your Element...\nValid are any character except ';'");
                    dialog.dispose();
                    optionPane = new JOptionPane(((Object) (oArray)), 3, 2);
                    dialog = optionPane.createDialog(bcedit.app, "Save Element...");
                    dialog.setContentPane(optionPane);
                    dialog.setDefaultCloseOperation(0);
                    dialog.setModal(true);
                    dialog.pack();
                    continue;
                }
                if(textField.getText().trim().indexOf(";") < 0)
                    break;
                oArray[0] = new String("You have entered at least one illegal character\nPlease Name your Element...\nValid are any character except ';'");
                dialog.dispose();
                optionPane = new JOptionPane(((Object) (oArray)), 3, 2);
                dialog = optionPane.createDialog(bcedit.app, "Save Element...");
                dialog.setContentPane(optionPane);
                dialog.setDefaultCloseOperation(0);
                dialog.setModal(true);
                dialog.pack();
            } while(true);
        while(textField.getText().trim().length() == 0);
        dialog.dispose();
        crc = bce.getCRC();
        bce.name = textField.getText().trim();
        String testMessage = "Saving file: " + Long.toString(crc) + ".bce" + "\n" + "CRC: " + Long.toString(crc) + "\n" + "Name: " + bce.name + "\n" + bce.getScript(true);
        JOptionPane.showMessageDialog(bcedit.app, testMessage, "Saving...", 1);
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(startpath));
            out.write(bce.getScript(true));
            out.close();
        }
        catch(Exception eout1)
        {
            String errstr1 = "Error while writing\n\t\"" + startpath + "\"\n";
            errstr1 = errstr1 + eout1.getMessage();
            System.out.println(errstr1);
            JOptionPane.showMessageDialog(bcedit.app, errstr1, "File write error", 0);
            return;
        }
        computerPanel.readElementLibrary();
    }

    private void doSaveSinglePreset()
    {
        String filename = null;
        if(editorPanel == null)
        {
            System.out.println("SavePresetToComputer::editorPanel is null!");
            JOptionPane.showMessageDialog(bcedit.app, "editorPanel is null.", "Internal error", 0);
            return;
        }
        BCPreset bcp = editorPanel.getactivePreset();
        if(bcp == null)
        {
            System.out.println("SaveSingleElementToComputer::BCPreset is null!");
            JOptionPane.showMessageDialog(bcedit.app, "Sorry, there is no active Presetin the current Editor that I could save.", "No Preset Error", 0);
            return;
        }
        filename = checkForSamePresetName(editorPanel.getDeviceType(), bcp.getPreset().getName());
        if(filename != null)
        {
            int x = JOptionPane.showConfirmDialog(bcedit.app, "There is alread a Preset named\n    " + bcp.getPreset().getName() + "\nPress Ok to overwrite or" + "\notherwise press Cancel", "Duplicate name error", 2);
            if(x != 0)
                return;
        }
        BCSettings settings = new BCSettings();
        String startpath = settings.getValue("storage location") + file_seperator + "Presets";
        SaveDialog dlg = new SaveDialog(startpath, ".bcp", computerPanel.getDeviceType());
        dlg.centerIn(bcedit.app.getBounds());
        dlg.filenameinput.setText(filename);
        dlg.setTitle("Save Preset...");
        dlg.show();
        if(dlg.getExitCode() == 1)
        {
            String fname = dlg.filenameinput.getText().trim();
            if(fname.length() == 0)
                return;
            if(fname.endsWith(".bcp"))
                fname = fname.substring(0, fname.length() - 4);
            startpath = startpath + file_seperator + fname + ".bcp";
            try
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(startpath));
                out.write(bcp.getSaveScript());
                out.close();
            }
            catch(Exception eout1)
            {
                String errstr1 = "Error while writing\n\t\"" + startpath + "\"\n";
                errstr1 = errstr1 + eout1.getMessage();
                System.out.println(errstr1);
                JOptionPane.showMessageDialog(bcedit.app, errstr1, "File write error", 0);
                return;
            }
            computerPanel.readPresetDirectory();
        }
    }

    private void doSaveSingleDevice(String fname, BCDevice bcd, String subDir)
    {
        String filename = null;
        int modelID = editorPanel.getDevice().getModelID();
        String extension = "." + BCL.getDeviceNameFromId(modelID) + ".bc";
        BCSettings settings = new BCSettings();
        String startpath = settings.getValue("storage location");
        if(subDir != null && subDir.length() > 0)
            startpath = startpath + file_seperator + subDir;
        if(fname == null)
        {
            SaveDialog dlg = new SaveDialog(startpath, extension, modelID);
            dlg.centerIn(bcedit.app.getBounds());
            dlg.filenameinput.setText(filename);
            dlg.setTitle("Save Device...");
            dlg.show();
            if(dlg.getExitCode() == 1)
                fname = dlg.filenameinput.getText().trim();
        }
        if(fname != null)
        {
            if(fname.length() == 0)
                return;
            if(fname.endsWith(extension))
                fname = fname.substring(0, fname.length() - extension.length());
            startpath = startpath + file_seperator + fname + extension;
            try
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(startpath));
                String s = "$rev " + BCL.getRevFromId(bcd.getModelID()) + ";" + bcd.getScript(1, true);
                out.write(s);
                out.close();
            }
            catch(Exception eout1)
            {
                String errstr1 = "Error while writing\n\t\"" + startpath + "\"\n";
                errstr1 = errstr1 + eout1.getMessage();
                System.out.println(errstr1);
                JOptionPane.showMessageDialog(bcedit.app, errstr1, "File write error", 0);
                return;
            }
            if(subDir != null && subDir.equals("Devices"))
                computerPanel.readDeviceDirectory();
        }
    }

    public static void reColor(Object o)
    {
        if((o instanceof Component) && !(o instanceof JButton))
        {
            Component c = (Component)o;
            c.setBackground(bgColor);
            c.setForeground(fgColor);
        }
        if(o instanceof Container)
        {
            Container c = (Container)o;
            for(int i = 0; i < c.getComponentCount(); i++)
                reColor(c.getComponent(i));

        }
    }

    private void sysExRequestPerformed(MySysExMessage semaphor)
    {
        controllerPanel.stopAnimation();
        controllerPanel.restoreState();
        byte msg[] = (byte[])semaphor.getNext();
        if(msg == null)
            return;
        if(msg[0] == 0)
        {
            int modelType = msg[6] & 0xff;
            switch(msg[7] & 0xff)
            {
            case 1: // '\001'
            case 32: // ' '
            case 34: // '"'
            case 52: // '4'
            case 65: // 'A'
            case 116: // 't'
            default:
                break;

            case 64: // '@'
                String script[] = new String[semaphor.length()];
                int num = 0;
                int identify = msg[8] & 0xff;
                while((msg = (byte[])semaphor.getNext()) != null) 
                    script[num++] = (new String(msg, 9, msg.length - 10)).trim();
                if(identify == 126)
                {
                    BCDevice bcd = BCL.initDeviceFromScript(script);
                    if((presetRequestor & 1) == 1)
                        controllerPanel.newDevice(bcd);
                    if((presetRequestor & 2) == 2)
                        editorPanel.newDevice(bcd);
                } else
                {
                    BCPreset bcp = BCL.initPresetFromScript(script);
                    if((presetRequestor & 1) == 1)
                        controllerPanel.newPreset(bcp);
                    if((presetRequestor & 2) == 2)
                        editorPanel.newPreset(bcp);
                }
                break;

            case 66: // 'B'
                Vector v = new Vector();
                while((msg = (byte[])semaphor.getNext()) != null) 
                {
                    int num2 = (((msg[7] & 0x7f) << 8) + msg[8] & 0x7f) + 1;
                    String sx1;
                    if(num2 < 10)
                        sx1 = "0" + Integer.toString(num2);
                    else
                        sx1 = Integer.toString(num2);
                    sx1 = sx1 + ": ";
                    sx1 = sx1 + (new String(msg, 9, msg.length - 10)).trim();
                    v.add(sx1);
                }
                controllerPanel.setPresetList(v);
                break;
            }
        }
    }

    public static void showChildren(String level, Container c)
    {
        if(level == null)
            level = "";
        String s = level + c.toString().substring(0, c.toString().indexOf("["));
        if(c.getName() != null)
            s = s + " (" + c.getName() + ")";
        System.out.println(s);
        if(c.getComponentCount() > 0)
        {
            level = level + " + ";
            for(int i = 0; i < c.getComponentCount(); i++)
                if(c.getComponent(i) instanceof Container)
                    showChildren(level, (Container)c.getComponent(i));

            level = level.substring(3);
        }
    }

    public static void showChildren(String level, JComponent c)
    {
        if(level == null)
            level = "";
        String s = level + c.toString().substring(0, c.toString().indexOf("["));
        if(c.getName() != null)
            s = s + " (" + c.getName() + ")";
        System.out.println(s);
        if(c.getComponentCount() > 0)
        {
            level = level + " + ";
            for(int i = 0; i < c.getComponentCount(); i++)
                if(c.getComponent(i) instanceof JComponent)
                    showChildren(level, (JComponent)c.getComponent(i));

            level = level.substring(3);
        }
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
            if(l.getName() == null)
                return;
            if(l.getName().startsWith("Load:") || l.getName().startsWith("Save:"))
            {
                StringTokenizer tok = new StringTokenizer(l.getName(), ":");
                int fkt = tok.nextToken().equals("Load") ? 0 : 1;
                int panelidx = BCL.readval(tok.nextToken());
                int fktidx = BCL.readval(tok.nextToken());
                switch((panelidx << 8) + (fktidx << 4) + fkt)
                {
                default:
                    break;

                case 0: // '\0'
                    if(controllerPanel.getSelectedCtrlString() == null)
                    {
                        JOptionPane.showMessageDialog(bcedit.app, "There is no Controller selected.", "???", 0);
                        return;
                    }
                    notify = new BCInternalNotify(this, "CONTROLER:RequestDevice:" + controllerPanel.getSelectedCtrlString());
                    break;

                case 1: // '\001'
                    if(controllerPanel.isVirtual())
                    {
                        JOptionPane.showMessageDialog(bcedit.app, "Virtual devices are read only.", "No way", 0);
                        return;
                    }
                    notify = new BCInternalNotify(this, "SendPresetToHardware:" + Integer.toString(126));
                    break;

                case 16: // '\020'
                    LoadPresetFromHardware();
                    break;

                case 17: // '\021'
                    if(controllerPanel.isVirtual())
                    {
                        JOptionPane.showMessageDialog(bcedit.app, "Virtual devices are read only.", "No way", 0);
                        return;
                    }
                    notify = new BCInternalNotify(this, "SendPresetToHardware:" + Integer.toString(controllerPanel.getPresetListIndex() + 1));
                    break;

                case 256: 
                    LoadDeviceFromComputer();
                    break;

                case 257: 
                    doSaveSingleDevice(null, editorPanel.getDevice(), "Devices");
                    break;

                case 272: 
                    LoadPresetFromComputer();
                    break;

                case 273: 
                    if(computerPanel.getActiveDevice() == null || computerPanel.getActivePreset() == null)
                        break;
                    if(editorPanel.getDeviceType() == computerPanel.getActiveDevice().getModelID())
                    {
                        boolean doIt = true;
                        if(settings.getBooleanValue("confirmComputerSave", true) && 0 != JOptionPane.showConfirmDialog(bcedit.app, "Do you really want to overwrite the selected preset?", "Question", 0))
                            doIt = false;
                        if(doIt)
                        {
                            int idx = computerPanel.getActivePresetIndex();
                            computerPanel.getActiveDevice().setPreset(computerPanel.getActivePresetIndex() + 1, editorPanel.getactivePreset());
                            doSaveSingleDevice(computerPanel.getActiveDeviceFilename(), computerPanel.getActiveDevice(), "Devices");
                            computerPanel.setActivePresetIndex(idx);
                        }
                    } else
                    {
                        JOptionPane.showMessageDialog(bcedit.app, "You cannot save to a different device type!", "Wrong device type", 0);
                    }
                    break;
                }
            }
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

    static EditorPanel editorPanel = null;
    static ControllerPanel controllerPanel = null;
    static ComputerPanel computerPanel = null;
    static EditorGlobals editorGlobals = null;
    static BcEditMenu MainMenuBar = null;
    static SizedPanel buttonPanel[] = null;
    private static int inActionCounter = 0;
    public static String file_seperator = System.getProperty("file.separator");
    public static Color bgColor = new Color(0x5a7eb8);
    public static Color fgColor = new Color(0xffd800);
    static int presetRequestor = 0;
    static final int REQUESTOR_CTRL = 1;
    static final int REQUESTOR_EDIT = 2;
    BCSettings settings;

}
