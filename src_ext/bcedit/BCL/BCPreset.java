// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCPreset.java

package bcedit.BCL;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.Adler32;

// Referenced classes of package bcedit.BCL:
//            BCSerialize, BCPresetHead, BCElement, BCL

public class BCPreset extends BCSerialize
{

    public BCPreset()
    {
        isReady = false;
        deviceType = -1;
        activeGroup = 1;
        preset = null;
        encoder = null;
        fader = null;
        button = null;
    }

    public BCPreset(int deviceID)
    {
        isReady = false;
        deviceType = -1;
        activeGroup = 1;
        preset = null;
        encoder = null;
        fader = null;
        button = null;
        init(deviceID);
    }

    public boolean init(int deviceID)
    {
        if(isReady)
            return false;
        switch(deviceID)
        {
        default:
            break;

        case 20: // '\024'
            preset = new BCPresetHead();
            encoder = new BCElement[32];
            fader = new BCElement[9];
            button = new BCElement[64];
            for(int cnt = 0; cnt < 32; cnt++)
                encoder[cnt] = new BCElement(cnt + 1, 0);

            for(int cnt = 0; cnt < 9; cnt++)
                fader[cnt] = new BCElement(cnt + 1, 2);

            for(int cnt = 0; cnt < 64; cnt++)
                button[cnt] = new BCElement(cnt + 1, 1);

            isReady = true;
            deviceType = 20;
            break;

        case 21: // '\025'
            preset = new BCPresetHead();
            encoder = new BCElement[56];
            fader = new BCElement[0];
            button = new BCElement[64];
            for(int cnt = 0; cnt < 56; cnt++)
                encoder[cnt] = new BCElement(cnt + 1, 0);

            for(int cnt = 0; cnt < 0; cnt++)
                fader[cnt] = new BCElement(cnt + 1, 2);

            for(int cnt = 0; cnt < 64; cnt++)
                button[cnt] = new BCElement(cnt + 1, 1);

            isReady = true;
            deviceType = 21;
            break;
        }
        return isReady;
    }

    public String toString()
    {
        String s = "";
        if(presetNumber < 10)
            s = "0";
        s = s + Integer.toString(presetNumber) + ": " + preset.getName();
        return s;
    }

    public int getModelID()
    {
        return deviceType;
    }

    public boolean isReady()
    {
        return isReady;
    }

    public boolean isEmpty()
    {
        for(int i = 0; i < encoder.length; i++)
            if((encoder[i].flags & 0x8000) == 32768)
                return false;

        for(int i = 0; i < button.length; i++)
            if((button[i].flags & 0x8000) == 32768)
                return false;

        for(int i = 0; i < fader.length; i++)
            if((fader[i].flags & 0x8000) == 32768)
                return false;

        return true;
    }

    public Vector getElementAsList()
    {
        Vector v1 = new Vector();
        int j = 0;
        String eType[] = {
            "Enc", "Btn", "Fdr"
        };
        for(j = 0; j <= 2; j++)
        {
            int i = 0;
            for(BCElement bce = getElement(i++, j); bce != null; bce = getElement(i++, j))
            {
                String toAdd;
                if(i < 10)
                    toAdd = eType[j] + " 0" + Integer.toString(i);
                else
                    toAdd = eType[j] + " " + Integer.toString(i);
                if((bce.flags & 0x8000) != 32768)
                {
                    toAdd = toAdd + " (unused)";
                } else
                {
                    String sEasy = bce.getEasy();
                    if(sEasy != null)
                        toAdd = toAdd + " " + sEasy;
                }
                v1.add(toAdd);
            }

        }

        return v1;
    }

    public Vector getElementAsList(String pathToElementLibrary, String pathSeperator)
    {
        Vector v1 = new Vector();
        int j = 0;
        String eType[] = {
            "Enc", "Btn", "Fdr"
        };
        j = 0;
        do
        {
            if(j > 2)
                break;
            int i = 0;
            for(BCElement bce = getElement(i++, j); bce != null; bce = getElement(i++, j))
            {
                String toAdd;
                if(i < 10)
                    toAdd = eType[j] + " 0" + Integer.toString(i);
                else
                    toAdd = eType[j] + " " + Integer.toString(i);
                if((bce.flags & 0x8000) != 32768)
                {
                    toAdd = toAdd + " (unused)";
                } else
                {
                    long crc = bce.getCRC();
                    String sEasy = null;
                    File fe = new File(pathToElementLibrary + pathSeperator + Long.toString(crc) + ".bce");
                    if(fe.exists())
                        try
                        {
                            BufferedReader in = new BufferedReader(new FileReader(fe));
                            StringTokenizer tok = new StringTokenizer(in.readLine(), ";");
                            String n = null;
                            do
                            {
                                if(!tok.hasMoreElements())
                                    break;
                                String s1 = tok.nextToken();
                                if(!s1.startsWith(".name "))
                                    continue;
                                int spos = s1.indexOf(' ');
                                sEasy = s1.substring(spos + 1).trim();
                                break;
                            } while(true);
                            in.close();
                        }
                        catch(Exception e) { }
                    else
                        sEasy = bce.getEasy();
                    if(sEasy != null)
                        toAdd = toAdd + " " + sEasy;
                }
                v1.add(toAdd);
            }

            j++;
        } while(true);
        return v1;
    }

