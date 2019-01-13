package de.dfki.sonogram;

import javax.swing.table.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import java.net.URL;
import java.io.File;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This class shows in tabular for the propperties of the generated Sonogram and
 * the option for the spectogram generation for all sorts of spectrums.
 * @author Christoph Lauer
 * @version 1.0,  Current 26/09/2002
 */
public class InfoDialog extends JFrame {
    private Sonogram   reftomain;
    private Object[][] data = new Object[36][2];
    Dimension scd;
    int       scr;

    public InfoDialog(Sonogram sono) {
        super("Detailed Information - Sonogram");

        setLocation(40,40);
        setSize(300,300);
        Toolkit tk = Toolkit.getDefaultToolkit();
        setIconImage(tk.getImage(Sonogram.class.getResource("Sonogram.gif")));

        scd = tk.getScreenSize();
        scr = tk.getScreenResolution();

        reftomain = sono;
        data[0][0] = "Sonogram Frequency Resolution:";
        data[1][0] = "Sonogram Time Resolution:";
        data[2][0] = "Sonogram Windowlength in Units:";
        data[3][0] = "Sonogram Windownumbers:";
        data[4][0] = "Sonogram Windowlength in Time:";
        data[5][0] = "Signal Duration:";
        data[6][0] = "Selected Visible Duration:";
        data[7][0] = "Filename:";
        data[8][0] = "File ULR:";
        data[9][0] = "Streamtype:";
        data[10][0] = "Samplerate:";
        data[11][0] = "Selected Overlapping:";
        data[12][0] = "Selected Windowfunktion";
        data[13][0] = "Peakfrequency:";
        data[14][0] = "Peaktime:";
        data[15][0] = "Formantfinder Time Span:";
        data[16][0] = "Formantfinder Frequency Resolution:";
        data[17][0] = "Cepstrum Time Span:";
        data[18][0] = "LPC Previous Time:";
        data[19][0] = "LPC Number of Ceofficients:";
        data[20][0] = "Surfaceplot Surface Frequency:";
        data[21][0] = "Surfaceplot Surface Time:";
        data[22][0] = "Surfaceplot Background Color:";
        data[23][0] = "Surfaceplot Rendering:";
        data[24][0] = "Frequency limit for Pitch detection:";
        data[25][0] = "Estimated file size for SVG export:";
        data[26][0] = "Number of Spectrum Elements:";
        data[27][0] = "Screen Size:";
        data[28][0] = "Screen Resolution:";
        data[29][0] = "Wavelet Selected:";
        data[30][0] = "Wavelet Octaves:";
        data[31][0] = "Wavelet Windowlength:";
        data[32][0] = "Autocorrelation Windowlength:";
        data[33][0] = "Autocorrelation Summarize Loop:";
        data[34][0] = "Autocorrelation Pitch Duration:";
        data[35][0] = "Autocorrelation Pitch Frequency:";

        String[] columnNames = {"Name",
                                "Value"};
        JTable table = new JTable(data, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(400,220));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setDefaultRenderer(Object.class,new ColoredTableCellRenderer());
        TableColumn column = null;
        for (int i = 0; i < 2; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0)
                column.setPreferredWidth(100); //sport column is bigger
            if (i == 1)
                column.setPreferredWidth(50);
        }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this window.
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
                              public void windowClosing(WindowEvent e) {
                                  reftomain.infovisible = false;
                              }
                          }
                         );

        pack();
        hide();
    }
    //-------------------------------------------------------------------------------------------------------------------------
    void update() {

        if (reftomain.spektrumExist==true && (this.isVisible()==true)) {
            double sfr =   ((double) (reftomain.samplerate) / (double)reftomain.timewindowlength);
            sfr        =   Math.round(sfr*100.0)/100.0;
            double str =   ((double)reftomain.samplesall/(double)(reftomain.samplerate) / (double)reftomain.timewindowlength * 2.0);
            str        =   Math.round(str*100000.0)/100.0;
            int    swu =   (reftomain.timewindowlength);
            int    swn =   (reftomain.spektrum.size());
            double swt =   ((double)swu/(double)(reftomain.samplerate));
            swt        =   Math.round(swt*100000.0)/100.0;
            double sid =   ((double)reftomain.samplesall/(double)(reftomain.samplerate));
            sid        =   Math.round(sid*1000.0)/1000.0;
            double sed =   ((double)reftomain.selecedwidth*(double)reftomain.samplesall/(double)(reftomain.samplerate));
            sed        =   Math.round(sed*1000.0)/1000.0;
            String fin =   "File load from URL";
            String fip =   "File load from URL";
            if (reftomain.fileisfromurl == false) {
                java.io.File file = reftomain.chooser.getSelectedFile();
                fin =   file.getName();
                fip =   file.getPath();
            }
            int    sar =   reftomain.samplerate;
            int    ovl =   reftomain.gad.sliderwinspeed.getValue();
            if (reftomain.gad.coverlapping.isSelected()==false)
                ovl = 1;
            String dst =   reftomain.reader.dstype;
            String sef =   reftomain.selectedFilter;
            double pef =   (double)reftomain.peaky / (double)reftomain.timewindowlength * (double)(reftomain.samplerate);
            pef        =   Math.round(pef*100.0)/100.0;
            double pet =   (double)reftomain.peakx / (double)reftomain.spektrum.size() * (double)sid;
            pet        =   Math.round(pet*1000.0)/1000.0;
            double ffw =   ((double)reftomain.fv.len /(double)(reftomain.samplerate));
            ffw        =   Math.round(ffw*100000.0)/100.0;
            double ffr =   ((double)(reftomain.samplerate) / (double)reftomain.fv.len);
            double cwl =   ((double)reftomain.cv.len /(double)(reftomain.samplerate) / 2.0);
            cwl        =   Math.round(cwl*100000.0)/100.0;
            double lpt =   ((double)reftomain.gad.sliderlpcsamfutur.getValue() / (double)(reftomain.samplerate));
            lpt        =   Math.round(lpt*100000.0)/100.0;
            int    lco =   (reftomain.gad.sliderlpccoef.getValue());
            int    sel =   swu/2*swn;
            double sut =   reftomain.gad.slidersurfacex.getValue()/10.0;
            double suf =   reftomain.gad.slidersurfacey.getValue()/10.0;
            int    red =   reftomain.gad.bgcol.getRed();
            int    gre =   reftomain.gad.bgcol.getGreen();
            int    blu =   reftomain.gad.bgcol.getBlue();
            int    flp =   reftomain.gad.sliderpitch.getValue();
            String ren = "";
            if (reftomain.gad.s1.isSelected()==true)
                ren = "Point Cloud Plot";
            if (reftomain.gad.s2.isSelected()==true)
                ren = "Line Grid Plot";
            if (reftomain.gad.s3.isSelected()==true)
                ren = "Area Surface Plot";
            double mes =   Math.round(Math.round((double)sel*83.175/1024.0)/1024.0*100.0)/100.0;
            String was =   reftomain.gad.wavelets.getSet(reftomain.gad.wcb.getSelectedIndex()).getName();
            int    oct =   reftomain.gad.sliderwaltl.getValue();
            int    wat =   reftomain.gad.walwindowlength;
            double awl =   reftomain.kv.windowlength;
            double aws =   reftomain.kv.windowshift;
            double apt =   reftomain.kv.ptime;
            double apf =   reftomain.kv.pfrequency;


            data [0][1] = new String (sfr + " Hz");
            data [1][1] = new String (str + " millisec.");
            data [2][1] = new String (swu + " Samples");
            data [3][1] = new String (swn + " Windows");
            ;
            data [4][1] = new String(swt + " millisec.");
            data [5][1] = new String (sid + " sec.");
            data [6][1] = new String (sed + " sec.");
            data [7][1] = fin;
            data [8][1] = fip;
            data [9][1] = dst;
            data [10][1] = new String(sar + " s/sec.");
            data [11][1] = new String(ovl + " Trans./Win.");
            data [12][1] = sef;
            data [13][1] = new String(pef + " Hz.");
            data [14][1] = new String(pet + " sec.");
            data [15][1] = new String(ffw + " millisec.");
            data [16][1] = new String(ffr + " Hz");
            data [17][1] = new String(cwl + " millisec.");
            data [18][1] = new String(lpt + " millisec.");
            data [19][1] = new String(lco + " Coefficents");
            data [20][1] = new String("Factor " + sut);
            data [21][1] = new String("Factor " + suf);
            data [22][1] = new String("Red:"+red+" Green:"+gre+" Blue:"+blu);
            data [23][1] = ren;
            data [24][1] = new String(flp + "Hz");
            data [25][1] = new String(mes + " MB");
            data [26][1] = new String(sel + " Spectral points");
            data [27][1] = new String(scd.width + " x " + scd.height);
            data [28][1] = new String(scr + " dots-per-inch");
            data [29][1] = was;
            data [30][1] = new String(oct + " Octaves");
            data [31][1] = new String(wat + " Samples");
            data [32][1] = new String(awl + " millisec");
            data [33][1] = new String(aws + " millisec");
            data [34][1] = new String(apt + " millisec");
            data [35][1] = new String(apf + " Hz");
            if (awl == 0.0)
                data [32][1] = new String("Not Enabled");
            if (aws == 0.0)
                data [33][1] = new String("Not Enabled");
            if (apt == 0.0)
                data [34][1] = new String("Not Enabled");
            if (apf == 0.0)
                data [35][1] = new String("Not Enabled");
            repaint();
        }
    }
}

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This is my own implementation of an table cell renderer 
 * for the single Table lines and collons.
 * @author Christoph Lauer
 * @version 1.0,  Current 26/09/2002
 */
