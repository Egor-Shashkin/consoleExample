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
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Andrei
 */
public class Plotter extends ApplicationFrame{
  private HashMap<Double, Double> cord;
  private int marg;
  private int width;
  private int height;
  private double max;
  private double x1;
  private double y1;



  public Plotter(String applicationTitle, String chartTitle, HashMap<Double, Double> cord) {
      super(applicationTitle);
      marg = 60;
      width = 300;
      height = 300;
      max = Collections.max(cord.entrySet(), Map.Entry.comparingByValue()).getValue();
      JFreeChart xylineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "x",
         "f(x)",
         createDataset(cord));
         
      ChartPanel chartPanel = new ChartPanel(xylineChart);
      chartPanel.setPreferredSize( new java.awt.Dimension(width, height));
      final XYPlot plot = xylineChart.getXYPlot();
      
      XYLineAndShapeRenderer renderer = new XYSplineRenderer();
      renderer.setSeriesPaint(0, Color.RED);
      renderer.setSeriesStroke(0, new BasicStroke());
      renderer.setDefaultShapesVisible(false);
      plot.setRenderer(renderer); 
      setContentPane(chartPanel); 
   }
  
  private XYDataset createDataset(HashMap<Double, Double> list){
    XYSeries dataset = new XYSeries("plot");
    for (var item : list.entrySet()) {
      dataset.add(item.getKey(), item.getValue());
    }
    
    return new XYSeriesCollection(dataset);
  }
  
  
  
  /*protected void paintComponent(Graphics grf){
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
  }*/
  
}
