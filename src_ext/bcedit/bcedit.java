// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   bcedit.java

package bcedit;

import java.io.PrintStream;

// Referenced classes of package bcedit:
//            splash, BCSettings, BCImages, MainFrame

public class bcedit
{

    public bcedit()
    {
    }

    public static final void main(String args[])
    {
        splash s = new splash();
        bcSettings = new BCSettings();
        bcImages = new BCImages();
        for(int i = 0; i < args.length; i++)
            System.out.println(args[i]);

        do
        {
            BCImages _tmp = bcImages;
            if(BCImages.ready != 0)
            {
                BCImages _tmp1 = bcImages;
                System.out.println(BCImages.ready);
            } else
            {
                app = new MainFrame();
                app.show();
                s.dispose();
                app.run();
                return;
            }
        } while(true);
    }

    public static BCImages bcImages = null;
    public static BCSettings bcSettings = null;
    public static MainFrame app = null;

}
