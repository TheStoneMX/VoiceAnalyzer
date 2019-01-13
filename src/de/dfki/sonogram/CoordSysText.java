package de.dfki.sonogram;

import javax.media.j3d.*;
import javax.vecmath.*;
import java.awt.*;

/**
 * Copyright (c) 2001 Christoph Lauer @ DFKI, All Rights Reserved.
 * clauer@dfki.de - www.dfki.de
 * <p>
 * Shape3D object for the Surface class.
 * text for the coordsystem.
 * @author Christoph Lauer
 * @version 1.0,  Current 30/09/2006
 */
public class CoordSysText extends TransformGroup {

    private Sonogram reftomain;
    public CoordSysText(Sonogram ref) {
	reftomain = ref;
	addGeometry();
    } 

    private void addGeometry() {

	// the marking text for the axis...
	Font3D font3d = new Font3D(new Font("Dialog",Font.BOLD,5),new FontExtrusion());
	Text3D textX = new Text3D(font3d,new String("Time")     ,new Point3f(0.0f,0.0f,0.0f));
	Text3D textY = new Text3D(font3d,new String("Frequency"),new Point3f(0.0f,0.0f,0.0f));
	Text3D textZ = new Text3D(font3d,new String("Amplitude"),new Point3f(0.0f,0.0f,0.0f));
	Shape3D x0 = new Shape3D(textX);

	Shape3D y0 = new Shape3D(textY);
	Shape3D z0 = new Shape3D(textZ);

	// scale the text
	Transform3D scale = new Transform3D();
	scale.setScale(0.016f);
	Transform3D rotX = new Transform3D();
	rotX.rotX(Math.PI/2);
	Transform3D rotY = new Transform3D();
	rotY.rotY(Math.PI/2);
	Transform3D rotYm = new Transform3D();
	rotYm.rotY(-Math.PI/2);
	Transform3D rotZ = new Transform3D();
	rotZ.rotZ(Math.PI/2);
	Transform3D transX = new Transform3D();
	transX.setTranslation(new Vector3f(0.61f,-0.6f,-0.4f));
	Transform3D transY = new Transform3D();
	transY.setTranslation(new Vector3f(-0.6f,0.61f,-0.4f));
	Transform3D transZ = new Transform3D();
	transZ.setTranslation(new Vector3f(-0.6f,-0.6f,0.61f));
	Transform3D transC = new Transform3D();
	transC.setTranslation(new Vector3f(0.0f,-0.02f,0.0f));



      	TransformGroup x1 = new TransformGroup(scale);
	x1.addChild(x0);
      	TransformGroup x2 = new TransformGroup(transC);
	x2.addChild(x1);
      	TransformGroup x3 = new TransformGroup(rotX);
	x3.addChild(x2);
      	TransformGroup x4 = new TransformGroup(transX);
	x4.addChild(x3);
        addChild(x4);

      	TransformGroup y1 = new TransformGroup(scale);
	y1.addChild(y0);
      	TransformGroup y2 = new TransformGroup(transC);
	y2.addChild(y1);
      	TransformGroup y3 = new TransformGroup(rotX);
	y3.addChild(y2);
      	TransformGroup y4 = new TransformGroup(rotZ);
	y4.addChild(y3);
      	TransformGroup y5 = new TransformGroup(transY);
	y5.addChild(y4);
        addChild(y5);

      	TransformGroup z1 = new TransformGroup(scale);
	z1.addChild(z0);
      	TransformGroup z2 = new TransformGroup(transC);
	z2.addChild(z1);
      	TransformGroup z3 = new TransformGroup(rotYm);
	z3.addChild(z2);
      	TransformGroup z4 = new TransformGroup(rotZ);
	z4.addChild(z3);
      	TransformGroup z5 = new TransformGroup(transZ);
	z5.addChild(z4);
        addChild(z5);
    }
}