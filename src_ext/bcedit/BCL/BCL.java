// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCL.java

package bcedit.BCL;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

// Referenced classes of package bcedit.BCL:
//            BCElement, BCPreset, BCDevice, bclToken, 
//            BCLError, BCPresetHead, BCGlobal

public class BCL
{

    public BCL()
    {
    }

    public static final String FileHeaderElement(int devId)
    {
        String idString = getNameFromId(devId);
        if(idString != null)
            idString = "BCL,0002,ELEMENT," + idString;
        return idString;
    }

    public static final String FileHeaderPreset(int devId)
    {
        String idString = getNameFromId(devId);
        if(idString != null)
            idString = "BCL,0002,PRESET," + idString;
        return idString;
    }

    public static final String FileHeaderDevice(int devId)
    {
        String idString = getNameFromId(devId);
        if(idString != null)
            idString = "BCL,0002,DEVICE," + idString;
        return idString;
    }

    public static final int GetToken(String string)
    {
        for(int i = 0; i < scriptToken.length; i++)
            if(scriptToken[i].string.equals(string))
                return scriptToken[i].id;

        return -1;
    }

    public static final String GetToken(int n)
    {
        for(int i = 0; i < scriptToken.length; i++)
            if(scriptToken[i].id == n)
                return scriptToken[i].string;

        return null;
    }

    public static final String getRevFromId(int id)
    {
        switch(id)
        {
        case 21: // '\025'
            return "R1";

        case 20: // '\024'
            return "F1";
        }
        return null;
    }

    public static final String getDeviceNameFromId(int id)
    {
        switch(id)
        {
        case 21: // '\025'
            return "BCR2000";

        case 20: // '\024'
            return "BCF2000";
        }
        return null;
    }

    public static final String getNameFromId(int id)
    {
        switch(id)
        {
//        case 21: // '\025'
//            return "ROTARY";

        case 20: // '\024'
            return "FADER";
        }
        return null;
    }

    public static String[] itemize(String line)
    {
        String test = line.trim();
        String testx = null;
        int pos;
        if((pos = test.indexOf('\'')) >= 0)
        {
            int pos1;
            if((pos1 = test.indexOf('\'', pos + 1)) < 0)
                return null;
            testx = test.substring(pos + 1, pos1 - pos);
            test = test.substring(0, pos) + " <substring> " + test.substring(pos1 + 1);
        }
        StringTokenizer st = new StringTokenizer(test);
        String stringList[] = new String[st.countTokens()];
        for(pos = 0; st.hasMoreTokens(); pos++)
        {
            stringList[pos] = st.nextToken();
            if(stringList[pos].equals("<substring>") && testx != null)
                stringList[pos] = testx;
        }

        return stringList;
    }

    public static final int readvalueoff(String toEvaluate)
    {
        return readvalueoff(toEvaluate, 0);
    }

    public static final int readvaluetoken(String toEvaluate)
    {
        return readvaluetoken(toEvaluate, 0);
    }

    public static final int readvaluetoken(String toEvaluate, int defaultReturn)
    {
        int retval;
        if((retval = GetToken(toEvaluate)) != -1)
            return retval;
        if((retval = readval(toEvaluate)) != 0x80000000)
            return retval;
        else
            return defaultReturn;
    }

    public static final int readvalueoff(String toEvaluate, int defaultReturn)
    {
        int retval = defaultReturn;
        if(GetToken(toEvaluate) == 768)
            return 0;
        retval = readval(toEvaluate);
        if(retval != 0x80000000)
            return retval + 1;
        else
            return defaultReturn;
    }

    public static final int readval(String toEvaluate)
    {
        int radix = 10;
        toEvaluate = toEvaluate.trim();
        if(toEvaluate.charAt(0) == '$')
        {
            radix = 16;
            toEvaluate = toEvaluate.substring(1);
        }
        int retval;
        try
        {
            retval = Integer.parseInt(toEvaluate, radix);
        }
        catch(NumberFormatException nfe)
        {
            retval = 0x80000000;
            System.out.println("Error NAN (" + toEvaluate + ")");
        }
        return retval;
    }

    public static String dumppoolCompressed(int token, int buffer[], int len)
    {
        if(len == 0)
            return new String("");
        String s = ";" + GetToken(token) + ".C";
        for(int i = 0; i < len; i++)
            s = s + " " + Integer.toString(buffer[i]);

        return s;
    }

    public static String dumppool(int token, int buffer[], int len)
    {
        int i = 0;
        boolean newline = true;
        String s = new String("");
        do
        {
            while(i < len) 
            {
                if(newline)
                {
                    s = s + ";" + GetToken(token);
                    newline = false;
                }
                int tok = buffer[i++];
                if(tok == 254)
                    switch(buffer[i++])
                    {
                    case 0: // '\0'
                        s = s + ";";
                        newline = true;
                        break;

                    case 64: // '@'
                    case 65: // 'A'
                    case 66: // 'B'
                    case 67: // 'C'
                    case 68: // 'D'
                    case 69: // 'E'
                    case 70: // 'F'
                    case 71: // 'G'
                    case 80: // 'P'
                    case 84: // 'T'
                    case 85: // 'U'
                    case 86: // 'V'
                        s = s + " " + GetToken(buffer[i - 1] + 1024);
                        break;

                    case 72: // 'H'
                    case 73: // 'I'
                        s = s + " " + GetToken(buffer[i - 1] + 1024);
                        tok = buffer[i++];
                        tok |= buffer[i++] << 7;
                        s = s + " " + Integer.toString(tok);
                        break;

                    case 81: // 'Q'
                    case 82: // 'R'
                    case 83: // 'S'
                        s = s + " " + GetToken(buffer[i - 1]);
                        s = s + " " + Integer.toString(buffer[i++]);
                        break;

                    default:
                        s = s + " " + Integer.toString(tok);
                        break;
                    }
                else
                    s = s + " " + Integer.toString(tok);
            }
            return s;
        } while(true);
    }

    public static final String[] lineToToken(String line)
    {
        int j = 0;
        char nextDelim = ' ';
        Vector tok = new Vector();
        for(int tlen = line.length(); j < tlen;)
        {
            String tmp = "";
            for(; j < tlen && line.charAt(j) == ' '; j++);
            if(j >= tlen)
                break;
            if(line.charAt(j) == '\'')
            {
                nextDelim = '\'';
                j++;
            }
            while(j < tlen && line.charAt(j) != nextDelim) 
                tmp = tmp + line.charAt(j++);
            if(j < tlen && nextDelim == '\'')
            {
                j++;
                nextDelim = ' ';
            }
            tok.add(tmp);
        }

        String retVal[] = new String[tok.size()];
        for(j = 0; j < tok.size(); j++)
            retVal[j] = (String)tok.elementAt(j);

        return retVal;
    }

