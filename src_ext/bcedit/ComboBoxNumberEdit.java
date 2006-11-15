// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ComboBoxNumberEdit.java

package bcedit;

import java.awt.*;
import java.io.PrintStream;
import javax.swing.JComboBox;
import javax.swing.JTextField;

// Referenced classes of package bcedit:
//            CBNumberEditor, JNumberField

public class ComboBoxNumberEdit extends JComboBox
{

    public ComboBoxNumberEdit()
    {
        setEditor(new CBNumberEditor());
        setEditable(true);
    }

    public String getText()
    {
        if(editor instanceof CBNumberEditor)
            return ((CBNumberEditor)editor).getText();
        else
            return null;
    }

    public void setText(String text)
    {
        if(editor instanceof CBNumberEditor)
            ((CBNumberEditor)editor).setText(text);
    }

    public void setMinValue(int minValue)
    {
        if(editor instanceof CBNumberEditor)
            ((CBNumberEditor)editor).setMinValue(minValue);
    }

    public void setMaxValue(int maxValue)
    {
        if(editor instanceof CBNumberEditor)
            ((CBNumberEditor)editor).setMaxValue(maxValue);
    }

    public void setEnabled(boolean flag)
    {
        if(flag)
            setBackground(new Color(0xd6d3ce));
        else
            setBackground(new Color(0xffffff));
        super.setEnabled(flag);
    }

    public void addItem(Object anObject)
    {
        super.addItem(anObject);
        myItemchanged();
    }

    public void insertItemAt(Object anObject, int index)
    {
        super.insertItemAt(anObject, index);
        myItemchanged();
    }

    public void removeItem(Object anObject)
    {
        super.removeItem(anObject);
        myItemchanged();
    }

    public void removeItemAt(int anIndex)
    {
        super.removeItemAt(anIndex);
        myItemchanged();
    }

    public void removeAllItems()
    {
        super.removeAllItems();
        myItemchanged();
    }

    private void myItemchanged()
    {
        if(this.editor instanceof CBNumberEditor)
        {
            JTextField editor = (JTextField)((CBNumberEditor)this.editor).getEditorComponent();
            if(editor instanceof JNumberField)
            {
                int cnt = getItemCount();
                if(cnt == 0)
                {
                    ((JNumberField)editor).acceptedEntry = null;
                } else
                {
                    String itemList[] = new String[cnt];
                    for(int i = 0; i < cnt; i++)
                        itemList[i] = getItemAt(i).toString();

                    ((JNumberField)editor).acceptedEntry = itemList;
                }
            } else
            {
                System.out.println("cannot assign editor");
            }
        } else
        {
            System.out.println("cannot assign this.editor");
        }
    }

    public Dimension getPreferredSize()
    {
        int cnt = getItemCount();
        if(cnt == 0)
        {
            Dimension d = new Dimension(0, 0);
            FontMetrics f = getFontMetrics(getFont());
            d.width = f.stringWidth("65536") + 2 * f.charWidth(' ') + 4;
            d.height = f.getHeight() + 4;
            return d;
        } else
        {
            return super.getPreferredSize();
        }
    }
}
