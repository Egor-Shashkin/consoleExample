/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.myUtility.*;
import com.projection.Plane;
import com.projection.Point3D;
import com.projection.Projection;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Andrei
 */

//deprecated, use Client instead
public class GettingClient {
  public static void main(String[] args){
    Plane plane = new Plane(1, 1, 1, 0);
    Projection projection = new Projection();
    Point3D point = new Point3D(2,1,5);
    Point2D rotatedPoint = projection.rotateToPlane(plane, point);
    System.out.println("plane equation: " + plane.getAsEquation());
    System.out.printf("normal: %s, %s%n", plane.getNormal().asList(), plane.getD());
    System.out.printf("ort noraml: %s, %s%n", plane.getNormal().norm().asList(), plane.getD());
    System.out.printf("projection to plane: %s%n", projection.projectToPlane(plane, point).asList());
    System.out.printf("point in cartesian: %s%n", point.asList());
    System.out.printf("point in spheric: %s%n", projection.cartesianToSpheric(point).asList());
    System.out.printf("rotated point: %s, %s%n", rotatedPoint.getX(), rotatedPoint.getY());
    List<Point2D> fTimeRe = new ArrayList<>();
    fTimeRe.add(new Point2D.Double());
    fTimeRe.add(rotatedPoint);


    Plotter plot = new Plotter("Title", "Title", fTimeRe);
    plot.pack();
    plot.setVisible(true);


    /*
    List<Point2D> fTimeRe;
    List<Double[]> fOmega;
      int range = 20;
//      double period = Math.PI * 2;
//      range = (int)Math.round(period);
      fTimeRe = FourierTransformer.getFuncPoints(FourierTransformer.ifft(
              (FourierTransformer.fft(FourierTransformer.getFuncValues("exp(x)", range)))), range);
      fOmega = FourierTransformer.FourierSeries("x", range);
      fTimeRe = FourierTransformer.inverseFourierSeries(range, fOmega, 2 * range);
      Plotter plot = new Plotter("Title", "Title", fTimeRe);
      plot.pack();
      plot.setVisible(true);
*/
  }
}

