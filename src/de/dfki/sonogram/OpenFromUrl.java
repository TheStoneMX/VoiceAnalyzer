package de.dfki.sonogram;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * Simple swing edit dialog to grab a URL from KeyBoard.
 * @author Christoph Lauer
 * @version 1.0,  Begin 31/06/2001, Current 26/09/2002
 */
public class OpenFromUrl extends JFrame implements ActionListener {
    JTextField tf;
    Sonogram reftomain;
    public OpenFromUrl(Sonogram owner) {
        reftomain = owner;
        // Change Icon
        Toolkit tk = Toolkit.getDefaultToolkit();
        setIconImage(tk.getImage(Sonogram.class.getResource("Sonogram.gif")));
        // Set Title Text
        setTitle("URL of the remote Media File");
        Container cp = getContentPane();
        cp.setLayout(new FlowLayout());
        setBackground(Color.lightGray);
        tf = new JTextField(35);
        tf.setText(reftomain.storedurl);
        cp.add(tf);
        JButton button = new JButton("Open");
        button.addActionListener(this);
        cp.add(button);
        setSize(515,64);
        pack();
        // place it in the middle of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        int w = getSize().width;
        int h = getSize().height;
        int x = (dim.width-w)/2;
        int y = (dim.height-h)/2;
        setBounds(x, y, w, h);
        // and finaly make it visible
        setVisible(true);
    }
    public void actionPerformed(ActionEvent event) {
        String cmd = event.getActionCommand();
        if (cmd.equals("Open")) {
            System.out.println("--> URI: "+tf.getText());
            reftomain.reader.generateSamplesFromURL(tf.getText());
            reftomain.storedurl = tf.getText();
            hide();
            reftomain.fileisfromurl = true;
        }
    }
}
