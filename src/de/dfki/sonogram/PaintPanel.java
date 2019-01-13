package de.dfki.sonogram;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.dnd.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * This class presents the Paint Window
 * to display a Sonogram Picture.
 * It is a generic Component and implements the
 * @author Christoph Lauer
 * @version 1.6,  Current 10/07/2002
 */
public class PaintPanel extends JPanel implements MouseMotionListener {
    int           onespek       = 0;
    short         xm            = 0;
    short         ym            = 0;
    int           wnp           = 0;                               // Window Number Position for paintOneSpejtrum in units
    int           wsp           = 0;                               // Window Size position for paintOneSpejtrum in units
    double        wnf           = 0.0;                             // self as wnp from 0..1
    double        wsf           = 0.0;                             // self as wsp from 0..1
    int           mousex        = 0;
    int           mousey        = 0;
    Color         coldv         = new Color(80,80,120);
    Color         colred        = new Color(200,0,0);
    Color         collv         = new Color(150,150,200);
    Color         colwt         = new Color(255,255,255);
    Color         colbk         = new Color(0,0,0);
    Color         coldg         = new Color(15,50,20);             // Darkgreen
    Color         collg         = new Color(220,255,0);
    Color         colg          = new Color(180,205,0);
    public double plstart       = 0.0;                             // Playstart
    public double plstop        = 0.0;                             // Playstop
    double        plbegin       = 0.0;
    public double plbutton      = 0.0;
    private Sonogram reftosonogram;
    Color[]       coFI          = FireColor.getFireColorArray();
    ;
    Color[]       coFIi         = new Color[256];
    Color[]       coCO          = new Color[256];
    Color[]       coCOi         = new Color[256];
    Color[]       coRA          = new Color[256];
    Color[]       coRAi         = new Color[256];
    Color[]       coCG          = new Color[256];
    Color[]       coCGi         = new Color[256];
    Color[]       coSW          = new Color[256];
    Color[]       coSWi         = new Color[256];
    Dimension     olddimension;
    BufferedImage         doublebufferimage;
    Graphics2D      g;
    Image         defaultimage;
	
