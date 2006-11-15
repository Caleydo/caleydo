// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import bcedit.BCL.BCDevice;
import bcedit.BCL.MsgBuilder;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.sound.midi.*;

// Referenced classes of package bcedit:
//            BCSettings, MySysExMessage, BcSysExListener, MidiDeviceHolder, 
//            ControllerPanel, BCActionListener

class ReadControllerFull extends Thread
{

    public ReadControllerFull(BCActionListener alistener, MidiDeviceHolder m, ControllerPanel p)
    {
        mdh = null;
        controllerPanel = null;
        actionListener = null;
        bTimeout = false;
        modelId = 0;
        vscript = null;
        fakeFileToLoad = null;
        actionListener = alistener;
        mdh = m;
        controllerPanel = p;
        modelId = m.getDeviceInfo()[5];
        vscript = new Vector();
    }

    public void run()
    {
        BCSettings settings = new BCSettings();
        fakeFileToLoad = settings.getValue("MidiDeviceFake");
        MySysExMessage semaphor = new MySysExMessage();
        BcSysExListener listener = null;
        javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
        MidiDevice MidiIn = null;
        MidiDevice MidiOut = null;
        MidiDevice device = null;
        controllerPanel.startAnimation();
        if(fakeFileToLoad != null)
        {
            try
            {
                BufferedReader in = new BufferedReader(new FileReader(fakeFileToLoad));
                try
                {
label0:
                    do
                    {
                        if(!in.ready())
                            break;
                        String line = in.readLine().trim();
                        if(!line.startsWith("$DEVICE"))
                        {
                            vscript.add(line);
                            continue;
                        }
                        StringTokenizer tokens = new StringTokenizer(line.substring(8), " ");
                        int offset = 0;
                        int num = 0;
                        do
                        {
                            if(!tokens.hasMoreElements())
                                continue label0;
                            try
                            {
                                num = Integer.parseInt(tokens.nextToken());
                            }
                            catch(NumberFormatException e)
                            {
                                num = 0;
                            }
                        } while(offset++ != 5);
                        modelId = num;
                    } while(true);
                    in.close();
                }
                catch(IOException e2)
                {
                    System.out.print("ReadControllerFull:Exception caught while reading: ");
                    System.out.print(e2.getMessage());
                    bTimeout = true;
                }
            }
            catch(FileNotFoundException e1)
            {
                System.out.println("ReadControllerFull:File \"" + fakeFileToLoad + "\" not found.");
                bTimeout = true;
            }
        } else
        {
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
                    listener.sequenceNumber = 0;
                    semaphor.reset();
                    byte mmsg[] = MsgBuilder.requestPreset(devinfo[4] & 0xff, devinfo[5] & 0xff, 126);
                    sysexMessage.setMessage(mmsg, mmsg.length);
                    rx.send(sysexMessage, -1L);
                    try
                    {
                        sleep(10L);
                    }
                    catch(InterruptedException iE) { }
                    for(long was = System.currentTimeMillis(); System.currentTimeMillis() - was < 200L && semaphor.getLastUpdateTime() == -1L;)
                        try
                        {
                            sleep(10L);
                        }
                        catch(InterruptedException iE) { }

                    if(semaphor.getLastUpdateTime() != -1L)
                    {
                        String x;
                        do
                        {
                            if(System.currentTimeMillis() - semaphor.getLastUpdateTime() >= 300L)
                                break;
                            try
                            {
                                sleep(1L);
                            }
                            catch(InterruptedException iE) { }
                            x = null;
                            byte xb[];
                            while((xb = (byte[])semaphor.getNext()) != null) 
                            {
                                x = new String(xb);
                                int l = x.length();
                                x = x.substring(9, l - 1);
                                vscript.add(x);
                            }
                        } while(x == null || !x.equals("$end"));
                        if(System.currentTimeMillis() - semaphor.getLastUpdateTime() >= 300L)
                            bTimeout = true;
                    } else
                    {
                        bTimeout = true;
                    }
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
        }
        controllerPanel.stopAnimation();
        if(!bTimeout)
        {
            controllerPanel.loadingDevId = modelId;
            controllerPanel.loadingDevice = vscript;
            controllerPanel.loadingBControl = new BCDevice(controllerPanel.loadingDevId);
            controllerPanel.loadingBControl.initFromScript(controllerPanel.loadingDevId, vscript);
        } else
        {
            controllerPanel.loadingDevId = 0;
            controllerPanel.loadingDevice = null;
            controllerPanel.loadingBControl = null;
        }
        controllerPanel.restoreState();
    }

    private MidiDeviceHolder mdh;
    private ControllerPanel controllerPanel;
    private BCActionListener actionListener;
    private boolean bTimeout;
    private int modelId;
    public Vector vscript;
    public String fakeFileToLoad;
}
