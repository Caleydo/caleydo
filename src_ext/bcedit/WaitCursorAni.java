// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCActionListener.java

package bcedit;

import java.awt.*;

// Referenced classes of package bcedit:
//            bcedit, BCDefaults, MainFrame

class WaitCursorAni extends Thread
{

    public WaitCursorAni()
    {
        bStop = false;
        cursorOwner = null;
        cursorOwner = bcedit.app;
    }

    public WaitCursorAni(Object o)
    {
        bStop = false;
        cursorOwner = null;
        cursorOwner = o;
    }

    public void run()
    {
        java.awt.Cursor cOld = BCDefaults.curDefault;
        int i = 0;
        do
        {
            if(bStop)
                break;
            if(cursorOwner instanceof Component)
                ((Component)cursorOwner).setCursor(BCDefaults.curWaitAni[i++]);
            else
            if(cursorOwner instanceof Frame)
                ((Frame)cursorOwner).setCursor(BCDefaults.curWaitAni[i++]);
            else
            if(cursorOwner instanceof Window)
                ((Window)cursorOwner).setCursor(BCDefaults.curWaitAni[i++]);
            else
                bcedit.app.setCursor(BCDefaults.curWaitAni[i++]);
            try
            {
                sleep(80L);
            }
            catch(InterruptedException iE)
            {
                bStop = true;
            }
            if(i >= BCDefaults.curWaitAni.length)
                i = 0;
        } while(true);
        if(cursorOwner instanceof Component)
            ((Component)cursorOwner).setCursor(cOld);
        else
        if(cursorOwner instanceof Frame)
            ((Frame)cursorOwner).setCursor(cOld);
        else
        if(cursorOwner instanceof Window)
            ((Window)cursorOwner).setCursor(cOld);
        else
            bcedit.app.setCursor(cOld);
    }

    public void stopit()
    {
        bStop = true;
    }

    boolean bStop;
    Object cursorOwner;
}
