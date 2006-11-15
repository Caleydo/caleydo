// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import java.io.PrintStream;
import javax.sound.midi.*;

// Referenced classes of package bcedit:
//            MySysExMessage

class BcSysExListener
    implements Receiver
{

    public BcSysExListener(MidiDevice dev, MySysExMessage s)
    {
        bAddDeviceName = true;
        device = null;
        tx = null;
        sysexmsg = null;
        isActive = false;
        sequenceNumber = -1;
        iError = 0;
        reopen(dev, s);
    }

    public boolean reopen(MidiDevice dev, MySysExMessage s)
    {
        if(isActive)
            close();
        device = dev;
        sequenceNumber = -1;
        if(device == null)
            return isActive();
        if(s != null)
            sysexmsg = s;
        else
        if(sysexmsg == null)
            sysexmsg = new MySysExMessage();
        if(sysexmsg == null)
            return isActive;
        try
        {
            device.open();
        }
        catch(MidiUnavailableException MidiUnavailEx)
        {
            System.out.print("Error: ");
            System.out.println(MidiUnavailEx.getMessage());
            return isActive;
        }
        if(device == null)
            return false;
        if(device.getMaxTransmitters() == 0)
        {
            device.close();
            device = null;
            return isActive;
        }
        try
        {
            tx = device.getTransmitter();
        }
        catch(MidiUnavailableException MidiUnavailEx)
        {
            System.out.print("Error: ");
            System.out.println(MidiUnavailEx.getMessage());
            device.close();
            device = null;
            return isActive;
        }
        if(tx == null)
        {
            device.close();
            device = null;
            return isActive;
        } else
        {
            tx.setReceiver(this);
            isActive = true;
            return isActive;
        }
    }

    public MySysExMessage getSysExMessage()
    {
        return sysexmsg;
    }

    public boolean isActive()
    {
        return isActive;
    }

    public synchronized void send(MidiMessage message, long timeStamp)
    {
        if((message.getMessage()[0] & 0xff) == 240)
        {
            byte midimsg[] = message.getMessage();
            byte BCFRHead1[] = {
                -16, 0, 32, 50
            };
            int l;
            for(l = 0; l < BCFRHead1.length && BCFRHead1[l] == midimsg[l]; l++);
            if(l == BCFRHead1.length)
            {
                if(sequenceNumber >= 0)
                    if((midimsg[7] << 7 | midimsg[8]) == sequenceNumber)
                    {
                        sequenceNumber++;
                    } else
                    {
                        System.out.println("SequenceError! Expected " + sequenceNumber + " but read: " + (midimsg[7] << 7 | midimsg[8]));
                        iError = sequenceNumber + 1;
                    }
                if(iError == 0)
                {
                    if(bAddDeviceName)
                        sysexmsg.add(device.getDeviceInfo().getName());
                    sysexmsg.add(midimsg);
                }
            }
        }
    }

    public void close()
    {
        tx.setReceiver(null);
        device.close();
        device = null;
        tx = null;
        isActive = false;
    }

    public boolean setAddDeviceName(boolean b)
    {
        boolean bRet = bAddDeviceName;
        bAddDeviceName = b;
        return bRet;
    }

    private boolean bAddDeviceName;
    MidiDevice device;
    Transmitter tx;
    MySysExMessage sysexmsg;
    boolean isActive;
    public int sequenceNumber;
    private int iError;
}
