// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCPresetHead.java

package bcedit.BCL;


// Referenced classes of package bcedit.BCL:
//            BCSerialize, BCL

public class BCPresetHead extends BCSerialize
{

    public BCPresetHead()
    {
        dummy = new int[4];
        name = new String();
        txpool = new int[128];
        setName("");
        flags = 33027;
        txreq = 0;
    }

    public String toString()
    {
        return name;
    }

    public boolean setName(String dName)
    {
        int i = 24 - dName.length();
        if(i < 0)
            name = dName.substring(0, 24);
        else
            name = dName;
        return true;
    }

    public boolean checkName(String dName)
    {
        if(dName.length() > 24)
            return false;
        char chararray[] = dName.toCharArray();
        for(int i = 0; i < chararray.length; i++)
            if((byte)(chararray[i] & 0xff) > 127 || (byte)(chararray[i] & 0xff) < 32)
                return false;

        return true;
    }

    public String getName()
    {
        return name.trim();
    }

    public String getScript()
    {
        String s = new String();
        s = s + BCL.GetToken(259);
        s = s + ";" + BCL.GetToken(517) + new String(" '");
        s = s + name;
        for(int i = 24 - name.length(); i-- != 0;)
            s = s + " ";

        s = s + new String("'");
        s = s + ";" + BCL.GetToken(520) + " ";
        s = s + BCL.GetToken((flags & 0x400) == 0 ? 768 : 769);
        s = s + ";" + BCL.GetToken(513) + " " + BCL.GetToken((flags & 0x200) == 0 ? 768 : 769);
        s = s + ";" + BCL.GetToken(515) + " " + Integer.toString((flags & 3) + 1);
        s = s + ";" + BCL.GetToken(516) + " " + BCL.GetToken((flags & 0x100) == 0 ? 768 : 769);
        s = s + ";" + BCL.GetToken(523) + " " + BCL.GetToken((flags & 0x800) == 0 ? 768 : 769);
        s = s + BCL.dumppool(544, txpool, txreq);
        s = s + ";" + BCL.GetToken(518);
        return s;
    }

    public int flags;
    public int txreq;
    public int dummy[];
    public String name;
    public int txpool[];
    public static final String checktok = "preset:";
}
