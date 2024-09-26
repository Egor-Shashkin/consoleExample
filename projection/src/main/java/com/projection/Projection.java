/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.projection;

import java.awt.geom.Point2D;

/**
 *
 * @author Andrei
 */
public class Projection {
  public Point3D projectToPlane(Plane plane, Point3D point){
    double distance;
    Point3D norm = plane.getNormal().norm();
    distance  = (norm.getX()*point.getX() + norm.getY()*point.getY() + norm.getZ()*point.getZ() + plane.getD());
    Point3D projectedPoint = new Point3D(point.getX() - distance * norm.getX(), point.getY() - distance * norm.getY(), point.getZ() - distance * norm.getZ());
    return projectedPoint;
  }

  //questionable math
  //aligning plane normal with x axis
  //point is in cartesian coordinates
  public Point2D rotateToPlane(Plane plane, Point3D point){
    Point3D sphericNorm = cartesianToSpheric(plane.getNormal().norm());
    double phi = -sphericNorm.getZ();
    double theta = Math.PI/2 - sphericNorm.getY();

    // newPoint[0] =  Math.cos(phi)*Math.cos(theta)*x - Math.cos(theta)*Math.sin(phi)*y + Math.sin(theta)*z; -- x, throwing away because of projection
    double newY =                  Math.sin(phi)*point.getX() +                 Math.cos(phi)*point.getY();
    double newZ = -Math.sin(theta)*Math.cos(phi)*point.getX() + Math.sin(theta)*Math.sin(phi)*point.getY() + Math.cos(theta)*point.getZ();

    return new Point2D.Double(newY, newZ);
  }

  public Point2D rotateToPlaneSpheric(Plane plane, Point3D point){
    Point3D sphericNorm = cartesianToSpheric(plane.getNormal().norm());
    double phi = sphericNorm.getZ();
    point.setY(point.getY() - sphericNorm.getY());
    point = sphericToCartesian(point);
    // point[0] = Math.cos(phi)*point[0] + Math.sin(phi)*point[2]; -- x, throwing away because of projection
    point.setZ(-Math.sin(phi)*point.getX() + Math.cos(phi)*point.getY());
    return new Point2D.Double(point.getY(), point.getZ());
  }

  public double[] cartesianToSpheric(double[] point) {
    double x = point[0];
    double y = point[1];
    double z = point[2];
    
    double r = Math.sqrt(x * x + y * y + z * z);
    double phi = Math.atan2(y, x);
    double theta = Math.acos(z / r);
    
    return new double[]{r, theta, phi};
  }

  public Point3D cartesianToSpheric(Point3D point){
    double x = point.getX();
    double y = point.getY();
    double z = point.getZ();
    
    double r = Math.sqrt(x * x + y * y + z * z);
    double phi = Math.atan2(y, x);
    double theta = Math.acos(z / r);
    
    return new Point3D(r, theta, phi);
  }

  public double[] sphericToCartesian(double[] sphericalPoint) {
    double r = sphericalPoint[0];
    double theta = sphericalPoint[1];
    double phi = sphericalPoint[2];
    
    double x = r * Math.sin(phi) * Math.cos(theta);
    double y = r * Math.sin(phi) * Math.sin(theta);
    double z = r * Math.cos(phi);
    
    return new double[]{x, y, z};
  }

  public Point3D sphericToCartesian(Point3D sphericalPoint){
    double r = sphericalPoint.getX();
    double theta = sphericalPoint.getY();
    double phi = sphericalPoint.getZ();
    
    double x = r * Math.sin(phi) * Math.cos(theta);
    double y = r * Math.sin(phi) * Math.sin(theta);
    double z = r * Math.cos(phi);
    
    return new Point3D(x, y, z);
  }
}
