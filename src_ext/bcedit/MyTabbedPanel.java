// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MyTabbedPanel.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            BCActionListener, SizedPanel, TPanel, BCInternalNotify, 
//            BCDefaults

public class MyTabbedPanel extends JPanel
    implements MouseListener, FocusListener, KeyListener
{

    public MyTabbedPanel(String name)
    {
        tabNames = null;
        tabComp = null;
        descline = null;
        card = null;
        activeContainer = -1;
        bclistener = null;
        fillColor = BCDefaults.bgPanelColor;
        bclistener = new BCActionListener();
        tabNames = new Vector();
        tabComp = new Vector();
        descline = new JPanel();
        card = new JPanel();
        setName(name);
        descline.setOpaque(false);
        descline.setBackground(fillColor);
        SizedPanel sp = new SizedPanel(8, 8);
        sp.setOpaque(false);
        descline.add(sp);
        setOpaque(false);
        setLayout(new BorderLayout());
        add("North", descline);
        descline.setLayout(new FlowLayout(0, 0, 0));
        card.setLayout(new CardLayout());
        add("Center", card);
    }

    private int findContainerIndex(String desc)
    {
        for(int i = 0; i < tabNames.size(); i++)
            if(((TPanel)tabNames.elementAt(i)).text.equals(desc))
                return i;

        return -1;
    }

    public void setFocusTo(int idx, boolean b)
    {
        TPanel p = (TPanel)tabNames.elementAt(idx);
        p.setFocused(b);
    }

    public void setContainer(int idx)
    {
        if(idx >= 0 && idx < tabNames.size())
        {
            TPanel p = (TPanel)tabNames.elementAt(activeContainer);
            if(((TPanel)tabNames.elementAt(idx)).isEnabled())
            {
                CardLayout layout = (CardLayout)card.getLayout();
                p.setRaised(false);
                activeContainer = idx;
                p = (TPanel)tabNames.elementAt(activeContainer);
                p.setRaised(true);
                layout.show(card, p.text);
                (new BCInternalNotify(this, "NewContainerTabActivated")).start();
            } else
            {
                activeContainer = idx;
            }
        }
    }

    public void setContainer(Component comp)
    {
        int i = 0;
        do
        {
            if(i >= tabNames.size())
                break;
            if(tabNames.elementAt(i) == comp)
            {
                setContainer(i);
                break;
            }
            i++;
        } while(true);
    }

    public void setContainer(String desc)
    {
        int i;
        if((i = findContainerIndex(desc)) >= 0)
            setContainer(i);
    }

    private void removeContainer(int idx)
    {
        if(idx < 0)
        {
            return;
        } else
        {
            TPanel p = (TPanel)tabNames.elementAt(idx);
            p.removeMouseListener(this);
            p.removeFocusListener(this);
            p.removeKeyListener(this);
            tabNames.removeElementAt(idx);
            card.remove((Component)tabComp.elementAt(idx));
            tabComp.removeElementAt(idx);
            return;
        }
    }

    public void removeCard(String desc)
    {
        removeContainer(findContainerIndex(desc));
    }

    public void addCard(String desc, Component comp)
    {
        TPanel p = new TPanel(desc);
        tabNames.addElement(p);
        tabComp.addElement(comp);
        descline.add(p);
        card.add(desc, comp);
        if(activeContainer < 0)
        {
            activeContainer = 0;
            p.setRaised(true);
        } else
        {
            p.setRaised(false);
        }
        p.addMouseListener(this);
        p.addFocusListener(this);
        p.addKeyListener(this);
    }

    public void showCard(int idx, boolean bFlag)
    {
        TPanel p = (TPanel)tabNames.elementAt(idx);
        JComponent c = (JComponent)tabComp.elementAt(idx);
        p.setEnabled(bFlag);
        c.setEnabled(bFlag);
        p.setVisible(bFlag);
        c.setVisible(bFlag);
    }

    public void enableCard(String desc, boolean bFlag)
    {
        int i;
        if((i = findContainerIndex(desc)) >= 0)
        {
            TPanel p = (TPanel)tabNames.elementAt(i);
            JComponent c = (JComponent)tabComp.elementAt(i);
            p.setEnabled(bFlag);
            c.setEnabled(bFlag);
        }
    }

    public boolean isCardPresent(String desc)
    {
        return findContainerIndex(desc) != -1;
    }

    public int getActiveTab()
    {
        return activeContainer;
    }

    public String getActiveTabName()
    {
        return ((TPanel)tabNames.elementAt(activeContainer)).text;
    }

    public Component getActiveTabComponent()
    {
        return (Component)tabComp.elementAt(activeContainer);
    }

    public void focusGained(FocusEvent e)
    {
        int i = 0;
        do
        {
            if(i >= tabNames.size())
                break;
            if(tabNames.elementAt(i) == e.getSource())
            {
                setFocusTo(i, true);
                break;
            }
            i++;
        } while(true);
    }

    public void focusLost(FocusEvent e)
    {
        int i = 0;
        do
        {
            if(i >= tabNames.size())
                break;
            if(tabNames.elementAt(i) == e.getSource())
            {
                setFocusTo(i, false);
                break;
            }
            i++;
        } while(true);
    }

    public void mouseClicked(MouseEvent e)
    {
        if(!((Component)e.getSource()).hasFocus())
        {
            ((Component)e.getSource()).requestFocus();
            setContainer((Component)e.getSource());
        } else
        {
            Object comp = e.getSource();
            int i = 0;
            do
            {
                if(i >= tabNames.size())
                    break;
                if(tabNames.elementAt(i) == comp)
                {
                    if(activeContainer != i)
                        setContainer(i);
                    break;
                }
                i++;
            } while(true);
        }
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    public void keyPressed(KeyEvent e)
    {
        int cntRepeat = 0;
        if(e.getKeyCode() == 32)
        {
            if(cntRepeat++ == 0)
                setContainer((Component)e.getSource());
        } else
        {
            cntRepeat = 0;
        }
    }

    public void keyReleased(KeyEvent keyevent)
    {
    }

    public void keyTyped(KeyEvent keyevent)
    {
    }

    private Vector tabNames;
    private Vector tabComp;
    private JPanel descline;
    private JPanel card;
    private int activeContainer;
    private BCActionListener bclistener;
    public Color fillColor;
}
