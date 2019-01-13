package de.dfki.sonogram;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import javax.swing.border.MatteBorder;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * Displays splash screen in the center of the screen. Just create a
 * SplashScreen object with "new SplashScreen(..)" and it'll do what
 * you want.
 * @author Christoph Lauer
 * @version 1.0,  Current 26/09/2002
 */
public class SplashScreen extends JPanel {
    ImageIcon    pic;
    JWindow      splash = new JWindow();
    JProgressBar progress;
    int          h,w;
    //------------------------------------------------------------------------------------------
    /**
     * @param duration time the screen is shown in seconds.
     * @param filemame relative imagefilename
     * @param width 
     * @param height
     */
    public SplashScreen(String filename, int width, int height,
                        int duration,Color  start,Color end) {
        h = height;
        w = width;

        try {
            com.incors.plaf.kunststoff.KunststoffLookAndFeel kunststoffLF = new com.incors.plaf.kunststoff.KunststoffLookAndFeel();
            kunststoffLF.setCurrentTheme(new com.incors.plaf.kunststoff.KunststoffTheme());
            UIManager.setLookAndFeel(kunststoffLF);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Throwable t) {}

        pic = new ImageIcon(Sonogram.class.getResource(filename));
        setLocation(0,0);
        setSize(width,height);
        splash.getContentPane().setLayout(null);
        splash.getContentPane().add(this);
        splash.setSize(width,height+11);

        progress = new JProgressBar(0,100);
        ProgressBarUI spui = new ProgressBarUI(start,end,false);
        progress.setUI(spui);
        progress.setSize(width,11);
        progress.setStringPainted(true);
        progress.setLocation(0,height);
        progress.setFont(new Font("SansSerif",0,9));
        progress.setBorder(new MatteBorder(0,1,1,1,new Color(255,0,0)));
        progress.setBackground(new Color(150,150,200));
        splash.getContentPane().add(progress);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = splash.getSize().width;
        int h = splash.getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        splash.setBounds(x, y, w, h);
        splash.setVisible(true);
        repaint();
    }
    //------------------------------------------------------------------------------------------
    public void setProgress(int percent, String progressmessage) {
        progress.setValue(percent);
        progress.setString(progressmessage);
        try {
            Thread.sleep(100);
        } catch (Throwable t) {}
        if (percent == 100) {
            try {
                Thread.sleep(1500);
            } catch (Throwable t) {}
            splash.dispose();
        }
    }
    //------------------------------------------------------------------------------------------
    public void paintComponent(Graphics g) {
	pic.paintIcon(splash, g, 0, 0);
    }
}
//------------------------------------------------------------------------------------------

