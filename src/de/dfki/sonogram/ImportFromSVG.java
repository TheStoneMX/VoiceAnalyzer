package de.dfki.sonogram;
 
import java.io.*;
import javax.swing.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This class imports SVG files to Sonogram Internal Spectrums.
 * For that it parses the file with the here implemented parser which contains 
 * no DOM or SAX parser.
 * @author Christoph Lauer
 * @version 1.0,  Current 26/09/2002
 */
class ImportFromSVG {
    Sonogram     reftomain;
    File         file;
    //----------------------------------------------------------------------------
    /**
     * Construktor in which all is done to re import SVG file.
     * Open filechooser read Sonogram specific XML Tags
     * and save them to Sonogram main Class, take a question to 
     * reopen orginal file, generate new spektrum vector-array  
     * from SVG file and hang it into Sonogram main application. 
     * Note: Onely SVG files saved by Sonogram self can be reimported.
     */
    public ImportFromSVG(Sonogram s) {
        boolean writtenbysonogram = false;
        boolean writtenaspuresvg  = false;
        boolean isreadable        = false;
        String  line,tmp;
        int     t1        = 0;
        int     t2        = 0;
        ;
        int     width     = 0;
        int     heigth    = 0;
        int     x         = 0;
        int     y         = 0;
        int     amplitude = 0;
        int     scale     = 0;

        reftomain = s;
        System.out.println("--> Import Spectrum from SVG");
        EFileChooser chooser = new EFileChooser();

        chooser.setApproveButtonText("Okay");
        chooser.setDialogTitle("Select SVG file to Import");
        MediaFileFilter ffsvg = new MediaFileFilter("svg","Scalable Vector Graphics (*.svg)");
        chooser.addChoosableFileFilter(ffsvg);
        int returnVal = 0;
        try {
            do { // Check for Sonogram SVG file
                do {  // Open File with Chooser
                    returnVal = chooser.showOpenDialog(reftomain);
                    if (returnVal == JFileChooser.CANCEL_OPTION)
                        return;
                } while
                (returnVal != JFileChooser.APPROVE_OPTION);
                file = chooser.getSelectedFile();
                System.out.println("--> Chosed File:"+file.getAbsolutePath());
                BufferedReader rd = new BufferedReader(new FileReader(file));
                // Search for Sonogram SVG file specific String
                while((line = rd.readLine()) != null) {
                    if (line.indexOf("SVG file written by Sonogram")>0) {
                        System.out.println("--> File written by Sonogram.");
                        writtenbysonogram = true;
                    }
                    if (line.indexOf("SVG file written as Pure svg file")>0) {
                        System.out.println("--> No reopen File.");
                        writtenaspuresvg = true;
                    }
                }
                if (writtenbysonogram == false) {
                    reftomain.messageBox("No open possible","SVG file not written by Sonogram.\nNo opening possible",2);
                    return;
                }
                if (writtenaspuresvg == true) {
                    reftomain.messageBox("No open possible","SVG file written as PURE SVG\nwith no Sonogram specific Tags.\nNo opening possible.",2);
                    return;
                }
            } while (writtenbysonogram==false);
            // Read Configuration of this file and set Changes to Sonogram
            reftomain.spektrumExist = false;
            reftomain.openingflag   = true;
            BufferedReader rd = new BufferedReader(new FileReader(file));
            while((line = rd.readLine()) != null) {
                if (line.indexOf("FilePath")>0) {
                    t1  = line.indexOf("FilePath");
                    t1 += 10;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    reftomain.filepath = tmp;
                }
                if (line.indexOf("TimewindowLength")>0) {
                    t1  = line.indexOf("TimewindowLength");
                    t1 += 18;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    reftomain.timewindowlength = (short)Integer.parseInt(tmp);
                    heigth = reftomain.timewindowlength/2;
                }
                if (line.indexOf("WindowNumbers")>0) {
                    t1  = line.indexOf("WindowNumbers");
                    t1 += 15;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    width = Integer.parseInt(tmp);
                }
                if (line.indexOf("WindowNumberAuto")>0) {
                    t1  = line.indexOf("WindowNumberAuto");
                    t1 += 18;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("true"))
                        reftomain.gad.cauto.setSelected(true);
                    if (tmp.equals("false"))
                        reftomain.gad.cauto.setSelected(false);
                }
                if (line.indexOf("WindowFunktion")>0) {
                    t1  = line.indexOf("WindowFunktion");
                    t1 += 16;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("Hamming"))
                        reftomain.hamItem.setSelected(true);
                    if (tmp.equals("Hanning"))
                        reftomain.hanItem.setSelected(true);
                    if (tmp.equals("Blackman"))
                        reftomain.blaItem.setSelected(true);
                    if (tmp.equals("Rectagle"))
                        reftomain.rectItem.setSelected(true);
                    if (tmp.equals("Triangle"))
                        reftomain.triItem.setSelected(true);
                    if (tmp.equals("Gauss"))
                        reftomain.gauItem.setSelected(true);
                    if (tmp.equals("Welch"))
                        reftomain.welItem.setSelected(true);
                }
                if (line.indexOf("SmoothFrequenz")>0) {
                    t1  = line.indexOf("SmoothFrequenz");
                    t1 += 16;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("true")) {
                        reftomain.gad.csmooth.setSelected(true);
                        reftomain.gad.marksmothy = true;
                    }
                    if (tmp.equals("false")) {
                        reftomain.gad.csmooth.setSelected(false);
                        reftomain.gad.marksmothy = false;
                    }
                }
                if (line.indexOf("SmoothTime")>0) {
                    t1  = line.indexOf("SmoothTime");
                    t1 += 12;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("true")) {
                        reftomain.gad.csmoothx.setSelected(true);
                        reftomain.gad.marksmothx = true;
                    }
                    if (tmp.equals("false")) {
                        reftomain.gad.csmoothx.setSelected(false);
                        reftomain.gad.marksmothx = true;
                    }
                }
                if (line.indexOf("OverLapping")>0) {
                    t1  = line.indexOf("OverLapping");
                    t1 += 13;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("to=")==false)   // parsing Overlappingauto and overlapping
                        reftomain.gad.sliderwinspeed.setValue(Integer.parseInt(tmp));
                }
                if (line.indexOf("OverLappingAuto")>0) {
                    t1  = line.indexOf("OverLappingAuto");
                    t1 += 17;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("true")) {
                        reftomain.gad.coverlapping.setSelected(true);
                        reftomain.gad.markoverl = true;
                    }
                    if (tmp.equals("false")) {
                        reftomain.gad.coverlapping.setSelected(false);
                        reftomain.gad.markoverl = false;
                    }
                }
                if (line.indexOf("LogarithmEnabled")>0) {
                    t1  = line.indexOf("LogarithmEnabled");
                    t1 += 18;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("true")) {
                        reftomain.gad.clog.setSelected(true);
                        reftomain.gad.marklog = true;
                    }
                    if (tmp.equals("false")) {
                        reftomain.gad.clog.setSelected(false);
                        reftomain.gad.marklog = false;
                    }
                }
                if (line.indexOf("LogarithmScale")>0) {
                    t1  = line.indexOf("LogarithmScale");
                    t1 += 16;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    reftomain.gad.sliderlog.setValue(Integer.parseInt(tmp));
                }
                if (line.indexOf("Samplerate8Khz")>0) {
                    t1  = line.indexOf("Samplerate8Khz");
                    t1 += 16;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("true")) {
                        reftomain.gad.csampl.setSelected(true);
                        reftomain.gad.markcsamp = true;
                    }
                    if (tmp.equals("false")) {
                        reftomain.gad.csampl.setSelected(false);
                        reftomain.gad.markcsamp = false;
                    }
                }
                if (line.indexOf("Transformation")>0) {
                    t1  = line.indexOf("Transformation");
                    t1 += 16;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    if (tmp.equals("FFT")) {
                        reftomain.gad.rfft.setSelected(true);
                        reftomain.gad.marktrans = true;
                    }
                    if (tmp.equals("LPC")) {
                        reftomain.gad.rlpc.setSelected(true);
                        reftomain.gad.marktrans = true;
                    }
                }
                if (line.indexOf("LPCCoeficients")>0) {
                    t1  = line.indexOf("LPCCoeficients");
                    t1 += 16;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    reftomain.gad.sliderlpccoef.setValue(Integer.parseInt(tmp));
                }
                if (line.indexOf("LPCFFTPoints")>0) {
                    t1  = line.indexOf("LPCFFTPoints");
                    t1 += 14;
                    t2  = line.indexOf("\"",t1);
                    tmp = line.substring(t1,t2);
                    reftomain.gad.sliderlpcfftnum.setValue(Integer.parseInt(tmp));
                }
                if (line.indexOf("PaintScaleFact")>0) {
                    t1   = line.indexOf("PaintScaleFact");
                    t1  += 16;
                    t2   = line.indexOf("\"",t1);
                    tmp  = line.substring(t1,t2);
                    scale= Integer.parseInt(tmp);
                }
                if (line.indexOf("SampleRate")>0) {
                    t1   = line.indexOf("SampleRate");
                    t1  += 12;
                    t2   = line.indexOf("\"",t1);
                    tmp  = line.substring(t1,t2);
                    reftomain.samplerate = Integer.parseInt(tmp);
                }
                if (line.indexOf("SamplesTotal")>0) {
                    t1   = line.indexOf("SamplesTotal");
                    t1  += 14;
                    t2   = line.indexOf("\"",t1);
                    tmp  = line.substring(t1,t2);
                    reftomain.samplestotal = Integer.parseInt(tmp);
                }
            }
            reftomain.gad.applyChanges();
            System.out.println("--> Configuration tag read: w="+width+" heigth="+heigth);
            // Allocate new Specbuffer
            reftomain.spektrum.removeAllElements();
            for (int wn=0;wn<width;wn++)
                reftomain.spektrum.addElement(new float[heigth]);
            // Allocate temorary Array
            float[][] temparray = new float[width][heigth];
            // Read Data (Spectrum)
            rd = new BufferedReader(new FileReader(file));
            while((line = rd.readLine()) != null) {
                if (line.indexOf("rect")>0) {
                    t1   = line.indexOf("amplitude=");
                    t1   += 11;
                    t2   = line.indexOf("\"",t1);
                    tmp  = line.substring(t1,t2);
                    amplitude = Integer.parseInt(tmp);
                    t1   = line.indexOf("x=");
                    t1   += 3;
                    t2   = line.indexOf("\"",t1);
                    tmp  = line.substring(t1,t2);
                    x = Integer.parseInt(tmp);
                    t1   = line.indexOf("y=");
                    t1   += 3;
                    t2   = line.indexOf("\"",t1);
                    tmp  = line.substring(t1,t2);
                    y = Integer.parseInt(tmp);
                    temparray[x/scale][y/scale] = (float)amplitude;
                }
            }
            // Copy tmp in Spektrum
            float[] spekbuffer;
            for (int time=0;time<width;time++) {
                spekbuffer = (float[])reftomain.spektrum.get(time);
                for (int frequency = 0;frequency<heigth;frequency++) {
                    spekbuffer[heigth - frequency - 1] = temparray[time][frequency];
                }
            }
            // Check is File Exist
            try {
                File f = new File(reftomain.filepath.substring(5));
                isreadable = f.canRead();
                System.out.println("--> File is redable:" + isreadable);
            } catch (Throwable t) {
                reftomain.messageBox("Error","Error while check for file if exist",2);
            }
            // Copy Timeline
            float[] timelinetmp = new float[2000];
            float sum=0;
            float max=0;
            for (int t=0;t<2000;t++) {
                sum=0;
                for (int f=0;f<heigth;f++) {
                    sum+=temparray[((int)((float)t/2000.0f*(float)width))][f];
                }
                timelinetmp[t]=sum;
                if (max<sum)
                    max = sum;
            }
            for (int t=0;t<2000;t++) {
                timelinetmp[t]/=max;
                timelinetmp[t]*=(127);
            }
            for (int t=0;t<2000;t++)
                reftomain.timeline[t] =(byte)timelinetmp[t];
            reftomain.gad.cenergy.setSelected(false);
            // Some Stuff
            reftomain.spektrumExist   = true;      // Only for repaint
            reftomain.updateimageflag = true;
            reftomain.openingflag     = false;
            reftomain.repaint();

            if (isreadable == true) {
                reftomain.player = new PlaySound(reftomain.filepath,reftomain);
                reftomain.pp.plstart  = 0.0;
                reftomain.pp.plstop   = 1.0;
                reftomain.pp.plbutton = 0.0;
                System.out.println("--> Opening from SVG File Finished");
                int confirm = JOptionPane.showOptionDialog(reftomain,
                              "If a plot is imported via SVG,\n" +
                              "not all features of Sonogram are\n" +
                              "aviable (zoom,logarithm frequency...)\n" +
                              "Would you reopen ths File from orginal source ?", "Reopen ?"
                              ,JOptionPane.YES_NO_OPTION
                              ,JOptionPane.QUESTION_MESSAGE
                              ,null,null,null);
                if (confirm == 0) {
                    reftomain.openFile(reftomain.filepath);
                    return;
                }
            }

            reftomain.spektrumExist   = false;    // hinders repaint
            reftomain.enableItems(true);
            reftomain.stopItem.setEnabled(false);
            reftomain.stopbutton.setEnabled(false);
            reftomain.infoItem.setEnabled(false);
            reftomain.infobutton.setEnabled(false);
            reftomain.zinbutton.setEnabled(false);
            reftomain.zinItem.setEnabled(false);
            reftomain.zbabutton.setEnabled(false);
            reftomain.zbaItem.setEnabled(false);
            reftomain.zprebutton.setEnabled(false);
            reftomain.zpreItem.setEnabled(false);
            reftomain.cepbutton.setEnabled(false);
            reftomain.wavbutton.setEnabled(false);
            reftomain.wavItem.setEnabled(false);
            reftomain.cepItem.setEnabled(false);
            reftomain.forbutton.setEnabled(false);
            reftomain.forItem.setEnabled(false);
            reftomain.lpcbutton.setEnabled(false);
            reftomain.lpcItem.setEnabled(false);
            reftomain.hamItem.setEnabled(false);
            reftomain.hanItem.setEnabled(false);
            reftomain.gauItem.setEnabled(false);
            reftomain.welItem.setEnabled(false);
            reftomain.blaItem.setEnabled(false);
            reftomain.triItem.setEnabled(false);
            reftomain.svgItem.setEnabled(false);
            reftomain.svgbutton.setEnabled(false);
            reftomain.walItem.setEnabled(false);
            reftomain.walbutton.setEnabled(false);
            reftomain.rectItem.setEnabled(false);
            reftomain.d3button.setEnabled(false);
            reftomain.d3Item.setEnabled(false);
            reftomain.gad.sliderwinfunktion.setEnabled(false);
            reftomain.logItem.setEnabled(false);
            reftomain.gad.clog.setEnabled(false);
            reftomain.gad.sliderlog.setEnabled(false);
            reftomain.gad.csmoothx.setEnabled(false);
            reftomain.gad.csmooth.setEnabled(false);
            reftomain.gad.cenergy.setEnabled(false);
            reftomain.gad.coverlapping.setEnabled(false);
            reftomain.gad.sliderwinspeed.setEnabled(false);
            reftomain.gad.sliderwinsize.setEnabled(false);
            reftomain.gad.cauto.setEnabled(false);
            reftomain.spektrumExist   = true;  // set back to on


            if (isreadable==false) {
                reftomain.messageBox("Source File not found","The original Source File of this plot was not found at:\n"+reftomain.filepath+"\nNot all the Features of Sonogram are aviable without\nthe original Source Media File.",2);
                reftomain.gad.csampl.setEnabled(false);
                reftomain.playbutton.setEnabled(false);
                reftomain.playItem.setEnabled(false);
                reftomain.stopbutton.setEnabled(false);
                reftomain.stopItem.setEnabled(false);
                reftomain.revbutton.setEnabled(false);
                reftomain.revItem.setEnabled(false);
                reftomain.gad.btro.setEnabled(false);
                reftomain.wavbutton.setEnabled(false);
                reftomain.wavItem.setEnabled(false);
                reftomain.walItem.setEnabled(false);
            }
            File file = new File(reftomain.filepath.substring(5));
            reftomain.setTitle("Sonogram 1.2  " + file.getName());
            reftomain.openenedbysvg = true;
        } catch (Throwable t) {
            reftomain.messageBox("Error","Error while reading File\nSee console for details.",1);
            System.out.println(t);
        }
    }
}

