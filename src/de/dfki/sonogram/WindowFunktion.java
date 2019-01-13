package de.dfki.sonogram;


/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * Implements Static Methods for Window Funktions for
 * Shorttime Fourier Transformation (SFT)
 * @author  Christoph lauer
 * @version 1.2, Begin 15/02/2002, Current 26/09/2002
 */
public class WindowFunktion  {

    //------------------------------------------------------------------------------------------
    public static float[] hammingWindow(int len) {
        float[] hamming = new float[len];
        for (int n=0;n<len;n++) {
            hamming[n] = 0.54f-(float)(0.46*Math.cos(2.0*Math.PI*n/len));
        }
        return hamming;
    }
    //------------------------------------------------------------------------------------------
    public static float[] rectangleWindow(int len) {
        float[] rectangle = new float[len];
        for (int n=0;n<len;n++) {
            rectangle[n] = 1.0f;
        }
        return rectangle;
    }
    //------------------------------------------------------------------------------------------
    public static float[] blackmanWindow(int len) {
        float[] blackman = new float[len];
        for (int n=0;n<len;n++) {
            blackman[n] = 0.42f-(float)(0.5*Math.cos(2.0*Math.PI*n/len)+0.08*Math.cos(2.0*Math.PI*n/len));
        }
        return blackman;
    }
    //------------------------------------------------------------------------------------------
    public static float[] hanningWindow(int len) {
        float[] hanning = new float[len];
        for (int n=0;n<len;n++) {
            hanning[n]=0.5f-0.5f*(float)Math.cos(2.0*Math.PI*n/len);
        }
        return hanning;
    }
    //------------------------------------------------------------------------------------------
    public static float[] triangleWindow(int len) {
        float[] triangle = new float[len];
        for (int n=0;n<len;n++) {
            if (n<len/2) {
                triangle[n] = (float)n/(float)len * 2.0f;
            } else {
                triangle[n] = 2.0f - (float)(n) / (float)len * 2.0f;
            }
        }
        return triangle;
    }
    //------------------------------------------------------------------------------------------
    public static float[] welchWindow(int len) {
        float[] welch = new float[len];
        for (int n=0;n<len;n++) {
            welch[n] = 1.0f - (float)Math.pow(((double)n - (double)len / 2.0) / ((double)len / 2.0),2.0);
        }
        return welch;
    }
    //------------------------------------------------------------------------------------------
    public static float[] gaussWindow(int len) {
        float[] gauss = new float[len];
        double rho = 0.3333;                                     // Parameter der Gausskurve
        for (int n=0;n<len;n++) {
            gauss[n] = (float)Math.exp(-0.5*Math.pow((n-len/2.0)/(rho*len/2.0),2));
        }
        return gauss;
    }
    //------------------------------------------------------------------------------------------
} // WindowFunktion
