// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCDevice.java

package bcedit.BCL;

import java.io.PrintStream;
import java.util.StringTokenizer;
import java.util.Vector;

// Referenced classes of package bcedit.BCL:
//            BCSerialize, BCPreset, BCGlobal, BCPresetHead, 
//            BCL, BCElement

public class BCDevice extends BCSerialize
{

    public BCDevice(int modelId)
    {
        use_radix = 10;
        bcGlobal = null;
        bcPreset = new BCPreset[33];
        modelID = -1;
        activeType = -1;
        actControlType = -1;
        actControl = -1;
        reinit(modelId);
    }

    public void reinit(int modelId)
    {
        if(modelId == 20 || modelId == 21)
        {
            modelID = modelId;
            bcGlobal = new BCGlobal(modelId);
            for(int cnt = 0; cnt < 33; cnt++)
            {
                bcPreset[cnt] = new BCPreset(modelId);
                bcPreset[cnt].presetNumber = cnt;
            }

        }
    }

    public boolean isReady()
    {
        return bcGlobal != null && (bcGlobal.getModelId() == 20 || bcGlobal.getModelId() == 21);
    }

    public int getModelID()
    {
        return modelID;
    }

    public int getMaxButtonNumber()
    {
        switch(modelID)
        {
        case 20: // '\024'
            return 64;

        case 21: // '\025'
            return 64;
        }
        return -1;
    }

    public int getMaxFaderNumber()
    {
        switch(modelID)
        {
        case 20: // '\024'
            return 9;

        case 21: // '\025'
            return 0;
        }
        return -1;
    }

    public int getMaxEncoderNumber()
    {
        switch(modelID)
        {
        case 20: // '\024'
            return 32;

        case 21: // '\025'
            return 56;
        }
        return -1;
    }

    public boolean setPresetName(int id, String s)
    {
        if(id >= 0 && id < 33)
            return bcPreset[id].getPreset().setName(s);
        else
            return false;
    }

    public String getPresetName(int id)
    {
        if(id >= 0 && id < 33)
            return (new String(bcPreset[id].getPreset().name)).trim();
        else
            return null;
    }

    public boolean setPreset(int id, BCPreset bcp)
    {
        if(id >= 0 && id < 33)
        {
            bcPreset[id] = bcp;
            bcp.presetNumber = id;
            return true;
        } else
        {
            return false;
        }
    }

    public BCGlobal getGlobal()
    {
        return bcGlobal;
    }

    public BCPreset getPreset(int id)
    {
        if(id >= 0 && id < 33)
            return bcPreset[id];
        else
            return null;
    }

    private int evalCmd(String tmp[])
    {
        int expectedParameter = -1;
        int retval = 0;
        int numTok = BCL.GetToken(tmp[0]);
        switch(numTok)
        {
        case 257: 
            activeType = numTok;
            expectedParameter = 1;
            break;

        case 258: 
            activeType = numTok;
            expectedParameter = 0;
            break;

        case 259: 
            activeType = numTok;
            expectedParameter = 0;
            break;

        case 260: 
            activeType = numTok;
            expectedParameter = 1;
            break;

        case 261: 
            activeType = numTok;
            expectedParameter = 1;
            break;

        case 262: 
            activeType = numTok;
            expectedParameter = 1;
            break;

        case 265: 
            activeType = numTok;
            expectedParameter = 1;
            break;

        case 266: 
            activeType = numTok;
            expectedParameter = 1;
            break;

        case 256: 
            activeType = numTok;
            expectedParameter = 0;
            break;

        case 263: 
        case 264: 
        default:
            expectedParameter = -1;
            break;
        }
        if(expectedParameter == 1)
        {
            try
            {
                retval = Integer.parseInt(tmp[expectedParameter]);
            }
            catch(NumberFormatException nfe)
            {
                retval = 0x80000000;
            }
            switch(activeType)
            {
            case 263: 
            case 264: 
            default:
                break;

            case 260: 
                actControlType = 0;
                actControl = retval - 1;
                break;

            case 261: 
                actControlType = 1;
                actControl = retval - 1;
                break;

            case 262: 
                actControlType = 2;
                actControl = retval - 1;
                break;

            case 265: 
                if(retval != 0x80000000)
                {
                    bcPreset[retval] = bcPreset[0];
                    bcPreset[0] = new BCPreset(bcPreset[retval].getModelID());
                }
                break;
            }
        }
        return retval;
    }

