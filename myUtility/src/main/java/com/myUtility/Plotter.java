/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.awt.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Andrei
 */
public class Plotter extends ApplicationFrame{
  private final int width;
  private final int height;



  public Plotter(String applicationTitle, String chartTitle, HashMap<Double, Double> cord) {
      super(applicationTitle);
      width = 300;
      height = 300;
      Collections.max(cord.entrySet(), Map.Entry.comparingByValue()).getValue();
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
}
