package de.dfki.sonogram;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.net.URL;
import java.io.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This is the help dialog for Sonogram. It shows the index.html file in the sonogram
 * package folder. The HelpDialog Class has the abillity to link into the internet via
 * the http protokoll. It is a modal dialog.
 * @author Christoph Lauer
 * @version 1.0,  Current 26/09/2002
 */
public class LicenseDialog extends JDialog {
    JEditorPane browser;
    public LicenseDialog (Frame owner) {
        super(owner,"License",true);
        Sonogram reftomain = (Sonogram) owner;
        setSize(440,500);
	setResizable(false);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(5,5));
        browser = new JEditorPane();
        browser.setEditable(false);
        browser.setContentType("text/html");
        final java.net.URL helpurl = Sonogram.class.getResource("license.html");
        try {goToUrl(helpurl);
        } catch (Exception e) {
            reftomain.messageBox("Help Browser Error","Help file license.html is not found !",2);
            System.out.println("--> Can't find Helpfile index.html");
        }
        JScrollPane scroll = new JScrollPane(browser);
        cp.add(scroll,"Center");
        JButton okay;
        okay = new JButton("Close");
        okay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e1) {
		    hide();
		}
	    });
	cp.add(okay,"South");
        // place it in the middle of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getSize().width;
        int h = getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        setBounds(x, y, w, h);
    }
    private void goToUrl(URL url) {
        try {
            browser.setPage(url);
        } catch (Exception e) {
            System.out.println("--> Can't open URL");
        }
    }
}