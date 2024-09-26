package com.myUtility;

public class Projection {
  //TODO change double[] points to Point3D points
  class Plane{
    //plane equation ABC
    private double A;
    private double B;
    private double C;
    private double D;
    private double[] point;

    public Plane(double a, double b, double c, double[] point) {
      A = a;
      B = b;
      C = c;
      this.point = point;
      D = -A*point[0] - B*point[1] - C*point[2];
    }

    public Plane(double a, double b, double c, double d) {
      A = a;
      B = b;
      C = c;
      D = d;
      //finding random point following plane equation
      point = new double[]{0,0,-D/C};
    }

    public Plane(double[] point1, double[] point2){
      A = point1[1]*point2[2] - point1[2]*point2[1];
      B = point1[2] * point2[0] - point1[0]*point2[2];
      C = point1[0] * point2[1] - point1[1] * point2[0];
      D = 0;
    }

    public Plane(double[] point0, double[] point1, double[] point2){
      double x0 = point0[0];
      double y0 = point0[1];
      double z0 = point0[2];
      double x1 = point1[0];
      double y1 = point1[1];
      double z1 = point1[2];
      double x2 = point2[0];
      double y2 = point1[1];
      double z2 = point1[2];


      A = (y1-y0)*(z2-z0) - (z1-z0)*(y2-y0);
      B = (z1-z0)*(x2-x0) - (x1-x0)*(z2-z0);
      C = (x1-x0)*(y2-y0) - (y1-y0)*(x2-x0);
      D = -A*x0 - B*y0 - C*z0;
    }

    public double[] getNormal(){
      return new double[]{A,B,C};
    }

    public double getD(){
      return D;
    }

    public String getAsEquation(){
      return String.format("%s*(x-%s) + %s*(x-%s) + %s*(x-%s) = 0", A, point[0], B, point[1], C, point[3]);
    }
  }

  public double[] projectToPlane(Plane plane, double[] point){
    double distance;
    double[] norm = plane.getNormal();
    double normModule = Math.sqrt(Math.pow(norm[0], 2) + Math.pow(norm[1], 2) + Math.pow(norm[2], 2));
    distance  = (norm[0] * point[0] + norm[1]*point[1] + norm[2]*point[2] + plane.getD())/normModule;
    double[] projectedPoint = new double[3];
    for (int i = 0; i < projectedPoint.length; i++){
      projectedPoint[i] = point[i] - distance * norm[i]/normModule;
    }
    return projectedPoint;
  }
  
  public double[] cartesianToSpheric(double[] point) {
    double x = point[0];
    double y = point[1];
    double z = point[2];
    
    double r = Math.sqrt(x * x + y * y + z * z);
    double theta = Math.atan2(y, x);
    double phi = Math.acos(z / r);
    
    return new double[]{r, theta, phi};
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
}
