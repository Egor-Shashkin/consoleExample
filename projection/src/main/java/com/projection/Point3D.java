package com.projection;

import java.util.ArrayList;
import java.util.List;

public class Point3D {
    private double x;
    private double y;
    private double z;

    private double rho;
    private double phi;
    private double theta;
    private boolean sphericNeedInit;
    //TODO thread-safety
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

        rho = Double.MIN_VALUE;
        phi = Double.MIN_VALUE;
        theta = Double.MIN_VALUE;
        sphericNeedInit = true;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public List<Double> asList(){
        List<Double> list = new ArrayList<>();
        list.add(x);
        list.add(y);
        list.add(z);
        return list;
    }

    public double getRho(){
        if (sphericNeedInit) return rho = Math.sqrt(x * x + y * y + z * z);
        return rho;
    }

    public double getPhi(){
        if (sphericNeedInit) return phi = Math.atan2(y, x);
        return phi;
    }

    public double getTheta(){
        if (sphericNeedInit) return theta = Math.acos(z / getRho());
        return theta;
    }

    public double distance(double x1, double y1, double z1){
        return Math.sqrt(Math.pow(x-x1, 2) + Math.pow(y-y1, 2) + Math.pow(z-z1, 2));
    }

    public double length(){
        return distance(0, 0, 0);
    }
    
    public Point3D norm(){
        double normModule = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        return new Point3D(x/normModule, y/normModule, z/normModule);
    }

    public void setX(double x) {
        this.x = x;
        sphericNeedInit = true;
    }

    public void setY(double y) {
        this.y = y;
        sphericNeedInit = true;
    }

    public void setZ(double z) {
        this.z = z;
        sphericNeedInit = true;
    }

    public void setPoint(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
}
