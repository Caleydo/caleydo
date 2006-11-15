// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   splash.java

package bcedit;

import java.awt.*;
import java.io.PrintStream;

class loadSingleGraphic extends Component
{

    loadSingleGraphic()
    {
    }

    public synchronized Image getImage(String filename)
    {
        ClassLoader cl = getClass().getClassLoader();
        int i = 0;
        Image data = null;
        String fname = ImageSubDir + "/" + filename;
        java.net.URL urlx = cl.getResource(fname);
        if(urlx == null)
        {
            data = null;
            System.out.println("File: " + fname + " not found!");
        } else
        {
            try
            {
                data = getToolkit().createImage(urlx);
                if(!prepareImage(data, this))
                    while((checkImage(data, this) & 3) != 3) 
                        try
                        {
                            wait(10L);
                        }
                        catch(IllegalArgumentException e1)
                        {
                            System.out.println(e1.getMessage());
                        }
                        catch(IllegalMonitorStateException e2)
                        {
                            System.out.println(e2.getMessage());
                        }
                        catch(InterruptedException e3)
                        {
                            System.out.println(e3.getMessage());
                        }
            }
            catch(Exception e)
            {
                data = null;
                System.out.println(e.getMessage());
            }
        }
        return data;
    }

    static String ImageSubDir = "grafik";

}
