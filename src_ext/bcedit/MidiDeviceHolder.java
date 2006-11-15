// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MidiDeviceHolder.java

package bcedit;


public class MidiDeviceHolder
{

    public MidiDeviceHolder()
    {
        strOutDevice = null;
        strInDevice = null;
        returnedDescription = null;
    }

    public MidiDeviceHolder(String OutName, String InName, byte devdesc[])
    {
        strOutDevice = OutName;
        strInDevice = InName;
        returnedDescription = devdesc;
    }

    public String toString()
    {
        String s = new String(returnedDescription, 7, returnedDescription.length - 8);
        s = s + " ID:";
        s = s + Integer.toString(returnedDescription[4] + 1);
        return s;
    }

    public String getInDeviceName()
    {
        return strInDevice;
    }

    public String getOutDeviceName()
    {
        return strOutDevice;
    }

    public byte[] getDeviceInfo()
    {
        return returnedDescription;
    }

    String strOutDevice;
    String strInDevice;
    byte returnedDescription[];
}