    private int check_midimode(String tmp[])
    {
        if(activeType == 258)
        {
            if(tmp.length < 2)
                return 3;
            int value = BCL.readvaluetoken(tmp[1]);
            if(value >= 8 && value <= 16)
                bcGlobal.midimode = value - 8;
            else
                return 11;
        } else
        {
            return 23;
        }
        return 0;
    }

    private int check_request(String tmp[])
    {
        if(activeType == 259)
        {
            if(tmp.length < 2)
                return 3;
            switch(BCL.readvaluetoken(tmp[1]) & 0xfcff)
            {
            case 0: // '\0'
                bcPreset[0].getPreset().flags &= 0xfffffdff;
                return 0;

            case 1: // '\001'
                bcPreset[0].getPreset().flags |= 0x200;
                return 0;
            }
        } else
        {
            return 23;
        }
        return 12;
    }

    private int check_startup(String tmp[])
    {
        if(activeType == 258)
        {
            if(tmp.length < 2)
            {
                return 3;
            } else
            {
                int value = BCL.readvaluetoken(tmp[1]);
                bcGlobal.startup = value;
                return 0;
            }
        } else
        {
            return 23;
        }
    }

    private int check_egroups(String tmp[])
    {
        if(activeType == 259)
        {
            if(tmp.length < 2)
            {
                return 3;
            } else
            {
                int value = BCL.readvaluetoken(tmp[1], 0) + 1;
                bcPreset[0].getPreset().flags &= -4;
                bcPreset[0].getPreset().flags |= value - 1 & 3;
                return 0;
            }
        } else
        {
            return 23;
        }
    }

    private int check_fkeys(String tmp[])
    {
        if(activeType == 259)
        {
            if(tmp.length < 2)
                return 3;
            switch(BCL.readvaluetoken(tmp[1]) & 0xfcff)
            {
            case 0: // '\0'
                bcPreset[0].getPreset().flags &= 0xfffffeff;
                return 0;

            case 1: // '\001'
                bcPreset[0].getPreset().flags |= 0x100;
                return 0;
            }
        } else
        {
            return 23;
        }
        return 12;
    }

    private int check_name(String tmp[])
    {
        if(activeType == 259)
        {
            if(tmp.length < 2)
            {
                return 3;
            } else
            {
                bcPreset[0].getPreset().setName(tmp[1]);
                return 0;
            }
        } else
        {
            return 23;
        }
    }

    private int check_init(String tmp[])
    {
        int expectedParameter = 0;
        return 7;
    }

    private int check_footsw(String tmp[])
    {
        if(activeType == 258)
        {
            if(tmp.length < 2)
                return 3;
            switch(BCL.readvaluetoken(tmp[1]) & 0xfffffcff)
            {
            case 4: // '\004'
                return 0;

            case 5: // '\005'
                bcGlobal.flags |= 2;
                return 0;

            case 6: // '\006'
                bcGlobal.flags |= 1;
                return 0;
            }
            return 12;
        } else
        {
            return 23;
        }
    }

    private int check_snapshot(String tmp[])
    {
        if(activeType == 259)
        {
            if(tmp.length < 2)
                return 3;
            switch(BCL.readvaluetoken(tmp[1]) & 0xfffffcff)
            {
            case 0: // '\0'
                bcPreset[0].getPreset().flags &= 0xfffffbff;
                return 0;

            case 1: // '\001'
                bcPreset[0].getPreset().flags |= 0x400;
                return 0;
            }
            return 12;
        } else
        {
            return 23;
        }
    }

    private int check_rxch(String tmp[])
    {
        if(activeType == 258)
        {
            if(tmp.length < 2)
                return 3;
            if(tmp[1].equals("off"))
            {
                bcGlobal.flags &= 0xfffffdff;
                return 0;
            }
            int value;
            try
            {
                value = Integer.parseInt(tmp[1]);
            }
            catch(NumberFormatException nfe)
            {
                return 10;
            }
            if(value >= 1 && value <= 16)
            {
                bcGlobal.rxch = value - 1;
                bcGlobal.flags |= 0x200;
                return 0;
            }
        }
        return 23;
    }

