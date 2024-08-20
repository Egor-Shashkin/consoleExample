/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Andrei
 */
public class Plotter extends JPanel{
  private ArrayList<Double> cord;
  private int marg;
  private int width;
  private int height;
  private double max;
  private double x1;
  private double y1;

  public Plotter() {
    cord = new ArrayList<>();
    cord.add(65.0);
    cord.add(20.0);
    cord.add(60.0);
    cord.add(80.0);
    marg = 60;
    width = 300;
    height = 100;
    max = 80;
  }

  public Plotter(ArrayList<Double> cord) {
    this.cord = cord;
    marg = 60;
    width = 300;
    height = 300;
    max = Collections.max(cord);
  }
  
  
  
  protected void paintComponent(Graphics grf){
    double x0;
    double y0;
    super.paintComponent(grf);
    Graphics2D graph = (Graphics2D) grf;
    
    graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    graph.draw(new Line2D.Double(marg, marg, marg, height-marg));
    graph.draw(new Line2D.Double(marg, height-marg, width-marg, height-marg));
    
    double x = (double)(width-2*marg)/(cord.size()-1);
    double scale = (double)(height-2*marg)/max;
    
    graph.setPaint(Color.RED);
    x0 = marg;
    y0 = height-marg-scale*cord.get(0);
    for (int i = 0; i < cord.size(); i++){
      x1 = marg + i*x;
      y1 = height-marg-scale*cord.get(i);
      
      graph.fill(new Ellipse2D.Double(x1-2, y1-2, 4, 4));
      graph.draw(new Line2D.Double(x0, y0, x1, y1));
      y0 = y1;
      x0 = x1;
    }
  }
  
}
