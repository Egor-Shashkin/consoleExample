/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.myUtility;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;
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
public class Plotter extends ApplicationFrame implements ActionListener{
  private final int width;
  private final int height;
  private int counter;
  private static XYLineAndShapeRenderer renderer;
  private XYSeriesCollection dataset;
  ArrayList<Point2D> cord;
  



  public Plotter(String applicationTitle, String chartTitle, ArrayList<Point2D> cord) {
      super(applicationTitle);
      this.cord = cord;
      counter = 0;
      width = 900;
      height = 600;
      dataset = createDataset(cord);
      JFreeChart xylineChart = ChartFactory.createXYLineChart(
         chartTitle,
         "x",
         "f(x)",
         dataset);
      JButton button = new JButton("increase amp");
      button.setActionCommand("CHANGE_AMP");
      button.addActionListener(this);
         
      ChartPanel chartPanel = createPanel(xylineChart);
      final XYPlot plot = xylineChart.getXYPlot();
      
      JPanel panel = new JPanel();
      chartPanel.add(button, BorderLayout.SOUTH);
      panel.add(chartPanel);
      //panel.add(button, BorderLayout.SOUTH);
      
      renderer = defaultRenderer();
      plot.setRenderer(renderer); 
      setContentPane(panel); 
   }
  
  private ChartPanel createPanel(JFreeChart xylineChart){
    ChartPanel chartPanel = new ChartPanel(xylineChart);
      chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
      chartPanel.setDomainZoomable(true);
      chartPanel.setMouseWheelEnabled(true);
      chartPanel.setMouseZoomable(true);
    return chartPanel;
  }
  
  private XYLineAndShapeRenderer defaultRenderer(){
      XYLineAndShapeRenderer renderer = new XYSplineRenderer();
      renderer.setSeriesPaint(0, Color.RED);
      renderer.setSeriesPaint(1, Color.BLACK);
      renderer.setSeriesStroke(0, new BasicStroke());
      renderer.setDefaultShapesVisible(false);
      return renderer;
  }
  
  private XYSeriesCollection createDataset(ArrayList<Point2D> list){
    XYSeries dataset = new XYSeries("plot");
    for (var item : list) {
      dataset.add(item.getX(), item.getY());
    }
    
    return new XYSeriesCollection(dataset);
  }
  
  private XYSeries createSeries(ArrayList<Point2D> list){
    XYSeries dataset = new XYSeries(counter++);
    for (var item : list) {
      dataset.add(item.getX(), item.getY());
    }
    
    return dataset;
  }
  
  public void actionPerformed(ActionEvent e){
    if (e.getActionCommand().equals("CHANGE_AMP")){
      cord.stream().forEach(n -> n.setLocation(n.getX(), 2 * n.getY()));
      if (dataset.getSeriesCount() >= 2) 
        dataset.removeSeries(0);
      this.dataset.addSeries(createSeries(cord));
    }
  }
}