    public static String syx2script(byte buffer[])
    {
        int pos = 0;
        int pos2 = 0;
        int lineCount = 0;
        String script = "";
        do
        {
            if(pos >= buffer.length)
                break;
            if(buffer[pos] == -16)
            {
                for(pos2 = pos; pos2 < buffer.length && buffer[pos2] != -9; pos2++);
                script = script + new String(buffer, pos + 1, pos2 - (pos + 1)) + ";";
                pos = pos2 + 1;
            }
        } while(true);
        return script;
    }

    public static String[] script2Presets(String script)
    {
        int pos = -1;
        int pos2 = -1;
        int count = 0;
        String checkString = ";$";
        String header = "";
        while((pos = script.indexOf("$preset", pos + 1)) >= 0) 
            count++;
        if(count == 0)
            return null;
        if((pos = script.indexOf("$rev")) >= 0 && (pos2 = script.indexOf(checkString, pos + 1)) >= 0)
            header = script.substring(pos, pos2 + 1);
        String presetScript[] = new String[count];
        pos = -1;
        int x = 0;
        while(--count >= 0) 
        {
            pos2 = 0;
            pos = script.indexOf("$preset", pos + 1);
            pos2 = script.indexOf(checkString, pos);
            if(pos2 < 0)
                pos2 = script.length();
            else
                pos2++;
            presetScript[x++] = header + script.substring(pos, pos2);
        }
        return presetScript;
    }

    public static BCElement initElementFromScript(String line)
    {
        StringTokenizer tok = new StringTokenizer(line, ";");
        String script[] = new String[tok.countTokens()];
        int cnt = 0;
        while(tok.hasMoreTokens()) 
            script[cnt++] = tok.nextToken();
        return initElementFromScript(script);
    }

    public static BCElement initElementFromScript(String script[])
    {
        int error = 0;
        BCElement bce = new BCElement();
        int context = -1;
        int line;
        for(line = 0; line < script.length; line++)
        {
            String tmp[] = lineToToken(script[line]);
            if(tmp.length == 0 || tmp[0].charAt(0) == '#')
                continue;
            if(tmp[0].charAt(0) == '$')
            {
                context = GetToken(tmp[0]);
                switch(context)
                {
                case 28673: 
                    if(!tmp[1].equals("ELEMENT"))
                        error = 254;
                    break;

                case 260: 
                    if(bce.ctrlType == -1)
                        bce.ctrlType = 0;
                    // fall through

                case 261: 
                    if(bce.ctrlType == -1)
                        bce.ctrlType = 1;
                    // fall through

                case 262: 
                    if(bce.ctrlType == -1)
                        bce.ctrlType = 2;
                    if((bce.id = readval(tmp[1])) == 0x80000000)
                        error = 10;
                    break;

                case 256: 
                    error = 255;
                    break;

                default:
                    error = 23;
                    break;

                case 257: 
                case 28672: 
                case 28674: 
                    break;
                }
            } else
            if(tmp[0].charAt(0) == '.')
            {
                if(context < 0)
                {
                    error = 23;
                    break;
                }
                error = evalSubCmd(context, tmp, null, null, bce);
                if(error != 0 && ignoreErrors)
                    error = 0;
            }
            if(error != 0)
                break;
        }

        if(error == 0 || error == 255)
        {
            return bce;
        } else
        {
            lastError = error;
            JOptionPane.showMessageDialog(new JFrame(), "there was an error on line " + line + ":\n\t" + script[line] + "\nError: " + BCLError.getErrorString(error & 0xff), "Script Error", 0);
            return null;
        }
    }