    private int check_deviceid(String tmp[])
    {
    	try {
	        if(activeType != 258)
	            //break MISSING_BLOCK_LABEL_55;
	        	return 55;
	        if(tmp.length < 2)
	            return 3;
	        int value = Integer.parseInt(tmp[1]);
	        if(value < 1 || value > 16)
	            //break MISSING_BLOCK_LABEL_48;
	        	return 48;
	        
	        bcGlobal.deviceid = value - 1;
	        return 0;
	        //return 12;
	    } catch (NumberFormatException nfe) {
	        //nfe;
	        return 10;
	        //return 23;
	    }
    }

    private int check_lock(String tmp[])
    {
        if(activeType == 259)
        {
            if(tmp.length < 2)
                return 3;
            switch(BCL.readvaluetoken(tmp[1]) & 0xfffffcff)
            {
            case 0: // '\0'
                bcPreset[0].getPreset().flags &= 0xfffff7ff;
                return 0;

            case 1: // '\001'
                bcPreset[0].getPreset().flags |= 0x800;
                return 0;
            }
            return 12;
        } else
        {
            return 23;
        }
    }

    private int check_tx(String tmp[])
    {
        int cnt = 1;
        switch(activeType)
        {
        case 259: 
            BCPresetHead bcph = bcPreset[0].getPreset();
            try
            {
                for(; cnt < tmp.length; cnt++)
                    bcph.txpool[cnt - 1] = Integer.parseInt(tmp[cnt]);

            }
            catch(NumberFormatException nfe)
            {
                return 10;
            }
            return 0;

        case 260: 
        case 261: 
        case 262: 
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            if(bce != null)
                try
                {
                    bce.flags |= 0x8000;
                    bce.tx = 0;
                    for(; cnt < tmp.length; cnt++)
                        bce.txpool[cnt - 1] = Integer.parseInt(tmp[cnt]);

                    bce.tx = cnt;
                }
                catch(NumberFormatException nfe)
                {
                    bce.flags &= 0xffff7fff;
                }
            else
                return 7;
            return 0;
        }
        return 23;
    }

    private int check_resolution(String tmp[])
    {
        int cnt = 1;
        int value = 0;
        if(activeType == 260)
        {
            BCElement bce = bcPreset[0].getEncoder(actControl);
            bce.flags |= 0x8000;
            for(; cnt < tmp.length && cnt <= 4; cnt++)
            {
                try
                {
                    value = Integer.parseInt(tmp[cnt]);
                }
                catch(NumberFormatException nfe)
                {
                    return 10;
                }
                bce.resolution[cnt - 1] = value;
            }

            while(cnt++ < 4) 
                bce.resolution[cnt - 1] = value;
            return 0;
        } else
        {
            return 23;
        }
    }

    private int check_minmax(String tmp[])
    {
        switch(activeType)
        {
        case 260: 
        case 261: 
        case 262: 
            if(tmp.length != 3)
                return 3;
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            if(bce != null)
            {
                bce.flags |= 0x8000;
                try
                {
                    bce.min = Integer.parseInt(tmp[1]);
                }
                catch(NumberFormatException nfe)
                {
                    return 10;
                }
                try
                {
                    bce.max = Integer.parseInt(tmp[2]);
                }
                catch(NumberFormatException nfe)
                {
                    return 10;
                }
                bce.min = bce.min <= 16383 ? bce.min : 0;
                bce.max = bce.max <= 16383 ? bce.max : 0;
                return 0;
            } else
            {
                return 7;
            }
        }
        return 12;
    }

    private int check_mode(String tmp[])
    {
        switch(activeType)
        {
        case 260: 
        {
            if(tmp.length < 2)
                return 3;
            BCElement bce;
            if((bce = bcPreset[0].getEncoder(actControl)) == null)
                return 7;
            bce.flags |= 0x8000;
            bce.flags &= 0xfffffff0;
            switch(BCL.GetToken(tmp[1]))
            {
            case 784: 
                bce.flags |= 1;
                break;

            case 785: 
                bce.flags |= 2;
                break;

            case 786: 
                bce.flags |= 3;
                break;

            case 787: 
                bce.flags |= 4;
                break;

            case 788: 
                bce.flags |= 5;
                break;

            case 789: 
                bce.flags |= 6;
                break;

            case 790: 
                bce.flags |= 7;
                break;

            case 791: 
                bce.flags |= 8;
                break;

            case 792: 
                bce.flags |= 9;
                break;

            case 793: 
                bce.flags |= 0xa;
                break;

            case 794: 
                bce.flags |= 0xb;
                break;

            default:
                return 12;
            }
            return 0;
        }

        case 261: 
        {
            if(tmp.length < 2)
                return 3;
            BCElement bce;
            if((bce = bcPreset[0].getButton(actControl)) == null)
                return 12;
            bce.flags &= 0xfffff9ff;
            switch(BCL.GetToken(tmp[1]))
            {
            case 801: 
                bce.flags |= 0x600;
                break;

            case 802: 
                bce.flags |= 0x200;
                break;

            default:
                return 12;

            case 800: 
                break;
            }
            bce.flags |= 0x8000;
            return 0;
        }
        }
        return 23;
    }

