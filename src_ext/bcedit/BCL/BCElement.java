// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCElement.java

package bcedit.BCL;

import java.io.PrintStream;
import java.util.zip.Adler32;

// Referenced classes of package bcedit.BCL:
//            BCSerialize, BCL

public class BCElement extends BCSerialize
{

    public BCElement(int newId, int type)
    {
        name = "";
        rxeasy = new int[4];
        easypar = new int[6];
        txpool = new int[128];
        resolution = new int[4];
        init(newId, type);
    }

    public BCElement()
    {
        name = "";
        rxeasy = new int[4];
        easypar = new int[6];
        txpool = new int[128];
        resolution = new int[4];
        init(0, -1);
    }

    public void init(int newId, int type)
    {
        id = newId;
        ctrlType = type;
        flags = min = max = init = tx = easy = 0;
        for(int cnt = 0; cnt < 128; cnt++)
        {
            if(cnt < 4)
            {
                rxeasy[cnt] = 0;
                resolution[cnt] = 0;
            }
            if(cnt < 6)
                easypar[cnt] = 0;
            txpool[cnt] = 0;
        }

    }

    private String _getValue(int value)
    {
        return Integer.toString(value);
    }

    private String _getValueOff(int value)
    {
        if(value == 0)
            return new String("off");
        else
            return Integer.toString(value - 1);
    }

    private String _getValueAll(int value)
    {
        if(value == 0)
            return new String("all");
        else
            return Integer.toString(value - 1);
    }

    private String getEasy_PC()
    {
        String s = _getValueOff(easypar[2]) + " ";
        s = s + _getValueOff(easypar[3]) + " ";
        if(ctrlType == 1)
            s = s + _getValueOff(easypar[4]) + " ";
        return s;
    }

    private String getEasy_CCNRPN()
    {
        String s = _getValue(easypar[2]) + " ";
        s = s + _getValue(easypar[3]) + " ";
        if(ctrlType == 1)
        {
            if((easypar[5] & 2) == 0)
                s = s + _getValueOff(easypar[4]) + " ";
            else
                s = s + _getValue(easypar[4]) + " ";
            if(easypar[5] < 2)
            {
                s = s + BCL.GetToken(easypar[5] + 1296) + " ";
            } else
            {
                s = s + BCL.GetToken(1298) + " ";
                int value = easypar[5] >> 2;
                if(value > 127)
                    value = -(256 - value);
                s = s + _getValue(value) + " ";
            }
        } else
        {
            s = s + _getValue(easypar[4]) + " ";
            s = s + BCL.GetToken(easypar[5] + 1312) + " ";
        }
        return s;
    }

    private String getEasy_NOTE()
    {
        String s = _getValue(easypar[2]) + " ";
        s = s + _getValue(easypar[3]) + " ";
        s = s + BCL.GetToken((easypar[5] & 1) + 1296) + " ";
        return s;
    }

    private String getEasy_PB()
    {
        String s = _getValue(easypar[3]) + " ";
        return s;
    }

    private String getEasy_AT()
    {
        String s = _getValueAll(easypar[2]) + " ";
        s = s + _getValue(easypar[3]) + " ";
        if(ctrlType == 1)
        {
            if((easypar[5] & 2) == 0)
                s = s + _getValueOff(easypar[4]) + " ";
            else
                s = s + _getValue(easypar[4]) + " ";
            if(easypar[5] < 2)
            {
                s = s + BCL.GetToken(easypar[5] + 1296) + " ";
            } else
            {
                s = s + BCL.GetToken(1298) + " ";
                int value = easypar[5] >> 2;
                if(value > 127)
                    value = -(256 - value);
                s = s + _getValue(value) + " ";
            }
        } else
        {
            s = s + _getValue(easypar[4]) + " ";
        }
        return s;
    }

