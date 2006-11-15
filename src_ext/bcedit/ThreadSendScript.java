// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import bcedit.BCL.BCElement;
import bcedit.BCL.BCLError;
import bcedit.BCL.BCPreset;
import java.awt.Frame;
import java.io.PrintStream;
import java.util.StringTokenizer;
import javax.sound.midi.*;
import javax.swing.JOptionPane;

// Referenced classes of package bcedit:
//            MySysExMessage, BcSysExListener, BCInternalNotify, EditorPanel, 
//            ControllerPanel, MidiDeviceHolder, BCActionListener

class ThreadSendScript extends Thread
{

    public ThreadSendScript(BCActionListener alistener, ControllerPanel p, EditorPanel e, int type)
    {
        semaphor = new MySysExMessage();
        listener = null;
        infos = MidiSystem.getMidiDeviceInfo();
        MidiIn = null;
        MidiOut = null;
        device = null;
        bcp = null;
        bce = null;
        actionListener = null;
        typeToSend = -1;
        storeToFlash = 0;
        int atype = e.getActiveElementNumber() >> 8 & 0xff;
        int anum = e.getActiveElementNumber() & 0xff;
        bcp = e.getactivePreset();
        bce = bcp.getElement(anum, atype);
        mdh = p.getSelectedCtrl();
        controllerPanel = p;
        editorPanel = e;
        typeToSend = type & 0xff;
        storeToFlash = type >> 8 & 0xff;
        actionListener = alistener;
    }

    public void run()
    {
        boolean bTimeout = false;
        if(bce == null || bcp == null || mdh == null)
            return;
        if(controllerPanel.saveState())
        {
            controllerPanel.startAnimation();
            for(int i = 0; i < infos.length && (MidiOut == null || MidiIn == null); i++)
                try
                {
                    device = MidiSystem.getMidiDevice(infos[i]);
                    if(MidiOut == null && device.getMaxReceivers() != 0 && infos[i].getName().equals(mdh.getOutDeviceName()))
                        MidiOut = device;
                    if(MidiIn == null && device.getMaxTransmitters() != 0 && infos[i].getName().equals(mdh.getInDeviceName()))
                        MidiIn = device;
                }
                catch(MidiUnavailableException e) { }

            if(MidiIn != null && MidiOut != null)
            {
                int seq = 0;
                byte devinfo[] = mdh.getDeviceInfo();
                SysexMessage sysexMessage = new SysexMessage();
                listener = new BcSysExListener(device, semaphor);
                listener.sequenceNumber = 0;
                listener.setAddDeviceName(false);
                try
                {
                    MidiOut.open();
                    Receiver rx = MidiOut.getReceiver();
                    while(semaphor.getNext() != null) 
                        try
                        {
                            sleep(1L);
                        }
                        catch(InterruptedException iE) { }
                    listener.sequenceNumber = -1;
                    String aga;
label0:
                    switch(typeToSend)
                    {
                    case 0: // '\0'
                        aga = bce.getScript(false);
                        break;

                    case 1: // '\001'
                        aga = bcp.getScript(storeToFlash, false);
                        break;

                    case 2: // '\002'
                        aga = "";
                        int i = 1;
                        do
                        {
                            if(i >= 33)
                                break label0;
                            String ag1 = editorPanel.getPreset(i).getScript(i, false);
                            if(ag1 != null)
                            {
                                aga = aga + ag1;
                                if(!ag1.endsWith(";"))
                                    aga = aga + ";";
                            }
                            i++;
                        } while(true);

                    default:
                        aga = null;
                        break;
                    }
                    StringTokenizer tok = null;
                    if(aga != null && aga.trim().length() > 0)
                    {
                        if(devinfo[5] == 21)
                            aga = "$rev R1;" + aga;
                        else
                        if(devinfo[5] == 20)
                            aga = "$rev F1;" + aga;
                        if(typeToSend != 0)
                            if(aga.endsWith(";"))
                                aga = aga + "$end";
                            else
                                aga = aga + ";$end";
                        tok = new StringTokenizer(aga, ";");
                    } else
                    {
                        tok = new StringTokenizer("", ";");
                    }
                    aga = "";
label1:
                    do
                    {
                        if(!tok.hasMoreTokens() || bTimeout)
                            break;
                        semaphor.reset();
                        String smsg = tok.nextToken();
                        if(smsg.startsWith("$"))
                            aga = smsg;
                        byte mmsg1[] = smsg.getBytes();
                        byte mmsg[] = new byte[mmsg1.length + 10];
                        mmsg[0] = -16;
                        mmsg[1] = 0;
                        mmsg[2] = 32;
                        mmsg[3] = 50;
                        mmsg[4] = devinfo[4];
                        mmsg[5] = devinfo[5];
                        mmsg[6] = 32;
                        mmsg[7] = (byte)(seq >> 7 & 0x7f);
                        mmsg[8] = (byte)(seq & 0x7f);
                        for(int len = 0; len < mmsg1.length; len++)
                            mmsg[9 + len] = mmsg1[len];

                        mmsg[9 + mmsg1.length] = -9;
                        seq++;
                        sysexMessage.setMessage(mmsg, mmsg.length);
                        rx.send(sysexMessage, -1L);
                        long was = System.currentTimeMillis();
                        long waitFor = 300L;
                        if(smsg.startsWith("$store"))
                            waitFor += 700L;
                        while(System.currentTimeMillis() - was < waitFor && semaphor.getLastUpdateTime() == -1L) 
                            try
                            {
                                sleep(2L);
                            }
                            catch(InterruptedException iE) { }
                        if(semaphor.getLastUpdateTime() != -1L)
                        {
                            String x = null;
                            do
                            {
                                byte xb[];
                                do
                                    if((xb = (byte[])semaphor.getNext()) == null)
                                        continue label1;
                                while(xb[xb.length - 2] == 0);
                                JOptionPane.showMessageDialog(new Frame(), "Error received:\n    \"" + BCLError.getErrorString(xb[xb.length - 2] & 0xff) + "\"\nwhile sending:\n    \"" + smsg + "\"\nWe are currently working on:" + "\n    \"" + aga + "\"", "Error in script", 0);
                                bTimeout = true;
                            } while(true);
                        }
                        JOptionPane.showMessageDialog(new Frame(), "Timeout Error\nwhile sending:\n    \"" + smsg + "\"\nWe are currently working on:" + "\n    \"" + aga + "\"", "Timeout error", 0);
                        bTimeout = true;
                    } while(true);
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
                listener.close();
                MidiOut.close();
            }
            controllerPanel.stopAnimation();
            controllerPanel.restoreState();
            if(!bTimeout && typeToSend != 0)
                (new BCInternalNotify(this, "CONTROLER:BuildPresetNames:Dummy", 20L)).start();
        }
    }

    MySysExMessage semaphor;
    BcSysExListener listener;
    javax.sound.midi.MidiDevice.Info infos[];
    MidiDevice MidiIn;
    MidiDevice MidiOut;
    MidiDevice device;
    BCPreset bcp;
    BCElement bce;
    MidiDeviceHolder mdh;
    ControllerPanel controllerPanel;
    EditorPanel editorPanel;
    BCActionListener actionListener;
    int typeToSend;
    int storeToFlash;
}