    private int check_txinterval(String tmp[])
    {
        if(activeType == 258)
        {
            if(tmp.length < 2)
                return 3;
            try
            {
                bcGlobal.txinterval = Integer.parseInt(tmp[1]) - 1;
            }
            catch(NumberFormatException nfe)
            {
                return 10;
            }
            return 0;
        } else
        {
            return 23;
        }
    }

    private int check_override(String tmp[])
    {
        switch(activeType)
        {
        case 262: 
            if(tmp.length < 2)
                return 3;
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            bce.flags |= 0x8000;
            switch(BCL.GetToken(tmp[1]))
            {
            case 817: 
                bce.flags &= 0xfffffbff;
                break;

            case 818: 
                bce.flags |= 0x400;
                break;

            default:
                bce.flags &= 0xffff7fff;
                break;
            }
            return 0;
        }
        return 23;
    }

    private int check_motor(String tmp[])
    {
        switch(activeType)
        {
        case 262: 
            if(tmp.length < 2)
                return 3;
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            bce.flags |= 0x8000;
            switch(BCL.GetToken(tmp[1]))
            {
            case 768: 
                bce.flags &= 0xfffffdff;
                break;

            case 769: 
                bce.flags |= 0x200;
                break;

            default:
                bce.flags &= 0xffff7fff;
                break;
            }
            return 0;
        }
        return 23;
    }

    private int check_default(String tmp[])
    {
        switch(activeType)
        {
        case 260: 
        case 261: 
        case 262: 
            if(tmp.length < 2)
                return 3;
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            if(bce != null)
            {
                bce.flags |= 0x8000;
                try
                {
                    int value = Integer.parseInt(tmp[1]);
                    bce.init = value;
                    bce.flags |= 0x1000;
                }
                catch(NumberFormatException nfe)
                {
                    bce.flags &= 0xffffefff;
                }
            }
            return 0;
        }
        return 23;
    }

    private int check_local(String tmp[])
    {
        switch(activeType)
        {
        case 260: 
        case 261: 
        case 262: 
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            bce.flags |= 0x8000;
            switch(BCL.GetToken(tmp[1]))
            {
            case 768: 
                bce.flags &= 0xfffffeff;
                break;

            case 769: 
                bce.flags |= 0x100;
                break;

            default:
                return 12;
            }
            return 0;
        }
        return 23;
    }

    private int check_showvalue(String tmp[])
    {
        switch(activeType)
        {
        case 260: 
        case 261: 
        case 262: 
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            bce.flags |= 0x8000;
            switch(BCL.GetToken(tmp[1]))
            {
            case 768: 
                bce.flags &= 0xffffdfff;
                break;

            case 769: 
                bce.flags |= 0x2000;
                break;

            default:
                bce.flags &= 0xffff7fff;
                break;
            }
            return 0;
        }
        return 23;
    }

    private int check_easypar(String tmp[])
    {
        int retval = 0;
        switch(activeType)
        {
        case 260: 
        case 261: 
        case 262: 
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            if(bce != null)
            {
                bce.flags |= 0x8000;
                if((retval = readEasyPar(bce, tmp, activeType)) != 0)
                    bce.flags &= 0xffff7fff;
                return retval;
            } else
            {
                return 7;
            }
        }
        return 23;
    }