    AlphaComposite compositeNebular  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
    AlphaComposite compositeAl  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
    AlphaComposite compositeAl1 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
    AlphaComposite compositeAl2 = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);
    AlphaComposite compositeGr  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    AlphaComposite compositeFo  = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f);
    AlphaComposite compositeNo  = AlphaComposite.getInstance(AlphaComposite.SRC);
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Overrides default update Funktion which 
     * not clear Screen.
     */
    public void update(Graphics g) {
        paint(g);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
      * Construktor for PaintPanel.
      * Here are Colorarrays generated and Border set.
      */
    public PaintPanel(Sonogram ref) {
        reftosonogram = ref;
        setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.LOWERED));
        addMouseMotionListener(this);
        int r=0,g=0,b=0,s=0;
        for (int i=0;i<256;i++) {                                        // building Colorarrays
            // Generate Balck/White Colors
            coSWi[i]  = new Color(i,i,i);
            coSW[i] = new Color(255-i,255-i,255-i);
            // Generate rainbow Colors
            r = i;
            g =(255 - Math.abs(i - 120)*4);
            if (g < 0)
                g=0;
            b =(255 - Math.abs(i - 60)*4);
            if (b < 0)
                b=0;
            if (i>210)
                b=(210 - i)*(-5);
            coRA[i]  = new Color(r,g,b);
            coRAi[i] = new Color(255-r,255-g,255-b);
            // generate normal Colors
            g =(i-128)*2;
            if (g<0)
                g=0;
            coCO[i]  = new Color(r,g,b);
            coCOi[i] = new Color(255-r,255-g,255-b);
            // generate fire Colors
            if (i<=90) {
                r=120-(int)(120.0*((double)i/90.0));
                g=160-(int)(160.0*((double)i/90.0));
                b=207;
            }
            if (i>90)
                g=(int)(255.0*((double)(i-90)/165.0));
            if (i>100 && i<=150)
                r=(int)(255.0*((double)(i-100)/50.0));
            if (i>150)         {
                r=255;
                b=0;
            }
            if (i>90 && i<=150)
                r=(int)(255.0*((double)(i-90)/60.0));
            coFIi[i] = new Color(255-r,255-g,255-b);
            coCG[i]  = new Color(i/2+30,i,20);
            coCGi[i] = new Color(i/2,i/2,i);

        }

        java.net.URL imurl = this.getClass().getResource("BigSplash.jpg");
        //java.net.URL imurl = this.getClass().getResource("SplashCopy.jpg");
        defaultimage = new ImageIcon(imurl).getImage();
        setDropTarget(new DropTarget(reftosonogram,new DnDHandler(reftosonogram)));
        MouseListener mlst = new MouseAdapter () {
                                 public void mousePressed(MouseEvent e) {
                                     int x = e.getX();
                                     if ((x>40 && x<(xm-60))) {
                                         double pos = (double)(x-40) / (double)(xm-100);
                                         plstart = plstop = plbegin = plbutton = pos;
                                         paintTimeSlider(null);
                                     }
                                 }
                             };
        addMouseListener(mlst);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Eventhandling for mouse move events.
     * as defined in Interface of the MouseMotionListener
     * @param e Mouseevent
     */
    public void mouseMoved (MouseEvent e) {
        int x = mousex = e.getX();
        int y = mousey = e.getY();
	if ((x>39 && x<(xm-60)) && (y<ym-70)) {
            wnf = (double)(x-39) / (double)(xm-101);           // relative X position
            wsf = (double)(y)    / (double)(ym-70) ;           // relative y position
	    wnp = (int) ((double)reftosonogram.spektrum.size()      * wnf);  // time position in samples
            wsp = (int) ((double)(reftosonogram.timewindowlength/2) * wsf);  // frequenz position in Hz
            paintOneSpektrum(false);
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * set Funktion implements the Interfacefunktion from MouseMotionListener.
     * Is called when mousebutton is pressed and mouse moved.
     */
    public void mouseDragged (MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        if ((x>40 && x<(xm-60))) {
            double pos = (double)(x-40) / (double)(xm-100);
            if (pos < plbegin) {
                plstart   = pos;
                plbutton  = pos;
                plstop    = plbegin;
            }
            if (pos > plbegin) {
                plstart   = plbegin;
                plbutton  = plbegin;
                plstop    = pos;
            }
            reftosonogram.selectedstart = reftosonogram.selectedstartold + plstart * reftosonogram.selecedwidthold;
            reftosonogram.selecedwidth  = reftosonogram.selecedwidthold  * (plstop - plstart);
            paintTimeSlider(null);
            if (reftosonogram.infovisible==true)
                reftosonogram.infod.update();
        }
        mouseMoved(e);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Updates the Sliderpicture at bottom.
     * @param g: Grapgics-Object to paint up. Is g = null so this Method recive it from "this"
     */
    public void paintTimeSlider(Graphics g) {
        if (reftosonogram.spektrumExist == true) {
            int xb = (int)(plstart * (double)(xm-100)) + 40;     //
            int xe = (int)(plstop  * (double)(xm-100)) + 40;     //
            int xl;
            if (plbutton<plstop)
                xl = (int)(plbutton *(double)(xm-100)) + 38;     //
            else
                xl = (int)(plstop   *(double)(xm-100)) + 38;     //
            if (g == null)
                g = this.getGraphics();
            g.setColor(coldv);
            g.fillRect(38,ym-70,xm-96,10);                       // Delete old one
            g.setColor(collg);
            g.drawRect(xb,ym-67,xe-xb,3);                        // Rect in middle
            g.drawRect(xl,ym-70,4,9);                            // Button
            g.drawLine(xe,ym-70,xe,ym-61);                       // Line at end
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Updates the Sliderpicture at bottom.
     * @param g: Grapgics-Object to paint up. Is g = null so this Method recive it from "this"
     */
    public void paintTimeSliderAtOnePlace(double place) {
        if (reftosonogram.spektrumExist == true) {
            int x = (int)(place * (double)(xm-100)) + 40;     //
            g = (Graphics2D) this.getGraphics();
            g.setColor(coldv);
            g.fillRect(38,ym-70,xm-96,10);                       // Delete old one
           g.setColor(collg);
            g.drawRect(x,ym-70,4,9);                            // Button
            g.drawLine(x,ym-70,x,ym-61);                       // Line at end
        }
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Paints the Simple Frequency View and its smooth-line.
     * Is calrd from mouseMoved and paints its Values also.
     @param playing: If Funtion is called from Playing, specposition is getted from plbutton variable.
     */
    public void paintOneSpektrum(boolean calledfromplaying) {
        if (reftosonogram.spektrumExist==true && reftosonogram.openingflag==false && reftosonogram.transformflag==false) {
            Graphics2D g         = (Graphics2D)this.getGraphics();
            double   ffakt       = (double)(ym-70) / (double)(reftosonogram.timewindowlength/2);
            float    peakamp     = 0.0f;
            float    peakfrequ   = 0.0f;
            float    mousefrequ  = 0.0f;
            float    mousetime   = 0.0f;
            float    mouseamp    = 0.0f;
            float[]  spekbuff;
            float[]  orginalbuff;
            int      peakplace   = 0;
            int      x,y;
            // For Logarithm Frequencyview
            double   powerof     = (double)reftosonogram.gad.sliderlogfr.getValue()/3.0;// Power from GAD
            double   powkonst    = Math.pow(10,powerof);                  // Konst for scale y achse
            double   powfact     = 0.0;                                   // Logarithm Scale fact
            int      yppow       = 0;                                     // Logarithmy Position
            int      diffylog    = 0;

            if (reftosonogram.gad.cantialise.isSelected() == true)
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            // Paint Spectrum
            g.setColor(coldg);                                            // BG rect
            g.fillRect(xm-55,2,53,ym-71);                    // Rect for Single Fr. view
            g.setColor(collv);
            g.drawLine(xm-55,ym-69,xm-2,ym-69);
            //GRID
            g.setColor(coldv);
            for (double yg=0;yg<ym-69.0;yg+=((double)ym-70.0)/10.0)
                // logarithm Frequency
                if (reftosonogram.gad.cslogfr.isSelected()==true) {
                    powfact      = Math.pow((double)(yg+1)/(double)(ym-69),powerof);
                    yppow        = (int)((double)yg*powfact);
                    if (yppow>2)
                        g.drawLine(xm-60,(int)yppow,xm-3,(int)yppow);
                }
                // Non Logarithm
                else
                    if (yg>2)
                        g.drawLine(xm-60,(int)yg,xm-3,(int)yg);
            if (calledfromplaying == true && reftosonogram.gad.csspecwhileplaying.isSelected()==true) {// When plaing take position from plbutton
                {
                    int spectimepos = (int)(plbutton*(double)reftosonogram.spektrum.size());
                    if (spectimepos < reftosonogram.spektrum.size())
                        orginalbuff = (float[])reftosonogram.spektrum.get(spectimepos);
                    else
                        orginalbuff = (float[])reftosonogram.spektrum.get(reftosonogram.spektrum.size()-1);
                }
            } else {
                if (wnp >=  reftosonogram.spektrum.size())
                    wnp  =  reftosonogram.spektrum.size()-1;
                if (wnp < 0 )
                    wnp = 0;
                orginalbuff = (float[])reftosonogram.spektrum.get(wnp);
            }
            // If Normalize is on
            if (reftosonogram.gad.cback.isSelected()==true) {
                float peak = 0.0f;
                spekbuff   = new float[reftosonogram.timewindowlength/2];
                for (int i=0;i<(reftosonogram.timewindowlength/2);i++)
                    if(peak < orginalbuff[i])
                        peak = orginalbuff[i];
                for (int i=0;i<(reftosonogram.timewindowlength/2);i++)
                    spekbuff[i]=orginalbuff[i]/peak*255.0f;
            } else
                spekbuff = orginalbuff;
            // Draw Spektrum
            double step = (double)reftosonogram.timewindowlength/2.0/(double)(ym-60);
            for (double f=(float)(reftosonogram.timewindowlength/2)-1.0f;f>0.0f;f-=step) {
                x = (int) (spekbuff[(int)f]/255.0f*52.0f);
                y = (int) ((ym-72) - f * ffakt);
                if (reftosonogram.gad.cscolsi.isSelected()==true) {
		    g.setColor(collg);
		}
                else
                    selectColor(g,(int)spekbuff[(int)f]);
                if (reftosonogram.gad.cslogfr.isSelected()==true) { // Logarithm
                    powfact      = Math.pow(((double)(reftosonogram.timewindowlength/2.0)-(double)f)/(double)reftosonogram.timewindowlength*2.0,powerof);
                    yppow        = (int)((double)y*powfact+0.5);

                    double fnext       = f + step;
                    int    ynext       = (int) ((ym-72) - fnext * ffakt);
                    double powfactnext = Math.pow(((double)(reftosonogram.timewindowlength/2.0)-(double)fnext)/(double)reftosonogram.timewindowlength*2.0,powerof);
                    int    yppownext   = (int)((double)ynext*powfactnext-0.5);

                    diffylog           = yppow - yppownext;
                    if (diffylog==0)
                        diffylog=1;
                    if (yppow>2)
                        g.fillRect(xm-2-x,yppow,x,diffylog);
                } else                                  // No Logarithm
                    if (y>-1)
                        g.drawLine(xm-3-x,y+2,xm-3,y+2);
            }
            // Smoothed red curve
            if (reftosonogram.gad.csmoothsi.isSelected()==true) {
                // Search and draw peak point
                for (int f=0;f<(reftosonogram.timewindowlength/2);f++) {
                    if (peakamp < spekbuff[f]) {
                        peakplace = f;
                        peakamp   = spekbuff[f];
                    }
                }
                g.setColor(new Color(200,0,0));                             // Peakpoint
                x = (int) (spekbuff[(int)peakplace]/255.0f*50f);
                y = (int) ((ym-72) - peakplace * ffakt);
                if (reftosonogram.gad.cslogfr.isSelected()==true) {
                    powfact      = Math.pow(((double)(reftosonogram.timewindowlength/2.0)-(double)peakplace)/(double)reftosonogram.timewindowlength*2.0,powerof);
                    y        = (int)Math.ceil((double)y*powfact);
                }
                if (x>5 && y>5)
                    g.fillOval(xm-x-4,y-4,8,8);
                // Draw Smooth
                float[] smoothspek = new float[reftosonogram.timewindowlength/2];
                for (int f=3;f<(reftosonogram.timewindowlength/2-3);f++) {
                    smoothspek[f] = (  spekbuff[f-1] + spekbuff[f-2] + spekbuff[f-3]
                                       + spekbuff[f+1] + spekbuff[f+2] + spekbuff[f+3])/6.0f;
                }
                g.setColor(colred);
                int xa = xm - 3;
                int ya = ym - 72;
                for (double f=0.0f;f<(float)(reftosonogram.timewindowlength/2);f+=((double)reftosonogram.timewindowlength/2.0/(double)(ym-60))) {
                    x = xm - 3 - (int) (smoothspek[(int)f]/255.0f*52.0f);
                    y = (int) ((ym-72) - f * ffakt)+2;
                    if (reftosonogram.gad.cslogfr.isSelected()==true) {
                        powfact = Math.pow(((double)(reftosonogram.timewindowlength/2.0)-(double)f)/(double)reftosonogram.timewindowlength*2.0,powerof);
                        y       = (int)Math.ceil((double)y*powfact);
                    }
                    if (y>1 && y<(ym-71))
                        g.drawLine(x,y,xa,ya);
                    ya = y;
                    xa = x;
                }
	    }
                // And Now let's paint and Calculate the numbers for the right upper display
                peakfrequ  = (float)peakplace/(float)(reftosonogram.timewindowlength/2)*(float)reftosonogram.samplerate/2.0f;
                mousefrequ = (float)reftosonogram.samplerate/2.0f-(float)wsp/(float)(reftosonogram.timewindowlength/2)*(float)reftosonogram.samplerate/2.0f;
		if (reftosonogram.gad.cslogfr.isSelected()==true) { // frequency logarithm display
		    // scale wieder delogarithmieren auf normale scala
		    double mousecoord = Math.pow((double)mousey*Math.pow((double)ym-70.0,powerof),1.0/((double)powerof+1.0));

		    //
		    //
		    //
		    // Notiz: obige formel aus anderen hegeleitet. Vermeintliche nichtlinearität
		    // in fünf stunden auf view seiten papier gelösst !!!
		    //
		    //
		    //	   

		    double wsflog = (double)(mousecoord)    / (double)(ym-70) ;           // relative y position
		    double wsplog = (int) ((double)(reftosonogram.timewindowlength/2) * wsflog);  // frequenz position in Hz
		    mousefrequ = (float)reftosonogram.samplerate/2.0f-(float)wsplog/(float)(reftosonogram.timewindowlength/2)*(float)reftosonogram.samplerate/2.0f;
		}
		else 
		    mousefrequ = (float)reftosonogram.samplerate/2.0f-(float)wsp/(float)(reftosonogram.timewindowlength/2)*(float)reftosonogram.samplerate/2.0f;

// 		if (reftosonogram.gad.cslogfr.isSelected()==true) {
//                     powfact    = Math.pow((double)mousefrequ/(double)(reftosonogram.samplerate/2),powerof)/powkonst;
//                     mousefrequ = (int)(mousefrequ*powfact);
//                 }
                if (calledfromplaying == true && reftosonogram.gad.csspecwhileplaying.isSelected()==true)// When plaing take position from plbutton
                    mousetime  = (float)((plbutton)*reftosonogram.selecedwidthold+reftosonogram.selectedstartold) * (float)reftosonogram.samplesall /  (float)reftosonogram.samplerate;
                else
                    mousetime  = ((float) reftosonogram.selectedstartold + (float)wnf
                                  * (float) reftosonogram.selecedwidthold)  * (float)reftosonogram.samplesall /  (float)reftosonogram.samplerate;
                mousetime  = (float)Math.round(mousetime*100) / 100.0f;
                mouseamp   = (float)orginalbuff[reftosonogram.timewindowlength/2-wsp-1] / 255.0f * 100.0f;
                mouseamp   = (float)Math.round(mouseamp*10) / 10.0f;

                g.setColor(collv);                          // Delete old Numbers
                g.fillRect(xm-55,ym-69,52,66);              // Delete old one
                g.setColor(new Color(125,125,185));
                g.fillRect(xm-56,ym-30,51,26);              // filled zone
                g.fillRect(xm-56,ym-65,51,31);              // filled zone

                g.setColor(new Color(95,95,180));           // bounds
                g.drawRect(xm-57,ym-31,52,27);
                g.drawRect(xm-57,ym-66,52,32);

                g.setColor(new Color(20,20,130));
                g.setFont(new Font("Dialog",0,9));
                String spe    = ("P: " + String.valueOf((int)peakfrequ)  + "Hz");
                String sfr    = ("F: " + String.valueOf((int)mousefrequ) + "Hz");
                String sti    = ("T: " + String.valueOf(mousetime)       + "s.");
                String sam    = ("A: " + String.valueOf(mouseamp)        + "%");
                boolean first = true;
                String optA    = "";
                if (reftosonogram.gad.csmoothx.isSelected()==true) {
                    if (first==true)
                        first = false;
                    else
                        optA += ",";
                    optA += "sT";
                }
                if (reftosonogram.gad.csmooth.isSelected()==true) {
                    if (first==true)
                        first = false;
                    else
                        optA += ",";
                    optA += "sF";
                }
                if (reftosonogram.gad.coverlapping.isSelected()==true) {
                    if (first==true)
                        first = false;
                    else
                        optA += ",";
                    optA += "Ov"+reftosonogram.gad.sliderwinspeed.getValue();
                }
                String optB    = "";
                if (reftosonogram.hamItem.isSelected()==true)
                    optB += "Ham";
                if (reftosonogram.hanItem.isSelected()==true)
                    optB += "Han";
                if (reftosonogram.blaItem.isSelected()==true)
                    optB += "Bla";
                if (reftosonogram.rectItem.isSelected()==true)
                    optB += "Rec";
                if (reftosonogram.triItem.isSelected()==true)
                    optB += "Tri";
                if (reftosonogram.welItem.isSelected()==true)
                    optB += "Wel";
                if (reftosonogram.gauItem.isSelected()==true)
                    optB += "Gau";
                if (reftosonogram.gad.csampl.isSelected()) {
                    optB += ",8kHz";
                }
                if (reftosonogram.zoompreviousindex!=0) {
                    optB += ",z"+ reftosonogram.zoompreviousindex;
                }
                String optC    = "";
                first = true;
                if (reftosonogram.gad.clog.isSelected()==true) {
                    if (first==true)
                        first = false;
                    optC += "logA";
                }
                if (reftosonogram.gad.cslogfr.isSelected()==true) {
                    if (first==true)
                        first = false;
                    else
                        optC += ",";
                    optC += "logFr";
                }
                g.drawString(spe,xm-56,ym-58);
                g.drawString(sfr,xm-56,ym-50);
                g.drawString(sti,xm-56,ym-42);
                g.drawString(sam,xm-56,ym-34);
                g.drawString(optC,xm-55,ym-23);
                g.drawString(optA,xm-55,ym-14);
                g.drawString(optB,xm-55,ym-5);
                g.drawRect(xm-14,ym-41,7,6);
                selectColor(g,(int)(255f*mouseamp/100f));                    // Draw Colored rect for Amplitude
                g.fillRect(xm-13,ym-40,6,5);
                reftosonogram.wv.update();
                reftosonogram.av.update();
                reftosonogram.cv.update();
                reftosonogram.fv.update();
                reftosonogram.lv.update();
                reftosonogram.kv.update();
		reftosonogram.pv.mouse_x = mousex;
		if (reftosonogram.gad.cptrack.isSelected() == true)
		    reftosonogram.pv.update();
	}
        
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Paint the complette Sonogram on Window with all Items.
     * Method selected between different programm-conditions.
     * It uses the double-buffer technic to buffer the actual
     * view in Image-Object. So no repaint is required to update
     * Complette View. Sliderupdate overrides the Picture that
     * is stored in <i>doublebufferimage</i>. If no changes on
     * win-size are maked from user, simply the Picture are drawn
     * on JPanel.
     */
    public void paintComponent(Graphics gr) {
        Dimension size  = getSize();
        xm             = (short)size.getWidth();                          // Windowsize for Spektogramm
        ym             = (short)size.getHeight();                         // Windowsize for Spektogramm
	//	System.out.println(xm+"  "+ym);
	if(g == null || size.width != olddimension.width
                || size.height != olddimension.height
                ||  reftosonogram.updateimageflag == true) {                   // Check if an Image update is needed


            olddimension = size;
            doublebufferimage = new BufferedImage(size.width-1,size.height,BufferedImage.TYPE_INT_RGB);
            g = (Graphics2D) doublebufferimage.getGraphics();
            if (reftosonogram.spektrumExist == false) {
                g.drawImage(defaultimage,0,0,xm,ym,new Color(0,0,0),this);
                reftosonogram.enableItems(false);
            }
            if (reftosonogram.gad.cantialise.isSelected() == true)
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            if (reftosonogram.spektrumExist == true &&  reftosonogram.openingflag == false) {      // Paint onely when File is opened

                double diffx            = (double)(xm-100)/ (double)reftosonogram.spektrum.size();// difference in Pixel from WFFT to WFFT
                double fakt             = (double)(xm-100)/2000.0;       // To draw Timesignal
                double diffy            = (double)(ym-70) / (double)reftosonogram.timewindowlength*2.0;// difference in Pixel from fr.-point to fr.-point
                int    xp               = 0;                             // Startpoint x for drawing the Rect.
                int    yp               = 0;                             // Startpoint y for drawing the Rect.
                int    rdiffx           = (int)Math.ceil(diffx);         // roundet off diffx
                int    rdiffy           = (int)Math.ceil(diffy);         // roundet off diffy
                float[]tempSpektrum;                                     // Reference to FFT Window
                int    bwcolor;                                          // Black/white Color
                int    red,green,blue;                                   // RGB Colors
                int    frequ            = reftosonogram.samplerate / 2;
                double secs             = 0.0;
                double timetoscreenfakt = (double)(xm-100)/2000.0;       // To draw Timesignal
                float  pitchtmp         = 0.0f;
                float  pitchamp         = 0.0f;
                int    pitchplace       = 0;
                // For Logarithm Frequencyview
                double powerof          = (double)reftosonogram.gad.sliderlogfr.getValue()/3.0; // Power from GAD
                double powkonst         = Math.pow(10,powerof);                   // Konst for scale y achse
                double powfact;                                           // Logarithm Scale fact
                int    diffylog;
                double powfactnext;
                int    ypnext;
                int    yppownext;
                int    yppow;                                             // Logarithm Position
                boolean peaktimeisreached = false;
                int    ypointpeak           = 0;
                int    xpointpeak           = 0;
                // some stuff at beginning
                if (reftosonogram.gad.highlightedbutton == 1)
                    reftosonogram.gad.highLightButton(0);
                reftosonogram.setCursor(Cursor.WAIT_CURSOR);
                System.out.println("--> Updating Sonogram-Image: Paintwindowsize x=" + xm + ", y=" + ym);
                System.out.println("--> diffx=" + diffx + ", diffy=" + diffy);
                g.setColor(collv);
                g.fillRect(0,0,xm,ym);
                if ( reftosonogram.updateimageflag == true)
                    reftosonogram.updateimageflag = false;
                g.setColor(coldv);
                g.setFont(new Font("Courier",0,9));
                // left side Grid over Frequencies
                for (double y=0;y<ym-69.0;y+=((double)ym-70.0)/10.0) {
                    if (reftosonogram.gad.cslogfr.isSelected()==true) {
                        powfact      = Math.pow((double)(y+1)/(double)(ym-69),powerof);
                        yppow        = (int)((double)(y)*powfact);
                        g.drawLine(0,(int)yppow,40,(int)yppow);
                        g.drawString(((double)(frequ / 10)/100.0) + "kHz",3,(int)yppow+9);
                        frequ -= reftosonogram.samplerate / 20;
                    } else {
                        g.drawLine(0,(int)y,40,(int)y);
                        g.drawString(((double)(frequ / 10)/100.0) + "kHz",3,(int)y+9);
                        frequ -= reftosonogram.samplerate / 20;
                    }
                }
                // UPPER DISPLAY Grid over Time
                secs       =  reftosonogram.selectedstartold*(double)reftosonogram.samplesall/(double)reftosonogram.samplerate;
                int isecs  = (int)secs;
                int offset = (int)(1.0 - (secs - (double)isecs) * ((double)xm-100.0) / ((double)reftosonogram.samplestotal /(double)reftosonogram.samplerate));
                secs = isecs;
                if ( reftosonogram.selectedstartold == 0.0)
                    secs = 0.0;
		// second marks
                for (int x=40+offset;x<xm-60;x+=(int)(((double)xm-100.0)/((double)reftosonogram.samplestotal/(double)reftosonogram.samplerate))) {
                    g.drawLine(x-1,ym-60,x-1,ym-40);
                    g.drawString(secs + "s",x+1,ym-52);
                    secs++;
                }
                // draw left sinde number background rectangle
                int polygonx[] = new int[4];
                int polygony[] = new int[4];
                polygonx[0] = 40;
                polygony[0] = ym-60;
                polygonx[1] = xm-60;
                polygony[1] = ym-60;
                polygonx[2] = 40+(int)(((double)xm-100.0)*(reftosonogram.selectedstartold+reftosonogram.selecedwidthold));
                polygony[2] = ym-50;
                polygonx[3] = 40+(int)(((double)xm-100.0)*reftosonogram.selectedstartold);
                polygony[3] = ym-50;
                if (reftosonogram.selectedstartold != 0.0 && reftosonogram.selecedwidthold!=1.0) {
                    g.setComposite(compositeGr);
                    g.setColor(coldv);
                    g.fillPolygon(polygonx,polygony,4);
                    g.setComposite(compositeNo);
                    g.drawLine(polygonx[0],polygony[0],polygonx[3],polygony[3]);
                    g.drawLine(polygonx[1],polygony[1],polygonx[2],polygony[2]);
                    g.setComposite(compositeNo);
                }
                // Painting Sonogram on the Image
                for (int x=0;x<reftosonogram.spektrum.size();x++) {                      // loop over FFT Vectors
                    if (x == reftosonogram.peakx)                                        // If peakpoint is reached
                        peaktimeisreached = true;
                    else
                        peaktimeisreached = false;
                    tempSpektrum = (float[])reftosonogram.spektrum.get(x);              // WFT Frequ. Vector
                    // loop over FFT Vector-Elements
                    for(int y=0;y<(reftosonogram.timewindowlength/2);y++) {
                        bwcolor = (int)tempSpektrum[(reftosonogram.timewindowlength/2-1) - y];
                        if (bwcolor < 0)
                            bwcolor   = 0;                 // Range-test
                        if (bwcolor > 255)
                            bwcolor = 255;                 // Range-test
                        selectColor(g,bwcolor);
                        xp = (int)((double)x * diffx) + 40;               // Startpoint in Pixels
                        yp = (int)((double)y * diffy);                    // Startpoint in Pixels
                        if (reftosonogram.gad.cslogfr.isSelected() == true) {           // Logarithm Frequency View
                            // Calculate position for logview
                            powfact      = Math.pow((double)y/(double)reftosonogram.timewindowlength*2.0,powerof);
                            yppow        = (int)((double)yp*powfact);
                            // Calculate diffy for logview by calculating next point
                            powfactnext  = Math.pow((double)(y+1)/(double)reftosonogram.timewindowlength*2.0,powerof);
                            ypnext  = (int)((double)(y+1) * diffy);                    // Startpoint in Pixels
                            yppownext    = (int)((double)ypnext + powfactnext);
                            diffylog = yppownext - yppow;
                            // Paint logview
                            g.fillRect(xp,yppow,rdiffx,diffylog);               // Draws Rect
                            if (reftosonogram.gad.cspitchonely.isSelected()==true && reftosonogram.gad.cspitch.isSelected()==true) {
                                g.setColor(collv);
                                g.setComposite(compositeNebular);
                                g.fillRect(xp,yppow,rdiffx,diffylog);               // Draws Rect
                                g.setComposite(compositeNo);
                            }
                            if (peaktimeisreached==true)   // Absolute Peak
                                if (y==(reftosonogram.timewindowlength/2-reftosonogram.peaky-1)) {
                                    xpointpeak = xp+rdiffx/2-10;
                                    ypointpeak = yppow+diffylog/2-10;
                                }
                        } else {
                            // Non Logarithm View
                            g.fillRect(xp,yp,rdiffx,rdiffy);
                            if (reftosonogram.gad.cspitchonely.isSelected()==true && reftosonogram.gad.cspitch.isSelected()==true) {
                                g.setColor(collv);
                                g.setComposite(compositeNebular);
                                g.fillRect(xp,yp,rdiffx,rdiffy);
                                g.setComposite(compositeNo);
                            }
                            if (peaktimeisreached==true)   // Absolute Peak
                                if (y==(reftosonogram.timewindowlength/2-reftosonogram.peaky-1)) {
                                    xpointpeak = xp+rdiffx/2-10;
                                    ypointpeak = yp+rdiffy/2-10;
                                }
                        }
                    }

                    // PITCH: Begin Pitch detection
                    if (reftosonogram.gad.cspitch.isSelected()==true) {                  // Pitch detection
                        pitchtmp  = 0.0f;
                        pitchamp  = 0.0f;
                        int begin = 0;
                        // PITCH:Frequencylimitation for Pitchdetection
                        if (reftosonogram.gad.cspitchlimitation.isSelected() == true) {
                            begin = (reftosonogram.timewindowlength/2)
                                    - (int)((double)(reftosonogram.timewindowlength/2)/reftosonogram.samplerate*2
                                            *reftosonogram.gad.sliderpitch.getValue());
                            if (begin >= (reftosonogram.timewindowlength/2-1))
                                begin = (reftosonogram.timewindowlength/2-2);
                        }
                        float smoothspek[] = new float[reftosonogram.timewindowlength/2];
                        // PITCH:Smooth out for Pitchdetection
                        if (reftosonogram.gad.cspitchsmooth.isSelected()==true) {
                            for (int y=3;y<(reftosonogram.timewindowlength/2-3);y++) {
                                smoothspek[y] = (  tempSpektrum[y-1] + tempSpektrum[y-2] + tempSpektrum[y-3]
                                                   + tempSpektrum[y]
                                                   + tempSpektrum[y+1] + tempSpektrum[y+2] + tempSpektrum[y+3])/7.0f;
                            }
                        }
                        // PITCH:If Smooth out for Pitch is not selected
                        else
                            for (int y=3;y<(reftosonogram.timewindowlength/2-3);y++) {
                                smoothspek[y] = tempSpektrum[y];
                            }
                        // PITCH: Main loop over frequency
                        // local absolute peak
                        pitchplace = begin;
                        for (int y=begin;y<(reftosonogram.timewindowlength/2);y++) {
                            pitchtmp = (int)smoothspek[(reftosonogram.timewindowlength/2-1) - y];
                            if (pitchtmp>pitchamp) {
                                pitchamp = pitchtmp;
                                pitchplace = y;
                            }
                        }

                        xp = (int)((double)x * diffx) + 40;               // Startpoint in Pixels
                        yp = (int)((double)pitchplace * diffy);           // Startpoint in Pixels
                        if (reftosonogram.gad.cspitchblack.isSelected()==false)
                            g.setColor(colred);
                        else
                            g.setColor(colwt);
                        if (reftosonogram.gad.cslogfr.isSelected() == true) {           // Logarithm Frequency View
                            // PITCH: Calculate position for logview
                            powfact      = Math.pow((double)pitchplace/(double)reftosonogram.timewindowlength*2.0,powerof);
                            yppow        = (int)((double)yp*powfact);
                            // PITCH:Calculate diffy for logview by calculating next point
                            ypnext       = (int)((double)(pitchplace+1) * diffy);                    // Startpoint in Pixels
                            yppownext    = (int)((double)ypnext + powfact/powkonst);
                            diffylog = yppownext - yppow;
                            // PITCH:Paint logview
                            g.fillRect(xp,yppow,rdiffx,rdiffy);               // Draws Rect
                        } else

                            g.fillRect(xp,yp,rdiffx,rdiffy);                  // Draws Rect
                    }
                }
                // END PITCH:

                if (reftosonogram.gad.clocalpeak.isSelected()==true) {
                    g.setColor(collv);
                    g.drawOval(xpointpeak+5,ypointpeak+5,10,10);
                    g.setComposite(compositeAl);
                    g.fillOval(xpointpeak,ypointpeak,20,20);
                    g.setComposite(compositeNo);
                }

                // Draws Grid if it is selected
                if (reftosonogram.gridItem.isSelected()) {
                    g.setComposite(compositeGr);
                    if (reftosonogram.fireItem.isSelected()==true)
                        g.setComposite(compositeAl);
                    // Colors for Grid
                    if (((reftosonogram.negItem.isSelected() == true ) && (reftosonogram.colItem.isSelected()  == true ))
                            ||((reftosonogram.negItem.isSelected() == true ) && (reftosonogram.fireItem.isSelected() == true ))
                            ||((reftosonogram.negItem.isSelected() == false) && (reftosonogram.bwItem.isSelected()   == true )))
                        g.setColor(colbk);
                    else
                        g.setColor(colwt);
                    // grid in Spectrum over time
                    for (int x=40+offset;x<xm-60;x+=((double)xm-100.0)/((double)reftosonogram.samplestotal/(double)reftosonogram.samplerate))
                        if (x>=40)
                            g.drawLine(x-1,0,x-1,ym-70);
                    // grid over Frequencies
                    for (double y=0;y<ym-69.0;y+=((double)ym-70.0)/10.0)
                        // GRID:If logarithm Frequency
                        if (reftosonogram.gad.cslogfr.isSelected()==true) {
                            powfact      = Math.pow((double)(y+1)/(double)(ym-69),powerof);
                            yppow        = (int)((double)y*powfact);
                            g.drawLine(40,(int)yppow,xm-60,(int)yppow);
                        } else
                            g.drawLine(40,(int)y,xm-60,(int)y);
                    g.setComposite(compositeNo);
                }
                // Drawing Timeline
                g.setColor(coldg);
                g.fillRect(40,ym-50,xm-100,50);             // Delete old one
                g.setColor(new Color(100,80,90));
                g.setComposite(compositeAl);
		// the color marker for the zoom area
		int xold = 0; // memorizer for the highest point
		int yold = 0; // memorizer for the highest point
		int ymax = 0; // memorizer for the highest point
                for (int xg=41;xg<xm-60;xg+=((double)xm-100.0)/((double)reftosonogram.samplesall/(double)reftosonogram.samplerate))  // Timegrid
                    g.drawLine(xg-1,ym-50,xg-1,ym);
                int amp,x,y;
		// UPPER DISPLAY WITH ENERGY INTENSITY
                if (reftosonogram.energyflag == true) {                   // if Energytimesignal is on
                    g.setColor(new Color(100,80,90));       // Grid...
                    g.drawLine(40,ym-10,xm-61,ym-10);
                    g.drawLine(40,ym-20,xm-61,ym-20);
                    g.drawLine(40,ym-28,xm-61,ym-28);
                    g.drawLine(40,ym-35,xm-61,ym-35);
                    g.drawLine(40,ym-40,xm-61,ym-40);
                    g.drawLine(40,ym-44,xm-61,ym-44);
                    g.drawLine(40,ym-48,xm-61,ym-48);
                    g.setColor(new Color(200,200,255));
                    g.setComposite(compositeNo);
                    g.drawString("Energy",42,ym-38);
                    g.setComposite(compositeAl);
                    g.setColor(new Color(200,255,0));
                    for (int t=0;t<2000;t++) {
                        y =  (int)((float)reftosonogram.timeline[t] / 2.5);
                        x =  (int)((double)t*timetoscreenfakt)+40;
                        if ((double)t/2000.0> reftosonogram.selectedstart && (double)t/2000.0<( reftosonogram.selectedstart+ reftosonogram.selecedwidth))
                            if ( reftosonogram.selecedwidth == 1.0)
				// selectColor(g,reftosonogram.timeline[t]*2);
				g.setColor(collg);
                            else {
                                g.setComposite(compositeFo);
                                g.setColor(colred);
                                g.drawLine(x,ym-50,x,ym);
                                g.setComposite(compositeAl);
                            }
                        else
			    // selectColor(g,reftosonogram.timeline[t]*2);
			    g.setColor(collg);
                        //g.drawLine(x,ym-y,x,ym);
			if (x != xold) {   // if the x jumps forward
			    g.setComposite(compositeAl1);
			    g.drawLine(xold,ym+ymax,xold,ym-yold);
			    g.setComposite(compositeNo);
			    g.drawLine(xold+1,ym-ymax,xold,ym-yold);
			    g.drawLine(xold+1,ym+ymax,xold,ym+yold);
			    xold = x;			    
			    yold = ymax;
			    ymax = 0;
			    ymax = Math.abs(y);
			}
			else {             // else look if there is a peak
			    if (ymax<Math.abs(y)) ymax=Math.abs(y);
			}
                    }
                }
		// UPPER DISPLAY WITH AMPLITUDE
                if (reftosonogram.energyflag == false) {                  // if Energytimesignal is off = Timesiganal
                    g.setColor(new Color(100,80,90));       
		    
                    g.drawLine(40,ym-5,xm-61,ym-5);  // Grid...
                    g.drawLine(40,ym-10,xm-61,ym-10);
                    g.drawLine(40,ym-15,xm-61,ym-15);
                    g.drawLine(40,ym-20,xm-61,ym-20);
                    g.drawLine(40,ym-30,xm-61,ym-30);
                    g.drawLine(40,ym-35,xm-61,ym-35);
                    g.drawLine(40,ym-40,xm-61,ym-40);
                    g.drawLine(40,ym-45,xm-61,ym-45);
		    
                    g.setColor(new Color(200,200,255));
                    g.setComposite(compositeNo);
                    g.drawString("Amplitude",42,ym-38);
                    g.setComposite(compositeAl);
                    for (int t=0;t<2000;t++) {
                        y = reftosonogram.timeline[t] / 5;
                        x = (int)((double)t*timetoscreenfakt)+40;
                        if ((double)t/2000.0>reftosonogram.selectedstart && (double)t/2000.0<(reftosonogram.selectedstart+reftosonogram.selecedwidth))
                            if (reftosonogram.selecedwidth == 1.0)
                                g.setColor(collg);
                            else {
                                g.setComposite(compositeFo);
                                g.setColor(colred);
                                g.drawLine(x,ym-50,x,ym);
                                g.setColor(colred);
                            }
                        else
                            g.setColor(colg);
                        //g.drawLine(x,ym-25+y,x,ym-25-y);
			// the bright borders of the amplitude signal
			if (x != xold) {   // if the x jumps forward
			    g.setComposite(compositeAl1);
			    g.drawLine(xold,ym-25+ymax,xold,ym-25-yold);
			    g.setComposite(compositeNo);
			    g.drawLine(xold+1,ym-25-ymax,xold,ym-25-yold);
			    g.drawLine(xold+1,ym-25+ymax,xold,ym-25+yold);
			    xold = x;			    
			    yold = ymax;
			    ymax = 0;
			    ymax = Math.abs(y);
			}
			else {             // else look if there is a peak
			    if (ymax<Math.abs(y)) ymax=Math.abs(y);
			}
			
                    }
		    g.setComposite(compositeAl);
		    g.setColor(collg);
                    g.drawLine(40,ym-25,xm-60,ym-25);            // Middleline
                }
                g.setColor(new Color(100,0,0));
                g.drawLine(polygonx[3],ym-50,polygonx[3],ym);
                g.drawLine(polygonx[2],ym-50,polygonx[2],ym);
                // Text on left Side
                g.setColor(new Color(50,50,200));                // draw Spektrumdimensions
                g.setFont(new Font("Courier",0,9));
                String tr;
                if (reftosonogram.gad.rfft.isSelected()==true)
                    tr = "FFT";
                else
                    tr = "LPC";
                String wn  = "wn:" + String.valueOf(reftosonogram.spektrum.size());
                String wl  = "wl:" + String.valueOf(reftosonogram.timewindowlength);
                g.drawString(tr,3,ym-21);
                g.drawString(wn,3,ym-12);
                g.drawString(wl,3,ym-3);
                // Grids and fill in Single View
                g.setColor(coldg);
                g.fillRect(xm-55,2,53,ym-71);                    // Rect for Single Fr. view
                g.setColor(coldv);
                for (double yg=0;yg<ym-69.0;yg+=((double)ym-70.0)/10.0)
                    // If logarithm Frequency
                    if (reftosonogram.gad.cslogfr.isSelected()==true) {
                        powfact      = Math.pow((double)(yg+1)/(double)(ym-69),powerof);
                        yppow        = (int)((double)yg*powfact);
                        if (yppow>2)
                            g.drawLine(xm-60,(int)yppow,xm-3,(int)yppow);
                    }
                // Non Logarithm
                    else
                        if (yg>2)
                            g.drawLine(xm-60,(int)yg,xm-3,(int)yg);
                // Rainbow on Edge
                for (x=0;x<255;x++) {                            // Rainbow on Edge
                    xp = (int)((double)x / 255.0 * 31.0);
                    selectColor(g,x);
                    g.drawLine(5+xp,ym-50,5+xp,ym-35);
                }
                g.setColor(new Color(0,0,255));
                g.drawRect(4,ym-50,32,15);
                System.out.println("--> Updating complete");
                wnp           = 0;                               // Window Number Position for paintOneSpejtrum in units
                wsp           = 0;                               // Window Size position for paintOneSpejtrum in units
                wnf           = 0.0;                             // self as wnp from 0..1
                wsf           = 0.0;                             // self as wsp from 0..1
            }
        }
        gr.drawImage(doublebufferimage,0,0,this);
        if (reftosonogram.infovisible   == true)
            reftosonogram.infod.update();
        reftosonogram.cv.update();
        reftosonogram.av.update();
        reftosonogram.fv.update();
        reftosonogram.lv.update();
        reftosonogram.wv.update();
        reftosonogram.kv.update();
        reftosonogram.pv.update();
        // And the single spectrum too
        if (reftosonogram.spektrumExist==true && reftosonogram.openingflag == false) {
            paintOneSpektrum(false);
            paintTimeSlider(gr);
        }
        reftosonogram.setCursor(Cursor.DEFAULT_CURSOR);
    }
    //-------------------------------------------------------------------------------------------------------------------------
    /**
     * Select Paintcolor from Graphics to selected Solorarray in GeneralAdjustmentDialog.
     * @param g Graphics-object with to select Color.
     * @param bwcolor arraypoint of Color to select.
     */
    public void selectColor(Graphics g,int bwcolor) {
        if (reftosonogram.colItem.isSelected() == true && reftosonogram.negItem.isSelected() == false)
            g.setColor(coCO[bwcolor]);
        if (reftosonogram.colItem.isSelected() == true && reftosonogram.negItem.isSelected() == true)
            g.setColor(coCOi[bwcolor]);
        if (reftosonogram.bwItem.isSelected() == true && reftosonogram.negItem.isSelected()  == false)
            g.setColor(coSW[bwcolor]);
        if (reftosonogram.bwItem.isSelected() == true && reftosonogram.negItem.isSelected()  == true)
            g.setColor(coSWi[bwcolor]);
        if (reftosonogram.fireItem.isSelected() == true && reftosonogram.negItem.isSelected()== false)
            g.setColor(coFI[bwcolor]);
        if (reftosonogram.fireItem.isSelected() == true && reftosonogram.negItem.isSelected()== true)
            g.setColor(coFIi[bwcolor]);
        if (reftosonogram.rainItem.isSelected() == true && reftosonogram.negItem.isSelected()== false)
            g.setColor(coRA[bwcolor]);
        if (reftosonogram.rainItem.isSelected() == true && reftosonogram.negItem.isSelected()== true)
            g.setColor(coRAi[bwcolor]);
        if (reftosonogram.greenItem.isSelected() == true && reftosonogram.negItem.isSelected()== false)
            g.setColor(coCG[bwcolor]);
        if (reftosonogram.greenItem.isSelected() == true && reftosonogram.negItem.isSelected()== true)
            g.setColor(coCGi[bwcolor]);
    }
    //-------------------------------------------------------------------------------------------------------------------------
}
