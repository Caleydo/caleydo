// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import java.awt.Frame;
import java.io.PrintStream;
import java.util.Vector;
import javax.sound.midi.*;
import javax.swing.JOptionPane;

// Referenced classes of package bcedit:
//            BcSysExListener, BCInternalNotify, MidiDeviceHolder, MySysExMessage, 
//            BCActionListener

class performSysExRequest extends Thread
{

    public performSysExRequest(BCActionListener bcActionListener, MidiDeviceHolder midiDeviceHolder, MySysExMessage mySysExMessage, Vector message, int sequnceStartNumber)
    {
        mdh = null;
        actionListener = null;
        semaphor = null;
        v = null;
        actionListener = bcActionListener;
        mdh = midiDeviceHolder;
        semaphor = mySysExMessage;
        v = message;
        startSequence = sequnceStartNumber;
    }

    public void run()
    {
        MidiDevice MidiIn = null;
        MidiDevice MidiOut = null;
        MidiDevice device = null;
        javax.sound.midi.MidiDevice.Info infos[] = MidiSystem.getMidiDeviceInfo();
        BcSysExListener listener = null;
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
            SysexMessage sysexMessage = new SysexMessage();
            listener = new BcSysExListener(MidiIn, semaphor);
            listener.sequenceNumber = startSequence;
            listener.setAddDeviceName(false);
            try
            {
                MidiOut.open();
                Receiver rx = MidiOut.getReceiver();
                listener.sequenceNumber = startSequence;
                int i = 0;
label0:
                do
                {
                    if(i >= v.size())
                        break;
                    int WaitForAnswer = 0;
                    byte compareEnd[] = null;
                    Object o = v.get(i++);
                    if(o instanceof Integer)
                    {
                        WaitForAnswer = ((Integer)o).intValue();
                        o = v.get(i++);
                    } else
                    if((o instanceof byte[]) && ((byte[])o)[0] != -16)
                    {
                        WaitForAnswer = 0x7fffffff;
                        compareEnd = (byte[])o;
                        o = v.get(i++);
                    }
                    if(!(o instanceof byte[]))
                        continue;
                    byte bmsg[] = new byte[((byte[])o).length + 1];
                    bmsg[0] = 0;
                    for(int _x = 0; _x < ((byte[])o).length; _x++)
                        bmsg[1 + _x] = ((byte[])o)[_x];

                    semaphor.add(bmsg);
                    int len = semaphor.length();
                    sysexMessage.setMessage((byte[])o, ((byte[])o).length);
                    rx.send(sysexMessage, -1L);
                    try
                    {
                        sleep(((byte[])o).length / 3);
                    }
                    catch(InterruptedException iE) { }
label1:
                    do
                    {
                        if(WaitForAnswer <= 0)
                            continue label0;
                        for(long was = System.currentTimeMillis(); System.currentTimeMillis() - was < 300L && len == semaphor.length();)
                            try
                            {
                                sleep(3L);
                            }
                            catch(InterruptedException iE) { }

                        if(len == semaphor.length())
                            break;
                        WaitForAnswer--;
                        len = semaphor.length();
                        if(compareEnd == null || !(semaphor.peekLast() instanceof byte[]))
                            continue;
                        int checkpos = compareEnd[0];
                        int checklen = 1;
                        byte lmsg[] = (byte[])semaphor.peekLast();
                        do
                            if((compareEnd[checklen] & 0xffffff80) == 0 && compareEnd[checklen] != lmsg[(checkpos + checklen) - 1])
                                continue label1;
                        while(++checklen != compareEnd.length);
                        WaitForAnswer = 0;
                    } while(true);
                    JOptionPane.showMessageDialog(new Frame(), "The device is not responding anymore...", "Timeout", 0);
                    semaphor.reset();
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
            try
            {
                listener.close();
                MidiOut.close();
            }
            catch(Exception eio1)
            {
                System.out.println("Midi Unavailable?");
            }
        }
        BCInternalNotify notify = new BCInternalNotify(semaphor, "SysExRequestPerformed");
        notify.start();
    }

    public static final int infinite = 0x7fffffff;
    private MidiDeviceHolder mdh;
    private BCActionListener actionListener;
    private MySysExMessage semaphor;
    private Vector v;
    private int startSequence;
}