    private int check_keyoverride(String tmp[])
    {
        switch(activeType)
        {
        case 262: 
            BCElement bce = bcPreset[0].getElement(actControl, actControlType);
            if(bce == null)
                return 7;
            bce.flags |= 0x8000;
            if(BCL.GetToken(tmp[1]) == 768)
            {
                bce.flags &= 0xfffffeff;
                return 0;
            }
            int v = BCL.readval(tmp[1]);
            if(v >= 1 && v <= getMaxButtonNumber())
            {
                if(--v < 32)
                    v &= 7;
                else
                    v -= 24;
                bce.flags = (bce.flags &= 0xffffff80) | 0x100 | v;
            } else
            {
                return 11;
            }
            return 0;
        }
        return 23;
    }

    private int evalSubCmd(String tmp[])
    {
        int returnValue = 0;
        int numTok = BCL.GetToken(tmp[0]);
        int value = 0;
        BCElement bce = null;
        switch(numTok)
        {
        case 512: 
            return check_midimode(tmp);

        case 513: 
            return check_request(tmp);

        case 514: 
            return check_startup(tmp);

        case 515: 
            return check_egroups(tmp);

        case 516: 
            return check_fkeys(tmp);

        case 517: 
            return check_name(tmp);

        case 518: 
            return check_init(tmp);

        case 519: 
            return check_footsw(tmp);

        case 520: 
            return check_snapshot(tmp);

        case 521: 
            return check_rxch(tmp);

        case 522: 
            return check_deviceid(tmp);

        case 523: 
            return check_lock(tmp);

        case 544: 
            return check_tx(tmp);

        case 547: 
            return check_resolution(tmp);

        case 548: 
            return check_minmax(tmp);

        case 549: 
            return check_mode(tmp);

        case 550: 
            return check_txinterval(tmp);

        case 551: 
            return check_override(tmp);

        case 552: 
            return check_motor(tmp);

        case 554: 
            return check_default(tmp);

        case 555: 
            return check_local(tmp);

        case 556: 
            return check_showvalue(tmp);

        case 557: 
            return check_easypar(tmp);

        case 558: 
            return check_keyoverride(tmp);

        case 524: 
        case 525: 
        case 526: 
        case 527: 
        case 528: 
        case 529: 
        case 530: 
        case 531: 
        case 532: 
        case 533: 
        case 534: 
        case 535: 
        case 536: 
        case 537: 
        case 538: 
        case 539: 
        case 540: 
        case 541: 
        case 542: 
        case 543: 
        case 545: 
        case 546: 
        case 553: 
        default:
            return returnValue;
        }
    }

