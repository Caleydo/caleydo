// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCGlobal.java

package bcedit.BCL;

import java.util.StringTokenizer;

// Referenced classes of package bcedit.BCL:
//            BCSerialize, BCL

public class BCGlobal extends BCSerialize
{

    public String toString()
    {
        return toString(36);
    }

    public String toString(int radix)
    {
        if(radix < 2 || radix > 36 || Model_Id < 0)
            return null;
        String rStr = "global:" + Integer.toString(radix);
        rStr = rStr + "," + Integer.toString(Model_Id, radix);
        rStr = rStr + "," + Integer.toString(flags, radix);
        rStr = rStr + "," + Integer.toString(midimode, radix);
        rStr = rStr + "," + Integer.toString(startup, radix);
        rStr = rStr + "," + Integer.toString(rxch, radix);
        rStr = rStr + "," + Integer.toString(deviceid, radix);
        for(int cnt = 0; cnt < fadermin.length; cnt++)
            rStr = rStr + "," + Integer.toString(fadermin[cnt], radix);

        for(int cnt = 0; cnt < fadermax.length; cnt++)
            rStr = rStr + "," + Integer.toString(fadermax[cnt], radix);

        rStr = rStr + "," + Integer.toString(txinterval, radix);
        for(int cnt = 0; cnt < dummy.length; cnt++)
            rStr = rStr + "," + Integer.toString(dummy[cnt], radix);

        return rStr;
    }

    public boolean init(String rStr)
    {
        int radix;
        StringTokenizer t;
        radix = -1;
        String s = null;
        t = null;
        if(!rStr.startsWith("global:")) {
        	return false;        
            //break MISSING_BLOCK_LABEL_311;
        }
        
        s = rStr.substring("global:".length());
        t = new StringTokenizer(s);
        int f_Id;
        radix = Integer.parseInt(t.nextToken());
        f_Id = Integer.parseInt(t.nextToken(), radix);
        
        if(Model_Id < 0) {
        	//goto _L2; else goto _L1
        
_L1:
        //f_Id;
        //JVM INSTR lookupswitch 2: default 106
        
        switch ( f_Id ) {
    //                   20: 92
    //                   21: 99;
//           goto _L3 _L4 _L5
//_L4:
        case 0:
        initFader();
        break; /* Loop/switch isn't completed */
//_L5:
//        case 1:
//        initRotary();
//        break; /* Loop/switch isn't completed */
//_L3:
//        return false;
        
//        default: 106
//_L2:
        }
        
        if(Model_Id != f_Id)
            return false;
        
        try
        {
            flags = Integer.parseInt(t.nextToken(), radix);
            midimode = Integer.parseInt(t.nextToken(), radix);
            startup = Integer.parseInt(t.nextToken(), radix);
            rxch = Integer.parseInt(t.nextToken(), radix);
            deviceid = Integer.parseInt(t.nextToken(), radix);
            for(int cnt = 0; cnt < fadermin.length; cnt++)
                fadermin[cnt] = Integer.parseInt(t.nextToken(), radix);

            for(int cnt = 0; cnt < fadermax.length; cnt++)
                fadermax[cnt] = Integer.parseInt(t.nextToken(), radix);

            txinterval = Integer.parseInt(t.nextToken(), radix);
            for(int cnt = 0; cnt < dummy.length; cnt++)
                dummy[cnt] = Integer.parseInt(t.nextToken(), radix);

            return true;
        }
        catch(NumberFormatException e)
        {
            return false;
        }
        
        
        }
        return false;
    }

    public BCGlobal(int deviceIdChar)
    {
        fadermin = null;
        fadermax = null;
        dummy = new int[4];
        Model_Id = -1;
        switch(deviceIdChar)
        {
        case 20: // '\024'
            initFader();
            break;

//        case 21: // '\025'
//            initRotary();
//            break;
        }
    }

    public void initRotary()
    {
        Model_Id = 21;
        fadermin = new int[1];
        fadermax = new int[1];
    }

    public void initFader()
    {
        Model_Id = 20;
        fadermin = new int[9];
        fadermax = new int[9];
    }

    public int getModelId()
    {
        return Model_Id;
    }

    public String getScript()
    {
        String s = "";
        s = s + BCL.GetToken(258) + ";";
        return s;
    }

    public static final int GLOBAL_MIDIMODES = 8;
    public static final int GLOBAL_MAXDEVICEID = 16;
    public static final int GLOBAL_footsw_mask = 3;
    public static final int GLOBAL_footsw_inv = 1;
    public static final int GLOBAL_footsw_auto = 2;
    public static final int GLOBAL_uselast = 256;
    public static final int GLOBAL_midirxon = 512;
    public static final int SINGLEPOOLSIZE = 128;
    public static final int NAMELENGTH = 24;
    public int flags;
    public int midimode;
    public int startup;
    public int rxch;
    public int deviceid;
    public int fadermin[];
    public int fadermax[];
    public int txinterval;
    public int dummy[];
    private int Model_Id;
    public static final String checktok = "global:";
       
}