class ColoredTableCellRenderer implements TableCellRenderer {
    private Color lightBlue = new Color(160, 160, 255);
    private Color darkBlue  = new Color( 64,  64, 128);

    public Component getTableCellRendererComponent(
        JTable table,
        Object value,
        boolean isSelected,
        boolean hasFocus,
        int row,
        int column
    ) {
        //Label erzeugen
        JLabel label = new JLabel((String)value);
        label.setOpaque(true);
        Border b = BorderFactory.createEmptyBorder(1,1,1,1);
        label.setBorder(b);
        label.setFont(table.getFont());
        label.setForeground(table.getForeground());
        label.setBackground(table.getBackground());
        if (hasFocus) {
            label.setBackground(darkBlue);
            label.setForeground(Color.white);
        } else if (isSelected) {
            label.setBackground(lightBlue);
        } else {
            //Angezeigte Spalte in Modellspalte umwandeln
            column = table.convertColumnIndexToModel(column);
            if (column == 1) {
                if (row<5) {
                    label.setBackground(Color.yellow);
                    label.setForeground(Color.black);
                } else if (row>4 && row<7) {
                    label.setBackground(Color.orange);
                    label.setForeground(Color.black);
                } else if (row>6 && row<11) {
                    label.setBackground(Color.magenta);
                    label.setForeground(Color.black);
                } else if (row>10 && row<13) {
                    label.setBackground(Color.cyan);
                    label.setForeground(Color.black);
                } else if (row>12 && row<15) {
                    label.setBackground(Color.red);
                    label.setForeground(Color.black);
                } else if (row>14 && row<17) {
                    label.setBackground(Color.green);
                    label.setForeground(Color.black);
                } else if (row==17) {
                    label.setBackground(Color.gray);
                    label.setForeground(Color.white);
                } else if (row>17 && row<20) {
                    label.setBackground(darkBlue);
                    label.setForeground(Color.white);
                } else if (row == 20 || row == 21 || row==22 || row == 23) {
                    label.setBackground(Color.blue);
                    label.setForeground(Color.orange);
                } else if (row == 24) {
                    label.setBackground(new Color(202,225,255));
                    label.setForeground(Color.black);
                } else if (row == 25 || row == 26) {
                    label.setBackground(new Color(195,255,62));
                    label.setForeground(Color.red);
                } else if (row == 27 || row==28) {
                    label.setBackground(new Color(195,201,255));
                    label.setForeground(Color.blue);
                } else if (row == 29 || row==30 || row == 31) {
                    label.setBackground(new Color(65,105,225));
                    label.setForeground(Color.black);
                } else if (row == 32 || row==33 || row == 34 || row == 35) {
                    label.setBackground(new Color(165,0,0));
                    label.setForeground(Color.black);
                }
            }
            if (column == 0) {
                label.setBackground(lightBlue);
            }
        }
        return label;
    }
}