    private String getEasy_MMC()
    {
        String s = _getValueAll(easypar[1]) + " ";
        s = s + BCL.GetToken(easypar[2] + 1328) + " ";
        if(easypar[5] < 0 || easypar[5] > 4)
            easypar[5] = 0;
        if(easypar[5] != 0)
        {
            int fps[] = {
                0, 24, 25, 30, 30
            };
            int fpsidx = easypar[5];
            if(fpsidx >= fps.length)
                fpsidx = 0;
            int value = easypar[3] / 60;
            String sTmp = Integer.toString(value);
            if(sTmp.length() < 2)
                sTmp = "0" + sTmp;
            s = s + sTmp + ":";
            value = easypar[3] % 60;
            sTmp = Integer.toString(value);
            if(sTmp.length() < 2)
                sTmp = "0" + sTmp;
            s = s + sTmp + ":";
            try
            {
                value = easypar[4] / fps[fpsidx];
            }
            catch(Exception nan1)
            {
                value = 0;
            }
            sTmp = Integer.toString(value);
            if(sTmp.length() < 2)
                sTmp = "0" + sTmp;
            s = s + sTmp + ".";
            try
            {
                value = easypar[4] % fps[fpsidx];
            }
            catch(Exception nan2)
            {
                value = 0;
            }
            sTmp = Integer.toString(value);
            if(sTmp.length() < 2)
                sTmp = "0" + sTmp;
            s = s + sTmp + " ";
        } else
        {
            s = s + "00:00:00.00 ";
        }
        s = s + BCL.GetToken(easypar[5] + 1360) + " ";
        return s;
    }

    private String getEasy_GSXG()
    {
        String s = BCL.GetToken(easypar[2] + 1344) + " ";
        s = s + _getValue(easypar[3]) + " ";
        if(ctrlType == 1)
        {
            s = s + _getValueOff(easypar[4]) + " ";
            s = s + BCL.GetToken((easypar[5] & 1) + 1296) + " ";
        } else
        {
            s = s + _getValue(easypar[4]) + " ";
        }
        return s;
    }

    public String getEasy()
    {
        if((flags & 0x8000) != 0 && easy != 0)
        {
            String s = new String("");
            s = s + BCL.GetToken(1279 + easypar[0]) + " ";
            if(easypar[0] != 7)
                s = s + _getValue(easypar[1] + 1) + " ";
            switch(easypar[0])
            {
            case 1: // '\001'
                s = s + getEasy_PC();
                break;

            case 2: // '\002'
            case 3: // '\003'
                s = s + getEasy_CCNRPN();
                break;

            case 4: // '\004'
                s = s + getEasy_NOTE();
                break;

            case 5: // '\005'
                s = s + getEasy_PB();
                break;

            case 6: // '\006'
                s = s + getEasy_AT();
                break;

            case 7: // '\007'
                s = s + getEasy_MMC();
                break;

            case 8: // '\b'
                s = s + getEasy_GSXG();
                break;
            }
            return s.trim();
        } else
        {
            return null;
        }
    }

    public long getCRC()
    {
        String s = getScript(false);
        if(s == null)
            return 0L;
        int idx1 = s.indexOf(" ");
        int idx2 = s.indexOf(";");
        byte b[] = s.getBytes();
        for(int i = idx1 + 1; i < idx2; i++)
            b[i] = 32;

        Adler32 c = new Adler32();
        c.update(b);
        return c.getValue();
    }

    public String getScript(boolean bAddName)
    {
        return getScript(bAddName, ";");
    }