    public BCPresetHead getPreset()
    {
        return preset;
    }

    public BCElement[] getEncoders()
    {
        return encoder;
    }

    public BCElement[] getFaders()
    {
        return fader;
    }

    public BCElement[] getButtons()
    {
        return button;
    }

    public BCElement getEncoder(int i)
    {
        switch(deviceType)
        {
        default:
            break;

        case 20: // '\024'
            if(i >= 0 && i < 32)
                return encoder[i];
            break;

        case 21: // '\025'
            if(i >= 0 && i < 56)
                return encoder[i];
            break;
        }
        return null;
    }

    public BCElement getFader(int i)
    {
        switch(deviceType)
        {
        default:
            break;

        case 20: // '\024'
            if(i >= 0 && i < 9)
                return fader[i];
            break;

        case 21: // '\025'
            if(i >= 0 && i < 0)
                return fader[i];
            break;
        }
        return null;
    }

    public BCElement getButton(int i)
    {
        switch(deviceType)
        {
        default:
            break;

        case 20: // '\024'
            if(i >= 0 && i < 64)
                return button[i];
            break;

        case 21: // '\025'
            if(i >= 0 && i < 64)
                return button[i];
            break;
        }
        return null;
    }

    public BCElement getElement(int i, String elementType)
    {
        if(elementType.equals("BUTTON"))
            return getButton(i);
        if(elementType.equals("FADER"))
            return getFader(i);
        if(elementType.equals("ENCODER"))
            return getEncoder(i);
        else
            return null;
    }

    public BCElement getElement(int i, int elementType)
    {
        switch(elementType)
        {
        case 2: // '\002'
            return getFader(i);

        case 1: // '\001'
            return getButton(i);

        case 0: // '\0'
            return getEncoder(i);
        }
        return null;
    }

    public boolean setElement(BCElement bce)
    {
        boolean retval = false;
        switch(bce.ctrlType)
        {
        default:
            break;

        case 2: // '\002'
            if(bce.id > 0 && fader.length > bce.id - 1)
            {
                fader[bce.id - 1] = bce;
                retval = true;
            }
            break;

        case 1: // '\001'
            if(bce.id > 0 && button.length > bce.id - 1)
            {
                button[bce.id - 1] = bce;
                retval = true;
            }
            break;

        case 0: // '\0'
            if(bce.id > 0 && encoder.length > bce.id - 1)
            {
                encoder[bce.id - 1] = bce;
                retval = true;
            }
            break;
        }
        return retval;
    }

    public long getCRC()
    {
        String s = getScript(0, false);
        if(s == null)
        {
            return 0L;
        } else
        {
            byte b[] = s.getBytes();
            Adler32 c = new Adler32();
            c.update(b);
            return c.getValue();
        }
    }

    public String getSaveScript()
    {
        return "$deviceType " + Integer.toString(deviceType) + ";" + getScript(0, false);
    }

    public BCPreset getCopy()
    {
        String s = BCL.GetToken(257) + " " + BCL.getRevFromId(deviceType) + ";" + getScript(0, true);
        if(!s.endsWith(";"))
            s = s + ";";
        s = s + BCL.GetToken(256) + ";";
        return BCL.initPresetFromScript(s);
    }

    public String getScript(int store, boolean bAddName)
    {
        if(!isReady())
            return null;
        String s = preset.getScript();
        s = s + ";";
        for(int i = 0; i < encoder.length; i++)
        {
            String e;
            if((e = encoder[i].getScript(bAddName)) != null)
            {
                s = s + e;
                s = s + ";";
            }
        }

        for(int i = 0; i < button.length; i++)
        {
            String e;
            if((e = button[i].getScript(bAddName)) != null)
            {
                s = s + e;
                s = s + ";";
            }
        }

        for(int i = 0; i < fader.length; i++)
        {
            String e;
            if((e = fader[i].getScript(bAddName)) != null)
            {
                s = s + e;
                s = s + ";";
            }
        }

        if(store != 0)
            s = s + BCL.GetToken(265) + " " + Integer.toString(store) + ";";
        return s;
    }

    private boolean isReady;
    private int deviceType;
    public int activeGroup;
    public int presetNumber;
    private BCPresetHead preset;
    private BCElement encoder[];
    private BCElement fader[];
    private BCElement button[];
}
