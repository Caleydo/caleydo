// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ListLabel.java

package bcedit;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import javax.swing.JPanel;

// Referenced classes of package bcedit:
//            BCDefaults

public class ListLabel extends JPanel
    implements MouseListener, FocusListener, KeyListener
{

    public ListLabel(String strlabel)
    {
        maxRad = 20;
        bgColor = new Color(0xffffff);
        bgColorH = new Color(255);
        textColor = new Color(0);
        textColorH = new Color(65535);
        position = 0;
        extraspace = new Dimension(0, 0);
        bAllowFontChange = true;
        bRepaint = false;
        bHighlighted = false;
        b3DFrame = true;
        bUseGradient = false;
        rounded = 0;
        bEnabled = true;
        bTypeButton = false;
        bIsArmed = false;
        img = null;
        imageposition = 1;
        bFocused = false;
        mouseEventSimulated = 0;
        text = strlabel;
        init();
    }

    public ListLabel()
    {
        maxRad = 20;
        bgColor = new Color(0xffffff);
        bgColorH = new Color(255);
        textColor = new Color(0);
        textColorH = new Color(65535);
        position = 0;
        extraspace = new Dimension(0, 0);
        bAllowFontChange = true;
        bRepaint = false;
        bHighlighted = false;
        b3DFrame = true;
        bUseGradient = false;
        rounded = 0;
        bEnabled = true;
        bTypeButton = false;
        bIsArmed = false;
        img = null;
        imageposition = 1;
        bFocused = false;
        mouseEventSimulated = 0;
        text = "";
        init();
    }

    private void init()
    {
        setFont(new Font("Helvetica", 0, 12));
    }

    public void setPosition(int pos)
    {
        position = pos;
        if(bRepaint)
            repaint();
    }

    public void setTextColor(Color nc)
    {
        textColor = nc;
        setForeground(nc);
        if(bRepaint)
            repaint();
    }

    public void setTextColorHighlight(Color nc)
    {
        textColorH = nc;
        if(bRepaint)
            repaint();
    }

    public void setbgColor(Color nc)
    {
        bgColor = nc;
        setBackground(nc);
        if(bRepaint)
            repaint();
    }

    public void setbgColorHighlight(Color nc)
    {
        bgColorH = nc;
        if(bRepaint)
            repaint();
    }

    public void setFont(Font newFont)
    {
        if(bAllowFontChange)
            myFont = newFont;
        FontMetrics f = getFontMetrics(newFont);
        fontHeight = f.getHeight();
        fontAscent = f.getAscent();
        fontDescent = f.getDescent();
        fontSpaceWidth = f.charWidth(' ');
        if(bRepaint)
            repaint();
    }

    public Dimension getMaximumSize()
    {
        return super.getMaximumSize();
    }

    public Dimension getMinimumSize()
    {
        return super.getMinimumSize();
    }

    public Dimension getPreferredSize()
    {
        if(getComponentCount() > 0)
            return super.getPreferredSize();
        if(myFont == null)
            System.out.println("ERROR:ListLabel: myFont==null");
        Dimension d = new Dimension(0, 0);
        FontMetrics f = getFontMetrics(myFont);
        d.width = f.stringWidth(text) + 2 * fontSpaceWidth;
        d.height = f.getHeight();
        if(extraspace.width > 0)
            d.width += extraspace.width;
        if(extraspace.height > 0)
            d.height += extraspace.height;
        return d;
    }

    public void setBounds(int x, int y, int w, int h)
    {
        super.setBounds(x, y, w, h);
    }

    public synchronized void setImage(Image nImg)
    {
        img = nImg;
        repaint();
    }

    public synchronized Image getImage()
    {
        return img;
    }

    public int getImageOrientation()
    {
        return imageposition;
    }

    public void setImageOrientation(int e)
    {
        imageposition = e;
    }

    public void setOpaque(boolean flag)
    {
        super.setOpaque(flag);
    }

    public void setText(String ntext)
    {
        text = ntext;
        if(bRepaint)
            repaint();
    }

    public String getText()
    {
        return text;
    }

    public boolean setSelectItem(boolean state)
    {
        if(!state)
            return deselectItem();
        else
            return selectItem();
    }

    public boolean deselectItem()
    {
        if(bHighlighted)
        {
            bHighlighted = false;
            if(bRepaint)
                repaint();
            return true;
        } else
        {
            return false;
        }
    }

    public boolean selectItem()
    {
        if(!bHighlighted)
        {
            bHighlighted = true;
            if(bRepaint)
                repaint();
            return true;
        } else
        {
            return false;
        }
    }

    public boolean isHighlighted()
    {
        return bHighlighted;
    }

    public boolean isEnabled()
    {
        return bEnabled;
    }

    public void setEnabled(boolean b)
    {
        if(b)
            enable();
        else
            disable();
    }

    public void disable()
    {
        if(bEnabled)
        {
            bEnabled = false;
            super.setEnabled(bEnabled);
            if(bRepaint)
                repaint();
        }
    }

    public void enable()
    {
        if(!bEnabled)
        {
            bEnabled = true;
            super.setEnabled(bEnabled);
            if(bRepaint)
                repaint();
        }
    }

    public boolean setAutoRepaint(boolean state)
    {
        boolean b = bRepaint;
        bRepaint = state;
        return b;
    }

    public void paintComponent(Graphics g)
    {
        Dimension d = getSize();
        Insets i = getInsets();
        java.awt.Rectangle rc = g.getClipBounds();
        if(isOpaque())
        {
            g.setColor(bgColor);
            g.fillRect(i.left, i.top, d.width, d.height);
        }
        if(bUseGradient && d.height - 8 > 0)
        {
            if(rounded != 0)
            {
                Dimension d2 = new Dimension(0, 0);
                d2.width = d.width - i.left - i.right;
                d2.height = d.height - i.top - i.bottom;
                Graphics2D g2d = (Graphics2D)g;
                GradientPaint gradient = null;
                if(rounded < 0)
                {
                    if(!bIsArmed)
                        gradient = new GradientPaint(i.left, i.top, Color.white, 0.0F, d2.height, BCDefaults.bgPanelColor);
                    else
                        gradient = new GradientPaint(i.left, i.top, BCDefaults.bgPanelColor, 0.0F, d2.height, Color.white);
                    g2d.setPaint(gradient);
                    g2d.fillRect(i.left, i.top, d2.width, d2.height);
                } else
                {
                    gradient = new GradientPaint(i.left, i.top, BCDefaults.bgPanelColor, 0.0F, d2.height / 2, BCDefaults.bgColor, true);
                    g2d.setPaint(gradient);
                    int rr = Math.min(Math.min(d2.height, d2.width), maxRad);
                    g2d.fillRoundRect(i.left, i.top, d2.width - 1, d2.height - 1, rr - 1, rr - 1);
                    if(!b3DFrame)
                    {
                        g.setColor(new Color(0x80000000, true));
                        g.drawRoundRect(i.left, i.top, d2.width - 1, d2.height - 1, rr - 1, rr - 1);
                    }
                }
            } else
            {
                boolean useVersion2 = false;
                int y = i.top;
                float hsb_value[] = Color.RGBtoHSB(bgColorH.getRed(), bgColorH.getGreen(), bgColorH.getBlue(), null);
                float height2 = (float)d.height / 2.0F;
                float b_add = hsb_value[2] / height2;
                float s_add = hsb_value[1] / height2;
                float sat = 0.0F;
                float b2_add;
                float br;
                if(useVersion2)
                {
                    b2_add = (1.0F - hsb_value[2]) / height2;
                    br = 1.0F;
                } else
                {
                    b2_add = 0.0F;
                    br = hsb_value[2];
                }
                Color nc[] = new Color[d.height];
                int cnt;
                for(cnt = 0; cnt < d.height / 2; cnt++)
                {
                    nc[cnt] = new Color(Color.HSBtoRGB(hsb_value[0], sat, br));
                    if(useVersion2)
                        br -= b2_add;
                    sat += s_add;
                }

                for(; cnt < d.height; cnt++)
                {
                    nc[cnt] = new Color(Color.HSBtoRGB(hsb_value[0], sat, br));
                    br -= b_add;
                }

                if(!bIsArmed)
                    for(cnt = 0; cnt < d.height; cnt++)
                    {
                        g.setColor(nc[cnt]);
                        g.drawLine(i.left, y, d.width, y);
                        y++;
                    }

                else
                    for(cnt = 0; cnt < d.height; cnt++)
                    {
                        g.setColor(nc[d.height - 1 - cnt]);
                        g.drawLine(i.left, y, d.width, y);
                        y++;
                    }

            }
        } else
        {
            g.setColor(bHighlighted ? bgColorH : bgColor);
            if(rounded != 0)
            {
                int rr = Math.min(Math.min(d.height - 1, d.width - 1), maxRad);
                g.fillRoundRect(i.left, i.top, d.width - 1, d.height - 1, rr, rr);
            } else
            {
                g.fillRect(i.left, i.top, d.width, d.height);
            }
        }
        if(b3DFrame)
            if(rounded != 0)
            {
                Color cn = bHighlighted ? bgColorH : bgColor;
                Color cH = cn.brighter();
                Color cH2 = new Color(0x50000000 + cH.getRGB(), true);
                Color cD = cn.darker();
                Color cD2 = new Color(0x50000000 + cD.getRGB(), true);
                int rr = Math.min(Math.min(d.height - 1, d.width - 1), maxRad);
                g.setClip(i.left, i.top, d.width - 1 - rr / 2, d.height - 1);
                g.setColor(bHighlighted ? cD : cH);
                g.drawRoundRect(i.left, i.top, d.width - 1, d.height - 1, rr, rr);
                g.setColor(bHighlighted ? cD2 : cH2);
                g.drawRoundRect(i.left + 1, i.top + 1, d.width - 2, d.height - 2, rr - 1, rr - 1);
                g.setClip(i.left + rr / 2, i.top + 1, d.width - 1 - rr / 2, d.height - 1);
                g.setColor(bHighlighted ? cH : cD);
                g.drawRoundRect(i.left, i.top, d.width - 2, d.height - 1, rr, rr);
                g.setColor(bHighlighted ? cH2 : cD2);
                g.drawRoundRect(i.left + 1, i.top + 1, d.width - 3, d.height - 2, rr - 1, rr - 1);
                g.setColor(cn);
                g.setClip(null);
            } else
            {
                g.draw3DRect(i.left, i.top, d.width - 1, d.height - 1, !bHighlighted);
            }
        if(img != null)
        {
            int ix = imageposition != 0 ? imageposition != 1 ? (i.left + d.width) - img.getWidth(this) : i.left + (d.width - img.getWidth(this)) / 2 : i.left;
            int iy = i.top + (d.height - img.getHeight(this)) / 2;
            if(bTypeButton && isHighlighted())
            {
                ix++;
                iy++;
            }
            g.drawImage(img, ix, iy, img.getWidth(this), img.getHeight(this), this);
        }
        g.setFont(myFont);
        if(!bEnabled)
        {
            int rgba;
            if(bHighlighted)
                rgba = textColorH.getRGB() & 0x50000000;
            else
                rgba = textColor.getRGB() & 0x50000000;
            g.setColor(new Color(rgba, true));
        } else
        if(bHighlighted)
            g.setColor(textColorH);
        else
            g.setColor(textColor);
        Dimension dText = getPreferredSize();
        int xpos = i.left + fontSpaceWidth;
        int ypos = (dText.height + (d.height - dText.height - extraspace.height) / 2) - fontDescent;
        ypos--;
        if(position == 1)
        {
            FontMetrics f = getFontMetrics(myFont);
            xpos = (d.width - f.stringWidth(text)) / 2;
        } else
        if(position == 2)
        {
            FontMetrics f = getFontMetrics(myFont);
            xpos = d.width - f.stringWidth(text) - fontSpaceWidth;
        }
        if(bTypeButton && isHighlighted())
            g.drawString(text, xpos + 1, ypos + 1);
        else
            g.drawString(text, xpos, ypos);
        if(bFocused)
        {
            g.setColor(Color.black);
            if(rounded != 0)
            {
                int rr = Math.min(Math.min(d.height - 3, d.width - 3), maxRad);
                g.drawRoundRect(i.left + 1, i.top + 1, d.width - 3, d.height - 3, rr, rr);
            } else
            {
                g.drawRect(i.left + 2, i.top + 2, d.width - 5, d.height - 5);
            }
        }
    }

    public void useGradient(boolean useGradient, boolean reverse)
    {
        bUseGradient = useGradient;
        if(bUseGradient)
            bIsArmed = reverse;
    }

    public void setBControlDefault()
    {
        bUseGradient = true;
        setTextColor(new Color(0x5a7eb8));
        setbgColor(new Color(0x4a7597));
        setTextColorHighlight(new Color(0xffd800));
        setbgColorHighlight(new Color(0x4a7597));
        selectItem();
        setPosition(1);
        extraspace.height = 4;
        b3DFrame = false;
        setOpaque(false);
    }

    public void setBControlButtonDefault2()
    {
        setBControlButtonDefault2(true);
    }

    public void setBControlButtonDefault2(boolean newLook)
    {
        bUseGradient = false;
        setOpaque(false);
        setTextColor(new Color(0xffd800));
        setbgColor(new Color(0x4a7597));
        setTextColorHighlight(new Color(0xffd800));
        setbgColorHighlight(new Color(0x4a7597));
        setPosition(1);
        extraspace.height = 4;
        extraspace.width = 8;
        b3DFrame = true;
        bTypeButton = true;
        bRepaint = true;
        addMouseListener(this);
        setFocusable(true);
        addFocusListener(this);
        addKeyListener(this);
    }

    public void setBControlButtonDefault()
    {
        bUseGradient = false;
        setTextColor(new Color(0xbfa200));
        setbgColor(new Color(0x4a7597));
        setTextColorHighlight(new Color(0xffd800));
        setbgColorHighlight(new Color(0x4a7597));
        setPosition(1);
        extraspace.height = 4;
        b3DFrame = true;
        if(rounded != 0)
            if(fontSpaceWidth > 0)
                extraspace.width = 2 * fontSpaceWidth;
            else
                extraspace.width = 4;
    }

    public void mouseClicked(MouseEvent e)
    {
        if(!bEnabled)
            return;
        else
            return;
    }

    public void mouseEntered(MouseEvent e)
    {
        if(!bEnabled)
            return;
        if(bIsArmed)
            selectItem();
    }

    public void mouseExited(MouseEvent e)
    {
        if(!bEnabled)
            return;
        if(bTypeButton && isHighlighted())
            deselectItem();
    }

    public void mousePressed(MouseEvent e)
    {
        if(!bEnabled)
            return;
        bIsArmed = true;
        if(bTypeButton && !isHighlighted())
            selectItem();
    }

    public void mouseReleased(MouseEvent e)
    {
        if(!bEnabled)
            return;
        bIsArmed = false;
        if(bTypeButton && isHighlighted())
            deselectItem();
    }

    public void focusGained(FocusEvent e)
    {
        bFocused = true;
        repaint();
    }

    public void focusLost(FocusEvent e)
    {
        bFocused = false;
        if(mouseEventSimulated > 0)
            simulateMouseReleased();
        repaint();
    }

    public void keyPressed(KeyEvent e)
    {
        int cntRepeat = 0;
        if(e.getKeyCode() == 32)
        {
            if(cntRepeat++ == 0)
                simulateMousePressed();
        } else
        {
            cntRepeat = 0;
        }
    }

    private void simulateMousePressed()
    {
        MouseListener l[] = getMouseListeners();
        if(l.length == 0)
            return;
        mouseEventSimulated++;
        MouseEvent me = new MouseEvent(this, 501, 0L, 0, 0, 0, 1, false, 1);
        for(int i = 0; i < l.length; i++)
            l[i].mousePressed(me);

    }

    private void simulateMouseReleased()
    {
        MouseListener l[] = getMouseListeners();
        if(l.length == 0)
            return;
        mouseEventSimulated--;
        MouseEvent me = new MouseEvent(this, 502, 0L, 0, 0, 0, 1, false, 1);
        for(int i = 0; i < l.length; i++)
            l[i].mouseReleased(me);

    }

    private void simulateMouseClicked()
    {
        MouseListener l[] = getMouseListeners();
        if(l.length == 0)
            return;
        MouseEvent me = new MouseEvent(this, 500, 0L, 0, 0, 0, 1, false, 1);
        for(int i = 0; i < l.length; i++)
            l[i].mouseClicked(me);

    }

    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == 32 && mouseEventSimulated > 0)
        {
            simulateMouseReleased();
            simulateMouseClicked();
        }
    }

    public void keyTyped(KeyEvent keyevent)
    {
    }

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;
    public int maxRad;
    private String text;
    private Font myFont;
    private int fontHeight;
    private int fontAscent;
    private int fontDescent;
    private int fontSpaceWidth;
    private Color bgColor;
    private Color bgColorH;
    private Color textColor;
    private Color textColorH;
    private int position;
    public Dimension extraspace;
    public boolean bAllowFontChange;
    public boolean bRepaint;
    private boolean bHighlighted;
    boolean b3DFrame;
    boolean bUseGradient;
    int rounded;
    boolean bEnabled;
    boolean bTypeButton;
    boolean bIsArmed;
    private Image img;
    private int imageposition;
    private boolean bFocused;
    private int mouseEventSimulated;
}
