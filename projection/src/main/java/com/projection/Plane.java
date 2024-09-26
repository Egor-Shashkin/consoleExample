/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.projection;

/**
 *
 * @author Andrei
 */
public class Plane {
  private double A;
  private double B;
  private double C;
  private double D;
  private Point3D point;
//  private Point3D point;
  public Plane(double a, double b, double c, Point3D point) {
    A = a;
    B = b;
    C = c;
    this.point = point;
    D = -A*point.getX() - B*point.getY() - C*point.getZ();
  }

  public Plane(double a, double b, double c, double d) {
    A = a;
    B = b;
    C = c;
    D = d;
    //finding some point following plane equation
    try{
      point = new Point3D(0,0,-D/C);
    } catch(ArithmeticException e){
      point = new Point3D(0,-D/B,0);
    }
  }

  public Plane(double[] point1, Point3D point2){
    A = point1[1] * point2.getZ() - point1[2] * point2.getY();
    B = point1[2] * point2.getX() - point2.getX() * point2.getZ();
    C = point2.getX() * point2.getY() - point1[1] * point2.getX();
    D = 0;
  }

  public Plane(Point3D point0, Point3D point1, Point3D point2){
    double x0 = point0.getX();
    double y0 = point0.getY();
    double z0 = point0.getZ();
    double x1 = point1.getX();
    double y1 = point1.getY();
    double z1 = point1.getZ();
    double x2 = point2.getX();
    double y2 = point2.getY();
    double z2 = point2.getZ();


    A = (y1-y0)*(z2-z0) - (z1-z0)*(y2-y0);
    B = (z1-z0)*(x2-x0) - (x1-x0)*(z2-z0);
    C = (x1-x0)*(y2-y0) - (y1-y0)*(x2-x0);
    D = -A*x0 - B*y0 - C*z0;
  }

  public Point3D getNormal(){
    return new Point3D(A,B,C);
  }
  

  public double getD(){
    return D;
  }

  public String getAsEquation(){
    return String.format("%s*(x-(%s)) + %s*(y-(%s)) + %s*(z-(%s)) = 0", A, point.getX(), B, point.getY(), C, point.getZ());
  }
}

