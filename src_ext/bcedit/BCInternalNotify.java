// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BCInternalNotify.java

package bcedit;


// Referenced classes of package bcedit:
//            BCActionListener

public class BCInternalNotify extends Thread
{

    public BCInternalNotify(Object o, String actionToSend, long tm)
    {
        delay = 0L;
        bclistener = new BCActionListener();
        objectToSend = o;
        this.actionToSend = actionToSend;
        if(tm > 0L)
            delay = tm;
    }

    public BCInternalNotify(Object o, String actionToSend)
    {
        delay = 0L;
        bclistener = new BCActionListener();
        objectToSend = o;
        this.actionToSend = actionToSend;
    }

    public void run()
    {
        if(delay > 0L)
            try
            {
                sleep(delay);
            }
            catch(Exception e) { }
        bclistener.internalNotify(objectToSend, actionToSend);
    }

    private BCActionListener bclistener;
    Object objectToSend;
    String actionToSend;
    long delay;
}