    public static BCPreset initPresetFromScript(Object o)
    {
        int model;
        int error;
        BCDevice bcd;
        BCPreset bcp;
        BCElement bce;
        int context;
        String toConvert;
        String script[];
        String fname;
        model = -1;
        error = 0;
        bcd = null;
        bcp = null;
        bce = null;
        context = -1;
        toConvert = null;
        script = null;
        if(o instanceof String[])
        {
            script = (String[])o;
            //break MISSING_BLOCK_LABEL_275;
        }
        if(!(o instanceof String)) {
            //break MISSING_BLOCK_LABEL_243;
        }
        
        char bsep = '\u0100';
        String ssep = new String("\377");
        toConvert = (String)o;
        if(toConvert.indexOf(ssep) <= 0) {        	        
            //break MISSING_BLOCK_LABEL_275;
        }
        
        fname = toConvert.substring(toConvert.indexOf(bsep) + 1);
        String s;
        StringTokenizer t2;
        BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fname));
			toConvert = in.readLine();
			 in.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        StringTokenizer tok = new StringTokenizer(toConvert, ";");
        s = tok.nextToken();
        if(!s.startsWith("BCL")) {
        	
            //break MISSING_BLOCK_LABEL_275;
        }
        
        t2 = new StringTokenizer(s, ",");
        if(!t2.nextToken().equals("BCL"))
            return null;
        if(Integer.parseInt(t2.nextToken()) != 2)
            return null;
        if(!t2.nextToken().equals("DEVICE"))
            return null;
        try
        {
            toConvert = toConvert.substring(s.length());
        }
        catch(Exception E)
        {
            System.out.println(E.getMessage());
            return null;
        }
        //break MISSING_BLOCK_LABEL_275;
        
        if((o instanceof byte[]) && ((byte[])o)[0] == -16)
            toConvert = syx2script((byte[])o);
        else
            return null;
        int line;
        if(toConvert != null)
        {
            StringTokenizer tok2 = new StringTokenizer(toConvert, ";");
            script = new String[tok2.countTokens()];
            line = 0;
            while(tok2.hasMoreTokens()) 
                script[line++] = tok2.nextToken();
        }
        for(line = 0; line < script.length; line++)
        {
            String tmp[] = lineToToken(script[line]);
            if(tmp.length == 0 || tmp[0].charAt(0) == '#')
                continue;
            if(tmp[0].charAt(0) == '$')
            {
                context = GetToken(tmp[0]);
                switch(context)
                {
                case 28672: 
                {
                    if((model = readval(tmp[1])) == 0x80000000)
                        error = 10;
                    else
                    if(bcd == null)
                        bcp = new BCPreset(model);
                    else
                    if(bcd.getModelID() != model)
                        error = 23;
                    break;
                }

                case 28673: 
                {
                    if(!tmp[1].equals("PRESET"))
                        error = 254;
                    break;
                }

                case 257: 
                {
                    if(tmp.length < 2)
                    {
                        error = 3;
                    } else
                    {
                        if(tmp[1].equals("R1"))
                            model = 21;
                        else
                        if(tmp[1].equals("F1"))
                            model = 20;
                        if(model == -1)
                            error = 23;
                        if(bcp == null)
                            bcp = new BCPreset(model);
                        else
                        if(bcp.getModelID() != model)
                            error = 23;
                    }
                    break;
                }

                case 259: 
                {
                    if(bcp != null)
                    {
                        bcp.getPreset().flags = 33027;
                        bcp.getPreset().txreq = 0;
                    } else
                    {
                        error = 17;
                    }
                    break;
                }

                case 260: 
                {
                    int num = readval(tmp[1]);
                    if(num == 0x80000000)
                        error = 10;
                    else
                        bce = bcp.getElement(num - 1, 0);
                    break;
                }

                case 261: 
                {
                    int num = readval(tmp[1]);
                    if(num == 0x80000000)
                        error = 10;
                    else
                        bce = bcp.getElement(num - 1, 1);
                    break;
                }

                case 262: 
                {
                    int num = readval(tmp[1]);
                    if(num == 0x80000000)
                        error = 10;
                    else
                        bce = bcp.getElement(num - 1, 2);
                    break;
                }

                case 256: 
                {
                    error = 255;
                    break;
                }

                default:
                {
                    error = 23;
                    break;
                }

                case 265: 
                case 266: 
                case 28674: 
                    break;
                }
            } else
            if(tmp[0].charAt(0) == '.')
            {
                if(context < 0)
                {
                    error = 23;
                    break;
                }
                error = evalSubCmd(context, tmp, null, bcp, bce);
                if(error != 0 && ignoreErrors)
                    error = 0;
            }
            if(error != 0)
                break;
        }

        if(error == 0 || error == 255)
        {
            return bcp;
        } else
        {
            lastError = error;
            JOptionPane.showMessageDialog(new JFrame(), "there was an error on line " + line + ":\n\t" + script[line] + "\nError: " + BCLError.getErrorString(error & 0xff), "Script Error", 0);
            return null;
        }
    }

    public static BCDevice initDeviceFromFile(String fname)
    {
        String toConvert = null;
        String s;
        StringTokenizer t2;
        BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fname));
			toConvert = in.readLine();
			in.close();
			
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringTokenizer tok = new StringTokenizer(toConvert, ";");
        s = tok.nextToken();
        if(!s.startsWith("BCL")) {
         //   break MISSING_BLOCK_LABEL_137;
        }
        
        t2 = new StringTokenizer(s, ",");
        if(!t2.nextToken().equals("BCL"))
            return null;
        if(Integer.parseInt(t2.nextToken()) != 2)
            return null;
        if(!t2.nextToken().equals("DEVICE"))
            return null;
        try
        {
            toConvert = toConvert.substring(s.length());
        }
        catch(Exception E)
        {
            System.out.println(E.getMessage());
            return null;
        }
        return initDeviceFromScript(toConvert);
    }

    public static BCDevice initDeviceFromScript(Object o)
    {
        int model;
        int error;
        BCDevice bcd;
        BCPreset bcp;
        BCElement bce;
        int context;
        String script[];
        String toConvert;
        String fname;
        model = -1;
        error = 0;
        bcd = null;
        bcp = null;
        bce = null;
        context = -1;
        script = null;
        toConvert = null;
        if(o instanceof String[])
        {
            script = (String[])o;
            //break MISSING_BLOCK_LABEL_284;
        }
        
//        if(!(o instanceof String))
//            break MISSING_BLOCK_LABEL_252;
        
        char bsep = '\u0100';
        String ssep = new String("\377");
        toConvert = (String)o;
        
//        if(toConvert.indexOf(ssep) <= 0)
//            break MISSING_BLOCK_LABEL_243;
        
        fname = toConvert.substring(toConvert.indexOf(bsep) + 1);
        String s;
        StringTokenizer t2;
        BufferedReader in;
		try {
			in = new BufferedReader(new FileReader(fname));
			
			toConvert = in.readLine();        
			in.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        StringTokenizer tok = new StringTokenizer(toConvert, ";");
        s = tok.nextToken();
        
//        if(!s.startsWith("BCL"))
//            break MISSING_BLOCK_LABEL_284;
        
        t2 = new StringTokenizer(s, ",");
        if(!t2.nextToken().equals("BCL"))
            return null;
        if(Integer.parseInt(t2.nextToken()) != 2)
            return null;
        if(!t2.nextToken().equals("DEVICE"))
            return null;
        try
        {
            toConvert = toConvert.substring(s.length());
        }
        catch(Exception E)
        {
            System.out.println(E.getMessage());
            return null;
        }
        //break MISSING_BLOCK_LABEL_284;
        
        toConvert = (String)o;
        
        //break MISSING_BLOCK_LABEL_284;
        
        if((o instanceof byte[]) && ((byte[])o)[0] == -16)
            toConvert = syx2script((byte[])o);
        else
            return null;
        int line;
        if(toConvert != null)
        {
            StringTokenizer tok2 = new StringTokenizer(toConvert, ";");
            script = new String[tok2.countTokens()];
            line = 0;
            while(tok2.hasMoreTokens()) 
                script[line++] = tok2.nextToken();
        }
        for(line = 0; line < script.length; line++)
        {
            if(script[line] == null || script[line].trim().length() == 0)
                continue;
            String tmp[] = lineToToken(script[line].trim());
            if(tmp.length == 0 || tmp[0].charAt(0) == '#')
                continue;
            if(tmp[0].charAt(0) == '$')
            {
                context = GetToken(tmp[0]);
                int num;
                switch(context)
                {
                case 28672: 
                    if((model = readval(tmp[1])) == 0x80000000)
                        error = 10;
                    else
                    if(bcd == null)
                        bcd = new BCDevice(model);
                    else
                    if(bcd.getModelID() != model)
                        error = 23;
                    break;

                case 28673: 
                    if(!tmp[1].equals("DEVICE"))
                        error = 254;
                    break;

                case 257: 
                    if(tmp[1].equals("R1"))
                        model = 21;
                    else
                    if(tmp[1].equals("F1"))
                        model = 20;
                    if(model == -1)
                        error = 23;
                    else
                    if(bcd == null)
                        bcd = new BCDevice(model);
                    else
                    if(bcd.getModelID() != model)
                        error = 23;
                    break;

                case 259: 
                    bcp = new BCPreset(bcd.getModelID());
                    break;

                case 260: 
                    num = readval(tmp[1]);
                    if(num == 0x80000000)
                        error = 10;
                    else
                        bce = bcp.getElement(num - 1, 0);
                    break;

                case 261: 
                    num = readval(tmp[1]);
                    if(num == 0x80000000)
                        error = 10;
                    else
                        bce = bcp.getElement(num - 1, 1);
                    break;

                case 262: 
                    num = readval(tmp[1]);
                    if(num == 0x80000000)
                        error = 10;
                    else
                        bce = bcp.getElement(num - 1, 2);
                    break;

                case 265: 
                    if((num = readval(tmp[1])) != 0x80000000)
                        bcd.setPreset(num, bcp);
                    else
                        error = 10;
                    break;

                case 256: 
                    error = 255;
                    break;

                default:
                    error = 23;
                    break;

                case 258: 
                case 266: 
                case 28674: 
                    break;
                }
            } else
            if(tmp[0].charAt(0) == '.')
            {
                if(context < 0)
                {
                    error = 23;
                    break;
                }
                error = evalSubCmd(context, tmp, bcd, bcp, bce);
                if(error != 0 && ignoreErrors)
                    error = 0;
            }
            if(error != 0)
                break;
        }

        if(error == 0 || error == 255)
        {
            return bcd;
        } else
        {
            lastError = error;
            JOptionPane.showMessageDialog(new JFrame(), "there was an error on line " + line + ":\n\t" + script[line] + "\nError: " + BCLError.getErrorString(error & 0xff), "Script Error", 0);
            return null;
        }
    }

    public static BCDevice getCopy(BCDevice bcd)
    {
        return initDeviceFromScript(GetToken(257) + " " + getRevFromId(bcd.getModelID()) + ";" + bcd.getScript(1, true) + GetToken(256));
    }

    private static int evalSubCmd(int context, String tmp[], BCDevice bcDevice, BCPreset bcPreset, BCElement bcElement)
    {
        int returnValue = 0;
        int numTok = GetToken(tmp[0]);
        int value = 0;
        BCElement bce = null;
        switch(numTok)
        {
        case 512: 
            return check_midimode(context, tmp, bcDevice);

        case 513: 
            return check_request(context, tmp, bcPreset);

        case 514: 
            return check_startup(context, tmp, bcDevice);

        case 515: 
            return check_egroups(context, tmp, bcPreset);

        case 516: 
            return check_fkeys(context, tmp, bcPreset);

        case 517: 
            return check_name(context, tmp, bcPreset, bcElement);

        case 518: 
            return check_init(context, tmp, bcPreset);

        case 519: 
            return check_footsw(context, tmp, bcDevice);

        case 520: 
            return check_snapshot(context, tmp, bcPreset);

        case 521: 
            return check_rxch(context, tmp, bcDevice);

        case 522: 
            return check_deviceid(context, tmp, bcDevice);

        case 523: 
            return check_lock(context, tmp, bcPreset);

        case 544: 
            return check_tx(context, tmp, bcPreset, bcElement);

        case 547: 
            return check_resolution(context, tmp, bcElement);

        case 548: 
            return check_minmax(context, tmp, bcElement);

        case 549: 
            return check_mode(context, tmp, bcElement);

        case 550: 
            return check_txinterval(context, tmp, bcDevice);

        case 551: 
            return check_override(context, tmp, bcElement);

        case 552: 
            return check_motor(context, tmp, bcElement);

        case 554: 
            return check_default(context, tmp, bcElement);

        case 555: 
            return check_local(context, tmp, bcElement);

        case 556: 
            return check_showvalue(context, tmp, bcElement);

        case 557: 
            return check_easypar(context, tmp, bcElement);

        case 558: 
            return check_keyoverride(context, tmp, bcElement, 64);

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

    private static int check_midimode(int context, String tmp[], BCDevice bcDevice)
    {
        if(context == 258)
        {
            if(bcDevice.getGlobal() == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            int value = readvaluetoken(tmp[1]) - 768;
            if(value >= 8 && value <= 16)
                bcDevice.getGlobal().midimode = value - 8;
            else
                return 11;
        } else
        {
            return 23;
        }
        return 0;
    }

    private static int check_request(int context, String tmp[], BCPreset bcPreset)
    {
        if(context == 259)
        {
            if(bcPreset == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            switch(readvaluetoken(tmp[1]) & 0xfcff)
            {
            case 0: // '\0'
                bcPreset.getPreset().flags &= 0xfffffdff;
                return 0;

            case 1: // '\001'
                bcPreset.getPreset().flags |= 0x200;
                return 0;
            }
        } else
        {
            return 23;
        }
        return 12;
    }

    private static int check_startup(int context, String tmp[], BCDevice bcDevice)
    {
        if(context == 258)
        {
            if(bcDevice.getGlobal() == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            int value = readvaluetoken(tmp[1]);
            bcDevice.getGlobal().startup = value;
            if(value == 770)
                bcDevice.getGlobal().flags |= 0x100;
            return 0;
        } else
        {
            return 23;
        }
    }

    private static int check_egroups(int context, String tmp[], BCPreset bcPreset)
    {
        if(context == 259)
        {
            if(bcPreset == null)
                return 7;
            if(tmp.length < 2)
            {
                return 3;
            } else
            {
                int value = readvaluetoken(tmp[1], 0);
                bcPreset.getPreset().flags &= -4;
                bcPreset.getPreset().flags |= value - 1 & 3;
                return 0;
            }
        } else
        {
            return 23;
        }
    }

    private static int check_fkeys(int context, String tmp[], BCPreset bcPreset)
    {
        if(context == 259)
        {
            if(bcPreset == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            switch(readvaluetoken(tmp[1]) & 0xfcff)
            {
            case 0: // '\0'
                bcPreset.getPreset().flags &= 0xfffffeff;
                return 0;

            case 1: // '\001'
                bcPreset.getPreset().flags |= 0x100;
                return 0;
            }
        } else
        {
            return 23;
        }
        return 12;
    }

    private static int check_name(int context, String tmp[], BCPreset bcPreset, BCElement bce)
    {
        switch(context)
        {
        case 260: 
        case 261: 
        case 262: 
            if(bce != null)
                bce.name = tmp[1];
            return 0;

        case 259: 
            if(bcPreset == null)
                return 7;
            if(tmp.length < 2)
            {
                return 3;
            } else
            {
                bcPreset.getPreset().setName(tmp[1]);
                return 0;
            }
        }
        return 23;
    }

    private static int check_init(int context, String tmp[], BCPreset bcPreset)
    {
        int expectedParameter = 0;
        BCElement bce[] = bcPreset.getEncoders();
        for(int i = 0; i < bce.length; i++)
            bce[i].init(i + 1, 0);

        bce = bcPreset.getButtons();
        for(int i = 0; i < bce.length; i++)
            bce[i].init(i + 1, 1);

        bce = bcPreset.getFaders();
        for(int i = 0; i < bce.length; i++)
            bce[i].init(i + 1, 2);

        return 0;
    }

    private static int check_footsw(int context, String tmp[], BCDevice bcDevice)
    {
        if(context == 258)
        {
            if(bcDevice.getGlobal() == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            switch(readvaluetoken(tmp[1]) & 0xfffffcff)
            {
            case 4: // '\004'
                return 0;

            case 5: // '\005'
                bcDevice.getGlobal().flags |= 2;
                return 0;

            case 6: // '\006'
                bcDevice.getGlobal().flags |= 1;
                return 0;
            }
            return 12;
        } else
        {
            return 23;
        }
    }

    private static int check_snapshot(int context, String tmp[], BCPreset bcPreset)
    {
        if(context == 259)
        {
            if(bcPreset == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            switch(readvaluetoken(tmp[1]) & 0xfffffcff)
            {
            case 0: // '\0'
                bcPreset.getPreset().flags &= 0xfffffbff;
                return 0;

            case 1: // '\001'
                bcPreset.getPreset().flags |= 0x400;
                return 0;
            }
            return 12;
        } else
        {
            return 23;
        }
    }

    private static int check_rxch(int context, String tmp[], BCDevice bcDevice)
    {
        if(context == 258)
        {
            if(bcDevice.getGlobal() == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            if(tmp[1].equals("off"))
            {
                bcDevice.getGlobal().flags &= 0xfffffdff;
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
                bcDevice.getGlobal().rxch = value - 1;
                bcDevice.getGlobal().flags |= 0x200;
                return 0;
            }
        }
        return 23;
    }

    private static int check_deviceid(int context, String tmp[], BCDevice bcDevice)
    {
        try {
			if(context != 258) { }
//			    break MISSING_BLOCK_LABEL_63;
			
			if(bcDevice.getGlobal() == null)
			    return 7;
			if(tmp.length < 2)
			    return 3;
			int value = Integer.parseInt(tmp[1]);
			
			if(value < 1 || value > 16) { }
//			    break MISSING_BLOCK_LABEL_55;
			
			bcDevice.getGlobal().deviceid = value - 1;
			return 0;
			//return 12;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			 return 10;
		        //return 23;			
		}
    }

    private static int check_lock(int context, String tmp[], BCPreset bcPreset)
    {
        if(context == 259)
        {
            if(bcPreset == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            switch(readvaluetoken(tmp[1]) & 0xfffffcff)
            {
            case 0: // '\0'
                bcPreset.getPreset().flags &= 0xfffff7ff;
                return 0;

            case 1: // '\001'
                bcPreset.getPreset().flags |= 0x800;
                return 0;
            }
            return 12;
        } else
        {
            return 23;
        }
    }

    private static int check_tx(int context, String tmp[], BCPreset bcPreset, BCElement bce)
    {
        int cnt = 1;
        switch(context)
        {
        case 259: 
            BCPresetHead bcph = bcPreset.getPreset();
            try
            {
                for(; cnt < tmp.length; cnt++)
                    try
                    {
                        bcph.txpool[cnt - 1] = Integer.parseInt(tmp[cnt]);
                    }
                    catch(NumberFormatException nfe1)
                    {
                        bcph.txpool[cnt - 1] = GetToken(tmp[cnt]);
                    }

            }
            catch(NumberFormatException nfe)
            {
                return 10;
            }
            return 0;

        case 260: 
        case 261: 
        case 262: 
            if(bce != null)
                try
                {
                    bce.flags |= 0x8000;
                    bce.tx = 0;
                    for(; cnt < tmp.length; cnt++)
                        try
                        {
                            bce.txpool[cnt - 1] = Integer.parseInt(tmp[cnt]);
                        }
                        catch(NumberFormatException nfe1)
                        {
                            bce.txpool[cnt - 1] = GetToken(tmp[cnt]);
                        }

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

    private static int check_resolution(int context, String tmp[], BCElement bce)
    {
        int cnt = 1;
        int value = 0;
        if(context == 260)
        {
            if(bce == null)
                return 7;
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

    private static int check_minmax(int context, String tmp[], BCElement bce)
    {
        switch(context)
        {
        case 260: 
        case 261: 
        case 262: 
            if(tmp.length != 3)
                return 3;
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

    private static int check_mode(int context, String tmp[], BCElement bce)
    {
        switch(context)
        {
        case 260: 
            if(bce == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            bce.flags |= 0x8000;
            bce.flags &= 0xfffffff0;
            switch(GetToken(tmp[1]))
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

            case 769: 
            case 770: 
            case 771: 
            case 772: 
            case 773: 
            case 774: 
            case 775: 
            case 776: 
            case 777: 
            case 778: 
            case 779: 
            case 780: 
            case 781: 
            case 782: 
            case 783: 
            default:
                return 12;

            case 768: 
                break;
            }
            return 0;

        case 261: 
            if(bce == null)
                return 12;
            if(tmp.length < 2)
                return 3;
            bce.flags &= 0xfffff9ff;
            switch(GetToken(tmp[1]))
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
        return 23;
    }

    private static int check_txinterval(int context, String tmp[], BCDevice bcDevice)
    {
        if(context == 258)
        {
            if(bcDevice.getGlobal() == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            try
            {
                bcDevice.getGlobal().txinterval = Integer.parseInt(tmp[1]) - 1;
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

    private static int check_override(int context, String tmp[], BCElement bce)
    {
        switch(context)
        {
        case 262: 
            if(bce == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            bce.flags |= 0x8000;
            switch(GetToken(tmp[1]))
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

    private static int check_motor(int context, String tmp[], BCElement bce)
    {
        switch(context)
        {
        case 262: 
            if(bce == null)
                return 7;
            if(tmp.length < 2)
                return 3;
            bce.flags |= 0x8000;
            switch(GetToken(tmp[1]))
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

    private static int check_default(int context, String tmp[], BCElement bce)
    {
        switch(context)
        {
        case 260: 
        case 261: 
        case 262: 
            if(tmp.length < 2)
                return 3;
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

    private static int check_local(int context, String tmp[], BCElement bce)
    {
        switch(context)
        {
        case 260: 
        case 261: 
        case 262: 
            return 0;
        }
        return 23;
    }

    private static int check_showvalue(int context, String tmp[], BCElement bce)
    {
        switch(context)
        {
        case 260: 
        case 261: 
        case 262: 
            if(bce == null)
                return 7;
            bce.flags |= 0x8000;
            switch(GetToken(tmp[1]))
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

    private static int check_easypar(int context, String tmp[], BCElement bce)
    {
        int retval = 0;
        switch(context)
        {
        case 260: 
        case 261: 
        case 262: 
            if(bce == null)
                return 7;
            bce.flags |= 0x8000;
            if((retval = readEasyPar(bce, tmp, context)) != 0)
                bce.flags &= 0xffff7fff;
            return retval;
        }
        return 23;
    }

    private static int check_keyoverride(int context, String tmp[], BCElement bce, int maxButtonNumber)
    {
        switch(context)
        {
        case 262: 
            if(bce == null)
                return 7;
            bce.flags |= 0x8000;
            if(GetToken(tmp[1]) == 768)
            {
                bce.flags &= 0xfffffeff;
                return 0;
            }
            int v = readval(tmp[1]);
            if(v >= 1 && v <= maxButtonNumber)
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

    private static int readEasyPar(BCElement bce, String tmp[], int type)
    {
        int frames[] = {
            0, 24, 25, 30, 30
        };
        int wp[] = new int[6];
        int iError = 0;
        wp[0] = (GetToken(tmp[1]) - GetToken("PC")) + 1;
        if(wp[0] == 7)
        {
            if(GetToken(tmp[2]) == 771)
            {
                wp[1] = 0;
            } else
            {
                wp[1] = readval(tmp[2]);
                if(wp[1] < 0 || wp[1] > 126)
                    iError = 2;
                else
                    wp[1]++;
            }
        } else
        {
            wp[1] = readval(tmp[2]);
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
                wp[2] = readvalueoff(tmp[3], 129);
                if(wp[2] > 128)
                {
                    iError = 3;
                    break;
                }
                wp[3] = readvalueoff(tmp[4], 129);
                if(wp[3] > 128)
                {
                    iError = 4;
                    break;
                }
                if(type != 261)
                    break;
                wp[4] = readvalueoff(tmp[5], 129);
                if(wp[4] > 128)
                    iError = 5;
                break;

            case 2: // '\002'
            case 3: // '\003'
                int cnt = 2;
                do
                {
                    if(cnt > 3)
                        break;
                    wp[cnt] = readval(tmp[cnt + 1]);
                    if(wp[cnt] < 0 || wp[cnt] > 16383 || wp[cnt] > 127 && wp[0] == 2)
                    {
                        iError = cnt + 1;
                        break;
                    }
                    cnt++;
                } while(true);
                if(type == 261)
                {
                    wp[cnt] = readvalueoff(tmp[cnt + 1]);
                    if(wp[cnt] < 0 || wp[cnt] > 16384)
                    {
                        iError = cnt + 1;
                        break;
                    }
                    wp[5] = GetToken(tmp[6]) - 1296;
                    if(wp[5] < 0 || wp[5] > 2)
                    {
                        iError = 6;
                        break;
                    }
                    if(wp[5] <= 1)
                        break;
                    wp[cnt] = Math.max(wp[cnt] - 1, 0);
                    int x = readval(tmp[7]);
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
                wp[4] = readval(tmp[5]);
                if(wp[4] < 0 || wp[4] > 16383)
                {
                    iError = 5;
                    break;
                }
                wp[5] = GetToken(tmp[6]) - 1312;
                if(wp[5] < 0 || wp[5] > 8)
                    iError = 6;
                break;

            case 4: // '\004'
                wp[2] = readval(tmp[3]);
                if(wp[2] < 0 || wp[2] > 127)
                {
                    iError = 3;
                    break;
                }
                wp[3] = readval(tmp[4]);
                if(wp[3] < 0 || wp[3] > 127)
                {
                    iError = 4;
                    break;
                }
                wp[5] = GetToken(tmp[5]) - 1296;
                if(wp[5] < 0 || wp[5] > 1)
                    iError = 6;
                break;

            case 5: // '\005'
                wp[3] = readval(tmp[3]);
                if(wp[3] > 127)
                    iError = 3;
                break;

            case 6: // '\006'
                if(GetToken(tmp[3]) == 771)
                {
                    wp[2] = 0;
                } else
                {
                    wp[2] = readval(tmp[3]);
                    if(wp[2] < 0 || wp[2] > 127)
                    {
                        iError = 3;
                        break;
                    }
                    wp[2]++;
                }
                wp[3] = readval(tmp[4]);
                if(wp[3] < 0 || wp[3] > 127)
                {
                    iError = 4;
                    break;
                }
                if(type == 261)
                {
                    wp[4] = readvalueoff(tmp[5]);
                    if(wp[4] < 0 || wp[4] > 128)
                    {
                        iError = 5;
                        break;
                    }
                    wp[5] = GetToken(tmp[6]) - 1296;
                    if(wp[5] < 0 || wp[5] > 2)
                    {
                        iError = 6;
                        break;
                    }
                    if(wp[5] <= 1)
                        break;
                    int x = readval(tmp[7]);
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
                wp[4] = readval(tmp[5]);
                if(wp[4] < 0 || wp[4] > 127)
                    iError = 5;
                break;

            case 7: // '\007'
                if(tmp.length < 6)
                {
                    iError = 259;
                    break;
                }
                wp[2] = GetToken(tmp[3]) - 1328;
                if(wp[2] < 0 || wp[2] > 7)
                {
                    iError = 3;
                    break;
                }
                wp[5] = GetToken(tmp[5]) - 1360;
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
                an = readval(n1);
                if(an > 23)
                {
                    iError = -3;
                    break;
                }
                mmc = an * 60;
                an = readval(n2);
                if(an > 59)
                {
                    iError = -4;
                    break;
                }
                mmc += an;
                wp[3] = mmc;
                an = readval(n3);
                if(an > 59)
                {
                    iError = -5;
                    break;
                }
                mmc = an * frames[wp[5]];
                an = readval(n4);
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
                wp[2] = GetToken(tmp[3]) - 1344;
                if(wp[2] < 0 || wp[2] > 14)
                {
                    iError = 3;
                    break;
                }
                wp[3] = readval(tmp[4]);
                if(wp[3] > 127)
                {
                    iError = 4;
                    break;
                }
                if(type == 261)
                {
                    wp[4] = readvalueoff(tmp[5]);
                    if(wp[4] < 0 || wp[4] > 128)
                    {
                        iError = 5;
                        break;
                    }
                    wp[5] = GetToken(tmp[6]) - 1296;
                    if(wp[5] < 0 || wp[5] > 1)
                        iError = 6;
                    break;
                }
                wp[4] = readval(tmp[5]);
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
            if(iError >= 256 && iError <= 511)
                value = iError & 0xff;
            else
                value = 0;
            for(int i = 0; i < tmp.length; i++)
                if(i == iError)
                    System.out.print("<<< " + tmp[i] + " >>> ");
                else
                    System.out.print(tmp[i] + " ");

            System.out.println("");
            if(value > 0)
                System.out.println("Error: " + BCLError.getErrorString(value));
            else
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

    public static boolean ignoreErrors = true;
    public static int lastError = 0;
    private static final int MIDIEXTTOKEN = 254;
    private static final int MPAR_val = 64;
    private static final int MPAR_val0 = 65;
    private static final int MPAR_val03 = 66;
    private static final int MPAR_val47 = 67;
    private static final int MPAR_val811 = 68;
    private static final int MPAR_val1213 = 69;
    private static final int MPAR_val713 = 70;
    private static final int MPAR_val17 = 71;
    private static final int MPAR_reloffs = 72;
    private static final int MPAR_relsign = 73;
    private static final int MPAR_rel2s = 80;
    private static final int MPAR_cks1 = 81;
    private static final int MPAR_cks2 = 82;
    private static final int MPAR_cks3 = 83;
    private static final int MPAR_ifp = 84;
    private static final int MPAR_ifn = 85;
    private static final int MPAR_ntimes = 86;
    public static final String FILEID = "BCL";
    public static final String FILEVER = "0002";
    public static final String FILETYPE1 = "DEVICE";
    public static final String FILETYPE2 = "PRESET";
    public static final String FILETYPE3 = "ELEMENT";
    public static final String stringFADER = "FADER";
    public static final String stringROTARY = "ROTARY";
    public static final int TYPE_MODEL = 28672;
    public static final int TYPE_FILETYPE = 28673;
    public static final int TYPE_FILEREV = 28674;
    public static final int TYPE_REV = 257;
    public static final int TYPE_GLOBAL = 258;
    public static final int TYPE_PRESET = 259;
    public static final int TYPE_ENCODER = 260;
    public static final int TYPE_BUTTON = 261;
    public static final int TYPE_FADER = 262;
    public static final int TYPE_STORE = 265;
    public static final int TYPE_RECALL = 266;
    public static final int TYPE_END = 256;
    public static final int CTRL_UNDEFINED = -1;
    public static final int CTRL_ENCODER = 0;
    public static final int CTRL_BUTTON = 1;
    public static final int CTRL_FADER = 2;
    public static final int FADER = 20;
    public static final int ROTARY = 21;
    public static final int NANO44 = 23;
    public static final byte msgHead[] = {
        -16, 32, 50, -1, 0, 0
    };
    public static final int ET_OFF = 0;
    public static final int ET_PC = 1;
    public static final int ET_CC = 2;
    public static final int ET_NRPN = 3;
    public static final int ET_NOTE = 4;
    public static final int ET_PB = 5;
    public static final int ET_AT = 6;
    public static final int ET_MMC = 7;
    public static final int ET_GSXG = 8;
    public static final int ET_SPC1 = 9;
    public static final int ET_SPC2 = 10;
    public static final int EASY_CMD = 0;
    public static final int EASY_CHANNEL = 1;
    public static final int EASY_PARAMETER = 2;
    public static final int EASY_VALUE1 = 3;
    public static final int EASY_VALUE2 = 4;
    public static final int EASY_CTRLMODE = 5;
    public static final String scriptLineBreak = ";";
    public static final String easy1string[] = {
        "OFF", "PC", "CC", "NRPN", "NOTE", "PB", "AT", "MMC", "GS/XG", "SPC1", 
        "SPC2"
    };
    public static final bclToken easyStringToken[] = {
        new bclToken(0, "OFF"), new bclToken(1, "PC"), new bclToken(2, "CC"), new bclToken(3, "NRPN"), new bclToken(4, "NOTE"), new bclToken(5, "PB"), new bclToken(6, "AT"), new bclToken(7, "MMC"), new bclToken(8, "GS/XG"), new bclToken(9, "SPC1"), 
        new bclToken(10, "SPC2")
    };
    public static final bclToken scriptToken[] = {
        new bclToken(28672, "$deviceType"), new bclToken(28673, "$fileType"), new bclToken(28674, "$fileRev"), new bclToken(28688, "DEVICE"), new bclToken(28689, "PRESET"), new bclToken(28690, "ELEMENT"), new bclToken(28948, "FADER"), new bclToken(28949, "ROTARY"), new bclToken(257, "$rev"), new bclToken(258, "$global"), 
        new bclToken(259, "$preset"), new bclToken(260, "$encoder"), new bclToken(261, "$button"), new bclToken(262, "$fader"), new bclToken(265, "$store"), new bclToken(266, "$recall"), new bclToken(256, "$end"), new bclToken(512, ".midimode"), new bclToken(513, ".request"), new bclToken(514, ".startup"), 
        new bclToken(515, ".egroups"), new bclToken(516, ".fkeys"), new bclToken(517, ".name"), new bclToken(518, ".init"), new bclToken(519, ".footsw"), new bclToken(520, ".snapshot"), new bclToken(521, ".rxch"), new bclToken(522, ".deviceid"), new bclToken(523, ".lock"), new bclToken(544, ".tx"), 
        new bclToken(546, ".xref"), new bclToken(547, ".resolution"), new bclToken(548, ".minmax"), new bclToken(549, ".mode"), new bclToken(550, ".txinterval"), new bclToken(551, ".override"), new bclToken(552, ".motor"), new bclToken(553, ".rangeon"), new bclToken(554, ".default"), new bclToken(555, ".local"), 
        new bclToken(556, ".showvalue"), new bclToken(557, ".easypar"), new bclToken(558, ".keyoverride"), new bclToken(768, "off"), new bclToken(769, "on"), new bclToken(770, "last"), new bclToken(771, "all"), new bclToken(772, "norm"), new bclToken(773, "auto"), new bclToken(774, "inv"), 
        new bclToken(776, "U-1"), new bclToken(777, "U-2"), new bclToken(778, "U-3"), new bclToken(779, "U-4"), new bclToken(780, "S-1"), new bclToken(781, "S-2"), new bclToken(782, "S-3"), new bclToken(783, "S-4"), new bclToken(784, "1dot"), new bclToken(785, "1dot/off"), 
        new bclToken(786, "12dot"), new bclToken(787, "12dot/off"), new bclToken(788, "bar"), new bclToken(789, "bar/off"), new bclToken(790, "spread"), new bclToken(791, "pan"), new bclToken(792, "qual"), new bclToken(793, "cut"), new bclToken(794, "damp"), new bclToken(800, "down"), 
        new bclToken(801, "toggle"), new bclToken(802, "updown"), new bclToken(817, "move"), new bclToken(818, "pickup"), new bclToken(819, "motor"), new bclToken(1088, "val"), new bclToken(1088, "val0.6"), new bclToken(1089, "val0"), new bclToken(1090, "val0.3"), new bclToken(1091, "val4.7"), 
        new bclToken(1092, "val8.11"), new bclToken(1093, "val12.13"), new bclToken(1094, "val7.13"), new bclToken(1095, "val1.7"), new bclToken(1096, "reloffs"), new bclToken(1097, "relsign"), new bclToken(1098, "rel2s"), new bclToken(1099, "cks-1"), new bclToken(1100, "cks-2"), new bclToken(1101, "cks-3"), 
        new bclToken(1102, "ifp"), new bclToken(1103, "ifn"), new bclToken(1104, "ntimes"), new bclToken(1280, "PC"), new bclToken(1281, "CC"), new bclToken(1282, "NRPN"), new bclToken(1283, "NOTE"), new bclToken(1284, "PB"), new bclToken(1285, "AT"), new bclToken(1286, "MMC"), 
        new bclToken(1287, "GS/XG"), new bclToken(1296, "toggleoff"), new bclToken(1297, "toggleon"), new bclToken(1298, "increment"), new bclToken(1312, "absolute"), new bclToken(1313, "relative-1"), new bclToken(1314, "relative-2"), new bclToken(1315, "relative-3"), new bclToken(1316, "inc/dec"), new bclToken(1317, "absolute/14"), 
        new bclToken(1318, "relative-1/14"), new bclToken(1319, "relative-2/14"), new bclToken(1320, "relative-3/14"), new bclToken(1328, "play"), new bclToken(1329, "pause"), new bclToken(1330, "stop"), new bclToken(1331, "fwd"), new bclToken(1332, "rew"), new bclToken(1333, "locate"), new bclToken(1334, "punch-in"), 
        new bclToken(1335, "punch-out"), new bclToken(1344, "cutoff"), new bclToken(1345, "resonance"), new bclToken(1346, "v-rate"), new bclToken(1347, "v-depth"), new bclToken(1348, "v-delay"), new bclToken(1349, "eg-attack"), new bclToken(1350, "eg-decay"), new bclToken(1351, "eg-release"), new bclToken(1352, "modulation"), 
        new bclToken(1353, "p-time"), new bclToken(1354, "volume"), new bclToken(1355, "panorama"), new bclToken(1356, "rev-send"), new bclToken(1357, "crs-send"), new bclToken(1358, "dly-send"), new bclToken(1360, "noloc"), new bclToken(1361, "24f"), new bclToken(1362, "25f"), new bclToken(1363, "30df"), 
        new bclToken(1364, "30f"), new bclToken(33025, "$rev"), new bclToken(33026, "$global"), new bclToken(33027, "$preset"), new bclToken(33028, "$encoder"), new bclToken(33029, "$button"), new bclToken(33030, "$fader"), new bclToken(33033, "$store"), new bclToken(33034, "$recall"), new bclToken(33024, "$end"), 
        new bclToken(33280, ".midimode"), new bclToken(33281, ".request"), new bclToken(33282, ".startup"), new bclToken(33283, ".egroups"), new bclToken(33284, ".fkeys"), new bclToken(33285, ".name"), new bclToken(33286, ".init"), new bclToken(33287, ".footsw"), new bclToken(33288, ".snapshot"), new bclToken(33289, ".rxch"), 
        new bclToken(33290, ".deviceid"), new bclToken(33291, ".lock"), new bclToken(33312, ".tx"), new bclToken(33314, ".xref"), new bclToken(33315, ".resolution"), new bclToken(33316, ".minmax"), new bclToken(33317, ".mode"), new bclToken(33318, ".txinterval"), new bclToken(33319, ".override"), new bclToken(33320, ".motor"), 
        new bclToken(33321, ".rangeon"), new bclToken(33322, ".default"), new bclToken(33323, ".local"), new bclToken(33324, ".showvalue"), new bclToken(33325, ".easypar"), new bclToken(33326, ".keyoverride"), new bclToken(33536, "Off"), new bclToken(33537, "On"), new bclToken(33538, "Last"), new bclToken(33539, "All"), 
        new bclToken(33540, "Norm"), new bclToken(33541, "Auto"), new bclToken(33542, "Inv"), new bclToken(33544, "Usb-1"), new bclToken(33545, "Usb-2"), new bclToken(33546, "Usb-3"), new bclToken(33547, "Usb-4"), new bclToken(33548, "S-1"), new bclToken(33549, "S-2"), new bclToken(33550, "S-3"), 
        new bclToken(33551, "S-4"), new bclToken(33552, "1 Dot"), new bclToken(33553, "1 Dot/Off"), new bclToken(33554, "1-2 Dot"), new bclToken(33555, "1-2 Dot/Off"), new bclToken(33556, "Bar"), new bclToken(33557, "Bar/Off"), new bclToken(33558, "Spread"), new bclToken(33559, "Pan"), new bclToken(33560, "Qual"), 
        new bclToken(33561, "Cut"), new bclToken(33562, "Damp"), new bclToken(33568, "Down"), new bclToken(33569, "Toggle"), new bclToken(33570, "Updown"), new bclToken(33585, "Move"), new bclToken(33586, "Pickup"), new bclToken(33587, "Motor"), new bclToken(33856, "Val"), new bclToken(33856, "Val0.6"), 
        new bclToken(33857, "Val0"), new bclToken(33858, "Val0.3"), new bclToken(33859, "Val4.7"), new bclToken(33860, "Val8.11"), new bclToken(33861, "Val12.13"), new bclToken(33862, "Val7.13"), new bclToken(33863, "Val1.7"), new bclToken(33864, "Reloffs"), new bclToken(33865, "Relsign"), new bclToken(33866, "Rel2s"), 
        new bclToken(33867, "Cks-1"), new bclToken(33868, "Cks-2"), new bclToken(33869, "Cks-3"), new bclToken(33870, "Ifp"), new bclToken(33871, "Ifn"), new bclToken(33872, "Ntimes"), new bclToken(34048, "Program Change"), new bclToken(34049, "Control Change"), new bclToken(34050, "NRPN"), new bclToken(34051, "Note"), 
        new bclToken(34052, "Pitch Bend"), new bclToken(34053, "After Touch"), new bclToken(34054, "MMC"), new bclToken(34055, "GS/XG"), new bclToken(34064, "Toggle Off"), new bclToken(34065, "Toggle On"), new bclToken(34066, "Increment"), new bclToken(34080, "Absolute"), new bclToken(34081, "Relative-1"), new bclToken(34082, "Relative-2"), 
        new bclToken(34083, "Relative-3"), new bclToken(34084, "Inc / Dec"), new bclToken(34085, "Absolute (14 bit)"), new bclToken(34086, "Relative-1 (14 bit)"), new bclToken(34087, "Relative-2 (14 bit)"), new bclToken(34088, "Relative-3 (14 bit)"), new bclToken(34096, "Play"), new bclToken(34097, "Pause"), new bclToken(34098, "Stop"), new bclToken(34099, "Forward"), 
        new bclToken(34100, "Rewind"), new bclToken(34101, "Locate"), new bclToken(34102, "Punch-In"), new bclToken(34103, "Punch-Out"), new bclToken(34112, "Cutoff"), new bclToken(34113, "Resonance"), new bclToken(34114, "Vlb.-Rate"), new bclToken(34115, "Vlb.-Depth"), new bclToken(34116, "Vlb.-Delay"), new bclToken(34117, "EG-Attack"), 
        new bclToken(34118, "EG-Decay"), new bclToken(34119, "EG-Release"), new bclToken(34120, "Modulation"), new bclToken(34121, "Porta.-Time"), new bclToken(34122, "Volume"), new bclToken(34123, "Panorama"), new bclToken(34124, "Rev-Send"), new bclToken(34125, "Chor.-Send"), new bclToken(34126, "Delay-Send"), new bclToken(34128, "Noloc"), 
        new bclToken(34129, "24 Frames"), new bclToken(34130, "25 Frames"), new bclToken(34131, "30 Dropframes"), new bclToken(34132, "30 Frames")
    };

}
