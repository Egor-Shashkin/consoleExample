/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.clients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import com.myUtility.*;
import java.awt.geom.Point2D;

/**
 *
 * @author Andrei
 */

//deprecated, use Client instead
public class GettingClient {
  public static void main(String[] args){
    ArrayList<Point2D> fTimeRe;
      int range = 20;
//      double period = Math.PI * 2;
//      range = (int)Math.round(period);
      fTimeRe = (ArrayList<Point2D>) FourierTransformer.getFuncPoints(FourierTransformer.ifft(
              (FourierTransformer.fft(FourierTransformer.getFuncValues("exp(x)", range)))), range);
//      fTimeRe = (ArrayList<Point2D>) transform.inverseFourierSeries(startingPoint, range, fOmega, period);
      Plotter plot = new Plotter("Title", "Title", fTimeRe);
      plot.pack();
      plot.setVisible(true);
  }
}

