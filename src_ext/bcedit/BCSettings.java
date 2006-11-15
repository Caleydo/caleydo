// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCSettings.java

package bcedit;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

// Referenced classes of package bcedit:
//            MySettings

public class BCSettings
{

    public BCSettings()
    {
        if(settings == null)
            settings = new Vector();
        if(settings.size() == 0)
        {
            String userhome = System.getProperty("user.home");
            filename = userhome + System.getProperty("file.separator") + filename;
            readIniFile();
        }
    }

    public boolean writeSettings()
    {
        try
        {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            String s = new String("# ----------------------------------------------------------");
            out.write(s, 0, s.length());
            out.newLine();
            s = new String("# configuration file for Behringer's B-Control Series Editor ");
            out.write(s, 0, s.length());
            out.newLine();
            for(int i = 0; i < settings.size(); i++)
            {
                MySettings m = (MySettings)settings.elementAt(i);
                s = m.key + "=" + m.value;
                out.write(s, 0, s.length());
                out.newLine();
            }

            s = new String("# EOF");
            out.write(s, 0, s.length());
            out.newLine();
            out.close();
        }
        catch(Exception e2)
        {
            System.out.println("Error while creating File: \"" + filename + "\"");
            System.out.println("System reports:" + e2.getMessage());
            JOptionPane.showMessageDialog(new JFrame(), "Error while creating File: \"" + filename + "\"" + "\n" + "System reports:" + e2.getMessage(), "Error creating file", 0);
            return false;
        }
        return true;
    }

    private boolean readIniFile()
    {
    	try {
	        BufferedReader in;
	        int lno;
	        lno = 0;
	        try
	        {
	            in = new BufferedReader(new FileReader(filename));
	        }
	        catch(FileNotFoundException e1)
	        {
	            store("storage location", System.getProperty("user.home") + System.getProperty("file.separator") + "BCEdit");
	            return writeSettings();
	        }
	        do
	        {
	            if(!in.ready())
	                break;
	            String line = in.readLine().trim();
	            lno++;
	            if(line.length() != 0 && line.charAt(0) != '#' && line.indexOf('=') != -1)
	            {
	                StringTokenizer tokens = new StringTokenizer(line, "=");
	                int num = tokens.countTokens();
	                if(num != 2)
	                {
	                    String errString = "Error in ini-File line: " + lno;
	                    System.out.println(errString);
	                    int offset = 0;
	                    String toks;
	                    for(errString = errString + "\n"; tokens.hasMoreElements(); errString = errString + "\n" + offset + " : " + toks)
	                    {
	                        toks = tokens.nextToken();
	                        System.out.println(" - " + ++offset + toks);
	                    }
	
	                    JOptionPane.showMessageDialog(new JFrame(), errString, "Error in ini-File", 0);
	                } else
	                {
	                    String k = tokens.nextToken().trim();
	                    String v = tokens.nextToken().trim();
	                    store(k, v);
	                }
	            }
	        } while(true);
	        
	        in.close();
	        String startpath = getValue("storage location");
	        if(startpath != null)
	        {
	            File checkFile = new File(startpath);
	            if(!checkFile.exists())
	                startpath = null;
	        }
	        if(startpath == null)
	        {
	            System.out.println("storage location is invalid... Falling back to default");
	            store("storage location", System.getProperty("user.home") + System.getProperty("file.separator") + "BCEdit");
	        }
	        return true;
        }
        catch(IOException e2) 
        {
	        //e2;
	        System.out.print("Exception caught while reading: \"" + filename + "\"");
	        System.out.print(e2.getMessage());
	        JOptionPane.showMessageDialog(new JFrame(), "Error while reading File: \"" + filename + "\"" + "\n" + "System reports:" + e2.getMessage(), "Error reading file", 0);
	        return false;
        }
    }

    public int getCount()
    {
        return settings.size();
    }

    public String getValue(String keyname)
    {
        for(int i = 0; i < settings.size(); i++)
            if(keyname.equals(((MySettings)settings.elementAt(i)).key))
                return ((MySettings)settings.elementAt(i)).value;

        return null;
    }

    public boolean getBooleanValue(String keyname)
    {
        return getBooleanValue(keyname, false);
    }

    public boolean getBooleanValue(String keyname, boolean bDefault)
    {
        String compareMe[] = null;
        String value = getValue(keyname);
        if(value == null)
            return bDefault;
        if(!bDefault)
            compareMe = (new String[] {
                "yes", "true", "1", "on"
            });
        else
            compareMe = (new String[] {
                "no", "false", "0", "off"
            });
        for(int i = 0; i < compareMe.length; i++)
            if(value.equalsIgnoreCase(compareMe[i]))
                return !bDefault;

        return bDefault;
    }

    public String getValue(int idx)
    {
        if(idx >= 0 && idx < settings.size())
            return ((MySettings)settings.elementAt(idx)).value;
        else
            return null;
    }

    public String getKey(int idx)
    {
        if(idx >= 0 && idx < settings.size())
            return ((MySettings)settings.elementAt(idx)).key;
        else
            return null;
    }

    public String store(String name, String contents)
    {
        String retval = null;
        for(int i = 0; i < settings.size(); i++)
            if(name.equals(((MySettings)settings.elementAt(i)).key))
            {
                retval = new String(((MySettings)settings.elementAt(i)).value);
                ((MySettings)settings.elementAt(i)).value = contents;
                return retval;
            }

        settings.add(new MySettings(name, contents));
        return retval;
    }

    public String append(String name, String contents)
    {
        char bsep = '\u0100';
        String retval = null;
        for(int i = 0; i < settings.size(); i++)
            if(name.equals(((MySettings)settings.elementAt(i)).key))
            {
                retval = new String(((MySettings)settings.elementAt(i)).value);
                contents = retval + bsep + contents;
                ((MySettings)settings.elementAt(i)).value = contents;
                return contents;
            }

        settings.add(new MySettings(name, contents));
        return contents;
    }

    static Vector settings = null;
    private static String filename = "bcedit.cfg";

}
