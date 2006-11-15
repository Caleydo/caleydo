// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BcEditMenu.java

package bcedit;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;

// Referenced classes of package bcedit:
//            MyMenuItem

class LoadMenu
{

    public LoadMenu(String menufile)
    {
        vmenu = new Vector();
        int lineNumber = 0;
        int menulevel = 0;
        BufferedReader in = null;
        ClassLoader cl = getClass().getClassLoader();
        try
        {
            in = new BufferedReader(new FileReader(menufile));
        }
        catch(Exception e1_1)
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(cl.getResourceAsStream(menufile)));
            }
            catch(Exception e1)
            {
                System.out.println("LoadMenu:File \"" + menufile + "\" not found.");
                return;
            }
        }
        try
        {
            do
            {
                if(!in.ready())
                    break;
                boolean bFound = false;
                boolean bIsItem = false;
                lineNumber++;
                String line = in.readLine().trim();
                if(line.length() == 0 || line.charAt(0) == '#')
                    continue;
                if(line.substring(0, 4).equals("endm"))
                {
                    vmenu.addElement(null);
                    menulevel--;
                    continue;
                }
                if(line.substring(0, 4).equals("menu"))
                {
                    menulevel++;
                    bFound = true;
                } else
                if(line.substring(0, 4).equals("item"))
                {
                    bFound = true;
                    bIsItem = true;
                } else
                {
                    System.out.println("Unknown command at line " + lineNumber + " in file " + menufile);
                    continue;
                }
                if(!bFound)
                    continue;
                int whpos;
                for(whpos = 4; line.charAt(whpos) == ' ' || line.charAt(whpos) == '\t'; whpos++);
                if(line.charAt(whpos) == '=')
                {
                    whpos++;
                } else
                {
                    System.out.println("LoadMenu:Syntax error at Line " + lineNumber + " in file " + menufile);
                    continue;
                }
                if(bIsItem)
                {
                    if(line.substring(whpos).equals("seperator"))
                    {
                        vmenu.addElement(new MyMenuItem("-", "VK_UNDEFINED", "NO_MASK", "VK_UNDEFINED", "", true, 1));
                    } else
                    {
                        int elemcnt = 0;
                        StringTokenizer tokens = new StringTokenizer(line.substring(whpos), ",");
                        int numel = Math.max(tokens.countTokens(), 6);
                        String elem[] = new String[numel];
                        while(elemcnt < numel) 
                            elem[elemcnt++] = null;
                        elemcnt = 0;
                        elem[5] = new String("true");
                        while(tokens.hasMoreElements()) 
                        {
                            int offset = 0;
                            for(elem[elemcnt] = tokens.nextToken(); elem[elemcnt].charAt(offset) == ' ' || elem[elemcnt].charAt(offset) == '\t'; offset++);
                            elem[elemcnt] = elem[elemcnt].substring(offset);
                            elemcnt++;
                        }
                        vmenu.addElement(new MyMenuItem(elem[0], elem[1], elem[2], elem[3], elem[4], Boolean.valueOf(elem[5]).booleanValue(), 1));
                    }
                } else
                {
                    int elemcnt = 0;
                    StringTokenizer tokens = new StringTokenizer(line.substring(whpos), ",");
                    int numel = Math.max(tokens.countTokens(), 6);
                    String elem[] = new String[numel];
                    while(elemcnt < numel) 
                        elem[elemcnt++] = null;
                    elemcnt = 0;
                    for(elem[5] = new String("true"); tokens.hasMoreElements(); elem[elemcnt++] = tokens.nextToken());
                    vmenu.addElement(new MyMenuItem(elem[0], elem[1], elem[2], elem[3], elem[4], Boolean.valueOf(elem[5]).booleanValue(), 0));
                }
            } while(true);
            in.close();
        }
        catch(IOException e2)
        {
            System.out.print("LoadMenu:Exception caught while reading: ");
            System.out.print(e2.getMessage());
        }
    }

    public Vector vmenu;
}
