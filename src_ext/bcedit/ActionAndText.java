// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ActionAndText.java

package bcedit;


public class ActionAndText
{

    public ActionAndText(String action, String text)
    {
        Text = text;
        Action = action;
    }

    public String setAction(String s)
    {
        String x = Action;
        Action = s;
        return x;
    }

    public String setText(String s)
    {
        String x = Text;
        Text = s;
        return x;
    }

    public String getAction()
    {
        return Action;
    }

    public String getText()
    {
        return Text;
    }

    private String Text;
    private String Action;
}
