// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GPanel.java

package bcedit;

import java.awt.Image;
import java.awt.Toolkit;

// Referenced classes of package bcedit:
//            GPanel, BCImages

class GPanelAnimation extends Thread
{

    public GPanelAnimation(Image imageList[], long t_Delay, GPanel gp)
    {
        img = null;
        gpanel = null;
        bStop = false;
        img = new Image[imageList.length + 1];
        int i;
        for(i = 0; i < img.length - 1; i++)
            img[i] = imageList[i];

        img[i] = gp.getImage();
        gpanel = gp;
        tDelay = t_Delay;
    }

    public GPanelAnimation(String filename[], long t_Delay, Toolkit tk, GPanel gp)
    {
        img = null;
        gpanel = null;
        bStop = false;
        img = new Image[filename.length + 1];
        int i;
        for(i = 0; i < img.length - 1; i++)
            img[i] = tk.getImage(filename[i]);

        img[i] = gp.getImage();
        gpanel = gp;
        tDelay = t_Delay;
    }

    public GPanelAnimation(String name[], long t_Delay, GPanel gp)
    {
        img = null;
        gpanel = null;
        bStop = false;
        img = new Image[name.length + 1];
        int i;
        for(i = 0; i < img.length - 1; i++)
            img[i] = BCImages.getImage(name[i]);

        img[i] = gp.getImage();
        gpanel = gp;
        tDelay = t_Delay;
    }

    public void run()
    {
        int i = 0;
        while(!bStop) 
        {
            long was = System.currentTimeMillis();
            gpanel.setImage(img[i++]);
            if(i >= img.length - 1)
                i = 0;
            try
            {
                sleep(tDelay);
            }
            catch(InterruptedException iE) { }
        }
        restoreImage();
    }

    public void restoreImage()
    {
        gpanel.setImage(img[img.length - 1]);
    }

    public void stop_anim()
    {
        bStop = true;
    }

    private Image img[];
    private GPanel gpanel;
    private boolean bStop;
    private long tDelay;
}