    public String getScript(boolean bAddName, String lineBreak)
    {
        if((flags & 0x8000) == 0)
            return null;
        if(id == 0)
        {
            System.out.println("BCElement::getScript() called, but ID not set!");
            return null;
        }
        if(ctrlType == -1)
        {
            System.out.println("BCElement::getScript() called, ctrlType is undefined!");
            return null;
        }
        String nid;
        switch(ctrlType)
        {
        case 0: // '\0'
            nid = BCL.GetToken(260);
            break;

        case 1: // '\001'
            nid = BCL.GetToken(261);
            break;

        case 2: // '\002'
            nid = BCL.GetToken(262);
            break;

        default:
            return null;
        }
        nid = nid + " " + Integer.toString(id);
        String s = new String(nid);
        if(bAddName && name != null && name.trim().length() > 0)
        {
            s = s + lineBreak + BCL.GetToken(517) + " '";
            if(name.length() == 0)
                s = s + nid;
            else
                s = s + name;
            s = s + "'";
        }
        String sEasy;
        if((sEasy = getEasy()) != null)
            s = s + lineBreak + BCL.GetToken(557) + " " + sEasy;
        s = s + lineBreak + BCL.GetToken(556) + " " + BCL.GetToken((flags & 0x2000) != 0 ? 769 : 768);
        switch(ctrlType)
        {
        default:
            break;

        case 0: // '\0'
        {
            int tok;
            if((flags & 0xf) == 0)
                tok = 768;
            else
                tok = 783 + (flags & 0xf);
            s = s + lineBreak + BCL.GetToken(549) + " " + BCL.GetToken(tok);
            if(easy != 0)
                return s;
            s = s + lineBreak + BCL.GetToken(547);
            for(int i = 0; i < 4; i++)
                s = s + " " + _getValue(resolution[i]);

            s = s + lineBreak + BCL.GetToken(548) + " " + _getValue(min) + " " + _getValue(max);
            s = s + lineBreak + BCL.GetToken(554) + " ";
            if((flags & 0x1000) == 0)
                s = s + BCL.GetToken(768);
            else
                s = s + _getValue(init);
            s = s + BCL.dumppool(544, txpool, tx);
            break;
        }

        case 1: // '\001'
        {
            if(easy != 0)
                return s;
            s = s + BCL.GetToken(549) + " ";
            if((flags & 0x400) != 0)
                s = s + BCL.GetToken(801);
            else
            if((flags & 0x200) != 0)
                s = s + BCL.GetToken(802);
            else
                s = s + BCL.GetToken(800);
            s = s + lineBreak + BCL.GetToken(548) + " " + Integer.toString(min) + " " + Integer.toString(max);
            s = s + lineBreak + BCL.GetToken(554) + " ";
            if((flags & 0x1000) != 0)
                s = s + Integer.toString(init);
            else
                s = s + BCL.GetToken(768);
            s = s + BCL.dumppool(544, txpool, tx);
            break;
        }

        case 2: // '\002'
        {
            s = s + lineBreak + BCL.GetToken(552) + " " + BCL.GetToken((flags & 0x200) == 0 ? 768 : 769);
            s = s + lineBreak + BCL.GetToken(551) + " " + BCL.GetToken((flags & 0x400) == 0 ? 817 : 818);
            s = s + lineBreak + BCL.GetToken(558) + " ";
            int tok = flags & 0x7f;
            if((flags & 0x100) != 0)
            {
                if(tok > 7)
                    tok = tok + 24 + 1;
                else
                    tok++;
                s = s + Integer.toString(tok);
            } else
            {
                s = s + BCL.GetToken(768);
            }
            if(easy != 0)
                return s;
            s = s + lineBreak + BCL.GetToken(548) + " " + Integer.toString(min) + " " + Integer.toString(max);
            s = s + lineBreak + BCL.GetToken(554) + " ";
            if((flags & 0x1000) != 0)
                s = s + Integer.toString(init);
            else
                s = s + BCL.GetToken(768);
            s = s + BCL.dumppool(544, txpool, tx);
            break;
        }
        }
        return s;
    }

    public String toString()
    {
        boolean bGenName = false;
        if(name == null)
            bGenName = true;
        else
        if(name.trim().length() == 0)
            bGenName = true;
        else
            return name;
        String n;
        switch(ctrlType)
        {
        case -1: 
            n = "Unk ";
            break;

        case 0: // '\0'
            n = "Enc ";
            break;

        case 1: // '\001'
            n = "Btn ";
            break;

        case 2: // '\002'
            n = "Fdr ";
            break;

        default:
            n = "    ";
            break;
        }
        n = n + _getValue(id);
        String n1;
        if((n1 = getEasy()) != null)
            n = n + ": " + n1;
        return n;
    }

    public int id;
    public int ctrlType;
    public String name;
    public int flags;
    public int min;
    public int max;
    public int init;
    public int tx;
    public int easy;
    public int rxeasy[];
    public int easypar[];
    public int txpool[];
    public int resolution[];
}
