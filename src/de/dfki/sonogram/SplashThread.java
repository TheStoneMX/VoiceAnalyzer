package de.dfki.sonogram;

import javax.swing.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This is the splash thread for the splash screen.
 * @author Christoph Lauer
 * @version 1.0,  Current 26/09/2002
 */

public class SplashThread extends Thread {

    private JWindow splash;
    private int seconds;
    //------------------------------------------------------------------------------------------
    SplashThread(JWindow win, int duration) {
        splash = win;
        seconds = duration;
    }
    //------------------------------------------------------------------------------------------
    public void run() {
        try {
            // wait time before splash win is removed
            sleep(seconds * 1000);
        } catch (InterruptedException e) {}
        splash.dispose();
    }
    //------------------------------------------------------------------------------------------
}
