package de.dfki.sonogram;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.MatteBorder;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This is my own ProgressBar implementation for the Soogram main frame
 * @author Christoph Lauer
 * @version 1.0,  Begin 15/09/2002, Current 26/09/2002
 */
class SonoProgressMonitor extends JWindow implements Runnable {
    private JProgressBar progressbar;
    private boolean      valuehaschanged  = false;
    private boolean      stringhaschanged = false;
    private boolean      cancelispressed  = false;
    private boolean      endthread        = false;
    private int          progress;
    private String       note             = "Transformating";
    private Thread       timeThread;
    int    min;
    int    max;
    Sonogram  owner;
    int locsx,locsy;
    int locpx,locpy;
    int dimsx,dimsy;
    int dimpx,dimpy;
    int dimx,dimy;
    int locx,locy;
    int progressheigth      = 11;
    String str;

    //----------------------------------------------------------------------------
    public SonoProgressMonitor(Sonogram rowner, String title
                               ,String message,int rmin, int rmax) {
        owner = rowner;
        max = rmax;
        min = rmin;
        locsx  = (int)((Sonogram)owner).getLocation().getX();
        locsy  = (int)((Sonogram)owner).getLocation().getY();
        locpx  = (int)((Sonogram)owner).pp.getLocation().getX();
        locpy  = (int)((Sonogram)owner).pp.getLocation().getY();
        dimsx  = (int)((Sonogram)owner).getSize().getWidth();
        dimsy  = (int)((Sonogram)owner).getSize().getHeight();
        dimpx  = (int)((Sonogram)owner).pp.getSize().getWidth();
        dimpy  = (int)((Sonogram)owner).pp.getSize().getHeight();
        locx = locsx+(dimsx-dimpx)/2;
        locy = locsy+dimsy-progressheigth-4;
        setLocation(locx,locy-4);
        dimx = dimpx;
        dimy = progressheigth;
        setSize(dimx,dimy);
        progressbar = new JProgressBar(0,100);
        progressbar.setToolTipText("Press with mouse on this progressbar to interrupt curent task.");
        ProgressBarUI spui;
        if (owner.spektrumExist == false)
            spui = new ProgressBarUI(new Color(0,0,0),new Color(255,0,0),true);
        else
            spui = new ProgressBarUI(new Color(0,0,150),new Color(130,0,0),true);
        progressbar.setUI(spui);
        progressbar.setStringPainted(true);
        progressbar.setFont(new Font("SansSerif",0,10));
        progressbar.setBorder(new MatteBorder(1,1,1,1,new Color(102,102,153)));
        progressbar.setBackground(new Color(180,180,230));
        progressbar.setDoubleBuffered(true);
        MouseListener mlst = new MouseAdapter () {
                                 public void mousePressed(MouseEvent e) {
                                     cancelispressed = true;
                                     ((Sonogram)owner).messageBox("Interrupted !","Mouse pressed in progressbar.\n" + note + " Interrupted.",2);
                                 };
                             };
        progressbar.addMouseListener(mlst);
        getContentPane().add(progressbar);
        timeThread = new Thread(this);
        timeThread.setDaemon(true);
        timeThread.setPriority(Thread.MAX_PRIORITY);
        timeThread.start();

        setVisible(true);
    }
    //----------------------------------------------------------------------------
    public void setProgress(int progresss) {
        valuehaschanged = true;
        progress = progresss;
    }
    //----------------------------------------------------------------------------
    boolean isCanceled() {
        return(cancelispressed);
    }
    //----------------------------------------------------------------------------
    void endRun() {
        endthread = true;
    }
    //----------------------------------------------------------------------------
    void setNote(String notes) {
        valuehaschanged = true;
        note = notes;
    }
    //----------------------------------------------------------------------------
    void close() {
        endRun();
        timeThread.stop();
        timeThread = null;
        dispose();
    }
    //----------------------------------------------------------------------------
    public void run () {
        while (endthread == false) {

            if (((Sonogram)owner).getState()==Frame.ICONIFIED) {
                setVisible(false);
            } else
                toFront();
            locsx  = (int)((Sonogram)owner).getLocation().getX();
            locsy  = (int)((Sonogram)owner).getLocation().getY();
            locpx  = (int)((Sonogram)owner).pp.getLocation().getX();
            locpy  = (int)((Sonogram)owner).pp.getLocation().getY();
            dimsx  = (int)((Sonogram)owner).getSize().getWidth();
            dimsy  = (int)((Sonogram)owner).getSize().getHeight();
            dimpx  = (int)((Sonogram)owner).pp.getSize().getWidth();
            dimpy  = (int)((Sonogram)owner).pp.getSize().getHeight();
            locx = locsx+(dimsx-dimpx)/2+2;
            locy = locsy+dimsy-progressheigth-7;
            dimx = dimpx -4;
            dimy = progressheigth;
            setSize(dimx,dimy);
            setLocation(locx,locy);
            // For Mac dim-3 -- Size
            // For mac Locx+2, locy+3

            if (valuehaschanged == true) {
                valuehaschanged = false;
                progressbar.setValue(progress);
                str = progress + "% :" + note ;
                progressbar.setString(str);
                Graphics g = getGraphics();
                progressbar.update(g);
            }
            try {
                Thread.currentThread().sleep(41); // Sind genau 24 pro sekunde ;-)
            } catch (InterruptedException ie) {
                System.out.println("--> Interupt Eception in SonoProgressMonitor");
            }
        }
    }
    //----------------------------------------------------------------------------
}