    private int readEasyPar(BCElement bce, String tmp[], int type)
    {
        int frames[] = {
            0, 24, 25, 30, 30
        };
        int wp[] = new int[6];
        int iError = 0;
        wp[0] = (BCL.GetToken(tmp[1]) - BCL.GetToken("PC")) + 1;
        if(wp[0] == 7)
        {
            if(BCL.GetToken(tmp[2]) == 771)
            {
                wp[1] = 0;
            } else
            {
                wp[1] = BCL.readval(tmp[2]);
                if(wp[1] < 0 || wp[1] > 126)
                    iError = 2;
                else
                    wp[1]++;
            }
        } else
        {
            wp[1] = BCL.readval(tmp[2]);
            if(wp[1] < 1 || wp[1] > 16)
                iError = 2;
            else
                wp[1]--;
        }
        if(iError == 0)
            switch(wp[0])
            {
            default:
                break;

            case 1: // '\001'
                wp[2] = BCL.readvalueoff(tmp[3], 129);
                if(wp[2] > 128)
                {
                    iError = 3;
                } else
                {
                    wp[3] = BCL.readvalueoff(tmp[4], 129);
                    if(wp[3] > 128)
                        iError = 4;
                    else
                    if(type == 261)
                    {
                        wp[4] = BCL.readvalueoff(tmp[5], 129);
                        if(wp[4] > 128)
                            iError = 5;
                    }
                }
                break;

            case 2: // '\002'
            case 3: // '\003'
                int cnt = 2;
                do
                {
                    if(cnt > 3)
                        break;
                    wp[cnt] = BCL.readval(tmp[cnt + 1]);
                    if(wp[cnt] < 0 || wp[cnt] > 16383 || wp[cnt] > 127 && wp[0] == 2)
                    {
                        iError = cnt + 1;
                        break;
                    }
                    cnt++;
                } while(true);
                if(type == 261)
                {
                    wp[cnt] = BCL.readvalueoff(tmp[cnt + 1]);
                    if(wp[cnt] < 0 || wp[cnt] > 16384)
                    {
                        iError = cnt + 1;
                        break;
                    }
                    wp[5] = BCL.GetToken(tmp[6]) - 1296;
                    if(wp[5] < 0 || wp[5] > 2)
                    {
                        iError = 6;
                        break;
                    }
                    if(wp[5] <= 1)
                        break;
                    int x = BCL.readval(tmp[7]);
                    if(x < 0)
                    {
                        x = -x;
                        wp[5] = 2 + (256 - x << 2);
                    } else
                    {
                        wp[5] = 2 + (x << 2);
                    }
                    if(x < 1 || x > 127)
                        iError = 7;
                    break;
                }
                wp[4] = BCL.readval(tmp[5]);
                if(wp[4] < 0 || wp[4] > 16383)
                {
                    iError = 5;
                    break;
                }
                wp[5] = BCL.GetToken(tmp[6]) - 1312;
                if(wp[5] < 0 || wp[5] > 8)
                    iError = 6;
                break;

            case 4: // '\004'
                wp[2] = BCL.readval(tmp[3]);
                if(wp[2] < 0 || wp[2] > 127)
                {
                    iError = 3;
                    break;
                }
                wp[3] = BCL.readval(tmp[4]);
                if(wp[3] < 0 || wp[3] > 127)
                {
                    iError = 4;
                    break;
                }
                wp[5] = BCL.GetToken(tmp[5]) - 1296;
                if(wp[5] < 0 || wp[5] > 1)
                    iError = 6;
                break;

            case 5: // '\005'
                wp[3] = BCL.readval(tmp[3]);
                if(wp[3] > 127)
                    iError = 3;
                break;

            case 6: // '\006'
                if(BCL.GetToken(tmp[3]) == 771)
                {
                    wp[2] = 0;
                } else
                {
                    wp[2] = BCL.readval(tmp[3]);
                    if(wp[2] < 0 || wp[2] > 127)
                    {
                        iError = 3;
                        break;
                    }
                    wp[2]++;
                }
                wp[3] = BCL.readval(tmp[4]);
                if(wp[3] < 0 || wp[3] > 127)
                {
                    iError = 4;
                    break;
                }
                if(type == 261)
                {
                    wp[4] = BCL.readvalueoff(tmp[5]);
                    if(wp[4] < 0 || wp[4] > 128)
                    {
                        iError = 5;
                        break;
                    }
                    wp[5] = BCL.GetToken(tmp[6]) - 1296;
                    if(wp[5] < 0 || wp[5] > 1)
                        iError = 6;
                    break;
                }
                wp[4] = BCL.readval(tmp[5]);
                if(wp[4] < 0 || wp[4] > 127)
                    iError = 5;
                break;

            case 7: // '\007'
                wp[2] = BCL.GetToken(tmp[3]) - 1328;
                if(wp[2] < 0 || wp[2] > 7)
                {
                    iError = 3;
                    break;
                }
                wp[5] = BCL.GetToken(tmp[5]) - 1360;
                if(wp[5] < 0 || wp[5] > 4)
                {
                    iError = 5;
                    break;
                }
                if(wp[5] == 0)
                    break;
                if(tmp[4].length() != 11)
                {
                    iError = -1;
                    break;
                }
                if(tmp[4].charAt(2) != ':' || tmp[4].charAt(5) != ':' || tmp[4].charAt(8) != '.')
                {
                    iError = -2;
                    break;
                }
                String n1 = tmp[4].substring(0, 2);
                String n2 = tmp[4].substring(3, 5);
                String n3 = tmp[4].substring(6, 8);
                String n4 = tmp[4].substring(9);
                int mmc = 0;
                int an = 0;
                an = BCL.readval(n1);
                if(an > 23)
                {
                    iError = -3;
                    break;
                }
                mmc = an * 60;
                an = BCL.readval(n2);
                if(an > 59)
                {
                    iError = -4;
                    break;
                }
                mmc += an;
                wp[3] = mmc;
                an = BCL.readval(n3);
                if(an > 59)
                {
                    iError = -5;
                    break;
                }
                mmc = an * frames[wp[5]];
                an = BCL.readval(n4);
                if(an > frames[wp[5]])
                {
                    iError = -6;
                } else
                {
                    mmc += an;
                    wp[4] = mmc;
                }
                break;

            case 8: // '\b'
                wp[2] = BCL.GetToken(tmp[3]) - 1344;
                if(wp[2] < 0 || wp[2] > 14)
                {
                    iError = 3;
                    break;
                }
                wp[3] = BCL.readval(tmp[4]);
                if(wp[3] > 127)
                {
                    iError = 4;
                    break;
                }
                if(type == 261)
                {
                    wp[4] = BCL.readvalueoff(tmp[5]);
                    if(wp[4] < 0 || wp[4] > 128)
                    {
                        iError = 5;
                        break;
                    }
                    wp[5] = BCL.GetToken(tmp[6]) - 1296;
                    if(wp[5] < 0 || wp[5] > 1)
                        iError = 6;
                    break;
                }
                wp[4] = BCL.readval(tmp[5]);
                if(wp[4] < 0 || wp[4] > 127)
                    iError = 5;
                break;
            }
        if(iError == 0)
        {
            for(int value = 0; value < 6; value++)
                bce.easypar[value] = wp[value];

            bce.easy = 6;
        } else
        {
            System.out.println("Error while parsing:");
            int value;
            if(iError >= -6 && iError < 0)
            {
                value = iError;
                iError = 4;
            } else
            {
                value = 0;
            }
            for(int i = 0; i < tmp.length; i++)
                if(i == iError)
                    System.out.print("<<< " + tmp[i] + " >>> ");
                else
                    System.out.print(tmp[i] + " ");

            System.out.println("");
            switch(value)
            {
            case -1: 
                System.out.println("TimeFormat wrong");
                break;

            case -2: 
                System.out.println("TimeFormat must be in form: hh:mm:ss.ff");
                break;

            case -3: 
                System.out.println("Format for hour is incorrect (00-23)");
                break;

            case -4: 
                System.out.println("Format for minute is incorrect (00-59)");
                break;

            case -5: 
                System.out.println("Format for second is incorrect (00-59)");
                break;

            case -6: 
                System.out.println("Format for frame is incorrect (00-[fps])");
                break;
            }
        }
        return iError;
    }

