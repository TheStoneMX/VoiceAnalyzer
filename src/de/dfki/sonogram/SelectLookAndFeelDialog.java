package de.dfki.sonogram;

import javax.swing.*;
import java.util.Hashtable;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * Simple dialog whith slider for L&F selection.
 * @author Christoph Lauer
 * @version 1.0,  Current 26/09/2002
 */
class SelectLookAndFeelDialog extends JDialog {
    JSlider slider;
    Sonogram reftomain;

    public SelectLookAndFeelDialog (Sonogram ref) {
        super(ref,"Look and Feel for Sonogram",false);
        reftomain = ref;
        setSize(117,193);

        JPanel p = new JPanel();
        p.setBorder(new TitledBorder(new EtchedBorder(),"LookAndFeel"));
        p.setLayout(null);

        slider = new JSlider(JSlider.VERTICAL,0,5,3);
        slider.setToolTipText("Change Look and Feel for Sonogram");
        Hashtable h = new Hashtable();
        h.put (new Integer (0),  new JLabel("Metal"));
        h.put (new Integer (1),  new JLabel("Motiv"));
        h.put (new Integer (2),  new JLabel("Windows"));
        h.put (new Integer (3),  new JLabel("Plastic"));
        h.put (new Integer (4),  new JLabel("Nativ OS"));
         h.put (new Integer (5),  new JLabel("Ocean"));
        slider.setLabelTable (h);
        slider.setPaintLabels (true);
        slider.setSnapToTicks(true);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setBounds(5,15,100,85);
        p.add(slider);
        JButton btok= new JButton ("Okay");
        ActionListener olst = new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                      reftomain.setLookAndFeel(slider.getValue());
                                      hide();
                                  }
                              };
        btok.addActionListener(olst);
        btok.setBounds(6,100,95,25);
        p.add(btok);
        JButton btap= new JButton ("Apply");
        ActionListener alst = new ActionListener() {
                                  public void actionPerformed(ActionEvent e) {
                                      reftomain.setLookAndFeel(slider.getValue());
                                  }
                              };
        btap.addActionListener(alst);
        btap.setBounds(6,130,95,25);
        p.add(btap);
        getContentPane().add(p);
    }
}

