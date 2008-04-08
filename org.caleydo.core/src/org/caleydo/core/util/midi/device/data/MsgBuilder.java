package org.geneview.core.util.midi.device.data;

//import java.io.PrintStream;

public class MsgBuilder
{


    public static final int CMD_IDNTIFY = 1;
    public static final int CMD_SEND_PRESET = 32;
    public static final int CMD_SELECT_PRESET = 34;
    public static final int CMD_REQUEST_PRESET = 64;
    public static final int CMD_REQUEST_GLOBALS = 65;
    public static final int CMD_REQUEST_PRESET_NAME = 66;
    public static final int CMD_FLASH_DATA = 52;
    public static final int CMD_REQUEST_MEMORY_DUMP = 116;
    
    
    public MsgBuilder()
    {
    }

    public static byte[] StringtoScriptByte(String s, int id, int type, int lineno)
    {
        boolean debugMe = true;
        int i = s.length();
        int j = 0;
        byte b[] = new byte[i + 10];
        setHead(b, id, type, 32, false);
        b[7] = (byte)(lineno >> 7 & 0x7f);
        b[8] = (byte)(lineno & 0x7f);
        for(; j < i; j++)
            b[9 + j] = (byte)s.charAt(j);

        b[9 + j] = -9;
        if(debugMe)
            for(j = 0; j < b.length; j++)
            {
                if(j < 9)
                {
                    System.out.print("$" + Integer.toString(b[j] & 0xff, 16) + ",");
                    continue;
                }
                if(j == b.length - 1)
                    System.out.println(Integer.toString(b[j] & 0xff, 16));
                else
                    System.out.print("'" + new String(b, j, 1) + "',");
            }

        return b;
    }

    public static byte[] StringtoByte(String s)
    {
        int i = s.length();
        byte b[] = new byte[i];
        for(int j = 0; j < i; j++)
            b[j] = (byte)s.charAt(j);

        return b;
    }

    public static final byte[] setHead(byte msg[], int id, int type, int cmd, boolean end)
    {
        msg[0] = -16;
        msg[1] = 0;
        msg[2] = 32;
        msg[3] = 50;
        msg[4] = (byte)id;
        msg[5] = (byte)type;
        msg[6] = (byte)cmd;
        if(end)
            msg[7] = -9;
        return msg;
    }

    public static final byte[] requestMemoryDump(int id, int dev, int BlockAdr)
    {
        byte msg[] = new byte[10];
        setHead(msg, id, dev, 116, false);
        msg[7] = (byte)(BlockAdr >> 7 & 0x7f);
        msg[8] = (byte)(BlockAdr & 0x7f);
        msg[9] = -9;
        return msg;
    }

    public static final byte[] selectPreset(int id, int dev, int presetNo)
    {
        byte msg[] = new byte[9];
        setHead(msg, id, dev, 34, false);
        msg[7] = (byte)presetNo;
        msg[8] = -9;
        return msg;
    }

    public static final byte[] requestPreset(int id, int dev, int presetNo)
    {
        byte msg[] = new byte[9];
        setHead(msg, id, dev, 64, false);
        msg[7] = (byte)presetNo;
        msg[8] = -9;
        return msg;
    }

    public static final byte[] identify(int id, int dev)
    {
        byte msg[] = new byte[8];
        return setHead(msg, id, dev, 1, true);
    }

    public static final byte[] requestPresetName(int id, int dev, int presetNo)
    {
        byte msg[] = new byte[9];
        setHead(msg, id, dev, 66, false);
        msg[7] = (byte)presetNo;
        msg[8] = -9;
        return msg;
    }

    public static final byte[] requestGlobals(int id, int dev)
    {
        byte msg[] = new byte[8];
        return setHead(msg, id, dev, 65, true);
    }

}