    private boolean evaluateToken(String tmp[])
    {
        if(tmp.length == 0)
            return false;
        if(tmp[0].charAt(0) == '$')
            evalCmd(tmp);
        else
        if(tmp[0].charAt(0) == '.')
        {
            evalSubCmd(tmp);
        } else
        {
            System.out.println("Error in Parser: BCL.BCDevice.initFromScript()");
            System.out.println("                 -> Neither '.' nor '$'");
        }
        return false;
    }

    public boolean initFromScript(int modelId, String s)
    {
        reinit(modelId);
        for(StringTokenizer tok = new StringTokenizer(s, ";"); tok.hasMoreElements(); evaluateToken(BCL.lineToToken(tok.nextToken())));
        return false;
    }

    public boolean initFromScript(int modelId, Vector script)
    {
        reinit(modelId);
        for(int no = 0; no < script.size(); no++)
            evaluateToken(BCL.lineToToken((String)script.elementAt(no)));

        return false;
    }

    public boolean initFromScript(int modelId, String line[])
    {
        reinit(modelId);
        for(int no = 0; no < line.length; no++)
            evaluateToken(BCL.lineToToken(line[no]));

        return false;
    }

    public String getScript(int store, boolean bAddName)
    {
        String s = bcGlobal.getScript();
        if(!s.endsWith(";"))
            s = s + ";";
        for(int i = 1; i < 33; i++)
        {
            s = s + bcPreset[i].getScript(store != 0 ? i : 0, bAddName);
            if(!s.endsWith(";"))
                s = s + ";";
        }

        return s;
    }

    static final int TYPE_REV = 257;
    static final int TYPE_GLOBAL = 258;
    static final int TYPE_PRESET = 259;
    static final int TYPE_ENCODER = 260;
    static final int TYPE_BUTTON = 261;
    static final int TYPE_FADER = 262;
    static final int TYPE_STORE = 265;
    static final int TYPE_RECALL = 266;
    static final int TYPE_END = 256;
    private int use_radix;
    private BCGlobal bcGlobal;
    private BCPreset bcPreset[];
    private int modelID;
    private int activeType;
    private int actControlType;
    private int actControl;
}
